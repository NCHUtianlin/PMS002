package service.basicService.requestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONObject;
import service.basicService.reponseService.UserResponseService;
import service.interfaceService.BasicImplementService;
import dao.UserDao;
import domain.User;


public class UserService extends BasicImplementService {
	
	private static Log log = LogFactory.getLog(UserService.class.getName());
	
	private UserResponseService uResponseService = new UserResponseService();
	
	//@Autowired
	private static UserDao dao;
	public void setDao(UserDao dao) {
		this.dao = dao;
	}


	//******************添加用户
	public int addUser(HttpServletRequest request) 
	{
		log.info(request.getRemoteAddr()+"进入addUser()方法");
		/*******
		 * 1. 判断账户名与密码是否含有非法字符
		 * 2. 判断用户权限
		 * 3. 通过request接收数据
		 * 4. 接收数据，验证数据
		 * 5. 验证通过，验证信息是否已经存在，不存在则加入数据库，存在则返回错误码
		 * 6. 验证失败，返回错误信息
		 * 
		 * ********/
		String userName = "" ;
		String hashedPassword = "" ;
		String json = "";
		JSONObject jsonObject = null;
		//boolean jsonFlag = true;
		int userAuthority = -1;//用户权限标识
		
		String name = "";
		String phone = "";
		String password = "";
		String email = "";
		int authority = -1;
		
		//识别用户身份
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
		else {
			log.info(request.getRemoteAddr()+"addUser()方法结束");
			return check;
		}
			
		//如果userAuthority = 1，说明该用户为管理员身份，具有添加用户信息的权限
		if(userAuthority == 1)
		{
			//获取JSON数据
			try {
				
				json=request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+" 接收用户"+userName+"提供的的json数据："+json);
				
				jsonObject=JSONObject.fromObject(json);
				log.info(request.getRemoteAddr()+"用户"+userName+"发送的请求数据 : "+jsonObject);
				if( jsonObject.size() == 0 )
				{
					//System.out.println("没有JSON数据");
					log.info(request.getRemoteAddr()+"用户"+userName+" 添加用户时未能提供json数据，拒绝请求");
					log.info(request.getRemoteAddr()+"addUser()方法结束");
					return -430;
				}
				
			} catch (Exception e) {
				//System.out.println("没有JSON数据");
				log.info(request.getRemoteAddr()+"用户"+userName+" 添加用户时未能提供数据，拒绝请求" , e );
				log.info(request.getRemoteAddr()+"addUser()方法结束");
				return -431;
			}
			

			//接收json数据
			
			// 1 .接收数据,如果有参数不存在或者为null值，则返回错误码 -3
			try {
				
				name = jsonObject.get("name").toString();
				phone = jsonObject.get("phone").toString();
				password = jsonObject.get("password").toString();
				email = jsonObject.get("email").toString();
				authority = jsonObject.getInt("authority");
				//System.out.println("取得数据："+name+" , "+phone);
				
			} catch (Exception e) {
				//System.out.println("缺少参数（有字段为null） ： "+e);
				log.info(request.getRemoteAddr()+"用户"+userName+" 添加用户时，检测到缺少必要参数，异常如下：",e );
				log.info(request.getRemoteAddr()+"addUser()方法结束");
				return -13;//返回错误状态码
			}	
			
			
			
			//boolean flag = true;
			//校验参数:姓名、密码、手机、邮箱、权限
			if( checkEspecialCode(name) || name == "" )
			{
				log.info(request.getRemoteAddr()+"提供的参数name="+name+"或者password="+password+"含有非法字符");
				log.info(request.getRemoteAddr()+"addUser()方法结束");
				return -14;
			}
			else if(checkChineseCode(password) || password == "" )
			{
				log.info(request.getRemoteAddr()+"提供的参数password="+password+"含有中文字符");
				log.info(request.getRemoteAddr()+"addUser()方法结束");
				return -14;
			}
			else if(!checkEmail(email) || email == "" )
			{
				log.info(request.getRemoteAddr()+"提供的参数email="+email+"不符合邮箱标准");
				log.info(request.getRemoteAddr()+"addUser()方法结束");
				return -17;
			}
			else if( !checkPhone(phone) || phone == "" )
			{
				log.info(request.getRemoteAddr()+"提供的参数phone="+phone+"不符合中国手机号码标准");
				log.info(request.getRemoteAddr()+"addUser()方法结束");
				return -16;
			}
			else if( authority != 0 && authority != 1 )
			{
				log.info(request.getRemoteAddr()+"接收到参数authority="+authority+", 参数有误，权限参数只能是 0 或者 1");
				log.info(request.getRemoteAddr()+"addUser()方法结束");
				return -18;
			}
			
			//进入数据库查询，判断该用户信息是否已经注册（通过手机号码校验）
			//Map<String, Object> mapPhone = new HashMap<String, Object>();
			//mapPhone.put("phone", phone);
			//List list = dao.getInfo( userTableName, mapPhone );
			
			User list = dao.getUserInfo("phone", phone );
			//System.out.println("返回list"+list);
			//list里面没有元素，则说明该手机号未曾注册过
			if( list == null )
			{
				Map<String , Object> map = new HashMap<String, Object>();
				map.put("name", name);
				map.put("password", password);
				map.put("phone", phone);
				map.put("email", email);
				map.put("authority", authority);
				map.put("workState", 1);//添加的信用户，默认工作状态为正常

				//for( Map.Entry<String, Object> m : map.entrySet())
					//System.out.println(m.getKey()+" ； "+m.getValue());
				
				//想数据库插入用户信息
				int k = dao.insert(userTableName, map);
				if( k > 0 )
				{
					log.info(request.getRemoteAddr()+"用户"+userName+"成功添加一个用户（"+map.get("phone")+")");
					log.info(request.getRemoteAddr()+"addUser()方法结束");
					return 200;//插入数据成功
				}
				else {
					log.info(request.getRemoteAddr()+"用户"+userName+"添加用户失败");
					log.info(request.getRemoteAddr()+"addUser()方法结束");
					return -27;//服务器出错，插入数据失败
				}
			}
					
			//userInfo不为null，则该信息已经存在
			else {
				log.info(request.getRemoteAddr()+"用户"+userName+"添加用户失败（该用户已经存在，不能重复添加）");
				log.info(request.getRemoteAddr()+"addUser()方法结束");
				return -10;
			}
					
		}
		
