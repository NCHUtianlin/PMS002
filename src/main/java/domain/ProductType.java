package domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "productTypeTable" )
public class ProductType {
	
	@Id
	private String _id;
	private String productTypeName;
	private String hardwareVersion;
	private String softwareVersion;
	private int macNumber;
	private int productTypeState;

	
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public String getHardwareVersion() {
		return hardwareVersion;
	}
	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	public int getMacNumber() {
		return macNumber;
	}
	public void setMacNumber(int macNumber) {
		this.macNumber = macNumber;
	}
	public int getProductTypeState() {
		return productTypeState;
	}
	public void setProductTypeState(int productTypeState) {
		this.productTypeState = productTypeState;
	}
	
	
}
