package mongoDBtest;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCovert {

	public static void main(String[] args)
	{
		System.out.println(dateConvert("2016-6-6"));
		
		getThisTime();
	}
	//将字符串转换成Date类型
	public static Date dateConvert(String code)
	{
		System.out.println(code);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		
		try {
			date = dateFormat.parse(code);
			System.out.println(dateFormat.format(date));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return date;//转化的结果 ： Tue Aug 08 00:00:00 CST 2017
		
	}		
		
	//获取当前时间
		public static void getThisTime() {

			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");//设置日期格式
			String d=df.format(date);
			
			try {
				date = df.parse(d);
				
			} catch (ParseException e) {
				//log.error("获取当前时间，并转换格式，发生异常" , e );
			}

			System.out.println(date);
		}
	
}
