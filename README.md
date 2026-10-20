# Finance Control API

Sistema de Controle Financeiro — API REST para gerenciar **contas**, **transações** (receitas/despesas) e **relatórios** mensais, construída com **Spring Boot**, **Arquitetura Hexagonal (Ports & Adapters)** e **Apache Kafka** para mensageria e disparos de eventos.

## Stack

- Java 17
- Spring Boot 3.4.x
- Spring Web (REST)
- Spring Data JPA (Hibernate)
- Bean Validation (`jakarta.validation`)
- Spring Actuator (health check / métricas)
- Apache Kafka (`spring-kafka`) — mensageria e disparo de eventos
- PostgreSQL (produção) e H2 (apenas testes/CI)
- JUnit 5 + Mockito + AssertJ

## Arquitetura Hexagonal (Ports & Adapters)

```
        ┌──────────────────────────────────────────────┐
        │                  DOMAIN                      │
        │  model (Account, Transaction) + enums +      │
        │  eventos de domínio + exceções               │
        └───────────────▲──────────────┬───────────────┘
                        │              │
        ┌───────────────┴──────────────▼───────────────┐
        │               APPLICATION                    │
        │  port/in  (casos de uso)                     │
        │  port/out (interfaces de saída)              │
        │  service  (implementações dos casos de uso)  │
        │  dto      (comandos e resultados)            │
        └───────▲───────────────────────▲──────────────┘
                │                       │
        ┌───────┴────────┐      ┌───────┴──────────────┐
        │  ADAPTERS (in) │      │   ADAPTERS (out)     │
        │  web (REST)    │      │  persistence (JPA)   │
        │  kafka (consum.)│      │  kafka (producer)   │
        └────────────────┘      └──────────────────────┘
```

### Fluxo de uma requisição

1. `adapter/in/web` recebe a requisição, valida o DTO e converte para um **comando** da aplicação.
2. `application/service` (caso de uso) executa as regras de negócio usando o **domínio** e as **portas de saída**.
3. `adapter/out/persistence` persiste via JPA (entidades + repositórios + mappers isolados do domínio).
4. `adapter/out/kafka` publica eventos de domínio quando necessário.

### Vantagens desse desenho

- **Domínio puro**: sem dependência de Spring/JPA — fácil de testar e evoluir.
- **Acoplamento invertido**: a aplicação depende de interfaces (portas), não de infraestrutura.
- **Trocabilidade**: dá para trocar PostgreSQL por outro banco, ou Kafka por outro broker, sem tocar na regra de negócio.

## Mensageria e disparos (Kafka)

| Evento                    | Tópico                       | Quando é publicado                         |
|---------------------------|------------------------------|--------------------------------------------|
| `TransactionCreatedEvent` | `finance.transaction.events` | Ao criar uma transação (INCOME/EXPENSE)    |
| `LowBalanceEvent`         | `finance.account.events`     | Quando uma despesa deixa o saldo negativo  |

O `FinanceEventConsumer` (adapter de entrada) consome esses tópicos e dispara as notificações/alertas (atualmente loga; pronto para integrar e-mail/SMS/push).

- **Producer**: `KafkaEventPublisher` implementa a porta `EventPublisherPort`.
- **Fallback**: com `app.kafka.enabled=false`, o `LoggingEventPublisher` assume (usado nos testes), então a aplicação roda **sem Kafka**.
- Os tópicos são auto-criados pelo broker em desenvolvimento (`KAFKA_AUTO_CREATE_TOPICS_ENABLE=true`).

## Como executar

### 1. Subir PostgreSQL e Kafka (Docker)

```bash
docker compose up -d
```

### 2. Rodar a aplicação

```bash
./mvnw spring-boot:run
```

