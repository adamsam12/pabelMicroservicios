package com.everis.customer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.everis.customer.entity.Client;
import com.everis.customer.service.ClientService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("client")
@Slf4j
public class ClientController {

	@Autowired
	private ClientService clientService;
	
	@GetMapping("/")
	public Flux<Client> findAllClient(){
		return clientService.findAll();
	}
	
	@PostMapping("/")
	public Mono<Client> createClient(@RequestBody Client client){
		return clientService.createClient(client);
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Client>> updateClient(@PathVariable("id") String id, @RequestBody Client client){
		client.setId(id);
		return clientService.updateClient(client).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Client>> deleteClient(@PathVariable("id") String id){
		return clientService.deleteClient(id).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Client>> getClientById(@PathVariable("id") String id){
		
		/* Recorre Mono<Client> que viene de getCustomer, si no es null, entrega la respuesta, en caso contrario solo un notFound(). */
		
		return clientService.getClient(id).flatMap(response -> Mono.just(ResponseEntity.ok(response)))
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}
}
