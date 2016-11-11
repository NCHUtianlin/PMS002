package service.interfaceService;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import service.basicService.requestService.ProductService;
import dao.UserDao;
import domain.User;

@Service
public class BasicImplementService implements BasicInterfaceService {
	
	private static Log log = LogFactory.getLog(BasicImplementService.class.getName());
	
	@Autowired
	private static UserDao dao;
	public void setDao(UserDao dao) {
		this.dao = dao;
	}
	
	
	//验证用户身份 ， 若返回 0 ，为普通用户  ，  若返回 1，为管理员
	public int checkUser( HttpServletRequest request )
	{  
		Object userName = null;
		Object hashedPassword = null;
		try{
			// 1. 接收账户信息			
			request.setCharacterEncoding("UTF-8");
			userName = request.getHeader("userName");
			hashedPassword = request.getHeader("hashedPassword");
		
		} catch (Exception e) {
			log.info(request.getRemoteAddr()+"账户信息不全(userName="+userName+",hashedPassword="+hashedPassword+")，未能识别用户身份，拒绝请求");
			log.error(request.getRemoteAddr()+"检测账户信息异常：",e);
			return -421;
		}
		int k =checkUser2(request, userName.toString(), hashedPassword.toString());
		return k;
		
	}
	
	public int checkUser2(HttpServletRequest request , String userName , String hashedPassword )
	{
		// 2. 是否为null
		if( userName == null && hashedPassword == null )
		{
			log.info(request.getRemoteAddr()+"没有账户信息(userName="+userName+",hashedPassword="+hashedPassword+")，未能识别用户身份，拒绝请求");
			return -421;
		}
		else if( userName == null )
		{
			log.info(request.getRemoteAddr()+"账户名userName=null，无法识别用户身份，拒绝请求");
			return -422;
		}
		else if( hashedPassword == null )
		{
			log.info(request.getRemoteAddr()+"密码hashedPassword=null，非法访问系统，拒绝请求");
			return -423;
		}	
			
				
		// 3. 账户信息是否为空
		if( userName.toString() == "" )
		{
			log.info(request.getRemoteAddr()+"账户名userName为空，无法识别用户身份，拒绝请求");
			return -422;
		}
		else if( hashedPassword == "" )
		{
			log.info(request.getRemoteAddr()+"密码hashedPassword为空，非法访问系统，拒绝请求");
			return -423;
		}			
		// 4. 账户信息中是否含有非法字符
		else if ( checkEspecialCode( userName.toString() ) ) {
			log.info(request.getRemoteAddr()+"账户名("+userName+")含有非法字符，拒绝请求");
			return -424;
		}
		else if( checkEspecialCode( hashedPassword.toString() ) ) 
		{
			log.info(request.getRemoteAddr()+"密码("+hashedPassword+")含有非法字符，拒绝请求");
			return -425;
		}
		else {			
		// 5. 通过校验，然后通过账户名查找用户信息 >>> 账户名是手机号码 。为了详细解析错误，做两次查询
			// 5.1 先检测该该账户名是否存在
			Object object = dao.findOne( "userTable", "phone", userName.toString() );
			//账户名不存在
			if( object == null )
			{
				log.info(request.getRemoteAddr()+" 用户"+userName+"不存在，属于非法用户");
				return -426;
			}
			//账户名存在
			else {				
			// 5.2 第二次查询，查询条件为 账户名和工作状态，只有工作状态正常的用户才能登陆系统
				User user =  (User) dao.getAccount(userName.toString());
				// 账户名存在，但是没有权限登陆系统
				if( user == null )
				{
					log.info(request.getRemoteAddr()+" 用户"+userName+"没有权限登陆系统");
					return -428;
				}
				// 正常用户
				else
				{
					String pwd = user.getPassword();
					int authority = user.getAuthority();
					
					if(hashedPassword.equals( pwd) ){
						//密码匹配，说明用户存在,返回用户权限 0或1
						return authority;
					}
					else {
						log.info(request.getRemoteAddr()+" 用户"+userName+"登陆时密码错误");
						return -427;
					}
				}
			
			}
	 
		}					
					
	}

