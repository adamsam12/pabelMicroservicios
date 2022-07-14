package com.everis.customer.service;

import com.everis.customer.entity.Client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientService {

	public Mono<Client> findByDni(String dni);
	public Flux<Client> findByClientType(Integer clientType);
	public Flux<Client> findAll();
	
	public Mono<Client> createClient(Client client);
    public Mono<Client> updateClient(Client client);
    public Mono<Client> deleteClient(String id);
    public Mono<Client> getClient(String id);
    
}
