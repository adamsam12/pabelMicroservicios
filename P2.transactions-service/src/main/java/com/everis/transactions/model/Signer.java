package com.everis.transactions.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signer {
    private String id;
    private String signerType;
    private String dni;
    private String name;
    private String firstName;
    private String lastName;
    private String status; //ACTIVE OR RETIRED
}
