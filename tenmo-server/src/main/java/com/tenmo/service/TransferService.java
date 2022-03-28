package com.tenmo.service;

import com.tenmo.dao.AccountDao;
import com.tenmo.security.exceptions.InsufficientFundsException;
import com.tenmo.model.Account;
import com.tenmo.model.Transfer;

import java.math.BigDecimal;

public class TransferService {
    //transferStatus
    public static final Long PENDING = 1L;
    public static final Long APPROVED = 2L;
    public static final Long REJECTED = 3L;

    //transferType
    public static final Long SEND = 1L;
    public static final Long REQUEST = 2L;

    private AccountDao accountDao;


    public TransferService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Long getTransferStatusId(String transferStatusString) {
        Long transferStatus = 0l;
        if(transferStatusString.equals("PENDING"))
            transferStatus = PENDING;
        else if(transferStatusString.equals("APPROVED"))
            transferStatus = APPROVED;
        else if(transferStatusString.equals("REJECTED"))
            transferStatus = REJECTED;
        return transferStatus;
    }

    //Called in the controller when creating transfers
    public Long getDefaultTransferStatus(Transfer transfer) {
        //SEND default is APPROVED
        //REQUEST default is PENDING
        Long transferStatusId;
        //Default transferStatus
        if(transfer.getTransferTypeId() == SEND)
            transferStatusId = APPROVED;
        else if (transfer.getTransferTypeId() == REQUEST)
            transferStatusId = PENDING;
        else
            return null;

        return transferStatusId;
    }

    public boolean sendMoney(Transfer transfer) throws InsufficientFundsException {
        Account accountFrom = accountDao.getByAccountId(transfer.getAccountFrom());
        Account accountTo = accountDao.getByAccountId(transfer.getAccountTo());
        //fromStarting is account_from starting balance
        BigDecimal fromStarting = accountFrom.getBalance();
        BigDecimal fromEnding = fromStarting.subtract(transfer.getAmount());



        boolean sufficientFunds = (fromEnding.compareTo(BigDecimal.ZERO) >= 0);

        if (transfer.getTransferStatusId() == APPROVED) {
            //toStarting is account_to starting balance
            BigDecimal toStarting = accountTo.getBalance();
            BigDecimal toEnding = toStarting.add(transfer.getAmount());
            if (sufficientFunds) {

                accountFrom.setBalance(fromEnding);

                accountTo.setBalance(toEnding);
                accountDao.update(accountFrom);
                accountDao.update(accountTo);

                return true;
            }
            throw new InsufficientFundsException();
        }
        return false;
    }


    public Transfer executeTransfer(Transfer transfer) {
        //Could be a boolean isSuccessful
        try{
            sendMoney(transfer);
        }
        catch (InsufficientFundsException ife){
            transfer.setTransferStatusId(PENDING); //automatically set back to pending if sending money fails
        }
        return transfer;
    }
}
