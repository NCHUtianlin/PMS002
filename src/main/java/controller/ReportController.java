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
import service.basicService.reponseService.ReportResponseService;
import service.basicService.reponseService.UserResponseService;
import service.basicService.requestService.ReportService;

@Controller
public class ReportController {

	private static Log log = LogFactory.getLog(ReportController.class.getName());
	
	private ReportService reportService = new ReportService();
	private ReportResponseService responseService = new ReportResponseService();
	private PublicResponse publicResponse = new PublicResponse();
	private VisitorCheck visitor = new VisitorCheck();
	
	@RequestMapping(value = "/report" , method = RequestMethod.GET)		
	public void getReport(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "GET", "report");
		int k = reportService.getReport( request , response );
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.reportResponse( request,response, k);
		}
		
		log.info( request.getRemoteAddr()+", GET请求的访问结束 ##");
		
	}
	
	@RequestMapping(value="/report" , method = RequestMethod.POST)
	public void addReport(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "POST", "report");
		int k = reportService.addReport(request);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.reportResponse( request,response, k);
		}
		
		log.info( request.getRemoteAddr()+", POST请求的访问结束 ##");
		
	}
	
	@RequestMapping(value="/report/*" , method = RequestMethod.PUT)
	public void updateReport(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "PUT", "report");
		int k = reportService.updateReport(request);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.reportResponse( request,response, k);
		}
		
		log.info( request.getRemoteAddr()+", PUT请求的访问结束 ##");
		
	}
	
	
}
