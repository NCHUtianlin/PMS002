package service.basicService.requestService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.ProductDataDao;
import domain.ProductNote;
import domain.ProductType;
import domain.User;
import net.sf.json.JSONObject;
import service.basicService.internal.OperProductData;
import service.basicService.reponseService.BasicResponseService;
import service.interfaceService.BasicImplementService;

@Service
public class ProductDataService extends BasicImplementService {

	private static Log log = LogFactory.getLog(ProductDataService.class.getName());
	
	private BasicResponseService responseService = new BasicResponseService();
	@Autowired
	private static ProductDataDao dao;
	public void setDao(ProductDataDao dao) {
		this.dao = dao;
	}
	
	/***获取产品类型信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public int getProductType( HttpServletRequest request , HttpServletResponse response )
	{
		/**
		 *  1  接收账户信息，判断用户身份
		 *  2 非法用户拒绝请求
		 *  3 接收请求参数，校验参数格式: 查询标记queryTab=0全部查询；queryTab=1查询可用的
		 *  4 查询数据，返回结果
		 * ***/
		log.info(request.getRemoteAddr()+" 进入getProductType()方法");
		
		//账户信息
		String userName = "";
		//String hashedPassword = "";
		int pageNow = -1;
		int pageSize = 30;
		//int userAuthority = -1;
		
