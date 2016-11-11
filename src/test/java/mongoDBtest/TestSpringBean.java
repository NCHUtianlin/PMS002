package mongoDBtest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import domain.Person;

public class TestSpringBean {
	
	public static void main(String[] args)
	{
		ApplicationContext aContext = new ClassPathXmlApplicationContext("config/spring.xml");
		Person person = (Person) aContext.getBean("person");
		System.out.println("name ="+person.getName()+" , phone = "+person.getPhone());
		
	}

}
