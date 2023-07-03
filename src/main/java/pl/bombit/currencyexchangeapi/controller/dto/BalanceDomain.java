package pl.bombit.currencyexchangeapi.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class BalanceDomain {
    private String balanceInPln;
    private String balanceInUsd;
}
