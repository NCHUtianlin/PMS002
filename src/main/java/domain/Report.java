package domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reportTable")
public class Report {
	
	@Id
	private String _id;
	private String productTypeID;
	private String productBatch;
	private int reportQuantity;
	private Date reportStartDate;
	private Date reportEndDate;
	private double reportCompleteRate;
	private String producer;
	private Date reportTime;
	private int reportPerson;
	private int checkPerson;
	private Date checkDate;
	private int checkResult;

	//测试一下
	public Report() {
		
	}
	public Report(String productBatch , int reportQuantity )
	{
		this.productBatch = productBatch;
		this.reportQuantity = reportQuantity;
		
	}
	
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getProductTypeID() {
		return productTypeID;
	}
	public void setProductTypeID(String productTypeID) {
		this.productTypeID = productTypeID;
	}
	public String getProductBatch() {
		return productBatch;
	}
	public void setProductBatch(String productBatch) {
		this.productBatch = productBatch;
	}
	public int getReportQuantity() {
		return reportQuantity;
	}
	public void setReportQuantity(int reportQuantity) {
		this.reportQuantity = reportQuantity;
	}
	
	public double getReportCompleteRate() {
		return reportCompleteRate;
	}
	public void setReportCompleteRate(double reportCompleteRate) {
		this.reportCompleteRate = reportCompleteRate;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	
	public int getReportPerson() {
		return reportPerson;
	}
	public void setReportPerson(int reportPerson) {
		this.reportPerson = reportPerson;
	}
	public int getCheckPerson() {
		return checkPerson;
	}
	public void setCheckPerson(int checkPerson) {
		this.checkPerson = checkPerson;
	}
	public Date getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	public int getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(int checkResult) {
		this.checkResult = checkResult;
	}
	public Date getReportStartDate() {
		return reportStartDate;
	}
	public void setReportStartDate(Date reportStartDate) {
		this.reportStartDate = reportStartDate;
	}
	public Date getReportEndDate() {
		return reportEndDate;
	}
	public void setReportEndDate(Date reportEndDate) {
		this.reportEndDate = reportEndDate;
	}
	public Date getReportTime() {
		return reportTime;
	}
	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}


	public String toJson()
	{
		return "{ _id:"+_id+", productTypeID:" +productTypeID+ ", productBatch:" +productBatch+ ","
				+ "reportQuantity:" +reportQuantity+ ", reportStartDate:" +reportStartDate+ ", "
						+ "reportEndDate:" +reportEndDate+ ", reportCompleteRate:" +reportCompleteRate+ ", "
								+ "producer:" +producer+ ", reportTime:" +reportTime+ ", reportPerson:" +reportPerson+ ","
										+ "checkPerson:" +checkPerson+ ", checkDate:" +checkDate+ ",checkResult:" +checkResult+ ""
												+ "}";
	}
	
}
