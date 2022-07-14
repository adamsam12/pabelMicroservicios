package com.everis.bank.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

	private String id;
	private String description;
	private LocalDate date;
	private Float amount;
	private String accountId;
	private String transactionType; // B - Cuenta Bancaria | C - Credito (Incluye Tarjeta de crédito)

}
