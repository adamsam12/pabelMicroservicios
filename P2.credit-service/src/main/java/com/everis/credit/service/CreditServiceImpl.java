package com.everis.credit.service;

import com.everis.credit.entity.Credit;
import com.everis.credit.model.Client;
import com.everis.credit.repository.CreditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditServiceImpl implements CreditService {

    private static final String URL_CLIENT = "http://localhost:8080/";

    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private ApiCall apiCall;

    @Override
    public Mono<Credit> getCreditByCreditId(String creditId) {

        return creditRepository.findById(creditId);
    }

    @Override
    public Mono<Credit> createCredit(Credit credit) {
        return apiCall.getClientByIdMicroservice(credit.getClientId())
                .flatMap(client -> {
                    //asumimos que una cliente personal o empresa puede tener N tarjetas de credito
                    if (client.getClientType() != -1 && credit.getCreditType().equals("T"))  return creditRepository.save(credit);

                    System.out.println("El cliente es de tipo: " + client.getClientType() );
                    System.out.println("El tipo de credito solicitado es: " + credit.getCreditType() );
                    if(client.getClientType() == 0 && credit.getCreditType().equals("P")) { //0 - Personal
                        System.out.println("Entre");
                        Flux<Credit> credits = findCreditByClientId(client.getId());
                        return credits.filter( c -> c.getStatus().equals("ACTIVE"))
                                .count().flatMap(
                                        numberActiveCredits -> {
                                            System.out.println("El numero de creditos activos del cliente personal es: " + numberActiveCredits);
                                            if (numberActiveCredits == 0L) return creditRepository.save(credit);
                                            else return Mono.empty();
                                        });
                    } else if(client.getClientType() == 1 && credit.getCreditType().equals("E")){ // 1 - Empresa
                         return creditRepository.save(credit);
                    } else {
                    	return Mono.empty();
                    }
                });
    }

    @Override
    public Mono<Credit> updateCredit(Credit credit) {
        return getCreditByCreditId(credit.getId())
                .flatMap( existingCredit -> {
                    existingCredit.setCreditType(credit.getCreditType());
                    existingCredit.setStatus(credit.getStatus());
                    existingCredit.setInitialAmount(credit.getInitialAmount());
                    existingCredit.setCurrentAmount(credit.getCurrentAmount());
                    existingCredit.setInterest(credit.getInterest());
                    existingCredit.setPaymentDay(credit.getPaymentDay());
                    //existingCredit.setClientId(credit.getClientId());
                    //existingCredit.setCreditAccountNumber(credit.getCreditAccountNumber());
                    //existingCredit.setCardNumber(credit.getCardNumber());
                    return creditRepository.save(existingCredit);
        });
    }

    @Override
    public Mono<Credit> deleteCredit(String id) {

        return getCreditByCreditId(id)
                .flatMap(c -> creditRepository.deleteById(c.getId()).thenReturn(c));
    }

    @Override
    public Flux<Credit> findAll() {

        return creditRepository.findAll();
    }

    @Override
    public Flux<Credit> findCreditByClientId(String clientId) {
        return creditRepository.findByClientId(clientId);
    }

    @Override
    public Mono<Credit> makeDeposit(String creditId, Float deposit) {
        return getCreditByCreditId(creditId).flatMap( cCredit -> {
            if (cCredit.getCurrentAmount()>0){
                if(cCredit.getCurrentAmount() - deposit < 0){
                    cCredit.setCurrentAmount(0.0F);
                    cCredit.setStatus("CANCELLED");
                }else{
                    cCredit.setCurrentAmount(cCredit.getCurrentAmount() - deposit);
                }
                return creditRepository.save(cCredit);
            }
            else return Mono.empty();
        });
    }
}
