package pl.bombit.currencyexchangeapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.bombit.currencyexchangeapi.controller.dto.AccountRequest;
import pl.bombit.currencyexchangeapi.controller.dto.BalanceDomain;
import pl.bombit.currencyexchangeapi.service.CurrencyExchangeService;
import pl.bombit.currencyexchangeapi.service.dto.AccountDto;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CurrencyExchangeController {

    public final CurrencyExchangeService currencyExchangeService;
    public String loggedUserAccId;

    @PostMapping( "/createAccount")
    @ResponseStatus(HttpStatus.CREATED)
    public String createAccount(@RequestBody AccountRequest request) {
        return currencyExchangeService.createAccount(request);
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String firstName, @RequestParam String lastName) {
        loggedUserAccId = currencyExchangeService.login(firstName, lastName);
        if (loggedUserAccId != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + loggedUserAccId);
            return new ResponseEntity<>(loggedUserAccId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Account not found", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(  "/checkAccountStatus")
    public ResponseEntity<AccountDto> checkAccountStatus(HttpServletRequest request) {
            String authorizationHeader = request.getHeader("Authorization");
            String accId;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && loggedUserAccId.equals(authorizationHeader.substring(7))) {
                 accId = authorizationHeader.substring(7);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            AccountDto dto = currencyExchangeService.checkAmount(accId);
            if (dto != null) {
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
            }
    }
    @PostMapping("exchangeMoney")
    public ResponseEntity<BalanceDomain> exchangeMoney(HttpServletRequest request, @RequestParam Long amount, @RequestParam String currency) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String accId;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && loggedUserAccId.equals(authorizationHeader.substring(7))) {
            accId = authorizationHeader.substring(7);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(currencyExchangeService.exchangeCurrency(accId, amount, currency), HttpStatus.OK);
    }
}
