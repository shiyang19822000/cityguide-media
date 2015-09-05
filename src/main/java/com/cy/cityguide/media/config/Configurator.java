package com.cy.cityguide.media.config;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.druid.pool.DruidDataSource;
import com.cychina.platform.id.GeneratorFactory;
import com.cychina.platform.id.IdGenerator;

@Configuration
@EnableSpringConfigured
@EnableTransactionManagement
@PropertySource(value = { "classpath:database.properties", "classpath:druid.properties", "classpath:global.properties" })
@ComponentScan(basePackages = { "com.cy.cityguide.media.dao.impl", "com.cy.cityguide.media.service.impl", "com.cy.cityguide.media.exception" })
public class Configurator {

	private Logger logger = Logger.getLogger(Configurator.class);

	@Autowired
	private Environment environment;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Bean(name = "dataSource")
	public DruidDataSource createDruidDataSource() {
		try {
			DruidDataSource druidDataSource = new DruidDataSource();
			druidDataSource.setUrl(environment.getProperty("database_url", String.class));
			druidDataSource.setUsername(environment.getProperty("database_username", String.class));
			druidDataSource.setPassword(environment.getProperty("database_password", String.class));
			druidDataSource.setInitialSize(environment.getProperty("druid_initial_size", Integer.class));
			druidDataSource.setMinIdle(environment.getProperty("druid_min_idle", Integer.class));
			druidDataSource.setMaxActive(environment.getProperty("druid_max_active", Integer.class));
			druidDataSource.setMaxWait(environment.getProperty("druid_max_wait", Long.class));
			druidDataSource.setTimeBetweenEvictionRunsMillis(environment.getProperty("druid_time_between_eviction_runs_millis", Long.class));
			druidDataSource.setMinEvictableIdleTimeMillis(environment.getProperty("druid_min_evictable_idle_time_millis", Long.class));
			druidDataSource.setValidationQuery(environment.getProperty("druid_validation_query", String.class));
			druidDataSource.setTestWhileIdle(environment.getProperty("druid_test_while_idle", Boolean.class));
			druidDataSource.setTestOnBorrow(environment.getProperty("druid_test_on_borrow", Boolean.class));
			druidDataSource.setTestOnReturn(environment.getProperty("druid_test_on_return", Boolean.class));
			druidDataSource.setPoolPreparedStatements(environment.getProperty("druid_pool_prepared_statements", Boolean.class));
			druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(environment.getProperty("druid_max_pool_prepared_statement_per_connection_size", Integer.class));
			druidDataSource.setFilters(environment.getProperty("druid_filters", String.class));
			return druidDataSource;
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory createSqlSessionFactory() {
		try {
			SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
			factoryBean.setConfigLocation(new ClassPathResource("mybatis/Config.xml"));
			factoryBean.setDataSource(dataSource);
			return factoryBean.getObject();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Bean(name = "sqlSessionTemplate")
	public SqlSessionTemplate createSqlSessionTemplate() {
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
		return sqlSessionTemplate;
	}
	
	@Bean(name = "transactionManager")
	public DataSourceTransactionManager createDataSourceTransactionManager() {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
		dataSourceTransactionManager.setDataSource(dataSource);
		return dataSourceTransactionManager;
	}
	
	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver createCommonsMultipartResolver() {
		try {
			CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
			commonsMultipartResolver.setDefaultEncoding(environment.getProperty("global_file_upload_defaultencoding", String.class));
			commonsMultipartResolver.setMaxInMemorySize(environment.getProperty("global_file_upload_maxinmemorysize", Integer.class));
			commonsMultipartResolver.setMaxUploadSize(environment.getProperty("global_file_upload_maxuploadsize", Long.class));
			Resource uploadTempDir = new FileSystemResource(environment.getProperty("global_file_upload_uploadtempdir", String.class));
			commonsMultipartResolver.setUploadTempDir(uploadTempDir);
			return commonsMultipartResolver;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Bean(name= "idGenerator")
	public IdGenerator idGenerator() {
		IdGenerator idGenerator = GeneratorFactory.DEFAULT.create();
		return idGenerator;		
//		idGenerator.nextId().toString()
	}

}
