package com.intive.atm.services;

import static com.intive.atm.exeptions.AtmErrorCode.ERROR_ACCOUNT_WITHDRAWAL;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.intive.atm.exeptions.AtmErrorCode;
import com.intive.atm.exeptions.AtmException;
import com.intive.atm.models.Account;
import com.intive.atm.models.Customer;

import lombok.Setter;

@Service
@CacheConfig(cacheNames={"customers"})
public class AtmServiceImpl implements AtmService {

    @Setter
    private List<Customer> customers;

    @Override
    @Cacheable
    public List<Customer> findAll() {
        return customers;
    }

    @Override
    public Customer getCustomer(String username) {
        return customers.stream().filter(c -> username.equals(c.getUsername())).findFirst()
                             .orElseThrow(
                                () -> new AtmException(
                                        String.format("The customer not found %s", username),
                                        AtmErrorCode.ERROR_CUSTOMER_NOT_FOUND
                                ));
    }

    @Override
    @CachePut
    public Customer addCustomer(Customer customer) {
        customers.add(customer);
        return customer;
    }

    @Override
    @CachePut
    public boolean deposit(int accountNumber, double amount) {
        Account account = getAccount(accountNumber);
        account.setBalance(account.getBalance() + amount);

        return true;
    }

    @Override
    @CachePut
    public boolean withdraw(int accountNumber, double amount) {
        Account account = getAccount(accountNumber);
        if (checkIfWithdrawalAvailable(account, amount)) {
            account.setBalance(account.getBalance() - amount);
        }
        return true;
    }

    @Override
    @CachePut
    public boolean transfer(int accountNumberFrom, int accountNumberTo, double amount) {
        Account accountFrom = getAccount(accountNumberFrom);
        Account accountTo = getAccount(accountNumberTo);

        if (checkIfWithdrawalAvailable(accountFrom, amount)) {
            accountFrom.setBalance(accountFrom.getBalance() - amount);
            accountTo.setBalance(accountTo.getBalance() + amount);
        }
        return true;
    }

    @Override
    public Account getAccount(int accountNumber) {
        return customers.stream().filter(c -> c.getAccount().getAccountNumber() == accountNumber)
                             .map(Customer::getAccount).findFirst()
                             .orElseThrow(
                                () -> new AtmException(
                                        String.format("The account not found %d", accountNumber),
                                        AtmErrorCode.ERROR_ACCOUNT_NOT_FOUND
                                ));
    }

    @Override
    @CachePut
    public boolean changeLimit(int accountNumber, double limit) {
        Account account = getAccount(accountNumber);
        account.setLimit(limit);

        return true;
    }

    private boolean checkIfWithdrawalAvailable(Account account, double amount) {
        if (account.getBalance() < amount) {
            throw new AtmException(String.format("Money cannot be withdrawn. Insufficient balance (%.2f) for account %d",
                    account.getBalance(), account.getAccountNumber()),
                    ERROR_ACCOUNT_WITHDRAWAL);
        }

        if (account.getLimit() < amount) {
            throw new AtmException(String.format("Money cannot be withdrawn. Withdrawal limit %.2f for account %d",
                    account.getLimit(), account.getAccountNumber()),
                    ERROR_ACCOUNT_WITHDRAWAL);
        }

        return true;
//        return account.getBalance() >= amount && account.getLimit() >= amount;
    }
}