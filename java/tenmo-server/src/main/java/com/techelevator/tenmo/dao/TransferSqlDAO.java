package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Transfer;
@Component
public class TransferSqlDAO implements TransferDAO {
	
	JdbcTemplate jdbcTemplate;
	
	public TransferSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Transfer getByTransferId(long transferId) {
		Transfer transfer = new Transfer();
		String sql = "SELECT * FROM transfers WHERE transfer_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
		if(results.next()) {
			transfer = mapRowToTransfer(results);
		}
		return transfer;
	}

	@Override
	public List<Transfer> getByAccountId(long accountId) {
		List<Transfer> transfers = new ArrayList<>();
		String sql = "SELECT * FROM transfers WHERE account_from = ? OR account_to = ? ORDER BY transfer_id";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
		while(results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}
		return transfers;
	}

	@Override
	public void updateTransferStatus(Transfer transfer) {
		String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ?";
		jdbcTemplate.update(sql, transfer.getTransferStatus(), transfer.getTransferId());
	}

	@Override
	public Transfer initiateTransfer(Transfer transfer) {
		String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
		Long id = jdbcTemplate.queryForObject(sql, new Object[] {transfer.getTransferType(), transfer.getTransferStatus(), 
				transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount()}, Long.class);
		transfer.setTransferId(id);
		return transfer;
	}
	@Override
	public String getTransferStatus(int statusId) {
		String sql = "SELECT transfer_status_desc FROM transfer_statuses WHERE transfer_status_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, statusId);
		String ans = "";
		if (results.next()) {
			ans = results.getString("transfer_status_desc");
		}
		return ans;
	}
	
	@Override
	public String getTransferType(int typeId) {
		String sql = "SELECT transfer_type_desc FROM transfer_types WHERE transfer_type_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, typeId);
		String ans = "";
		if (results.next()) {
			ans = results.getString("transfer_type_desc");
		}
		return ans;
	}
	
	@Override
	public List<Transfer> getPendingByAccountId(long accountId) {
		List<Transfer> transfers = new ArrayList<>();
		String sql = "SELECT * FROM transfers WHERE account_from = ? AND transfer_status_id = 1 ORDER BY transfer_id";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
		while(results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}
		return transfers;
	}
	
	
	private Transfer mapRowToTransfer(SqlRowSet results) {
		Transfer theTransfer = new Transfer();
		theTransfer.setTransferId(results.getLong("transfer_id"));
		theTransfer.setTransferType(results.getInt("transfer_type_id"));
		theTransfer.setTransferStatus(results.getInt("transfer_status_id"));
		theTransfer.setAccountFrom(results.getLong("account_from"));
		theTransfer.setAccountTo(results.getLong("account_to"));
		theTransfer.setAmount(results.getBigDecimal("amount"));
		return theTransfer;
	}

	

}
