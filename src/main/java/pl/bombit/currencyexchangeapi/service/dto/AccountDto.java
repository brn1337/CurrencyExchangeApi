package pl.bombit.currencyexchangeapi.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@ToString
public class AccountDto {
    private String accId;
    private String firstName;
    private String lastName;
    private double balanceInPln;
    private double balanceInUsd;
}
