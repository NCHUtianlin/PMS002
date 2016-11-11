package service.basicService.reponseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProductResponseService extends BasicResponseService {

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
	
	//产品
	public void productResponse(HttpServletRequest request, HttpServletResponse response , int k )
	{
		switch (k) {
		case 200:
			okResponseEmpty(response, k ,request.getRemoteAddr());
			return ;
		case -101:
			setErrorMsg( k, "参数错误parameter" , "不能输入含有非法字符" , 400 );
			break;
		case -102:
			setErrorMsg( k, "参数错误" , "deviceID不存在" , 400 );
			break;
		case -103:
			setErrorMsg( k, "参数错误" , "MAC不存在" , 400 );
			break;
		case -104:
			setErrorMsg( k, "参数错误" , "产品类型名称含有非法字符" , 400 );
			break;
		case -105:
			setErrorMsg( k, "参数错误" , "申报人姓名含有非法字符" , 400 );
			break;
		case -106:
			setErrorMsg( k, "参数错误" , "产品id含有非法字符" , 400 );
			break;
		case -107:
			setErrorMsg( k, "参数错误" , "日期格式错误", 400 );
			break;
		case -108:
			setErrorMsg( k, "参数错误" , "生产批次含有非法字符" , 400 );
			break;
		case -109:
			setErrorMsg( k, "参数错误" , "生产商名称含有非法字符" , 400 );
			break;
		case -110:
			setErrorMsg( k, "参数错误" , "产品状态数值不正确" , 400 );
			break;
		case -111:
			setErrorMsg( k, "参数错误" , "硬件版本信息含有非法字符" , 400 );
			break;
		case -112:
			setErrorMsg( k, "参数错误" , "软件版本信息含有非法字符" , 400 );
			break;
		case -113:
			setErrorMsg( k, "参数错误" , "测试结果数值不正确" , 400 );
			break;
		case -114:
			setErrorMsg( k, "参数错误" , "deviceID含有非法字符" , 400 );
		case -115:
			setErrorMsg( k, "操作失败" , "修改产品信息失败，请重试" , 403 );
			break;
		case -116:
			setErrorMsg( k, "操作失败" , "缺少必要参数" , 403 );
			break;
		case -117:
			setErrorMsg( k, "参数错误parameter" , "数值非法" , 400 );
			break;
		case -118:
			setErrorMsg( k, "没有资源" , "没有资源" , 404 );
			break;
		case -119:
			setErrorMsg( k, "操作失败" , "该产品已经被废弃，无法修改该产品数据" , 403 );
			break;
		
			
		default:
			setErrorMsg( k, "服务器错误", "服务器错误（服务器暂时不能提供服务）", 500 );
			break;
		}
		
		errorResponse(response, errorCode, errorCodeMsg, data, statusCode ,request.getRemoteAddr());
		
	}
	//测试数据、产品类型
	public void productDataResponse(HttpServletRequest request, HttpServletResponse response , int k )
	{
		switch (k) {
		case 200:
			okResponseEmpty(response, k ,request.getRemoteAddr());
			return ;
		case -151:
			setErrorMsg( k, "操作失败queryTab" , "查询目的不明确，操作无效" , 400 );
			break;
		case -152:
			setErrorMsg( k, "参数错误" , "deviceID含有非法字符" , 400 );
			break;
		case -153:
			setErrorMsg( k, "参数错误" , "产品类型名称含有非法字符" , 400 );
			break;
		case -154:
			setErrorMsg( k, "参数错误" , "硬件版本含有非法字符" , 400 );
			break;
		case -155:
			setErrorMsg( k, "参数错误" , "软件版本含有非法字符" , 400 );
			break;
		case -156:
			setErrorMsg( k, "参数错误" , "MAC数量非法" , 400 );
			break;
		case -157:
			setErrorMsg( k, "参数错误" , "产品类型的状态数值非法", 400 );
			break;
		case -158:
			setErrorMsg( k, "参数错误" , "缺少必要参数" , 400 );
			break;
		case -159:
			setErrorMsg( k, "操作失败" , "添加失败，请重试" , 403 );
			break;
		case -160:
			setErrorMsg( k, "参数错误parameter" , "不能输入非法字符" , 400 );
			break;
		case -161:
			setErrorMsg( k, "参数错误" , "日期格式错误" , 400 );
			break;
		case -162:
			setErrorMsg( k, "参数错误result" , "数值非法" , 400 );
			break;
		case -163:
			setErrorMsg( k, "参数错误productBatch" , "生产批次含有非法字符" , 400 );
			break;
		case -164:
			setErrorMsg( k, "操作失败" , "没有提供有效参数，操作无效" , 400 );
			break;
		case -165:
			setErrorMsg( k, "参数错误" , "产品类型名称长度不能超过20" , 400 );
			break;
			
		default:
			setErrorMsg( k, "服务器错误", "服务器错误（服务器暂时不能提供服务）", 500 );
			break;
		}
	
		errorResponse(response, errorCode, errorCodeMsg, data, statusCode ,request.getRemoteAddr());
		
	}
	//生产完成记录
	public void noteResponse(HttpServletRequest request, HttpServletResponse response , int k )
	{
		switch (k) {
		case 200:
			okResponseEmpty(response, k ,request.getRemoteAddr());
			return ;
		case -201:
			setErrorMsg( k, "参数错误" , "生产批次含有非法字符" , 400 );
			break;
		case -202:
			setErrorMsg( k, "参数错误productOverdueExplain" , "信息含有非法字符，请重新填写" , 400 );
			break;
		case -203:
			setErrorMsg( k, "参数错误notePerson" , "记录人id含有非法字符" , 400 );
			break;
		case -204:
			setErrorMsg( k, "参数错误" , "请将表单填写完整" , 400 );
			break;
		case -205:
			setErrorMsg( k, "参数错误" , "日期格式错误" , 400 );
			break;
		case -206:
			setErrorMsg( k, "参数错误" , "实际生产产品总数数值非法" , 400 );
			break;
		case -207:
			setErrorMsg( k, "参数错误" , "产品合格数量数值非法", 400 );
			break;
		case -208:
			setErrorMsg( k, "参数错误" , "逾期productOverdue数值非法" , 400 );
			break;
		case -209:
			setErrorMsg( k, "操作失败" , "该批次信息已存在，不能重复添加" , 403 );
			break;
		case -210:
			setErrorMsg( k, "操作失败r" , "添加信息失败，请重试" , 403 );
			break;
		case -211:
			setErrorMsg( k, "操作失败" , "信息不存在，操作无效" , 403 );
			break;
		case -212:
			setErrorMsg( k, "参数错误" , "实际生产开始时间应该在完成时间之前" , 403 );
			break;
		case -213:
			setErrorMsg( k, "参数错误" , "生产批次含有中文字符" , 403 );
			break;
			
		default:
			setErrorMsg( k, "服务器错误", "服务器错误（服务器暂时不能提供服务）", 500 );
			break;
		}

		errorResponse(response, errorCode, errorCodeMsg, data, statusCode ,request.getRemoteAddr());
		
	}
	//导入导出
	public void exportResponse(HttpServletRequest request, HttpServletResponse response , int k )
	{
		switch (k) {
		case 200:
			okResponseEmpty(response, k ,request.getRemoteAddr());
			return ;
		case -251:
			setErrorMsg( k, "参数错误" , "参数type错误，无法提供服务" , 400 );
			break;
		case -252:
			setErrorMsg( k, "参数错误" , "没有数据，暂时无法提供服务" , 400 );
			break;
		case -253:
			setErrorMsg( k, "参数错误" , "参数type含有非法字符" , 400 );
			break;
		case -254:
			setErrorMsg( k, "参数错误" , "参数productBatch含有非法字符" , 400 );
			break;
		case -255:
			setErrorMsg( k, "操作失败" , "导出数据失败，请稍后重试" , 403 );
			break;
		case -256:
			setErrorMsg( k, "操作失败" , "数据发送失败，请稍后重试" , 403 );
			break;
		case -257:
			setErrorMsg( k, "操作失败" , "请提供参数productBatch", 403 );
			break;
		case -258:
			setErrorMsg( k, "操作失败" , "功能未完善，无法提供服务", 403 );
			break;
		case -259:
			setErrorMsg( k, "操作失败" , "没有提供参数type,拒绝请求", 403 );
			break;
		
		default:
			setErrorMsg( k, "服务器错误", "服务器错误（服务器暂时不能提供服务）", 500 );
			break;
		}

		errorResponse(response, errorCode, errorCodeMsg, data, statusCode ,request.getRemoteAddr());
		
	}

	
}