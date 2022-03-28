package com.tenmo.validation;

import com.tenmo.model.Account;
import com.tenmo.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputValidation {

    public BigDecimal getABigDecimal(Scanner scan) {
        BigDecimal input = null;
        while(input == null) {
            String strInput = scan.nextLine();
            try {
                input = new BigDecimal(strInput);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid decimal.");
            }
        }
        return input;
    }

    public Long getALong(Scanner scan) {
        Long input = null;
        while(input == null) {
            String strInput = scan.nextLine();
            try {
                input = Long.parseLong(strInput);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid ID.");
            }
        }
        return input;
    }

    public int getAInt(Scanner scan) {
        Integer input = null;
        while(input == null) {
            String strInput = scan.nextLine();
            try {
                input = Integer.parseInt(strInput);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid selection.");
            }
        }
        return input;
    }

    public static List<Long> getIds(User user) {
        List<Long> invalid = new ArrayList<>();
        invalid.add(user.getId());
        return invalid;
    }

    public static List<Long> getIds(List<Account> accounts) {
        List<Long> ids = new ArrayList<>();
        for(Account a : accounts) {
            ids.add(a.getId());
        }
        return ids;
    }

    public static boolean hasId(Long input, List<Long> ids) {
        return ids.contains(input);
    }

}
