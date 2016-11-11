package excel.excelTemplate;

public class AnalysisProducer {

	private String producer;//生产商
	private int totalQuantity;//实际生产总数
	private double passRate;//产品合格率
	private double testRate;//产品测试率
	private double perTime;//单产时间
	
	public AnalysisProducer()
	{
		
	}
	public AnalysisProducer(String producer,int totalQuantity,double passRate,double testRate,double perTime)
	{
		this.producer = producer;
		this.totalQuantity = totalQuantity;
		this.passRate = passRate;
		this.testRate = testRate;
		this.perTime = perTime;
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
