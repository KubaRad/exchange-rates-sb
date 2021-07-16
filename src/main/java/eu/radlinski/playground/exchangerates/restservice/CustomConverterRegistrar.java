package eu.radlinski.playground.exchangerates.restservice;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

@Configuration
public class CustomConverterRegistrar implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
//        WebMvcConfigurer.super.addFormatters(registry);
        registry.addConverter(new CurrencyTypeConverter());
        registry.addConverter(new LocalDateConverter());
    }
}
