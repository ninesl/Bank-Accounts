package com.tenmo.services;

import com.tenmo.model.Account;
import com.tenmo.model.Transfer;
import com.tenmo.model.User;
import com.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import Account, User and Transfer

public class AccountService {
    private static final String API_BASE_URL = "http://localhost:8080/users";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    //get Account by accountId
    public Account getAccountByAccountId(Long id) {
        Account account = null;
        try {
            ResponseEntity<Account> response =
                    restTemplate.exchange(API_BASE_URL + "/account/{id}",
                            HttpMethod.GET, makeAuthEntity(), Account.class, id);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

//View Current Accounts by Account Id

    public List<Account> getAccountsByUserId(long id) {
        List<Account> accounts = new ArrayList<>();
        try {
            ResponseEntity<Account[]> response =
                    restTemplate.exchange(API_BASE_URL + "/account?user-id={id}",
                            HttpMethod.GET, makeAuthEntity(), Account[].class, id);
            accounts.addAll(Arrays.asList(response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return accounts;
    }

//View Transfer History by Account Id

    public List<Transfer> getTransfersByAccountId(long id) {
        List<Transfer> transfers = new ArrayList<>();
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(API_BASE_URL + "/account/transfers?id={id}",
                            HttpMethod.GET, makeAuthEntity(), Transfer[].class, id);
            transfers.addAll(Arrays.asList(response.getBody()));

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

//View Transfers by Status type and Account Id (PENDING, APPROVED, REJECTED)

    public List<Transfer> getTransfersByAccountIdByStatusType(long id, String status) {
        List<Transfer> pendingTransfers = new ArrayList<>();
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(API_BASE_URL + "/account/transfers?id={id}&status={status}",
                            HttpMethod.GET, makeAuthEntity(), Transfer[].class, id, status);
            pendingTransfers.addAll(Arrays.asList(response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return pendingTransfers;
    }

    // Update status of transfer (PENDING to APPROVED or REJECTED) and execute it
    public Transfer executeTransferByStatus(Transfer updatedTransfer) {

        try {

            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "/account/transfers" , HttpMethod.PUT,
                    makeTransferEntity(updatedTransfer), Transfer.class);
            updatedTransfer = response.getBody();//will be automagically set to pending from backend if approved with
                                                 //insufficient funds
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return updatedTransfer;
    }

//See all users

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try {
            ResponseEntity<User[]> response =
                    restTemplate.exchange(API_BASE_URL,
                            HttpMethod.GET, makeAuthEntity(), User[].class);
            users.addAll(Arrays.asList(response.getBody()));
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    //Find User by Id (Do we ever use that?)
    public User getUser(long id) {
        User user = null;

        try {
            ResponseEntity<User> response =
                    restTemplate.exchange(API_BASE_URL + "/{id}",
                            HttpMethod.GET, makeAuthEntity(), User.class, id);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    public User getOwner(long accountId){
        User owner = null;

        try {
            ResponseEntity<User> response =
                    restTemplate.exchange(API_BASE_URL + "/owner?account-id={accountId}",
                            HttpMethod.GET, makeAuthEntity(), User.class, accountId);
            owner = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return owner;

    }


//POST a new transfer all types *SERVER SIDE DEALS WITH EACH TYPE*

    public Transfer createNewTransfer(Transfer transfer) {
        Transfer returnedTransfer = null;
        HttpEntity<Transfer> entity = makeTransferEntity(transfer);

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "/account/transfers", HttpMethod.POST, entity, Transfer.class);
            returnedTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }


//Creates a new HttpEntity with the `Authorization: Bearer:` header and a User request body

    private HttpEntity<User> makeUserEntity(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(user, headers);
    }

//Creates a new HttpEntity with the `Authorization: Bearer:` header and an Account request body

    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(account, headers);
    }


//Creates a new HttpEntity with the `Authorization: Bearer:` header and a Transfer request body

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    //Returns an HttpEntity with the `Authorization: Bearer:` header

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}



