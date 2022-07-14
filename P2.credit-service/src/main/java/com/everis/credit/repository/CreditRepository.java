package com.everis.credit.repository;

import com.everis.credit.entity.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CreditRepository extends ReactiveMongoRepository <Credit, String>{
    public Flux<Credit> findByClientId(String clientId);
}
