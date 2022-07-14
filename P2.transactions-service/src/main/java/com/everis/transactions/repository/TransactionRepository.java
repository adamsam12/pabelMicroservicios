package com.everis.transactions.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.transactions.entity.Transaction;

import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

	public Flux<Transaction> findByAccountId(String accountId);
	
}
