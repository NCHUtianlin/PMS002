package service.basicService.reponseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PublicResponse extends BasicResponseService {

	private int errorCode = 0;
	private String errorCodeMsg = "";
	private String data = ""; 
	private int statusCode;
	
	private void setErrorMsg( int errorCode , String errorCodeMsg , String data , int statusCode )
	{
		this.errorCode = errorCode;
		this.errorCodeMsg = errorCodeMsg;
		this.data = data;
		this.statusCode = statusCode;
	}
	
	public void publicResponse(HttpServletRequest request, HttpServletResponse response , int k )
	{
		switch (k) {
			case -421:
				setErrorMsg( k, "账户信息错误" , "没有账户信息，无法识别用户身份" , 401 );
				break;
			case -422:
				setErrorMsg( k, "账户信息错误" , "未提供账户名，无法识别用户身份" , 403 );
				break;
			case -423:
				setErrorMsg( k, "账户信息错误" , "未提供密码，非法访问" , 403 );
				break;
			case -424:
				setErrorMsg( k, "账户信息错误" , "账户名存在非法字符" , 403 );
				break;
			case -425:
				setErrorMsg( k, "账户信息错误" , "密码含有非法字符" , 403 );
				break;
			case -426:
				setErrorMsg( k, "账户信息错误" , "账户名错误" , 403 );
				break;
			case -427:
				setErrorMsg( k, "账户信息错误" , "密码错误", 403 );
				break;
			case -428:
				setErrorMsg( k, "权限不足" , "权限不足" , 403 );
				break;
			case -429:
				setErrorMsg( k, "权限非法" , "权限非法" , 403 );
				break;
			case -430:
				setErrorMsg( k, "缺少必要参数" , "json数据为空" , 403 );
				break;
			case -431:
				setErrorMsg( k, "请求格式错误" , "请提供json数据" , 415 );
				break;
			case -432:
				setErrorMsg( k, "重复提交" , "表单重复提交" , 403 );
				break;
			case -433:
				setErrorMsg( k, "参数类型错误" , "当前页码pageNow不存在" , 400 );
				break;
			case -434:
				setErrorMsg( k, "参数类型错误" , "每页数目pageSize无效" , 400 );
				break;
			case -435:
				setErrorMsg(k, "操作失败", "您已在其他设备登陆", k);
				break;
				
			case 200:
				okResponseEmpty(response, k ,request.getRemoteAddr());
				return ;
				
			case 400:
				setErrorMsg( k, "请求参数错误" , "请求参数错误，无法理解" , 400 );
				break;
			case 401:
				setErrorMsg( k, "身份验证失败" , "身份验证失败，拒绝请求" , 401 );
				break;
			case 403:
				setErrorMsg( k, "拒绝请求" , "拒绝请求" , 403 );
				break;
			case 404:
				setErrorMsg( k, "没有资源" , "请求失败，资源在服务器中未找到" , 404 );
				break;
			case 414:
				setErrorMsg( k, "拒绝请求" , "请求的URL长度过长，拒绝请求", 414 );
				break;
			case 415:
				setErrorMsg( k, "拒绝请求" , "请求格式错误，拒绝请求" , 415 );
				break;
			case 500:
				setErrorMsg(500, "服务器错误", "服务器错误（服务器暂时不能提供服务）", 500 );
				break;
			case 503:
				setErrorMsg( k, "服务器错误" , "服务器维修中或过载，当前无法处理请求" , 503 );
				break;
			case 505:
				setErrorMsg( k, "拒绝请求" , "服务器不支持当前请求的HTTP版本" , 505 );
				break;
			
			default:
				setErrorMsg(500, "服务器错误", "服务器错误（服务器暂时不能提供服务）", 500 );
				break;
				
		}
		
		errorResponse(response, errorCode, errorCodeMsg, data, statusCode ,request.getRemoteAddr());
		
	}
	
}
