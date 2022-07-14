package com.everis.transactions.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class BankAccount {
	
    private String id;
    private String description;
    private String accountType; 
    private Integer limitPerMonth;
    private Float commission;
    private String bankAccountNumber;
    private Float balance;
    private Integer transactionDay;
    private Float minimumAmount; // Solo para Cuenta de Ahorro y cliente VIP
    private String clientId;

    private Flux<Signer> signer;
}
