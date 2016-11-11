package service.basicService.requestService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import service.interfaceService.BasicImplementService;
import controller.ExportExcelController;
import dao.DataAnalysisDao;
import dao.MacDao;
import dao.ProductDao;
import dao.UserDao;
import excel.ExportExcel;


public class ExportExcelService extends BasicImplementService {
	
	private static Log log = LogFactory.getLog(ExportExcelController.class.getName());
	
	private static UserDao dao;
	public void setDao(UserDao dao) {
		this.dao = dao;
	}
	
	public int ExportService(HttpServletRequest request , HttpServletResponse response)
	{
		log.info(request.getRemoteAddr()+"ExportService()方法开始");
		
		String userName = "";
		String hashedPassword = "";
		JSONObject jsonObject = null;
		String type = "";
		String productBatch = "";
		Map<String , Object> map = new HashMap<String, Object>();
		int userAuthorty = -1;
		//识别用户身份
		try {
			userName = request.getParameter("userName");
			hashedPassword = request.getParameter("hashedPassword");
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+"没有账户信息或者账户信息不全，拒绝请求");
			log.info(request.getRemoteAddr()+"ExportService()方法结束");
			return -421;//返回错误码
		}
		
		int check = checkUser2(request,userName,hashedPassword);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
				userAuthorty = check;
				
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
			}
					
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"ExportService()方法结束");
			return check;//返回错误码
		}		
		
		try {
			type = request.getParameter("type");
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+"没有提供参数type，拒绝请求");
			log.info(request.getRemoteAddr()+"ExportService()方法结束");
			return -259;//返回错误码
		}
		try {
			productBatch = request.getParameter("productBatch");
			if( checkEspecialCode(productBatch) )
			{
				log.info(request.getRemoteAddr()+"参数productBatch含有非法字符，拒绝请求");
				log.info(request.getRemoteAddr()+"ExportService()方法结束");
				return -254;//返回错误码
			}
			map.put("productBatch", productBatch);
		} catch (Exception e) {
			
		}
		
		List<Map<String , Object>> list = new ArrayList<Map<String,Object>>();
		String filename = "Unkonw";
		try {
			filename = new String("生产管理系统数据表.xls".getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error( "" , e );
			log.info(request.getRemoteAddr()+"ExportService()方法结束");
			return 500;
		}
		
		String fileName = "exportPMS.xls";
        
        if( type == null )
        {
        	log.info(request.getRemoteAddr()+"参数type("+type+")为null,拒绝请求");
        	log.info(request.getRemoteAddr()+"ExportService()方法结束");
        	return -251;
        }
        else if( "Users".equals(type) )
        {
        	if( userAuthorty == 1 )
        	{
	        	try {
	        		list = dao.getInfo("userTable", null);
	            	fileName = "pms-user.xls";
				} catch (Exception e) {
					log.error("处理数据导出请求过程中，获取用户信息时发生异常" , e );
					log.info(request.getRemoteAddr()+"ExportService()方法结束");
					return 500;
				}
        	}
        	else {
        		log.info(request.getRemoteAddr()+" 用户"+userName+"为普通用户，没有权限导出用户信息数据,拒绝请求");
            	log.info(request.getRemoteAddr()+"ExportService()方法结束");
            	return -428;
			}
        	
        }
        else if( "Products".equals(type)){
        	try {
        		if( map.size() == 0 )
        		{
        			log.info(request.getRemoteAddr()+"没有提供参数productBatch，拒绝请求");
        			log.info(request.getRemoteAddr()+"ExportService()方法结束");
                	return -257;
        		}
        		list = dao.getInfo("productTable", map);
        		if( list != null && list.size() > 0 )
        		{
        			ProductDao pdDao = new ProductDao();
                	list = pdDao.UnionInfo(list);
                	fileName = "pms-product.xls";
        		}
        		else{
        			log.info(request.getRemoteAddr()+"没有数据，暂时未能提供服务");
        			log.info(request.getRemoteAddr()+"ExportService()方法结束");
                	return -252;
        		}
            	
			} catch (Exception e) {
				log.error("处理数据导出请求过程中，获取产品信息时发生异常" , e );
				log.info(request.getRemoteAddr()+"ExportService()方法结束");
				return 500;
			}
        	
		}
        else if( "MACs".equals(type)){
        	if( productBatch != null && productBatch.length() > 0 )
        	{
        		try {
        			MacDao dao = new MacDao();
            		list = dao.getByBatch(productBatch , -1, -1);
            		
				} catch (Exception e) {
					log.error("处理数据导出请求过程中，获取MAC信息时发生异常" , e );
					log.info(request.getRemoteAddr()+"ExportService()方法结束");
					return 500;
				}
        		
        		if( list != null && list.size() > 0 )
        		{
        			filename = "pms-MAC.xls";
        			/*for( Map<String , Object> m : list )
        			{
        				for( String key : m.keySet() )
        				{
        					if( "key".equals("macUseState") )
        					{
        						m.remove(key);
        					}
        				}
        			}*/
        		}
        		else {
        			log.info(request.getRemoteAddr()+"没有数据，暂时未能提供服务");
        			log.info(request.getRemoteAddr()+"ExportService()方法结束");
                	return -252;
				}
        	}
        	else {
        		log.info(request.getRemoteAddr()+"没有提供参数productBatch，拒绝请求");
    			log.info(request.getRemoteAddr()+"ExportService()方法结束");
            	return -257;
			}
        	
		}
        else if( "AnalysisBatch".equals(type)){
        	try {
        		DataAnalysisDao sDao = new DataAnalysisDao();
            	list = sDao.productBatch();
            	
            	if( list != null && list.size() > 0 )
            	{
            		fileName = "pms-analysisBatch.xls";
            	}
            	else{
        			log.info(request.getRemoteAddr()+"没有数据，暂时未能提供服务");
        			log.info(request.getRemoteAddr()+"ExportService()方法结束");
                	return -252;
            	}
            	
			} catch (Exception e) {
				log.error("处理数据导出请求过程中，获取用户信息时发生异常" , e );
				log.info(request.getRemoteAddr()+"ExportService()方法结束");
				return 500;
			}
        	
		}
        else if( "AnalysisProducer".equals(type)){
        	try {
        		DataAnalysisDao sDao = new DataAnalysisDao();
            	list = sDao.producer();
            	if( list != null && list.size() > 0 )
            	{
            		fileName = "pms-ananlysisProducer.xls";
            	}else {
        			log.info(request.getRemoteAddr()+"没有数据，暂时未能提供服务");
        			log.info(request.getRemoteAddr()+"ExportService()方法结束");
                	return -252;
				}

			} catch (Exception e) {
				log.error("处理数据导出请求过程中，获取用户信息时发生异常" , e );
				log.info(request.getRemoteAddr()+"ExportService()方法结束");
				return 500;
			}
        	
		}
        else if( "AnalysisType".equals(type)){
        	try {
        		DataAnalysisDao sDao = new DataAnalysisDao();
            	list = sDao.productType();
            	if( list != null && list.size() > 0 )
            	{
            		fileName = "pms-analysisType.xls";
            	}else {
        			log.info(request.getRemoteAddr()+"没有数据，暂时未能提供服务");
        			log.info(request.getRemoteAddr()+"ExportService()方法结束");
                	return -252;
				}
            	
			} catch (Exception e) {
				log.error("处理数据导出请求过程中，获取用户信息时发生异常" , e );
				log.info(request.getRemoteAddr()+"ExportService()方法结束");
				return 500;
			}
        	
		}
        else {
			log.info("请求错误，无法提供服务");
			log.info(request.getRemoteAddr()+"ExportService()方法结束");
			return -251;
		}
        
        
        response.setContentType("application/vnd.ms-excel");
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		response.setHeader("Content-Disposition", "attachment;filename="+filename );

		String docsPath = "F:";
        
		ExportExcel exportExcel = new ExportExcel();
		int k = exportExcel.test( docsPath , fileName , list , type);
		
		if( k < 0 )
		{
			log.info(request.getRemoteAddr()+" 导出数据失败");
			return -255;
		}
		
		String filePath = docsPath + System.getProperties().getProperty("file.separator") + fileName;
		int j = exportExcel.download(filePath, response);
		if( j < 0 )
		{
			log.info(request.getRemoteAddr()+" 数据发送失败");
			return -256;
		}
		
		log.info(request.getRemoteAddr()+" 导出数据成功");
		return 200;
	}

}
