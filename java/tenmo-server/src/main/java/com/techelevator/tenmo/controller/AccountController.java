package com.techelevator.tenmo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {
	
	private AccountDAO accountDao;
	
	private TransferDAO transferDao;
	
	private UserDAO userDao;

	public AccountController(AccountDAO accountDao, TransferDAO transferDao, UserDAO userDao) {
		this.accountDao = accountDao;
		this.transferDao = transferDao;
		this.userDao = userDao;
	}
	
	@RequestMapping(path = "/account/{userId}", method = RequestMethod.GET)
	public Account getAccountByUserId(@PathVariable long userId) {
		return accountDao.getByUserID(userId);
	}
	
	@RequestMapping(path = "/account", method = RequestMethod.PUT)
	public void updateBalance(@RequestBody Account account) {
		accountDao.updateBalance(account);
	}
	
	@RequestMapping(path = "/accounts/{accountId}", method = RequestMethod.GET)
	public String getUsernameByAccountID(@PathVariable long accountId) {
		Account account = accountDao.getByAccountID(accountId);
		return userDao.findUsernameById(account.getUserId());
	}
	
	@RequestMapping(path = "/transfers", method = RequestMethod.POST)
	public void initiateTransfer(@RequestBody Transfer transfer) {
		Account accountFrom = accountDao.getByAccountID(transfer.getAccountFrom());
		Account accountTo = accountDao.getByAccountID(transfer.getAccountTo());
		if (accountFrom.getBalance().compareTo(transfer.getAmount()) >= 0 && transfer.getTransferType() == 2) {
			transfer.setTransferStatus(2);
			accountFrom.setBalance(accountFrom.getBalance().subtract(transfer.getAmount()));
			accountTo.setBalance(accountTo.getBalance().add(transfer.getAmount()));
			accountDao.updateBalance(accountFrom);
			accountDao.updateBalance(accountTo);
		} else if (transfer.getTransferType() == 2) {
			transfer.setTransferStatus(3);
		} else {
			transfer.setTransferStatus(1);
		}
		transferDao.initiateTransfer(transfer);
	}

	
	@RequestMapping(path = "/account/{userId}/transfers", method = RequestMethod.GET)
	public List<Transfer> getTransfersByAccountId(@PathVariable long userId) {
		return transferDao.getByAccountId(accountDao.getByUserID(userId).getAccountId());
	}

	
	@RequestMapping(path = "/account/{userId}/transfers/{transferId}", method = RequestMethod.GET)
	public Transfer getDetailsByTransferId(@PathVariable long transferId) {
		return transferDao.getByTransferId(transferId);
	}
	
	@RequestMapping(path = "/transfers/status/{id}", method = RequestMethod.GET)
	public String getTransferStatus(@PathVariable int id) {
		return transferDao.getTransferStatus(id);
	}
	
	@RequestMapping(path = "/transfers/type/{id}", method = RequestMethod.GET)
	public String getTransferType(@PathVariable int id) {
		return transferDao.getTransferType(id);
	}
	
	@RequestMapping(path = "/accounts", method = RequestMethod.GET)
	public List<User> getAllUsers() {
		return userDao.findAll();
	}
	
	@RequestMapping(path = "/account/{userId}/transfers/pending", method = RequestMethod.GET)
	public List<Transfer> getPendingTransfersByAccountId(@PathVariable long userId) {
		return transferDao.getPendingByAccountId(accountDao.getByUserID(userId).getAccountId());
	}
	
	@RequestMapping(path = "/transfers", method = RequestMethod.PUT)
	public void updateTransferStatus(@RequestBody Transfer transfer) {
		if (transfer.getTransferStatus() == 2) {
			Account accountFrom = accountDao.getByAccountID(transfer.getAccountFrom());
			Account accountTo = accountDao.getByAccountID(transfer.getAccountTo());
			accountFrom.setBalance(accountFrom.getBalance().subtract(transfer.getAmount()));
			accountTo.setBalance(accountTo.getBalance().add(transfer.getAmount()));
			accountDao.updateBalance(accountFrom);
			accountDao.updateBalance(accountTo);
		}
		transferDao.updateTransferStatus(transfer);
	}

}
