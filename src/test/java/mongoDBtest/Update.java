package mongoDBtest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import service.basicService.internal.OperProductData;
import dao.basic.BasicImplentsDao;
import domain.ProductNote;
import domain.Report;

public class Update {

	private static MongoTemplate mongoTemplate;
	
	public static void main(String[] args)
	{
		
		ApplicationContext aContext = new ClassPathXmlApplicationContext("config/spring.xml");
		mongoTemplate = (MongoTemplate) aContext.getBean("mongoTemplate");
		
		//OperProductData  oper = new OperProductData();
		//oper.addProduct("580df3cdee61b212a40ec293", 600, "20161024092900029" );
		 
		
		//Map<String, Object> map = new HashMap<String, Object>();
		//map.put("productTotalQuantity",2323);
		
		//BasicImplentsDao dao = new BasicImplentsDao();
		
		//Object object = dao.findOne("productNoteTable", "_id", "101");
		//System.out.println(object);
		
		//dao.updateByOne(ProductNote.class, "productBatch", "20161230", map);;
		
		//dao.updateById(ProductNote.class, "101", map);
		
		//�������������
		//Object obj = dao.getByOne(ProductNote.class, "_id", "101");
		
		//Object obj = dao.getByOne(ProductNote.class, "productBatch", "20161230");
		//System.out.println(obj);
		
	}
	
}
