package excel.excelTemplate;

public class AnalysisType {
	
	private String productTypeName;//产品类型名称
	private int totalQuantity;//实际生产总数
	private double passRate;//产品合格率
	private double testRate;//产品测试率
	private double perTime;//单产时间
	
	public AnalysisType()
	{
		
	}
	public AnalysisType(String productTypeName,int totalQuantity,double passRate,double testRate,double perTime)
	{
		this.productTypeName = productTypeName;
		this.totalQuantity =totalQuantity;
		this.passRate = passRate;
		this.testRate = testRate;
		this.perTime = perTime;
	}
	
	public String getProductTypeName() {
		return productTypeName;
	}
	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
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
