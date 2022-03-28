package com.tenmo.model;

import org.springframework.data.annotation.Id;

import javax.annotation.processing.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class Transfer {


    private Long id; //null before we change it when accessing the database

    //@Size(min=1, max=2)
    private Long transferTypeId; //1 Send 2 Request
    //@Size(min=1, max=3)
    private Long transferStatusId; //1 Pending 2 Approved 3 Rejected
    @NotNull
    private Long accountFrom;
    @NotNull
    private Long accountTo;

    @NotNull
    @Positive
    private BigDecimal amount;

    public Transfer() {} //default bc Spring freaks out

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
        return accountFrom;
    }
    public Long getAccountTo() {
        return accountTo;
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
        this.accountFrom = accountFrom;
    }
    public void setAccountTo(Long accountTo) {
        this.accountTo = accountTo;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
