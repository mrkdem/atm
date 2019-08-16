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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javaslang.control.Try;

@RestController
@RequestMapping(value = "/api")
@Api(value = "ATM application", description = "ATM operations for bank customers")
public class AtmEndpoint {

    private static final Logger logger =  LoggerFactory.getLogger(AtmEndpoint.class.getName());

    private final AtmServiceImpl atmService;

    @Autowired
    AtmEndpoint(AtmServiceImpl atmService) {
        this.atmService = atmService;
    }

    @ApiOperation(value = "View a list of available customers", response = List.class)
    @GetMapping(value = "/customers/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(atmService.findAll());
    }

    @ApiOperation(value = "Get an customer by username")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved customer"),
            @ApiResponse(code = 404, message = "The customer is not found")
    })
    @GetMapping(value = "/customers/{username}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("username") String username) {
        return ResponseEntity.ok(atmService.getCustomer(username));
    }

    @ApiOperation(value = "Add customer")
    @PostMapping(value = "/customers/add")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        logger.info("/customers/add add new customer " + customer);
        return ResponseEntity.ok(atmService.addCustomer(customer));
    }

    @ApiOperation(value = "Deposit money to given account number")
    @PutMapping(value = "/accounts/deposit/{accountNumber}/{amount}")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The money was successfully deposited"),
            @ApiResponse(code = 404, message = "The account is not found"),
            @ApiResponse(code = 500, message = "The money cannot be deposit")
    })
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

    @ApiOperation(value = "Withdraw money from given account number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The money was successfully withdrawn"),
            @ApiResponse(code = 404, message = "The account is not found"),
            @ApiResponse(code = 500, message = "The money cannot be withdrawn")
    })
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

    @ApiOperation(value = "Transfer money between accounts")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The money was successfully transferred"),
            @ApiResponse(code = 404, message = "The account is not found"),
            @ApiResponse(code = 500, message = "The money cannot be transferred")
    })
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

    @ApiOperation(value = "Get an account by account number")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved an account"),
            @ApiResponse(code = 404, message = "The account is not found")
    })
    @GetMapping(value = "/accounts/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable("accountNumber") int accountNumber) {
        return ResponseEntity.ok(atmService.getAccount(accountNumber));
    }

    @ApiOperation(value = "Change account limit")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully changed limit"),
            @ApiResponse(code = 404, message = "The account is not found"),
            @ApiResponse(code = 500, message = "The limit cannot be changed")
    })
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