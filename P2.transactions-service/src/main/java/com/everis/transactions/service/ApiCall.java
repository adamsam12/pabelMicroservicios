package com.everis.transactions.service;

import com.everis.transactions.model.BankAccount;
import com.everis.transactions.model.Credit;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@Service
public class ApiCall {

    private static final String URL_BANK = "http://bank-service:8081/";
    private static final String URL_CREDIT = "http://credit-service:8082/";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    public Mono<BankAccount> putDepositBankAccount(String accountId, Float amount){
        CircuitBreaker cb = circuitBreakerFactory.create("clientServiceBreaker");
        String url = URL_BANK + "bank-account/" + accountId + "/deposit?quantity=" + amount;

        return Mono.just(cb.run( () ->
                        restTemplate.exchange(url, HttpMethod.PUT, null, BankAccount.class).getBody()
                        , throwable -> fallbackPutDepositBankAccount()
                ));
    }
    public BankAccount fallbackPutDepositBankAccount(){
        System.out.println("Fallback Bank Account");
        BankAccount b = new BankAccount();
        b.setId("-1");
        return b;
    }

    public Mono<Credit> putDepositCredit(String accountId, Float amount){
        CircuitBreaker cb = circuitBreakerFactory.create("clientServiceBreaker");
        String url = URL_CREDIT  + "credit/" + accountId + "/deposit?quantity=" + amount;

        return Mono.just(cb.run( () ->
                restTemplate.exchange(url, HttpMethod.PUT, null, Credit.class).getBody()
                        , throwable -> fallbackPutDepositCredit()
                ));
    }

    public Credit fallbackPutDepositCredit(){
        System.out.println("Fallback Credit Account");
        Credit c = new Credit();
        c.setId("-1");
        return c;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
