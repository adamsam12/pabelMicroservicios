package com.everis.transactions.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class Transaction {
	
	@Id
	private String id;
	
	private String description;
	private LocalDate date;
	private Float amount;
	private String accountId;
	private String transactionType; // B - Cuenta Bancaria | C - Credito (Incluye Tarjeta de crédito)
	
}
