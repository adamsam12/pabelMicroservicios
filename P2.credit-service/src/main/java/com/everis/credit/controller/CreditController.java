package com.everis.credit.controller;

import com.everis.credit.entity.Credit;
import com.everis.credit.service.CreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("credit")
@Slf4j
public class CreditController {
    @Autowired
    private CreditService creditService;

    @GetMapping("/")
    public Flux<Credit> findAllCredit(){
        return creditService.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<Credit> getCreditByCreditId(@PathVariable("id") String creditId){
        return creditService.getCreditByCreditId(creditId);
    }

    @PostMapping("/")
    public Mono<Credit> createCredit(@RequestBody Credit credit){

        return creditService.createCredit(credit);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Credit>> updateCredit(@PathVariable("id") String id, @RequestBody Credit credit){
        credit.setId(id);
        return creditService.updateCredit(credit)
                .flatMap(response -> Mono.just(ResponseEntity.ok(response)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Credit>> deleteCredit(@PathVariable("id") String id){
        return creditService.deleteCredit(id)
                .flatMap(response -> Mono.just(ResponseEntity.ok(response)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/client/{id}")
    public Flux<Credit> findCreditByClientId(@PathVariable("id") String clientId){
        return creditService.findCreditByClientId(clientId);
    }

    @PutMapping("/{accountId}/deposit")
    public Mono<ResponseEntity<Credit>> updateBalance(@PathVariable("accountId") String accountId, @RequestParam(required = true) Float quantity){
        return creditService.makeDeposit(accountId, quantity).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));

    }
}
