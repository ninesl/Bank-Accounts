package com.tenmo.model;

import java.math.BigDecimal;

public class Transfer {


    private Long id; //null before we change it when accessing the database
    private Long transferTypeId; //1 Send 2 Request
    private Long transferStatusId; //1 Pending 2 Approved 3 Rejected
    private Long accountFromId;
    private Long accountToId;
    private BigDecimal amount;

    public Transfer() {}

    public Long getId() {
        return id;
    }
    public Long getTransferTypeId() {
        return transferTypeId;
    }
    public Long getTransferStatusId() {
        return transferStatusId;
    }
    public Long getAccountFrom() {
        return accountFromId;
    }
    public Long getAccountTo() {
        return accountToId;
    }
    public BigDecimal getAmount() {
        return amount;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setTransferTypeId(Long transferTypeId) {
        this.transferTypeId = transferTypeId;
    }
    public void setTransferStatusId(Long transferStatusId) {
        this.transferStatusId = transferStatusId;
    }
    public void setAccountFrom(Long accountFrom) {
        this.accountFromId = accountFrom;
    }
    public void setAccountTo(Long accountTo) {
        this.accountToId = accountTo;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


}
