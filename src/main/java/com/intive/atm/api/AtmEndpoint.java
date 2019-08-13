package com.intive.atm.api;

import static com.intive.atm.exeptions.AtmErrorCode.ERROR_ACCOUNT_WITHDRAWAL;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intive.atm.exeptions.AtmException;
import com.intive.atm.models.Account;
import com.intive.atm.models.Customer;
import com.intive.atm.services.AtmServiceImpl;

import javaslang.control.Try;

@RestController
@RequestMapping(value = "/api")
public class AtmEndpoint {

    private static final Logger logger =  LoggerFactory.getLogger(AtmEndpoint.class.getName());

    private final AtmServiceImpl atmService;

    @Autowired
    AtmEndpoint(AtmServiceImpl atmService) {
        this.atmService = atmService;
    }

    @GetMapping(value = "/customers/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(atmService.findAll());
    }

    @GetMapping(value = "/customers/{username}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("username") String username) {
        return ResponseEntity.ok(atmService.getCustomer(username));
    }

    @PostMapping(value = "/customers/add")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        logger.info("/customers/add add new customer " + customer);
        return ResponseEntity.ok(atmService.addCustomer(customer));
    }

    @PutMapping(value = "/accounts/deposit/{accountNumber}/{amount}")
    public ResponseEntity<Boolean> deposit(@PathVariable("accountNumber") int accountNumber, @PathVariable("amount") double amount) {
        logger.info(String.format("/accounts/deposit deposit cash %.2f to %d", amount, accountNumber));

        boolean result = Try.of(() -> atmService.deposit(accountNumber, amount))
                            .getOrElseThrow(
                                    throwable -> {
                                        if (throwable instanceof AtmException) {
                                            throw (AtmException) throwable;
                                        }
                                        throw new AtmException(
                                                String.format("Money cannot be deposit for account %d", accountNumber),
                                                ERROR_ACCOUNT_WITHDRAWAL,
                                                throwable);
                                    }
                            );

        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/accounts/withdraw/{accountNumber}/{amount}")
    public ResponseEntity<Boolean> withdraw(@PathVariable("accountNumber") int accountNumber, @PathVariable("amount") double amount) {
        logger.info(String.format("/accounts/withdraw withdraw cash %.2f to %d", amount, accountNumber));

        boolean result = Try.of(() -> atmService.withdraw(accountNumber, amount))
                            .getOrElseThrow(
                                    throwable -> {
                                        if (throwable instanceof AtmException) {
                                            throw (AtmException) throwable;
                                        }
                                        throw new AtmException(
                                                String.format("Money cannot be withdrawn from account %d", accountNumber),
                                                ERROR_ACCOUNT_WITHDRAWAL,
                                                throwable);
                                    }
                            );

        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/accounts/transfer/{accountFrom}/{accountTo}/{amount}")
    public ResponseEntity<Boolean> transfer(@PathVariable("accountFrom") int accountFrom, @PathVariable("accountTo") int accountTo,
                                   @PathVariable("amount") double amount) {
        logger.info(String.format("/accounts/transfer transfer cash %.2f from %d to %d", amount, accountFrom, accountTo));

        boolean result = Try.of(() -> atmService.transfer(accountFrom, accountTo, amount))
                            .getOrElseThrow(
                                    throwable -> {
                                        if (throwable instanceof AtmException) {
                                            throw (AtmException) throwable;
                                        }
                                        throw new AtmException(
                                                String.format("Money cannot be transfer from %d to %d", accountFrom, accountTo),
                                                ERROR_ACCOUNT_WITHDRAWAL,
                                                throwable);
                                    }
                            );

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/accounts/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable("accountNumber") int accountNumber) {
        return ResponseEntity.ok(atmService.getAccount(accountNumber));
    }

    @PutMapping(value = "/accounts/change/{accountNumber}/limit/{limit}")
    public ResponseEntity<Boolean> changeLimit(@PathVariable("accountNumber") int accountNumber, @PathVariable("limit") double limit) {
        logger.info(String.format("/accounts/change/limit/ change limit for account %d", accountNumber));

        boolean result = Try.of(() -> atmService.changeLimit(accountNumber, limit))
                            .getOrElseThrow(
                                    throwable -> {
                                        if (throwable instanceof AtmException) {
                                            throw (AtmException) throwable;
                                        }
                                        throw new AtmException(
                                                String.format("Limit cannot be changed for account %d", accountNumber),
                                                ERROR_ACCOUNT_WITHDRAWAL,
                                                throwable);
                                    }
                            );

        return ResponseEntity.ok(result);
    }

}