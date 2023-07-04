package pl.bombit.currencyexchangeapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.bombit.currencyexchangeapi.controller.dto.AccountRequest;
import pl.bombit.currencyexchangeapi.controller.dto.BalanceDomain;
import pl.bombit.currencyexchangeapi.repository.AccountJpaRepository;
import pl.bombit.currencyexchangeapi.repository.entity.AccountEntity;
import pl.bombit.currencyexchangeapi.service.dto.AccountDto;
import pl.bombit.currencyexchangeapi.service.dto.ExchangeResponse;
import pl.bombit.currencyexchangeapi.service.dto.Rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private final AccountJpaRepository accountRepository;

    @Override
    @Transactional
    public String createAccount(AccountRequest request) {
        return Optional.of(accountRepository.save(mapDtoToEntity(AccountDto.builder()
                        .accId(createAccIdForNewAccount())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .balanceInPln(request.getInitialBalance())
                        .balanceInUsd(0L)
                        .build())))
                .map(AccountEntity::getAccId)
                .orElseThrow(() -> new IllegalStateException("Failed to create the account."));
    }

    @Override
    @Transactional(readOnly = true)
    public String login(String firstName, String lastName) {
        return Optional.of(accountRepository.findByFirstNameAndLastName(firstName, lastName))
                .map(AccountEntity::getAccId)
                .orElseThrow(() -> new NoSuchElementException("User not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto checkAmount(String accId) {
        return Optional.of(accountRepository.findByAccId(accId))
                .map(this::mapEntityToDto)
                .orElse(null);
    }

    @Override
    public BalanceDomain exchangeCurrency(String accId, double amount, String currency) throws IOException {
        double rate = getUsdValueFromNbpApi();
        AccountDto accountDto = mapEntityToDto(accountRepository.findByAccId(accId));
        if (currency.equalsIgnoreCase("pln") && amount < accountDto.getBalanceInPln()) {
            accountDto.setBalanceInPln(accountDto.getBalanceInPln() - amount);
            accountDto.setBalanceInUsd(rate / amount + accountDto.getBalanceInUsd());
            accountRepository.save(mapDtoToEntity(accountDto));

        } else if (currency.equalsIgnoreCase("usd") && amount < accountDto.getBalanceInUsd()){
            accountDto.setBalanceInUsd(accountDto.getBalanceInUsd() - amount);
            accountDto.setBalanceInPln(rate * amount + accountDto.getBalanceInPln());
            accountRepository.save(mapDtoToEntity(accountDto));
        }
        return BalanceDomain.builder()
                .balanceInPln("Posiadasz :" + accountDto.getBalanceInPln() + " PLN")
                .balanceInUsd("Posiadasz :" + accountDto.getBalanceInUsd() + " USD")
                .build();
    }

    private double getUsdValueFromNbpApi() throws IOException {
        String apiUrl = "http://api.nbp.pl/api/exchangerates/rates/a/usd/today/";

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            ExchangeResponse exchangeResponse = objectMapper.readValue(response.toString(), ExchangeResponse.class);
            List<Rate> rates = exchangeResponse.getRates();
            if (!rates.isEmpty()) {
                Rate rate = rates.get(0);
                return rate.getMid();
            } else {
                throw new IOException("nie znaleziono kursu waluty");
            }
        } else {
            throw new IOException("Nie znaleziono waluty: " + responseCode);
        }
    }

    private String createAccIdForNewAccount() {
        return UUID.randomUUID().toString();
    }

    private AccountEntity mapDtoToEntity(AccountDto dto) {
        return AccountEntity.builder()
                .accId(createAccIdForNewAccount())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .balanceInPln(dto.getBalanceInPln())
                .balanceInUsd(0d)
                .build();
    }

    private AccountDto mapEntityToDto(AccountEntity entity) {
        return AccountDto.builder()
                .accId(entity.getAccId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .balanceInUsd(entity.getBalanceInUsd())
                .balanceInPln(entity.getBalanceInPln())
                .build();
    }
}
