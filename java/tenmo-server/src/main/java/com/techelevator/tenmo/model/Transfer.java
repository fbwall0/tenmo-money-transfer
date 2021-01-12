package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

	private long transferId;
	private int transferType;
	private int transferStatus;
	private long accountFrom;
	private long accountTo;
	private BigDecimal amount;
	
	public Transfer() {
		
	}
	
	public Transfer(long transferId, int transferType, int transferStatus, long accountFrom, long accountTo,
			BigDecimal amount) {
		this.transferId = transferId;
		this.transferType = transferType;
		this.transferStatus = transferStatus;
		this.accountFrom = accountFrom;
		this.accountTo = accountTo;
		this.amount = amount;
	}



	public long getTransferId() {
		return transferId;
	}

	public void setTransferId(long transferId) {
		this.transferId = transferId;
	}

	public int getTransferType() {
		return transferType;
	}

	public void setTransferType(int transferType) {
		this.transferType = transferType;
	}

	public int getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(int transferStatus) {
		this.transferStatus = transferStatus;
	}

	public long getAccountFrom() {
		return accountFrom;
	}

	public void setAccountFrom(long accountFrom) {
		this.accountFrom = accountFrom;
	}

	public long getAccountTo() {
		return accountTo;
	}

	public void setAccountTo(long accountTo) {
		this.accountTo = accountTo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	
	

}
