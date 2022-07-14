package com.everis.credit.entity;

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

    @Id
    private String id;

    private String creditType; //(P) personal, (E) empresarial, (T) tarjeta credito
    private String status; //"ACTIVO", "CANCELADO"
    private Float initialAmount;
    private Float currentAmount;
    private Float interest;
    private Integer paymentDay;
    private String creditAccountNumber;
    private String clientId;

    private String cardNumber;

}