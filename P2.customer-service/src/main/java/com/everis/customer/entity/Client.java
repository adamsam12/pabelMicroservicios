package com.everis.customer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    private String id;

    private String dni;
    private String lastName;
    private String firstName;
    private String names;
    private LocalDate dateBirth;
    private String address;
    private String nationality;
    private Integer clientType; //0 Personal; 1 Empresarial
    private String clientSubType; // "V" Vip; "P" PYME

}
