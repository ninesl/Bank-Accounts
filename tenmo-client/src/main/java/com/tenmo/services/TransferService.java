package com.tenmo.services;

import com.tenmo.model.Account;
import com.tenmo.model.Transfer;
import com.tenmo.model.User;
import com.tenmo.validation.InputValidation;

import java.util.List;

public class TransferService {
    //transferStatus
    public static final long PENDING = 1L;
    public static final long APPROVED = 2L;
    public static final long REJECTED = 3L;

    //transferType
    public static final Long SEND = 1L;
    public static final Long REQUEST = 2L;

    private AccountService accountService;

    public TransferService(AccountService accountService) {
        this.accountService = accountService;
    }

    private Transfer findTransferById(List<Transfer> transfers, Long id) {
        for (Transfer t : transfers) {
            if (t.getId().equals(id))
                return t;
        }
        return null;
    }

    public String getStatusName(Long id) {
        if(id.equals(PENDING)) {
            return "PENDING";
        }
        if(id.equals(APPROVED)) {
            return "APPROVED";
        }
        if(id.equals(REJECTED)) {
            return "REJECTED";
        }
        return "ERROR - you should never see this";//should never happen
    }
    public String getTypeName(Long id) {
        if(id.equals(SEND)) {
            return "SEND";
        }
        if(id.equals(REQUEST)) {
            return "REQUEST";
        }
        return "ERROR - you should never see this";//should never happen
    }

    //This could have just been and if statement, when we were writing it I thought switch would be cool
    //ended up just being a weird casting mess
    public void transferRequestProcessResponse(Transfer transfer){
        switch(Math.toIntExact(transfer.getTransferStatusId())) {
            case (int)PENDING:
                System.out.println("We could not process your transfer, please check your balance.");
                System.out.println("The transfer is set to PENDING.");
                break;
            case (int)APPROVED:
                System.out.println("The transfer was processed successfully!");
                break;
            case (int)REJECTED: //confirm to the user it was rejected
                System.out.println("The transfer was rejected.");
                break;
        }
    }


    public void acceptOrReject(List<Transfer> pendingTransfers) {
        Long userInput = null;
        Transfer executingTransfer;
        while(userInput == null) {
            userInput = ConsoleService.promptForLong("Please enter an ID to approve/reject (0 to Exit): ", 0L);
            executingTransfer = findTransferById(pendingTransfers, userInput);

            if(userInput != 0L) {
                if (executingTransfer != null) {
                    do {
                        userInput = ConsoleService.promptForLong("Please accept(1) or reject(2) this transfer (0 to Exit): ", 0L);
                        if((userInput+1L) == (APPROVED) || (userInput+1L) == (REJECTED)) {
                            executingTransfer.setTransferStatusId(userInput+1);

                            executingTransfer = accountService.executeTransferByStatus(executingTransfer);

                            transferRequestProcessResponse(executingTransfer);
                        } else if(userInput != 0L) {
                            userInput = null;
                        }
                    } while (userInput == null);
                } else {
                    System.out.println("That transfer ID is invalid.");
                }
            }
        }
    }

    public User promptForUser(User fromUser) {
        Long userInput = null;
        User toUser;
        while(userInput == null) {
            userInput = ConsoleService.promptForLong("Please enter a user ID to send money to (0 to Exit): ", 0L);
            toUser = accountService.getUser(userInput);
            if(userInput != 0L){
                if (userInput.equals(fromUser.getId())) {
                    System.out.println("You cannot choose your own user ID.");

                } else if (toUser == null) {//dont want to show this if they exit
                    System.out.println("That user was not found.");
                } else{
                    return toUser;
                }
                userInput = null;
            }


        }
        return null;
    }

    public Long getAccountIdFromList(String prompt, List<Account> accounts) {
        Long userInput = null;
        boolean hasId = false;
        while(!hasId) {
            userInput = ConsoleService.promptForLong(prompt, 0L);
            hasId = InputValidation.hasId(userInput, InputValidation.getIds(accounts));
            if(!hasId && userInput != 0L) {
                System.out.println("That is not a valid ID.");
            } else if (userInput != 0L){
                return userInput;
            } else {
                //is equal to 0
                hasId = true;
            }
        }
        return userInput;//null escape
    }
}


