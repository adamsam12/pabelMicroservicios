package com.everis.credit.service;

import com.everis.credit.entity.Credit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditService {

    public Mono<Credit> getCreditByCreditId(String creditId);
    public Mono<Credit> createCredit(Credit credit);
    public Mono<Credit> updateCredit(Credit credit);
    public Mono<Credit> deleteCredit(String creditId);

    public Flux<Credit> findAll();
    public Flux<Credit> findCreditByClientId(String clientId);

    public Mono<Credit> makeDeposit(String creditId, Float deposit);
}
