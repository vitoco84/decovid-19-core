[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=vitoco84_decovid-19-core)](https://sonarcloud.io/summary/new_code?id=vitoco84_decovid-19-core)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=vitoco84_decovid-19-core&metric=coverage)](https://sonarcloud.io/component_measures/metric/coverage/list?id=vitoco84_decovid-19-core)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=vitoco84_decovid-19-core&metric=bugs)](https://sonarcloud.io/component_measures/metric/reliability_rating/list?id=vitoco84_decovid-19-core)
![Known Vulnerabilities](https://snyk.io/test/github/vitoco84/decovid-19-core/badge.svg)
![CI decovid-19-core](https://github.com/vitoco84/decovid-19-core/actions/workflows/ci-decovid-19-core.yml/badge.svg)

# Decovid-19-Core
> * API that can decode and verify QR-Codes for EU Digital Covid-19 Health Certificates.
> * No personal or sensitive data is stored in any way.

# Install Prerequisites
> JDK 11

# Getting it Running
> Edit `application.yml` in `config` folder
> 
> Run from terminal with: `.\gradlew bootRun` or run Gradle task: `Tasks\application\bootRun`

# Health
> Actuator health endpoint served under [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health)

<a name="test-data"></a>
# Test Data
> Test Data taken from: [dcc-testdata](https://github.com/ehn-dcc-development/dcc-testdata)

# Trusted Document Signing Certificates (DSC)
> Trust List Documentation API and Endpoints taken from: [Digitaler-Impfnachweis](https://github.com/Digitaler-Impfnachweis/certification-apis)

# DSC Trust List API
> A collection of REST API endpoints can be found here: [EU DCC Gateway REST API](https://eu-digital-green-certificates.github.io/dgc-gateway/)

# Swagger UI
> API endpoints documentation served under [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)
> 
> Swagger documentation served under [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html)
>
> **All values in these examples are taken from public available test data (see [Link](#test-data) above).**

# Postman
> Postman collection for testing purposes is available under `resources/Decovid-19-Core.postman_collection.json`

# Digital Covid Certificate Schema and Value Sets
> Schema and Value Sets taken from [eu-dcc-schema](https://github.com/ehn-dcc-development/eu-dcc-schema) and [eu-dcc-valuesets](https://github.com/ehn-dcc-development/eu-dcc-valuesets)

# Gradle Version Catalog
> [Documentation](https://docs.gradle.org/current/userguide/platforms.html)

# Examples for API Resources
> ## Example HcertServerRequest
> POST [http://localhost:8082/decovid19/hcert/prefix](http://localhost:8082/decovid19/hcert/prefix)
> ```yaml
> {
>     "hcertPrefix": "HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4"
> }
> ```
> ## Example HcertServerResponse
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
>     "hcertAlgo": "PS256",
>     "hcertIssuer": "CH BAG",
>     "hcertTimeStamp": {
>         "hcerExpirationTime": "2022-05-29T07:55:08Z",
>         "hcertIssuedAtTime": "2021-05-29T07:55:08Z"
>     },
>     "hcertSignature": "Fqzo50TBt9un8CakzKb/gXIUOVXPWBUQ0OrKD2BBXi2QEJHYmByWdl/fyreCJalXbkNfDGVXxJ5g0vk4h+khCFQCrYAX1fIRBFgMZQAX2juzM7dGZKwIJOLcZifX75ekbEvrcgWxWCUE1Ucc2OXsu6PitnOV/f5jaDVWugB3KomsrDSPi/O9SSraWgHDaINfAZ8xjXfoQ+wUdHjQYipuwVqThOzz0QKlpXUZFjmQqVHvym+raiWMN4j+2xfqElGCf0jmbUNSixm3mCtkRquoTkmdcCfmECnE/mLVnnRmFzjvj9yB8OVvFT56kSrIrfcABGZapc+Z0r6Cbnrm/ytJfA=="
> }
> ```
> ## Example PEMCertServerRequest
> POST [http://localhost:8082/decovid19/hcert/qrcode/pem](http://localhost:8082/decovid19/hcert/qrcode/pem)
> ```yaml
> {
>    "pemCertificate": "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"
> }
> ```
> ## Example PEMCertServerResponse
> ```yaml
> {
>    "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4uZO4/7tneZ3XD5OAiTyoANOohQZC+DzZ4YC0AoLnEO+Z3PcTialCuRKS1zHfujNPI0GGG09DRVVXdv+tcKNXFDt/nRU1zlWDGFf4/63l5RIjkWFD3JFKqR8IlcJjrYYxstuZs3May3SGQJ+kZaeH5GFZMRvE0waHqMxbfwakvjf8qyBXCrZ1WsK+XJf7iYbJS2dO1a5HnegxPuRA7Zz8ikO7QRzmSongqOlkejEaIkFjx7gLGTUsOrBPYa5sdZqinDwmnjtKi52HLWarMXs+t1MN4etIp7GE7/zarjBNxk1Efiiwl+RdcwJ2uVwfrgzxfv3/TekZF8IUykV2Geu3QIDAQAB",
>    "subject": "CN=BAG Covid-19 Signer A TEST, OU=Covid-19-Zertifikat, OU=GE-0220-BAG, OU=, O=Bundesamt für Gesundheit (BAG), OID.2.5.4.97=NTRCH-CHE-467.023.568, OID.2.5.4.15=Government Entity, L=Köniz, ST=BE, C=CH",
>    "signatureAlgorithm": "SHA256withRSA",
>    "validTo": "2024-05-04T14:15:51Z",
>    "validFrom": "2021-05-04T14:15:51Z",
>    "serialNumber": "2e46d100e4e5d8d4489f3bca20ba66dc",
>    "issuer": "CN=Swiss Government aRegulated CA 02, OU=Swiss Government PKI, O=Bundesamt fuer Informatik und Telekommunikation (BIT), OID.2.5.4.97=VATCH-CHE-221.032.573, C=CH",
>    "publicKeyParams": {
>        "modulus": "e2e64ee3feed9de6775c3e4e0224f2a0034ea214190be0f3678602d00a0b9c43be6773dc4e26a50ae44a4b5cc77ee8cd3c8d06186d3d0d15555ddbfeb5c28d5c50edfe7454d739560c615fe3feb79794488e45850f72452aa47c2257098eb618c6cb6e66cdcc6b2dd219027e91969e1f918564c46f134c1a1ea3316dfc1a92f8dff2ac815c2ad9d56b0af9725fee261b252d9d3b56b91e77a0c4fb9103b673f2290eed0473992a2782a3a591e8c46889058f1ee02c64d4b0eac13d86b9b1d66a8a70f09a78ed2a2e761cb59aacc5ecfadd4c3787ad229ec613bff36ab8c137193511f8a2c25f9175cc09dae5707eb833c5fbf7fd37a4645f08532915d867aedd",
>        "publicExponent": "10001",
>        "algo": "RSA",
>        "bitLength": "2048"
>    },
>    "signature": "SP2AYJVGV5WWHpCXvHf3/ctob7pX1fZHXfwkos5XfX5dArVjqNM4oaiTlB0Fk5KxUCmIhi7lIa92soy564JShPkIhM3jtQygKC/XItTP4UbR/SfjNO4teL5HSD5QddyqHdaJUX/OE1sAhOxIEnFPqOa0DFFOTAEUYWJauRvSJ8MB2KlsUILpkxMx03KfB8bxkFTDdUIPoREVLSWAGKwxKS0OE6ZnmwoLdhvu7HxQO9msx9ci5Q58fb6ApXn6xk9uCMTQr5HiJA4VCZ7oRaH+uk/BqDfb/1lcgLv6cYh0R/6oD5IpT/SpVu1spOGxKR/U6BnAysiiFkFkqbFsf/ZoVDR/hBC0omQtpps6P64LNKq0rv3ZdU918XT42Fdn2hH2+ajJzhix6VjTYKAh+VK+dYyB/qx22XfMP+41Gt5TYz65AauWV9tOWpFKtuXtBWkziV9JYsnokoLGaaZNIojQZx7bJ6KdUnwqMbPUTOkbM++expO+YqFSmundq16TpUuzHBKOe70Lgwytv/WFlveeFR9mJcWfzgiZitNrbQ6teluAK89uy/kR+sqeO5EyIJgsTNp4yAYBb5399ppI2qk0Mea+629wvuEXSaoXQzhiOjx1aXd7Ib2sHj11c16NwQi83D6YcuI/wkcOOemBJPr65aRXFKX6EnwG/Bm6/rMzGTc=",
>    "valid": true
> }
> ```
> ## Example QRCodeServerRequest
> POST [http://localhost:8082/decovid19/hcert/qrcode/url](http://localhost:8082/decovid19/hcert/qrcode/url)
> ```yaml
> {
>    "url": "https://www.google.ch/"
> }
> ```
> ## Example QRCodeServerResponse
> ![Google URL](src/main/resources/images/QRCodeServerResponse.png)
> 
> ## Example Generate Fake Covid-19 Test Health Certificate Request
> This is only used for test purposes. No real digital signature is present in it.
> 
> POST [http://localhost:8082/decovid19/hcert/qrcode/hcert](http://localhost:8082/decovid19/hcert/qrcode/hcert)
> ```yaml
> {
>   "nam": {
>       "fn": "Uncle",
>       "fnt": "UNCLE",
>       "gn": "Bob",
>       "gnt": "BOB"
>   },
>   "dob": "1943-02-01",
>   "ver": "1.0.0",
>   "t": [
>     {
>         "tg": "COVID-19",
>         "co": "Switzerland",
>         "tt": "Rapid Test",
>         "nm": "COVID-19",
>         "ma": "COVID-19 Test",
>         "sc": "2021-04-30",
>         "tr": "Not detected",
>         "tc": "Test Center",
>         "is": "Bundesamt für Gesundheit (BAG)"
>     }
>    ]
> }
> ```
> ## Example QRCodeServerResponse
> ![Fake QR-Code Test](src/main/resources/images/FakeQRCodeTestServerResponse.png)
> 
> ## Example HcertVerificationServerRequest
> Leave Bearer Token empty for verifying EU Certificates.
> 
> For the Swiss Certificates provide the Token given by the BIT (Bundesamt für Informatik und Telekommunikation). For a Token get in contact with: [Swiss Admin GitHub](https://github.com/admin-ch).
> 
> POST [http://localhost:8082/decovid19/hcert/verify](http://localhost:8082/decovid19/hcert/verify)
> ```yaml
> {
>   "bearerToken": "",
>   "keyId": "DEsVUSvpFAE=",
>   "hcertPrefix": "HC1:6BF+70790T9WJWG.FKY*4GO0.O1CV2 O5 N2FBBRW1*70HS8WY04AC*WIFN0AHCD8KD97TK0F90KECTHGWJC0FDC:5AIA%G7X+AQB9746HS80:54IBQF60R6$A80X6S1BTYACG6M+9XG8KIAWNA91AY%67092L4WJCT3EHS8XJC$+DXJCCWENF6OF63W5NW6WF6%JC QE/IAYJC5LEW34U3ET7DXC9 QE-ED8%E.JCBECB1A-:8$96646AL60A60S6Q$D.UDRYA 96NF6L/5QW6307KQEPD09WEQDD+Q6TW6FA7C466KCN9E%961A6DL6FA7D46JPCT3E5JDLA7$Q6E464W5TG6..DX%DZJC6/DTZ9 QE5$CB$DA/D JC1/D3Z8WED1ECW.CCWE.Y92OAGY8MY9L+9MPCG/D5 C5IA5N9$PC5$CUZCY$5Y$527B+A4KZNQG5TKOWWD9FL%I8U$F7O2IBM85CWOC%LEZU4R/BXHDAHN 11$CA5MRI:AONFN7091K9FKIGIY%VWSSSU9%01FO2*FTPQ3C3"
> }
> ```
> ## Example HcertVerificationServerResponse
> ```yaml
> {
>   "verified": true
> }
> ```
