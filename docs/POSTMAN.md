# Documentação Postman — Finance Control API

Guia completo para testar todos os endpoints usando o **Postman**, com a collection pronta para importar.

## 1. Pré-requisitos

- Aplicação rodando: `mvn spring-boot:run` (API em `http://localhost:8080`)
- Infraestrutura ativa: `docker compose up -d` (PostgreSQL + Kafka)
- Postman instalado

## 2. Importar a collection

1. Abra o Postman.
2. Clique em **Import** (canto superior esquerdo) ou pressione `Ctrl/Cmd + O`.
3. Arraste/Selecione o arquivo: `postman/finance-control.postman_collection.json`.
4. A collection **Finance Control API** aparecerá com 4 pastas: **Contas**, **Transações**, **Relatórios** e **Infra**.

## 3. Variáveis da collection

| Variável        | Valor padrão              | Descrição                                            |
|-----------------|---------------------------|------------------------------------------------------|
| `baseUrl`       | `http://localhost:8080`   | URL base da API (ajuste se mudar a porta)            |
| `accountId`     | `1`                       | Preenchida automaticamente ao criar uma conta        |
| `transactionId` | `1`                       | Preenchida automaticamente ao criar uma transação    |

> As variáveis `accountId` e `transactionId` são atualizadas pelos **scripts de teste** (`Tests`) da própria collection: ao criar um registro, o `id` retornado é salvo na variável automaticamente.

Para visualizar/editar: clique nos `...` da collection → **Edit** → aba **Variables**.

## 4. Estrutura da collection

```
Finance Control API
├── Contas        (5 requisições)
├── Transações    (6 requisições)
├── Relatórios    (1 requisição)
└── Infra         (1 requisição)
```

## 5. Referência completa das requisições

### 5.1 Contas

| # | Requisição          | Método | URL                                      |
|---|---------------------|--------|------------------------------------------|
| 1 | Criar conta         | POST   | `{{baseUrl}}/api/accounts`               |
| 2 | Listar contas       | GET    | `{{baseUrl}}/api/accounts`               |
| 3 | Buscar conta por ID | GET    | `{{baseUrl}}/api/accounts/{{accountId}}` |
| 4 | Atualizar conta     | PUT    | `{{baseUrl}}/api/accounts/{{accountId}}` |
| 5 | Excluir conta       | DELETE | `{{baseUrl}}/api/accounts/{{accountId}}` |

**1. Criar conta** — Header: `Content-Type: application/json`

Body:
```json
{
  "name": "Conta Corrente",
  "type": "CHECKING",
  "initialBalance": 1500.00,
  "currency": "BRL"
}
```
- Status esperado: `201 Created`
- Script de teste: salva `accountId` automaticamente.

**2. Listar contas** — sem body. Status esperado: `200 OK` (array de contas).

**3. Buscar conta por ID** — usa `{{accountId}}`. Status: `200 OK`.

**4. Atualizar conta** — Header: `Content-Type: application/json`

Body:
```json
{
  "name": "Conta Corrente Atualizada",
  "type": "CHECKING",
  "initialBalance": 2500.00,
  "currency": "BRL"
}
```
- Status esperado: `200 OK`

**5. Excluir conta** — sem body. Status: `204 No Content`.

### 5.2 Transações

| # | Requisição                  | Método | URL                                                           |
|---|-----------------------------|--------|---------------------------------------------------------------|
| 1 | Criar transação (INCOME)    | POST   | `{{baseUrl}}/api/transactions`                                |
| 2 | Criar transação (EXPENSE)   | POST   | `{{baseUrl}}/api/transactions`                                |
| 3 | Listar transações           | GET    | `{{baseUrl}}/api/transactions`                                |
| 4 | Filtrar por conta           | GET    | `{{baseUrl}}/api/transactions?accountId={{accountId}}`        |
| 5 | Buscar transação por ID     | GET    | `{{baseUrl}}/api/transactions/{{transactionId}}`              |
| 6 | Excluir transação           | DELETE | `{{baseUrl}}/api/transactions/{{transactionId}}`              |

**1. Criar transação (receita/INCOME)** — Header: `Content-Type: application/json`

Body:
```json
{
  "accountId": {{accountId}},
  "type": "INCOME",
  "category": "SALARY",
  "description": "Salário mensal",
  "amount": 2000.00,
  "date": "2026-10-19"
}
```
- Status: `201 Created`
- Script: salva `transactionId`. **Soma** o valor ao saldo da conta.

**2. Criar transação (despesa/EXPENSE)** — Body:
```json
{
  "accountId": {{accountId}},
  "type": "EXPENSE",
  "category": "FOOD",
  "description": "Mercado",
  "amount": 320.50,
  "date": "2026-10-19"
}
```
- Status: `201 Created`. **Subtrai** o valor do saldo da conta.

**3. Listar transações** — sem body. Status: `200 OK`.

**4. Filtrar por conta** — query param `accountId`. Status: `200 OK`.