	//将Java对象转为json字符串
	public String convertToJson(Object obj)
	{
		JSONObject json = JSONObject.fromObject(obj);//将java对象转换为json对象  
        String str = json.toString();//将json对象转换为字符串  
          
        return str; 
	}
	
	//判断是否含有非法字符
	public boolean checkEspecialCode( String code )
	{
		boolean flag = false;
		
		/*if( code == "" || code == null )
		{
			log.info(code+"为空或者null");
			return true;
		}*/
		
		String regExpress = "[`~!#$%^&*()+=|{}':;',\\[\\]<>/?~！#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern regex = Pattern.compile(regExpress);
		Matcher matcher = regex.matcher(code);
		flag = matcher.find();
		if(flag)
		{
			log.info("参数"+code+"含有非法字符");
			return true;
		}
		return flag;
	}
	//判断是否含有非法字符，批量判断
	public boolean checkEspecialCode(Object[] objects)
	{
			boolean flag = false;
			String regExpress = "[`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
			Pattern regex = Pattern.compile(regExpress);
			Matcher matcher = null;
			
			if(objects != null)
			{
				for(int i=0; i<objects.length; i++)
				{
					matcher = regex.matcher(objects[i].toString());
					flag = matcher.find();
					
					//System.out.println(objects[i].toString()+"--》非法字符判断");
					//只要有一个字段含有非法字符，则返回true，后面的字段则无需进行判断了
					if( flag )//匹配成功，即含有非法字符
					{
						log.info("参数"+objects[i].toString()+"含有非法字符");
						return true;
					}
						
				}
			}
			
			return flag;
	}
	//检测是否含有中文
	public boolean checkChineseCode( String code )
	{
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]"); // 汉字编码区间
        Matcher m = p.matcher(code.toString());
        if( m.find() )//含有中文字符
        {
        	return true;
        }
        else {
			return false;
		}
	}
	
	//验证邮箱
	public boolean checkEmail(String code)
	{
			boolean flag = true;
			
			String regExpress = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(regExpress);
			Matcher matcher = regex.matcher(code);
			flag = matcher.matches();
			if(!flag)
			{
				if( !code.endsWith(".com"))
				{
					log.info("参数"+code+"为邮箱字段，但邮箱格式错误");
					flag = false;
				}
				
			}
			return flag;
		}
	//验证手机号码
	public boolean checkPhone(String code)
		{
			boolean flag = false;
			
			String regExpress = "^((13[0-9])|(15([0-3]|[5-9]))|(18[0-3,5-9]))\\d{8}$";
			Pattern regex = Pattern.compile(regExpress);
			Matcher matcher = regex.matcher(code);
			flag = matcher.matches();
			if(!flag)
			{
				log.info("参数"+code+"为手机字段，但手机格式错误");
				flag = false;
			}
			return flag;
	}
	//校验全数字类型数据
	public boolean checkNmuberAll(String code)
	{
			boolean flag = true;
			String regExpress = "\\d+";
			Pattern regex = Pattern.compile(regExpress);
			Matcher matcher = regex.matcher(code);
			flag = matcher.matches();
			
			if(!flag)
			{
				log.info("参数"+code+"含有非数字字符");
				flag = false;
			}
			return flag;
	}
	

	//验证字符串是否符合日期格式
	public boolean checkDate(String code)
	{
		int year = 0;
		int month = 0;
		int day = 0;
		
		//判断code中是否含有非法字符
		boolean flag = checkEspecialCode(code);
		if( flag || code == "" || code == null)
		{
			log.info("参数"+code+"为日期字段，但是为空，不符合要求");
			return false;
		}
		else {
			if( code.indexOf("-") != -1 )
			{
				try {
					String date[] = code.split("-");
					year = Integer.parseInt(date[0]);
					month = Integer.parseInt(date[1]);
					day = Integer.parseInt(date[2]);
					
				} catch (Exception e) {
					log.info("发生异常，参数"+code+"为日期字段，获取年、月、日时出错："+e.getMessage());
					return false;
				}
				
				if( year <= 0 || month <= 0 || month > 12 || day <= 0 || day > 31 )
				{
					return false;
				}
				else if ( (year%4 == 0 && year%100 != 0)||(year%400 == 0) ) {
					//闰年
					if(month == 2)
					{
						if(day > 29)
						{
							log.info("参数"+code+"为日期字段，但日期错误，闰年2月天数为29天");
							return false;
						}
					}
				}
				else { //平年
					if(month == 2)
					{
						if(day > 28)
						{
							log.info("参数"+code+"为日期字段，但日期错误，平年2月天数为28天");
							return false;
						}
					}
				}
				
				int mon[] = {1,3,5,7,8,10,12};
				for (int i = 0; i < mon.length; i++) 
				{
					if(month == mon[i])
					{
						if(day > 31)
						{
							log.info("参数"+code+"为日期字段，但日期错误，大月最多31天");
							return false;
						}
					}
				}
				int monb[] = {4,6,9,11};
				for (int i = 0; i < monb.length; i++) 
				{
					if(month == monb[i])
					{
						if(day > 30)
						{
							log.info("参数"+code+"为日期字段，但日期错误，小月天数最多30天");
							return false;
						}
					}
				}
				
			}
			else {
				{
					log.info("参数"+code+"为日期字段，但日期格式错误，正确格式为 yyyy-MM-DD");
					return false;
				}
			}
			
			return true;
			
		}
		
	}

	//生成一个生产批次
	public static String getBatch()
	{
		String batch = "";
		
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss mmm");//设置日期格式
		String d=df.format(date);//date转换为字符串

		//System.out.println(d);
		
		StringTokenizer tokenizer = new StringTokenizer(d,"-: ");
		for (int i = 0; tokenizer.hasMoreTokens(); i++) {
			batch += tokenizer.nextToken();
		}
		
		//System.out.println("生产批次 : " + batch);
		
		return batch;
	}
	
	//获取当前时间
	public Date getThisTime() {

		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String d=df.format(date);
		
		try {
			date = df.parse(d);
			
		} catch (ParseException e) {
			log.error("获取当前时间，并转换格式，发生异常" , e );
		}

		return date;
	}


	//将字符串转换成Date类型
	public Date dateConvert(String code)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		
		try {
			date = dateFormat.parse(code);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return date;//转化的结果 ： Tue Aug 08 00:00:00 CST 2017
		
	}
	//Date数据转字符串
	public String dateToString( Object value )
	{
		if( value == null )
		{
			return null;
		}
		String d = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			d = dateFormat.format(value);
		} catch (Exception e) {
			//log.info("Date类型转String发生异常：" , e);
		}
		
		return d;
	}
	
	//判断日期值是否合法( strart必须在end之前；如果end=null,则只验证start是否在今天之后)
	public boolean checkReportDate( String start , String end )
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date thisDate = getThisTime();
			Date startDate = dateFormat.parse(start);
			Date endDate = dateFormat.parse(end);
			
			System.out.println(dateToString(thisDate));
			// 1. 比较日期start是否在当前时间之后
			int i = thisDate.compareTo(startDate);
			if( i > 0 )//日期start在日期在当前时间之前，错误
			{
				return true;
			}
			else {
				if( end != null)
				{
					// 2. 比较start和end两个日期的大小，start不能在end之后
					int j = startDate.compareTo(endDate);
					if( j <= 0 )
						return  false;//日期start在日期end之前，合法
					else 
						return  true;//日期start在日期end之后，不合法
					
				}
				
			}
			
			
		} catch (ParseException e) {
			log.error("比较时间参数是否合法，发生异常：" , e );
		}
		
		
		return false;
	}
	//判断日期值是否合法
	public boolean checkNoteDate( String start , String end )
	{
		if( start == "" || end == "" )
		{
			log.info("有日期为空");
			return true;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date startDate = dateFormat.parse(start);
			Date endDate = dateFormat.parse(end);
			
			//  比较start和end两个日期的大小，start不能在end之后
			int j = startDate.compareTo(endDate);
			if( j <= 0 )
				return  false;//日期start在日期end之前，合法
			else 
				return  true;//日期start在日期end之后，不合法
			
		} catch (ParseException e) {
			log.error("比较时间参数是否合法，发生异常：" , e );
		}
		
		
		return false;
	}	
	
	
}
