package pl.bombit.currencyexchangeapi.service;

import pl.bombit.currencyexchangeapi.controller.dto.AccountRequest;
import pl.bombit.currencyexchangeapi.controller.dto.BalanceDomain;
import pl.bombit.currencyexchangeapi.service.dto.AccountDto;

import java.io.IOException;

public interface CurrencyExchangeService {
    String createAccount(AccountRequest request);

    String login(String firstName, String lastName);

    AccountDto checkAmount(String accId);

    BalanceDomain exchangeCurrency(String accId, double amount, String currency) throws IOException;
}
