package com.tenmo.dao;

import com.tenmo.model.Account;

import java.util.List;

public interface AccountDao {

    public Account getByAccountId(Long accountId);

    //TODO make new account
    //public boolean createAccountWithUserId(Long userId);

    public Account update(Account newBalance);

    public List<Account> getAllAccountsByUserId(Long userId);

}
