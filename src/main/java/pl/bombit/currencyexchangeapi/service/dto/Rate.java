package pl.bombit.currencyexchangeapi.service.dto;

import lombok.Data;

@Data
public class Rate {
    private String no;
    private String effectiveDate;
    private double mid;
}
