package dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import service.basicService.requestService.UserService;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

import dao.basic.BasicImplentsDao;
import domain.User;

@Repository
public class UserDao extends BasicImplentsDao{
	
	private static Log log = LogFactory.getLog(UserDao.class.getName());
	@Autowired
	private MongoTemplate mongoTemplate;
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		//System.out.println("UserDao开始注入。。。。");
		this.mongoTemplate = mongoTemplate;
	}
 
	//管理员查询所有用户信息，工作状态workState为-1的用户数据不能被显示出来（他已经被管理员删除了）
	public List getAllUser(String tableName, int pageNow, int pageSize) 
	{

		DBCollection  table = mongoTemplate.getCollection(tableName);
		table = mongoTemplate.getCollection(tableName);  
		DBCursor cursor = null;
        List<DBObject> list = new ArrayList<DBObject>();
        //工作状态不能为-1
        BasicDBObject parameter = new BasicDBObject(); 
        parameter.append("workState", new BasicDBObject(QueryOperators.NIN , new int[]{-1}));
        //按照姓名的首字母顺序排序
        BasicDBObject sortobj = new BasicDBObject();
        sortobj.put("name", 1);
        sortobj.put("phone", 1);
        
        try {
			
	        if( pageNow > 0 )
	        {
	    		if( pageSize <= 0 )
	    			pageSize = 30;//默认30页
	    		
	    		cursor = table.find().skip((pageNow - 1)*pageSize).sort(sortobj).limit(pageSize);
	    	}
	    	else 
	    	{
	    		cursor = table.find(parameter).sort(sortobj);//全部查询，不分页
	    	}
	    	
	    	while( cursor.hasNext() )
	        {
	        	list.add(cursor.next());
	        }	
        } catch (Exception e) {
			log.error("发生异常，获取用户信息失败："+e.getMessage());
		}
        
		if( list == null || list.size() == 0 )
			return null;
		else
			return list;
	}
	
	//通过id删除信息
	public int deleteById(String tableName , String id) 
	{
		Criteria criteria = Criteria.where("_id").in(id);

        if (criteria != null) {
            Query query = new Query();
            query.addCriteria(criteria);
            
            try {
            	query.addCriteria(new Criteria().where("workState").ne(-1));//工作状态不是-1 的（被管理员删除的）
                
                if (query != null && mongoTemplate.findOne(query, User.class) != null)
                    mongoTemplate.remove(mongoTemplate.findOne(query, User.class));
                else {
    				return -1;//信息已经不存在
    			}
			} catch (Exception e) {
				log.error("发生异常，删除用户失败："+e.getMessage());
			}
            
        } 
        return 1;//操作成功
	}	
	
	//单字段查询用户信息，排除已删除用户，即用户工作状态workSate为-1
	public User getUserInfo( String key , String value)
	{
		User user = new User() ;
		DBCollection table = mongoTemplate.getCollection("userTable");
		/*DBCursor cursor = null;
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put(key, value);
		dbObject.append("workState", new BasicDBObject(QueryOperators.NIN , new int[]{-1}));*/
		
		Criteria criteria = new Criteria();
		criteria.and(key).is(value);
		criteria.and("workState").ne(-1);
		
		try {
			user = mongoTemplate.findOne( new Query(criteria) , User.class );
		} catch (Exception e) {
			log.error("通过"+key+"="+value+"查询用户信息时发生异常：" , e);
		}
		//log.info(">>>>>>>>>>>> "+user.toJson());
		
		return user;
	}
	
	//检索账户信息：登陆、获取权限---->> 账户名是手机号码
	public User getAccount( String userName )
	{   
		User user = null;
		Criteria criteria = new Criteria();  
		criteria.and("phone").is(userName);  
		//criteria.and("password").is(hashedPassword); 
		criteria.and("workState").is(1);//用户工作状态必须是1（正常）才能登陆和查询数据
		Query query = new Query(criteria);
		
		try {
			user = mongoTemplate.findOne(query, User.class);
			
		} catch (Exception e) {
			log.error("发生异常，获取账户信息失败： " , e);
		}
		
		return user;
	}	
		

}
