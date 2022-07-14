package com.everis.transactions.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.transactions.entity.Transaction;
import com.everis.transactions.model.BankAccount;
import com.everis.transactions.model.Credit;
import com.everis.transactions.repository.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ApiCall apiCall;
	
	@Override
	public Flux<Transaction> findByAccountId(String accountId) {
		return transactionRepository.findByAccountId(accountId);
	}

	@Override
	public Flux<Transaction> findAll() {
		return transactionRepository.findAll();
	}

	@Override
	public Mono<Transaction> createTransaction(Transaction transaction) {

		if(transaction.getTransactionType().equals("B")) {
			return apiCall.putDepositBankAccount(transaction.getAccountId(), transaction.getAmount())
					.flatMap(currentAccount -> {
						if (!currentAccount.getId().equals("-1")) {
							System.out.println("Se realizó la transacción con un valor de " + transaction.getAmount() + " de la cuenta " +
									currentAccount.getBankAccountNumber() + " de tipo " + currentAccount.getAccountType());
							return transactionRepository.save(transaction);}
						else return Mono.empty();
					});
		} else { // "C" Credito
			return apiCall.putDepositCredit(transaction.getAccountId(), transaction.getAmount())
					.flatMap(currentCredit -> {
						if (!currentCredit.getId().equals("-1")) {
							System.out.println("Se realizó la transacción con un valor de " + transaction.getAmount() + " del crédito " +
									currentCredit.getCreditAccountNumber() + " de tipo " + currentCredit.getCreditType());
							return transactionRepository.save(transaction);
						}
						else return Mono.empty();
					});
		}
//		return transactionRepository.save(transaction);
		
	}

	@Override
	public Mono<Transaction> insertTransaction(Transaction transaction){
		return transactionRepository.save(transaction);
	}

	@Override
	public Mono<Transaction> updateTransaction(Transaction transaction) {
		return getTransaction(transaction.getId())
				.flatMap(eTransaction -> {
					eTransaction.setDescription(transaction.getDescription());
					return transactionRepository.save(eTransaction);
		});
	}

	@Override
	public Mono<Transaction> deleteTransaction(String id) {
		return getTransaction(id).flatMap(c -> transactionRepository.deleteById(c.getId()).thenReturn(c));
	}

	@Override
	public Mono<Transaction> getTransaction(String id) {
		return transactionRepository.findById(id);
	}

	
	
}
