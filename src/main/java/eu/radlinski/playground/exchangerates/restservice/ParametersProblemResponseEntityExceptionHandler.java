package eu.radlinski.playground.exchangerates.restservice;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@ControllerAdvice
public class ParametersProblemResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = { RatesResource.CurrencyNotStoredException.class,
            RatesResource.OnlyOneDateParameterException.class,
            MethodArgumentTypeMismatchException.class,
            CurrencyTypeConverter.CurrencyTypeConversionException.class,
            LocalDateConverter.LocalDateConversionException.class,
            InitDatabaseResource.ExchangeRatesCommunicationException.class
    })
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        String msg;
        if(ex instanceof MethodArgumentTypeMismatchException){
            if(ex.getCause() instanceof ConversionFailedException){
                /*
                 * Dive into exception chain to get messages from custom converters
                 */
                msg = ex.getCause().getCause() != null ? ex.getCause().getCause().getMessage() : ex.getCause().getMessage();
            } else {
                msg=ex.getMessage();
            }
        } else {
            msg = ex.getMessage();
        }
        HttpStatus resultStatus = ex instanceof InitDatabaseResource.ExchangeRatesCommunicationException ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST;
        ErrorDescription bodyOfResponse = new ErrorDescription(msg);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), resultStatus, request);
    }
}
