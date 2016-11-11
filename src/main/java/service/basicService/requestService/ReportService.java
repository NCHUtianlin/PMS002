package service.basicService.requestService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dao.ReportDao;
import domain.ProductType;
import domain.Report;
import domain.User;
import service.basicService.internal.OperProductData;
import service.basicService.reponseService.UserResponseService;
import service.interfaceService.BasicImplementService;
import net.sf.json.JSONObject;


public class ReportService extends BasicImplementService {
	
	private static Log log = LogFactory.getLog(ReportService.class.getName());
	
	private UserResponseService uResponseService = new UserResponseService();
	//@Autowired
	private static ReportDao dao;
	public void setDao(ReportDao dao) {
		this.dao = dao;
	}

	
	/******************添加申报信息
	 * 1. 通过request接收数据
	 * 2. 判断账户名与密码是否含有非法字符
	 * 3. 判断用户权限
	 * 4. 接收数据，验证数据
	 * 5. 验证通过，插入数据库
	 * 6. 验证失败，返回错误信息
	 * @param request
	 * @return
	 */
	public int addReport(HttpServletRequest request) 
	{
		log.info(request.getRemoteAddr()+" 开始进入addReport()方法");
			
		String json = "";
		JSONObject jsonObject = null;
		//账户信息
		String userName = "";
		String hashedPassword = "";
		//添加申报信息的必要参数
		String productTypeID;
		int reportQuantity;
		String reportStartDate;
		String reportEndDate;
		double reportCompleteRate;
		String producer;
		//int userAuthority = -1 ;
		
		//接收账户信息
		int check = checkUser(request);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
				//userAuthority = check;//用户权限
				userName = request.getHeader("userName").toString();
				hashedPassword = request.getHeader("hashedPassword").toString();
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"addReport()方法结束");
			return check;
		}
		
		try {
			//获取JSON数据
			json=request.getParameter("jsondata");
			log.info(request.getRemoteAddr()+"用户"+userName+"提供的JSON数据 : "+json);
			
			jsonObject=JSONObject.fromObject(json);
			
			if( jsonObject.size() == 0 )
			{
				log.error(request.getRemoteAddr()+" 用户"+userName+"提供的JSON数据为空，拒绝请求");
				log.info(request.getRemoteAddr()+" addReport()方法 结束");
				return -430;
			}
				
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+"用户"+userName+"没有提供Json数据！拒绝请求");
			log.info(request.getRemoteAddr()+" addReport()方法结束");
			return -431;
		}
			
		//接收json数据
		// 1 .接收数据,如果有参数不存在或者为null值，则返回错误码 -42
		try {
			
			productTypeID = jsonObject.getString("productTypeID");
			reportQuantity = jsonObject.getInt("reportQuantity");
			reportStartDate = jsonObject.getString("reportStartDate");
			reportEndDate = jsonObject.getString("reportEndDate");
			reportCompleteRate = jsonObject.getDouble("reportCompleteRate");
			producer = jsonObject.getString("producer");
			//System.out.println("生产商："+producer);
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+"缺少参数（有字段为空）或有参数类型不符");
			log.info(request.getRemoteAddr()+" addReport()方法结束");
			return -51;
		}	
		//----接收完毕
		
			
		//如果userAuthority = 1 或者 0，说明该用户是合法用户，具有添加申报信息的权限
		//if( userAuthority == 1 || userAuthority == 0 )
		//{
			//校验参数
			if( checkEspecialCode(producer) || producer == "" )
			{
				log.info(request.getRemoteAddr()+"有参数(producer="+producer+")含有非法字符，拒绝请求！");
 				log.info(request.getRemoteAddr()+" addReport()方法结束");
				return -52;
			}
			else if( !checkDate(reportStartDate) || reportStartDate == "" )
			{
				log.info(request.getRemoteAddr()+"有参数(reportStartDate="+reportStartDate+")含有非法字符，拒绝请求！");
 				log.info(request.getRemoteAddr()+" addReport()方法结束");
				return -53;
			}
			else if( !checkDate(reportEndDate) || reportEndDate == "" )
			{
				log.info(request.getRemoteAddr()+"有参数(reportEndDate="+reportEndDate+")含有非法字符，拒绝请求！");
 				log.info(request.getRemoteAddr()+" addReport()方法结束");
				return -54;
			}
			else if( checkReportDate(reportStartDate, reportEndDate) )
			{
				log.info(request.getRemoteAddr()+" 计划生产开始日期应该在今天之后、在结束日期之前");
				log.info(request.getRemoteAddr()+" addReport()方法结束");
				return -74;
			}
			else if( reportCompleteRate < 0 )
			{
				log.info(request.getRemoteAddr()+"有参数(reportCompleteRate="+reportCompleteRate+")含有非法字符，拒绝请求！");
 				log.info(request.getRemoteAddr()+" addReport()方法结束");
				return -55;
			}
			else if( reportQuantity < 0 )
			{
				log.info(request.getRemoteAddr()+"有参数(reportQuantity="+reportQuantity+")含有非法字符，拒绝请求！");
 				log.info(request.getRemoteAddr()+" addReport()方法结束");
				return -56;
			}
			
			//校验该产品类型是否存在产品类型表里
			if(  null == dao.getByOne(ProductType.class,"_id", productTypeID) )
			{
				log.info(request.getRemoteAddr()+"非法请求，产品类型不存在！");
				log.info(request.getRemoteAddr()+" addReport()方法结束");
				return -57;
			}
			
			//获取申报人的 id
			User userThis = (User) dao.getAccount(userName, hashedPassword);
			
			Map<String , Object> map = new HashMap<String, Object>();
			map.put("productTypeID", productTypeID );
			map.put("producer", producer );
			map.put("reportQuantity", reportQuantity );
			map.put("reportStartDate", dateConvert(reportStartDate) );
			map.put("reportEndDate", dateConvert(reportEndDate) );
			map.put("reportCompleteRate", reportCompleteRate );
			map.put("reportPerson", userThis.get_id() );//申报人编号
			map.put("reportTime", getThisTime() );//申报时间即当前时间
			map.put("checkResult", 2 );
			
			//想数据库插入申报信息
			int k = dao.insert(reportTableName, map);
			if( k > 0 )
			{
				log.info(request.getRemoteAddr()+"用户"+userName+"添加申报信息成功");
				log.info(request.getRemoteAddr()+" addReport()方法结束");
				return 200;//插入数据成功
			}
			else {
				log.info(request.getRemoteAddr()+"用户"+userName+"添加申报信息失败");
				log.info(request.getRemoteAddr()+" addReport()方法结束");
				return -58;//服务器出错，插入数据失败
			}
				
						
		/*}
		//非法用户，拒绝请求
		else {
			log.info(request.getRemoteAddr()+"经系统识别为非法用户，拒绝请求！");
			log.info(request.getRemoteAddr()+" addReport()方法结束");
			return -44;
		}*/
					
			
	}
	
	/*****************修改申报信息
	 * 
	 * @param request
	 * @return
	 */
	public int updateReport(HttpServletRequest request)
	{
		/** 1. 获取要修改信息的id，如果不存在，则返回错误码
		 *  1. 获取账户信息:拒绝非法用户请求
		 *  2. 接收数据，并校验； 若是没有json数据，则返回200空消息体;若有json数据，但没有我所必要的数据，返回错误码
		 *  3. 
		 *      当申报表已经通过审核，则不可修改
		 *      当用户为普通用户时，不能修改有关审核内容
		 *      非法用户，拒绝请求
		 *      该id的资源是否存在
		 * **/

		log.info(request.getRemoteAddr()+" 进入updateReport()方法");
		//判断URL长度
		if( request.getContentLength() > 1024 )
		{
			log.info(request.getRemoteAddr()+" URL过长，拒绝请求！");
			log.info(request.getRemoteAddr()+" updateReport()方法 结束");
			return 414;
		}
		
		//从地址栏获取id
		String uri = request.getRequestURI();
		//System.out.println("uri :"+uri);
		String[] index = uri.split("/:");
		String id = "";
		try {
			id =index[1];
			//System.out.println("获取到 id = "+id);
			
			if( checkEspecialCode(id) )
			{
				log.info(request.getRemoteAddr()+" 参数id="+id+"含有非法字符，拒绝请求");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return 415;
			}
			
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+" URL格式出错,发生异常：",e);
			log.info(request.getRemoteAddr()+" updateReport()方法 结束");
			return 415;
		}
		
		//用Map存放参数，如果参数不为空，则将参数名和值放进map
		Map<String, Object> map = new HashMap<String, Object>();
		String json = "";
		JSONObject jsonObject = null;
		//boolean jsonFlag = true;
		int userAuthority = -1;//用户权限标记
		//账户信息
		String userName = "";
		String hashedPassword = "";
		//审核信息的相关参数
		int checkResult = -1;
		String checkExplain = "";
		
		
		//1. 获取账户信息
		int check = checkUser(request);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
				userAuthority = check;//用户权限
				userName = request.getHeader("userName").toString();
				hashedPassword = request.getHeader("hashedPassword").toString();
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"updateReport()方法结束");
			return check;
		}
		
		
		try {
			//获取JSON数据
			json=request.getParameter("jsondata");
			log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据: "+json );
			
			jsonObject=JSONObject.fromObject(json);
			//System.out.println("jsondata : "+jsonObject);
			if( jsonObject.size() == 0 )
			{
				log.info(request.getRemoteAddr()+" 用户("+userName+")没有提供如何参数，数据库不做如何处理，给客户端返回状态码200");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return 200;
			}
			
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+"没有提供Json数据");
			log.info(request.getRemoteAddr()+" updateReport()方法 结束");
			return 200;
		}
		
		//接收json数据
		try {
			String productTypeID = jsonObject.getString("productTypeID");
			//productTypeID小于等于 0 ，或者该产品类型不存在
			if( productTypeID == "" || checkEspecialCode(productTypeID) || dao.getById(ProductType.class, productTypeID) == null )
			{   log.info(request.getRemoteAddr()+"productTypeID("+productTypeID+")含有非法字符或者不存在这样的产品类型");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -59;
			}
			map.put("productTypeID", productTypeID);
		} catch (Exception e){
			
		}
		try {
			int reportQuantity = jsonObject.getInt("reportQuantity");
			if( reportQuantity <= 0 )
			{
				log.info(request.getRemoteAddr()+"参数reportQuantity("+reportQuantity+")为申报数量，不能小于0");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -56;
			}
			map.put("reportQuantity", reportQuantity);
		} catch (Exception e) {
			
		}
		try {
			String reportStartDate = jsonObject.getString("reportStartDate");
			if( checkDate(reportStartDate) ) {
				if( checkReportDate(reportStartDate, null)){
					log.info(request.getRemoteAddr()+"参数reportStartDate("+reportStartDate+")为计划开始日期参数，必须在当前时间之后");
					log.info(request.getRemoteAddr()+" updateReport()方法 结束");
					return -74;
				}
				else {
					map.put("reportStartDate", dateConvert(reportStartDate) );
				}
			}
			else
			{
				log.info(request.getRemoteAddr()+"参数reportStartDate("+reportStartDate+")为日期参数，必须符合日期格式：yyyy-MM-dd");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -53;
			}
		} catch (Exception e) {
			
		}
		try {
			String reportEndDate = jsonObject.getString("reportEndDate");
			if( checkDate(reportEndDate) ){
				if( checkReportDate(reportEndDate, null)){
					log.info(request.getRemoteAddr()+"参数reportStartDate("+reportEndDate+")为计划完成日期参数，必须在当前时间之后");
					log.info(request.getRemoteAddr()+" updateReport()方法 结束");
					return -74;
				}
				else {
					map.put("reportEndDate", dateConvert(reportEndDate) );
				}
			}
			else
			{
				log.info(request.getRemoteAddr()+"参数reportEndDate("+reportEndDate+")为日期参数，必须符合日期格式：yyyy-MM-dd");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -54;
			}
		} catch (Exception e) {
			
		}
		try {
			double reportCompleteRate = jsonObject.getDouble("reportCompleteRate");
			if( reportCompleteRate < 0 )
			{
				log.info(request.getRemoteAddr()+"参数reportCompleteRate("+reportCompleteRate+")为计划生产完成率，不能小于0");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -55;
			}
		} catch (Exception e) {
			
		}
		try {
			String producer = jsonObject.getString("producer");
			if( checkEspecialCode(producer) )
			{
				log.info(request.getRemoteAddr()+"参数producer("+producer+")含有非法字符");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -52;
			}
			map.put("producer", producer);
		} catch (Exception e) {
			
		}
		try {
			checkResult = jsonObject.getInt("checkResult");
			
			if( userAuthority  == 0 )
			{   
				log.info(request.getRemoteAddr()+"用户("+userName+")经系统识别为普通用户，无权审核申报信息");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -428;
			}
			
			if( checkResult == 0 || checkResult == 1 ||checkResult == 2 )
				map.put("checkResult", checkResult);
			else {
				log.info(request.getRemoteAddr()+"参数checkResult("+checkResult+")数值不符，只能为0或1或2");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -60;
			}
		} catch (Exception e) {
			
		}
		try {
			checkExplain = jsonObject.getString("checkExplain");
			
			if( userAuthority  == 0 )
			{
				log.info(request.getRemoteAddr()+"用户("+userName+")经系统识别为普通用户，无权审核申报信息");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -428;
			}
			
			if( checkEspecialCode(checkExplain) )
			{
				log.info(request.getRemoteAddr()+"参数checkExplain("+checkExplain+")中含有非法字符，拒绝请求");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -61;
			}
			map.put("checkExplain", checkExplain);
		} catch (Exception e) {
			
		}
		
		if( map.size() == 0 )
		{
			log.info(request.getRemoteAddr()+"缺少必要的参数，拒绝请求");
			log.info(request.getRemoteAddr()+" updateReport()方法 结束");
			return -62;
		}
		//遍历map
		//for(Map.Entry<String , Object> m : map.entrySet())
		//{
		//	System.out.println(m.getKey()+" ："+m.getValue());
		//}
		
		//接收和校验数据完毕--------
		
		
		//合法用户
		//if( userAuthority == 1 || userAuthority == 0 )
		//{
			//判断要修改的信息是否存在
			//Report report = (Report) getById(Report.class , id);
			Object report = dao.findOne(reportTableName, "_id", id);
			
			if( report == null )
			{
				log.info(request.getRemoteAddr()+"用户("+userName+")要修改的申报信息不存在，拒绝请求");
				log.info(request.getRemoteAddr()+" updateReport()方法 结束");
				return -66;
			}
			else 
			{
				//普通用户不能修改审核信息
				if( userAuthority == 0 )
				{
					if( checkResult == 0 || checkResult == 1 ||checkResult == 2 || checkExplain.length() > 0 )
					{
						log.info(request.getRemoteAddr()+"用户("+userName+")经系统识别为普通用户，权限不足，无法修改审核信息");
						log.info(request.getRemoteAddr()+" updateReport()方法 结束");
						return -428;
					}
					
					int k = dao.updateById(Report.class, id , map);//更新申报表信息
					if( k > 0 )
					{
						log.info(request.getRemoteAddr()+"用户("+userName+")更新申报信息成功");
						log.info(request.getRemoteAddr()+" updateReport()方法 结束");
						return 200;
					}
					else
					{
						log.info(request.getRemoteAddr()+"用户("+userName+")更新申报信息失败");
						log.info(request.getRemoteAddr()+" updateReport()方法 结束");
						return -63;
					}
				}
				//管理员添加或者修改审核信息
				else {
					//获取该管理员编号
					User userManager = (User) dao.getAccount(userName, hashedPassword);
					
					map.put("checkDate", getThisTime());//审核日期
					map.put("checkPerson", userManager.get_id());//审核人编号
					
					//如果审核通过，则生成一个生产批次
					String productBatch = "";
					if( checkResult == 1 )
					{
						while(true)
						{
							productBatch = getBatch();//生成一个批次号码
							Report reports = (Report) dao.getByOne(Report.class , "productBatch", productBatch);
							
							//判断该生产批次是否已经存在
							if( reports == null )
								break;//不存在，则生成的该生产批次有效
						
						}
						map.put("productBatch", productBatch);
						int k = dao.updateById(Report.class, id , map);//更新申报表信息
						
						/*******审核通过，申请deviceID,分配MAC地址，批量添加产品信息
						 *   
						 */
						if( k > 0 )
						{
							OperProductData oper = new OperProductData();
							//批量添加产品信息
							int m = oper.addProduct(userName , productBatch);
							if( m > 0 )
							{
								//批量生产产品信息成功
								log.info(request.getRemoteAddr()+"用户("+userName+")经系统识别为管理员，你批准该次申报，该申报的生产批次是"
										+productBatch+",该批次的产品基本信息已经生成");
								log.info(request.getRemoteAddr()+" updateReport()方法 结束");
								return 200;
							}
							else {
								//批量生产产品信息失败，MAC资源不足，那么本次申报应当拒绝，即checkResult = 0
								
								Map<String , Object> backMap = new HashMap<String, Object>();
								backMap.put("checkResult", 0);
								dao.updateByOne(Report.class, "productBatch", productBatch, backMap);
								
								if( m == -75 )
								{
									log.info(request.getRemoteAddr()+" mac资源可以满足需求，但申请mac出错，请稍后重试");
									log.info(request.getRemoteAddr()+" updateReport()方法 结束");
									return -75;
								}
								else {
									log.info(request.getRemoteAddr()+"用户("+userName+")经系统识别为管理员，你批准该次申报，该申报的生产批次是"
											+productBatch+",但是现在的MAC地址可用数量不足，无法满足本次申报需求，拒绝请求");
									
									log.info(request.getRemoteAddr()+" updateReport()方法 结束");
									return -64;
								}
								
							}
							
						}
						else if( k == -2 )
						{
							log.info(request.getRemoteAddr()+"批次为"+productBatch+"已经审核,不能进行修改了，如有需要，请重新申请");
							log.info(request.getRemoteAddr()+" updateReport()方法 结束");
							return -67;//已经审核的申报信息不能进行修改了
						}
						else// if( k ==500 )
						{
							log.info(request.getRemoteAddr()+"服务器故障，修改申报信息失败");
							log.info(request.getRemoteAddr()+" updateReport()方法 结束");
							return -63;
						}
							
					}//-----审核通过
					//审核未通过，或者对申报进行修改
					else {
						int k = dao.updateById(Report.class, id , map);//更新申报表信息
						if( k > 0 )
						{
							log.info(request.getRemoteAddr()+"修改申报信息成功");
							log.info(request.getRemoteAddr()+" updateReport()方法 结束");
							return 200;
						}
						else if( k == -2 )
						{
							log.info(request.getRemoteAddr()+"已经审核的申报信息不能进行修改了");
							log.info(request.getRemoteAddr()+" updateReport()方法 结束");
							return -67;//已经审核的申报信息不能进行修改了
						}
						else// if( k ==500 )
						{
							log.info(request.getRemoteAddr()+" 服务器故障，修改申报信息失败");
							log.info(request.getRemoteAddr()+" updateReport()方法 结束");
							return -63;
						}
					}
					
				}
				
				
			}
			
			
		/*}
		//未能识别用户或非法用户
		else {
			log.info(request.getRemoteAddr()+"未能识别用户身份，或为非法用户，拒绝请求");
			log.info(request.getRemoteAddr()+" updateReport()方法 结束");
			return -53;
		}*/
		
		
	}
	
	/****************获取申报信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public int getReport(HttpServletRequest request , HttpServletResponse response) 
	{
		/***
		 * 1. 获取账户信息
		 * 2. 接收json数据，校验数据>>>> 具体查询：提供的参数是具体的参数数据； 参数queryTab 为 1 时表示查询个人申报信息，为0 时查询全部
		 * 							模糊匹配：只提供一个参数 parameter，后面进行多项匹配
		 * 					只要有parameter，那么就进行模糊查询，否则按具体参数查询；如果未提供任何参数，查询自己的申报信息
		 * 3. 获取用户身份
		 *   管理员可以查看所有信息；普通用户只能看到自己申报的信息；非法用户拒绝请求
		 * */
		log.info(request.getRemoteAddr()+"已经进入getReport()方法");
		
		if( request.getContentLength() > 2048 )
		{  
			log.info(request.getRemoteAddr()+"请求的URL长度过长，拒绝请求");
			log.info(request.getRemoteAddr()+" getReport()方法结束");
			return 414;
		}
		
		
		//用Map存放参数，如果参数不为空，则将参数名和值放进map
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapTimes = new HashMap<String, Object>();
		JSONObject jsonObject = null;
		//boolean jsonFlag = true;//判断接收的json数据是否为空或者没有json数据: true为有json数据
		boolean pFlag = true ;//判断json数据中是否存在参数parameter，如果存在，即是模糊查询：true为没有参数parameter
		Object parameter = null;
		//账户信息参数
		String userName = "";
		String hashedPassword = "";
		//分页参数
		int pageNow = -1;
		int pageSize = -1;
		int queryTab = 0;//标记请求范围：0 表示请求所有， 1 表示请求自己的申报; 默认为0
		int userAuthority = -1;
		
		//1. 获取账户信息
		int check = checkUser(request);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
				userAuthority = check;//用户权限
				userName = request.getHeader("userName").toString();
				hashedPassword = request.getHeader("hashedPassword").toString();
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"getReport()方法结束");
			return check;
		}
		
		try {
			//获取JSON数据
			String json=request.getParameter("jsondata");
			log.info(request.getRemoteAddr()+"用户("+userName+") 提供的json数据："+json );
			
			jsonObject=JSONObject.fromObject(json);
			
			if( jsonObject.size() == 0 )
			{
				log.info(request.getRemoteAddr()+"json数据为空，错误。。");
				log.info(request.getRemoteAddr()+" getReport()方法结束");
				return -430;
				//jsonFlag = false;//json数据为空
			}
			
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+"没有json数据");
			log.info(request.getRemoteAddr()+" getReport()方法结束");
			return -431;
			//jsonFlag = false;//没有json数据
		}
		
		//2. 接收json数据，校验数据
		
		//获取参数信息,如果该参数不存在（null），将被catch捕获，但不需要作处理，继续接收下一个参数
		try {
			parameter = jsonObject.get("parameter");
			if( checkEspecialCode(parameter.toString()) )
			{
				log.info(request.getRemoteAddr()+"参数parameter("+parameter+")含有非法字符");
				log.info(request.getRemoteAddr()+" getReport()方法结束");
				return -68;
			}
			/*else if( parameter.toString().length() == 0 )
			{
				log.info(request.getRemoteAddr()+"参数parameter("+parameter+")为空");
				log.info(request.getRemoteAddr()+" getReport()方法结束");
				return -64;
			}*/
			else {
				//System.out.println("parameter"+parameter);
				map.put("parameter", parameter);
				pFlag = false;
				//System.out.println(map.get("parameter"));
			}
		} catch (Exception e) {
			
		}	
		
		
		//有json数据，不存在参数parameter，那么进入具体查询
		if( pFlag )
		{
			try {
				queryTab = jsonObject.getInt("queryTab");
				if( queryTab != 0 && queryTab != 1 )
				{
					log.info(request.getRemoteAddr()+"参数queryTab("+queryTab+")数值非法，只能为0或1，错误");
					log.info(request.getRemoteAddr()+" getReport()方法结束");
					return -69;
				}
			} catch (Exception e) {
				
			}
			try {
				String productTypeID = jsonObject.getString("productTypeID");
				if( checkEspecialCode(productTypeID) ){
					log.info(request.getRemoteAddr()+"参数productTypeID("+productTypeID+")含有非法字符，错误");
					log.info(request.getRemoteAddr()+" getReport()方法结束");
					return -70;
				}
				map.put("productTypeID", productTypeID);
				//System.out.println(map.get("productTypeID"));
				
			} catch (Exception e) 
			{ }
			try {
				String productBatch = jsonObject.get("productBatch").toString();
				if( ! checkEspecialCode(productBatch) )
				{
					log.info(request.getRemoteAddr()+"参数productBatch("+productBatch+")含有非法字符，错");
					log.info(request.getRemoteAddr()+" getReport()方法结束");
					return -71;
				}
				else if( productBatch != ""){
					map.put("productBatch", productBatch);
					//System.out.println(map.get("productBatch"));
				}
				
			} catch (Exception e) 
			{ }
			try {
				int reportQuantity =  jsonObject.getInt("reportQuantity");
				if( reportQuantity <= 0 ){
					log.info(request.getRemoteAddr()+"参数reportQuantity("+reportQuantity+"),计划生产数量不能小于等于 0");
					log.info(request.getRemoteAddr()+" getReport()方法结束");
					return -56;
				}
				map.put("reportQuantity", reportQuantity);
				//System.out.println(map.get("reportQuantity"));
				
			} catch (Exception e) 
			{ }
		/**获取时间段，里面包含了start和end
	 * **/
			try {
				Object obj =  jsonObject.get("times");
				//System.out.println("计划生产开始时间段"+obj);
				JSONObject j = JSONObject.fromObject(obj);
				String start = j.getString("start");
				String end = j.getString("end");
				//校验日期格式
				if( checkDate(start) && checkDate(end) )
				{
					//System.out.println(start+"  --- "+end);
					//****######  mapTimes 里面存放了两个时间，后面进行多项匹配：申报时间，计划生产开始时间、完成时间，一起查询
					mapTimes.put("start", start );
					mapTimes.put("end", end );
				}
				else {
					log.info(request.getRemoteAddr()+"提供的日期参数("+obj+")格式错误,日期格式：yyyy-MM-dd ");
					log.info(request.getRemoteAddr()+" getReport()方法结束");
					return -72;
				}
				
			} catch (Exception e) 
			{ 
				
			}
			
			try {
				String producer = jsonObject.get("producer").toString();
				if( checkEspecialCode(producer) )
				{
					log.info(request.getRemoteAddr()+"参数producer("+producer+")含有非法字符，错误");
					log.info(request.getRemoteAddr()+" getReport()方法结束");
					return -52;
				}
				else if( producer != ""){
					map.put("producer", producer);
					//System.out.println(map.get("producer"));
				}
				
			} catch (Exception e) 
			{ }
			try {
				pageNow = jsonObject.getInt("pageNow");
			} catch (Exception e) 
			{ 
				//log.info("没有分页请求或者分页参数类型错误");
				//System.out.println("没有分页请求或者分页参数类型错误");
				pageNow = 1;
			}
			try {
				pageSize = jsonObject.getInt("pageSize");
				if( pageSize <= 0 )
					pageSize = 30 ;

			} catch (Exception e) {
				//log.info("pageSize为null或者类型不符");
				//System.out.println("pageSize为null或者类型不符");
				pageSize = 30;
			}
			
			
		}	
			
		
		
		List<Map<String , Object>> reports = null ;		
		
		//如果参数parameter存在，则模糊查询
		if( !pFlag  )
		{
			try {
				User user = dao.getAccount(userName, hashedPassword);
				reports = dao.query(reportTableName, parameter, userAuthority, user.get_id() , pageNow , pageSize );
			
			}catch (Exception e) {
				log.error(request.getRemoteAddr()+"模糊查询发生异常：" , e);
				log.info(request.getRemoteAddr()+" getReport()方法结束");
				return -73;
			}
		}
		//普通用户或者没有提供json数据，那么查询用户自己的申报信息
		else if( userAuthority == 0 || queryTab == 1 )
		{
			User user = dao.getAccount(userName, hashedPassword);
			map.put("reportPerson", user.get_id());
			reports = dao.getInfoMyself(reportTableName, map, pageNow, pageSize);
			
		}
		//管理员
		else// if( userAuthority == 1 )
		{
			//没有时间段参数
			if( mapTimes.size() == 0 )
			{
				reports =  dao.getInfoByPage(reportTableName, map, pageNow, pageSize);
			}
			//有时间段参数,不分页
			else if ( mapTimes.size() > 0 ){
				reports = dao.getInfoByTimes(reportTableName, map, mapTimes, pageNow, pageSize);
				
			}
			
		}
		/*else {
			log.info(request.getRemoteAddr()+"未能识别用户身份");
			log.info(request.getRemoteAddr()+" getReport()方法结束");
			return -63;
		}*/
		
