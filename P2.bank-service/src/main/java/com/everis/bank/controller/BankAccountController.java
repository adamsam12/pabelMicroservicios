package com.everis.bank.controller;

import com.everis.bank.entity.Signer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.everis.bank.entity.BankAccount;
import com.everis.bank.service.BankAccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("bank-account")
public class BankAccountController {

	@Autowired
	private BankAccountService bankAccountService;
	
	@GetMapping("/")
	public Flux<BankAccount> findAllBankAccount(){
		return bankAccountService.findAll();
	}
	
	@GetMapping("/client/{clientId}")
	public Flux<BankAccount> getBankAccountByClientId(@PathVariable("clientId") String clientId){
		return bankAccountService.findByClientId(clientId);
	}
	
	@PostMapping("/")
	public Mono<BankAccount> createBankAccount(@RequestBody BankAccount bankAccount){
		return bankAccountService.createBankAccount(bankAccount);
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<BankAccount>> updateBankAccount(@PathVariable("id") String id, @RequestBody BankAccount bankAccount){
		bankAccount.setId(id);
		return bankAccountService.updateBankAccount(bankAccount).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

	}
	
	@PutMapping("/{accountId}/deposit")
	public Mono<ResponseEntity<BankAccount>> updateBalance(@PathVariable("accountId") String accountId, @RequestParam(required = true) Float quantity){
//		bankAccount.setId(id);
		return bankAccountService.makeDeposit(accountId, quantity).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));

	}

	@PutMapping("transfer/{accountIdA}/{accountIdB}/deposit")
	public Mono<ResponseEntity<BankAccount>> wireTransfer(@PathVariable("accountIdA") String accountIdA,
														  @PathVariable("accountIdB") String accountIdB,
														  @RequestParam(required = true) Float quantity){
		return bankAccountService.transfer(accountIdA, accountIdB, quantity).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
	}

	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<BankAccount>> deleteBankAccount(@PathVariable("id") String id){
		return bankAccountService.deleteBankAccount(id).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<BankAccount>> getBankAccountById(@PathVariable("id") String id){
		
		return bankAccountService.getBankAccount(id).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}
	
//	@PutMapping("/{id}")
//	public Mono<ResponseEntity<BankAccount>> updateBankAccount(@PathVariable("id") String id, @RequestBody BankAccount bankAccount){
//		bankAccount.setId(id);
//		return bankAccountService.updateBankAccount(bankAccount).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
//				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
//
//	}
//
	@PutMapping("/{id}/signer")
	public Mono<ResponseEntity<BankAccount>> addSigner(@PathVariable("id") String id, @RequestBody Signer signer){
		return bankAccountService.addSigner(id, signer).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

	}

	@PutMapping("/{id}/signer/update")
	public Mono<ResponseEntity<BankAccount>> updateSigner(@PathVariable("id") String id, @RequestBody Signer signer){
		return bankAccountService.updateSigner(id, signer).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

	}

	@PutMapping("/{id}/signer/delete/{dni}")
	public Mono<ResponseEntity<BankAccount>> deleteSigner(@PathVariable("id") String id, @PathVariable("dni") String dni){
		return bankAccountService.deleteSigner(id, dni).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

	}
	
}
