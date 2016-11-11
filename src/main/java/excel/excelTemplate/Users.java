package excel.excelTemplate;

public class Users {

	private String name;
	private String phone;
	//private String password;
	private String email;
	private String authority;
	private String workState;
	//private byte[] preface;
	
	/*public byte[] getPreface() {
		return preface;
	}
	public void setPreface(byte[] preface) {
		this.preface = preface;
	}*/
	//测试一下
	public Users() {
		
	}
	public Users( String name , String phone , String email , String authority 
			 , String workState )
	{
		
		this.name = name;
		this.phone = phone;
		//this.password = password;
		this.email = email;
		this.authority = authority;
		this.workState = workState;
		//this.preface = preface;
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getWorkState() {
		return workState;
	}
	public void setWorkState(String  workState) {
		this.workState = workState;
	}
	
	
}
