# MachForms — Backend

API REST do sistema de cadastro de participantes em retiros e acampamentos para jovens.

**Stack:** Java 21 · Spring Boot 3.5 · Maven · Spring Web · Spring Data JPA · Spring Security (JWT) · Spring DevTools · MySQL · Bean Validation

## Como rodar

1. Tenha instalados **Java 21**, **Maven 3.9+** e o **MySQL** rodando na porta padrão `3306`.
2. Informe o usuário e a senha do MySQL (o banco `machforms_db` é criado sozinho na primeira execução):

   ```bash
   # Linux / macOS
   export DB_USERNAME=root
   export DB_PASSWORD=sua_senha

   # Windows (PowerShell)
   $env:DB_USERNAME="root"; $env:DB_PASSWORD="sua_senha"
   ```

   Ou edite `spring.datasource.username` / `spring.datasource.password` em `src/main/resources/application.properties`.
3. Suba a API:

   ```bash
   mvn spring-boot:run
   ```

   A API fica em `http://localhost:3000`, a mesma base que o frontend usa (`API_URL = 'http://localhost:3000/api'`).
   Para trocar a porta, defina a variável `PORT`. Se você serve o HTML na porta 3000, mude uma das duas.
4. Testes (usam H2 em memória, não precisam do MySQL): `mvn test`

> Em produção defina `MACHFORMS_JWT_SECRET` (mínimo de 32 caracteres) e ajuste `machforms.cors.allowed-origin-patterns`.

### Já rodou a versão anterior do backend?

O Hibernate (`ddl-auto=update`) adiciona as colunas novas do evento sozinho, mas **não** afrouxa regras de colunas que já existem. O e-mail do organizador deixou de ser obrigatório, então rode uma vez:

```sql
ALTER TABLE organizadores MODIFY email VARCHAR(150) NULL;
```

Em desenvolvimento, é mais simples apagar o banco (`DROP DATABASE machforms_db;`) e deixar a API recriá-lo.

## Arquitetura

```
com.machforms
├── config        SecurityConfig, CorsConfig, JwtProperties, CorsProperties
├── controller    Camada HTTP: só recebe, valida (@Valid) e delega
├── service       Interfaces das regras de negócio (EventoService, ParticipanteService, ChatService, ...)
│   └── impl      Implementações
├── repository    Spring Data JPA
├── domain        Entidades (Organizador, Evento, Participante) e enums com rótulo
├── dto           request / response (records imutáveis)
├── mapper        Conversão entidade <-> DTO
├── security      TokenService (JWT), filtro, UserDetailsService, handler 401/403
├── validation    Validações customizadas (@Telefone, @ChaleExigeResponsavel, @EventoInformado)
├── exception     Exceções de negócio + GlobalExceptionHandler
└── util          Normalizadores e formato de data
```

Como os princípios foram aplicados:

- **S — Responsabilidade única:** controller só trata HTTP; service só regra de negócio; mapper só conversão; `GeradorDeProtocolo`, `TokenService` e `OrganizadorService` têm um motivo único para mudar.
- **O — Aberto/fechado:** novos valores (ex.: "Dia 4", outra restrição alimentar) entram nos enums, sem mexer em serviços. Todos os enums seguem `RotuloEnum`/`Rotulos`.
- **L — Substituição de Liskov:** qualquer implementação de `TokenService`, `GeradorDeProtocolo` ou `ChatService` pode ser trocada sem afetar quem usa.
- **I — Segregação de interfaces:** interfaces pequenas e específicas (`AuthService`, `EventoService`, `ParticipanteService`, `GeradorDeProtocolo`, `ChatService`).
- **D — Inversão de dependência:** controllers e serviços dependem de interfaces, injetadas por construtor.
- **Clean Code:** nomes que revelam intenção (domínio em português, igual ao frontend), métodos curtos, sem números mágicos, DTOs imutáveis (`record`), erros em um formato único.

## Endpoints (contrato com o frontend)

| Método | Rota | Acesso | Usado em |
|---|---|---|---|
| POST | `/api/eventos` | público | `submitEvento()`: cria organizador + evento |
| GET | `/api/eventos` | público | `loadEventosBusca()`: devolve **todos** os eventos (array) |
| GET | `/api/eventos/meus` | 🔒 token | eventos do organizador logado |
| POST | `/api/login` | público | `submitLogin()` |
| POST | `/api/inscricoes` | público | `submitRestricao()`: fim do fluxo de inscrição |
| GET | `/api/inscricoes` | 🔒 token | `loadPainel()` |
| POST | `/api/chat` | 🔒 token | `sendChat()` |

Rotas protegidas exigem `Authorization: Bearer <token>`, que o frontend já envia.

A **busca, a ordenação (alfabética, mais recentes, mais próximos) e a paginação são feitas no próprio frontend** (`renderResultadosBusca()`), então `GET /api/eventos` devolve a lista completa. Os parâmetros `lat` e `lon` que o frontend envia são aceitos e ignorados.

### Exemplos

