package domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "userTable")
public class User {
	
	@Id
	private String _id;
	private String name;
	private String phone;
	private String password;
	private String email;
	private int authority;
	private int workState;
	private byte[] preface;
	
	public byte[] getPreface() {
		return preface;
	}
	public void setPreface(byte[] preface) {
		this.preface = preface;
	}
	//测试一下
	public User() {
		
	}
	public User( String _id , String name , String phone , String password , String email , int authority 
			 , int workState , byte[] preface )
	{
		this._id = _id;
		this.name = name;
		this.phone = phone;
		this.password = password;
		this.email = email;
		this.authority = authority;
		this.workState = workState;
		this.preface = preface;
	}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getAuthority() {
		return authority;
	}
	public void setAuthority(int authority) {
		this.authority = authority;
	}
	public int getWorkState() {
		return workState;
	}
	public void setWorkState(int workState) {
		this.workState = workState;
	}
	
	public String toJson()
	{
		return "{ _id:"+_id+", name:\'"+name+"\', phone:\'"+phone+"\',"
				+ "email:\'"+email+"\', authority:\'"+authority+"\', workState:\'"+workState+"\'}";
	}

}
