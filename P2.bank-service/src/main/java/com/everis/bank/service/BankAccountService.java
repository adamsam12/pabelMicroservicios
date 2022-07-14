package com.everis.bank.service;

import com.everis.bank.controller.BankAccountController;
import com.everis.bank.entity.BankAccount;

import com.everis.bank.entity.Signer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankAccountService {

	public Flux<BankAccount> findByClientId(String clientId);
	public Flux<BankAccount> findAll();
	
	public Mono<BankAccount> createBankAccount(BankAccount bankAccount);
    public Mono<BankAccount> updateBankAccount(BankAccount bankAccount);
    public Mono<BankAccount> deleteBankAccount(String id);
    public Mono<BankAccount> getBankAccount(String id);
    
    // Negocio
    public Mono<BankAccount> makeDeposit(String accountId, Float deposit);

    public Mono<BankAccount> addSigner(String id, Signer signer);
	public Mono<BankAccount> updateSigner(String id, Signer signer);
    public Mono<BankAccount> deleteSigner(String id, String dni);

    public Mono<BankAccount> transfer(String idA, String idB, Float deposit);
}
