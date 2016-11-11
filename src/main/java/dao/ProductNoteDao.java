package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import service.basicService.requestService.UserService;
import dao.basic.BasicImplentsDao;
import domain.User;

public class ProductNoteDao extends BasicImplentsDao {

	private static Log log = LogFactory.getLog(ProductNoteDao.class.getName());

	@Autowired
	private static MongoTemplate mongoTemplate;
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

		
	// 修改生产完成记录，当修改的字段为生产批次时，那么之前批量修改的该批次的产品状态需要还原成 -1
	@Override
	public void updateByOne(Class clazz, String key,  Object value, Map<String, Object> map ) 
	{
		//System.out.println(key+" , "+ value + " , "+ map);
		Update update = new Update();
		
		for(Map.Entry<String , Object> m : map.entrySet())
		{
			//System.out.println(m.getKey() +"  ； "+ m.getValue());
			update.set(m.getKey(), m.getValue());
		}
		
		try {
			WriteResult result = mongoTemplate.updateFirst(new Query(Criteria.where(key).is(value)), update, clazz);
			//System.out.println(result);
		} catch (Exception e) {
			log.error("发生异常，更新数据失败: "+e.getMessage()); 
		}
		
	}	
	
	
	//获取与自己相关的生产完成记录信息（自己的申报的生产完成记录或者自己提交的生产完成记录）
	public List getMyNote( String userName , Map<String, Object> map, int pageNow, int pageSize ) 
	{
		
	// 1. 先从用户表中获取用户 id
		String userid = "";
		try {
			User user = mongoTemplate.findOne( new Query(new Criteria().where("phone").is(userName)) , User.class );
			userid = user.get_id();
			
		} catch (Exception e) {
			log.error("通过账户名获取用户个人信息，发生异常：" , e );
		}
		
		BasicDBList dbList = new BasicDBList();
		
	// 1.2 通过用户id检索申报表，获取用户申报成功的生产批次
		
		BasicDBObject parameter1 = new BasicDBObject();
		Map<String , Object> bMap = new HashMap<String, Object>();
		bMap.put("reportPerson", userid);
		List<Map<String, Object>> bList = getInfo( "reportTable" , bMap );
		if( bList != null ){
			for(Map<String, Object> m : bList )
			{
				BasicDBObject dbObject = new BasicDBObject();
				dbObject.put("productBatch", m.get("productBatch"));
				dbList.add(dbObject);
			}
			parameter1.put("$or", dbList);
		}
		
		
		DBCollection table = mongoTemplate.getCollection("productNoteTable");   
		DBCursor cursor = null;
   // 2. 将查询条件放入parameter		
        BasicDBObject parameter2 = new BasicDBObject(); 
        parameter2.put("notePerson", userid);
        if( map != null && map.size() > 0 )
		{
			for( Map.Entry<String, Object> entry : map.entrySet() )
			{
				parameter2.put(entry.getKey(), entry.getValue());
			}
		}
        
        BasicDBObject parameter = new BasicDBObject(); 
        BasicDBList cond = new BasicDBList();
        cond.add(parameter1);
        cond.add(parameter2);
        parameter.put("$and", cond );
        
        List<DBObject> list = new ArrayList<DBObject>();
   // 3. 分页查询       
        if( pageNow > 0 )
    	{
    		if( pageSize <= 0 )
    			pageSize = 30;//默认30页
    		
    		try {
    			
    			cursor = table.find(parameter).skip((pageNow - 1)*pageSize).sort(new BasicDBObject("_id",-1) ).limit(pageSize);
        		
			} catch (Exception e2) {
				log.error("发生异常，获取信息失败 ：", e2);
			}
    		
    	}
   // 4. 全部查询，不分页
    	else //pageSize <= 0 
    	{
    		try {
    			cursor = table.find(parameter);
    			
			} catch (Exception e2) {
				log.error("发生异常，获取信息失败 ：", e2);
			}
    		
    	}
   // 5. 将查询结果放进list    	
      	while( cursor.hasNext() )
        {
        	list.add(cursor.next());
        }
        
        
        if( list.size() == 0 )
        {
			return null;
		}
        else {
        	return list;
		}
		
		
	}	
	
	
}
