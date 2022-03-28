package com.tenmo;

import com.tenmo.model.*;
import com.tenmo.services.AccountService;
import com.tenmo.services.AuthenticationService;
import com.tenmo.services.ConsoleService;
import com.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.List;

public class App {

    //TODO refactor sendBucks() and requestBucks()
    private static final String API_BASE_URL = "http://localhost:8080/";

    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();

    //will get auth because it is the same accountService pointer
    private final TransferService transferService = new TransferService(accountService);
    private final ConsoleService consoleService = new ConsoleService(accountService, transferService);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
        System.out.println("Good Bye.");
    }

    private void mainMenu() {
        Integer menuSelection = null;
        while (menuSelection == null) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForInteger("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            menuSelection = null;
            consoleService.pause();
        }
    }

    private User getCurrentUser(){
        return currentUser.getUser();
    }

    private List<Account> viewCurrentBalance() {
        List<Account> accounts = accountService.getAccountsByUserId(getCurrentUser().getId());
        //consoleService.printUserDetails(getCurrentUser());
        for(Account a : accounts)
            consoleService.printAccountDetails(a);
        return accounts;
    }

    private void viewTransferHistory() {
        //prompt for acc #
        viewCurrentBalance();
        Long userInput = consoleService.promptForLong("Please enter an account ID for transfer history (0 to Exit): ", 0L);

        if(userInput != 0L) {
            List<Transfer> transfers = accountService.getTransfersByAccountId(userInput);
            if (transfers.size() != 0) {
                consoleService.printSimpleTransferBanner();
                for (Transfer t : transfers) {
                    consoleService.printSimpleTransferDetails(t, getCurrentUser());
                }
            }
            else {
                System.out.println("No transfer history.");
            }
        }
    }

    private void viewPendingRequests() {
        //prompt for acc #
        viewCurrentBalance();
        Long userInput = consoleService.promptForLong("Please enter an account ID to see all Pending Transfers (0 to Exit): ", 0L);
        //userInput != null if you didn't escape
        if(userInput != null) {
            List<Transfer> pendingTransfers = accountService.getTransfersByAccountIdByStatusType(userInput, "PENDING");
            if(pendingTransfers.size() > 0) {
                consoleService.printSimpleTransferBanner();
                for (Transfer t : pendingTransfers) {
                    consoleService.printSimpleTransferDetails(t, getCurrentUser());
                }

                transferService.acceptOrReject(pendingTransfers);//todo fix looping input
            } else {
                System.out.println("No pending transfers.");
            }
        }
    }

    private void sendBucks() {
        List<User> allUsers;
        allUsers = accountService.getAllUsers();
        System.out.println("User ID   Username");
        System.out.println("------------------");
        for(User u : allUsers) {
            System.out.println(u.getId() + "      " + u.getUsername());
        }
        //TODO Refactor
        Transfer transfer = new Transfer();

        User toUser = transferService.promptForUser(getCurrentUser());
        if(toUser != null){
            List<Account> toUserAccounts = accountService.getAccountsByUserId(toUser.getId());
            System.out.println("----------------------------------");
            System.out.println(toUser.getUsername() + "'s Accounts");
            System.out.println("----------------------------------");
            for (Account a : toUserAccounts) {
                System.out.println("Account ID: " + a.getId());
            }
            System.out.println(" ");
            Long toInput = transferService.getAccountIdFromList("Please enter an account to send TO (0 to exit): ", toUserAccounts);
            if(toInput != 0L) {
                transfer.setAccountTo(toInput);
                System.out.println("");
                System.out.println("----------------------------------");
                System.out.println("-------   My Accounts   ----------");
                System.out.println("----------------------------------");
                List<Account> accounts = viewCurrentBalance();
                Long input = transferService.getAccountIdFromList("Please enter an account to send FROM (0 to exit): ", accounts);
                if (input != 0) {
                    transfer.setAccountFrom(input);
                    boolean validMoney = false;
                    BigDecimal amountToSend;
                    while (!validMoney) {
                        amountToSend = ConsoleService.promptForDecimal("Please enter an amount of money to send (0 to exit):", BigDecimal.ZERO);
                        if (amountToSend.compareTo(BigDecimal.ZERO) != 0) { //if not escape character
                            //has enough funds
                            validMoney = (accountService.getAccountByAccountId(input).getBalance().compareTo(amountToSend)) >= 0;
                            if (validMoney) {
                                transfer.setTransferTypeId(TransferService.SEND);
                                transfer.setTransferStatusId(TransferService.APPROVED);//will get set to pending if not approved on back end
                                transfer.setAmount(amountToSend);
                                Transfer transferWithId = accountService.createNewTransfer(transfer);
                                //transfer = accountService.executeTransferByStatus(transfer);

                                //sending the transfer to service to process
                                //transferService.transferRequestProcessResponse(transfer);
                                System.out.println("Transfer Successfully Made");

                            }
                        } else {
                            validMoney = true;
                        }
                    }
                }
            }
        }
    }

    private void requestBucks() {
        //List users to request from
        List<User> allUsers = accountService.getAllUsers();
        System.out.println("User ID   Username");
        System.out.println("------------------");
        for(User u : allUsers) {
            System.out.println(u.getId() + "      " + u.getUsername());
        }
        //List which account of yours to deposit to
        //TODO Refactor
        Transfer transfer = new Transfer();
        User fromUser = transferService.promptForUser(getCurrentUser());
        if(fromUser != null) {


            List<Account> fromUserAccounts = accountService.getAccountsByUserId(fromUser.getId());
            System.out.println("----------------------------------");
            System.out.println(fromUser.getUsername() + "'s Accounts");
            System.out.println("----------------------------------");
            for (Account a : fromUserAccounts) {
                System.out.println("Account ID: " + a.getId());
            }
            System.out.println(" ");
            Long fromInput = transferService.getAccountIdFromList("Please enter an account to request FROM (0 to exit): ", fromUserAccounts);

            if (fromInput != 0L) {
                transfer.setAccountFrom(fromInput); //
                System.out.println("");
                System.out.println("----------------------------------");
                System.out.println("-------   My Accounts   ----------");
                System.out.println("----------------------------------");
                List<Account> accounts = viewCurrentBalance();
                System.out.println("");
                Long input = transferService.getAccountIdFromList("Please enter an account to send DEPOSIT TO (0 to exit): ", accounts);
                if (input != 0) {
                    transfer.setAccountTo(input);
                    boolean validMoney = false;
                    BigDecimal amountToRequest;
                    while (!validMoney) {
                        amountToRequest = ConsoleService.promptForDecimal("Please enter an amount of money to REQUEST (0 to exit):", BigDecimal.ZERO);
                        if (amountToRequest.compareTo(BigDecimal.ZERO) != 0) { //if not escape character

                            transfer.setTransferTypeId(TransferService.REQUEST);
                            transfer.setTransferStatusId(TransferService.PENDING);//will get set to pending if not approved on back end
                            transfer.setAmount(amountToRequest);
                            Transfer transferWithId = accountService.createNewTransfer(transfer);
                            transfer = accountService.executeTransferByStatus(transferWithId);//sending the transfer to service to process
                            //transferService.transferRequestProcessResponse(transfer);
                            System.out.println("Request Successfully Made. Status PENDING");
                            validMoney = true;
                            //}
                        } else {
                            validMoney = true;
                        }
                    }
                }
            }
        }
    }

    //LOGIN

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForInteger("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            accountService.setAuthToken(currentUser.getToken());
        }
    }
}
