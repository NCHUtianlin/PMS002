package service.basicService.reponseService;

import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import service.basicService.requestService.ReportService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BasicResponseService {
	
	private static Log log = LogFactory.getLog(BasicResponseService.class.getName());
	
	private int totalSize = 0;//表的数据总量
	
	
	public void  errorResponse( HttpServletResponse response , int errorCode, 
			String errorCodeMsg  , String data, int statusCode ,String userAddress)
	{
		try {
			
			/*****************响应数据***************/
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			
			response.setStatus(statusCode);

			//String userBack = "{\"errorCode\":\""+errorCode+"\",\"errorCodeMsg\":\""+errorCodeMsg+"\",\"data\":\""+data+"\"}";
			//JSONObject jObject = JSONObject.fromObject(userBack);
			JSONObject jObject = new JSONObject();
			jObject.put("errorCode", errorCode);
			jObject.put("errorCodeMsg", errorCodeMsg);
			jObject.put("data", data);
			
			out.print(jObject);
			out.flush();
			out.close();
			log.info(userAddress+" 操作失败，返回客户端信息："+jObject);
			jObject.clear();
			
		} catch (Exception e) {
			log.error(userAddress+" 系统发送响应报文失败 ，发生异常：" ,e);
		}
		
		
	}
	//返回消息体为空
	public void okResponseEmpty( HttpServletResponse response , int statusCode ,String userAddress)
	{

		/*****************响应数据***************/
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
			
		response.setStatus(statusCode);
		log.info(userAddress+" 操作成功，返回给客户端状态码 200");	
		
	}
	//返回数据
	public void okResponseGetInfo( HttpServletResponse response , int statusCode , List<Map<String, Object>> list ,String userAddress)
	{
		
		try{
			/*****************响应数据***************/
			response.setContentType("text/json");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();

			response.setStatus(statusCode);
	
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			
			for( Map<String,Object> m:list)
			{
				JSONObject jsonObject2 = new JSONObject();
				for(String key : m.keySet() )
				{
					//System.out.println(key+" : "+ m.get(key));
					if( m.get(key) == null )
					{
						jsonObject2.put(key, "" );
					}
					else {
						jsonObject2.put(key, m.get(key).toString() );
					}
					
				}
				
				//jsonObject.put("phone", m.get("phone"));
				jsonArray.add(jsonObject2);
			}
			
			if( totalSize > 0 )
			{
				
				jsonObject.put("total", totalSize);
				
			}
			jsonObject.put("rows", jsonArray);
			
			out.print(jsonObject);
			out.flush();
			out.close();
			log.info(userAddress+" 操作成功，返回客户端数据： "+jsonObject);
			
			jsonArray.clear();
			jsonObject.clear();
	
		} catch (Exception e) {
			log.error(userAddress+"发生异常，JSON 格式错误 : ", e);
			
			errorResponse(response, 500, "json格式错误", e.toString(), statusCode , userAddress);
		}

	}
	//返回数据，和上面这个方法不同的是参数，这里携带的参数是已经有Java对象转换好的json字符串
	public void okResponseGetInfo( HttpServletResponse response , int statusCode , String jsonStr ,String userAddress)
	{
		try{
			/*****************响应数据***************/
			response.setContentType("text/html");
			response.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();

			response.setStatus(statusCode);
	
			//System.out.println("返回数据："+jsonStr);
			//String userBack = "{\"userName\":\"Jack\",\"phone\":\"18270893627\"}";
			JSONObject jObject = JSONObject.fromObject(jsonStr);
			
			out.print(jObject);
			out.flush();
			out.close();
			log.info(userAddress+" 操作成功，给客户端返回数据： "+jObject);
		
			jObject.clear();
	
		} catch (Exception e) {
			log.error(userAddress+" 发生异常，JSON 格式错误 : " , e);
			errorResponse(response, 500, "json格式错误", e.toString(), statusCode ,userAddress);
			return ;
		}

	}

	
	
	
// get set 方法
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}


}
