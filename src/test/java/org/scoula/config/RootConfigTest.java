package org.scoula.config;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j2
class RootConfigTest {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Test
	@DisplayName("DataSrouce 연결이 되다")
	public void dataSource() throws SQLException{
		try(Connection con = dataSource.getConnection()){
			log.info("DataSource 준비 완료");
			log.info(con);
		}
	}
	@Test
	public void testSqlSessionFactory(){
		try(
			SqlSession session = sqlSessionFactory.openSession();
			Connection con = session.getConnection();
			){
			log.info(session);
			log.info(con);
		}catch (Exception e){
			fail(e.getMessage());
		}
	}


}