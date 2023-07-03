package pl.bombit.currencyexchangeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "pl.bombit.currencyexchangeapi.repository")
@SpringBootApplication
public class CurrencyExchangeApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyExchangeApiApplication.class, args);
    }

}
