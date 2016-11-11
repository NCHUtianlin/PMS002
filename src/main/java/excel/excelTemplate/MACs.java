package excel.excelTemplate;


//MAC地址与一维条形码是一一对应
public class MACs {
	
	private String deviceID;
	private String MAC;
	private byte[] preface;
	
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public MACs(  String MAC , byte[] preface )
	{
		this.MAC = MAC;
		this.preface = preface;
	}
	
	public String getMAC() {
		return MAC;
	}
	public void setMAC(String mAC) {
		MAC = mAC;
	}
	public byte[] getPreface() {
		return preface;
	}
	public void setPreface(byte[] preface) {
		this.preface = preface;
	}

}
