package com.techelevator.tenmo.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Account;
@Component
public class AccountSqlDAO implements AccountDAO {
	
	JdbcTemplate jdbcTemplate;
	
	public AccountSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Account getByUserID(long userId) {
		Account account = new Account();
		String sql = "SELECT * FROM accounts WHERE user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
		if(results.next()) {
			account = mapRowToAccount(results);
		}
		return account;
	}
	
	@Override
	public Account getByAccountID(long accountId) {
		Account account = new Account();
		String sql = "SELECT * FROM accounts WHERE account_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
		if(results.next()) {
			account = mapRowToAccount(results);
		}
		return account;
	}

	@Override
	public void updateBalance(Account account) {
		String sql = "UPDATE accounts SET balance = ? WHERE account_id = ? AND user_id = ?";
		jdbcTemplate.update(sql, account.getBalance(), account.getAccountId(), account.getUserId());

	}
	
	private Account mapRowToAccount(SqlRowSet results) {
		Account account = new Account();
		account.setAccountId(results.getLong("account_id"));
		account.setUserId(results.getLong("user_id"));
		account.setBalance(results.getBigDecimal("balance"));
		return account;
	}

}
