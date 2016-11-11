package domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "productTable" )
public class Product {
	
	@Id
	private String _id;
	private String deviceID;
	private String[] MAC;
	private int productTypeID;
	private String productBatch;
	private int productState;
	
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getDeviceID() {
		return deviceID;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public String[] getMAC() {
		return MAC;
	}
	public void setMAC(String[] mAC) {
		MAC = mAC;
	}
	public int getProductTypeID() {
		return productTypeID;
	}
	public void setProductTypeID(int productTypeID) {
		this.productTypeID = productTypeID;
	}
	public String getProductBatch() {
		return productBatch;
	}
	public void setProductBatch(String productBatch) {
		this.productBatch = productBatch;
	}
	public int getProductState() {
		return productState;
	}
	public void setProductState(int productState) {
		this.productState = productState;
	}
	
	public String toJson()
	{
		return "{ deviceID:"+deviceID+" , MAC:"+MAC+", productTypeID:"+productTypeID+", "
				+ "	productBatch:"+productBatch+", productState:"+productState+" }";
	}

}
