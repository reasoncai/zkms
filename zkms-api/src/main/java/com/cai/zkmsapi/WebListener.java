package com.cai.zkmsapi;

import dom.cai.zkms.ServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

/**
 * Created by reason on 17/5/29.
 */
@Component
public class WebListener implements ServletContextListener {
    @Value("${server.address}")
    private String serverAddress;
    @Value("${server.port}")
    private int serverPort;
    @Autowired
    private ServiceRegistry serviceRegistry;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //获取请求映射
        ServletContext servletContext = servletContextEvent.getServletContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
        for (RequestMappingInfo info:handlerMethods.keySet()
             ) {
            String serviceName = info.getName();
            if(serviceName!=null){
                //注册服务
                serviceRegistry.register(serviceName,String.format("%s:%d",serverAddress,serverPort));
            }
        }


    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
