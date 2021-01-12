package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

import com.techelevator.tenmo.model.Account;

public interface AccountDAO {
	
	Account getByUserID(long userId);
	
	Account getByAccountID(long accountId);
	
	void updateBalance(Account account);

}
