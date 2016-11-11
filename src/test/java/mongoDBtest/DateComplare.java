package mongoDBtest;

import service.interfaceService.BasicImplementService;

public class DateComplare {

	public static void  main(String[] agrs) {
		BasicImplementService imp = new BasicImplementService();
		boolean flag = imp.checkNoteDate("2011-10-2", "2016-12-23");
		System.out.println(flag);
	}
}
