package mongoDBtest;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import dao.MacDao;
import domain.MACInfo;
import service.basicService.internal.BitMac;



public class Mac {
	
	private static MongoTemplate mongoTemplate;
	
	public static void main( String[] arsStrings )
	{
		/*List<String> list = new ArrayList<String>();
		list.add("aaa");
		list.add("bbb");
		System.out.println(list.size()+" : "+list);
		list.clear();
		System.out.println(list.size()+" : "+list);*/
		
		ApplicationContext aContext = new ClassPathXmlApplicationContext("config/spring.xml");
		MacDao dao = new MacDao();
		List<Map<String, Object>> list = dao.getOne(3);
		JSONObject jsonObject = JSONObject.fromObject(list.get(0).toString());
		MACInfo macInfo = (MACInfo) jsonObject.toBean(jsonObject , MACInfo.class);
		
		
		for(Map<String, Object> map : list )
		{
			int num = Integer.parseInt(map.get("usableNum").toString());
			Object object =  map.get("MAC");
			System.out.println( object.getClass().getName()+" : "+ object);
			int[] mac = new int[32];
			for(int i=0 ; i < 32 ; i++ )
			{
				//mac[i] = (int) object[i];
			}
			
		}
		
	}

}
