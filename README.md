# Teams Tracking System

Sistema fullstack para rastreamento de equipes externas, com CRUD de agentes, sincronizacao com API GPS externa, historico de localizacoes, check-ins, rota do dia, painel de sincronizacao e tratamento resiliente de falhas da integracao.

## Tecnologias

### Backend

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring WebFlux / WebClient
- MySQL
- Maven

### Frontend

- Next.js 16 com App Router
- React 19
- Tailwind CSS
- TanStack Query
- React Hook Form
- Zod
- shadcn/ui

### Infraestrutura

- Docker
- Docker Compose
- MySQL 8

## Funcionalidades

- CRUD de agentes
- Listagem e detalhe de agentes
- Registro manual de check-ins
- Listagem de check-ins por agente e geral
- Sincronizacao de agentes com API externa
- Sincronizacao de localizacoes com API externa
- Sincronizacao incremental de check-ins com `syncToken`
- Historico de rota do dia por agente
- Calculo de distancia com Haversine
- Descarte de leituras GPS com `accuracy > 50`
- Tratamento de rate limit `429` com `Retry-After`
- Retry para instabilidade `503` com backoff exponencial e jitter
- Paginacao nas sincronizacoes externas
- Logs persistidos das sincronizacoes
- Painel frontend de monitoramento
- Tratamento global de excecoes no backend

## Estrutura do projeto

```text
teams-tracking-system/
├── backend/
├── frontend/
├── docs/
├── docker-compose.yml
└── README.md
```

## Executando com Docker

Na raiz do projeto, execute:

```bash
docker compose up --build
```

Apos subir os containers, acesse:

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- MySQL: localhost:3306

Para parar os containers:

```bash
docker compose down
```

Para parar e remover o volume do banco:

```bash
docker compose down -v
```

## Executando localmente

### Banco de dados

Crie um banco MySQL local:

```sql
CREATE DATABASE teams_tracking;
```

Configuracao padrao do backend:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/teams_tracking
    username: root
    password:
```

### Backend

Entre na pasta do backend:

```bash
cd backend
```

Execute a aplicacao com Maven:

```bash
mvn spring-boot:run
```

Ou gere o jar:

```bash
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

Observacao: o Maven Wrapper do projeto pode exigir os arquivos da pasta `.mvn/wrapper`. Caso ele nao esteja completo, use Maven instalado localmente ou execute pelo Docker.

### Frontend

Entre na pasta do frontend:

```bash
cd frontend
```

Instale as dependencias:

```bash
npm install
```

Crie um arquivo `.env.local` apontando para o backend:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Execute em modo desenvolvimento:

```bash
npm run dev
```

Acesse:

```text
http://localhost:3000
```

## API externa

Base URL:

```text
https://desafio-media.onrender.com
```

A chave da API esta configurada em `backend/src/main/resources/application.yaml` e tambem pode ser sobrescrita por variaveis de ambiente no Docker Compose.

## Principais endpoints

### Agentes

```http
GET    /api/agents
GET    /api/agents/{id}
POST   /api/agents
PUT    /api/agents/{id}
DELETE /api/agents/{id}
```

### Rotas

```http
GET /api/agents/{id}/route
```

### Check-ins

```http
GET  /api/v1/check-ins
POST /api/agents/{id}/check-ins
GET  /api/agents/{id}/check-ins
```

### Sincronizacao

```http
POST /api/v1/sync/agents
POST /api/v1/sync/locations
POST /api/v1/sync/check-ins
POST /api/v1/sync/all
GET  /api/v1/sync/logs
GET  /api/v1/sync/logs/{syncType}
```

## Tratamento de erros

O backend possui tratamento global de excecoes e retorna erros no formato:

```json
{
  "error": {
    "code": "string",
    "message": "string",
    "details": "string"
  }
}
```

Exemplos de cenarios tratados:

- Validacao de payload
- Entidade nao encontrada
- Conflitos de integridade no banco
- Falhas da API externa
- Erros em sincronizacoes paralelas
- Erros internos inesperados

## Sincronizacao

A sincronizacao e organizada no backend pelo `SyncService`, com logs persistidos em `SyncLog` e tokens em `SyncToken`.

Fluxos atuais:

- `AGENT_SYNC`: sincroniza agentes externos por `externalId`
- `POSITION_SYNC`: sincroniza localizacoes e atualiza a posicao atual do agente
- `CHECKIN_SYNC`: sincroniza eventos/check-ins externos de forma incremental

A aplicacao registra inicio, fim, status, quantidade processada, erro e token de sincronizacao quando disponivel.

## Regras de GPS

Localizacoes e check-ins externos passam por validacao basica antes de persistir:

- latitude obrigatoria
- longitude obrigatoria
- `accuracy <= 50` quando a acuracia for informada

Isso evita que dados muito imprecisos prejudiquem rotas e calculo de distancia.

## Documentacao adicional

As decisoes tecnicas estao documentadas em:

```text
docs/decisoes-tecnicas.md
```

## Observacoes

- Geofences foram removidas do escopo atual do projeto.
- O frontend consome o backend pela variavel `NEXT_PUBLIC_API_URL`.
- O Docker Compose configura o backend para acessar o MySQL pelo hostname interno `mysql`.
- Em ambiente local fora do Docker, confirme se o MySQL esta ativo antes de iniciar o backend.
