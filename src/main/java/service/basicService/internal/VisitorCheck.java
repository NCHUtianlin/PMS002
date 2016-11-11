package service.basicService.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import controller.HomeController;
import controller.UserController;
import dao.UserDao;
import domain.User;
import service.basicService.reponseService.PublicResponse;
import service.basicService.reponseService.UserResponseService;
import service.interfaceService.BasicImplementService;

public class VisitorCheck {
	
	private static Log log = LogFactory.getLog(VisitorCheck.class.getName());
	private PublicResponse publicResponse = new PublicResponse();
	private static Map<String , Object> visitorMap = new HashMap<String, Object>();//存放已登陆用户的账户名和IP
	
	private UserResponseService uResponseService = new UserResponseService();
	//private PublicResponse publicResponse = new PublicResponse();
	
	
	public static void removeVisitorMap( String key ) {
		visitorMap.remove(key);
	}

	public static void setVisitorMap(String userName , String ip) {
		visitorMap.put( userName , ip );
	}
	
	
	public int exitSystem( HttpServletRequest request ,HttpServletResponse response )
	{
		
		String userName = "";
		String hashedPassword = "";
		
		try {
			userName = request.getHeader("userName");
			hashedPassword = request.getHeader("hashedPassword");
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+"账户信息不全(userName="+userName+",hashedPassword="+hashedPassword+")，未能识别用户身份,抛弃请求");
			log.error(request.getRemoteAddr()+"检测账户信息异常：",e);
			return -1;
		}
		
		if( userName == null || userName.length() == 0 )
		{
			log.info( request.getRemoteAddr()+ " 未知用户非法访问系统，请求方式：GET， 请求的URL=" + request.getRequestURL()+" 抛弃请求" );
			publicResponse.publicResponse(request, response, -422);
			return -1;	
		}
		else if( hashedPassword == null || hashedPassword.length() == 0 ){
			log.info(request.getRemoteAddr()+"账户信息不全(userName="+userName+",hashedPassword="+hashedPassword+")，未能识别用户身份，抛弃请求");
			return -1;
		}
		else {
			UserDao dao = new UserDao();
			User user = dao.getAccount(userName, hashedPassword);
			if( user != null )
			{
				return 1;
			}
			else {
				return -1;
			}
		}
		
	}
	
	public int visitorCheck( HttpServletRequest request , HttpServletResponse response ,String method , String resource ) throws IOException
	{
		String ip = request.getRemoteAddr();
		String url = request.getRequestURL().toString();
		String userName = "";
		if("export".equals(resource))
		{
			 userName = request.getParameter("userName");
		}
		else {
			userName = request.getHeader("userName");
		}
		
		if( userName == null || userName.length() == 0 )
		{
			log.info( request.getRemoteAddr()+ " 未知用户非法访问系统，请求方式：GET， 请求的URL=" + request.getRequestURL() );
			uResponseService.userResponse( request, response, -422 );
			return -1;	//非法访问
		}
		
		log.info("## "+ ip + " 账户名为"+userName+"访问系统，请求方式："+method+"， 请求资源："+resource+"， 请求的URL=" + url );
		
		boolean flag = true;
		if( visitorMap.size() == 0 )
		{
			flag = true;
		}
		//System.out.println(flag);
		//遍历Map,判断该用户是否已经登陆(  0 登陆 了但是在不同的IP地址上    1 已经登陆在同一台主机上   2 没有登陆  )
		for( String key : visitorMap.keySet() )
		{
			String history = visitorMap.get(key).toString();
			//System.out.println( history );
			
			if( userName.equals(key) )
			{
				flag = false;
				String oldIP = visitorMap.get(key).toString();
				String nowIP = request.getRemoteAddr().toString();
				//判断此次登陆与之前登陆的IP是否一致
				if( nowIP.equals(oldIP))
				{
					//log.info(request.getRemoteAddr()+" 登陆在同一台主机上");
					if( resource.equals("login"))
					{
						removeVisitorMap(userName);
					}
					return 1;//已经登陆在同一台主机上
				}
				//登陆 了但是在不同的IP地址上
				else {
					log.info( request.getRemoteAddr()+"用户"+userName+"已经在主机"+visitorMap.get(key)+"登陆,请先退出其他账户");
					publicResponse.publicResponse(request, response, -435);
					return -1;
				}
				
			}
			else {
				log.info(request.getRemoteAddr()+" 用户"+userName+"未经过登陆，非法访问系统，请先登陆");
				flag = true;
			}
		}
		
		//经过判断，该用户还未登陆，请先登陆
		if( flag && ( !resource.equals("login") ) )
		{
			log.info(request.getRemoteAddr()+" 用户"+userName+"还未登陆，请先登陆");
			//HomeController controller = new HomeController();
			//controller.main(request, response);//跳转到登陆页面
			return 0;
			
		}else {
			return 1;
		}
		
		
	}
	
	

}
