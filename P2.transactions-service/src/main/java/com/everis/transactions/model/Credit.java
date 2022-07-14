package com.everis.transactions.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credit {

    private String id;
    private String creditType;
    private String status;
    private Float initialAmount;
    private Float currentAmount;
    private Float interest;
    private Integer paymentDay;
    private String creditAccountNumber;
    private String clientId;
    private String cardNumber;

}