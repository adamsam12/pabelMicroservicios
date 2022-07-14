package com.everis.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.everis.customer.entity.Client;
import com.everis.customer.repository.ClientRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientServiceImpl implements ClientService {

	@Autowired
	private ClientRepository clientRepository;
	
	@Override
	public Mono<Client> findByDni(String dni) {
		return clientRepository.findByDni(dni);
	}

	@Override
	public Flux<Client> findByClientType(Integer clientType) {
		return clientRepository.findByClientType(clientType);
	}

	@Override
	public Flux<Client> findAll() {
		return clientRepository.findAll();
	}
	
	@Override
	public Mono<Client> createClient(Client client) {
		return clientRepository.save(client);
	}

	@Override
	public Mono<Client> updateClient(Client client) {

		return getClient(client.getId())
				.flatMap(existingClient -> {
					existingClient.setDni(client.getDni());
					existingClient.setLastName(client.getLastName());
					existingClient.setFirstName(client.getFirstName());
					existingClient.setNames(client.getNames());
					existingClient.setDateBirth(client.getDateBirth());
					existingClient.setAddress(client.getAddress());
					existingClient.setNationality(client.getNationality());
					existingClient.setClientType(client.getClientType());
					return clientRepository.save(existingClient);
		});
	}

	@Override
	public Mono<Client> deleteClient(String id) {
		return getClient(id).flatMap(c -> clientRepository.deleteById(c.getId()).thenReturn(c));
	}

	@Override
	public Mono<Client> getClient(String id) {
		
		return clientRepository.findById(id);
	}
	
}
