[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=vitoco84_decovid-19-core)](https://sonarcloud.io/summary/new_code?id=vitoco84_decovid-19-core)

![Known Vulnerabilities](https://snyk.io/test/github/vitoco84/decovid-19-core/badge.svg)

![CI decovid-19-core](https://github.com/vitoco84/decovid-19-core/actions/workflows/ci-decovid-19-core.yml/badge.svg)

# Decovid-19-Core
> WIP

# Install Prerequisites
> JDK 11

# Getting it Running
> Edit `application.yml` in `config` folder
> 
> Run from terminal with: `.\gradlew bootRun`
> 
> or run Gradle task: `Tasks\application\bootRun`

# Health
> Actuator health endpoint served under [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health)

# Test Data
> Test Data taken from: [dcc-testdata](https://github.com/ehn-dcc-development/dcc-testdata)

# Trusted Document Signing Certificates (DSC)
> Trust List Documentation API and Endpoints taken from: [Digitaler-Impfnachweis](https://github.com/Digitaler-Impfnachweis/certification-apis)

# Swagger UI

> API endpoints documentation served under [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)
> 
> Swagger documentation served under [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
>
> ## Example HcertServerRequest:
> ```yaml
> {
>     "hcertPrefix": "HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4"
> }
> ```
> ## Example HcertServerResponse:
> ```yaml
> {
>     "hcertPrefix": "HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4",
>     "hcertContent": {
>         "nam": {
>             "fn": "Müller",
>             "fnt": "MUELLER",
>             "gn": "Céline",
>             "gnt": "CELINE"
>         },
>         "dob": "1943-02-01",
>         "ver": "1.0.0",
>         "v": [
>           {
>               "ci": "urn:uvci:01:CH:2987CC9617DD5593806D4285",
>               "co": "Switzerland",
>               "dn": 2,
>               "dt": "2021-04-30",
>               "is": "Bundesamt für Gesundheit (BAG)",
>               "ma": "Moderna Biotech Spain S.L.",
>               "mp": "Spikevax",
>               "sd": 2,
>               "tg": "COVID-19",
>               "vp": "SARS-CoV-2 mRNA vaccine"
>           }
>         ]
>     },
>     "hcertKID": "mmrfzpMU6xc=",
>     "hcertAlgo": "RSA_PSS_256",
>     "hcertIssuer": "CH BAG",
>     "hcertTimeStamp": {
>         "hcerExpirationTime": "2022-05-29T07:55:08Z",
>         "hcertIssuedAtTime": "2021-05-29T07:55:08Z"
>     }
> }
> ```

# Postman
> Postman collection for testing purposes is available under `resources/Decovid-19-Core.postman_collection.json`

# Digital Covid Certificate Schema and Value Sets
> Schema and Valuesets taken from [eu-dcc-schema](https://github.com/ehn-dcc-development/eu-dcc-schema)

# Gradle Version Catalog
> [Documentation](https://docs.gradle.org/current/userguide/platforms.html)
