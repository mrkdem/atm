package com.intive.atm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.intive.atm.models.Account;
import com.intive.atm.models.Customer;
import com.intive.atm.services.AtmServiceImpl;

@SpringBootApplication
public class AtmApplication implements CommandLineRunner {

	@Autowired
	AtmServiceImpl atmService;

	public static void main(String[] args) {
		SpringApplication.run(AtmApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<Customer> customers = new ArrayList<>();

		customers.add(Customer.builder().username("customer_1").account(Account.builder().accountNumber(1111).balance(10000).limit(1000).build()).build());
		customers.add(Customer.builder().username("customer_2").account(Account.builder().accountNumber(2222).balance(20000).limit(2000).build()).build());
		customers.add(Customer.builder().username("customer_3").account(Account.builder().accountNumber(3333).balance(30000).limit(3000).build()).build());
		customers.add(Customer.builder().username("customer_4").account(Account.builder().accountNumber(4444).balance(40000).limit(4000).build()).build());

		atmService.setCustomers(customers);
	}

}
