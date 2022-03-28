package com.tenmo.dao;

import com.tenmo.model.Transfer;
import com.tenmo.service.TransferService;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;
    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final String getFullTransfer = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer ";

    @Override
    public Transfer create(Transfer newTransfer) {
        //transfer_id is serialized
        //Transfer transfer = newTransfer;
        Long transfer_id = null;
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                     "VALUES(?, ?, ?, ?, ?) RETURNING transfer_id;";
        try {
            transfer_id = jdbcTemplate.queryForObject(sql, Long.class, newTransfer.getTransferTypeId(), newTransfer.getTransferStatusId(),
                                   newTransfer.getAccountFrom(), newTransfer.getAccountTo(), newTransfer.getAmount());

        } catch (DataAccessException e) {
            //null default
        }
        return getByTransferId(transfer_id);
    }

    @Override
    public Transfer getByTransferId(Long transferId) {
        Transfer transfer = null;
        String sql = getFullTransfer + "WHERE transfer_id = ?;";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, transferId);
        if(row.next())
            transfer = mapRowToTransfer(row);
        return transfer;
    }

    @Override
    public List<Transfer> getTransfersByAccountId(Long accountId) {
        List<Transfer> transfers = new ArrayList<>();


        String sql = getFullTransfer + "WHERE (account_from = ? OR account_to = ?);";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        while(results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransfersByAccountIdTransferStatus(Long accountId, Long transferStatusId) {
        List<Transfer> transfers = new ArrayList<>();


        String sql = getFullTransfer + "WHERE (account_from = ? OR account_to = ?) AND transfer_status_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId, transferStatusId);
        while(results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    @Override
    public List<Transfer> getPendingTransfersByAccountTo(Long accountId, Long transferStatusId) {
        List<Transfer> transfers = new ArrayList<>();

        String sql = getFullTransfer + "WHERE (account_to = ?) AND transfer_status_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, TransferService.PENDING);
        while(results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    //approving or rejecting
    @Override
    public Transfer updateTransfer(Transfer newTransfer) {

        String sql = "UPDATE transfer SET transfer_type_id = ?, transfer_status_id = ?, " +
                     "account_from = ?, account_to = ?, amount = ? WHERE transfer_id = ?;";

        try {
            jdbcTemplate.update(sql, newTransfer.getTransferTypeId(), newTransfer.getTransferStatusId(),
                    newTransfer.getAccountFrom(), newTransfer.getAccountTo(), newTransfer.getAmount(), newTransfer.getId());
        } catch (DataAccessException e) {
            return null;
        }

        return newTransfer;
    }

    private Transfer mapRowToTransfer(SqlRowSet row) {
        Transfer transfer = new Transfer();
        transfer.setId(row.getLong("transfer_id"));
        transfer.setTransferTypeId(row.getLong("transfer_type_id"));
        transfer.setTransferStatusId(row.getLong("transfer_status_id"));
        transfer.setAccountFrom(row.getLong("account_from"));
        transfer.setAccountTo(row.getLong("account_to"));
        transfer.setAmount(row.getBigDecimal("amount"));
        return transfer;
    }
}
