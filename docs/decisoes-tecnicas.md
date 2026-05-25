# Decisoes tecnicas

Este documento registra as principais decisoes tecnicas adotadas no projeto `teams-tracking-system`, considerando o estado atual da implementacao.

## Agentes

### Identificacao interna com Long

A entidade `Agent` utiliza `Long` como identificador principal interno, gerado pelo banco de dados.

Essa decisao mantem o modelo simples para o CRUD local e evita depender diretamente do formato dos identificadores da API externa como chave primaria da aplicacao.

### externalId como identificador da integracao

O campo `externalId` foi mantido como identificador unico do agente vindo da API externa.

Durante a sincronizacao, o sistema usa esse campo para localizar registros existentes e aplicar upsert: se o agente ja existe, ele e atualizado; se nao existe, um novo registro e criado.

Essa estrategia evita duplicidade em execucoes repetidas do scheduler e preserva a rastreabilidade entre o dado local e o dado externo.

### Status do agente

O status do agente e representado pelo enum `AgentStatus`, com valores como `ACTIVE` e `INACTIVE`.

No CRUD local, esse campo pode ser informado manualmente. Na sincronizacao externa, o status e atualizado conforme os dados recebidos da API.

### Localizacao atual no cadastro do agente

A entidade `Agent` tambem guarda os campos `currentLat`, `currentLng` e `lastSeenAt`.

Esses campos representam a posicao operacional mais recente do agente e sao atualizados durante a sincronizacao de localizacoes.

Essa decisao facilita consultas rapidas da posicao atual sem precisar buscar todo o historico de localizacoes.

## Localizacoes e rotas

### Persistencia local de localizacoes

As localizacoes recebidas da API externa sao persistidas localmente na entidade `Location`.

Essa decisao permite manter historico de movimentacao, montar rotas do dia, calcular distancia percorrida e reduzir a dependencia da API externa no momento da consulta.

### Id interno para localizacoes

A entidade `Location` utiliza `Long` como identificador interno gerado pelo banco.

Como a API externa nao fornece necessariamente um identificador unico para cada leitura de GPS, a idempotencia e tratada pela combinacao entre agente e horario de captura.

### Idempotencia por agente e recordedAt

Antes de salvar uma localizacao, o sistema verifica se ja existe um registro para o mesmo agente no mesmo `recordedAt`.

Essa regra evita duplicidade quando a mesma posicao e recebida mais de uma vez em execucoes proximas da sincronizacao.

### Uso de lastSeen como referencia temporal

O campo `lastSeen` da API externa e convertido para `recordedAt` na entidade `Location`.

Esse horario e usado para ordenar os pontos de localizacao e montar a rota do dia em ordem cronologica.

### Descarte de GPS impreciso

Leituras GPS com `accuracy` maior que 50 metros sao descartadas antes da persistencia.

Essa regra evita que pontos muito imprecisos prejudiquem o historico de rotas, a posicao atual do agente e o calculo de distancia.

### Rota do dia baseada em dados locais

A rota do dia e calculada a partir das localizacoes ja persistidas no banco local.

Essa decisao evita consultar a API externa durante a visualizacao da rota e valoriza o historico sincronizado pela propria aplicacao.

### Calculo com Haversine

A distancia percorrida e calculada com a formula de Haversine, que considera latitude e longitude para calcular a distancia entre dois pontos geograficos.

A logica foi isolada em `HaversineUtil`, evitando acoplamento com controllers e services e permitindo reutilizacao futura.

### Nao criacao de tabela de rotas

Nao foi criada uma tabela especifica para rotas.

A rota e calculada dinamicamente a partir dos registros existentes em `locations`, evitando duplicacao de dados e mantendo a modelagem mais simples.

## Check-ins

### Check-ins manuais e externos

Os check-ins representam eventos operacionais vinculados aos agentes.

Eles podem ser criados manualmente pela aplicacao ou sincronizados a partir da API externa.

### Identificacao interna com Long

A entidade `CheckIn` utiliza `Long` como identificador interno gerado pelo banco.

Essa decisao mantem consistencia com as demais entidades principais do projeto e simplifica o relacionamento com o agente.

### externalEventId para idempotencia

Eventos externos sao armazenados com `externalEventId`, marcado como unico.

Durante a sincronizacao, o sistema verifica esse identificador antes de criar um novo check-in. Se o evento ja existe, ele nao e duplicado.

### Origem do check-in

O campo `source` diferencia check-ins criados manualmente de eventos recebidos da API externa.

Check-ins manuais usam `MANUAL` por padrao. Eventos externos usam o valor recebido da API ou `EXTERNAL` quando a origem nao e informada.

### Tipo de check-in

O tipo do check-in e representado por `CheckInType`.

A classe `CheckInTypeMapper` converte valores externos, como `check_in`, `stop_detected` e `low_battery`, para os enums internos correspondentes.

Valores desconhecidos usam `CHECKIN` como fallback para evitar falhas de sincronizacao por tipos nao previstos.

### Dados operacionais do check-in

A entidade `CheckIn` armazena latitude, longitude, endereco, acuracia, velocidade, observacoes, distancia desde o ponto anterior, horario de ocorrencia e horario de sincronizacao.

Esses campos permitem representar tanto registros manuais simples quanto eventos operacionais vindos da API externa.

### Descarte de check-ins com GPS impreciso

Check-ins externos tambem passam pela validacao de GPS.

Eventos sem latitude/longitude ou com `accuracy` maior que 50 metros nao sao persistidos, seguindo a mesma regra aplicada as localizacoes.

