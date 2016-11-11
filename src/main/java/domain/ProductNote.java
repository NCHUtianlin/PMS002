package domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document( collection = "productNoteTable" )
public class ProductNote {
	
	@Id
	private String _id;
	private String productBatch;
	private String productStartDate;
	private String productEndDate;
	private int productTotalQuantity;
	private int productUsableNumber;
	private int productOverdue;
	private String productOverdueExplain;
	private int notePerson;
	
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getProductBatch() {
		return productBatch;
	}
	public void setProductBatch(String productBatch) {
		this.productBatch = productBatch;
	}
	public String getProductStartDate() {
		return productStartDate;
	}
	public void setProductStartDate(String productStartDate) {
		this.productStartDate = productStartDate;
	}
	public String getProductEndDate() {
		return productEndDate;
	}
	public void setProductEndDate(String productEndDate) {
		this.productEndDate = productEndDate;
	}
	public int getProductTotalQuantity() {
		return productTotalQuantity;
	}
	public void setProductTotalQuantity(int productTotalQuantity) {
		this.productTotalQuantity = productTotalQuantity;
	}
	public int getProductUsableNumber() {
		return productUsableNumber;
	}
	public void setProductUsableNumber(int productUsableNumber) {
		this.productUsableNumber = productUsableNumber;
	}
	public int getProductOverdue() {
		return productOverdue;
	}
	public void setProductOverdue(int productOverdue) {
		this.productOverdue = productOverdue;
	}
	public String getProductOverdueExplain() {
		return productOverdueExplain;
	}
	public void setProductOverdueExplain(String productOverdueExplain) {
		this.productOverdueExplain = productOverdueExplain;
	}
	public int getNotePerson() {
		return notePerson;
	}
	public void setNotePerson(int notePerson) {
		this.notePerson = notePerson;
	}
	
	public String toJson()
	{
		return "{ _id:"+_id+",productBatch:"+productBatch+",productStartDate:"+productStartDate+","
				+ "productEndDate:"+productEndDate+",productTotalQuantity:"+productTotalQuantity+","
						+ "productUsableNumber:"+productUsableNumber+",productOverdue:"+productOverdue+","
								+ "productOverdueExplain:"+productOverdueExplain+",notePerson:"+notePerson+" }";
	}
	
}
