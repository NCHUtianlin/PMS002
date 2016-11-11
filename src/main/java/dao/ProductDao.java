package dao;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import dao.basic.BasicImplentsDao;
import domain.Product;
import domain.ProductType;


public class ProductDao extends BasicImplentsDao {

	private static Log log = LogFactory.getLog(ProductDao.class.getName());
	
	@Autowired
	private static MongoTemplate mongoTemplate;
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		//System.out.println("productDao开始注入。。。。。");
		this.mongoTemplate = mongoTemplate;
	}

	
	//插入信息，批量插入信息
	public int insert(List<DBObject> list)
	{
		//System.out.println("list数据"+list);
		//System.out.println(list.size());
		
		try {
			
			mongoTemplate.getCollection("productTable").insert(list);
			
		} catch (Exception e) {
			log.error("发生异常，批量添加失败："+e.getMessage());
			return -1;
		}
		return 1;
	}		
	
	
	//将获得的产品基本信息进行联表整合
	public List UnionInfo( List<Map<String , Object>>  list )
	{
		if( list == null || list.size() == 0 )
		{
			return null;
		}
		
		String producer = "" ;//该批次的生产商
		String productEndDate = "";//该批次生产完成时间
		String productTypeName = "";//该批次的产品类型名称
		String productBatch = list.get(0).get("productBatch").toString();
		//通过产品批次批次查询出该批次的生产商和该批次的完成时间
		Map<String , Object> rMap = new HashMap<String, Object>();
		rMap.put("productBatch", productBatch);
		List<Map<String , Object>> reportList = getInfo("reportTable", rMap);
		//System.out.println("report : "+reportList.size());
		
		
		List<Map<String , Object>> noteList = getInfo("productNoteTable", rMap);
		
		
		if( reportList == null || reportList.size() == 0 )
		{
			return null;
		}
		if( noteList != null && noteList.size() > 0 )
		{
			productEndDate = noteList.get(0).get("productEndDate").toString();
		}
		producer = reportList.get(0).get("producer").toString();//获得该批次的生产商
		
		//通过产品类型ID查询产品类型名称
		String productTypeID = list.get(0).get("productTypeID").toString();
		Map<String ,Object> pMap = new HashMap<String, Object>();
		pMap.put("_id", productTypeID);
		//System.out.println("产品类型ID : "+productTypeID+" 生产商 ： "+ producer);
		ProductType typeList = (ProductType) getById(ProductType.class , productTypeID );
		if( typeList == null  )
		{
			return null;
		}
		productTypeName = typeList.getProductTypeName();
		
		for( int i=0 ; i < list.size() ; i++ )
		{
			list.get(i).put("productTypeName", productTypeName);
			list.get(i).put("producer", producer);
			list.get(i).put("productEndDate", productEndDate);
		}
		
		return list;
	}

	
	//按时间段查询
	public List getInfoByTimes( Map<String, Object> map,
			Map<String, Object> mapTimes , int pageNow , int pageSize ) {

		List list = new ArrayList();
		DBCollection table = null;
		DBCursor cursor = null;
		BasicDBList dbList = new BasicDBList();
		//Query query = new Query();
// 1.  mapTimes里的时间可能是 testingTimes 也可能是 productEndTimes
		if ( mapTimes != null )
		{
			boolean flag = true;//默认分页
			if( pageNow <= 0 )
			{
				flag = false;
			}
			
			if( pageSize <= 0){
					pageSize = 30;
			}
			
			for( Map.Entry<String, Object> m : mapTimes.entrySet() )
			{
// 2. 提供的是测试时间，则在测试表中查询出该时间段内的产品deviceID
				if( "testingTimes".equals(m.getKey()) )
				{
					table = mongoTemplate.getCollection("testingTable");
					
					JSONObject j = JSONObject.fromObject(m.getValue());
					String start = j.getString("start");
					String end = j.getString("end");
					
					BasicDBObject parameter1 = new BasicDBObject();
					try {
						parameter1.put("$gt", new SimpleDateFormat("yyyy-MM-dd").parse(start));
						parameter1.put("$lte", new SimpleDateFormat("yyyy-MM-dd").parse(end));
					} catch (ParseException e1) {
						
						e1.printStackTrace();
					}
					
					
					
					BasicDBObject dbObject1 = new BasicDBObject();
					dbObject1.put("testDate", parameter1);
					
					DBCursor testCursor = null;
					
					try {
						if( flag ) //分页，测试表中的数据与产品表中的数据是一一对应
						{
							testCursor = table.find(dbObject1);
							//testCursor = table.find(dbObject1).skip((pageNow - 1)*pageSize).sort(new BasicDBObject("_id",-1) ).limit(pageSize);
						}
						else
						{
							testCursor = table.find(dbObject1);
						}
						
					} catch (Exception e) {
						log.error("按测试时间段查询测试数据时发生异常：" , e);
					}
	// 2.1  将该段时间内的测试数据放入testList中
					List<DBObject> testList = new ArrayList<DBObject>();
					while( testCursor.hasNext() )
					{
						testList.add(testCursor.next());
						//System.out.println(testList);
						//JSONObject jsonObject = JSONObject.fromObject(testList.get(0).toString());
						//System.out.println(jsonObject.get("testDate").toString()+"   "+jsonObject.get("testDate"));
					}
	// 2.2 遍历testList，通过产品的deviceID在产品表中获取对应的数据，
					if( testList != null && testList.size() > 0 )
					{
						for(int i = 0 ; i < testList.size() ; i++ )
						{
							try {
								JSONObject jsonObject = JSONObject.fromObject(testList.get(i));
								String deviceID = jsonObject.getString("deviceID");
								//Object obj = findOne("productTable", "deviceID", deviceID);
								Map<String , Object> smMap = new HashMap<String , Object>();
								List<Map<String , Object>> lsList = getInfo("", smMap);
								if( lsList != null )
								{
									for( Map<String , Object> mp : lsList)
									{
										mp.put("testResult", jsonObject.get("result"));
										list.add(mp);
									}
								}
								
								
							} catch (Exception e) {
								log.error("按deviceID查询产品数据时发生异常：" , e);///*********************************
							}
							
							
						}//---遍历每一个产品（有测试数据的）
					}//有测试数据存在
					
				}//-----根据测试的时间查询产品信息
// 3. 提供的是生产完成时间，则在生产完成记录中查询出该时间段内的生产批次
				else if( "productEndTimes".equals(m.getKey()) )
				{
					table = mongoTemplate.getCollection("productNoteTable");
					
					JSONObject j = JSONObject.fromObject(m.getValue());
					String start = j.getString("start");
					String end = j.getString("end");
					
					BasicDBObject parameter1 = new BasicDBObject();
					parameter1.put("$gt", start);
					parameter1.put("$lte", end);
					
					
					BasicDBObject dbObject1 = new BasicDBObject();
					dbObject1.put("productEndDate", parameter1);
					
					DBCursor noteCursor = null;
					List<DBObject> noteList = new ArrayList<DBObject>();
					try {
						noteCursor = table.find(dbObject1);
						while (noteCursor.hasNext()) {
							noteList.add(noteCursor.next());
						}
					} catch (Exception e) {
						log.error("根据生产完成时间段查询生产完成记录信息时发生异常：" , e );
					}
					
					if( noteList != null && noteList.size() > 0 )
					{
						
						for( int i = 0 ; i < noteList.size() ; i++ )
						{
							JSONObject jsonObject = JSONObject.fromObject(noteList.get(i));
							String productBatch = jsonObject.getString("productBatch");
							//通过批次查询产品信息
							if( flag )
							{
								Map<String , Object> pMap = new HashMap<String, Object>();
								pMap.put("productBatch", productBatch);
								List pList = getInfoByPage("productTable", pMap, pageNow, pageSize);
								if( pList != null && pList.size() > 0 )
								{
									list.add(pList);
									if( pList.size() < pageSize )
									{
										pageSize = pageSize - pList.size();//当前业获取到的数量少于需求，则下一轮循环补足
									}
									else if(pList.size() == pageSize){
										break ;//当前业所需数量已经满足，不需要再获取数据了
									}
								}
							}
							//不分页
							else {
								try {
									Object obj = findOne("productTable", "productBatch", productBatch);
									list.add(obj);
									
								} catch (Exception e) {
									log.error("根据产品批次查询产品信息是发生异常：" , e);
								}
								
							}
							
						}//-----遍历该时间段内的每一个批次
					}//----该时间段内有批次存在
				}//---根据生产完成记录中产品完成时间查询
			}
			
		}
		

		if( list.size() == 0 )
		{
			return null;
		}
		else {

			return list;
		}
		

	}//---按时间段分页查询

	//如果提供的key是deviceID ,那么找出对应的_id,再进行更新 >>> 当该产品的状态为3时，即为废弃状态，则不可更改
	public int updateById( String key , String value , Map<String, Object> map )
	{
		BasicDBObject dbObject = new BasicDBObject();
		//获取该产品信息，如果是已废弃的产品，则不可更改
		//Product product = mongoTemplate.findOne(new Query(new Criteria().where(key).is(new ObjectId(value))), Product.class);
		Map<String, Object> proMap = new HashMap<String,Object>();
		proMap.put(key, new ObjectId(value));
		List proList = getInfo("productTable", proMap);
		if( proList == null )
		{
			log.info("该产品不存在，无法修改");
			return -1;
		}
		else {
			JSONObject json = JSONObject.fromObject(proList.get(0));
			int productState = json.getInt("productState");
			if( productState == 3 )
			{
				log.info("该产品已废弃，无法进行更改");
				return -2;
			}
		}
		
		
		if( !("_id".equals(key)) )
		{
			//通过deviceID 找出对应的_id
			BasicImplentsDao dao = new BasicImplentsDao();
			//通过deviceID 找出对应的_id
			Map<String , Object> dmMap = new HashMap<String, Object>();
			dmMap.put(key, value);
			List<Map<String , Object>> list = dao.getInfo("productTable", dmMap );
			
			for( Map<String, Object> m : list )
			{
				value = m.get("_id").toString();
			}
			
			System.out.println("获取到_id="+value);
			
		}
		
		Update update = new Update();
		
		for(Map.Entry<String , Object> m : map.entrySet())
		{
			//System.out.println(m.getKey() +"  ； "+ m.getValue());
			update.set(m.getKey(), m.getValue());
		}
		
		try {
			WriteResult result = mongoTemplate.updateMulti(new Query(Criteria.where("_id").is( new ObjectId(value) ) ), update, Product.class );
			//System.out.println(result);
		} catch (Exception e) {
			log.error("发生异常，更新数据失败: " , e); 
			return -1;
		}
		
		return 1;
		
	}
	
	
}
