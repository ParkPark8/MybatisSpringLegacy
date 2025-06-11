package org.scoula.config;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource({"classpath:/application.properties"})
@MapperScan(basePackages = {})
/***
 * Mybatis- MapperScan : Mapper인터페이스를 검색할 패키지 목록 지정
 * -> 해당 인터페이스를 빈으로 지정
 * 구현체를 동적으로 자동으로 생성
 */
public class RootConfig {
	@Autowired
	ApplicationContext applicationContext;
	@Value("${jdbc.driver}")
	String driver;
	@Value("${jdbc.url}")
	String url;
	@Value("${jdbc.username}")
	String username;
	@Value("${jdbc.password}")
	String password;

	@Bean
	public DataSource dataSource(){
		HikariConfig config = new HikariConfig();

		config.setDriverClassName(driver);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);

		HikariDataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception{
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));
		sqlSessionFactory.setDataSource(dataSource());
		return (SqlSessionFactory)sqlSessionFactory.getObject();
	}
	@Bean
	public DataSourceTransactionManager transactionManager(){
		DataSourceTransactionManager manager = new DataSourceTransactionManager(dataSource());
		return manager;
	}


}
