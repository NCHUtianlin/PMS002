package mongoDBtest;

import java.sql.Date;

import dao.basic.BasicImplentsDao;
import domain.ProductType;
import domain.Report;

public class GetType {

	
	public static void getType(Object obj)
	{
		System.out.println("��������� "+ obj.getClass().getName());
		
	}
	
	public static void main(String[] args)
	{
		
		String string = "����";
		int age = 22;
		
		getType(string);
		getType(age);
	
		
	}

		
}
