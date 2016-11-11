package domain;

public class DataAnalysis {
	
	
	private int num ;//信息数量
	private String productBatch;//批次
	private String productTypeName;//产品类型名称
	private String producer;//生产商
	private int totalQuantity;//实际生产总数
	private double passRate;//产品通过率
	private double testRate;//产品测试率
	private double perTime;//单产时间
	
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getProductBatch() {
		return productBatch;
	}
	public void setProductBatch(String productBatch) {
		this.productBatch = productBatch;
	}
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public int getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
	public double getPassRate() {
		return passRate;
	}
	public void setPassRate(double passRate) {
		this.passRate = passRate;
	}
	public double getTestRate() {
		return testRate;
	}
	public void setTestRate(double testRate) {
		this.testRate = testRate;
	}
	public double getPerTime() {
		return perTime;
	}
	public void setPerTime(double perTime) {
		this.perTime = perTime;
	}
	
	

}
