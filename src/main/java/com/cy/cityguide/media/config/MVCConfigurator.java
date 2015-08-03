package com.cy.cityguide.media.config;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.cy.cityguide.media.controller")
public class MVCConfigurator extends WebMvcConfigurerAdapter {

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		jackson2HttpMessageConverter.setObjectMapper(objectMapper);
		messageConverters.add(jackson2HttpMessageConverter);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
		String[] resources = { "/js/**", "/css/**", "/image/**" };
		String[] Locations = { "/js/", "/css/", "/image/" };
		resourceHandlerRegistry.addResourceHandler(resources).addResourceLocations(Locations);
	}

	@Bean(name = "freeMarkerConfigurer")
	public FreeMarkerConfigurer createFreeMarkerConfigurer() {
		Properties freemarkerSettings = new Properties();
		freemarkerSettings.put("default_encoding", "UTF-8");
		freemarkerSettings.put("output_encoding", "UTF-8");

		FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
		freeMarkerConfigurer.setFreemarkerSettings(freemarkerSettings);
		freeMarkerConfigurer.setTemplateLoaderPath("/WEB-INF/view/");
		return freeMarkerConfigurer;
	}

	@Bean(name = "freeMarkerViewResolver")
	public FreeMarkerViewResolver createFreeMarkerViewResolver() {
		FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
		freeMarkerViewResolver.setPrefix("");
		freeMarkerViewResolver.setSuffix(".ftl");
		freeMarkerViewResolver.setCache(false);
		freeMarkerViewResolver.setContentType("text/html;charset=utf-8");
		freeMarkerViewResolver.setExposeRequestAttributes(true);
		freeMarkerViewResolver.setExposeSessionAttributes(true);
		freeMarkerViewResolver.setRequestContextAttribute("rc");
		return freeMarkerViewResolver;
	}

}
