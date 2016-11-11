package excel.excelTemplate;

public class Products {
	
	private String deviceID;
	private String productTypeName;
	private String[] MAC;
	
	
	public Products() {
		
	}
	public Products(String deviceID , String[] MAC , String productTypeName )
	{
		this.deviceID = deviceID;
		this.MAC = MAC;
		this.productTypeName = productTypeName;
		
	}
	
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public String[] getMAC() {
		return MAC;
	}
	public void setMAC(String[] mAC) {
		MAC = mAC;
	}
	

}
