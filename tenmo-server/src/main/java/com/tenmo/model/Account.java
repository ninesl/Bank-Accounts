package com.tenmo.model;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Account {

    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private BigDecimal balance;

    public Account() {} //default bc Spring freaks out

    public Long getId() {
        return id;
    }
    public Long getUserId() {
        return userId;
    }
    public BigDecimal getBalance() {
        return balance;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
