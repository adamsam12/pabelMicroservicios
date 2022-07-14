package com.everis.bank.service;

import com.everis.bank.entity.Signer;

import com.everis.bank.model.Credit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.bank.entity.BankAccount;
import com.everis.bank.model.Transaction;
import com.everis.bank.repository.BankAccountRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

	@Autowired
	private BankAccountRepository bankAccountRepository;

	@Autowired
	private ApiCall apiCall;
	
	@Override
	public Flux<BankAccount> findByClientId(String clientId) {
		return bankAccountRepository.findByClientId(clientId);
	}

	@Override
	public Flux<BankAccount> findAll() {
		return bankAccountRepository.findAll();
	}
	
	@Override
	public Mono<BankAccount> createBankAccount(BankAccount bankAccount) {
		return apiCall.getClientByIdMicroservice(bankAccount.getClientId())
				.flatMap( client -> {

					if(client.getClientType() == 0) { // Personal

						Flux<BankAccount> accounts = findByClientId(client.getId());
						return accounts
								.filter(a -> a.getAccountType().equals(bankAccount.getAccountType())).count().flatMap(
								numberAccounts -> {
									log.info("El numero de cuentas de tipo '" + bankAccount.getAccountType() + "' es: "+ numberAccounts);
									//Un cliente personal solo puede tener una cuenta por cada tipo
									if (numberAccounts == 0L) {
										//si eres vip, para crear una cuenta ahorro debes tener tarjeta de credito
										if(client.getClientSubType().equals("V")) {
											Flux<Credit> credits = apiCall.getCreditByClientIdMicroservice(client.getId());
											return saveIfHasACreditCard(credits, bankAccount);
										}
										else return bankAccountRepository.save(bankAccount);
									}
									else return Mono.empty();
								});

					} else if(client.getClientType() == 1){ // Empresa
						// Solo puede tener cuenta corriente
						if (bankAccount.getAccountType().equals("A") || bankAccount.getAccountType().equals("P")) return Mono.empty();
						else {
							//si eres PYME y no quieres tener comision de mantenimiento
							if (client.getClientSubType().equals("P") && bankAccount.getCommission()==0F){
								Flux<Credit> credits = apiCall.getCreditByClientIdMicroservice(client.getId());
								return saveIfHasACreditCard(credits, bankAccount);
							}
							else return bankAccountRepository.save(bankAccount);
						}
					}
					else return Mono.empty();

				});

	}

	private Mono<BankAccount> saveIfHasACreditCard(Flux<Credit> credits, BankAccount bankAccount){
		return credits.filter( c -> c.getId().equals("-1")).count().flatMap(
				invalidCredit ->{
					if (invalidCredit>0) return Mono.empty();
					else
						return credits.filter(c -> c.getCreditType().equals("T")).count().flatMap(
								numberOfCreditCards -> {
									if (numberOfCreditCards>0)  return bankAccountRepository.save(bankAccount);
									else return Mono.empty();
								}
						);
				}
		);
	}

	@Override
	public Mono<BankAccount> updateBankAccount(BankAccount bankAccount) {
		return getBankAccount(bankAccount.getId())
				.flatMap(existingBankAccount -> {
					existingBankAccount.setBalance(bankAccount.getBalance());
					return bankAccountRepository.save(existingBankAccount);
		});
	}

	@Override
	public Mono<BankAccount> deleteBankAccount(String id) {
		return getBankAccount(id).flatMap(b -> bankAccountRepository.deleteById(b.getId()).thenReturn(b));
	}

	@Override
	public Mono<BankAccount> getBankAccount(String id) {
		return bankAccountRepository.findById(id).flatMap( eBankAccount -> {
			if (eBankAccount.getAccountType().equals("C") && eBankAccount.getSigners() != null) { //ver si es cuenta corriente
				Flux <Signer> signerFlux = eBankAccount.getSigners().filter(eSigner -> !eSigner.getStatus().equals("RETIRED"));
				eBankAccount.setSigners(signerFlux);
				return Mono.just(eBankAccount);
			}else
				return Mono.just(eBankAccount);
		});
	}

	@Override
	public Mono<BankAccount> makeDeposit(String accountId, Float deposit) {
		// Cuenta bancaria en donde se realiza el movimiento
		Mono<BankAccount> bankAccountDB = getBankAccount(accountId);
		return bankAccountDB.flatMap(currentAccount -> {
			// Obteniendo transacciones según número de cuenta
			Flux<Transaction> transactions = apiCall.getTransactionByAccountIdMicroservice(accountId);
			// Pregunto si la transaccion es valida
			return transactions.filter( t -> t.getId().equals("-1")).count().flatMap(invalidTransaction -> {
				if (invalidTransaction > 0)
					return Mono.empty();
				else
					return transactions.filter(c -> c.getDate().isAfter(LocalDate.now().withDayOfMonth(1)) && c.getDate().isBefore(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())))
							.count().flatMap(numberTransactions -> {

								log.info("El numero de transacciones durante este mes es: "+ numberTransactions);

								if (currentAccount.getAccountType().equals("A") || currentAccount.getAccountType().equals("C"))
									return saveDeposit(numberTransactions, currentAccount, deposit);
								else if (currentAccount.getAccountType().equals("P") ){
									//Las cuentas plazo fijo solo permiten un movimiento al mes y en un dia específico del mes.
									if ( currentAccount.getTransactionDay() == LocalDate.now().getDayOfMonth()) {
										return saveDeposit(numberTransactions, currentAccount, deposit);
									}
									else return Mono.empty();
								} else return Mono.empty();
							});
			});
		});
	}

	private Mono<BankAccount> saveDeposit(Long numberTransactions, BankAccount currentAccount, Float deposit){
		//si no supera el limite mensual
		if (numberTransactions < currentAccount.getLimitPerMonth()) {
			if(currentAccount.getBalance() + deposit > 0) { // Si el movimiento deja el saldo actual > 0
				currentAccount.setBalance(currentAccount.getBalance() + deposit);
				return bankAccountRepository.save(currentAccount);
			} else {
				return Mono.empty();
			}
		}
		//si supera el limite mensual, se le cobra comision
		else {
			if(currentAccount.getBalance() + deposit - currentAccount.getCommission()> 0) { // Si el movimiento deja el saldo actual > 0
				currentAccount.setBalance(currentAccount.getBalance() + deposit - currentAccount.getCommission());
				return bankAccountRepository.save(currentAccount);
			} else {
				return Mono.empty();
			}
		}
	}
	
	/* Lógca de titulares o firmantes */
	@Override
	public Mono<BankAccount> addSigner(String id, Signer signer) {
		return getBankAccount(id).flatMap( eBankAccount -> {
				if (eBankAccount.getAccountType().equals("C")){ // Si es cuenta corriente
					if (eBankAccount.getSigners()!=null){
						return eBankAccount.getSigners().filter( eSigner -> eSigner.getDni().equals(signer.getDni()))
								.count().flatMap( // Si existe el Dni
										numberOfCoincidence -> {
											if (numberOfCoincidence == 0L) {
												Flux<Signer> lts = eBankAccount.getSigners().concatWith(Flux.just(signer));
												eBankAccount.setSigners(lts);
												return bankAccountRepository.save(eBankAccount);
											}else
												return Mono.empty();
										}
								);
					}
					else{
						eBankAccount.setSigners(Flux.just(signer));
						return bankAccountRepository.save(eBankAccount);
					}
				}else
					return Mono.empty();
				});
	}

	@Override
	public Mono<BankAccount> updateSigner(String id, Signer signer) {
		return getBankAccount(id).flatMap( eBankAccount -> { //encontrar la cuenta bancaria
			if (eBankAccount.getAccountType().equals("C") && eBankAccount.getSigners()!=null){ //ver si es cuenta corriente
				return eBankAccount.getSigners().filter( eSigner -> eSigner.getDni().equals(signer.getDni()))
						.elementAt(0).flatMap(esigner -> {
							esigner.setSignerType(signer.getSignerType());
							esigner.setName(signer.getName());
							esigner.setFirstName(signer.getFirstName());
							esigner.setLastName(signer.getLastName());
							return bankAccountRepository.save(eBankAccount);
						});
			}
			else{
				return Mono.empty();
			}
		});
	}

	//ELIMINACION LOGICA
	@Override
	public Mono<BankAccount> deleteSigner(String id, String dni) {
		return getBankAccount(id).flatMap(eBankAccount -> { // Encontrar la cuenta bancaria
			if (eBankAccount.getAccountType().equals("C") && eBankAccount.getSigners() != null) { // Ver si es cuenta corriente
				return eBankAccount.getSigners().filter( eSigner -> eSigner.getDni().equals(dni))
						.elementAt(0).flatMap(es -> {
							es.setStatus("RETIRED");
							return bankAccountRepository.save(eBankAccount);
						});

			}
			else
				return Mono.empty();
		});
	}

	@Override
	public Mono<BankAccount> transfer(String idA, String idB, Float deposit){
		//A es la cuenta origen y B es la cuenta destino
		//obtenemos las dos cuentas bancarias
		return getBankAccount(idA).flatMap(bankAccountA -> {
			return getBankAccount(idB).flatMap(bankAccountB -> {
				//Si la cuenta A deposita a la cuenta B, A debe tener el saldo
				if(bankAccountA.getBalance() >= deposit){
					//se esta considerando que las transacciones bancarias no siguen la logica descrita en el metodo makeDeposit
					return apiCall.setTransaction(idB, deposit, "Deposito de la cuenta A").flatMap( transactionB -> {
						if (transactionB.getId().equals("-1")) //transaccion fallida
							return Mono.empty();
						else{
							bankAccountB.setBalance(bankAccountB.getBalance() + deposit); //se deposita el dinero de la cuenta B
							return bankAccountRepository.save(bankAccountB).flatMap(b -> {
								return apiCall.setTransaction(idA, deposit*(-1F), "Tranferencia a la cuenta B").flatMap(transactionA -> {
									if (transactionA.getId().equals("-1")) {//transaccion fallida
										//rollback
										bankAccountB.setBalance(bankAccountB.getBalance() - deposit);
										return  bankAccountRepository.save(bankAccountB).flatMap( bb -> Mono.empty());
									}else{
										bankAccountA.setBalance(bankAccountA.getBalance() - deposit);
										return bankAccountRepository.save(bankAccountA);
									}
								});
							});
						}
					});
				}
				else return Mono.empty();
			});
		});
	}

}
