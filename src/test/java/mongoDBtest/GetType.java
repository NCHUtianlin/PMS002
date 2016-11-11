package mongoDBtest;

import java.sql.Date;

import dao.basic.BasicImplentsDao;
import domain.ProductType;
import domain.Report;

public class GetType {

	
	public static void getType(Object obj)
	{
		System.out.println("你的类型是 "+ obj.getClass().getName());
		
	}
	
	public static void main(String[] args)
	{
		
		String string = "天林";
		int age = 22;
		
		getType(string);
		getType(age);
	
		
	}

		
}