///**************************     返回数据        ******************************
		if( pageNow > 0 )
		{	//获取数据总量
			int totalSize = dao.getTableCount(reportTableName , map);
			uResponseService.setTotalSize(totalSize);
		}
		//返回结果
		if (reports != null) {
			//获取数据成功
			for( Map<String, Object> m : reports )
			{
				//System.out.println(m.get("reportStartDate"));
				//将Date日期格式化
				Object date1 = dateToString( m.get("reportStartDate") ) ;
				Object date2 = dateToString( m.get("reportEndDate") );
				Object date3 = dateToString( m.get("reportTime") );
				Object date4 = dateToString( m.get("checkDate") );
				
				m.remove("reportStartDate");
				m.remove("reportEndDate");
				m.remove("reportTime");
				m.remove("checkDate");
				m.put("reportStartDate", date1);
				m.put("reportEndDate", date2);
				m.put("reportTime", date3);
				m.put("checkDate", date4);
				
				//将申报表里的产品类型ID换成对应的产品类型名称：那么，要从产品类型表里获取名称
				String typeid = m.get("productTypeID").toString();
				String reportPerson = m.get("reportPerson").toString();
				Object checkPerson = m.get("checkPerson");

				ProductType type = (ProductType) dao.getByOne(ProductType.class, "_id", new ObjectId(typeid));
				User userReport = (User) dao.getById(User.class, reportPerson);
				if( checkPerson != null )
				{
					User userCheck = (User) dao.getById(User.class, checkPerson.toString());
					if( userCheck == null )
					{
						log.error(request.getRemoteAddr()+" 获取的申报信息有问题，它的审核人不存在");
						log.info(request.getRemoteAddr()+" getReport()方法结束");
						return -73;
					}
					m.remove("checkPerson");
					m.put("checkPerson", userCheck.getName());
				}
				
				if( type == null )
				{
					log.error(request.getRemoteAddr()+" 获取的申报信息有问题，它的产品类型不存在");
					log.info(request.getRemoteAddr()+" getReport()方法结束");
					return -73;
				}
				else if( userReport == null )
				{
					log.error(request.getRemoteAddr()+" 获取的申报信息有问题，它的申报人不存在");
					log.info(request.getRemoteAddr()+" getReport()方法结束");
					return -73;
				}
				
				
				m.remove("productTypeID");
				m.put("productTypeName", type.getProductTypeName());
				m.remove("reportPerson");
				m.put("reportPerson", userReport.getName());
				
				
			}
			log.info(request.getRemoteAddr()+" 用户("+userName+")获取申报信息成功");
			uResponseService.okResponseGetInfo(response, 200, reports ,request.getRemoteAddr());
			
			log.info(request.getRemoteAddr()+" getReport()方法结束");
			return 200;
		}
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")请求的资源没有找到，获取申报信息失败");
			log.info(request.getRemoteAddr()+" getReport()方法结束");
			return 404;
		}
		
	}
	
	
	
	
}
