package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {
	
	Transfer getByTransferId(long transferId);
	
	List<Transfer> getByAccountId(long accountId);
	
	void updateTransferStatus(Transfer transfer);
	
	Transfer initiateTransfer(Transfer transfer);
	
	String getTransferStatus(int statusId);
	
	String getTransferType(int typeId);

	List<Transfer> getPendingByAccountId(long accountId);

}
