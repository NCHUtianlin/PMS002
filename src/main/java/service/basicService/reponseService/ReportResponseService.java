package service.basicService.reponseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ReportResponseService extends BasicResponseService {

	private int errorCode = 0;
	private String errorCodeMsg = "";
	private String data = ""; 
	private int statusCode = 0;
	
	private void setErrorMsg( int errorCode , String errorCodeMsg , String data , int statusCode )
	{
		this.errorCode = errorCode;
		this.errorCodeMsg = errorCodeMsg;
		this.data = data;
		this.statusCode = statusCode;
	}
	
	public void reportResponse(HttpServletRequest request, HttpServletResponse response , int k )
	{
		switch (k) {
		case 200:
			okResponseEmpty(response, k ,request.getRemoteAddr());
			return ;
		case -51:
			setErrorMsg( k, "缺少必要参数" , "请将表单填写完整" , 400 );
			break;
		case -52:
			setErrorMsg( k, "参数错误" , "生产商名称含有非法字符" , 400 );
			break;
		case -53:
			setErrorMsg( k, "参数错误" , "计划生产开始日期格式错误" , 400 );
			break;
		case -54:
			setErrorMsg( k, "参数错误" , "计划生产完成日期格式错误" , 400 );
			break;
		case -55:
			setErrorMsg( k, "参数错误" , "计划完成率数值非法" , 400 );
			break;
		case -56:
			setErrorMsg( k, "参数错误" , "计划生产数量数值非法" , 400 );
			break;
		case -57:
			setErrorMsg( k, "参数类型错误" , "产品类型不存在，申报无效", 400 );
			break;
		case -58:
			setErrorMsg( k, "操作失败" , "申报失败，请重试" , 400 );
			break;
		case -59:
			setErrorMsg( k, "操作失败" , "产品类型不存在，更新失败" , 403 );
			break;
		case -60:
			setErrorMsg( k, "资源不存在" , "审核无效，请重试" , 403 );
			break;
		case -61:
			setErrorMsg( k, "参数错误" , "信息含有非法字符，请重新填写" , 403 );
			break;
		case -62:
			setErrorMsg( k, "参数错误" , "缺少必要参数，更新失败" , 400 );
			break;
		case -63:
			setErrorMsg( k, "操作失败" , "更新信息失败，请重试" , 400 );
			break;
		case -64:
			setErrorMsg( k, "资源不足" , "MAC资源不足，拒绝申报" , 400 );
		case -65:
			setErrorMsg( k, "操作失败" , "deviceID申请失败，请重试" , 404 );
			break;
		case -66:
			setErrorMsg( k, "操作失败" , "申报信息不存在，更新失败" , 404 );
			break;
		case -67:
			setErrorMsg( k, "操作失败" , "此申报已经审核，不能再做修改" , 403 );
			break;
		case -68:
			setErrorMsg( k, "参数错误" , "查询字段含有非法字符" , 403 );
			break;
		case -69:
			setErrorMsg( k, "操作失败" , "查询目的不明确，操作无效" , 403 );
			break;
		case -70:
			setErrorMsg( k, "操作失败" , "产品类型不存在" , 403 );
			break;
		case -71:
			setErrorMsg( k, "参数错误" , "生产批次含有非法字符" , 403 );
			break;
		case -72:
				setErrorMsg( k, "参数错误" , "日期格式错误" , 403 );
				break;
		case -73:
			setErrorMsg( k, "操作失败" , "获取信息失败，请重试" , 403 );
			break;
		case -74:
				setErrorMsg( k, "参数错误" , "计划生产开始日期应该在今天之后、在结束日期之前" , 403 );
				break;
		case -75:
			setErrorMsg( k, "操作失败" , "MAC资源不足，无法满足申报需求" , 403 );
			break;
		case -76:
			setErrorMsg( k, "操作失败" , "申请MAC出错，请稍后重试" , 403 );
			break;
		case -77:
				setErrorMsg( k, "操作失败" , "申请deviceID出错，请稍后重试" , 403 );
			break;
				
				
		default:
			setErrorMsg( k, "服务器错误", "服务器错误（服务器暂时不能提供服务）", 500 );
			break;
		}
		
		errorResponse(response, errorCode, errorCodeMsg, data, statusCode ,request.getRemoteAddr());
		
	}
	
	
	
}
