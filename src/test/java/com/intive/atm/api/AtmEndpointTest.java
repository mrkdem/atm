package com.intive.atm.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.intive.atm.models.Account;
import com.intive.atm.models.Customer;
import com.intive.atm.services.AtmService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AtmEndpointTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AtmService atmService;

    private HttpHeaders headers;

    @Before
    public void setup() {
        //given
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    public void shouldFindAllCustomers() {
        //when
        ResponseEntity<List<Customer>> responseEntity = restTemplate.exchange(
                "/api/customers/all",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<Customer>>() {
                });

        //then
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(responseEntity.getBody().size(), is(equalTo(4)));
    }

    @Test
    public void shouldWithdrawCash() {
        //when
        ResponseEntity withdrawResponseEntity = restTemplate.exchange(
                String.format("/api/accounts/withdraw/%d/%d", 1111, 1000),
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                Object.class);

        ResponseEntity<Account> accountResponseEntity = restTemplate.exchange(
                String.format("/api/accounts/%d", 1111),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Account.class);

        //then
        assertThat(withdrawResponseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(withdrawResponseEntity.getBody(), is(equalTo(Boolean.TRUE)));

        assertThat(accountResponseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(accountResponseEntity.getBody().getBalance(), is(equalTo(9000.0)));
    }

    @Test
    public void shouldChangeLimit() {
        //when
        ResponseEntity changeLimitResponseEntity = restTemplate.exchange(
                String.format("/api/accounts/change/%d/limit/%d", 1111, 3000),
                HttpMethod.PUT,
                new HttpEntity<>(headers),
                Object.class);

        ResponseEntity<Account> accountResponseEntity = restTemplate.exchange(
                String.format("/api/accounts/%d", 1111),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Account.class);

        //then
        assertThat(changeLimitResponseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(changeLimitResponseEntity.getBody(), is(equalTo(Boolean.TRUE)));

        assertThat(accountResponseEntity.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(accountResponseEntity.getBody().getLimit(), is(equalTo(3000.0)));
    }
}