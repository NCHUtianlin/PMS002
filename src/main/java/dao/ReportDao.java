package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import service.basicService.requestService.UserService;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

import dao.basic.BasicImplentsDao;
import domain.Report;

public class ReportDao extends BasicImplentsDao {

	private static Log log = LogFactory.getLog(UserService.class.getName());
	
	@Autowired
	private static MongoTemplate mongoTemplate;
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		//System.out.println("reportDao开始注入。。。。");
		this.mongoTemplate = mongoTemplate;
	}

	//按条件查询自己的信息， 普通用户
	public List getInfoMyself( String tableName , Map<String, Object> map , int pageNow , int pageSize )
	{
		DBCollection  table = mongoTemplate.getCollection(tableName);
		List<DBObject> list = new ArrayList<DBObject>();
		DBCursor cursor = null;
		
		//分页查询
		if( pageNow <= 0 )
			pageNow = 1;
		if( pageSize <= 0 )
			pageSize = 30;
		//没有参数，全部查询
		if( map == null )
		{
			try {
				cursor = table.find().skip((pageNow - 1)*pageSize).limit(pageSize);
			} catch (Exception e) {
				log.error("发生异常，获取申报信息失败：" , e);
			}
			
		}
		//条件查询
		else {
			DBObject parameter = new BasicDBObject();
			for( Map.Entry<String, Object> m : map.entrySet() )
			{
				parameter.put(m.getKey(), m.getValue());
			}
			
			try {
				cursor = table.find(parameter).skip((pageNow - 1)*pageSize).limit(pageSize);
				
			} catch (Exception e) {
				log.error("发生异常，获取申报信息失败：" , e);
			}
			
		}
		
		if( cursor != null && cursor.size() > 0 )
        {
			//System.out.println("数据查询，cursor 里面的元素 "+cursor.size());	
			
			while( cursor.hasNext() )
		    {
		        list.add(cursor.next());
		    }
        }
        else {
			list = null;
		}
	
        //System.out.println("查询结果list : "+ list);
		return list;
	}	
	//修改申报信息：修改的申报信息是已经审批了的，那么就不能做修改了，只能重新申报
	  @Override
	  public int updateById(Class clazz, String id, Map<String, Object> map) {
	    // 判断该信息是否已经被审批了
	    //Report report = (Report) getById(Report.class, id);

	    BasicDBObject dbObject = new BasicDBObject();
	    dbObject.put("_id", new ObjectId(id) );
	    
	    BasicDBObject dbObject2 = new BasicDBObject().append("checkResult", new BasicDBObject( QueryOperators.NIN, new int[] { 0,1 }));
	    
	    BasicDBList dbList = new BasicDBList();
	    dbList.add(dbObject);
	    dbList.add(dbObject2);
	    BasicDBObject cond = new BasicDBObject();
	    cond.put("$and", dbList);
	    
	    DBCursor cursor = mongoTemplate.getCollection("reportTable").find(cond);
	    //System.out.println(cursor);
	    
	    //System.out.println("cursor.hasNext() = "+ cursor.hasNext());
	    //信息被审批了 （拒绝 or 同意）
	    if ( !cursor.hasNext() )
	    {
	      log.info("该申报表已经审核，不能进行修改");
	      return -2;
	    }
	    
	    Update update = new Update();
	    
	    for(Map.Entry<String , Object> m : map.entrySet())
	    {
	      update.set(m.getKey(), m.getValue());
	    }
	    
	    try {
	      mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(id)), update, clazz);

	    } catch (Exception e) {
	      log.error("发生异常，修改信息失败：" , e);
	      return -5;
	    }
	    log.info("修改申报信息成功。");
	    return 1;
	  }

	//按时间段查询
	public List getInfoByTimes(String tableName, Map<String, Object> map,
			Map<String, Object> mapTimes , int pageNow , int pageSize ) {

		List<DBObject> list = new ArrayList<DBObject>();
		DBCollection table = mongoTemplate.getCollection(tableName);
		DBCursor cursor = null;	
		//Query query = new Query();
		// mapTimes里时间段查询 reportStartTimes reportEndTimes reportTimes三个时间段
			
		if ( mapTimes != null )
		{
				
			//System.out.println("时间段："+mapTimes.get("start")+" : "+ mapTimes.get("end"));
			//大于等于
			//query.addCriteria(Criteria.where("reportStartDate").gte(mapTimes.get("start")));
			//小于等于
			//query.addCriteria(Criteria.where("reportEndDate").lte(mapTimes.get("end")));	
			BasicDBList dbList = new BasicDBList();
			
			BasicDBObject parameter1 = new BasicDBObject();
			parameter1.put("$gt", mapTimes.get("start"));
			parameter1.put("$lte", mapTimes.get("end"));
			
			BasicDBObject dbObject1 = new BasicDBObject();
			dbObject1.put("reportStartDate", parameter1);
			BasicDBObject dbObject2 = new BasicDBObject();
			dbObject2.put("reportEndDate", parameter1);
			BasicDBObject dbObject3 = new BasicDBObject();
			dbObject3.put("reportTime", parameter1);
			
			dbList.add(dbObject1);
			dbList.add(dbObject2);
			dbList.add(dbObject3);
			
			BasicDBObject cond = new BasicDBObject();
			cond.put("$or", dbList);
			
			table = mongoTemplate.getCollection(tableName);
			
			try {
				if( pageNow <= 0 )
				{
					cursor = table.find(cond);
				}
				else {
					if( pageSize <= 0)
						pageSize = 30;
					
					cursor = table.find( cond ).skip((pageNow - 1)*pageSize).sort(new BasicDBObject("_id",-1) ).limit(pageSize);
					
				}
				
			} catch (Exception e) {
				log.error("发生异常，获取数据失败： ", e);
			}
			
		}
		else {
			list = null;
		}
		
		if( cursor.hasNext() )
		{
			list.add(cursor.next());
		}
		else {
			list = null;
		}
		
		return list;
		
	}	
	  
	
}
