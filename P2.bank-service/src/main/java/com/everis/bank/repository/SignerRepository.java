package com.everis.bank.repository;

import com.everis.bank.entity.BankAccount;
import com.everis.bank.entity.Signer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface SignerRepository extends ReactiveMongoRepository<Signer, String> {
    public Flux<BankAccount> findByDni(String currentAccountId);
}