		//如果userAuthority = 0，说明该用户为普通用户，没有添加用户的权限，返回错误码 -5 
		else// if( userAuthority == 0)
		{
			log.info(request.getRemoteAddr()+"用户"+userName+"为普通用户，在本系统中没有添加用户的权限");
			log.info(request.getRemoteAddr()+"addUser()方法结束");
			return -428;
		}
		
		
	}
	
	//****************获取用户信息
	public int getUser(HttpServletRequest request , HttpServletResponse response)
	{
		log.info(request.getRemoteAddr()+"开始进入getUser()方法");
		if( request.getContentLength() > 2048 )
		{
			log.info(request.getRemoteAddr()+"请求的URL长度过长，拒绝请求");
			log.info(request.getRemoteAddr()+"getUser()方法结束");
			return 414;
		}
		
		/**
		 * 1. 获取必要的数据，账户信息不能为null，否则返回错误码
		 * 2. 校验账户信息并识别该用户身份
		 * 3. 管理员：必须携带参数queryTab,表明你所要查询的对象（ 0 所有用户 ， 1 个人信息 ， 2 按条件查询信息 ）
		 *    普通用户：queryTab = 1，只能查询个人信息，所传其他参数无效
		 *    未能识别的用户：非法，拒绝请求
		 * */
		String userName = "";
		String hashedPassword ="";
		//用Map存放参数，如果参数不为空，则将参数名和值放进map
		Map<String, Object> map = new HashMap<String, Object>();
		String json = "";
		JSONObject jsonObject = null;
		boolean jsonFlag = true;//判断是否接收到json数据
		//参数
		String parameter = "";
		String id = "";
		int pageNow = -1;
		int pageSize = -1;
		int queryTab = -1;
		int userAuthority = -1;
				
				
		//识别用户身份
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
			log.info(request.getRemoteAddr()+"getUser()方法结束");
			return check;//返回错误码
		}
		
		try {
			//获取JSON数据
			json=request.getParameter("jsondata");
			log.info(request.getRemoteAddr()+"用户"+userName+"提供的JSON请求数据："+json);
			
			jsonObject=JSONObject.fromObject(json);
			
			if( jsonObject.size() == 0 )
			{
				//System.out.println("没有json数据，至少携带queryTab标记查询");
				log.info(request.getRemoteAddr()+"用户"+userName+"没有提供JSON数据，系统无法提供信息");
				log.info(request.getRemoteAddr()+"getUser()方法结束");
				return 430;
			}
			
		} catch (Exception e) {
			//System.out.println("没有json数据");
			log.info(request.getRemoteAddr()+"用户"+userName+"没有提供JSON数据，系统无法提供信息");
			jsonFlag = false;
			log.info(request.getRemoteAddr()+"getUser()方法结束");
			return 431;
		}
		
		//接收json数据
		if( jsonFlag )
		{
			//获取参数信息,如果该参数不存在（null），将被catch捕获，但不需要作处理，继续接收下一个参数
			try {
				parameter = jsonObject.getString("parameter");
				if( checkEspecialCode(parameter))
				{
					log.info(request.getRemoteAddr()+"参数parameter("+parameter+")中含有非法字符,拒绝请求");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -28;
				}
			} catch (Exception e) {
				
			}
			try {
				id = jsonObject.getString("_id");
				if( checkEspecialCode(id) )
				{
					log.info(request.getRemoteAddr()+"参数id("+id+") 含有非法字符，错误");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -26;
				}
				//System.out.println("获取参数_id = "+id);
				map.put("_id", id);
				
			} catch (Exception e) 
			{
				
			}
			try {
				String name = jsonObject.getString("name");
				if( checkEspecialCode(name) )
				{
					log.info(request.getRemoteAddr()+"参数name("+name+")含有非法字符，错误");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -14;
				}
				else if( name != ""){
					map.put("name", name);
					//System.out.println("你的名字是 "+map.get("name"));
				}
			} catch (Exception e) 
			{ }
			try {
				String phone = jsonObject.get("phone").toString();
				if( ! checkPhone(phone) )
				{
					log.info(request.getRemoteAddr()+"参数phone("+phone+")不符合手机号码规范，错误");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -16;
				}
				else if( phone != ""){
					map.put("phone", phone);
					//System.out.println("phone = "+map.get("phone"));
				}
			} catch (Exception e) 
			{ }
			try {
				String email = jsonObject.get("email").toString();
				//System.out.println("你的Email是 "+email);
				if( ! checkEmail(email) )
				{
					log.info(request.getRemoteAddr()+"参数email("+email+")含有非法字符，错误");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -17;
				}
				else if( email != ""){
					map.put("email", email);
					//System.out.println("email = "+map.get("email"));
				}
			} catch (Exception e) 
			{ }
			try {
				int authority = jsonObject.getInt("authority");
				if( authority != 0 && authority != 1 )
				{
					log.info(request.getRemoteAddr()+"接收到参数authority="+authority+",数值非法，权限参数只能为0或1，拒绝请求");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -18;
				}
				
				map.put("authority", authority);
				//System.out.println("authority = "+map.get("authority"));
				
			} catch (Exception e) 
			{ }
			try {
				int workState = jsonObject.getInt("workState");
				if( workState != 0 && workState != 1 )
				{
					log.info(request.getRemoteAddr()+"接收到workState="+workState+",状态参数只能为0或1，拒绝请求");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -19;
				}
				
				map.put("workState", workState);
				
			} catch (Exception e) 
			{ }
			try {
				pageNow = jsonObject.getInt("pageNow");
				if( pageNow <= 0 )
				{
					log.info(request.getRemoteAddr()+"接收到pageNow="+pageNow+",当前页数不能小于等于0，拒绝请求");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -433;
				}
			} catch (Exception e) { 
				pageNow = -1; //表示不分页
			}
			try {
				pageSize = jsonObject.getInt("pageSize");
				if( pageSize <= 0 )
				{
					log.info(request.getRemoteAddr()+"接收到参数pageSize="+pageSize+",数值不能小于等于0，拒绝请求");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -434;
				}
			} catch (Exception e) { 
				pageSize = 30; //表示不分页
			}
			try {
				queryTab = jsonObject.getInt("queryTab");
				if( queryTab != 0 && queryTab != 1 && queryTab != 2 )
				{
					log.info(request.getRemoteAddr()+"接收到参数queryTab="+queryTab+",数值只能为0或1 或2，拒绝请求");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -25;
				}
			} catch (Exception e) {
				//System.out.println("queryTab没有接收到，无法标记用户查询类型");
				log.info(request.getRemoteAddr()+"用户"+userName+"进入系统获取用户信息,但是没有给出查询标记queryTab，拒绝请求");
				log.info(request.getRemoteAddr()+"getUser()方法结束");
				return -25;
			}
			
		}//如果接收到了json数据，则一个一个进行接收
		
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		//3. 判断用户权限
		//3.1. 如果userAuthority = 1，说明该用户为管理员身份
		if( userAuthority == 1 )
		{
			log.info(request.getRemoteAddr()+"用户"+userName+"，经过系统识别，其身份为管理员");
			//pagenow为 0 ，则返回所有用户信息；如果map不为null，则是按条件查询所有用户信息
			if( queryTab == 0 )
			{
				//System.out.println(" queryTab = 0 ，则返回所有用户信息");
				//查询所有用户信息
				list = dao.getAllUser(userTableName, pageNow , pageSize );
				
			}
			
			//pageNow = 2 ,则按条件分页查询用户信息
			else if( queryTab == 2 )
			{
				
				if( parameter.length() >= 0 )
				{
					//log.info("用户"+userName+"进入系统获取用户信息，提供了一个未知参数parameter，则系统进行模糊匹配查询");
					list = dao.query(userTableName, parameter, 1, "" , pageNow , pageSize );
				}
				else if( id.length() == 24 )
				{
					//System.out.println("根据id 查询信息");
					//log.info("用户"+userName+"进入系统获取用户信息,提供参数为id = "+id);
					User user=  (User) dao.getById(User.class, id);
					if( user != null )
					{
						log.info(request.getRemoteAddr()+"用户"+userName+"获取用户信息成功");
						
						user.setPassword("*******");
						uResponseService.okResponseGetInfo(response, 200, convertToJson(user) ,request.getRemoteAddr());
						log.info(request.getRemoteAddr()+"getUser()方法结束");
						return 200;
					}
					else {
						
						log.info(request.getRemoteAddr()+"用户"+userName+"进入系统获取用户信息,然而并没有查询到数据");
						log.info(request.getRemoteAddr()+"getUser()方法结束");
						return -21;
					}
				}
				else if( map != null && map.size() > 0 ){
					//查询所有用户信息
					list = dao.getInfoByPage(userTableName, map , pageNow ,pageSize);
				}
				else {
					//System.out.println("没有提供参数，拒绝请求");
					log.info(request.getRemoteAddr()+"用户"+userName+"进入系统获取用户信息,提供的查询标记queryTab显示为按条件查询用户，却没有提供条件，系统无法理解，拒绝请求");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -29;
				}
				
				
			}
			//queryTab = 1，查询个人信息
			else if( queryTab == 1 )
			{
				//System.out.println("queryTab = 1，查询个人信息");
				//log.info("用户"+userName+"进入系统获取个人信息");
				User user = (User) dao.getAccount(userName, hashedPassword);
				if( user != null )
				{
					log.info(request.getRemoteAddr()+"用户"+userName+"获取用户信息成功");
					
					user.setPassword("*******");
					uResponseService.okResponseGetInfo(response, 200, convertToJson(user) ,request.getRemoteAddr() );
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return 200;
				}
				else {
					log.error(request.getRemoteAddr()+"用户"+userName+"通过验证，却获取个人信息失败");
					log.info(request.getRemoteAddr()+"getUser()方法结束");
					return -21;
				}
			}
			else {
				log.info(request.getRemoteAddr()+"用户"+userName+"进入系统获取用户信息，但未能提供查询标记，系统无法提供查询服务");
				log.info(request.getRemoteAddr()+"getUser()方法结束");
				return -25;//拒绝请求
			}
			
		}
		//3.2. 如果userAuthority = 0，说明该用户为普通用户，只能查看自己的信息
		else// if( userAuthority == 0 )
		{
			log.info(request.getRemoteAddr()+"用户"+userName+"，经过系统识别，为普通用户");
			User user = (User) dao.getAccount(userName, hashedPassword);
			if( user != null )
			{
				log.info(request.getRemoteAddr()+"用户"+userName+"获取个人信息成功");
				user.setPassword("*******");
				uResponseService.okResponseGetInfo(response, 200, convertToJson(user) ,request.getRemoteAddr());
				log.info(request.getRemoteAddr()+"getUser()方法结束");
				return 200;
			}
			else {
				log.error(request.getRemoteAddr()+"用户"+userName+"通过验证，却获取个人信息失败");
				log.info(request.getRemoteAddr()+"getUser()方法结束");
				return -21;
			}
			
		}
		
		
	///******************************  返回数据  *********************************************	
		if( pageNow > 0 || queryTab == 0 || queryTab == 2 )
		{
			int totalSize = dao.getTableCount(userTableName , map);
			uResponseService.setTotalSize(totalSize);
		}
		
		//4 返回数据
		if( list != null && list.size() > 0 )
		{
			log.info(request.getRemoteAddr()+"用户"+userName+"进入系统获取用户信息成功");
			uResponseService.okResponseGetInfo(response, 200, list ,request.getRemoteAddr());
			log.info(request.getRemoteAddr()+"getUser()方法结束");
			return 200;
		}
		else {
			//System.out.println("数据库未能提供数据。。。。");
			log.info(request.getRemoteAddr()+"用户"+userName+"请求的资源没有找到");
			log.info(request.getRemoteAddr()+"getUser()方法结束");
			return 404;
		}
	}

	//****************删除用户信息
	public int deleteUser(HttpServletRequest request)
	{
		/*******
		 * 1. 通过request接收数据: id, userName,hashPassword
		 * 2. 判断账户名与密码是否含有非法字符
		 * 3. 判断用户权限
		 * 4. 管理员：判断数据库中是否存在该信息，存在则删除该信息，不存在则返回错误码
		 * 5. 普通用户权限不足
		 * 
		 * ********/
		
		log.info(request.getRemoteAddr()+"开始进入 deleteUser()方法");
		//判断URL长度
		if( request.getContentLength() > 1024 )
		{
			//System.out.println("URL过长，拒绝请求");
			log.info(request.getRemoteAddr()+"URL长度过长，拒绝请求");
			log.info(request.getRemoteAddr()+"deleteUser()方法结束");
			return 414;
		}
		
		String id = "";
		try {
			//从地址栏获取id
			String uri = request.getRequestURI();
			//System.out.println("uri :"+uri);
			String[] index = uri.split("/:");
			
			id = index[1];
			
			log.info(request.getRemoteAddr()+"接收到URL参数id="+id+",该参数为用户要删除的对象");
			
			if( checkEspecialCode(id) )
			{
				log.info(request.getRemoteAddr()+"接收到URL参数id="+id+"含有非法字符，拒绝请求");
				log.info(request.getRemoteAddr()+"deleteUser()方法结束");
				return 415;
			}
			
		} catch (Exception e) {
			//System.out.println("请求格式错误。。");
			log.info(request.getRemoteAddr()+"请求的URL格式错误，拒绝请求");
			log.info(request.getRemoteAddr()+"deleteUser()方法结束");
			return 415;
		}
		
		
		String userName = "";
		String hashedPassword = "";
		int userAuthority = -1;
		
		//识别用户身份
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
				log.info(request.getRemoteAddr()+"deleteUser()方法结束");
				return check;
		}		
		
		
		// 2.1. 如果userAuthority = 1，说明该用户为管理员身份，具有删除用户信息的权限
		if( userAuthority == 1 )
		{
			log.info(request.getRemoteAddr()+"用户"+userName+"经过系统识别，其身份为管理员");
			
			//验证该id是否为本用户自己的
			//获取本用户自己的id
			User userThis = (User) dao.getAccount(userName, hashedPassword);
				
			if( id.equals(userThis.get_id()) )
			{
				//System.out.println("权限非法，用户不能自己删除自己");
				log.info(request.getRemoteAddr()+"用户"+userName+"进入系统，提供的删除对象是自己，属于非法行为");
				log.info(request.getRemoteAddr()+"deleteUser()方法结束");
				return -429;
			}
			else {
				
				int k = dao.deleteById(userTableName, id);
				if( k > 0 )
				{   //删除操作成功
					log.info(request.getRemoteAddr()+"用户"+userName+"进入系统删除了用户（id = "+id+"）的信息");
					log.info(request.getRemoteAddr()+"deleteUser()方法结束");
					return 200;
				}
				else if( k == -1 ){
					//该用户已不存在
					log.info(request.getRemoteAddr()+"用户"+userName+"要删除的对象以及不存在了");
					log.info(request.getRemoteAddr()+"deleteUser()方法结束");
					return -20;
				}
				else {
					log.info(request.getRemoteAddr()+"用户"+userName+"进入系统删除用户信息时，系统删除失败");
					log.info(request.getRemoteAddr()+"deleteUser()方法结束");
					return -30;
				}
				
			}				
			
		}
		// 2.2. 该用户为普通用户身份,权限不足
		else// if( userAuthority == 0 )
		{
			log.info(request.getRemoteAddr()+"用户"+userName+"经过系统识别，其身份为普通用户，没有删除的操作权限，属于非法行为");
			log.info(request.getRemoteAddr()+"deleteUser()方法结束");
			return -428;
		}
		
		
	}


	//****************修改用户信息
	public int updateUser(HttpServletRequest request)
	{
		log.info(request.getRemoteAddr()+"开始进入updateUser()方法");
		/*******
		 * 1. 通过request接收数据: id, userName,hashPassword
		 * 2. 判断账户名与密码是否含有非法字符
		 * 3. 判断用户权限（普通用户不能修改工作权限和状态，管理员不能修改其他用户密码）
		 * 4. 接收数据，验证数据格式;  若是没有json数据，则返回200空消息体
		 * 5. 验证通过，数据库中是否存在该信息，存在则删除该信息，不存在则返回错误码
		 * 6. 验证失败，返回错误信息
		 * 
		 * ********/
		
		//判断URL长度
		if( request.getContentLength() > 1024 )
		{
			//System.out.println("URL过长，拒绝请求");
			log.info(request.getRemoteAddr()+"URL长度过长，拒绝请求");
			log.info(request.getRemoteAddr()+"updateUser()方法结束");
			return 414;
		}
		
		//从地址栏获取id
		String uri = request.getRequestURI();
		//System.out.println("uri :"+uri);
		String[] index = uri.split("/:");
		String id = "";
		try {
			id = index[1];
			//System.out.println("获取到 id = "+id);
			
			if( checkEspecialCode(id) )
			{
				log.info(request.getRemoteAddr()+"接收到URL参数id="+id+"含有非法字符，拒绝请求");
				log.info(request.getRemoteAddr()+"updateUser()方法结束");
				return 415;
			}
			log.info(request.getRemoteAddr()+"接收到URL参数id="+id+",该参数为用户要修改对象的id");
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+"请求格式错误,URL参数id未获取到，系统无法判断要修改的对象，拒绝请求");
			log.info(request.getRemoteAddr()+"updateUser()方法结束");
			return 415;
		}			
		
		String json = "";
		JSONObject jsonObject = null;
		//boolean jsonFlag = true;
		//将有值的参数放入map，字段名和值就可以对应，方便数据库操作
		Map<String , Object> map = new HashMap<String , Object>();
		int userAuthority = -1;
		//参数
		String userName = "";
		String hashedPassword = "";
		String password = "";
		int authority = -1;
		int workState = -1;
		
		//识别用户身份
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
			log.info(request.getRemoteAddr()+"updateUser()方法结束");
			return check;
		}		
		
		
		//合法用户，接收数据，校验数据
		//if( userAuthority == 0 || userAuthority == 1 )
		//{
			//获取JSON数据
			try {
				//获取JSON数据
				json=request.getParameter("jsondata");
				log.info(request.getRemoteAddr()+"用户"+userName+"提供的JSON请求数据："+ json );
				
				jsonObject=JSONObject.fromObject(json);
				
				if( jsonObject.size() == 0 )
				{
					log.info(request.getRemoteAddr()+"客户端发送过来的JSON数据为空，没有参数，不对数据库中数据做修改");
					log.info(request.getRemoteAddr()+"updateUser()方法结束");
					return 200;//没有提供参数，不做数据库操作，直接返回200
				}
				
			} catch (Exception e) {
				log.info(request.getRemoteAddr()+"没有json数据，不对数据库中数据做修改: "+e.getMessage());
				log.info(request.getRemoteAddr()+"updateUser()方法结束");
				return 200;
			}
			
		
			//接收数据
			//if( jsonFlag )
			//{
				
				//获取参数信息,如果该参数不存在（null），将被catch捕获，但不需要作处理，继续接收下一个参数
				try {
					String name = jsonObject.getString("name");
					if( checkEspecialCode(name) || name == "" )
					{
						log.info(request.getRemoteAddr()+"参数name("+name+")含有非法字符，错误");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return -14;
					}
					else if( name != ""){
						map.put("name",name);
					}
				} catch (Exception e) 
				{ }
				try {
					password = jsonObject.getString("password");
					if( checkEspecialCode(password) || password == "" )
					{
						log.info(request.getRemoteAddr()+"参数password("+password+")含有非法字符，错误");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return -15;
					}
					else if( password != ""){
						map.put("password",password);
						
					}
				} catch (Exception e) 
				{ }
				try {
					String phone = jsonObject.getString("phone");
					if( ! checkPhone(phone) || phone == "" )
					{
						log.info(request.getRemoteAddr()+"参数phone("+phone+")不符合手机号码规范，错误");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return -16;
					}
					else if( phone != ""){
						map.put("phone",phone);
					}
				} catch (Exception e) 
				{ }
				try {
					String email = jsonObject.get("email").toString();
					//System.out.println("你的Email是 "+email);
					if( ! checkEmail(email) )
					{
						log.info(request.getRemoteAddr()+"参数email("+email+")含有非法字符，错误");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return -17;
					}
					else if( email != ""){
						map.put("email",email);
					}
				} catch (Exception e) 
				{ }
				try {
					authority = jsonObject.getInt("authority");
					if( authority != 0 && authority != 1 )
					{
						log.info(request.getRemoteAddr()+"接收到参数authority="+authority+",数值错误，数值只能为0或1,拒绝请求");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return -18;
					}
					/************ 权限不足 *****************/
					if( userAuthority == 0 )
					{
						log.info(request.getRemoteAddr()+"系统识别该用户（"+userName+"）为普通用户，无法修改自己的权限，属于非法行为，拒绝请求");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return -428;
					}
					map.put("authority",authority);
					
				} catch (Exception e) 
				{ }
				try {
					workState = jsonObject.getInt("workState");
					if( workState != 0 && workState != 1 )
					{
						log.info(request.getRemoteAddr()+"接收到参数workState="+workState+",数值错误，数值只能是0或1，拒绝请求");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return -19;
					}
					/************ 权限不足 *****************/
					if( userAuthority == 0 )
					{
						log.info(request.getRemoteAddr()+"系统识别该用户（"+userName+"）为普通用户，无法修改自己的工作状态，属于非法行为，拒绝请求");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return -428;
					}
					
					map.put("workState",workState);
					
				} catch (Exception e) 
				{ }
			
			/*}//---接收json数据
			else {
				log.info(request.getRemoteAddr()+"没有参数，不做数据库操作，返回200");
				log.info(request.getRemoteAddr()+"updateUser()方法结束");
				return 200;
			}
		
		}//------合法用户
		//非法用户，拒绝请求
		else {
			log.info(request.getRemoteAddr()+"未能识别该用户身份！拒绝请求");
			log.info(request.getRemoteAddr()+"updateUser()方法结束");
			//System.out.println("未能识别该用户身份");
			return -33;
		}
		*/
		
		// 2.1. 判断要修改的信息是否存在
		if( dao.getById(User.class, id) == null )
		{
			log.info(request.getRemoteAddr()+"要修改的用户信息已经不存在，无法进行修改操作，拒绝请求");
			log.info(request.getRemoteAddr()+"updateUser()方法结束");
			return -21;//信息不存在
		}
		
		// 2.2. 获取本用户的id，判断本用户是否是修改自己的信息
		User userThis = (User) dao.getAccount(userName, hashedPassword);
		
		//id 相同，则是请求修改自己的信息
		if( userThis != null && id.equals(userThis.get_id()) )
		{
			//用户要修改自己的工作状态或者权限，非法行为
			if( workState != userThis.getWorkState() || authority != userThis.getAuthority() )
			{
				//workState 和 authority 初始化的值为-1
				if( workState != -1 )
				{
					log.info(request.getRemoteAddr()+"用户"+userName+"意图修改自己的工作状态，属于非法行为，拒绝");
					log.info(request.getRemoteAddr()+"updateUser()方法结束");
					return -22;
				}
				else if( authority != -1 )
				{
					log.info(request.getRemoteAddr()+"用户"+userName+"意图修改自己的权限，属于非法行为，拒绝");
					log.info(request.getRemoteAddr()+"updateUser()方法结束");
					return -22;
				}
			}
			int k = dao.updateById( User.class , id , map );
			if( k > 0 )
			{
				log.info(request.getRemoteAddr()+"用户"+userName+"修改信息成功");
				log.info(request.getRemoteAddr()+"updateUser()方法结束");
				return 200;
			}
			else {
				log.info(request.getRemoteAddr()+"用户"+userName+"修改信息失败");
				log.info(request.getRemoteAddr()+"updateUser()方法结束");
				return -31;
			}
			
		}//----修改自己的信息
		//id不相同，是请求修改他人信息
		else 
		{
			// 2.3. 如果userAuthority = 1，管理员，不能修改其他用户的密码
			if( userAuthority == 1 )
			{
				if( password != null && password.length() > 0 )
				{
					log.info(request.getRemoteAddr()+"经过核对信息，你修改的是他人信息，你无权修改他人密码");
					log.info(request.getRemoteAddr()+"updateUser()方法结束");
					//System.out.println("权限不足，不能修改他人密码");
					return -22;
				}
				else {
					int k = dao.updateById( User.class , id , map );
					if( k > 0 )
					{
						log.info(request.getRemoteAddr()+"用户"+userName+"修改信息成功");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return 200;
					}
					else {
						log.info(request.getRemoteAddr()+"用户"+userName+"修改信息失败");
						log.info(request.getRemoteAddr()+"updateUser()方法结束");
						return -31;
					}
				}
				
			}//---管理员修改他人信息
			//普通用户权限不足，拒绝请求
			else {
				log.info(request.getRemoteAddr()+"用户"+userName+"为普通用户，没有权限修改他人信息，拒绝请求");
				log.info(request.getRemoteAddr()+"updateUser()方法结束");
				return -428;
			}
		}				
							
		
	}	
	

	
}
