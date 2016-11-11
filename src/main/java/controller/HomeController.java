package controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import service.basicService.internal.VisitorCheck;
import service.basicService.reponseService.PublicResponse;
import service.basicService.reponseService.UserResponseService;
import service.basicService.requestService.UserService;



@Controller		//注解设定HomeController为控制器类
public class HomeController {
	
	private static Log log = LogFactory.getLog(HomeController.class.getName());
	private UserService userService = new UserService();
	private UserResponseService uResponseService = new UserResponseService();
	private PublicResponse publicResponse = new PublicResponse();

	
	@RequestMapping("/main")		//指定访问该控制器的url
	public ModelAndView main(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		log.info( request.getRemoteAddr()+" 请求访问系统登陆页面，请求的URL="+request.getRequestURL() );
		
		return new ModelAndView("loginPMS","result","ok");	//指定转向登陆页面
	}
	
	@RequestMapping(value = "/login" )		
	public void login(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		
		String userName = request.getHeader("userName");
		int k =0 ;
		//判断该用户是否已经登陆
		VisitorCheck visitor = new VisitorCheck();
		int flag = visitor.visitorCheck(request, response, request.getMethod(), "login");
		if( flag < 0 )
		{
			log.info(request.getRemoteAddr()+" 登陆失败");
			return ;
		}
		else {
			k = userService.getUser(request, response);
		}
		
		
		if( k == 200 )
		{
			//登陆成功，将他存入map
			visitor.setVisitorMap(userName, request.getRemoteAddr());//将已登陆用户的信息存进Map
			log.info( request.getRemoteAddr()+" 账户名为"+userName+"登陆系统成功  ##");
		}
		else if( Math.abs(k) >= 400 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
			log.info( request.getRemoteAddr()+" 账户名为"+userName+"登陆系统失败  ##");
		}
		else {
			uResponseService.userResponse( request,response, k);
			log.info( request.getRemoteAddr()+" 账户名为"+userName+"登陆系统失败  ##");
		}
		
		return ;
		
	}
	
	@RequestMapping("/userExit")
	public void userExit(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		//判断该用户是否已经登陆
		VisitorCheck visitor = new VisitorCheck();
		int flag = visitor.exitSystem(request, response );
		if( flag < 0 )
		{
			return ;
		}		
		
		String userName = request.getHeader("userName");
		log.info( request.getRemoteAddr()+" 账户名为"+userName+"请求退出系统 ");
		//将他从map中移除
		VisitorCheck vCheck = new VisitorCheck();
		vCheck.removeVisitorMap(userName);

		return ;
		
	}
	
	
	@RequestMapping("/index")
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		log.error( request.getRemoteAddr()+" 开始访问系统，开始测试，请求的URL="+request.getRequestURL() );

		return new ModelAndView("index","result","ok");
	}
	
	
	

}
