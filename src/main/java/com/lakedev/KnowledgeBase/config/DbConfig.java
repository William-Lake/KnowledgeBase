package com.lakedev.KnowledgeBase.config;

import java.nio.file.Paths;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories(basePackages = "com.lakedev.KnowledgeBase.repository")
public class DbConfig
{
	
	
	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource()
	{
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		dataSource.setUrl("jdbc:sqlite:" + Paths.get("knowledge.db").toAbsolutePath().toString());
		
		dataSource.setUsername(env.getProperty("user"));
		
		dataSource.setPassword(env.getProperty("password"));

		dataSource.setDriverClassName(env.getProperty("driverClassName"));
		
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory()
	{
		final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		
		em.setDataSource(dataSource());
		
		em.setPackagesToScan(new String[] { "com.lakedev.KnowledgeBase.model" });
		
		em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		
		em.setJpaProperties(additionalProperties());
		
		return em;
	}

	final Properties additionalProperties()
	{
		final Properties hibernateProperties = new Properties();
		
		if (env.getProperty("hibernate.hbm2ddl.auto") != null)
		{
			hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		}
		
		if (env.getProperty("hibernate.dialect") != null)
		{
			hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		}
		
		if (env.getProperty("hibernate.show_sql") != null)
		{
			hibernateProperties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		}
		
		return hibernateProperties;
	}

}

@Configuration
@Profile("sqlite")
@PropertySource("classpath:persistence-sqlite.properties")
class SqliteConfig
{
}