package com.tenmo.dao;

import com.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {
    private JdbcTemplate jdbcTemplate;
    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final String getFullAccount = "SELECT account_id, user_id, balance FROM account ";
    @Override
    public Account getByAccountId(Long accountId) {
        String sql = getFullAccount + "WHERE account_id = ?;";

        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, accountId);
        if(row.next()){
            return mapRowToAccount(row);
        }

        return null;
    }


    @Override
    public List<Account> getAllAccountsByUserId(Long userId) {
        String sql = getFullAccount + "WHERE user_id = ?;";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, userId);

        List<Account> accounts = new ArrayList<>();
        while(row.next()) {
            accounts.add(mapRowToAccount(row));
        }
        return accounts;
    }

    @Override
    public Account update(Account account) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?;";

        try{
            jdbcTemplate.update(sql, account.getBalance(), account.getId());
        }
        catch (DataAccessException dae){
            return null;
        }
        return account;
    }

    private Account mapRowToAccount(SqlRowSet row) {
        Account account = new Account();
        account.setId(row.getLong("account_id"));
        account.setUserId(row.getLong("user_id"));
        account.setBalance(row.getBigDecimal("balance"));
        return account;
    }
}
