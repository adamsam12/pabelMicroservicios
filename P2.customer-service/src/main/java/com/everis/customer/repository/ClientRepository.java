package com.everis.customer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.everis.customer.entity.Client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientRepository extends ReactiveMongoRepository<Client, String>{

	public Mono<Client> findByDni(String dni);
	public Flux<Client> findByClientType(Integer clientType);
	
}
