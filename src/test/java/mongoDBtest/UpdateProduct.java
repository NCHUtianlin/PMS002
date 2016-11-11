package mongoDBtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import dao.basic.BasicImplentsDao;
import domain.Product;

public class UpdateProduct {
	
private static MongoTemplate mongoTemplate;
	
		public static void main(String[] args)
		{
			
			ApplicationContext aContext = new ClassPathXmlApplicationContext("config/spring.xml");
			mongoTemplate = (MongoTemplate) aContext.getBean("mongoTemplate");
			Map<String , Object> map = new HashMap<String, Object>();
			map.put("productState", 1);
			updateById("deviceID" , "DEV_HY_lock_20162016-11-02 20:41:450" , map );
		}

	//如果提供的key是deviceID ,那么找出对应的_id,再进行更新
		public static int updateById( String key , String value , Map<String, Object> map )
		{
			BasicDBObject dbObject = new BasicDBObject();
			
			
			if( !("_id".equals(key)) )
			{
				BasicImplentsDao dao = new BasicImplentsDao();
				//通过deviceID 找出对应的_id
				Map<String , Object> dmMap = new HashMap<String, Object>();
				dmMap.put(key, value);
				List<Map<String , Object>> list = dao.getInfo("productTable", dmMap );
				
				for( Map<String, Object> m : list )
				{
					value = m.get("_id").toString();
					/*for( String k : m.keySet() )
					{
						System.out.println( m.get(k) );
					}*/
				}
				
				System.out.println("获取到_id="+value);
				
			}
			
			/*Update update = new Update();
			
			for(Map.Entry<String , Object> m : map.entrySet())
			{
				//System.out.println(m.getKey() +"  ； "+ m.getValue());
				update.set(m.getKey(), m.getValue());
			}
			
			try {
				WriteResult result = mongoTemplate.updateMulti(new Query(Criteria.where("_id").is( new ObjectId(value) ) ), update, Product.class );
				//System.out.println(result);
			} catch (Exception e) {
				System.out.println("发生异常，更新数据失败: " + e); 
			}*/
			
			return 1;
			
		}
		
}
