package com.everis.transactions.service;

import com.everis.transactions.entity.Transaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

	public Flux<Transaction> findByAccountId(String accountId);
	public Flux<Transaction> findAll();
	
	public Mono<Transaction> createTransaction(Transaction transaction);
    public Mono<Transaction> updateTransaction(Transaction transaction);
    public Mono<Transaction> deleteTransaction(String id);
    public Mono<Transaction> getTransaction(String id);

    public Mono<Transaction> insertTransaction(Transaction transaction);
	
}
