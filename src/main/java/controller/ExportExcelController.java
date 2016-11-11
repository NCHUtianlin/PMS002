package controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import service.basicService.internal.VisitorCheck;
import service.basicService.reponseService.ProductResponseService;
import service.basicService.reponseService.PublicResponse;
import service.basicService.reponseService.UserResponseService;
import service.basicService.requestService.ExportExcelService;


@Controller
public class ExportExcelController {

	private static Log log = LogFactory.getLog(ExportExcelController.class.getName());
	private PublicResponse publicResponse = new PublicResponse();
	private ProductResponseService responseService = new ProductResponseService();
	
	@RequestMapping("export")
    private ModelAndView Export(HttpServletRequest request,HttpServletResponse response) throws Exception{
        
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
		
		int k =0 ;
		//判断该用户是否已经登陆( 0 登陆 了但是在不同的IP地址上    1 已经登陆在同一台主机上   2 没有登陆 )
		VisitorCheck visitor = new VisitorCheck();
		int flag = visitor.visitorCheck(request, response, request.getMethod(), "export");
		
		if( flag < 0 )
		{
			return null;
		}
		else if( flag > 0 ){
			ExportExcelService excelService = new ExportExcelService();
			k = excelService.ExportService(request, response);
			if( Math.abs(k) >= 400 || k == 200 )//公共错误码
			{
				publicResponse.publicResponse(request, response, k);
			}
			else {
				responseService.exportResponse(request, response, k);
			}
			
			log.info( request.getRemoteAddr()+", 导出数据结束 ## ");
			return null;
		}
		//还未登陆，请先登陆
		else {
			return new ModelAndView("loginPMS","result","ok");	//指定转向登陆页面
		}
		
		
		
    }
        
    
	
}
