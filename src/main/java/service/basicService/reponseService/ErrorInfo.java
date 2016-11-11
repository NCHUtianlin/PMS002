package service.basicService.reponseService;

import net.sf.json.JSONObject;

public class ErrorInfo {

	private int errorCode = 0;//错误码
	private String errorCodeMsg = "";//错误信息
	private int statusCode;//状态码
	
	public ErrorInfo() {
		
	}
	public ErrorInfo( int errorCode , String errorCodeMsg , int statusCode ) {
		this.errorCode = errorCode;
		this.errorCodeMsg = errorCodeMsg;
		this.statusCode = statusCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorCodeMsg() {
		return errorCodeMsg;
	}
	public void setErrorCodeMsg(String errorCodeMsg) {
		this.errorCodeMsg = errorCodeMsg;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public JSONObject ToJson()
	{
		ErrorInfo obj = new ErrorInfo();
		JSONObject jsonObject = JSONObject.fromObject(obj);
		System.out.println("打印JSON：");
		System.out.println(jsonObject.toString());
		return jsonObject;
	}
}
