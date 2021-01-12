package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

class AccountSqlDAOTest {

	private static SingleConnectionDataSource dataSource;
	private static JdbcTemplate jdbcTemplate;
	private AccountDAO accountDao;
	private UserDAO userDao;
	private User testUser;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		
		
		//disable autocommit for connections returned by this datasource
		//this allows us to rollback any changes after each test
		dataSource.setAutoCommit(false);
		
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		dataSource.destroy();
	}

	@BeforeEach
	void setUp() throws Exception {
		accountDao = new AccountSqlDAO(jdbcTemplate);
		userDao = new UserSqlDAO(jdbcTemplate);
		userDao.create("tester", "McTestington");
		testUser = userDao.findByUsername("tester");
	}

	@AfterEach
	void tearDown() throws Exception {
		dataSource.getConnection().rollback();
	}
	
	@Test
	void returns_same_account_with_user_id_or_account_id() {
		Account accountUser = null;
		accountUser = accountDao.getByUserID(testUser.getId());
		
		Assertions.assertNotNull(accountUser);
		
		Account accountAccount = null;
		accountAccount = accountDao.getByAccountID(accountUser.getAccountId());
		
		Assertions.assertNotNull(accountAccount);
		
		assertAccountsEqual(accountUser, accountAccount);
	}
	

	@Test
	void updates_balance_of_account() {
		Account account = null;
		account = accountDao.getByUserID(testUser.getId());
		
		Assertions.assertNotNull(account);
		BigDecimal expected = new BigDecimal(500.00).setScale(2);
		account.setBalance(expected);
		
		accountDao.updateBalance(account);
		
		account = accountDao.getByUserID(testUser.getId());
		
		Assertions.assertNotNull(account);
		
		Assertions.assertEquals(expected, account.getBalance());
	}

	private void assertAccountsEqual(Account accountUser, Account accountAccount) {
		Assertions.assertEquals(accountUser.getAccountId(), accountAccount.getAccountId());
		Assertions.assertEquals(accountUser.getUserId(), accountAccount.getUserId());
		Assertions.assertEquals(accountUser.getBalance(), accountAccount.getBalance());
	}

}
