package service.interfaceService;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;



public interface BasicInterfaceService {
	//表名
	final String userTableName = "userTable";
	final String reportTableName = "reportTable";
	final String productTableName = "productTable";
	final String productTypeTableName = "productTypeTable";
	final String productNoteTableName = "productNoteTable";
	final String MACTableName = "MACTable";
	final String testingTableName = "testingTable";
	
	//验证用户身份 ， 若返回 0 ，为普通用户，若返回 1，为管理员，若返回 -1 ，为非法用户
	public int checkUser( HttpServletRequest request );
	//判断是否含有非法字符
	public boolean checkEspecialCode(String code);
	//验证邮箱
	public boolean checkEmail(String code);
	//验证手机号码
	public boolean checkPhone(String code);
	//校验全数字类型数据
	public boolean checkNmuberAll(String code);
	//获取当前时间
	public Date getThisTime();

}
