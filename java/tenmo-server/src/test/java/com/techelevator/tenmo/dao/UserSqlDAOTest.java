package com.techelevator.tenmo.dao;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.tenmo.model.User;

class UserSqlDAOTest {
	
	private static SingleConnectionDataSource dataSource;
	private static JdbcTemplate jdbcTemplate;
	private UserDAO userDao;
	private User testUser;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		dataSource.setAutoCommit(false);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		dataSource.destroy();
	}
	
	@BeforeEach
	void setUp() throws Exception {
		userDao = new UserSqlDAO(jdbcTemplate);
		userDao.create("tester", "McTestington");
		testUser = userDao.findByUsername("tester");
	}

	@AfterEach
	void tearDown() throws Exception {
		dataSource.getConnection().rollback();
	}
	
	@Test
	public void returns_correct_userId_with_userName() {
		long actual = userDao.findIdByUsername(testUser.getUsername());
		
		Assertions.assertEquals(testUser.getId(), actual);
	}
	
	@Test
	public void returns_userName_with_userId() {
		String actual = userDao.findUsernameById(testUser.getId());
		
		Assertions.assertEquals(testUser.getUsername(), actual);
	}
	

}
