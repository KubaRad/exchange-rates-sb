package eu.radlinski.playground.exchangerates.restservice;

import eu.radlinski.playground.exchangerates.client.ExchangeRatesApiService;
import eu.radlinski.playground.exchangerates.client.ExchangeRatesData;
import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.services.DateTools;
import eu.radlinski.playground.exchangerates.services.RatesOutput;
import eu.radlinski.playground.exchangerates.services.RatesStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@RestController
@RequestMapping("/api/initdb")
public class InitDatabaseResource {
    private final  static Set<String> CURRENCY_STRINGS = Arrays.stream(CurrencyType.values()).map(CurrencyType::name).collect(Collectors.toSet());

    private final ExchangeRatesApiService ratesApiService;
    private final RatesStorageService ratesStorageService;

    @Autowired
    public InitDatabaseResource(ExchangeRatesApiService ratesApiService, RatesStorageService ratesStorageService) {
        this.ratesApiService = ratesApiService;
        this.ratesStorageService = ratesStorageService;
    }


    @GetMapping()
    @ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Data are succesfuly downloaded and stored",
                        content = {
                                @Content(mediaType = "application/json",
                                schema = @Schema(implementation = InitDatabaseResource.ImportInfo.class))
                        }
                ),
                @ApiResponse(
                        responseCode = "500",
                        description = "Problem with connection to exchangeratesapi.io serwer or internal processing problem. Detailed info in json object",
                        content = {
                                @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorDescription.class))
                        }
                )
        }
    )
    @Operation(
            summary = "Init database using data retrieved from exchangeratesapi.io API",
            description = "Connect to exchange rates API and download the data for last year. According to limitations only one day for month is loaded. Data are stored in service datastore. ")

    public ImportInfo initDb(){
        LocalDateTime startTime = LocalDateTime.now();
        List<RatesOutput> rates = new ArrayList<>();
        Set<String> errors = new HashSet<>();
        List<LocalDate> lastYear = DateTools.generateLastYearFirstDaysForNow();
        boolean generalClientException = false;
        for(LocalDate date:lastYear){
            try{
                ratesApiService.getHistoricalRates(date).ifPresentOrElse(hr -> {
                    fromExchangeratesData(hr).ifPresent(rates::add);
                }, () -> {
                    errors.add("Client process returned empty value");
                });
            } catch (Exception e){
                errors.add(e.getLocalizedMessage());
                if(!(e instanceof WebClientResponseException.NotFound)){
                    /*
                     * General error. Probably it is impossible to fulfill request in reasonable time
                     */
                    generalClientException = true;
                    break;
                }
            }

        }

         if(rates.isEmpty() || generalClientException){
             throw new ExchangeRatesCommunicationException(errors);
         }
         ratesStorageService.storeData(rates);
        LocalDateTime endTime = LocalDateTime.now();
        return errors.isEmpty() ? new ImportInfo(startTime, endTime, rates.size()) : new ImportInfo(startTime, endTime, lastYear.size(), rates.size(), errors);
    }

    private Optional<RatesOutput> fromExchangeratesData(ExchangeRatesData ratesData){
        if (!ratesData.getSuccess()){
            return Optional.empty();
        }
        if(!CURRENCY_STRINGS.contains(ratesData.getBase())){
            return Optional.empty();
        }
        Map<CurrencyType, BigDecimal> ratesMap = new EnumMap<>(CurrencyType.class);

        ratesData.getRates().forEach((key, value) -> {
            if (CURRENCY_STRINGS.contains(key)) {
                ratesMap.put(CurrencyType.valueOf(key), value);
            }
        });
        return Optional.of(new RatesOutput(ratesData.getDate(), CurrencyType.valueOf(ratesData.getBase()), ratesMap));
    }

    public static class ImportInfo{
        private final String status;
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
        private final int recordsToRetrieve;
        private final int recordsRetrieved;
        private final Set<String> errors;

        public String getStatus() {
            return status;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }

        public LocalDateTime getEndTime() {
            return endTime;
        }

        public int getRecordsToRetrieve() {
            return recordsToRetrieve;
        }

        public int getRecordsRetrieved() {
            return recordsRetrieved;
        }

        public Set<String> getErrors() {
            return errors;
        }

        public ImportInfo(LocalDateTime startTime, LocalDateTime endTime, int recordsRetrieved) {
            this.status="OK";
            this.startTime = startTime;
            this.endTime = endTime;
            this.recordsToRetrieve = recordsRetrieved;
            this.recordsRetrieved = recordsRetrieved;
            this.errors = Collections.emptySet();
        }

        public ImportInfo(LocalDateTime startTime, LocalDateTime endTime, int recordsToRetrieve, int recordsRetrieved, Set<String> errors) {
            this.status="Ok but some records not retrieved";
            this.startTime = startTime;
            this.endTime = endTime;
            this.recordsToRetrieve = recordsToRetrieve;
            this.recordsRetrieved = recordsRetrieved;
            this.errors = errors;
        }
    }

    public static class ExchangeRatesCommunicationException extends RuntimeException{
        public ExchangeRatesCommunicationException(Set<String> causes) {
            super("Error during connection to exchnageratesapi.io service. " + String.join(", ", causes));
        }
    }


}
