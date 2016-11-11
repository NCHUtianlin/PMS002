package controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import service.basicService.internal.VisitorCheck;
import service.basicService.reponseService.PublicResponse;
import service.basicService.reponseService.UserResponseService;
import service.basicService.requestService.UserService;

@Controller		
public class UserController {
	
	private static Log log = LogFactory.getLog(UserController.class.getName());
	private UserService userService = new UserService();
	private UserResponseService uResponseService = new UserResponseService();
	private PublicResponse publicResponse = new PublicResponse();
	private VisitorCheck visitor = new VisitorCheck();

	
	@RequestMapping(value = "/user" , method = RequestMethod.GET)		
	public void getUser(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		
		visitor.visitorCheck(request,response, "GET", "user");
		int k = userService.getUser(request, response);
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			uResponseService.userResponse( request,response, k);
		}
		
		log.info( request.getRemoteAddr()+", GET请求的访问结束 ## ");
		
	}
	
	@RequestMapping(value="/user" , method = RequestMethod.POST)
	public void addUser(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request,response, "POST", "user");
		
		int k = userService.addUser(request);
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			uResponseService.userResponse( request,response, k);
		}
		
		log.info( request.getRemoteAddr()+", POST请求的访问结束 ##");
		
	}
	
	@RequestMapping(value="/user/*" , method = RequestMethod.DELETE)
	public void deleteUser(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request,response, "DELETE", "user");
		int k = userService.deleteUser(request);
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			uResponseService.userResponse( request,response, k);
		}
		
		log.info( request.getRemoteAddr()+", DELETE请求的访问结束   ##");
		
	}
	
	
	@RequestMapping(value="/user/*" , method = RequestMethod.PUT)
	public void updateUser(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request,response, "PUT", "user");
		int k = userService.updateUser(request);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			uResponseService.userResponse( request,response, k);
		}
		
		log.info( request.getRemoteAddr()+", PUT请求的访问结束 ##");
		
	}
	
	
	
	
}
