package com.cy.cityguide.media.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.ibatis.logging.LogFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.Log4jConfigListener;

import com.alibaba.druid.support.http.StatViewServlet;

public class Initializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) throws ServletException {
		
		container.setInitParameter("webAppRootKey", "cityguide-media");	
		container.setInitParameter("log4jConfigLocation", "classpath:log4j.properties");
		container.addListener(Log4jConfigListener.class);
		LogFactory.useLog4JLogging();
		
		FilterRegistration.Dynamic characterEncodingFilter = container.addFilter("CharacterEncodingFilter", CharacterEncodingFilter.class);
		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		characterEncodingFilter.setInitParameter("forceEncoding", "true");
		characterEncodingFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
		
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(Configurator.class);
		container.addListener(new ContextLoaderListener(context));
		
		AnnotationConfigWebApplicationContext dispatcherServletContext = new AnnotationConfigWebApplicationContext();
		dispatcherServletContext.register(MVCConfigurator.class);
		ServletRegistration.Dynamic dispatcherServlet = container.addServlet("DispatcherServlet", new DispatcherServlet(dispatcherServletContext));
		dispatcherServlet.setInitParameter("throwExceptionIfNoHandlerFound", "true");
		dispatcherServlet.setLoadOnStartup(1);
		dispatcherServlet.addMapping("/");
		
		ServletRegistration.Dynamic statViewServlet = container.addServlet("StatViewServlet", StatViewServlet.class);
		statViewServlet.setInitParameter("allow", "127.0.0.1");
		statViewServlet.addMapping("/druid/*");
		
	}

}
