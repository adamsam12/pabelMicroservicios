package com.everis.bank.service;

import com.everis.bank.model.Client;
import com.everis.bank.model.Credit;
import com.everis.bank.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ApiCall {

    private static final String URL_CLIENT = "http://customer-service:8080/";
    private static final String URL_TRANSACTIONS = "http://transactions-service:8084/";
    private static final String URL_CREDIT = "http://credit-service:8082/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;


    public Mono<Client> getClientByIdMicroservice(String clientId){

        CircuitBreaker cb = circuitBreakerFactory.create("clientServiceBreaker");
        return Mono.just(cb.run( () ->
                        restTemplate.getForObject(URL_CLIENT + "client/" + clientId, Client.class)
                        , throwable -> fallbackGetClientById()
        ));
    }

    public Client fallbackGetClientById( ){
        Client c = new Client();
        c.setClientType(-1);
        return c;
    }

    public Flux<Transaction> getTransactionByAccountIdMicroservice(String accountId){
        CircuitBreaker cb = circuitBreakerFactory.create("clientServiceBreaker");

         return Flux.fromArray(cb.run( () ->
                         restTemplate.getForEntity(URL_TRANSACTIONS + "transaction/account/" + accountId, Transaction[].class).getBody()
                        , throwable -> fallbackGetTransaction()));
    }
    public Transaction[] fallbackGetTransaction( ){
        Transaction[] t = new Transaction[1];
        t[0] = new Transaction("-1", "-", LocalDate.now().plus(1, ChronoUnit.MONTHS), 0F, "-","-");
        return t;
    }

    public Mono<Transaction> setTransaction(String accountId, Float deposit, String description){
        CircuitBreaker cb = circuitBreakerFactory.create("clientServiceBreaker");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject t = new JSONObject();
        t.put("description", description);
        t.put("date", LocalDate.now());
        t.put("amount", deposit);
        t.put("accountId", accountId);
        t.put("transactionType", "B");
        HttpEntity<String> entity = new HttpEntity<String>(t.toString(), headers);

        return Mono.just(cb.run( () ->
                        restTemplate.postForObject(URL_TRANSACTIONS + "transaction/insert/", entity, Transaction.class)
                , throwable -> fallbackSetTransaction())
        );
    }

    public Transaction fallbackSetTransaction() {
        Transaction t = new Transaction();
        t.setId("-1");
        return t;
    }

    public Flux<Credit> getCreditByClientIdMicroservice(String clientId){
        CircuitBreaker cb = circuitBreakerFactory.create("clientServiceBreaker");
        return Flux.fromArray(cb.run( () ->
                        restTemplate.getForEntity(URL_CREDIT + "/credit/client/" + clientId, Credit[].class).getBody()
                , throwable -> fallbackGetCredit()));
    }
    public Credit[] fallbackGetCredit(){
        Credit[] t = new Credit[1];
        t[0] = new Credit();
        t[0].setId("-1");
        return t;
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
