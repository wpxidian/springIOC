package cn.edu.xidian.beanFactory;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class BeanFactory {
	
	private Map<String,Object> beanMap = new HashMap<String,Object>() ;
	
	/**
	 * 初始化 BeanFactory
	 * @param xml
	 */
	public void init(String xml) {
		
		try{
			//读取指定的配置文件
			SAXReader  reader = new SAXReader() ;
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader() ;
			InputStream ins = classLoader.getResourceAsStream(xml) ;
			//获取XML文件的根元素
			Document docment = reader.read(ins) ;
			Element root = docment.getRootElement() ;
			Element element1 ;
			//解析bean元素
			for(Iterator i = root.elementIterator("bean");i.hasNext();){
				
				element1 = (Element) i.next() ;
				 //获取bean的属性id和class
				Attribute id = element1.attribute("id") ;
				Attribute cls = element1.attribute("class") ;
				 //利用Java反射机制，通过class的名称获取Class对象
				Class<?> beanClass = Class.forName(cls.getText()) ;
				Object bean = beanClass.newInstance() ;
				//解析property元素
				for(Iterator ite = element1.elementIterator("property");ite.hasNext();){
					
					Element element2 = (Element) ite.next() ;
					Attribute name = element2.attribute("name") ;
					String propName = name.getText() ;
					String value = null ;
					
					for(Iterator iter = element2.elementIterator("value");iter.hasNext();){
						
						Element node = (Element) iter.next() ;
						value = node.getText() ;
						//获取属性的类型
						Field field = beanClass.getDeclaredField(propName) ;
						Type fieldType = field.getType() ;
						//获取属性对应 的setter方法
						Method method = beanClass.getMethod(getMethodName(propName),field.getType()) ;
						//将配置的value转换成属性的类型。调用属性对应的setter方法
						if(fieldType == String.class){
							method.invoke(bean, value) ;
						}else if(fieldType == int.class){
							method.invoke(bean, getInt(value)) ;
						}else if(fieldType == float.class){
							method.invoke(bean, getFloat(value)) ;
						}else if(fieldType == double.class){
							method.invoke(bean, getDouble(value)) ;
						}else if(fieldType == boolean.class){
							method.invoke(bean, getBoolean(value)) ;
						}else{
							throw new Exception("The type of property is unsupported") ;
						}
					}
				}
				//将创建的对象添加到HashMap中
				beanMap.put(id.getText(), bean) ;
			}
		}catch(Exception e){
			
		}
	}
	/**
	 * 获取容器中的bean
	 * @param name
	 * @return
	 */
	public Object getBean(String name){
		return beanMap.get(name) ;
	}
	
	/**
	 * 获取属性的Setter方法
	 * @param prop
	 * @return
	 */
	private String getMethodName(String prop){
		String firstChar = prop.substring(0,1) ;
		String str =  "set" + firstChar.toUpperCase() + prop.substring(1);
		return str ;
	}

	private int getInt(String prop){
		return Integer.parseInt(prop) ;
	}
	private float getFloat(String prop){
		return Float.parseFloat(prop) ;
	}
	private double getDouble(String prop){
		return Double.parseDouble(prop) ;
	}
	private boolean getBoolean(String prop){
		return Boolean.parseBoolean(prop) ;
	}
}
