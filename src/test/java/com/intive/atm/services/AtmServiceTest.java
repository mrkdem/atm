package com.intive.atm.services;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
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
        assertEquals("joe.doe", addedCustomer.getUsername());
        assertThat(addedCustomer.getAccount(), notNullValue());
        assertEquals(6666, addedCustomer.getAccount().getAccountNumber());
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
        assertEquals(31000, account.getBalance(), 0);
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
        assertEquals(18500, accountFrom.getBalance(), 0);
        assertEquals(11500, accountTo.getBalance(), 0);
    }
}
