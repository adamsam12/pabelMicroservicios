package com.everis.bank.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.bank.entity.BankAccount;

import reactor.core.publisher.Flux;

public interface BankAccountRepository extends ReactiveMongoRepository<BankAccount, String>{

	public Flux<BankAccount> findByClientId(String clientId);
	
	
}
