package com.cy.cityguide.media.config;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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

}
