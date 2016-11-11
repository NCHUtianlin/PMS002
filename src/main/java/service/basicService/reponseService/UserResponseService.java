package service.basicService.reponseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserResponseService extends BasicResponseService{
	
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

	public void userResponse(HttpServletRequest request, HttpServletResponse response , int k )
	{
		switch (k) {
			case 200:
				okResponseEmpty(response, k ,request.getRemoteAddr());
				return ;
			case -10:
				setErrorMsg( k, "已注册" , "该手机号已经注册，请确认您的手机号是否输入正确" , 403 );
				break;
			case -13:
				setErrorMsg( k, "缺少必要参数" , "请将表单填写完整" , 400 );
				break;
			case -14:
				setErrorMsg( k, "参数类型错误" , "姓名含有非法字符" , 400 );
				break;
			case -15:
				setErrorMsg( k, "参数类型错误" , "密码含有非法字符" , 400 );
				break;
			case -16:
				setErrorMsg( k, "参数类型错误" , "无效手机号" , 400 );
				break;
			case -17:
				setErrorMsg( k, "参数类型错误" , "邮箱格式错误" , 400 );
				break;
			case -18:
				setErrorMsg( k, "参数类型错误" , "权限authority数值不正确", 400 );
				break;
			case -19:
				setErrorMsg( k, "参数类型错误" , "工作状态workState数值不正确" , 400 );
				break;
			case -20:
				setErrorMsg( k, "操作失败" , "用户不存在，删除操作无效" , 403 );
				break;
			case -21:
				setErrorMsg( k, "资源不存在" , "用户不存在" , 403 );
				break;
			case -22:
				setErrorMsg( k, "非法行为" , "非法行为，操作无效" , 403 );
				break;
			/*case -23:
				setErrorMsg( k, "参数类型错误" , "当前页码pageNow不存在" , 400 );
				break;
			case -24:
				setErrorMsg( k, "参数类型错误" , "每页数目pageSize无效" , 400 );
				break;*/
			case -25:
				setErrorMsg( k, "没有查询标记参数queryTab" , "查询目的不明确，操作无效" , 400 );
				break;
			case -26:
				setErrorMsg( k, "参数类型错误" , "用户id含有非法字符" , 400 );
				break;
			case -27:
				setErrorMsg( k, "操作失败" , "添加用户失败" , 403 );
				break;
			case -28:
				setErrorMsg( k, "参数错误" , "查询字段含有非法字符" , 403 );
				break;
			case -29:
				setErrorMsg( k, "参数错误" , "没有提供查询参数" , 403 );
				break;
			case -30:
				setErrorMsg( k, "操作失败" , "删除用户失败" , 403 );
				break;
			case -31:
				setErrorMsg( k, "操作失败" , "修改用户信息失败" , 403 );
				break;
				
			default:
				setErrorMsg( k , "服务器错误", "服务器错误（服务器暂时不能提供服务）", 500 );
				break;
				
		}
		
		errorResponse(response, errorCode, errorCodeMsg, data, statusCode ,request.getRemoteAddr());
		
	}
	
	
	
}
