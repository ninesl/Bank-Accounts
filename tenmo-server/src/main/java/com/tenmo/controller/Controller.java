package com.tenmo.controller;

import com.tenmo.dao.AccountDao;
import com.tenmo.dao.TransferDao;
import com.tenmo.dao.UserDao;
import com.tenmo.model.Account;
import com.tenmo.model.Transfer;
import com.tenmo.model.User;
import com.tenmo.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

//could have all our SQL statements use JOIN user.id
//right now you technically could call any api method and affect other users if you knew their info
//through postman, client side you wouldn't be able to

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "users")
public class Controller {
    private AccountDao accountDao;
    private UserDao userDao;
    private TransferDao transferDao;
    private TransferService transferService;

    public Controller(UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.transferService = new TransferService(accountDao);
    }

    /*
    * localhost:8080/users?userid={userid}?
    * localhost:8080/users?accountid={accountid}?
    *
    *
    *
    * localhost:8080/users/{id}
    * localhost:8080/users/accounts/{id}
    * */


    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public List<Account> getAccountsByUserId(@RequestParam(name = "user-id") Long userId) {
        return accountDao.getAllAccountsByUserId(userId);
    }

    @RequestMapping(path = "/account/{id}", method = RequestMethod.GET)
    public Account getAccountAccountId(@PathVariable Long id) {
        return accountDao.getByAccountId(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/account", method = RequestMethod.PUT)
    public Account update(@Valid @RequestBody Account account) {
        return accountDao.update(account);
    }

    /*** USER HANDLERS ***/
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public User findUserById(@PathVariable (value = "id") long id) {
       return userDao.findByUserId(id);
    }

    @RequestMapping(path = "/owner", method = RequestMethod.GET)
    public User getUserByAccountId(@RequestParam(name = "account-id") long id){
        return userDao.findUserByAccountId(id);
    }


    // *** TRANSFER HANDLERS ***

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/account/transfers", method = RequestMethod.POST)
    public Transfer createTransfer(@Valid @RequestBody Transfer newTransfer) {
        Transfer transfer = new Transfer();
        newTransfer.setTransferStatusId(transferService.getDefaultTransferStatus(newTransfer));
        transfer = transferDao.create(newTransfer);

        //If a transfer is created and type is SEND, if not SEND it's REQUEST
        //Automagically send money if SEND type, otherwise would have to remember to execute client side
        //this prevents making a transfer as APPROVED and SEND but no update/logic
        if(newTransfer.getTransferTypeId() == transferService.SEND)
            transfer = executeTransferById(newTransfer);

        return transfer;
    }



    @RequestMapping(path = "/account/transfers", method = RequestMethod.GET) //?id={id}?status={pending, rejected etc}
    public List<Transfer> getTransfersByAccountIdByStatus(@RequestParam(required = true) long id,
                                                          @RequestParam(defaultValue = "", required = false) String status) {

        if(status.isBlank()) {
            return transferDao.getTransfersByAccountId(id);
        }
        return transferDao.getTransfersByAccountIdTransferStatus(id, transferService.getTransferStatusId(status));
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/account/transfers", method = RequestMethod.PUT)
    public Transfer executeTransferById(@Valid @RequestBody Transfer updatedTransfer) {
        /*
        * updated transfer with ACCEPTED or REJECTED will come client side in @RequestBody updatedTransfer
        * */
        updatedTransfer = transferService.executeTransfer(updatedTransfer);
        return transferDao.updateTransfer(updatedTransfer);
    }

    @RequestMapping(path = "/account/transfers/{id}", method = RequestMethod.GET)
    public Transfer getTransferByTransferId(@PathVariable long id) {
        return transferDao.getByTransferId(id);
    }

}
