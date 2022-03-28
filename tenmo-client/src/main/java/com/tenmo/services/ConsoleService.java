package com.tenmo.services;


import com.tenmo.model.Account;
import com.tenmo.model.Transfer;
import com.tenmo.model.User;
import com.tenmo.model.UserCredentials;
import com.tenmo.validation.InputValidation;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Scanner;

public class ConsoleService {

    private static final Scanner scanner = new Scanner(System.in);
    private static InputValidation inputValidation = new InputValidation();
    private AccountService accountService; //THIS IS FOR WHEN WE GET THE USERNAME OF AN ACCOUNT FOR DEALING WITH TRANSFERS
    private TransferService transferService;
    //should have not, passed the services all to eachother

    public String currencyFormat(BigDecimal n) {
        return NumberFormat.getCurrencyInstance().format(n);
    }

    public ConsoleService (AccountService accountService, TransferService transferService) {
        this.accountService = accountService;
        this.transferService = transferService;
    }

    public static int promptForInteger(String prompt) {
        Integer selection = null;
        while(selection == null) {
            System.out.print(prompt);
            selection = inputValidation.getAInt(scanner);
        }
        return selection;
    }

    public static BigDecimal promptForDecimal(String prompt, BigDecimal escape) {
        BigDecimal amount = null;
        while(amount == null) {
            System.out.print(prompt);
            amount = inputValidation.getABigDecimal(scanner);
            if(amount.compareTo(escape) == 0) {
                break;
            }
        }
        return amount;
    }

    //If escape character is met, return null
    public static Long promptForLong(String prompt, Long escape) {
        Long id = null;
        while(id == null) {
            System.out.print(prompt);
            id = inputValidation.getALong(scanner);
            if(id.equals(escape)) {
                break;
            }
        }
        return id;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View Current Balance");
        System.out.println("2: View All Transactions");
        System.out.println("3: View Pending Requests");
        System.out.println("4: Send TE Bucks");
        System.out.println("5: Request TE Bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printUserDetails(User user) {
        System.out.println();
        System.out.println("User ID  : " + user.getId());
        System.out.println("Username : " + user.getUsername());
        System.out.println();
    }

    public void printAccountDetails(Account account) {
        System.out.println("AccountID: " + account.getId() + "       Balance: " + currencyFormat(account.getBalance()));
    }

    public void printSimpleTransferBanner() {
        System.out.println("-------------------------------------------------------");
        System.out.printf("%-10s%-15s%15s%15s\n" +
                           "-------------------------------------------------------\n", "ID", "From/To", "Amount", "Status");
    }

    public void printSimpleTransferDetails(Transfer transfer, User curUser) {
        String usernameFrom = accountService.getOwner(transfer.getAccountFrom()).getUsername();
        String username = "";
        String fromTo = "";

        if(curUser.getUsername().equals(usernameFrom)) { //if its from us, it's to them
            fromTo = "To:";
            username = accountService.getOwner(transfer.getAccountTo()).getUsername();
        } else { //ifs to us, its from them
            fromTo = "From:";
            username = usernameFrom;
        }

        System.out.printf("%-10s%-7s%-8s%15s%15s\n", transfer.getId(), fromTo, username, currencyFormat(transfer.getAmount()),
                                                            transferService.getStatusName(transfer.getTransferStatusId()));
    }

    /*--------------------------------------------
            Transfer Details
            --------------------------------------------
        Id: 23
        From: Bernice
        To: Me Myselfandi
        Type: Send
        Status: Approved
        Amount: $903.14
    */

    public void printFullTransferDetails(Transfer transfer) {
        System.out.println("---------------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("---------------------------------------------");
        System.out.printf("%-15s: %d\n", "ID", Math.toIntExact(transfer.getId()));
        System.out.printf("%-15s: %s\n", "From", accountService.getUser(transfer.getAccountFrom()).getUsername());
        System.out.printf("%-15s: %s\n", "To", accountService.getUser(transfer.getAccountFrom()).getUsername());
        System.out.printf("%-15s: %s\n", "Type", transferService.getTypeName(transfer.getTransferTypeId()));
        System.out.printf("%-15s: %s\n", "Status", transferService.getStatusName(transfer.getTransferStatusId()));
        System.out.printf("%-15s: %s\n", "Amount", currencyFormat(transfer.getAmount()));
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
