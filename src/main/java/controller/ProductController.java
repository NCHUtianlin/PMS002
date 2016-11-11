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
import service.basicService.reponseService.ProductResponseService;
import service.basicService.reponseService.PublicResponse;
import service.basicService.reponseService.UserResponseService;
import service.basicService.requestService.MacService;
import service.basicService.requestService.ProductDataService;
import service.basicService.requestService.ProductNoteService;
import service.basicService.requestService.ProductService;


@Controller
public class ProductController {
	
	private static Log log = LogFactory.getLog(ProductController.class.getName());
	
	private ProductResponseService responseService = new ProductResponseService();
	private PublicResponse publicResponse = new PublicResponse();
	private VisitorCheck visitor = new VisitorCheck();

	//------------------ 1. 产品基本信息
	@RequestMapping(value = "/product" , method = RequestMethod.GET)		
	public void getProduct(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "GET", "product");
		ProductService productService = new ProductService();
		int k = productService.getProduct(request, response);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.productResponse(request, response, k);
		}
		log.info( request.getRemoteAddr()+", GET请求的访问结束 ##");
		
	}
	
	@RequestMapping(value="/product/*" , method = RequestMethod.PUT)
	public void updateProduct(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "PUT", "product");
		ProductService productService = new ProductService();
		int k = productService.updateProduct(request);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.productResponse(request, response, k);
		}
		log.info( request.getRemoteAddr()+", PUT请求的访问结束 ##");
		
	}
	
	//---------------------- 2. 测试信息
	@RequestMapping(value = "/testingdata" , method = RequestMethod.GET)		
	public void getTestData(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "GET", "testingdata");
		ProductDataService dataService = new ProductDataService();
		int k = dataService.getTestData(request, response);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.productDataResponse(request, response, k);
		}
		
		log.info(request.getRemoteAddr()+", GET请求的访问结束 ##");
		
	}
	
	//----------------------- 3. 产品类型信息
	@RequestMapping(value = "/productType" , method = RequestMethod.GET)		
	public void getProductType(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "GET", "productType");
		ProductDataService dataService = new ProductDataService();
		int k = dataService.getProductType(request, response);
		
		if( k == 200 )
		{
			
		}
		else if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.productDataResponse(request, response, k);
		}
		log.info( request.getRemoteAddr()+", GET请求的访问结束 ##");
		
	}
	
	@RequestMapping(value = "/productType" , method = RequestMethod.POST)		
	public void addProductType(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "POST", "productType");
		ProductDataService dataService = new ProductDataService();
		int k = dataService.addProductType(request);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.productDataResponse(request, response, k);
		}

		log.info( request.getRemoteAddr()+", POST请求的访问结束 ##");
		
	}

	@RequestMapping(value = "/productType/*" , method = RequestMethod.PUT)		
	public void updateProductType(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "PUT", "productType");
		ProductDataService dataService = new ProductDataService();
		int k = dataService.updateProductType(request);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.productDataResponse(request, response, k);
		}
		
		log.info( request.getRemoteAddr()+", PUT请求的访问结束 ##");
		
	}

	//----------------- 4. 生产完成记录
	@RequestMapping(value = "/productNote" , method = RequestMethod.POST)		
	public void addNote(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "POST", "productNote");
		ProductNoteService noteService = new ProductNoteService();
		int k = noteService.addNote(request);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.noteResponse(request, response, k);
		}
		
		log.info( request.getRemoteAddr()+", POST请求的访问结束 ##");
		
	}
	
	@RequestMapping(value = "/productNote/*" , method = RequestMethod.PUT)		
	public void updateNote(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "PUT", "productNote");
		ProductNoteService noteService = new ProductNoteService();
		int k = noteService.updateNote(request);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.noteResponse(request, response, k);
		}
		
		log.info( request.getRemoteAddr()+", PUT请求的访问结束 ##");
		
	}
	
	@RequestMapping(value = "/productNote" , method = RequestMethod.GET)		
	public void getNote(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "GET", "productNote");
		ProductNoteService noteService = new ProductNoteService();
		int k = noteService.getNote(request, response);
		
		if( k == 200 )
		{
			
		}
		else if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.noteResponse(request, response, k);
		}
		
		log.info( request.getRemoteAddr()+", GET请求的访问结束 ##");
		
	}
	
	//---------------- 5. MAC信息
	@RequestMapping(value = "/mac" , method = RequestMethod.GET)		
	public void getMAC(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "GET", "mac");
		MacService macService = new MacService();
		int k = macService.getMAC(request , response);
		
		if( k == 200 )
		{
			
		}
		else if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.productResponse(request, response, k);
		}
		
		log.info( request.getRemoteAddr()+", GET请求的访问结束 ##");
		
	}
	
	@RequestMapping(value = "/mac/*" , method = RequestMethod.PUT)		
	public void updateMAC(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "PUT", "mac");
		MacService macService = new MacService();
		int k = macService.updateMAC(request);
		
		if( Math.abs(k) >= 400 || k == 200 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.productResponse(request, response, k);
		}
		
		log.info( request.getRemoteAddr()+", PUT请求的访问结束 ##");
		
	}
	
	//------------------ 6. 产品数据统计
	@RequestMapping(value = "/productAnalysis" , method = RequestMethod.GET)		
	public void getProductAnalysis(HttpServletRequest request,HttpServletResponse response)throws IOException
	{
		visitor.visitorCheck(request, response, "GET", "productAnalysis");
		ProductService productService = new ProductService();
		int k = productService.getProductAnalysis(request , response);
		
		if( k == 200 )
		{
			
		}
		else if( Math.abs(k) >= 400 )//公共错误码
		{
			publicResponse.publicResponse(request, response, k);
		}
		else {
			responseService.productResponse(request, response, k);
		}
		
		log.info( request.getRemoteAddr()+", GET请求的访问结束 ##");
		
	}
	
	
}