> Configurações ficam em `src/main/resources/application.yml`, sobrescrevíveis por
> variáveis de ambiente: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KAFKA_BOOTSTRAP_SERVERS`.

### 3. Rodar os testes

```bash
mvn test
```

Os testes usam o perfil `test` (H2 em memória + `app.kafka.enabled=false`), então **não** exigem PostgreSQL nem Kafka.

## Documentação e testes

- **Documentação completa do Postman** (todas as requisições): [`docs/POSTMAN.md`](docs/POSTMAN.md)
- **Guia passo a passo** (executar + testar, incluindo Postman): [`docs/GUIA_EXECUCAO.md`](docs/GUIA_EXECUCAO.md)
- **Collection do Postman** (importar): [`postman/finance-control.postman_collection.json`](postman/finance-control.postman_collection.json)

## Endpoints

### Contas — `/api/accounts`

| Método | Rota                | Descrição             |
|--------|---------------------|-----------------------|
| POST   | `/api/accounts`     | Cria uma conta        |
| GET    | `/api/accounts`     | Lista todas           |
| GET    | `/api/accounts/{id}`| Busca por id          |
| PUT    | `/api/accounts/{id}`| Atualiza              |
| DELETE | `/api/accounts/{id}`| Remove                |

### Transações — `/api/transactions`

| Método | Rota                            | Descrição                                     |
|--------|---------------------------------|-----------------------------------------------|
| POST   | `/api/transactions`             | Cria (publica `TransactionCreatedEvent`)      |
| GET    | `/api/transactions`             | Lista (ou filtra por `?accountId=1`)          |
| GET    | `/api/transactions/{id}`        | Busca por id                                  |
| DELETE | `/api/transactions/{id}`        | Remove (reverte o saldo)                      |

### Relatórios — `/api/reports`

| Método | Rota                  | Descrição                                          |
|--------|-----------------------|----------------------------------------------------|
| GET    | `/api/reports/summary`| Resumo mensal: saldo total, receitas, despesas, líquido |

## Exemplos de payload

Criar conta:

```json
{
  "name": "Conta Corrente",
  "type": "CHECKING",
  "initialBalance": 1500.00,
  "currency": "BRL"
}
```

Criar transação (soma 2000.00 ao saldo e publica evento no Kafka):

```json
{
  "accountId": 1,
  "type": "INCOME",
  "category": "SALARY",
  "description": "Salário mensal",
  "amount": 2000.00,
  "date": "2026-10-01"
}
```

Criar despesa (subtrai do saldo; se ficar negativo, publica `LowBalanceEvent`):

```json
{
  "accountId": 1,
  "type": "EXPENSE",
  "category": "FOOD",
  "description": "Mercado",
  "amount": 320.50,
  "date": "2026-10-05"
}
```

## Regras de negócio implementadas

- Contas inativas não podem receber transações.
- `INCOME` soma ao saldo; `EXPENSE` subtrai.
- Excluir uma transação reverte automaticamente o impacto no saldo.
- Valores monetários usam `BigDecimal`.
- Auditoria (`createdAt`/`updatedAt`) no adapter de persistência via `@PrePersist`/`@PreUpdate`.

## Estrutura de pacotes

```
com.portfolio.financecontrol
├── domain
│   ├── model            → Account, Transaction (entidades puras)
│   ├── enums            → AccountType, TransactionType, TransactionCategory
│   ├── event            → DomainEvent, TransactionCreatedEvent, LowBalanceEvent
│   └── exception        → BusinessException, ResourceNotFoundException
├── application
│   ├── port/in          → AccountUseCase, TransactionUseCase, ReportUseCase
│   ├── port/out         → AccountPort, TransactionPort, EventPublisherPort
│   ├── service          → AccountService, TransactionService, ReportService
│   └── dto              → comandos e resultados
├── adapter
│   ├── in/web           → controllers, DTOs de entrada/saída, GlobalExceptionHandler
│   ├── in/kafka         → FinanceEventConsumer
│   └── out
│       ├── persistence  → AccountEntity, TransactionEntity, repositories, mappers, adapters
│       └── kafka        → KafkaEventPublisher, LoggingEventPublisher
└── config               → KafkaConfig (@EnableKafka condicional)
```

