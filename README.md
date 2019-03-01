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

* Indicate the start of the batch with the unique session identifier like "95cfd3".
```
curl -X POST "http://localhost:8080/session/95cfd3/start" -H  "accept: application/json"
```
* Upload the record for the instrument "stocks".
```
curl -X POST "http://localhost:8080/session/95cfd3/upload" -H  "accept: application/json" -H  "Content-Type: application/json" 
-d "{  \"records\": [{\"instrument_id\": \"stocks\",\"as_of\":\"2019-02-17T20:33:05.648Z\",\"payload\": {\"details\":\"dummy\",\"stock_price\": 1000}}] }"
```
* Stocks has below structure of the payload. Other custom structure can be defined programmatically.
```json
"payload" : {
  "details" : "dummy",
  "stock_price" : 1000
}
```
* Indicate the complete or cancel of the batch for the session "95cfd3".
```
curl -X POST "http://localhost:8080/provider/95cfd3/complete" -H  "accept: application/json"
```
* Fetch the latest price for the instrument "stocks".
```
curl -X POST "http://localhost:8080/instrument/stocks/latestPrice" -H  "accept: application/json"
```

Assumptions
---
1. As there could be multiple providers of the data, we have a unique identifier for each batch session.
2. The payload data structure is mapped to instrument id, i.e. for a given instrument id like "stocks", the payload data structure will always be same.
3. The new instrument and it's payload data structure needs to programmatically configured before it can consumed by the service.