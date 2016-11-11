package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import service.basicService.requestService.UserService;
import dao.basic.BasicImplentsDao;

@Repository
public class ProductDataDao extends BasicImplentsDao {

	private static Log log = LogFactory.getLog(ProductDataDao.class.getName());
	@Autowired
	private static MongoTemplate mongoTemplate;
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	//按批次查询测试信息
	public List getTestInfoByBatch( String productBatch )
	{
		/*
		 * 由于测试信息表中没有批次字段，那么需要先从产品表中将该批次的所有产品deviceID检索出来，并获取该批次的产品类型
		 * 然后通过deviceID一个一个获取对应的测试数据
		 * */
		//存放最后的测试数据
		List<Object> list = new ArrayList<Object>();
		
		// 1. 通过批次获取产品信息
		List<DBObject> productList = new ArrayList<DBObject>();
		try {
			DBCollection table = mongoTemplate.getCollection("productTable");
			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put("productBatch", productBatch);
			DBCursor cursor = table.find(dbObject);
			
			while( cursor.hasNext() )
			{
				productList.add(cursor.next());
			}
			
		} catch (Exception e) {
			log.error("通过批次获取产品信息过程中发生异常：", e );
		}
		
		if( productList != null && productList.size() > 0 )
		{
			String productTypeName = "";
			//先获取该批次的产品类型名称
			try {
				JSONObject jsonObject1 = JSONObject.fromObject(productList.get(0));
				String productTypeID = jsonObject1.get("productTypeID").toString();
				
				Object typeList = findOne("productTypeTable", "_id", productTypeID);
				
				JSONObject jsonObject2 = JSONObject.fromObject(typeList);
				productTypeName = jsonObject2.getString("productTypeName");
				//System.out.println("<<<<<<<<<<<<<<<"+productTypeName);
			} catch (Exception e) {
				log.error("通过批次获取产品类型名称时发生异常：" , e );
			}
			
			//获取测试数据
			for( int i=0 ; i < productList.size() ; i++ )
			{
				Map<String , Object> map = new HashMap<String, Object>();
				
				try {
					//获取一个deviceID
					JSONObject jsonObject3 = JSONObject.fromObject(productList.get(i));
					String deviceID = jsonObject3.getString("deviceID");
					
					//通过该deviceID获取测试数据
					Object testList = findOne("testingTable", "deviceID", deviceID);
					//该deviceID有测试数据
					if( testList != null )
					{
						JSONObject jsonObject4 = JSONObject.fromObject(testList);
						String testDate = jsonObject4.getString("testDate");
						int result = jsonObject4.getInt("result");
						
						map.put("deviceID", deviceID);
						map.put("testDate", testDate);
						map.put("result", result);
						map.put("productTypeName", productTypeName);
						map.put("productBatch", productBatch);
						list.add(map);
					}
						
				} catch (Exception e) {
					log.error("通过deviceID获取产品的测试数据时发生异常：" , e );
				}
				
			}
			//System.out.println("通过批次获取测试信息："+ list);
		}
		else
		{
			return null;
		}
		
		
		return list;
	}
	
	//模糊查询产品类型数据
	public List queryProductType( Object parameter )
	{
		BasicDBList dbList = new BasicDBList();
		Pattern pattern = Pattern.compile("^.*"+parameter+".*$", Pattern.CASE_INSENSITIVE);  //模糊匹配
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]"); // 汉字编码区间
        Matcher m = p.matcher(parameter.toString());
		
        //含有中文，则匹配产品类型名称
        if( m.find() )
        {
        	BasicDBObject object = new BasicDBObject();
        	object.put("productTypeName", pattern);
        	dbList.add(object);
        }
        else {
        	BasicDBObject object1 = new BasicDBObject();
        	object1.put("hardwareVersion", pattern);
        	BasicDBObject object2 = new BasicDBObject();
        	object2.put("softwareVersion", pattern);
        	
        	dbList.add(object1);
        	dbList.add(object2);
		}
        
        BasicDBObject cond = new BasicDBObject();
        cond.put("$or", dbList);
        DBCursor cursor = null;
        List<DBObject> list = new ArrayList<DBObject>();
        
        try {
			cursor = mongoTemplate.getCollection("productTypeTable").find(cond);
			while ( cursor.hasNext() ) {
				list.add( cursor.next() );
			}
		} catch (Exception e) {
			log.error("模糊查询产品类型是发生异常：" , e );
		}
        
        if( list.size() == 0 )
        	return null;
        else {
			return list;
		}
	}
	
}
