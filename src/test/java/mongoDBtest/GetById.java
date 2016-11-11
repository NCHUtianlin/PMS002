package mongoDBtest;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.client.result.DeleteResult;

import dao.DataAnalysisDao;
import dao.basic.BasicImplentsDao;
import dao.basic.QueryDao;
import domain.Report;
import domain.User;

public class GetById {
	

	private static MongoTemplate mongoTemplate;
	
	
	
	public static void query()
	{
		DBCollection table = mongoTemplate.getCollection("productTable");
		QueryDao dao = new QueryDao();
		BasicDBObject dObject = dao.queryProduct("DCA3AC33");
		DBCursor cursor = table.find(dObject);
		while( cursor.hasNext() )
		{
			System.out.println(cursor.next());
		}
		
	}
	
	
	public static void main(String[] args)
	{
		 
		ApplicationContext aContext = new ClassPathXmlApplicationContext("config/spring.xml");
		mongoTemplate = (MongoTemplate) aContext.getBean("mongoTemplate");
		//get("_id","57fca78a812f73061c08e965");
		
		//delete("_id", "57fca78a812f73061c08e965");
		
		//query();
		
		//DataAnalysisDao data = new DataAnalysisDao();
		//data.upTime("2012-3-2","2012-5-23");
		

		/*DataAnalysisDao dao = new DataAnalysisDao();
		List<Map<String, Object>> list = dao.productBatch();
		if( list != null )
		{
			for( Map<String , Object> m : list )
			{
				for(String key : m.keySet() )
				{
					System.out.println(key+" : "+ m.get(key));
					
				}
			}
		}
		*/
		
		
		BasicImplentsDao dao = new BasicImplentsDao();
		Report report = (Report) dao.getByOne(Report.class , "_id" , "58036abb812f731b141b3adf");
		if( report == null )
		{
			System.out.println("000000000000000000000000000000000");
		}
		else {
			System.out.println("OK..................");
		}
		System.getProperty("java.classpath");
	}
	
/*****************************************************************************************************************/	
	
	public static void get(String name , Object value)
	{
		User user = mongoTemplate.findOne(new Query(Criteria.where(name).is(value)), User.class);
		System.out.println("user : "+user.get_id()+" , "+user.getName());
	}
	
	public static void delete(String name , String value)
	{
		Criteria criteria = Criteria.where("_id").in(value);
        if (criteria != null) {
            Query query = new Query(criteria);
            if (query != null && mongoTemplate.findOne(query, User.class) != null)
                mongoTemplate.remove(mongoTemplate.findOne(query, User.class));
        } 
	}

}
