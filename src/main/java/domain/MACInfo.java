package domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "MACTable")
public class MACInfo {

	
	@Id
	private String _id;
	private int row;
	private int usableNum;
	private int[] MAC;
	
	public MACInfo()
	{}
	public MACInfo( int row , int num , int[] MAC )
	{
		this.row = row;
		this.usableNum = num;
		this.MAC = MAC;
	}
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getUsableNum() {
		return usableNum;
	}
	public void setUsableNum(int usableNum) {
		this.usableNum = usableNum;
	}
	public int[] getMAC() {
		return MAC;
	}
	public void setMAC(int[] mAC) {
		MAC = mAC;
	}
	
}
