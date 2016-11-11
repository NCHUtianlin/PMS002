package dao.basic;

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
import org.springframework.stereotype.Repository;

import service.basicService.requestService.UserService;
import service.interfaceService.BasicImplementService;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import domain.User;

@Repository
public class BasicImplentsDao extends BasicImplementService implements BasicInterfaceDao {
	
	private static Log log = LogFactory.getLog(BasicImplentsDao.class.getName());
	
	private DBCollection table;
	
	private static MongoTemplate mongoTemplate;
	@Autowired
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		System.out.println("自动注入。。" + mongoTemplate.getDb());
	}

	
	//获取信息，当map里面只有一个对象时，即单条件查询；多个对象则为多条件查询；null时为全部查询
	public List getInfo(String tableName , Map<String, Object> map)
	{
		List<DBObject> list = new ArrayList<DBObject>();
		DBCursor cursor = null;
		table = mongoTemplate.getCollection(tableName); 
		if( map == null )
		{
			try {
				cursor = table.find();
				while (cursor.hasNext()) {  
		            list.add(cursor.next());
		        }
			} catch (Exception e) {
				log.error("发生异常，获取信息失败 ：", e);
			}
			
		}
		else {
	        BasicDBObject parameter = new BasicDBObject(); 
	        for ( Map.Entry<String , Object > entry : map.entrySet() ) {
	        	//System.out.println(entry.getKey()+" , "+ entry.getValue());
				parameter.put(entry.getKey(), entry.getValue());
			}
	        
	        try {
	        	cursor = table.find(parameter);  
		        while (cursor.hasNext()) {  
		            list.add(cursor.next());
		        }
		        
			} catch (Exception e) {
				log.error("发生异常，获取信息失败 ：", e);
			}
	        
	        /*if(list.size() > 0)
	        {
	        	for(int i=0; i < list.size(); i++)
	        		System.out.println("getInfo : "+list.get(i));
	        }
	        else {
				System.out.println("getInfo()。。。。没有数据");
				list = null;
			}*/
	        if( list.size() == 0 )
	        {
	        	list = null;
	        }

		}
		//System.out.println(tableName+" >> getInfo: "+list);
		return list;

	}
	
	//分页查询,map为null则查询所有信息,返回List<Map<String , Object>>
	public List getInfoByPage(String tableName, Map<String, Object> map, int pageNow, int pageSize) 
	{

		table = mongoTemplate.getCollection(tableName);   
        BasicDBObject parameter = new BasicDBObject(); 
        DBCursor cursor = null;
        List<DBObject> list = new ArrayList<DBObject>();
        
        if( map == null || map.size() == 0 )//返回所有信息
        {
        	if( pageNow > 0 )
        	{
        		if( pageSize <= 0 )
        			pageSize = 30;//默认30页
        		
        		try {
        			//System.out.println("page:"+pageNow+" - "+pageSize);
        			cursor = table.find().skip((pageNow - 1)*pageSize).sort(new BasicDBObject("_id",-1) ).limit(pageSize);
            		
				} catch (Exception e2) {
					log.error("发生异常，获取信息失败 ：", e2);
				}
        		
        	}
        	else //pageSize <= 0 
        	{
        		try {
        			cursor = table.find();//全部查询，不分页
        			
				} catch (Exception e2) {
					log.error("发生异常，获取信息失败 ：", e2);
				}
        		
        	}
        	
        	while( cursor.hasNext() )
            {
            	list.add(cursor.next());
            }
        }//-----map为空，没有参数，全部查询
        //map不为空，按条件查询
		else if( map != null && map.size() > 0 )
		{
			for( Map.Entry<String, Object> entry : map.entrySet() )
			{
				parameter.put(entry.getKey(), entry.getValue());
			}
			
			if( pageNow > 0 )
			{
				if( pageSize <= 0 )
					pageSize = 30;
				
				try {
					//System.out.println("page:"+pageNow+" - "+pageSize);
					cursor = table.find( parameter ).skip((pageNow - 1)*pageSize).sort(new BasicDBObject("_id",-1) ).limit(pageSize);
					
				} catch (Exception e) {
					log.error("发生异常，获取信息失败 ：", e);
				}
				
			}
			else {
				try {
					cursor = table.find(parameter);
					
				} catch (Exception e) {
					log.error("发生异常，获取信息失败 ：", e);
				}
				
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
	
       // System.out.println("查询结果list : "+ list);
		return list;
		
	}
		
	//插入信息，返回值大于 0  则表示插入成功
	public int insert(String tableName , Map<String , Object> map)
	{
		//System.out.println("表名 ： "+tableName);
		try {
			table = mongoTemplate.getCollection(tableName); 
			DBObject dbObject = new BasicDBObject();
			for( Map.Entry<String, Object> m : map.entrySet() )
			{
				dbObject.put(m.getKey(), m.getValue());
			}
			table.insert(dbObject);
				
		} catch (Exception e) {
			log.error("发生异常，插入数据失败 ：", e);
			return -1;
		}
		return 1;
	}

	//通过id修改信息，修改的内容存放在map里面<字段名，值>
	public int updateById(Class clazz, String id, Map<String, Object> map ) 
	{
		Update update = new Update();
		
		for(Map.Entry<String , Object> m : map.entrySet())
		{
			update.set(m.getKey(), m.getValue());
		}
		try {
			mongoTemplate.updateFirst(new Query(Criteria.where("_id").is( new ObjectId(id) )), update, clazz);

		} catch (Exception e) {
			log.error("发生异常，更新信息失败 ：", e);
			return -1;
		}
		
		return 1;

	}

	// 更新产品信息，修改的内容存放在map里面<字段名，值>
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
			WriteResult result = mongoTemplate.updateMulti(new Query(Criteria.where(key).is(value)), update, clazz);
			//System.out.println(result);
		} catch (Exception e) {
			log.error("发生异常，更新数据失败: " , e); 
		}
		
	}	
	
	
	public Object getById(Class clazz , String id) {
		Object obj = new Object();
		try {
			obj = mongoTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(id))), clazz);
			
		} catch (Exception e) {
			log.error("发生异常，通过id获取数据失败: ", e);
			obj = null;
		}
		
		//System.out.println(obj);
		return obj;
	}

	//单字段查询( 表名，字段名，值 )
	public Object getByOne( Class clazz , String key , Object value )
	{
		Object obj = new Object();
		Criteria criteria = new Criteria();
		//如果是查询用户信息，被删除的用户标记workState为-1，对管理员是不可见的
		if( clazz.getClass().getName().contains("userTable") )
		{
			criteria.orOperator(Criteria.where("workState").is(0),Criteria.where("workState").is(1));
			criteria.and(key).is(value);
			Query query = new Query(criteria);
			try {
				obj = mongoTemplate.find(query, clazz);
				
			} catch (Exception e) {
				log.error("发生异常，获取数据失败 ： ", e);
			}
			
		}
		else {
			try {
				obj = mongoTemplate.findOne(new Query(Criteria.where(key).is(value)), clazz);
				
			} catch (Exception e) {
				log.error("发生异常，获取数据失败 ： ", e);
			}
			
		}
	
		return obj;
	}
	//单字段查询一条数据
	public Object findOne( String tableName , String key , String value )
	{
		List<Object> list = new ArrayList<Object>();
		table = mongoTemplate.getCollection(tableName);
		DBObject dbObject = new BasicDBObject();
		if( "_id".equals(key) )
			dbObject.put(key, new ObjectId(value));
		else
			dbObject.put(key, value);
		
		try {
			DBCursor cursor = table.find(dbObject);
			while( cursor.hasNext() )
			{
				list.add(cursor.next());
			}
			
		} catch (Exception e) {
			log.error("发生异常，获取数据失败 ： ", e);
		}
		
		//System.out.println(dbObject +"   find() >>>> "+ list);
		if( list.size() == 0 )
			return null;
		else
		{
			//System.out.println(list);
			return list.get(0);
		}

		
	}
		
	
	//检索账户信息：登陆、获取权限---->> 账户名是手机号码
	public User getAccount( String userName , String hashedPassword )
	{   
		User user = null;
		Criteria criteria = new Criteria();  
		criteria.and("phone").is(userName);  
		criteria.and("password").is(hashedPassword); 
		criteria.and("workState").is(1);//用户工作状态必须是1（正常）才能登陆和查询数据
		Query query = new Query(criteria);
		
		try {
			user = mongoTemplate.findOne(query, User.class);
			
		} catch (Exception e) {
			log.error("发生异常，通过账户名和密码获取账户信息失败： " , e);
		}
		
		return user;
	}
	
	//检索表里的数据总量
	public int getTableCount( String tableName , Map<String , Object> map )
	{
		int count = 0;
	
		table = mongoTemplate.getCollection(tableName); 
		
		try {
			if( map == null )
			{
				count = table.find().count();
				
			}
			else {
		        BasicDBObject parameter = new BasicDBObject(); 
		        for ( Map.Entry<String , Object > entry : map.entrySet() ) {
					parameter.put(entry.getKey(), entry.getValue());
				}
		        
		        count = table.find(parameter).count();  
			}
			
		} catch (Exception e) {
			log.error("发生异常，按条件检索数据总量时出错： ", e);
		}
		
		return count;
	}
	
	//专职模糊查询，与QueryDao对接， 只有在查询用户信息和申报信息的时候，才需要鉴别普通用户与管理员
	public List query( String tableName , Object parameter , int userAuthority , String userid , int pageNow , int pageSize )
	{
		List<DBObject> list = new ArrayList<DBObject>();
		table = mongoTemplate.getCollection(tableName);
		BasicDBObject dObject = new BasicDBObject();
		QueryDao dao = new QueryDao();
		
		if( "userTable".equals(tableName) && userAuthority == 1 )//只要管理员才具有查询其他用户信息的权限
		{
			dObject = dao.queryUser(parameter);
		}
		else if( "reportTable".equals(tableName) ){
			if( userAuthority == 0 )
				dObject = dao.queryReport(parameter , 0 , userid);
			else 
				dObject = dao.queryReport(parameter , 1 , null);
		}
		else if( "productTable".equals(tableName) ){
			dObject = dao.queryProduct(parameter);
		}
		else if( "testingTable".equals(tableName) ){
			dObject = dao.queryTest(parameter);
		}
		else if( "productTypeTable".equals(tableName) )
		{
			dObject = dao.queryProductType(parameter);
		}
		
		if( pageNow <= 0 )
			pageNow = 1;
		if( pageSize <= 0 )
			pageSize = 30;
		
		try {
			
			//System.out.println(dObject);
			
			DBCursor cursor = table.find(dObject).skip( (pageNow-1) * pageSize ).sort(new BasicDBObject("_id", -1)).limit(pageSize);
			while( cursor.hasNext() )
			{
				list.add(cursor.next());
			}
			
		} catch (Exception e) {
			log.error("发生异常，模糊查询数据失败： ", e);
		}
		
		if( list.size() == 0 )
			return null;
		
		else {
			return list;
		}
		
	}
	
	public List getInfo(BasicDBObject object , String tableName )
	{
		List<DBObject> list = new ArrayList<DBObject>();
		table = mongoTemplate.getCollection(tableName);
		try {
			DBCursor cursor = table.find(object);
			while( cursor.hasNext() )
			{
				list.add(cursor.next());
			}
			
		} catch (Exception e) {
			log.error("发生异常，模糊查询数据失败： ", e);
		}
		
		if( list.size() == 0 )
			return null;
		
		else {
			//System.out.println("我在dao里查询的结果是"+list);
			//System.out.println("");
			return list;
		}
		
	}

}