**5. Buscar transação por ID** — usa `{{transactionId}}`. Status: `200 OK`.

**6. Excluir transação** — sem body. Status: `204 No Content` (reverte o saldo).

### 5.3 Relatórios

| Requisição    | Método | URL                                |
|---------------|--------|------------------------------------|
| Resumo mensal | GET    | `{{baseUrl}}/api/reports/summary`  |

- Sem body. Status: `200 OK`.

### 5.4 Infra

| Requisição              | Método | URL                            |
|-------------------------|--------|--------------------------------|
| Health check (Actuator) | GET    | `{{baseUrl}}/actuator/health`  |

- Sem body. Status: `200 OK` → `{"status":"UP"}`.

## 6. Respostas de exemplo

### 6.1 Conta (`AccountResponse`)

```json
{
  "id": 1,
  "name": "Conta Corrente",
  "type": "CHECKING",
  "balance": 1500.00,
  "currency": "BRL",
  "active": true,
  "createdAt": "2026-10-19T22:10:00.123Z",
  "updatedAt": "2026-10-19T22:10:00.123Z"
}
```

### 6.2 Transação (`TransactionResponse`)

```json
{
  "id": 1,
  "accountId": 1,
  "accountName": "Conta Corrente",
  "type": "INCOME",
  "category": "SALARY",
  "description": "Salário mensal",
  "amount": 2000.00,
  "date": "2026-10-19",
  "createdAt": "2026-10-19T22:11:00.123Z"
}
```

### 6.3 Resumo mensal (`BalanceSummaryResponse`)

```json
{
  "totalBalance": 3179.50,
  "totalIncome": 2000.00,
  "totalExpense": 320.50,
  "netBalance": 1679.50
}
```

### 6.4 Erro (`ApiError`)

```json
{
  "timestamp": "2026-10-19T22:12:00.123Z",
  "status": 404,
  "error": "Not Found",
  "message": "Conta não encontrada: id=999",
  "path": "/api/accounts/999"
}
```

## 7. Fluxo completo recomendado

Execute nesta ordem:

1. **Contas → 1. Criar conta** → `201` e `accountId` salvo.
2. **Transações → 1. Criar transação (INCOME)** → `201`, saldo sobe em 2000.00.
3. **Transações → 2. Criar transação (EXPENSE)** → `201`, saldo desce em 320.50.
4. **Contas → 3. Buscar conta por ID** → confira `balance` = 1500 + 2000 − 320.50 = **3179.50**.
5. **Relatórios → Resumo mensal** → receitas/despesas/líquido do mês.
6. **Transações → 6. Excluir transação** → `204` e o saldo é revertido.

## 8. Valores válidos (enums)

| Campo             | Valores aceitos                                                                                                  |
|-------------------|------------------------------------------------------------------------------------------------------------------|
| `type` (conta)    | `CHECKING`, `SAVINGS`, `CREDIT_CARD`, `INVESTMENT`, `CASH`                                                        |
| `type` (transação)| `INCOME`, `EXPENSE`                                                                                               |
| `category`        | `FOOD`, `TRANSPORT`, `HOUSING`, `LEISURE`, `HEALTH`, `EDUCATION`, `SALARY`, `INVESTMENT`, `OTHER`                 |
| `currency`        | código ISO de 3 letras (ex.: `BRL`, `USD`, `EUR`)                                                                |
| `date`            | formato `yyyy-MM-dd`                                                                                              |

## 9. Testando cenários de erro

Para testar um erro, duplique um request (ou crie um novo) e altere o corpo/URL:

| Cenário                       | Como reproduzir                                              | Código esperado |
|-------------------------------|--------------------------------------------------------------|-----------------|
| Nome ausente                  | `POST /api/accounts` sem o campo `name`                      | `400 Bad Request` |
| Valor inválido                | `POST /api/transactions` com `"amount": -5`                  | `400 Bad Request` |
| Enum inválido                 | `POST /api/accounts` com `"type": "XYZ"`                     | `400 Bad Request` |
| Conta inexistente             | `GET /api/accounts/9999`                                     | `404 Not Found` |
| Transação inexistente         | `GET /api/transactions/9999`                                 | `404 Not Found` |
| Transação em conta inexistente| `POST /api/transactions` com `"accountId": 9999`             | `404 Not Found` |

> O código `422` (regra de negócio) existe, mas hoje só é disparado por **conta inativa** — e não há endpoint para inativar uma conta, então não é facilmente reproduzível pela API.

## 10. Observações

- **Relatório por mês**: `GET /api/reports/summary` soma as transações do **mês atual**. Use datas do mês corrente para vê-las no relatório.
- **Kafka**: ao criar transações, os logs da aplicação mostram os eventos publicados/consumidos (`TRANSACTION_CREATED`, `LOW_BALANCE`).
- **Ordem das pastas**: executar um `DELETE` ou `GET by ID` antes de criar retorna `404` — é esperado, pois as variáveis ainda apontam para IDs que não existem.

