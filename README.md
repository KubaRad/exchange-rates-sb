### Description
Microservice to retrieve exchange rates data  from https://exchangeratesapi.io  service and store them in inmemory database.

### Configuration
Initially only three configuration variables should be set:

1. The list of imported currencies as comma delimited strings

`exchange-rates.import-currencies=EUR,GPB,HKD`

2. exchangeratesapi.io URI

`exchange-rates.exchangeratesapiio-uri=http://api.exchangeratesapi.io`

3. exchangeratesapi.io API_KEY

`exchange-rates.exchangeratesapiio-api-key=2c5961460800a049752ced42ec211955`


### How to compile and run
Project is maven based so follow standard maven pipeline:

`mvn build package`

To run use:

`mvn payara-micro:start`

### API description
