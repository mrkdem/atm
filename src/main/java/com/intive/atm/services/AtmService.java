package com.intive.atm.services;

import java.util.List;

import com.intive.atm.models.Account;
import com.intive.atm.models.Customer;

public interface AtmService {

    List<Customer> findAll();
    Customer getCustomer(String username);
    Customer addCustomer(Customer customer);
    boolean deposit(int accountNumber, double amount);
    boolean withdraw(int accountNumber, double amount);
    boolean transfer(int accountNumberFrom, int accountNumberTo, double amount);
    Account getAccount(int accountNumber);
    boolean changeLimit(int accountNumber, double limit);

}
