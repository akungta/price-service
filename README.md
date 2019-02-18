# PriceService

How to start the PriceService application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/price-service-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080/swagger`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

Usages
---

* Indicate the START of the batch for the provider "akash". (This can also be considered as unique session identifier)
```
curl -X POST "http://localhost:8080/provider/akash/START" -H  "accept: application/json"
```
* Upload the record for the instrument "stocks"
```
curl -X POST "http://localhost:8080/provider/akash/upload" -H  "accept: application/json" -H  "Content-Type: application/json" 
-d "{  \"records\": [{\"instrument_id\": \"stocks\",\"as_of\":\"2019-02-17T20:33:05.648Z\",\"payload\": {\"details\":\"dummy\",\"stock_price\": 1000}}] }"
```
* Stocks has below structure of the payload. Other custom structure can be defined programmatically.
```json
"payload" : {
  "details" : "dummy",
  "stock_price" : 1000
}
```
* Indicate the COMPLETE or CANCEL of the batch for the provider "akash"
```
curl -X POST "http://localhost:8080/provider/akash/COMPLETE" -H  "accept: application/json"
```
* Fetch the latest price for the instrument "stocks"
```
curl -X POST "http://localhost:8080/consumer/stocks" -H  "accept: application/json"
```
