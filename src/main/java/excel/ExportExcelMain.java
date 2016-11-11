package excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import dao.basic.BasicImplentsDao;


public class ExportExcelMain {
	
	public static final String FILE_SEPARATOR = System.getProperties().getProperty("file.separator");
	private static MongoTemplate mongoTemplate;
	
	public static void main(String[] srga)
	{
	
		ApplicationContext aContext = new ClassPathXmlApplicationContext("config/spring.xml");
		mongoTemplate = (MongoTemplate) aContext.getBean("mongoTemplate");
		BasicImplentsDao dao = new BasicImplentsDao();
		
		List<Map<String , Object>> list = new ArrayList<Map<String,Object>>();
		list = dao.getInfo("userTable", null);
		
		
		String imagesPath = "E:";
		String docsPath = "E:";
		(new ExportExcel()).test(imagesPath, docsPath , list , "Users");
		String fileName = "export2003_a.xls";
		String filePath = docsPath + FILE_SEPARATOR + fileName;
		//download(filePath, response);
		
		for( Map<String , Object> map : list )
		{

			Iterator iterator = list.iterator();  
	        Object o = iterator.next();  
	        if(o instanceof Map){  
	            Map m = (Map) o;  
	            System.out.println(m);  
	            Iterator iter = m.entrySet().iterator();  
	            int first_j=0;  
	            while (iter.hasNext()) {  
	                System.out.println("------");
	                Map.Entry entry = (Map.Entry) iter.next();  
	                //System.out.println(entry);
	                String[] key_value = entry.toString().split("=");  
	                
	                for( int i=0 ; i< key_value.length ; i++ )
	                {
	                	System.out.println(key_value[i]);  
	                }
	                
	                
	                //sheet.addCell(new Label(first_j, 4, key_value[1], wcf_left));  
	                first_j++;  
	            }  
	            /*int i=5;  
	            while(iterator.hasNext()){  
	                Map row_map = (Map) iterator.next();  
	                Iterator row_iterator = row_map.entrySet().iterator();  
	                int second_j=0;  
	                while(row_iterator.hasNext()){  
	                    Map.Entry entry_column = (Map.Entry) row_iterator.next();  
	                    String[] key_value = entry_column.toString().split("=");  
	                    //sheet.addCell(new Label(second_j, i, key_value[1], wcf_left));  
	                    second_j++;  
	                }  
	                i++;  
	            }*/
	            
	        }

			
		}
		
		
		
		
	}
	
	private void download(String path, HttpServletResponse response) {
		try {
			// path是指欲下载的文件的路径。
			File file = new File(path);
			// 取得文件名。
			String filename = file.getName();
			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(filename.getBytes()));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(
					response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	

}
