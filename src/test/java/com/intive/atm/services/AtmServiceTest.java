package com.intive.atm.services;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.intive.atm.exeptions.AtmException;
import com.intive.atm.models.Account;
import com.intive.atm.models.Customer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AtmServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private AtmService atmService;

    @Test
    public void shouldAddCustomerWithAccount() {
        //given
        Customer customer = Customer.builder()
                           .username("joe.doe")
                           .account(Account.builder()
                                           .accountNumber(6666)
                                           .balance(60000)
                                           .limit(6000)
                                           .build())
                           .build();

        //when
        atmService.addCustomer(customer);
        Customer addedCustomer = atmService.getCustomer(customer.getUsername());

        //then
        assertThat(addedCustomer, notNullValue());
        assertThat(addedCustomer.getUsername(), is(equalTo("joe.doe")));
        assertThat(addedCustomer.getAccount(), notNullValue());
        assertThat(addedCustomer.getAccount().getAccountNumber(), is(equalTo(6666)));
    }

    @Test
    public void shouldThrowExceptionWhenCustomerNotFound() {
        //given
        String username = "test.test";
        thrown.expect(AtmException.class);
        thrown.expectMessage(String.format("The customer not found %s", username));

        //when
        atmService.getCustomer(username);

        //then
    }

    @Test
    public void shouldThrowExceptionWhenAccountNotFound() {
        //given
        int accountNumber = 77777777;
        thrown.expect(AtmException.class);
        thrown.expectMessage(String.format("The account not found %d", accountNumber));

        //when
        atmService.getAccount(accountNumber);

        //then
    }

    @Test
    public void shouldDepositCash() {
        //given
        int accountNumber = 3333;

        //when
        boolean isDeposited = atmService.deposit(accountNumber, 1000);
        Account account = atmService.getAccount(accountNumber);

        //then
        assertTrue(isDeposited);
        assertThat(account.getBalance(), is(equalTo(31000.0)));
    }

    @Test
    public void shouldTransferCash() {
        //given
        int accountNumberFrom = 2222;
        int accoutNumberTo = 1111;

        //when
        boolean isTranferred = atmService.transfer(accountNumberFrom, accoutNumberTo, 1500);
        Account accountFrom = atmService.getAccount(accountNumberFrom);
        Account accountTo = atmService.getAccount(accoutNumberTo);

        //then
        assertTrue(isTranferred);
        assertThat(accountFrom.getBalance(), is(equalTo(18500.0)));
        assertThat(accountTo.getBalance(), is(equalTo(11500.0)));
    }
}
