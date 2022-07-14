package com.everis.transactions.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everis.transactions.entity.Transaction;
import com.everis.transactions.service.TransactionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@GetMapping("/")
	public Flux<Transaction> findAllTransactions(){
		return transactionService.findAll();
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Transaction>> getTransactionById(@PathVariable("id") String id){
		
		return transactionService.getTransaction(id).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}
	

	@GetMapping("/account/{accountId}")
	public Flux<Transaction> getTransactionByAccountId(@PathVariable("accountId") String accountId){
		return transactionService.findByAccountId(accountId);
	}
	
	
	@PostMapping("/")
	public Mono<Transaction> createTransaction(@RequestBody Transaction transaction){
		return transactionService.createTransaction(transaction);
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Transaction>> updateTransaction(@PathVariable("id") String id, @RequestBody Transaction client){
		client.setId(id);
		return transactionService.updateTransaction(client).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Transaction>> deleteTransaction(@PathVariable("id") String id){
		return transactionService.deleteTransaction(id).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}

	//insertTransaction, simplemente inserta en la tabla transaccion
	//mientras que createTransaction, ademas de insertar tiene otra logica para el deposito y retiro de un tipo de cuenta bancario ó credito
	@PostMapping("/insert/")
	public Mono<Transaction> insertTransaction(@RequestBody Transaction transaction){
		return transactionService.insertTransaction(transaction);
	}
}
