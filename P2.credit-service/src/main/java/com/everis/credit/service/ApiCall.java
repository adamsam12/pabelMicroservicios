package com.everis.credit.service;

import com.everis.credit.model.Client;
import org.apache.maven.doxia.logging.SystemStreamLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;


@Service
public class ApiCall {

    private static final String URL_CLIENT = "http://customer-service:8080/";

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
        System.out.println("Entre al fallbaclGetClientById");
        Client c = new Client();
        c.setClientType(-1);
        return c;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}