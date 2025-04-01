# HTTP API for small ledger

#### Prerequisite

- Java 21

#### Build

`./gradlew clean build`

#### Run

`./gradlew bootRun`

#### Examples
1. deposit
curl --request POST \
--url http://localhost:8080/transaction/deposit \
--header 'Content-Type: application/json' \
--data '{
"accountId": "0df456cb-bccd-4c15-8535-c5485f4217d2",
"amount": 20
}'
   
2. withdrawal
curl --request POST \
--url http://localhost:8080/transaction/withdrawal \
--header 'Content-Type: application/json' \
--data '{
"accountId": "0df456cb-bccd-4c15-8535-c5485f4217d2",
"amount": 15
}'

3. history
curl --request GET \
--url 'http://localhost:8080/transaction/history?from=2025-04-01T12%3A56%3A00&to=2025-04-01T12%3A58%3A00&zoneId=Europe%2FLisbon&accountId=0df456cb-bccd-4c15-8535-c5485f4217d2'

4. balance
curl --request GET \
--url 'http://localhost:8080/balance/0df456cb-bccd-4c15-8535-c5485f4217d2?from=2025-03-31T18%3A05%3A00&to=2025-03-31T18%3A07%3A00&zoneId=UTC&accountId=0df456cb-bccd-4c15-8535-c5485f4217d2'

#### License
[The MIT License (MIT)](https://github.com/silaev/wms/blob/master/LICENSE/)

#### Copyright

Copyright (c) 2025 Konstantin Silaev <silaev256@gmail.com>
