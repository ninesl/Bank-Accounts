package com.tenmo.dao;

import com.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public Transfer create(Transfer newTransfer);//1 send 2 request

    public Transfer getByTransferId(Long transferId);

    public List<Transfer> getTransfersByAccountId(Long accountId);

    public List<Transfer> getTransfersByAccountIdTransferStatus(Long accountId, Long transferStatusId);

    public List<Transfer> getPendingTransfersByAccountTo(Long accountId, Long transferStatusId);

    public Transfer updateTransfer(Transfer newTransfer);
}
