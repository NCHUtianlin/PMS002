package service.basicService.requestService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.DataAnalysisDao;
import dao.ProductDao;
import dao.basic.QueryDao;
import domain.Product;
import domain.ProductType;
import net.sf.json.JSONObject;
import service.basicService.reponseService.BasicResponseService;
import service.interfaceService.BasicImplementService;



public class ProductService extends BasicImplementService {
	
	private static Log log = LogFactory.getLog(ProductService.class.getName());
	
	private BasicResponseService responseService = new BasicResponseService();
	
	//@Autowired
	private static ProductDao dao;
	private static DataAnalysisDao sDao;
	
	public static void setsDao(DataAnalysisDao sDao) {
		ProductService.sDao = sDao;
	}
	public void setDao(ProductDao dao) {
		this.dao = dao;
	}

	
	/*****
	 * 获取产品基本信息
	 * * 1 获取账户信息，识别用户身份，拒绝非法用户的请求
		 * 2 接收数据，并校验数据;没有参数，返回错误码;如果有参数parameter，则进入模糊查询；否则进入具体查询
		 * 3 查询数据
		 * 4 返回结果
	 * @param request
	 * @param response
	 * @return
	 */
	public int getProduct( HttpServletRequest request , HttpServletResponse response )
	{
		log.info(request.getRemoteAddr()+" 进入getProduct()方法");
		
		if( request.getContentLength() > 1024 )
		{   
			log.info(request.getRemoteAddr()+" URL请求过长，拒绝请求");
			log.info(request.getRemoteAddr()+" getProduct()方法结束");
			return 414;
		}
		
		String userName = "";
		//String hashedPassword = "";
		//int userAuthority = -1;
		
		//1. 获取账户信息
		int check = checkUser(request);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
				//userAuthority = check;//用户权限
				userName = request.getHeader("userName").toString();
				//hashedPassword = request.getHeader("hashedPassword").toString();
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
				return 500;
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"getProduct()方法结束");
			return check;
		}
		
		Map<String , Object> map = new HashMap<String, Object>();
		Map<String , Object> mapTimes = new HashMap<String, Object>();
		Object parameter = new Object();
		boolean flag = true;//标记是否存在参数parameter
		int pageNow = -1;//当前页数
		int pageSize = -1;//每页的数据数目
		
		//合法用户：管理员和普通用户
		//if( userAuthority == 1 || userAuthority == 0 )
		//{
			//接收json数据
			
			String json ="";
			JSONObject jsonObject = null;
			
			try {
				json = request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 接收到客户端发来的json数据："+json );
				
				jsonObject = JSONObject.fromObject(json);
				if( jsonObject.size() == 0 )
				{
					log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据为空，错误");
					log.info(request.getRemoteAddr()+" getProduct()方法结束");
					return -430;
				}
				
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+" 用户("+userName+")，没有提供json数据，错误");
				log.info(request.getRemoteAddr()+" getProduct()方法结束");
				return -431;
			}
			
			try {
				parameter = jsonObject.get("parameter");
				if( checkEspecialCode(parameter.toString()) )
				{ 
				    log.info(request.getRemoteAddr()+" 参数parameter("+parameter+")含有非法字符");
					log.info(request.getRemoteAddr()+" getProduct()方法结束");
					return -101;
				}else {
					flag = false;
				}
			} catch (Exception e) {
				
			}
			//接收具体参数
			if( flag )
			{
				try {
					String deviceID = jsonObject.getString("deviceID");
					if( checkEspecialCode(deviceID) )
					{
						log.info(request.getRemoteAddr()+"参数deviceID("+deviceID+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -102;
					}
					map.put("deviceID", deviceID);
				} catch (Exception e) {
					
				}
				try {
					String mac = jsonObject.getString("MAC");
					if( checkEspecialCode(mac) )
					{
						log.info(request.getRemoteAddr()+"参数MAC("+mac+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -103;
					}
					map.put("MAC", mac );
				} catch (Exception e) {
					
				}
				try {
					String productTypeName = jsonObject.getString("productTypeName");
					if( checkEspecialCode(productTypeName) ){
						log.info(request.getRemoteAddr()+"参数productTypeName("+productTypeName+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -104;
					}
					map.put("productTypeName", productTypeName );
				} catch (Exception e) {
					
				}
				try {
					String reportPerson = jsonObject.getString("reportPerson");
					if( checkEspecialCode(reportPerson) )
					{
						log.info(request.getRemoteAddr()+"参数reportPerson("+reportPerson+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -105;
					}
					map.put("reportPerson", reportPerson);
				} catch (Exception e) {
					
				}
				try {
					String id = jsonObject.getString("_id");
					if( checkEspecialCode(id) )
					{
						log.info(request.getRemoteAddr()+"参数id("+id+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -106;
					}
					map.put("_id", id);
				} catch (Exception e) {
					
				}
				try {
					Object testingTimes = jsonObject.get("testingTimes");
					JSONObject j = JSONObject.fromObject(testingTimes);
					String start = j.getString("start");
					String end = j.getString("end");
					//校验日期格式
					if( checkDate(start) && checkDate(end) )
					{
						//****######  mapTimes 里面存放的是一个对象，传送到dao层的时候需要解析出来，通过相应的名字来解析
						mapTimes.put("testingTimes", testingTimes);
						//mapTimes.put("start", start);
						//mapTimes.put("end", end);
					}
					else {
						log.info(request.getRemoteAddr()+" 参数(testingTimes="+testingTimes+"),日期格式错误，拒绝请求");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -107;
					}
				} catch (Exception e) {
					
				}
				try {
					Object productEndTimes = jsonObject.get("productEndTimes");
					JSONObject j = JSONObject.fromObject(productEndTimes);
					String start = j.getString("start");
					String end = j.getString("end");
					//校验日期格式
					if( checkDate(start) && checkDate(end) )
					{
						if( mapTimes.size() == 0 )//前面已经有一个时间段了，以第一个时间段为查询参数
						{
							mapTimes.put("productEndTimes", productEndTimes);
							//mapTimes.put("start", start);
							//mapTimes.put("end", end);
						}
						
					}
					else {
						log.info(request.getRemoteAddr()+" 参数(productEndTimes="+productEndTimes+"),日期格式错误，拒绝请求");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -107;
					}
				} catch (Exception e) {
					
				}
				try {
					String productBatch = jsonObject.getString("productBatch");
					if( checkEspecialCode(productBatch) )
					{
						log.info(request.getRemoteAddr()+"参数productBatch("+productBatch+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -108;
					}
					map.put("productBatch", productBatch);
				} catch (Exception e) {
					
				}
				try {
					String producer = jsonObject.getString("producer");
					if( checkEspecialCode(producer) )
						
					{
						log.info(request.getRemoteAddr()+"参数producer("+producer+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -109;
					}
					map.put("producer", producer);
				} catch (Exception e) {
					
				}
				try {
					int productState = jsonObject.getInt("productState");
					if( productState != 0 && productState != 1 && productState != -1 )
					{ 
						log.info(request.getRemoteAddr()+" productState("+productState+")数值不合法，只能是 0或1或-1");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -110;
					}
					map.put("productState", productState);
				} catch (Exception e) {
					
				}
				try {
					String hardwareVersion = jsonObject.getString("hardwareVersion");
					if( checkEspecialCode(hardwareVersion) )
					{
						log.info(request.getRemoteAddr()+"参数hardwareVersion("+hardwareVersion+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -111;
					}
					map.put("hardwareVersion", hardwareVersion);
				} catch (Exception e) {
					
				}
				try {
					String softwareVersion = jsonObject.getString("softwareVersion");
					if( checkEspecialCode(softwareVersion) )
					{
						log.info(request.getRemoteAddr()+"参数softwareVersion("+softwareVersion+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -112;
					}
					map.put("softwareVersion", softwareVersion);
				} catch (Exception e) {
					
				}
				try {
					int testingResult = jsonObject.getInt("testingResult");
					if( testingResult != 0 && testingResult != 1 )
					{
						log.info(request.getRemoteAddr()+" testingResult("+testingResult+")数值不合法，只能是0或1");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -113;
					}
					map.put("testingResult", testingResult);
				} catch (Exception e) {
					
				}
				try {
					String deviceID = jsonObject.getString("deviceID");
					if( checkEspecialCode(deviceID) )
					{
						log.info(request.getRemoteAddr()+"参数deviceID("+deviceID+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProduct()方法结束");
						return -114;
				    }
					map.put("deviceID", deviceID);
				} catch (Exception e) {
					
				}
				try {
					pageNow = jsonObject.getInt("pageNow");
					
				} catch (Exception e) 
				{   
					//log.info("没有分页请求或者分页参数类型错");
					pageNow = 1;
				}
				try {
					pageSize = jsonObject.getInt("pageSize");
					if( pageSize <= 0 )
					{
						pageSize = 30 ;
					}
	
				} catch (Exception e) {
					//log.info("pageSize为null或者类型不符");
					pageSize = 30;
				}
			
			}//------接收具体参数
			
		/*}//----合法用户：管理员和普通用户
		//非法用户
		else {
			log.info(request.getRemoteAddr()+" 未能识别用户身份，拒绝请求。。。");
			log.info(request.getRemoteAddr()+" getProduct()方法结束");
			return -84;
		}
		*/
		
		List<Map<String , Object>> list = null;
		//接收数据完毕，查询数据
		//模糊查询
		if( !flag )
		{
			list = dao.query(productTableName, parameter, 1, null , pageNow , pageSize );
			
		}
		//按时间条件查询
		else if( mapTimes != null && mapTimes.size() > 0 )
		{
			list = dao.getInfoByTimes( map, mapTimes, pageNow, pageSize);
		}
		//按一般条件查询
		else {
			if( map != null && map.size() > 0 )
			{
				list = dao.getInfoByPage(productTableName, map, pageNow, pageSize);
				
			}
			//没有提供参数，拒绝请求
			else {
				log.info(request.getRemoteAddr()+" 没有提供查询参数，拒绝请求");
				log.info(request.getRemoteAddr()+" getProduct()方法结束");
				return -430;
			}
			
		}
		
	///***************************   返回数据   ***********************************
		if( pageNow > 0 )
		{
			int totalSize = dao.getTableCount(productTableName , map);
			responseService.setTotalSize(totalSize);
		}
		
		//整合数据。联表查询
		list = dao.UnionInfo(list);
		if( list != null && list.size() > 0 )
		{
			for( Map<String, Object> m : list )
			{
				//System.out.println(m.get("reportStartDate"));
				//将Date日期格式化
				Object date1 = dateToString( m.get("productEndDate") ) ;
				
				m.remove("productEndDate");

				m.put("productEndDate", date1);
				
			}
			
			responseService.okResponseGetInfo(response, 200, list ,request.getRemoteAddr());
			log.info(request.getRemoteAddr()+" 用户("+userName+")获取产品信息成功");
			log.info(request.getRemoteAddr()+" getProduct()方法结束");
			return 200;
		}
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")请求的资源不存在");
			log.info(request.getRemoteAddr()+" getProduct()方法结束");
			return 404;
		}
		
	}
	
	/*****
	 * 更新产品信息：产品信息是由申报通过后批量生成的，用户只能修改产品的状态》》》 当用户将该产品废弃时，需要回收该产品所占有的mac地址
	 * 1. 获取账户信息，识别用户身份；非法用户，拒绝请求
	 * 2. 获取json数据，校验；没有提供参数，请求无效
	 * @param request
	 * @return
	 */
	public int updateProduct( HttpServletRequest request )
	{
		log.info(request.getRemoteAddr()+" 进入updateProduct()方法");
		
		String id = "";
		try {
			String uri = request.getRequestURI();
			String[] s = uri.split("/:");
			id = s[1];
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+" 未获取到URL参数id");
			log.info(request.getRemoteAddr()+" updateProduct()方法结束");
			return 415;
		}
		
		String userName = "";
		//String hashedPassword = "";
		//int userAuthority = -1;//标记用户身份
		boolean macBack = false;//如果修改productState为3（废弃），则回收mac
		int check = checkUser(request);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
				//userAuthority = check;//用户权限
				userName = request.getHeader("userName").toString();
				//hashedPassword = request.getHeader("hashedPassword").toString();
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
				return 500;
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"updateProduct()方法结束");
			return check;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObject = new JSONObject();
		//合法用户
	//	if( userAuthority == 0 || userAuthority == 1 )
		//{
			//接收json数据
			try {
				String json = request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据: "+json );
				
				jsonObject = JSONObject.fromObject(json);
				if( jsonObject.size() == 0 )
				{
					 log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据为空，错误");
					 log.info(request.getRemoteAddr()+" updateProduct()方法结束");
					 return -430;
				}
				
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+" 用户("+userName+")没有提供json数据，拒绝");
				log.info(request.getRemoteAddr()+" updateProduct()方法结束");
				return -431;
			}
			
			//接收参数
			try {
				int productState = jsonObject.getInt("productState");
				if( productState != 0 && productState != 1 && productState != 2 && productState != 3 && productState != -1 )
				{
					log.info(request.getRemoteAddr()+" 参数productState("+productState+")数值非法，只能是0或1或2或3");
					log.info(request.getRemoteAddr()+" updateProduct()方法结束");
					return -110;
				}
				//废弃该产品
				if( productState == 3 )
				{
					macBack = true;
				}
				map.put("productState", productState);
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+" 参数productState类型有误，发生异常：",e);
				log.info(request.getRemoteAddr()+" updateProduct()方法结束");
				return -110;
			}
			
			//通过id更新产品
			int k = dao.updateById( "_id", id, map );
			if( k > 0 )
			{
				if( macBack )
				{
					
				}
				
				log.info(request.getRemoteAddr()+" 用户("+userName+")修改产品信息成功");
				log.info(request.getRemoteAddr()+" updateProduct()方法结束");
				return 200;
			}else if( k == -2 ){
				log.info(request.getRemoteAddr()+" 用户("+userName+")修改产品信息失败,该产品已经被废弃，无法修改该产品数据");
				log.info(request.getRemoteAddr()+" updateProduct()方法结束");
				return -119;
				
			}
			else {
				log.info(request.getRemoteAddr()+" 用户("+userName+")修改产品信息失败");
				log.info(request.getRemoteAddr()+" updateProduct()方法结束");
				return -115;
			}

		/*}
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")非法用户，拒绝请求");
			log.info(request.getRemoteAddr()+" updateProduct()方法结束");
			return -94;
			
		}*/
		
	}
	
	
	
	/****
	 * 获取产品统计信息
	 * 1. 获取账户信息，判断是否为合法用户，拒绝非法用户请求
	 * 2. 获取json数据
	 * 3. 请求参数parameter ，分类查询（ 1. 批次 、2.  产品类型 、 3. 生产商、 4. 时间段 ）
	 * 4. 返回结果
	 * @param request
	 * @return
	 */
	public int getProductAnalysis( HttpServletRequest request , HttpServletResponse response )
	{
		log.info(request.getRemoteAddr()+" 进入getProductAnalysis()方法");
		
		int parameter = 0;
		String userName = "";
		//String hashedPassword = "";
		String start = "";
		String end = "";
		JSONObject jsonObject = new JSONObject();
		List<Map<String  , Object>> list = null;
		//int userAuthority = -1;
		
		int check = checkUser(request);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
			//	userAuthority = check;//用户权限
				userName = request.getHeader("userName").toString();
			//	hashedPassword = request.getHeader("hashedPassword").toString();
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
				return 500;
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"getProductAnalysis()方法结束");
			return check;
		}
		
		//合法用户
		//if( userAuthority == 0 || userAuthority == 1 )
		//{
			
			//接收json数据
			try {
				String json = request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 用户"+userName+"提供的json数据："+json );
				
				jsonObject = JSONObject.fromObject(json);
				
				if( jsonObject.size() == 0 )
				{
					log.error(request.getRemoteAddr()+" 用户"+userName+"提供的JSON数据为空，拒绝请求");
					log.info(request.getRemoteAddr()+" getProductAnalysis()方法结束");
					return -430;
				}
				
			} catch (Exception e) {
				log.error(request.getRemoteAddr()+" 用户"+userName+"没有提供的JSON数据，拒绝请求");
				log.info(request.getRemoteAddr()+" getProductAnalysis()方法结束");
				return -431;
			}			
			//查询选择参数（ 1. 批次 、2.  产品类型 、 3. 生产商、 4. 时间段 ）
			try {
				
				
				parameter = jsonObject.getInt("parameter");
				if( parameter <= 0 || parameter >= 5)
				{
					log.info(request.getRemoteAddr()+"参数parameter("+parameter+")数值非法（ 1. 批次 、2.  产品类型 、 3. 生产商、 4. 时间段 ）");
					log.info(request.getRemoteAddr()+" getProductAnalysis()方法结束");
					return -117;
				}
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+"未能提供查询参数parameter或者参数非法，拒绝请求");
				log.info(request.getRemoteAddr()+" getProductAnalysis()方法结束");
				return -106;
			}
			//时间段参数
			try {
				start = jsonObject.getString("start");
				end = jsonObject.getString("end");
				if( checkDate(start) || checkDate(end) )
				{
					log.info(request.getRemoteAddr()+"参数start("+start+")和end("+end+")为时间参数，必须符合日期格式：yyyy-MM-dd");
					log.info(request.getRemoteAddr()+" getProductAnalysis()方法结束");
					return -107;
				}
			} catch (Exception e) {
				
			}
			
		/*}
		//非法用户，拒绝
		else {
			log.info(request.getRemoteAddr()+" 用户"+userName+"非法用户，拒绝请求");
			log.info(request.getRemoteAddr()+" getProductAnalysis()方法结束");
			return -144;
		}*/
		
		
		
		//分类查询（ 1. 批次 、2.  产品类型 、 3. 生产商、 4. 时间段 ）
		// 1. 按批次统计
		if( parameter == 1 )
		{
			list = sDao.productBatch();
		}
		// 2. 按产品类型统计
		else if( parameter == 2 )
		{
			list = sDao.productType();		
		}
		// 3. 按生产商统计
		else if( parameter == 3 )
		{
			list = sDao.producer();
		}
		// 4. 按时间段统计
		else if( parameter == 4 )
		{
			list = sDao.time(start, end);
		}
		else {
			log.info(request.getRemoteAddr()+" 用户"+userName+"未提供有效参数，无法理解请求");
			log.info(request.getRemoteAddr()+" getProductAnalysis()方法结束");
			return -117;
		}
		
		if( list == null || list.size() == 0 )
		{
			log.info(request.getRemoteAddr()+"用户"+userName+" 请求的资源没有找到，获取统计数据失败");
			//responseService.okResponseGetInfo(response, 404, list ,request.getRemoteAddr());
			
			log.info(request.getRemoteAddr()+" getProductAnalysis()方法结束");
			return -118;
		}
		else {
			/*for( Map<String, Object> m : list )
			{
				//将Date日期格式化
				Object date1 = dateToString( m.get("date") ) ;
				
			}*/
			log.info(request.getRemoteAddr()+"用户"+userName+" 获取统计数据成功");
			responseService.okResponseGetInfo(response, 200, list ,request.getRemoteAddr());
			
			log.info(request.getRemoteAddr()+" getProductAnalysis()方法结束");
			return 200;
		}
	}

	
}
