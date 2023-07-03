package pl.bombit.currencyexchangeapi.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExchangeResponse {
    private String table;
    private String currency;
    private String code;
    private List<Rate> rates;
}