**POST /api/eventos** → `201` (corpo enviado por `submitEvento()`)
```json
{
  "nomeCriador": "Ana Souza",
  "dataNasc": "15/03/1990",
  "nomeEvento": "Trilha Verde 2027",
  "imagem": "https://exemplo.com/capa.jpg",
  "dataInicio": "2027-03-15T09:00",
  "localizacao": "Campos do Jordão, SP",
  "latitude": -22.7396,
  "longitude": -45.5911,
  "telefone": "(11) 91234-5678",
  "senha": "segredo123",
  "criadoEm": "2026-11-23T20:00:00.000Z"
}
```
Resposta (e cada item de `GET /api/eventos`):
```json
{
  "id": 1,
  "nomeEvento": "Trilha Verde 2027",
  "imagem": "https://exemplo.com/capa.jpg",
  "dataInicio": "2027-03-15T09:00:00",
  "localizacao": "Campos do Jordão, SP",
  "latitude": -22.7396,
  "longitude": -45.5911,
  "criadoEm": "2026-11-23T20:00:03.512Z"
}
```
- Opcionais: `imagem` (se vier, precisa ser URL `http(s)://`), `latitude`, `longitude` e `email`.
- O `criadoEm` enviado pelo navegador é ignorado; vale a data do servidor.
- `email` (opcional) é o login do organizador. Sem ele o evento é criado, mas o organizador não consegue entrar (veja abaixo).

**POST /api/login** → `200`
```json
{ "email": "ana@email.com", "senha": "segredo123" }
```
```json
{
  "token": "eyJ...", "tipo": "Bearer", "expiraEmSegundos": 28800,
  "nomeCriador": "Ana Souza", "email": "ana@email.com",
  "evento": { "id": 1, "nomeEvento": "Trilha Verde 2027", "...": "..." }
}
```

**POST /api/inscricoes** → `201` (corpo montado por `submitRestricao()`)
```json
{
  "eventoId": 1,
  "evento": "Trilha Verde 2027",
  "nome": "Ana Beatriz Souza",
  "email": "ana@email.com",
  "telefone": "(11) 91234-5678",
  "dias": ["Dia 1", "Dia 2"],
  "dormida": "Chalé",
  "responsavelChale": "Carlos Souza",
  "dataNasc": "20/05/2005",
  "restricoes": ["Vegano"]
}
```
O evento é localizado por `eventoId`; sem id, pelo nome (`evento`). Resposta: os mesmos campos, mais `protocolo` (ex.: `"MF483920"`) e `inscritoEm`.

**POST /api/chat** → `200`
```json
{ "message": "Olá" }
```
```json
{ "reply": "Recebi sua mensagem!" }
```

### Erros

O frontend exibe `data.message`, então todo erro traz `message`. Em `400`, a `message` já junta o motivo de cada campo recusado, e `campos` detalha campo por campo:

```json
{
  "timestamp": "2026-11-23T20:00:00Z",
  "status": 400,
  "erro": "Bad Request",
  "message": "Informe o nome do responsável pelo chalé",
  "caminho": "/api/inscricoes",
  "campos": { "responsavelChale": "Informe o nome do responsável pelo chalé" }
}
```

| Status | Quando |
|---|---|
| 400 | campo inválido, valor fora da lista, data de nascimento fora de `dd/mm/aaaa`, `dataInicio` fora de `aaaa-mm-ddThh:mm` |
| 401 | sem token, token expirado ou login errado |
| 404 | evento da inscrição não existe |
| 409 | e-mail de organizador já cadastrado, nome de evento repetido, e-mail já inscrito no evento |

## Pontos do frontend que o backend não consegue resolver sozinho

1. **O login é por e-mail, mas o formulário "Cadastro do evento" não pede e-mail.** O backend aceita o evento sem e-mail (para não quebrar o cadastro), só que esse organizador nunca consegue fazer login. Para o login funcionar, acrescente o campo no formulário e envie `email`:
   ```js
   ${field('evEmail','E-mail','nome@email.com','email')}${errBox('evEmail')}
   // em novoEvento: email: v('evEmail'),
   ```
2. **Texto da tela "Inscrição confirmada":** "Uma senha de acesso ao chat foi enviada para o e-mail dos responsáveis cadastrados." O frontend não envia e-mail de responsável nem tem login de participante, e o backend não envia e-mails. Isso não foi implementado.
3. **Bugs no HTML** (não afetam o backend):
   - `addMsg` usa `'msg ' + false`; o correto é `'msg ' + from` (sem isso as mensagens do chat perdem o estilo de usuário/bot).
   - `go('participante')` (botão Voltar da tela "dormida") aponta para uma tela que não existe; o nome correto é `cadastro`.
   - O botão `Adiminstração` está com erro de digitação.
   - O template da tela `chat` tem um `</div>` sobrando.
4. **CORS:** servindo o HTML por `http://localhost:<porta>` já funciona. Abrindo o arquivo direto do disco (`file://`) o navegador envia `Origin: null`; acrescente `null` em `machforms.cors.allowed-origin-patterns`.

## Decisões de modelagem

- **Datas de nascimento** trafegam como `dd/mm/aaaa` (igual ao frontend) e são validadas de forma estrita (`31/02/2000` é recusado). `dataInicio` usa o formato do `datetime-local` (`aaaa-mm-ddThh:mm`) e volta sem fuso, que o `new Date(...)` do frontend lê como horário local.
- **Enums** trafegam com o texto exato da interface (`"Dia 1"`, `"Chalé"`, `"Alérgico a amendoim"`) e são gravados no banco pelo nome (`DIA_1`, `CHALE`, ...).
- **Telefone** é validado com máscara (10 a 13 dígitos) e gravado apenas com dígitos.
- **Senhas** são guardadas com BCrypt; nunca em texto puro.
- **Responsável pelo chalé** só é obrigatório (e só é gravado) quando `dormida = "Chalé"`.
- **Imagem do evento** é uma URL (como o frontend envia), não um arquivo enviado.
- **Banco:** `docs/schema.sql` documenta o modelo; as tabelas são criadas automaticamente pelo Hibernate (`ddl-auto=update`). Imagem, data de início, localização e coordenadas aceitam nulo no banco para não quebrar bases já existentes; a obrigatoriedade de data de início e localização é validada na API.