## Sincronizacoes

### Historico persistido em SyncLog

Cada execucao de sincronizacao e registrada em `SyncLog`.

O log armazena tipo da sincronizacao, status, token de sincronizacao, quantidade de registros processados, mensagem de erro, horario de inicio e horario de fim.

Essa decisao atende ao requisito de manter historico persistido das sincronizacoes e permite alimentar o painel de monitoramento.

### Status da sincronizacao

O status da sincronizacao e representado por `SyncStatus`, com valores como `RUNNING`, `SUCCESS` e `FAILED`.

Cada sincronizacao e criada como `RUNNING`, atualizada para `SUCCESS` ao final da execucao ou para `FAILED` em caso de erro.

### SyncToken separado em SyncTokenStore

O token incremental tambem e persistido em uma tabela propria por meio de `SyncTokenStore`.

Essa separacao permite recuperar o ultimo token conhecido por tipo de sincronizacao antes de iniciar uma nova execucao.

### Sincronizacao incremental

A sincronizacao usa `syncToken` quando disponivel para buscar apenas dados novos ou alterados na API externa.

Check-ins usam o token salvo e tambem percorrem paginas por cursor quando a resposta externa informa continuidade.

### Paginacao obrigatoria

As sincronizacoes de agentes e localizacoes percorrem as paginas retornadas pela API externa usando `page`, `totalPages` e `limit=50`.

Essa decisao garante que a aplicacao nao processe apenas a primeira pagina de dados.

### Rate limiting 429

O cliente da API externa trata respostas `429` respeitando o header `Retry-After` quando ele e informado.

Quando o header nao esta presente ou nao pode ser interpretado, o sistema aplica uma espera padrao antes de tentar novamente.

### Instabilidade 503

Respostas `503` sao tratadas com retry limitado e backoff exponencial com jitter.

Essa decisao evita retentativas agressivas e melhora a resiliencia contra instabilidades temporarias da API externa.

### Falhas registradas e propagadas

Quando uma sincronizacao falha, o sistema registra o erro em `SyncLog` e propaga a excecao.

Dessa forma, os endpoints nao retornam sucesso falso quando a sincronizacao nao foi concluida corretamente.

### Sincronizacao completa

O endpoint de sincronizacao completa executa agentes, localizacoes e check-ins em paralelo usando `CompletableFuture`.

Se alguma execucao falhar, o erro e propagado e tratado pelo mecanismo global de excecoes.

## Schedulers

### Schedulers independentes

O projeto possui schedulers separados para agentes, posicoes e check-ins.

Essa separacao permite configurar frequencias diferentes para cada fluxo e reduz o acoplamento entre sincronizacoes.

### Frequencias atuais

A sincronizacao de posicoes executa com intervalo menor, pois representa dados operacionais mais volateis.

Agentes e check-ins possuem schedulers proprios, permitindo evolucao independente conforme a necessidade do sistema.

### Monitoramento de sincronizacao

O projeto possui endpoints para consultar os logs de sincronizacao.

Esses endpoints sao usados pelo frontend para montar o painel de monitoramento, exibindo historico, status, erros e quantidade de registros processados.

## Tratamento de erros

### GlobalExceptionHandler

Foi criado um `GlobalExceptionHandler` para padronizar respostas de erro da API.

As respostas seguem o formato:

```json
{
  "error": {
    "code": "string",
    "message": "string",
    "details": "string"
  }
}
```

### Erros tratados

O handler cobre erros de validacao, payload invalido, entidade nao encontrada, conflito de integridade, erro da API externa, falhas em execucoes paralelas e erros internos inesperados.

Essa decisao evita expor stack traces ao frontend e deixa o contrato de erro mais previsivel.

## Frontend

### Next.js com App Router

O frontend utiliza Next.js com App Router.

As telas foram organizadas por rotas de agentes, check-ins, sincronizacao e monitoramento.

### TanStack Query

O frontend usa TanStack Query para buscar dados, disparar mutacoes e invalidar caches apos operacoes como criar, atualizar, excluir e sincronizar.

Essa decisao reduz estado manual e melhora a consistencia das telas apos alteracoes no backend.

### Contratos centralizados

As chamadas HTTP ficam centralizadas em `src/lib/api.ts` e os tipos TypeScript em `src/lib/types.ts`.

Essa separacao facilita manutencao dos contratos entre frontend e backend.

### Busca e visualizacao operacional

A interface permite listar agentes, consultar detalhes, registrar check-ins, visualizar rota do dia, acompanhar check-ins e monitorar logs de sincronizacao.

Os IDs continuam preservados nos contratos, mas a interface prioriza informacoes operacionais como nome, status, localizacao, horarios e resultados de sincronizacao.

## Docker

### Containers separados

O projeto possui Dockerfiles separados para backend e frontend.

O `docker-compose.yml` sobe MySQL, backend e frontend, mantendo cada responsabilidade em um container proprio.

### Backend

O backend e construido com Maven em uma etapa de build e executado em uma imagem JRE com Java 17.

Essa estrategia evita depender do Maven Wrapper local, que nao esta completo no projeto, e gera uma imagem final menor para execucao.

### Frontend

O frontend e construido com Node.js, executando `npm ci`, `npm run build` e depois `npm run start`.

A variavel `NEXT_PUBLIC_API_URL` aponta para `http://localhost:8080`, permitindo que o navegador acesse o backend publicado pelo Docker Compose.

### Banco de dados

O MySQL roda em container proprio com volume persistente.

O backend acessa o banco pelo hostname interno `mysql`, configurado no `docker-compose.yml`.
