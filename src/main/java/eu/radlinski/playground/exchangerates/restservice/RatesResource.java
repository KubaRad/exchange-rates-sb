package eu.radlinski.playground.exchangerates.restservice;


import eu.radlinski.playground.exchangerates.model.CurrencyType;
import eu.radlinski.playground.exchangerates.services.RatesOutput;
import eu.radlinski.playground.exchangerates.services.RatesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@RestController
@RequestMapping("/api/rates")
public class RatesResource {

    public final  Set<CurrencyType> availableCurrencies;

    private final RatesService ratesService;

    @Autowired
    public RatesResource(@Value("${exchange-rates.import-currencies}") Set<CurrencyType> availableCurrencies, RatesService ratesService) {
        this.availableCurrencies = availableCurrencies;
        this.ratesService = ratesService;
    }

    @GetMapping("/{selectedDate}")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Data are present in storage, the return value is composition of rates for stored currencies. When 'targetCurrency' parameter is used object contains only rate for given currency.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = RatesOutput.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The data for specified date are not stored.",
                            content = @Content(mediaType = "")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Problem with interpreting parameters. The date format is invalid or currency symbol is unknown/or not stored.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDescription.class))
                    )
            }
    )
    @Operation(
            summary = "Rates data for specified date",
            description = "Retrieves data from storage. Date of rates is specified as path parameter, rates could be limited to specified currency using 'targetCurrency' parameter."
    )

    public RatesOutput getRatesForDate(
            @Parameter(
                    description = "Date (day) for which the data are retrieved. Use yyyy-MM-dd format. Actually only 1st day of month is stored (so effectively the format is yyyy-MM-01).",
                    required = true,
                    schema=@Schema(implementation = LocalDate.class)
            )
            @PathVariable(value = "selectedDate") LocalDate selectedDate,
            @Parameter(
                    description = "Currency code to limit output currencies",
                    required = false,
                    schema=@Schema(implementation = CurrencyType.class, enumAsRef = true),
                    example = "selectedCurrency=USD"
            )
            @RequestParam(value = "targetCurrency", required = false) CurrencyType targetCurrency){
        if(targetCurrency == null){
            return ratesService.ratesForDate(selectedDate)
                    .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, null, null));
        }

        if(!availableCurrencies.contains(targetCurrency)) {
            throw new CurrencyNotStoredException(targetCurrency, availableCurrencies);
        }
        return ratesService.ratesForDate(selectedDate, targetCurrency)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, null, null));
    }

    @GetMapping()
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of daily records for last year or in specified range. " +
                                    "When 'targetCurrency' parameter is used record contains only rate " +
                                    "for given currency. If there is no data in storage or in given range " +
                                    "the empty list is returned.",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = RatesOutput.class))
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Problem with interpreting parameters. The date format is invalid or currency symbol is unknown/or not stored.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDescription.class))
                    )
            }
    )
    @Operation(
            summary = "List of exchange rates",
            description = "Retrieves available data from storage. Only last year is returned by default. " +
                    "The dates range could be expanded/limited using 'startDate' and 'endDate' parameters. " +
                    "Rates could be limited to specified currency using 'targetCurrency' parameter. When " +
                    "the date parameters are used both of them must be specified."
    )
    public List<RatesOutput> getRatesForDates(
            @Parameter(
                    description = "Start date for requested range. Use yyyy-MM-dd format.",
                    required = false,
                    schema=@Schema(implementation = LocalDate.class),
                    example = "startDate=2020-09-13"
            )
            @RequestParam(value = "startDate", required = false)LocalDate startDate,
            @Parameter(
                    description = "End date for requested range. Use yyyy-MM-dd format.",
                    required = false,
                    schema=@Schema(implementation = LocalDate.class),
                    example = "endDate=2021-01-18"
            )
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @Parameter(
                    description = "Currency code to limit output currencies",
                    required = false,
                    schema=@Schema(implementation = CurrencyType.class, enumAsRef = true),
                    example = "selectedCurrency=USD"
            )
            @RequestParam(value = "targetCurrency", required = false) CurrencyType targetCurrency){

        if(startDate == null && endDate == null && targetCurrency == null){
            return ratesService.lastYearRates();
        }

        if(startDate == null && endDate == null ){
            if(!availableCurrencies.contains(targetCurrency)) {
                throw new CurrencyNotStoredException(targetCurrency, availableCurrencies);
            }
            return ratesService.lastYearRates(targetCurrency);
        }

        if(startDate != null && endDate != null && targetCurrency == null){
            return ratesService.ratesForDate(startDate, endDate);
        }

        if(startDate != null && endDate != null){
            if(!availableCurrencies.contains(targetCurrency)) {
                throw new CurrencyNotStoredException(targetCurrency, availableCurrencies);
            }
            return ratesService.ratesForDate(startDate, endDate, targetCurrency);
        }

        throw new OnlyOneDateParameterException();
    }


    public static class OnlyOneDateParameterException extends RuntimeException{
        public OnlyOneDateParameterException() {
            super("Both 'startDate' and 'endDate' parameters should be set to obtain rates in defined period");
        }
    }

    public static class CurrencyNotStoredException extends RuntimeException{
        public CurrencyNotStoredException(final CurrencyType receivedCurrency, final Set<CurrencyType> availableCurrencies) {
            super(MessageFormat.format("Stored exchange rates do not contains rate for currency: {0}. Available currencies: {1}"
                    , receivedCurrency
                    , availableCurrencies.stream()
                            .map(CurrencyType::name)
                            .collect(Collectors.joining(","))));
        }
    }

}
