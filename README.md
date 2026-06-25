# finance-batch

Serviço de importação em massa de transações financeiras via CSV, integrado ao [finance-api](https://github.com/gothsins/finance-api).

## Sobre o projeto

O finance-batch lê um arquivo CSV contendo transações financeiras, valida cada linha e as importa automaticamente no finance-api via chamadas REST autenticadas com JWT. Ao final de cada execução, gera um relatório de auditoria com o resultado da importação.

## Tecnologias

- Java 21
- Spring Boot 3.5
- Spring Batch
- Spring Web (RestClient)
- H2 (banco em memória para metadados do Batch)
- Lombok

## Arquitetura

transactions.csv

↓

CsvTransactionReader   → lê uma linha por vez como CsvTransaction

↓

TransactionProcessor   → valida e converte tipos (String → LocalDate, BigDecimal, Long)

↓

ApiTransactionWriter   → autentica no finance-api e envia cada transação via POST /transactions

↓

ImportJobListener      → gera relatório ao final da execução

## Pré-requisitos

- Java 21
- finance-api rodando na porta 8080

## Como rodar

**1. Configure as credenciais**

Crie o arquivo `src/main/resources/application.properties` baseado no `application-example.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:batchdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.batch.jdbc.initialize-schema=always
spring.batch.job.enabled=false

server.port=8081

finance.api.base-url=http://localhost:8080
finance.api.email=seu@email.com
finance.api.password=suasenha
```

**2. Prepare o CSV**

Coloque o arquivo em `src/main/resources/input/transactions.csv`:

```
date,description,amount,type,categoryId
2026-01-10,Almoço,45.90,EXPENSE,1
2026-01-15,Salário,3500.00,INCOME,1
```

Campos aceitos:
| Campo | Formato | Exemplo |
|---|---|---|
| date | YYYY-MM-DD | 2026-01-10 |
| description | texto | Almoço |
| amount | decimal positivo | 45.90 |
| type | INCOME ou EXPENSE | EXPENSE |
| categoryId | ID existente no finance-api | 1 |

**3. Suba a aplicação**

```bash
./mvnw spring-boot:run
```

**4. Dispare a importação**

POST http://localhost:8081/batch/import

**5. Verifique o relatório**

output/import-report.txt

Exemplo de relatório gerado:

```
========================================
 RELATÓRIO DE IMPORTAÇÃO - finance-batch
========================================
 Data/Hora : 2026-06-22T23:30:33
 Status    : COMPLETED
 Lidas     : 5
 Importadas: 5
 Rejeitadas: 0
========================================
```
## Estrutura do projeto
src/main/java/com/guilherme/finance_batch/

├── config/       # BatchConfig, ApiProperties

├── controller/   # BatchController (POST /batch/import)

├── dto/          # CsvTransaction, TransactionRequest, AuthRequest

├── listener/     # ImportJobListener (relatório de auditoria)

├── processor/    # TransactionProcessor (validação e conversão)

├── reader/       # CsvTransactionReader (leitura do CSV)

└── writer/       # ApiTransactionWriter (integração REST com finance-api)