package eu.radlinski.playground.exchangerates.client;

import eu.radlinski.playground.exchangerates.model.CurrencyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@Service
public class ExchangeRatesApiService {

    private final WebClient webClient;
    private final String exchangeratesApiKey;
    private final Set<CurrencyType> availableCurrencies;

    public ExchangeRatesApiService(WebClient.Builder webClientBuilder,
                                   @Value("${exchange-rates.exchangeratesapi-io-uri}")String exchangeratesUri,
                                   @Value("${exchange-rates.exchangeratesapi-io-api-key}") String exchangeratesApiKey,
                                   @Value("${exchange-rates.import-currencies}") Set<CurrencyType> availableCurrencies) {
        this.exchangeratesApiKey = exchangeratesApiKey;
        this.availableCurrencies = availableCurrencies;
        this.webClient = webClientBuilder.baseUrl(exchangeratesUri).build();
    }

    public ExchangeRatesData getHistoricalRates(LocalDate date){
        String currenciesString = availableCurrencies.stream()
                .map(CurrencyType::name)
                .collect(Collectors.joining(","));
        return webClient.get().uri(uriBuilder -> uriBuilder
                .path("/v1/{ratesDate}")
                .queryParam("access_key", exchangeratesApiKey)
                .queryParam("symbols", currenciesString)
                .build(date))
                .retrieve()
                .bodyToMono(ExchangeRatesData.class)
                .blockOptional(Duration.ofMillis(1000))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, null, null));
    }
}
