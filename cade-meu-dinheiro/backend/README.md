# Backend – Cadê meu dinheiro? (Java + Spring Boot)

Registre gastos em segundos e veja para onde o dinheiro está indo. Sem banco conectado.

## Como executar
Requisitos: **JDK 17 ou superior** e **Maven 3.9+** (ou abra a pasta no IntelliJ, Eclipse ou VS Code como projeto Maven). Na primeira execução o Maven baixa as dependências, então é preciso internet.

```
mvn spring-boot:run        # abre em http://localhost:8080 (somente a API; o site fica no frontend)
mvn test                   # testes automatizados
```
Login de teste (dados iniciais): `demo@exemplo.com` / `senha12345`.
Variáveis opcionais: `PORT`, `APP_SECRET` (assinatura dos tokens), `DADOS_TESTE=false` (não criar usuário de teste).

## Estrutura (arquitetura em camadas)
```
src/main/java/br/cademeudinheiro/
├── Application.java
├── controller/   Recebem as requisições HTTP e devolvem JSON (sem regra de negócio)
│   ├── AuthController, CategoriaController, GastoController, ResumoController, DocsController
├── service/      Regras de negócio
│   ├── AuthService, CategoriaService, GastoService, ResumoService
├── repository/   Acesso ao banco (JDBC), uma classe por tabela do DER
│   ├── UsuarioRepository, CategoriaRepository, GastoRepository
├── model/        Entidades do DER: Usuario, Categoria, Gasto
├── dto/          Objetos de entrada e saída da API (requests e responses)
├── validation/   Validacao: regras RN02, RN04, RN05 e mês
├── security/     Tokens (sessão de 7 dias) e AuthInterceptor (protege as rotas)
├── exception/    ApiException e ErroHandler (formato único de erro)
└── config/       WebConfig, SegurancaConfig (BCrypt) e DadosIniciais (categorias e usuário de teste)
src/main/resources/   application.properties e schema.sql (DER)
src/test/java/        controller/ApiTest (integração) e validation/ValidacaoTest (unitário)
```
Fluxo de uma requisição: `controller` → `service` → `repository` → banco.

## Rastreabilidade
| História | RF | Endpoint | Tela | Regras |
|---|---|---|---|---|
| US01 | RF01, RF02 | `POST /api/auth/registrar`, `POST /api/auth/entrar` | Entrar / Criar conta | RN06 |
| US02 | RF03, RF04 | `POST /api/gastos`, `GET /api/categorias` | Novo gasto | RN02–RN05 |
| US03 | RF05, RF10 | `GET /api/gastos?mes=AAAA-MM` | Histórico | RN01, RN07 |
| US04 | RF06, RF07 | `PUT /api/gastos/{id}`, `DELETE /api/gastos/{id}` | Novo gasto (edição), Histórico | RN01, RN08 |
| US05, US06 | RF08, RF09 | `GET /api/resumo?mes=AAAA-MM` | Resumo | RN07 |

## Documentação da API
Também em `GET /api/docs`. Rotas protegidas exigem `Authorization: Bearer <token>`. Erros: `{"erro":{"mensagem":"...","campo":"valor"}}`.

| Método e caminho | Corpo / parâmetros | Sucesso | Erros |
|---|---|---|---|
| `POST /api/auth/registrar` | `nome`, `email`, `senha` (8 a 72) | 201 `{token, usuario}` | 422, 409 e-mail em uso |
| `POST /api/auth/entrar` | `email`, `senha` | 200 `{token, usuario}` | 401 |
| `GET /api/categorias` | – | 200 lista | 401 |
| `GET /api/gastos` | `?mes=AAAA-MM` (padrão: mês atual) | 200 `{mes, gastos[]}` | 400, 401 |
| `POST /api/gastos` | `valor`, `categoria_id`, `data?`, `descricao?` | 201 gasto | 422, 401 |
| `PUT /api/gastos/{id}` | igual ao POST | 200 gasto | 422, 404, 401 |
| `DELETE /api/gastos/{id}` | – | 200 `{mensagem}` | 404, 401 |
| `GET /api/resumo` | `?mes=AAAA-MM` | 200 `{mes, total, por_categoria[]}` | 400, 401 |

## Banco de dados
`usuario` 1:N `gasto` e `categoria` 1:N `gasto`, com os atributos do dicionário de dados (UUID, NUMERIC(12,2), DATE etc.). Por padrão usa **H2 em arquivo** (pasta `data/`), em modo PostgreSQL, para rodar sem instalar nada. Para usar **PostgreSQL**, defina `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`; o `schema.sql` funciona nos dois. A regra de data não futura (RN04) é validada na API.

## Testes
`mvn test` roda 48 testes com banco H2 em memória:
- `controller/AuthApiTest` (US01), `GastoApiTest` (US02 a US04), `ResumoApiTest` (US05, US06): critérios de aceite e regras de negócio, pela API.
- `controller/ErrosApiTest`: formato e status dos erros (400, 401, 404, 405, 409, 422).
- `repository/BancoDadosTest`: restrições do DER (PK, FK, UNIQUE, CHECK, CASCADE).
- `validation/ValidacaoTest`: validações de valor, data, descrição e mês.

## Diferenças em relação aos ADRs
Backend em Java/Spring Boot, e não Supabase (ADR-003). O banco é H2 por padrão, com PostgreSQL por configuração (ADR-002).
