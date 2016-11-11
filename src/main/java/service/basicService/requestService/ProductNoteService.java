package service.basicService.requestService;

import java.util.ArrayList;
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

import net.sf.json.JSONObject;
import dao.ProductNoteDao;
import domain.ProductNote;
import domain.User;
import service.basicService.internal.OperProductData;
import service.basicService.reponseService.BasicResponseService;
import service.interfaceService.BasicImplementService;

@Service
public class ProductNoteService extends BasicImplementService {

	private static Log log = LogFactory.getLog(ProductNoteService.class.getName());
	
	private BasicResponseService responseService = new BasicResponseService();
	//@Autowired
	private static ProductNoteDao dao;
	public void setDao(ProductNoteDao dao) {
		this.dao = dao;
	}

	
	/**获取生产完成记录
	 * 1. 接收账户信息，识别用户身份
	 * 2. 非法用户：拒绝
	 * 3. 合法用户：接收json数据，校验数据；如果没有json数据，返回全部
	 * @param request
	 * @param response
	 * @return
	 */
	public int getNote( HttpServletRequest request , HttpServletResponse response )
	{
		log.info(request.getRemoteAddr()+" 进入getNote()方法");
		int pageNow = -1;
		int pageSize = -1;
		List<Map<String , Object>> list = new ArrayList<Map<String,Object>>();
		Map<String , Object> map = new HashMap<String, Object>();
		
		if( request.getContentLength() > 1024 )
		{
			log.info(request.getRemoteAddr()+" URL长度大于1024，拒绝请求");
			log.info(request.getRemoteAddr()+" getNote()方法结束");
			return 414;
		}
		
		boolean jsonFlag = true;//标记是否有json数据
		int queryTab = -1;//查询标记，如果queryTab=0,查询全部，queryTab=1查询与自己相关的记录（自己申报的或者自己提交的生产记录）
		int userAuthority = -1;
		//账户信息
		String userName = "";
		//String hashedPassword;
		
		//接收账户信息
		int check = checkUser(request);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
				userAuthority = check;//用户权限
				userName = request.getHeader("userName").toString();
				//hashedPassword = request.getHeader("hashedPassword").toString();
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
				return 500;
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"getNote()方法结束");
			return check;
		}
		
		//合法用户
		//if( userAuthority == 0 || userAuthority == 1 )
		//{
			
			JSONObject jsonObject = new JSONObject();
			try {
				String json = request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据：jsondata="+json );
				
				jsonObject = JSONObject.fromObject(json);
				if( jsonObject.size() == 0 )
				{
					log.info("json为空，缺少参数");
					//System.out.println("json为空，缺少参数");
					jsonFlag = false;
					//return -142;
				}
				
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+" 用户("+userName+")没有提供的json数据(jsondata)");
				//System.out.println("没有json数据");
				jsonFlag = false;
				//responseService.okResponseEmpty(response, 200);
				//return -142;
			}
			
			if( jsonFlag )
			{
				try {
					queryTab = jsonObject.getInt("queryTab");
					/*if( queryTab != 0 && queryTab != 1 )
					{
						log.info(request.getRemoteAddr()+" 没有查询标记参数queryTab，无法明确查询目的，拒绝请求");
						return -141;
					}*/
				} catch (Exception e) {
					
				}
				try {
					String productBatch = jsonObject.getString("productBatch");
					if( checkEspecialCode(productBatch) )
					{
						log.info(request.getRemoteAddr()+"参数productBatch("+productBatch+")含有非法字符");
						log.info(request.getRemoteAddr()+" getNote()方法结束");
						return -201;
					}
					map.put("productBatch", productBatch);
				} catch (Exception e) {
					
				}
				try {
					int productOverdue = jsonObject.getInt("productOverdue");
					if( productOverdue != 0 && productOverdue != 1 )
					{
						log.info(request.getRemoteAddr()+"参数productOverdue("+productOverdue+")数值非法，只能是0或1");
						log.info(request.getRemoteAddr()+" getNote()方法结束");
						return -208;
					}
					map.put("productOverdue", productOverdue);
				} catch (Exception e) {
					
				}
				try {
					String notePerson = jsonObject.getString("notePerson");
					if( checkEspecialCode(notePerson) )
					{
						log.info(request.getRemoteAddr()+"参数notePerson("+notePerson+")含有非法字符");
						log.info(request.getRemoteAddr()+" getNote()方法结束");
						return -203;
					}
					map.put("notePerson", notePerson);
				} catch (Exception e) {
					
				}
				try {
					pageNow = jsonObject.getInt("pageNow");
					if( pageNow <= 0 )
					{
						log.info(request.getRemoteAddr()+"参数pageNow("+pageNow+")数值非法，不能小于等于0");
						log.info(request.getRemoteAddr()+" getNote()方法结束");
						return -433;
					}	
				} catch (Exception e) {
					
				}
				try {
					pageSize = jsonObject.getInt("pageSize");
					if( pageSize <= 0 )
					{
						log.info(request.getRemoteAddr()+"参数pageSize("+pageSize+")数值非法，不能小于等于0");
						log.info(request.getRemoteAddr()+" getNote()方法结束");
						return -434;
					}
				} catch (Exception e) {
					pageSize = 30;//给出默认值
				}
				
			}
			//---接收参数完毕
			
			//查询与自己相关的记录（自己的申报的生产完成记录或者自己提交的生产完成记录）
			if( userAuthority == 0 || queryTab == 1 )
			{
				list = dao.getMyNote(userName, map, pageNow, pageSize);
			}
			else //if( map.size() > 0 )
			{
				list = dao.getInfoByPage(productNoteTableName, map, pageNow, pageSize);
				
			}
			/*//没有提供参数
			else {
				//System.out.println("未提供有效参数，拒绝请求");
				//return -142;
				list = dao.getInfo(productNoteTableName, null);//查询全部信息
			}*/
			
			
		/*}//-------合法用户
		//非法用户，拒绝
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")为非法用户，拒绝");
			log.info(request.getRemoteAddr()+" getNote()方法结束");
			return -143;
		}*/
		
		////*******************     返回数据   **********************
		if( pageNow > 0 )
		{
			int totalSize = dao.getTableCount(testingTableName , map);
			responseService.setTotalSize(totalSize);
		}
		
		if( list != null && list.size() > 0 )
		{
			
			for( Map<String, Object> m : list )
			{
				//将Date类型格式化
				System.out.println(m.get("productStartDate") );
				String date1 = dateToString( m.get("productStartDate") );
				String date2 = dateToString( m.get("productEndDate") );
				String date3 = dateToString( m.get("noteDate") );
				//移除原来的值
				m.remove("productStartDate");
				m.remove("productEndDate");
				m.remove("noteDate");
				//放进转换后的值
				m.put("productStartDate", date1 );
				m.put("productEndDate", date2 );
				m.put("noteDate", date3 );
				System.out.println(m.get("productStartDate") );
				
				//通过notePerson获取记录人的姓名
				String personid = m.get("notePerson").toString();
				User user = (User) dao.getByOne(User.class,"_id", personid);
				if( user == null )
				{
					log.error(request.getRemoteAddr()+"记录表的中的信息有误，记录人不存在");
					log.info(request.getRemoteAddr()+" getNote()方法结束");
					return 500;
				}
				m.put("notePerson", user.getName() );
				
			}
			
			log.info(request.getRemoteAddr()+" 用户("+userName+")获取生产完成记录成功");
			responseService.okResponseGetInfo(response, 200, list ,request.getRemoteAddr());
			log.info(request.getRemoteAddr()+" getNote()方法结束");
			return 200;
		}
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")所请求的资源没有找到");
			log.info(request.getRemoteAddr()+" getNote()方法结束");
			return 404;
		}
		
		
	}
	
	/***添加生产完成记录
	 * 1. 获取账户信息，识别用户身份
	 * 2. 非法用户：拒绝
	 * 3. 合法用户：接收json数据，校验；没有json数据，返回错误码
	 * 4. 插入数据
	 * @param request
	 * @return
	 */
	public int addNote( HttpServletRequest request )
	{
		log.info(request.getRemoteAddr()+" 开始进入addNote()方法");
		
		String productBatch = "";
		String productStartDate = "";
		String productEndDate = "";
		int productTotalQuantity = 0;
		int productUsableNumber = 0;
		int productOverdue = 0;
		String productOverdueExplain = "";
		
		if( request.getContentLength() > 1024 )
		{
			log.info(request.getRemoteAddr()+" URL长度超过1024，拒绝请求");
			log.info(request.getRemoteAddr()+" addNote()方法结束");
			return 414;
		}
		
		int userAuthority = -1;//标记用户身份
		String userName = "";
		String hashedPassword = "";
		//接收账户信息
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
				return 500;
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"addNote()方法结束");
			return check;
		}
		
		//合法用户
		//if( userAuthority == 0 || userAuthority == 1 )
		//{
			Map<String , Object> map = new HashMap<String, Object>();
			JSONObject jsonObject = new JSONObject();
			//接收json数据
			try {
				String json = request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据：jsondata="+json );
				
				jsonObject = JSONObject.fromObject(json);
				productBatch = jsonObject.getString("productBatch");
				productStartDate = jsonObject.getString("productStartDate");
				productEndDate = jsonObject.getString("productEndDate");
				productTotalQuantity = jsonObject.getInt("productTotalQuantity");
				productUsableNumber = jsonObject.getInt("productUsableNumber");
				productOverdue = jsonObject.getInt("productOverdue");
				productOverdueExplain = jsonObject.getString("productOverdueExplain");
				
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+" 用户("+userName+")添加生产完成记录时缺少必要参数或者参数不合法，拒绝请求。异常情况如下：" , e );
				log.info(request.getRemoteAddr()+" addNote()方法结束");
				return -204;
			}
			
			//校验参数格式,参数格式错误，则返回错误码 -122
			if( checkEspecialCode(productBatch) || productBatch == "" )
			{
				log.info(request.getRemoteAddr()+" 参数productBatch("+productBatch+")含有非法字符");
				log.info(request.getRemoteAddr()+" addNote()方法结束");
				return -201;
			}
			else if( checkChineseCode(productBatch))
			{
				log.info(request.getRemoteAddr()+" 参数productBatch("+productBatch+")含有中文字符");
				log.info(request.getRemoteAddr()+" addNote()方法结束");
				return -213;
			}
			else if( checkEspecialCode(productOverdueExplain) )
			{
				log.info(request.getRemoteAddr()+" 参数productOverdueExplain("+productOverdueExplain+")含有非法字符");
				log.info(request.getRemoteAddr()+" addNote()方法结束");
				return -202;
			}
			else if( !checkDate(productEndDate) || !checkDate(productStartDate) )
			{
				log.info(request.getRemoteAddr()+" 参数productStartDate("+productStartDate+")或productEndDate("+productEndDate+")为日期类型参数，日期格式必须符合：yyyy-MM-dd");
				log.info(request.getRemoteAddr()+" addNote()方法结束");
				return -205;
			}
			else if( checkNoteDate(productStartDate, productEndDate))
			{
				log.info(request.getRemoteAddr()+" 实际生产开始时间应该在完成时间之前");
				log.info(request.getRemoteAddr()+" addNote()方法结束");
				return -212;
			}
			else if( productTotalQuantity < 0 )
			{
				log.info(request.getRemoteAddr()+"productTotalQuantity("+productTotalQuantity+")数值非法，不能小于0");
				log.info(request.getRemoteAddr()+" addNote()方法结束");
				return -206;
			}
			else if( productUsableNumber < 0 )
			{
				log.info(request.getRemoteAddr()+"productUsableNumber("+productUsableNumber+")数值非法，不能小于0");
				log.info(request.getRemoteAddr()+" addNote()方法结束");
				return -207;
			}
			else if( productOverdue != 0 && productOverdue!=1 )
			{
				log.info(request.getRemoteAddr()+"productOverdue("+productOverdue+")数值非法，只能是0或1");
				log.info(request.getRemoteAddr()+" addNote()方法结束");
				return -208;
			}
			else {
				//校验数据完毕
				
				//判断该生产批次的信息是否已经存在
				Object pnote = dao.findOne(productNoteTableName, "productBatch", productBatch);
				//Object pnote = dao.getByOne(ProductNote.class, "productBatch", productBatch);
				//System.out.println(pnote);
				if( pnote != null )
				{
					log.info(request.getRemoteAddr()+" 该生产批次("+productBatch+")的信息已经存在，不能重复添加");
					log.info(request.getRemoteAddr()+" addNote()方法结束");
					return -209;
				}
				
				map.put("productBatch", productBatch);
				map.put("productStartDate", dateConvert(productStartDate));
				map.put("productEndDate", dateConvert(productEndDate));
				map.put("productTotalQuantity", productTotalQuantity);
				map.put("productUsableNumber", productUsableNumber);
				map.put("productOverdue", productOverdue);
				map.put("productOverdueExplain", productOverdueExplain);
				//获取记录人的编号
				try {
					User user = dao.getAccount(userName, hashedPassword );
					map.put("notePerson", user.get_id());
					map.put("noteDate", getThisTime());
					
				} catch (Exception e) {
					log.error("获取记录人id时发生异常：" , e );
				}
				
				
				//插入数据
				int k = dao.insert(productNoteTableName, map);
				
				if( k > 0 )
				{
					log.info(request.getRemoteAddr()+" 用户("+userName+")添加生产完成记录成功");
					//批量更新产品信息（根据测试数据更新产品状态）
					OperProductData oper = new OperProductData();
					oper.updateProduct(productBatch);
					
					log.info(request.getRemoteAddr()+" addNote()方法结束");
					return 200;
				}
				else {
					log.info(" 数据库操作失败，用户("+userName+")添加生产完成记录失败");
					
					log.info(request.getRemoteAddr()+" addNote()方法结束");
					return -210;
				}
				
			}
			
		/*}
		//非法用户，拒绝
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")为非法用户，拒绝请求");
			log.info(request.getRemoteAddr()+" addNote()方法结束");
			return -123;
		}*/
		
		
	}
	
	/****修改生产完成记录
	 * 1. 获取要修改的记录的id,获取账户信息
	 * 2. 接收json数据，校验；没有json数据，则不操作数据库，直接返回200 空消息体
	 * 3. 有json数据，接收校验后插入数据库
	 * @param request
	 * @return
	 */
	public int updateNote( HttpServletRequest request )
	{
		log.info(request.getRemoteAddr()+" 开始进入updateNote()方法");
		
		String id = "";
		try {
			String uri = request.getRequestURI();
			String[] str = uri.split("/:");
			id = str[1];
			
			//System.out.println("id :" + id);
			if( checkEspecialCode(id) )
			{
				log.info(request.getRemoteAddr()+" URL参数id("+id+")含有非法字符，拒绝请求");
				log.info(request.getRemoteAddr()+" updateNote()方法结束");
				return -131;
			}
				
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+" URL参数id获取失败，拒绝请求");
			log.info(request.getRemoteAddr()+" updateNote()方法结束");
			return 415;
		}
		
		String userName = "";
		//String hashedPassword;
		//获取账户信息
		int userAuthority = -1;//标记用户信息
		
		int check = checkUser(request);
		//账户验证通过
		if( check == 0 || check == 1 )
		{
			try {
				userAuthority = check;//用户权限
				userName = request.getHeader("userName").toString();
				//hashedPassword = request.getHeader("hashedPassword").toString();
			} catch (Exception e) {
				log.error("接收账户信息出错：" , e );
				return 500;
			}
			
		}
		//账户验证失败，返回错误码
		else {
			log.info(request.getRemoteAddr()+"updateNote()方法结束");
			return check;
		}
		
		//合法用户,接收数据
		//if( userAuthority == 0 || userAuthority == 1 )
		//{
			Map<String , Object> map = new HashMap<String, Object>();
			
			//判断该id的记录是否存在，存在，则接收数据，进行更新，否则返回错误码-131
			//ProductNote note = (ProductNote) getByOne(ProductNote.class,"_id", id);
			Object notelist = dao.findOne(productNoteTableName,"_id", id);
		
			//该id的记录存在，接收数据
			if( notelist != null )
			{
				JSONObject jsonObject = new JSONObject();
				try {
					String json = request.getParameter("jsondata");
					log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据：jsondata="+json );
					
					jsonObject = JSONObject.fromObject(json);
					if( jsonObject.size() == 0 )
					{
						log.info(request.getRemoteAddr()+" 用户("+userName+")提供的json数据为空，数据库不做任何操作，返回200");
						log.info(request.getRemoteAddr()+" updateNote()方法结束");
						return 200;
					}
				} catch (Exception e) {
					log.info(request.getRemoteAddr()+" 用户("+userName+")未提供json数据(jsondata)，数据库不做任何操作，直接返回200");
					log.info(request.getRemoteAddr()+" updateNote()方法结束");
					return 200;
				}
				
				//接收参数
				try {
					String productBatch = jsonObject.getString("productBatch");
					if( checkEspecialCode(productBatch) || productBatch == "" )
					{
						log.info(request.getRemoteAddr()+" 参数productBatch("+productBatch+")含有非法字符");
						log.info(request.getRemoteAddr()+" updateNote()方法结束");
						return -201;
					}
					map.put("productBatch", productBatch);
				} catch (Exception e) {
					
				}
				try {
					String productStartDate = jsonObject.getString("productStartDate");
					if( !checkDate(productStartDate) )
					{
						log.info(request.getRemoteAddr()+" 参数productStartDate("+productStartDate+")为日期类型参数，必须符合日期格式：yyyy-MM-dd");
						log.info(request.getRemoteAddr()+" updateNote()方法结束");
						return -205;
					}
					map.put("productStartDate", productStartDate );
				} catch (Exception e) {
					
				}
				try {
					String productEndDate = jsonObject.getString("productEndDate");
					if( !checkDate(productEndDate) )
					{
						log.info(request.getRemoteAddr()+" 参数productEndDate("+productEndDate+")为日期类型参数，必须符合日期格式：yyyy-MM-dd");
						log.info(request.getRemoteAddr()+" updateNote()方法结束");
						return -205;
					}
					map.put("productEndDate", productEndDate );
				} catch (Exception e) {
					
				}
				try {
					int productTotalQuantity = jsonObject.getInt("productTotalQuantity");
					if( productTotalQuantity < 0 )
					{
						log.info(request.getRemoteAddr()+" 参数productTotalQuantity("+productTotalQuantity+")数字非法，不能小于0");
						log.info(request.getRemoteAddr()+" updateNote()方法结束");
						return -206;
					}
					map.put("productTotalQuantity", productTotalQuantity);
				} catch (Exception e) {
					
				}
				try {
					int productUsableNumber = jsonObject.getInt("productUsableNumber");
					if( productUsableNumber < 0 )
					{
						log.info(request.getRemoteAddr()+" 参数productUsableNumber("+productUsableNumber+")数字非法，不能小于0");
						log.info(request.getRemoteAddr()+" updateNote()方法结束");
						return -207;
					}
					map.put("productUsableNumber", productUsableNumber);
				} catch (Exception e) {
					
				}
				try {
					int productOverdue = jsonObject.getInt("productOverdue");
					if( productOverdue != 0 && productOverdue != 1 )
					{
						log.info(request.getRemoteAddr()+" 参数productOverdue("+productOverdue+")数字非法，只能是0或1");
						log.info(request.getRemoteAddr()+" updateNote()方法结束");
						return -208;
					}
					map.put("productOverdue", productOverdue);
				} catch (Exception e) {
					
				}
				try {
					String productOverdueExplain = jsonObject.getString("productOverdueExplain");
					if( checkEspecialCode(productOverdueExplain) )
					{
						log.info(request.getRemoteAddr()+" 参数productOverdueExplain("+productOverdueExplain+")含有非法字符");
						log.info(request.getRemoteAddr()+" updateNote()方法结束");
						return -202;
					}
					map.put("productOverdueExplain", productOverdueExplain);
				} catch (Exception e) {
					
				}
				
			}
			//该id的记录不存在，拒绝请求
			else {
				log.error(request.getRemoteAddr()+" 用户("+userName+")要修改的对象("+id+")不存在");
				log.info(request.getRemoteAddr()+" updateNote()方法结束");
				return -211;
			}
			
			//System.out.println(map);
			//获取数据完毕,更新数据
			dao.updateByOne(ProductNote.class, "_id", new ObjectId(id) , map);
			
			log.error(request.getRemoteAddr()+" 用户("+userName+")修改id为"+id+"的生产完成记录成功");
			log.info(request.getRemoteAddr()+" updateNote()方法结束");
			return 200;
			
		/*}
		else {
			log.info(request.getRemoteAddr()+" 用户("+userName+")非法用户，拒绝");
			log.info(request.getRemoteAddr()+" updateNote()方法结束");
			return -134;
		}*/
		
		
	}//----更新生产完成记录
	
	
	
}
