package eu.radlinski.playground.exchangerates.restservice;

/**
 * @author Kuba Radliński <kuba at radlinski.eu>
 */

public class ErrorDescription {
    private final String description;

    public String getDescription() {
        return description;
    }

    public ErrorDescription(String description) {
        this.description = description;
    }
}
