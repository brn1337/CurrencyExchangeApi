package pl.bombit.currencyexchangeapi.controller.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class AccountRequest {
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private Long initialBalance;
}
