# Sprint 1 — Finance Control API

**Duração:** 2 semanas

## Sprint Goal
Tornar a API **multiusuário e segura** (autenticação JWT + ownership) e **preparada para produção** (migrations, versionamento, documentação, paginação e base de testes de integração).

## Escopo
- **Comprometido (17 pts):** FIN-001 → FIN-006
- **Stretch (7 pts):** FIN-007 → FIN-009

## Definition of Ready (DoR)
- Descrição, critérios de aceite e dependências preenchidos.
- Card quebrado em tamanho executável na sprint.

## Definition of Done (DoD)
- `./mvnw clean test` verde.
- Testes (unitário e/ou integração) para o que foi alterado.
- PR revisado e aprovado pelo Tech Lead.
- Documentação (README/docs/OpenAPI) atualizada.
- Sem regressão de comportamento.

## Backlog de cards

### FIN-001 — Padronizar JDK 21 + Maven Wrapper
- **Tipo:** Técnica · **Pontos:** 1 · **Prioridade:** Alta · **Dep:** — · **Semana:** 1
- **Descrição:** gerar Maven Wrapper (`mvn wrapper:wrapper`), subir `java.version` para 21 e atualizar a documentação (Java 17 → 21).
- **Critérios de aceite:**
  - `./mvnw clean test` passa sem Maven global instalado.
  - `java.version` = 21 no `pom.xml`.
  - README, GUIA_EXECUCAO e POSTMAN atualizados.

### FIN-002 — Migrations com Flyway
- **Tipo:** Técnica · **Pontos:** 2 · **Prioridade:** Alta · **Dep:** FIN-001 · **Semana:** 1
- **Descrição:** adicionar Flyway, criar `V1__create_accounts.sql` e `V2__create_transactions.sql` e trocar `ddl-auto: update` por `validate`. Habilitar Flyway também no perfil de teste (H2 em modo PostgreSQL).
- **Critérios de aceite:**
  - Schema criado exclusivamente por migration no startup.
  - `ddl-auto: validate`.
  - Banco novo sobe do zero; testes seguem verdes.

### FIN-003 — Versionamento `/v1` + OpenAPI (Swagger)
- **Tipo:** Técnica · **Pontos:** 2 · **Prioridade:** Alta · **Dep:** FIN-001 · **Semana:** 1
- **Descrição:** adicionar `springdoc-openapi-starter-webmvc-ui`, mover endpoints para `/api/v1/**` e documentar controllers/DTOs.
- **Critérios de aceite:**
  - `/swagger-ui.html` e `/v3/api-docs` acessíveis.
  - Endpoints sob `/api/v1/...`.
  - Collection Postman e docs atualizadas para `/v1`.

### FIN-004 — Usuário + Autenticação JWT
- **Tipo:** Funcional (Segurança) · **Pontos:** 5 · **Prioridade:** Alta · **Dep:** FIN-003 · **Semana:** 1 → 2
- **Descrição:** criar `User` (id, name, email, password_hash, created_at), endpoints `POST /api/v1/auth/register` e `POST /api/v1/auth/login`, Spring Security stateless com JWT e BCrypt, proteger os endpoints existentes.
- **Critérios de aceite:**
  - Register/login retornam token JWT.
  - Endpoints protegidos retornam `401` sem token.
  - Senha armazenada com BCrypt (nunca em texto puro).
  - Testes unitários do serviço de autenticação.

### FIN-005 — Multiusuário (Ownership)
- **Tipo:** Funcional (Segurança) · **Pontos:** 5 · **Prioridade:** Alta · **Dep:** FIN-004, FIN-002 · **Semana:** 2
- **Descrição:** migration `V3` adiciona `user_id` em accounts/transactions; vincular criação ao usuário autenticado; filtrar todas as consultas por usuário; bloquear acesso cruzado.
- **Critérios de aceite:**
  - Contas/transações isoladas por usuário.
  - Usuário A não vê/edita/exclui dados de B (403/404).
  - Relatório calcula apenas os dados do usuário logado.
  - Testes de integração de ownership.

### FIN-006 — Paginação e ordenação
- **Tipo:** Qualidade · **Pontos:** 2 · **Prioridade:** Média · **Dep:** FIN-003 · **Semana:** 2
- **Descrição:** usar `Pageable`/`Page` nos GET de listagem (accounts, transactions), com metadados de paginação na resposta.
- **Critérios de aceite:**
  - `?page=0&size=20&sort=createdAt,desc` funciona.
  - Resposta com `content`, `page`, `size`, `totalElements`, `totalPages`.

### FIN-007 — Testes de integração com Testcontainers *(stretch)*
- **Tipo:** Qualidade · **Pontos:** 3 · **Prioridade:** Média · **Dep:** FIN-005 · **Semana:** 2
- **Descrição:** Testcontainers PostgreSQL + Kafka para testes de repositório e fluxo ponta-a-ponta.
- **Critérios de aceite:** suíte roda com banco/broker reais; executa em perfil separado (ex.: `integration`).

### FIN-008 — Pipeline CI (GitHub Actions) *(stretch)*
- **Tipo:** Ops · **Pontos:** 2 · **Prioridade:** Média · **Dep:** FIN-001 · **Semana:** 2
- **Descrição:** workflow de build + test (e integração), com cache e JDK 21.
- **Critérios de aceite:** PR dispara build; falha se testes falharem.

### FIN-009 — Observabilidade *(stretch)*
- **Tipo:** Ops · **Pontos:** 2 · **Prioridade:** Baixa · **Dep:** FIN-001 · **Semana:** 2
- **Descrição:** logs estruturados (JSON), métricas de negócio customizadas e `/actuator/prometheus`.
- **Critérios de aceite:** logs estruturados; métricas expostas; log de eventos de negócio.

## Sequência e distribuição (2 semanas)

**Semana 1**
- D1–D2: FIN-001 + FIN-002
- D3–D4: FIN-003
- D5–D7: FIN-004 (início — entidade + endpoints de auth)

**Semana 2**
- D8–D9: FIN-004 (conclusão — filtro JWT + proteção) + revisão de segurança
- D10–D12: FIN-005 (ownership)
- D13: FIN-006 (paginação)
- D14: buffer + stretch (FIN-007/008/009) + review/retro

## Dependências
- FIN-002 depende de FIN-001
- FIN-003 depende de FIN-001
- FIN-004 depende de FIN-003
- FIN-005 depende de FIN-004 e FIN-002
- FIN-006 depende de FIN-003
- FIN-007 depende de FIN-005

## Riscos e mitigação
- **JWT/Segurança (FIN-004/005)** é o ponto mais delicado → pair programming + revisão rigorosa + testes de segurança.
- **Migrations (FIN-002)** podem quebrar dados existentes → validar em banco novo e em banco com dados.
- **Testcontainers (FIN-007)** exige Docker no CI → se travar, postergar para a Sprint 2.

## Rituais sugeridos
- **Daily** (15 min)
- **Planning** no início e **Review/Retro** no fim
- **Code review** obrigatório em cada PR (Tech Lead)

