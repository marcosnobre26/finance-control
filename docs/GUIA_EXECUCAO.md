# Guia de Execução e Testes — Finance Control API

Passo a passo para rodar e testar o projeto localmente, incluindo os **testes automatizados (JUnit)** e os **testes manuais com Postman**.

## 1. Pré-requisitos

| Ferramenta        | Versão mínima | Como verificar                                   |
|-------------------|---------------|--------------------------------------------------|
| Java (JDK)        | 17            | `java -version`                                  |
| Maven             | 3.8+          | `mvn -version`                                   |
| Docker + Compose  | recente       | `docker --version` / `docker compose version`    |
| Postman           | qualquer      | postman.com/downloads                            |

> Caso prefira, gere o Maven Wrapper com `mvn wrapper:wrapper` e use `./mvnw` no lugar de `mvn`.

## 2. Subir a infraestrutura (PostgreSQL + Kafka)

Na raiz do projeto:

```bash
docker compose up -d
```

Confira se os containers estão de pé:

```bash
docker compose ps
```

Esperado: `finance-control-db` (PostgreSQL, porta **5432**) e `finance-control-kafka` (porta **9092**).

## 3. Rodar a aplicação

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Teste rapidamente:

```bash
curl http://localhost:8080/actuator/health
# {"status":"UP"}
```

> Para rodar **sem Kafka** (desenvolvimento rápido), desative via:
> `mvn spring-boot:run -Dspring-boot.run.arguments=--app.kafka.enabled=false`

## 4. Rodar os testes automatizados (JUnit)

```bash
mvn test
```

Os testes usam **H2 em memória** e `app.kafka.enabled=false`, então **não** exigem PostgreSQL/Kafka.

| Teste                            | O que valida                                                              |
|----------------------------------|---------------------------------------------------------------------------|
| `AccountServiceTest`             | criação de conta e erro "não encontrada"                                   |
| `TransactionServiceTest`         | soma de saldo (INCOME), bloqueio de conta inativa, disparo de `LowBalanceEvent` |
| `FinanceControlApplicationTests` | carrega o contexto Spring com H2                                          |

## 5. Testar com Postman

### 5.1 Importar a collection

1. Abra o Postman.
2. Clique em **Import** (ou `Ctrl/Cmd + O`).
3. Selecione o arquivo `postman/finance-control.postman_collection.json`.
4. A collection **Finance Control API** aparece com as pastas **Contas**, **Transações**, **Relatórios** e **Infra**.

### 5.2 Variáveis da collection

- `baseUrl` → `http://localhost:8080` (ajuste se a API rodar em outra porta).
- `accountId` e `transactionId` → preenchidas **automaticamente** pelos scripts de teste ao criar registros.

### 5.3 Fluxo recomendado (nesta ordem)

1. **Contas → 1. Criar conta** (salva `accountId`).
2. **Transações → 1. Criar transação (INCOME)** (salva `transactionId`).
3. **Transações → 2. Criar transação (EXPENSE)**.
4. **Contas → 3. Buscar conta por ID** (veja o saldo atualizado).
5. **Relatórios → Resumo mensal** (receitas/despesas do mês).
6. **Infra → Health check**.

## 6. Observar a mensageria (Kafka)

Com a aplicação e o Kafka rodando, ao criar transações os logs mostram:

```
Evento TRANSACTION_CREATED publicado no tópico finance.transaction.events (offset=...)
[Kafka] Notificação disparada: INCOME de 2000.00 na conta Conta Corrente (2026-10-15)
```

Se uma despesa deixar o saldo negativo:

```
Evento LOW_BALANCE publicado no tópico finance.account.events (offset=...)
[Kafka] Alerta de saldo baixo: conta Conta Corrente com saldo -150.00
```

Para consumir um tópico direto no broker:

```bash
docker exec -it finance-control-kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic finance.transaction.events \
  --from-beginning
```

## 7. Referência rápida de endpoints

| Método | Rota                     | Descrição                           |
|--------|--------------------------|-------------------------------------|
| POST   | `/api/accounts`          | Cria conta                          |
| GET    | `/api/accounts`          | Lista contas                        |
| GET    | `/api/accounts/{id}`     | Busca conta por id                  |
| PUT    | `/api/accounts/{id}`     | Atualiza conta                      |
| DELETE | `/api/accounts/{id}`     | Remove conta                        |
| POST   | `/api/transactions`      | Cria transação                      |
| GET    | `/api/transactions`      | Lista (filtro `?accountId=`)        |
| GET    | `/api/transactions/{id}` | Busca transação por id              |
| DELETE | `/api/transactions/{id}` | Remove transação (reverte saldo)    |
| GET    | `/api/reports/summary`   | Resumo mensal                       |
| GET    | `/actuator/health`       | Health check                        |

## 8. Testando com curl (alternativa ao Postman)

Criar conta:

```bash
curl -X POST http://localhost:8080/api/accounts \
  -H 'Content-Type: application/json' \
  -d '{"name":"Conta Corrente","type":"CHECKING","initialBalance":1500.00,"currency":"BRL"}'
```

Criar receita (substitua o `accountId` retornado):

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H 'Content-Type: application/json' \
  -d '{"accountId":1,"type":"INCOME","category":"SALARY","description":"Salário","amount":2000.00,"date":"2026-10-15"}'
```

Resumo mensal:

```bash
curl http://localhost:8080/api/reports/summary
```

## 9. Códigos de resposta

| Código | Significado                                            |
|--------|--------------------------------------------------------|
| 200    | Sucesso (GET/PUT)                                      |
| 201    | Criado (POST)                                          |
| 204    | Sem conteúdo (DELETE)                                  |
| 400    | Payload inválido (validação)                           |
| 404    | Recurso não encontrado                                 |
| 422    | Regra de negócio violada (ex.: conta inativa)          |
| 500    | Erro interno                                           |

## 10. Solução de problemas

| Problema                          | Solução                                                             |
|-----------------------------------|---------------------------------------------------------------------|
| Porta 8080 em uso                 | rode com `--server.port=8081` e ajuste o `baseUrl` no Postman        |
| `Connection refused` no Postgres  | confirme `docker compose up -d` e o container `finance-control-db`   |
| Kafka indisponível                | rode com `--app.kafka.enabled=false` (a API funciona sem broker)     |
| Testes falham                     | apague `target/` e rode `mvn clean test`                             |
| Relatório zerado                  | o relatório soma o **mês atual**; use datas do mês corrente nas transações |