		if( request.getContentLength() > 1024 )
		{
			log.info(request.getRemoteAddr()+" URL长度大于1024，拒绝请求");
			log.info(request.getRemoteAddr()+" getProductType()方法结束");
			return 414;
		}
		// 1. 接收账户信息
		int check = checkUser(request);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
		//		userAuthority = check;//用户权限
				userName = request.getHeader("userName").toString();
		//		hashedPassword = request.getHeader("hashedPassword").toString();
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
				return 500;
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"getProductType()方法结束");
			return check;
		}
		
		//合法用户
		//if( userAuthority == 0 || userAuthority == 1 )
		//{
			String json = "";
			JSONObject jsonObject = null;
			boolean jsonFlag = true;
			int queryTab = -1;//查询标记 queryTab=0全部查询；queryTab=1查询可用的
			Map<String , Object> map = new HashMap<String, Object>();
			
			//3 接收请求参数
			try {
				json = request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据jsondata="+json );
				
				jsonObject = JSONObject.fromObject(json);
				//System.out.println("json:  "+jsonObject);
				if( jsonObject.size() == 0 )
					jsonFlag = false;
				
			} catch (Exception e) {
				//System.out.println("jsondata没有接收到");
				jsonFlag = false;
			}
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			//接收json数据
			if( jsonFlag )
			{
				try {
					queryTab = jsonObject.getInt("queryTab");
					if( queryTab != 0 && queryTab != 1 )
					{
						log.info(request.getRemoteAddr()+" 没有提供查询标记queryTab，无法明确查询目的，拒绝请求");
						return -151;
					}
				} catch (Exception e) {
					
				}
				
				try {
					String deviceID = jsonObject.getString("deviceID");
					if( checkEspecialCode(deviceID) )
					{
						log.info(request.getRemoteAddr()+" 参数deviceID("+deviceID+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProductType()方法结束");
						return -152;
					}
					
					map.put("deviceID", deviceID);
					
				} catch (Exception e) {
					
				}
				try {
					String productTypeName = jsonObject.getString("productTypeName");
					if( productTypeName.length() > 0 )
					{
						if( checkEspecialCode(productTypeName) )
						{
							log.info(request.getRemoteAddr()+" 参数productTypeName("+productTypeName+")含有非法字符");
							log.info(request.getRemoteAddr()+" getProductType()方法结束");
							return -153;
						}
						 
						map.put("productTypeName", productTypeName);
					}
				} catch (Exception e) {
					
				}
				try {
					String hardwareVersion = jsonObject.getString("hardwareVersion");
					if( checkEspecialCode(hardwareVersion) )
					{
						log.info(request.getRemoteAddr()+" 参数hardwareVersion("+hardwareVersion+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProductType()方法结束");
						return -154;
					}
					 
					map.put("hardwareVersion", hardwareVersion);
				} catch (Exception e) {
					
				}
				try {
					String softwareVersion = jsonObject.getString("softwareVersion");
					if( checkEspecialCode(softwareVersion) )
					{
						log.info(request.getRemoteAddr()+" 参数softwareVersion("+softwareVersion+")含有非法字符");
						log.info(request.getRemoteAddr()+" getProductType()方法结束");
						return -155;
					}
					
					map.put("softwareVersion", softwareVersion);
				} catch (Exception e) {
					
				}
				try {
					int macNumber = jsonObject.getInt("macNumber");
					if( macNumber > 0 && macNumber < 100 )
						map.put("macNumber", macNumber);
					else 
					{
						log.info(request.getRemoteAddr()+" 参数macNumber("+macNumber+")数值非法，数值范围 0 < macNumber < 100");
						log.info(request.getRemoteAddr()+" getProductType()方法结束");
						return -156;
					}
				} catch (Exception e) {
					
				}
				try {
					int productTypeState = jsonObject.getInt("productTypeState");
					if( productTypeState == 0 || productTypeState == 1 )
						map.put("productTypeState", productTypeState);
					else 
					{
						log.info(request.getRemoteAddr()+" 参数productTypeState("+productTypeState+")数值非法，只能是0或1");
						log.info(request.getRemoteAddr()+" getProductType()方法结束");
						return -157;
					}
				} catch (Exception e) {
					
				}
				try {
					pageNow = jsonObject.getInt("pageNow");
					if( pageNow <= 0 )
					{
						log.info(request.getRemoteAddr()+" 参数pageNow("+pageNow+")数值非法，不能小于等于0");
						log.info(request.getRemoteAddr()+" getProductType()方法结束");
						return -433;
					}
						
				} catch (Exception e) {
					
				}
				try {
					pageSize = jsonObject.getInt("pageSize");
					if( pageSize <= 0 )
					{
						log.info(request.getRemoteAddr()+" 参数pageSize("+pageSize+")数值非法，不能小于等于0");
						log.info(request.getRemoteAddr()+" getProductType()方法结束");
						return -434;
					}
				} catch (Exception e) {
					pageSize = 30;//给出默认值
				}

				//--接收json数据完毕
				//查询( 有参数，则按条件查询，没有参数，则返回全部 》》getInfo 需要做出判断)
				if( queryTab == 1 )
				{
					map.put("productTypeState", 1);//产品类型是可用的
				}
				list = dao.getInfoByPage(productTypeTableName, map, pageNow, pageSize);
				
			}
			//没有json数据，全部查询
			else {
				list = dao.getInfo(productTypeTableName, null);
			}
			
	///************************  返回数据    ************************************
			if( pageNow > 0 )
			{
				int totalSize = dao.getTableCount(productTypeTableName , map);
				responseService.setTotalSize(totalSize);
			}
			
			//返回数据
			if( list.size() > 0 )
			{
				log.info(request.getRemoteAddr()+" 用户("+userName+")获取产品类型信息成功");
				responseService.okResponseGetInfo(response, 200, list ,request.getRemoteAddr());
				
				log.info(request.getRemoteAddr()+" getProductType()方法结束");
				return 200;
			}
			else {
				log.info(request.getRemoteAddr()+" 用户("+userName+")请求的资源没有找到，获取信息失败");
				log.info(request.getRemoteAddr()+" getProductType()方法结束");
				return 404;
			}
			
		/*}//------合法用户，接收数据，查询数据，返回结果
		//非法用户，拒绝请求
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")为非法用户，拒绝请求");
			log.info(request.getRemoteAddr()+" getProductType()方法结束");
			return -117;
		}*/
		
	}
	
	/***添加产品类型
	 *  1. 接收账户信息，获取用户身份
	 *  2. 非法用户：拒绝
	 *  3. 合法用户： 
	 *  		3.1. 接收json数据，如果缺少必要数据，返回错误码
	 *  		3.2. //<<检测数据库中是否存在同名的产品类型，如果重名，请重新命名；>>重名也插入作为新的一条数据
	 *  		3.3. 插入数据库
	 * @param request
	 * @return
	 */
	public int addProductType( HttpServletRequest request )
	{
		log.info(request.getRemoteAddr()+" 进入addProductType()方法");
		
		//int userAuthority = -1;//标记用户身份  0和1 合法用户，其他为非法
		//账户信息
		String userName = "";
		//String hashedPassword = "";
		//产品类型信息
		String id;
		String productTypeName;
		String hardwareVersion;
		String softwareVersion;
		int macNumber;
		
		// 1. 接收账户信息
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
			log.info(request.getRemoteAddr()+"addProductType()方法结束");
			return check;
		}
		
		//合法用户，则接收json数据，校验，插入
		//if( userAuthority == 0 || userAuthority == 1 )
		//{
			//接收参数
			try {
				request.setCharacterEncoding("utf-8");
				String json = request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据jsondata="+json );
				
				JSONObject jsonObject = JSONObject.fromObject(json);
				log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据jsondata="+jsonObject);
				
				productTypeName = jsonObject.getString("productTypeName");
				hardwareVersion = jsonObject.getString("hardwareVersion");
				softwareVersion = jsonObject.getString("softwareVersion");
				macNumber = jsonObject.getInt("macNumber");
				
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据缺少参数或者参数类型不符，拒绝请求。。");
				log.info(request.getRemoteAddr()+" addProductType()方法结束");
				return -158;
			}
			//校验参数
			//Object[] objects = {productTypeName,hardwareVersion,softwareVersion};
			if( macNumber < 0 || macNumber > 100 )
			{
				log.info(request.getRemoteAddr()+" 参数macNumber("+macNumber+")数值非法，取值范围必须在（0,100）");
				log.info(request.getRemoteAddr()+" addProductType()方法结束");
				return -156;
			}
			else if( checkEspecialCode(productTypeName) || productTypeName == "" )
			{
				log.info(request.getRemoteAddr()+" 参数productTypeName("+productTypeName+")含有非法字符");
				log.info(request.getRemoteAddr()+" addProductType()方法结束");
				return -153;
			} else if( productTypeName.length() > 20 )
			{
				log.info(request.getRemoteAddr()+" 参数productTypeName("+productTypeName+")长度超过20个字符，错误");
				log.info(request.getRemoteAddr()+" addProductType()方法结束");
				return -165;
			}
			//检测硬件版本信息是否含有非法字符或者中文字符
			else if( checkEspecialCode(hardwareVersion) || checkChineseCode(hardwareVersion) || hardwareVersion == "" )
			{
				log.info(request.getRemoteAddr()+" 参数hardwareVersion("+hardwareVersion+")含有非法字符");
				log.info(request.getRemoteAddr()+" addProductType()方法结束");
				return -154;
			}
			//检测软件版本信息是否含有非法字符或者中文字符
			else if( checkEspecialCode(softwareVersion) || checkChineseCode(softwareVersion) || softwareVersion == "" )
			{
				log.info(request.getRemoteAddr()+" 参数softwareVersion("+softwareVersion+")含有非法字符");
				log.info(request.getRemoteAddr()+" addProductType()方法结束");
				return -155;
			}
			
			//参数合法
			Map<String , Object> map = new HashMap<String, Object>();
			map.put("productTypeName", productTypeName);
			map.put("hardwareVersion", hardwareVersion);
			map.put("softwareVersion", softwareVersion);
			map.put("macNumber", macNumber);
			map.put("productTypeState", 1);// 1 默认为可用
			
			int k = dao.insert(productTypeTableName, map);
			if( k > 0 )
			{
				log.info(request.getRemoteAddr()+" 用户("+userName+")添加产品类型成功");
				log.info(request.getRemoteAddr()+" addProductType()方法结束");
				return 200;
			}
			else {
				log.info(request.getRemoteAddr()+" 服务器故障，用户("+userName+")添加产品类型失败");
				log.info(request.getRemoteAddr()+" addProductType()方法结束");
				return -159;
			}
			
		/*}
		//非法用户，拒绝请求
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")为非法用户，拒绝请求");
			log.info(request.getRemoteAddr()+" addProductType()方法结束");
			return -104;
		}*/
		
	}
	/***修改产品类型
	 *  1. 通过url获取id 
	 *  2. 接收账户信息，识别用户身份
	 *  3. 非法用户：拒绝
	 *  4. 合法用户： 接收json数据，校验 ；若是没有json数据，则返回200空消息体
	 *  5. 修改数据
	 * @param request
	 * @return
	 */
	public int updateProductType( HttpServletRequest request )
	{
		log.info(request.getRemoteAddr()+" 进入updateProductType()方法");
		
		String id = "";
		//获取要修改的产品类型的id
		try {
			String uri = request.getRequestURI();
			String[] s = uri.split("/:");
			id = s[1];
			//System.out.println("获取到用户要修改的产品类型ID :"+id);
			if( checkEspecialCode(id) )
			{
				log.info(request.getRemoteAddr()+" 获取到URL参数id中含有非法字符，拒绝请求");
				log.info(request.getRemoteAddr()+" updateProductType()方法结束");
				return 415;
			}
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+" 获取URL参数id时发生异常：" , e); 
			log.info(request.getRemoteAddr()+" updateProductType()方法结束");
			return 415;
		}
		
		//账户信息
		String userName = "";
		//String hashedPassword;
		//int userAuthority = -1;//标记用户身份
		//获取账户信息
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
			log.info(request.getRemoteAddr()+"updateProductType()方法结束");
			return check;
		}
		
		//合法用户
		//if( userAuthority == 0 || userAuthority == 1 )
		//{
			//boolean jsonFlag = true;
			JSONObject jsonObject = new JSONObject();
			
			//接收json数据
			try {
				String json = request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据jsondata="+json);
				
				jsonObject = JSONObject.fromObject(json);
				if( jsonObject.size() == 0 )
				{
					log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据为空，数据库不做任何操作，返回200");
					log.info(request.getRemoteAddr()+" updateProductType()方法结束");
					return 200;
				}
				
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+" 用户("+userName+")没有提供json数据jsondata，数据库不做任何操作，返回200");
				log.info(request.getRemoteAddr()+" updateProductType()方法结束");
				return 200;
			}
			
			Map<String , Object> map = new HashMap<String, Object>();
			//有json数据，接收
			try {
				String productTypeName = jsonObject.getString("productTypeName");
				if( checkEspecialCode(productTypeName) || productTypeName == "" )
				{
					log.info(request.getRemoteAddr()+" 参数productTypeName("+productTypeName+")含有非法字符");
					log.info(request.getRemoteAddr()+" updateProductType()方法结束");
					return -153;
				}
				map.put("productTypeName", productTypeName);
			} catch (Exception e) {
				
			}
			try {
				int productTypeState = jsonObject.getInt("productTypeState");
				if( productTypeState != 0 && productTypeState != 1 )
				{
					log.info(request.getRemoteAddr()+" 参数productTypeState("+productTypeState+") 数值不符，只能为0或1");
					log.info(request.getRemoteAddr()+" updateProductType()方法结束");
					return -157;
				}
				map.put("productTypeState", productTypeState);
			} catch (Exception e) {
				
			}
			try {
				String hardwareVersion = jsonObject.getString("hardwareVersion");
				if( checkEspecialCode(hardwareVersion) || hardwareVersion == "" )
				{
					log.info(request.getRemoteAddr()+" 参数hardwareVersion("+hardwareVersion+")含有非法字符");
					log.info(request.getRemoteAddr()+" updateProductType()方法结束");
					return -154;
				}
				map.put("hardwareVersion", hardwareVersion);
			} catch (Exception e) {
				
			}
			try {
				String softwareVersion = jsonObject.getString("softwareVersion");
				if( checkEspecialCode(softwareVersion) || softwareVersion == "" )
				{
					log.info(request.getRemoteAddr()+" 参数softwareVersion("+softwareVersion+")含有非法字符");
					log.info(request.getRemoteAddr()+" updateProductType()方法结束");
					return -155;
				}
				map.put("softwareVersion", softwareVersion);
			} catch (Exception e) {
				
			}
			try {
				int macNumber = jsonObject.getInt("macNumber");
				if( macNumber < 0 || macNumber > 100 )
				{
					log.info(request.getRemoteAddr()+" 参数macNumber("+macNumber+")数值范围不符取值范围应该在（0,100）");
					log.info(request.getRemoteAddr()+" updateProductType()方法结束");
					return -156;
				}
				map.put("macNumber", macNumber);
			} catch (Exception e) {
				
			}
			//------接收数据完毕
			//更新数据
			if( map.size() > 0 )
			{
				dao.updateById(ProductType.class, id, map);
				log.info(request.getRemoteAddr()+" 用户("+userName+")更新产品类型信息成功");
				log.info(request.getRemoteAddr()+" updateProductType()方法结束");
				return 200;
				
			}
			else{
				log.info(request.getRemoteAddr()+" 用户("+userName+")没有接收如何参数，不做数据库操作，返回200");
				log.info(request.getRemoteAddr()+" updateProductType()方法结束");
				return 200;
			}
			
			
		/*}//------合法用户
		//非法用户
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")为非法用户，拒绝请求");
			log.info(request.getRemoteAddr()+" updateProductType()方法结束");
			return -113;
		}*/
		
	}
	
	/***获取测试数据
	 * 1. 接收账户信息，获取用户身份
	 * 2. 非法用户：拒绝
	 * 3. 合法用户: 接收json数据，校验；必须至少有一个参数，按条件查询数据；不能一次性返回数据库的所有数据
	 * 4. 查询数据，返回结果
	 * @param request
	 * @param response
	 * @return
	 */
	public int getTestData( HttpServletRequest request , HttpServletResponse response )
	{
		log.info(request.getRemoteAddr()+" 进入getTestData()方法");
		
		List<Map<String , Object>> list = new ArrayList<Map<String,Object>>();
		//int userAuthority = -1;//标记用户身份
		int pageNow = -1;
		int pageSize = 30;
		//账户信息
		String userName = "";
		//String hashedPassword = "";
		
		//接收账户信息
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
			log.info(request.getRemoteAddr()+"getTestData()方法结束");
			return check;
		}
		
		//合法用户
		//if( userAuthority == 0 || userAuthority ==1 )
		//{
			Map<String , Object> map = new HashMap<String, Object>();
			JSONObject jsonObject = new JSONObject();
			String productBatch = "";
			Object parameter = null;//模糊查询参数
			boolean flag = true;//如果有参数parameter，则进行模糊查询，值为false，否则进入具体查询
			
			//接收json数据
			try {
				String json = request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据: "+json );
				
				jsonObject = JSONObject.fromObject(json);
				if( jsonObject.size() == 0 )
				{
					log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据为空");
					log.info(request.getRemoteAddr()+" getTestData()方法结束");
					return -430;
				}
				
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+" 用户("+userName+")没有提供json数据jsondata");
				log.info(request.getRemoteAddr()+" getTestData()方法结束");
				return -431;
			}
			
			try {
				parameter = jsonObject.get("parameter");
				if( checkEspecialCode(parameter.toString()) )
				{
					log.info(request.getRemoteAddr()+"参数parameter("+parameter+")含有非法字符");
					log.info(request.getRemoteAddr()+" getTestData()方法结束");
					return -160;
				}
				flag = false;
			} catch (Exception e) {
				
			}
			
			//不存在模糊查询参数parameter，则接收其他参数进行具体查询
			if( flag )
			{
				try {
					String deviceID = jsonObject.getString("deviceID");
					if( checkEspecialCode(deviceID) )
					{
						log.info(request.getRemoteAddr()+"参数deviceID("+deviceID+")含有非法字符");
						log.info(request.getRemoteAddr()+" getTestData()方法结束");
						return -152;
					}
					map.put("deviceID", deviceID);
				} catch (Exception e) {
					
				}
				try {
					String testDate = jsonObject.getString("testDate");
					if( checkDate(testDate) )
					{
						log.info(request.getRemoteAddr()+"参数testDate("+testDate+")格式错误，日期格式必须符合：yyyy-MM-dd");
						log.info(request.getRemoteAddr()+" getTestData()方法结束");
						return -161;	
					}
					map.put("testDate", testDate );
				} catch (Exception e) {
					
				}
				try {
					int result = jsonObject.getInt("result");
					if( result != 0 && result != 1 )
					{
						log.info(request.getRemoteAddr()+"参数testDate("+result+")数值非法，只能是0或1");
						log.info(request.getRemoteAddr()+" getTestData()方法结束");
						return -162;
					}
					
					map.put("result", result);
						
				} catch (Exception e) {
					
				}
				try {
					productBatch = jsonObject.getString("productBatch");
					if( checkEspecialCode(productBatch) )
					{
						log.info(request.getRemoteAddr()+"参数productBatch("+productBatch+")含有非法字符");
						log.info(request.getRemoteAddr()+" getTestData()方法结束");
						return -163;
						
					}
					map.put("productBatch", productBatch);
				} catch (Exception e) {
					
				}
				try {
					pageNow = jsonObject.getInt("pageNow");
					if( pageNow <= 0 )
					{
						log.info(request.getRemoteAddr()+"参数pageNow("+pageNow+")数值非法，不能小于等于0");
						log.info(request.getRemoteAddr()+" getTestData()方法结束");
						return -433;
					}
						
				} catch (Exception e) {
					
				}
				try {
					pageSize = jsonObject.getInt("pageSize");
					if( pageSize <= 0 )
					{
						log.info(request.getRemoteAddr()+"参数pageSize("+pageSize+")数值非法，不能小于等于0");
						log.info(request.getRemoteAddr()+" getTestData()方法结束");
						return -434;
					}
				} catch (Exception e) {
					pageSize = 30;//给出默认值
				}
				
			}
			//接收数据完毕
			
			//查询数据
			if( parameter != null )
			{
				list = dao.query(testingTableName, parameter, 1, null , pageNow , pageSize );
			}
			else if(map.size() > 0)
			{
				if( productBatch.length() > 0 )
				{
					//按批次查询测试信息，测试表里没有该属性，需要联表操作
					list = dao.getTestInfoByBatch(productBatch);
					
				}
				else {
					list = dao.getInfoByPage(testingTableName, map, pageNow, pageSize);
				}
				
			}
			//没有接收到参数，拒绝请求
			else {
				log.info(request.getRemoteAddr()+" 没有提供参数，拒绝请求");
				log.info(request.getRemoteAddr()+" getTestData()方法结束");
				return -164;
			}
		
			////**********************   返回数据  *****************************
			if( pageNow > 0 )
			{
				int totalSize = dao.getTableCount(testingTableName , map);
				responseService.setTotalSize(totalSize);
			}
			
			if( list != null && list.size() > 0 )
			{
				for(Map<String, Object> m : list )
				{
					//将Date类型格式化
					//System.out.println(m.get("testDate") );
					String date = dateToString( m.get("testDate") );
					m.remove("testDate");
					m.put("testDate", date );
					//System.out.println(m.get("testDate") );
				}
					
				log.info(request.getRemoteAddr()+" 用户("+userName+")获取测试信息成功");
				responseService.okResponseGetInfo(response, 200, list ,request.getRemoteAddr());
				log.info(request.getRemoteAddr()+" getTestData()方法结束");
				return 200;
			}
			else {
				log.info(request.getRemoteAddr()+" 用户("+userName+")请求的资源没有找到，获取测试信息失败");
				log.info(request.getRemoteAddr()+" getTestData()方法结束");
				return 404;
			}
			
			
		/*}
		//非法用户，拒绝
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")为非法用户，拒绝");
			log.info(request.getRemoteAddr()+" getTestData()方法结束");
			return -74;
		}*/

	}

	
}
