package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

class TransferSqlDAOTest {

	private static SingleConnectionDataSource dataSource;
	private static JdbcTemplate jdbcTemplate;
	private AccountDAO accountDao;
	private UserDAO userDao;
	private TransferDAO transferDao;
	private User testUser;
	private User testUser2;
	private Transfer sendTransfer;
	private Transfer recieveTransfer;


	
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
		transferDao = new TransferSqlDAO(jdbcTemplate);
		userDao.create("tester", "McTestington");
		userDao.create("testalia", "McTestington");
		
		testUser = userDao.findByUsername("tester");
		testUser2 = userDao.findByUsername("testalia");
		
		Account accountUser = accountDao.getByUserID(testUser.getId());
		Account accountUser2 = accountDao.getByUserID(testUser2.getId());
		
		sendTransfer = new Transfer();
		sendTransfer.setTransferType(2);
		sendTransfer.setAccountFrom(accountUser.getAccountId());
		sendTransfer.setAccountTo(accountUser2.getAccountId());
		sendTransfer.setAmount(new BigDecimal(500));
		sendTransfer.setTransferStatus(2);

		
		recieveTransfer = new Transfer();
		recieveTransfer.setTransferType(1);
		recieveTransfer.setAccountTo(accountUser.getAccountId());
		recieveTransfer.setAccountFrom(accountUser2.getAccountId());
		recieveTransfer.setAmount(new BigDecimal(500));
		recieveTransfer.setTransferStatus(1);

	}

	@AfterEach
	void tearDown() throws Exception {
		dataSource.getConnection().rollback();
	}
	
	@Test
	void initiates_a_transfer_and_logs_it() {
		transferDao.initiateTransfer(sendTransfer);
		Account accountUser = accountDao.getByUserID(testUser.getId());
		Account accountUser2 = accountDao.getByUserID(testUser2.getId());
		List<Transfer> userTransfers = transferDao.getByAccountId(accountUser.getAccountId());
		Assertions.assertNotNull(userTransfers);
		Assertions.assertEquals(1, userTransfers.size());
		
		Transfer completedTransfer = userTransfers.get(0);
		Assertions.assertEquals(sendTransfer.getAccountFrom(), completedTransfer.getAccountFrom());
		Assertions.assertEquals(sendTransfer.getAccountTo(), completedTransfer.getAccountTo());
		Assertions.assertEquals(sendTransfer.getAmount().setScale(2), completedTransfer.getAmount());
		Assertions.assertEquals(sendTransfer.getTransferType(), completedTransfer.getTransferType());

	}
	
	@Test
	void initiates_a_transfer_and_pends_it() {
		transferDao.initiateTransfer(recieveTransfer);
		Account accountUser = accountDao.getByUserID(testUser.getId());
		Account accountUser2 = accountDao.getByUserID(testUser2.getId());
		List<Transfer> userTransfers = transferDao.getPendingByAccountId(accountUser2.getAccountId());
		Assertions.assertNotNull(userTransfers);
		Assertions.assertEquals(1, userTransfers.size());
		
		Transfer completedTransfer = userTransfers.get(0);
		Assertions.assertEquals(recieveTransfer.getAccountFrom(), completedTransfer.getAccountFrom());
		Assertions.assertEquals(recieveTransfer.getAccountTo(), completedTransfer.getAccountTo());
		Assertions.assertEquals(recieveTransfer.getAmount().setScale(2), completedTransfer.getAmount());
		Assertions.assertEquals(recieveTransfer.getTransferType(), completedTransfer.getTransferType());

	}
	
	@Test
	void initiates_a_transfer_and_finds_it_by_transfer_id() {
		transferDao.initiateTransfer(recieveTransfer);
		Account accountUser = accountDao.getByUserID(testUser.getId());
		Account accountUser2 = accountDao.getByUserID(testUser2.getId());
		List<Transfer> userTransfers = transferDao.getPendingByAccountId(accountUser2.getAccountId());
		Assertions.assertNotNull(userTransfers);
		Assertions.assertEquals(1, userTransfers.size());
		
		Transfer completedTransfer = userTransfers.get(0);
		
		Transfer completedTransfer2 = transferDao.getByTransferId(completedTransfer.getTransferId());
		
		Assertions.assertEquals(completedTransfer.getAccountFrom(), completedTransfer2.getAccountFrom());
		Assertions.assertEquals(completedTransfer.getAccountTo(), completedTransfer2.getAccountTo());
		Assertions.assertEquals(completedTransfer.getAmount().setScale(2), completedTransfer2.getAmount());
		Assertions.assertEquals(completedTransfer.getTransferType(), completedTransfer2.getTransferType());
		Assertions.assertEquals(completedTransfer.getTransferId(), completedTransfer2.getTransferId());
		Assertions.assertEquals(completedTransfer.getTransferStatus(), completedTransfer2.getTransferStatus());
	}
	
	@Test
	void initiates_a_transfer_and_can_update_status() {
		transferDao.initiateTransfer(recieveTransfer);
		Account accountUser = accountDao.getByUserID(testUser.getId());
		Account accountUser2 = accountDao.getByUserID(testUser2.getId());
		List<Transfer> userTransfers = transferDao.getPendingByAccountId(accountUser2.getAccountId());
		Assertions.assertNotNull(userTransfers);
		Assertions.assertEquals(1, userTransfers.size());
		
		Transfer completedTransfer = userTransfers.get(0);
		
		completedTransfer.setTransferStatus(3);
		transferDao.updateTransferStatus(completedTransfer);
		
		Transfer completedTransfer2 = transferDao.getByTransferId(completedTransfer.getTransferId());
		
		Assertions.assertEquals(completedTransfer.getTransferStatus(), completedTransfer2.getTransferStatus());
	}
	
	

}
