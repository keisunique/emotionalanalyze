package com.ke.emotionalanalyze.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * 
 * @类名 获取springbean对象工具类
 * @作者 zhengrongke
 * @日期 2017年11月14日 下午2:10:18
 * @版本号 1.0
 * @描述 springbean对象工具类，用于解决多线程bean无法注入的问题
 */
public class SpringBeanUtil implements ApplicationContextAware{

	//Spring上下文对象
	private static ApplicationContext applicationContext = null;
	
	/**
	 * 实现ApplicationContextAware接口的方法，获取到spring上下文
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {	
		SpringBeanUtil.applicationContext = applicationContext;
	}

	/**
	 * 
	 * @方法名 根据Bean的名字得到Bean对象
	 * @作者 zhengrongke
	 * @日期 2018年1月22日
	 * @版本 1.0
	 * @描述 根据Bean的名字得到Bean对象
	 * @param beanName 已经注入过的Bean对象
	 * @return Object对象，使用时要强转
	 */
	public static Object getBeanByName(String beanName) {  
        if (applicationContext == null){  
            return null;  
        }  
        return applicationContext.getBean(beanName);  
    }  
	
	/**
	 * 
	 * @方法名 根据bean的类型返回一个对象
	 * @作者 zhengrongke
	 * @日期 2018年1月22日
	 * @版本 1.0
	 * @描述 根据bean的类型返回一个对象
	 * @param type Bean类型
	 * @return 该类型的对象
	 */
    public static <T> T getBean(Class<T> type) {  
        return applicationContext.getBean(type);  
    }  
	

}
