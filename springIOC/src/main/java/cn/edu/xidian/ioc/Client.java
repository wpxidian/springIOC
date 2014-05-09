package cn.edu.xidian.ioc;

import cn.edu.xidian.bean.User;
import cn.edu.xidian.beanFactory.BeanFactory;

public class Client {
	
	public static void main(String[] args){
		
		BeanFactory factory = new BeanFactory() ;
		factory.init("applicationContext.xml") ;
		
		User user = (User) factory.getBean("user") ;
		
		System.out.println(user.getAge());
	}
}
