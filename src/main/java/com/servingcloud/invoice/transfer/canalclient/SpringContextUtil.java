package com.servingcloud.invoice.transfer.canalclient;

import org.springframework.context.ApplicationContext;

/**
 * spring上下文工具类
 */
public class SpringContextUtil {  
      
    private static ApplicationContext applicationContext;  
  
    public static ApplicationContext getApplicationContext() {
        return applicationContext;  
    }  
  
    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;  
    }  
  
    public static Object getBean(String name){
        return applicationContext.getBean(name);  
    }  
      
    public static Object getBean(Class<?> requiredType){
        return applicationContext.getBean(requiredType);  
    }  
  
}  