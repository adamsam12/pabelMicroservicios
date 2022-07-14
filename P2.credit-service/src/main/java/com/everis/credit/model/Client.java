package com.everis.credit.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    private String id;
    private String dni;
    private String lastName;
    private String firstName;
    private String names;
    private LocalDate dateBirth;
    private String address;
    private String nationality;
    private Integer clientType;
    private String clientSubType;

}