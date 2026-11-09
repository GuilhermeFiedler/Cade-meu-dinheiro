# Relatório de Segurança e Correções: Cadê meu dinheiro? (MVP)

**Univille**

**Alunos:** Guilherme T. Fiedler e Clara M. Martinovsky

**Data:** 09 de novembro de 2026

## 1. Introdução

Este relatório apresenta a análise de segurança do MVP *Cadê meu dinheiro?*, um aplicativo web para registrar gastos pessoais, e documenta as correções aplicadas. A **Parte 1** descreve as vulnerabilidades encontradas. A **Parte 2** descreve, para cada uma, o que foi alterado, em quais arquivos e como validar.

**Escopo.** Código-fonte do repositório: backend em Java com Spring Boot, frontend em React 18 com Vite 5, configurações, testes e documentação.

**Método.** Revisão manual de código via Claude Code(análise estática). Foram lidos todos os controllers, serviços, repositórios, configurações e serviços do frontend, buscando problemas nas categorias do OWASP Top 10 (2021): injeção, controle de acesso, autenticação, configuração insegura e componentes desatualizados. A gravidade (Alta, Média ou Baixa) considera impacto e probabilidade em um cenário de publicação do sistema.

## 2. Visão geral do sistema

O navegador carrega o frontend React (porta 5173), que chama a API em `/api` por meio de um proxy do Vite. O backend Spring Boot (porta 8080) acessa o banco por JDBC (H2 em arquivo por padrão, PostgreSQL por configuração). O banco tem três tabelas: `usuario`, `categoria` e `gasto`. As senhas são guardadas com BCrypt e a sessão usa um token assinado com HMAC-SHA256, enviado no cabeçalho `Authorization`.

# Parte 1. Vulnerabilidades encontradas

## 3. Resumo dos achados

Não foi encontrada vulnerabilidade crítica no código em si. Os riscos mais relevantes estão na configuração e na forma de publicação do sistema.

| ID | Achado | Gravidade | OWASP Top 10 (2021) |
| --- | --- | --- | --- |
| V1 | Usuário de teste com senha pública criado por padrão | Alta (em produção) | A07, A05 |
| V2 | Sem limite de tentativas de login (força bruta) | Média | A07 |
| V3 | Rotas protegidas por lista, não por padrão | Média | A01, A04 |
| V4 | Token em localStorage, de 7 dias e sem revogação | Média | A07, A04 |
| V5 | Sem cabeçalhos de segurança, HTTPS nem política de CORS | Média | A05 |
| V6 | Enumeração de e-mails | Média/Baixa | A07 |
| V7 | Banco H2 em arquivo e .gitignore incompleto | Média/Baixa | A05 |
| V8 | Logs em nível DEBUG ativos | Baixa | A05 |
| V9 | Política de senha fraca, sem verificação de e-mail nem recuperação | Baixa | A07 |
| V10 | Dependências desatualizadas | A confirmar | A06 |
| V11 | /api/docs público e respostas sem Cache-Control | Baixa | A05 |

## 4. Detalhamento

### V1. Usuário de teste com senha pública (Alta em produção)

**Onde:** `DadosIniciais.java`, `application.properties` (linha `app.dados-teste=${DADOS_TESTE:true}`) e os README.

**Problema:** o padrão cria a conta `demo@exemplo.com` com a senha `senha12345`, e essa credencial está escrita nos README. Quem publicar o sistema sem definir a variável `DADOS_TESTE` deixa no ar uma conta que qualquer pessoa consegue usar.

### V2. Sem limite de tentativas de login (Média)

**Onde:** `AuthController` e `AuthService`.

**Problema:** não há bloqueio por IP nem por conta. Um atacante pode testar senhas em grande volume. O BCrypt deixa cada tentativa mais lenta, mas não impede o ataque. O cadastro também pode ser chamado sem limite.

### V3. Rotas protegidas por lista (Média)

**Onde:** `WebConfig.java`.

**Problema:** a autenticação só era aplicada aos caminhos listados (`/api/categorias`, `/api/gastos/**`, `/api/resumo`). Qualquer rota nova ficaria pública por padrão se alguém esquecesse de incluí-la na lista. Hoje nenhuma rota vaza, mas o erro é fácil de cometer.

### V4. Token em localStorage, de 7 dias e sem revogação (Média)

**Onde:** `sessao.js` (frontend) e `Tokens.java`.

**Problema:** o token fica em `localStorage`, e um XSS futuro poderia roubá-lo. O token não tem estado no servidor: não existe logout no servidor nem como invalidar um token roubado antes dos 7 dias. Ponto positivo: não foi encontrado `dangerouslySetInnerHTML`, `innerHTML` nem `eval` no frontend, e o React escapa o conteúdo por padrão, então hoje não há XSS identificado.

### V5. Sem cabeçalhos de segurança, HTTPS nem política de CORS (Média)

**Onde:** backend e documentação de execução.

**Problema:** o backend não define `Content-Security-Policy`, `X-Content-Type-Options`, `X-Frame-Options` nem HSTS, e não há configuração de CORS ou de HTTPS. O README descreve só o ambiente de desenvolvimento (proxy do Vite), e o requisito RNF09 exige HTTPS em toda a comunicação.

### V6. Enumeração de e-mails (Média/Baixa)

**Onde:** `AuthService.java`.

**Problema:** o cadastro responde que o e-mail já está em uso (409). Além disso, o login com e-mail inexistente respondia mais rápido, porque o BCrypt não era executado. As duas diferenças permitem descobrir quem tem conta.

### V7. Banco H2 em arquivo e .gitignore incompleto (Média/Baixa)

**Onde:** `application.properties` e `.gitignore`.

**Problema:** o padrão é um H2 em `./data` com usuário `sa` e senha vazia. O `.gitignore` não cobria `backend/data/`, `target/` nem `.idea/` (a pasta `.idea` veio no próprio zip do projeto), e alguns padrões eram ancorados em um caminho que não existe a partir da pasta do arquivo. Se o banco local for commitado, os hashes de senha dos usuários vão junto para o repositório. O próprio README reconhece que o H2 em arquivo não serve para produção.

### V8. Logs em nível DEBUG (Baixa)

**Onde:** `application.properties`.

**Problema:** os loggers de inicialização do banco (`jdbc.datasource.init` e `boot.sql.init`) estavam em DEBUG, o que gera ruído e pode expor detalhes do banco nos logs de produção.

### V9. Política de senha, e-mail e recuperação (Baixa)

**Onde:** `AuthService.java`.

**Problema:** a única regra era ter entre 8 e 72 caracteres, então senhas como `12345678` eram aceitas. Também não há verificação de e-mail nem recuperação de senha.

### V10. Dependências desatualizadas (a confirmar)

**Onde:** `pom.xml` e `frontend/package.json`.

**Problema:** o projeto usava Spring Boot 3.3.5 e Vite 5.4.10, versões de 2024. Segundo pesquisa feita em 09/11/2026, a linha 3.3 do Spring Boot já saiu de suporte, e a linha 3.5 encerrou o suporte gratuito em 30/06/2026, com a versão 3.5.16 como último patch gratuito. Não foram verificadas vulnerabilidades específicas (CVEs) neste trabalho. Os arquivos `package-lock.json` também estavam no `.gitignore`, o que torna as instalações do frontend não reproduzíveis.

### V11. Detalhes menores (Baixa)

`/api/docs` lista todos os endpoints sem autenticação (útil a quem ataca, pouco risco isolado). As respostas com dados financeiros não enviavam `Cache-Control: no-store`.

## 5. O que já estava bem feito

- **Sem SQL injection:** todas as consultas usam parâmetros (`?`) via `JdbcTemplate`.
- **Sem acesso aos dados de outros usuários:** `UPDATE`, `DELETE` e consultas sempre filtram por `usuario_id`, e quem tenta mexer em gasto alheio recebe 404 (RN01 e RNF11, com teste automatizado).
- **Senhas com BCrypt** e **token assinado com HMAC-SHA256**, com comparação em tempo constante (`MessageDigest.isEqual`) e expiração.
- **Segredo aleatório** quando `APP_SECRET` não é definido, em vez de um valor fixo no código.
- **Validação rigorosa** de valor, data, mês e descrição, e **erros genéricos**, sem stack trace para o cliente.
- Nenhum segredo, chave ou arquivo `.env` commitado.

# Parte 2. Correções realizadas

## 6. Como cada vulnerabilidade foi tratada

| ID | Situação | Resumo da correção |
| --- | --- | --- |
| V1 | Corrigido | Usuário de teste só existe no perfil `dev` |
| V2 | Corrigido | Nova classe `LimiteTentativas` (HTTP 429) |
| V3 | Corrigido | Todas as rotas `/api/**` protegidas por padrão |
| V4 | Mitigado | Validade configurável, segredo mínimo de 32 caracteres, CSP documentada. Migração para cookie fica como trabalho futuro |
| V5 | Corrigido na API | Filtro de cabeçalhos e CORS opcional. Cabeçalhos do frontend e HTTPS ficam na configuração do servidor (documentado) |
| V6 | Parcial | Tempo de resposta igualado e limite de cadastros por IP. Mensagem 409 mantida |
| V7 | Corrigido | `.gitignore` refeito (dois arquivos) |
| V8 | Corrigido | Linhas de DEBUG removidas |
| V9 | Parcial | Lista de senhas previsíveis recusadas |
| V10 | Parcial | Spring Boot atualizado para 3.5.16. Frontend via `npm audit` |
| V11 | Parcial | `Cache-Control: no-store` aplicado. `/api/docs` mantido público |

### C1. Usuário de teste só no perfil dev (V1)

- `application.properties`: o padrão passou a ser `app.dados-teste=${DADOS_TESTE:false}`.
- **Novo** `application-dev.properties`: liga `app.dados-teste` (padrão `true`) apenas quando o perfil `dev` está ativo.
- `package.json`: o script `npm run dev` agora inicia o backend com `-Dspring-boot.run.profiles=dev`, então quem desenvolve continua tendo o usuário de teste. No IntelliJ, basta definir *Active profiles* = `dev` na configuração de execução.
- README e `backend/README.md` atualizados.

### C2. Limite de tentativas (V2)

- **Nova** classe `security/LimiteTentativas.java`: guarda, em memória e em janela deslizante, as falhas de login por e-mail e por IP e as tentativas de cadastro por IP. Passou do limite, a API responde **429** com a mensagem *Muitas tentativas. Tente novamente em alguns minutos.*
- Limites padrão: 5 falhas de login por e-mail em 15 minutos; 20 falhas por IP (quatro vezes o limite por e-mail, para pegar quem troca de e-mail a cada tentativa); 20 cadastros por IP. Um login correto zera o contador daquele e-mail.
- `AuthController` passou a receber o IP da requisição e `AuthService` usa o limitador nos métodos `registrar` e `entrar`.
- Valores configuráveis por `LIMITE_LOGIN_FALHAS`, `LIMITE_REGISTROS` e `LIMITE_JANELA_MIN`.

**Limite conhecido:** o contador fica em memória e vale para uma única instância do backend.

### C3. Rotas protegidas por padrão (V3)

Em `WebConfig.java`, a lista de rotas protegidas foi trocada por uma regra geral com exceções explícitas:

```java
registry.addInterceptor(autenticacao)
    .addPathPatterns("/api/**")
    .excludePathPatterns("/api/auth/**", "/api/docs");
```

Agora uma rota nova nasce protegida. Efeito colateral esperado: uma rota inexistente sem token passou a responder 401 (antes 404), o que também evita revelar quais rotas existem. O teste correspondente foi ajustado. Em `AuthInterceptor.java`, requisições `OPTIONS` (pré-verificação de CORS) passam sem token, pois quem responde a elas é o próprio mecanismo de CORS.

### C4. Mitigação do risco do token (V4)

- `Tokens.java`: a validade agora vem de `app.sessao-horas` (padrão 168 horas, que mantém o requisito RNF12 de 7 dias). Quem publicar pode reduzir pela variável `SESSAO_HORAS`.
- `Tokens.java`: se `APP_SECRET` for definido com menos de 32 caracteres, o backend **não inicia**. Vazio continua gerando uma chave aleatória.
- Para reduzir o impacto de um XSS, `docs/06-seguranca.md` traz uma política CSP para o servidor do frontend.

**Não resolvido:** o token continua em `localStorage` e sem revogação no servidor. A migração para cookie `HttpOnly` com `SameSite` exige tratar CSRF e mudar o frontend, e ficou como trabalho futuro.

### C5. Cabeçalhos de segurança e CORS (V5)

- **Nova** classe `security/CabecalhosSeguranca.java`, um filtro que adiciona a todas as respostas: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Referrer-Policy: no-referrer`, uma CSP restritiva para a API e `Cache-Control: no-store`. O `Strict-Transport-Security` (HSTS) só é enviado quando a requisição chega por HTTPS.
- `WebConfig.java`: CORS fica **desligado por padrão**. Só é habilitado se a variável `CORS_ORIGENS` listar os domínios do frontend (nunca `*`).
- **Novo** `docs/06-seguranca.md`: checklist de publicação (HTTPS, `APP_SECRET`, PostgreSQL, perfil dev desligado), exemplo de cabeçalhos para o servidor do frontend e a orientação sobre `SERVER_FORWARD_HEADERS_STRATEGY=native` quando houver proxy reverso.

### C6. Enumeração de e-mails (V6)

Em `AuthService.java`, o hash BCrypt de um valor descartável é criado na inicialização, e o login sempre executa o BCrypt, exista o e-mail ou não:

```java
String hash = u != null ? u.senhaHash() : hashFalso;
boolean confere = encoder.matches(tentativa, hash);
```

Isso iguala o tempo de resposta. Senhas acima de 72 caracteres (limite do BCrypt) nunca conferem e não são processadas, o que também evita gastar CPU com entradas gigantes. A mensagem 409 do cadastro foi mantida por usabilidade, e o abuso é limitado pelo limite de cadastros por IP (C2). **Risco aceito:** a solução completa é confirmar o e-mail por mensagem.

### C7. .gitignore (V7)

- `cade-meu-dinheiro/.gitignore` refeito: ignora `node_modules/`, `target/`, `backend/data/`, arquivos `*.mv.db`, `.env*` e logs, e deixa de ignorar os `package-lock.json`.
- **Novo** `.gitignore` na raiz do repositório: ignora `.idea/` e os mesmos itens.
- Como `.idea` já foi commitada, ela também precisa ser removida do índice do Git (git rm --cached).
- Para produção, o `docs/06-seguranca.md` exige PostgreSQL com usuário e senha próprios.

### C8. Logs (V8)

As duas linhas `logging.level...=DEBUG` foram removidas de `application.properties`.

### C9. Senhas previsíveis (V9)

- `Validacao.java`: novo método `senhaForte(senha, email)` recusa senhas de uma lista de senhas comuns (por exemplo `12345678`, `senha12345`, `password`), senhas de um único caractere repetido e senha igual ao e-mail ou à parte antes do @. A resposta é 422 com a mensagem *Escolha uma senha menos previsível*, no campo `senha`, e o frontend já exibe esse erro sem alteração.
- `AuthService.registrar` chama a validação depois da checagem de tamanho. A regra RN06 em `docs/01-requisitos.md` foi atualizada.
- Consequência: a senha usada nos testes (`12345678`) e no teste de ponta a ponta (`senha12345`) passou a ser recusada, então foi trocada por `Gasto#Teste2024` em `TestSupport.java` e `e2e/e2e.test.mjs`.

A lista é curta, de propósito. Em produção vale usar uma lista maior de senhas vazadas.

### C10. Dependências (V10)

- `pom.xml`: o pai do Spring Boot passou de **3.3.5** para **3.5.16**, a última versão gratuita da linha 3.x. É uma atualização dentro da mesma geração, de baixo risco, mas precisa ser confirmada rodando os testes. A migração para a linha 4.x deve ser planejada, pois o suporte gratuito da 3.5 acabou.
- Frontend: os comandos `npm audit` e a atualização do Vite estão em `docs/06-seguranca.md`. Eles precisam de internet e alteram o `package-lock.json`, por isso não foram aplicados nesta entrega.

### C11. Detalhes menores (V11)

O `Cache-Control: no-store` foi aplicado pelo filtro da C5. A rota `/api/docs` foi mantida pública por decisão: ela só lista endpoints, sem dados. Agora essa exceção está explícita no `WebConfig`.

## 7. Resumo técnico das mudanças no código

- **Classes novas:** `LimiteTentativas`, `CabecalhosSeguranca`.
- **Classes alteradas:** `Tokens`, `AuthInterceptor`, `AuthController`, `AuthService`, `Validacao`, `WebConfig`.
- **Configuração:** `application.properties` reescrito (comentários sem acentos, porque o Spring lê esse arquivo em Latin-1) e `application-dev.properties` novo.
- **Efeitos sobre quem usa o sistema:** não há mudança de telas nem de endpoints. Surgem as respostas 429 (muitas tentativas) e a nova mensagem 422 para senha previsível.

## 8. Testes

**Novos:**

- `LimiteApiTest`: bloqueio após falhas do mesmo e-mail; bloqueio persiste mesmo com a senha certa; login correto zera as falhas; bloqueio por IP ao variar e-mails; limite de cadastros por IP sem afetar outros IPs.
- `TokensTest`: token válido, adulterado, de outra chave, expirado, e segredo curto impedindo a inicialização.
- Em `AuthApiTest`, `ValidacaoTest` e `ErrosApiTest`: senha previsível recusada, e cabeçalhos de segurança presentes.

**Alterados:** `TesteApi` (limite de cadastros alto para os testes, que criam muitas contas do mesmo IP), `TestSupport` (senha de teste), `ErrosApiTest` (rota inexistente: 401 sem token e 404 com token) e `e2e.test.mjs` (senha).

## 9. Riscos residuais e próximos passos

- Token em `localStorage` e sem revogação (V4): migrar para cookie `HttpOnly` + `Secure` + `SameSite`, ou token curto com refresh.
- E-mail já cadastrado (V6): confirmar o e-mail por mensagem antes de ativar a conta.
- Sem recuperação de senha, verificação de e-mail e exclusão de conta (RNF16).
- Limite de tentativas em memória: com várias instâncias, mover o contador para um armazenamento compartilhado, como Redis.
- Dependências: rodar `npm audit` e o OWASP Dependency-Check, e planejar a migração para o Spring Boot 4.x.
- Publicação: seguir o checklist do `docs/06-seguranca.md` (HTTPS, PostgreSQL, `APP_SECRET`, cabeçalhos do frontend, backup).
- Fazer um teste dinâmico (por exemplo, com OWASP ZAP) depois que o sistema estiver publicado em um ambiente de teste.

## 10. Arquivos alterados e novos

Os caminhos são relativos à raiz do repositório (a pasta que contém `README.md` e a pasta `cade-meu-dinheiro`).

| Arquivo | Tipo | Motivo |
| --- | --- | --- |
| `.gitignore` | Novo | Ignorar `.idea/`, banco local e segredos (V7) |
| `README.md` | Alterado | Perfil dev, novas variáveis, Spring Boot 3.5 |
| `cade-meu-dinheiro/.gitignore` | Alterado | Ignorar `data/`, `target/`, `.env`; manter lockfiles (V7, V10) |
| `cade-meu-dinheiro/package.json` | Alterado | `npm run dev` usa o perfil dev (V1) |
| `cade-meu-dinheiro/e2e/e2e.test.mjs` | Alterado | Senha de teste que passa na nova regra (V9) |
| `cade-meu-dinheiro/docs/06-seguranca.md` | Novo | Controles, checklist de publicação, riscos (todos) |
| `cade-meu-dinheiro/docs/01-requisitos.md` | Alterado | RN06 atualizada (V9) |
| `cade-meu-dinheiro/docs/README.md` | Alterado | Índice com o novo documento |
| `cade-meu-dinheiro/backend/README.md` | Alterado | Perfil dev, 429, rotas protegidas |
| `cade-meu-dinheiro/backend/pom.xml` | Alterado | Spring Boot 3.5.16 (V10) |
| `.../resources/application.properties` | Alterado | Padrões seguros, novas variáveis, sem DEBUG (V1, V8) |
| `.../resources/application-dev.properties` | Novo | Usuário de teste só no perfil dev (V1) |
| `.../security/LimiteTentativas.java` | Novo | Limite de tentativas (V2, V6) |
| `.../security/CabecalhosSeguranca.java` | Novo | Cabeçalhos de segurança (V5, V11) |
| `.../security/Tokens.java` | Alterado | Validade configurável, segredo mínimo (V4) |
| `.../security/AuthInterceptor.java` | Alterado | Libera OPTIONS (CORS) |
| `.../config/WebConfig.java` | Alterado | Rotas protegidas por padrão e CORS opcional (V3, V5) |
| `.../controller/AuthController.java` | Alterado | Repassa o IP ao serviço (V2) |
| `.../service/AuthService.java` | Alterado | Limite, hash falso, senha forte (V2, V6, V9) |
| `.../validation/Validacao.java` | Alterado | Método `senhaForte` (V9) |
| `.../test/.../TesteApi.java` | Alterado | Limite de cadastros alto nos testes |
| `.../test/.../controller/TestSupport.java` | Alterado | Nova senha de teste |
| `.../test/.../controller/AuthApiTest.java` | Alterado | Teste de senha previsível |
| `.../test/.../controller/ErrosApiTest.java` | Alterado | Rota inexistente e cabeçalhos |
| `.../test/.../controller/LimiteApiTest.java` | Novo | Testes do limite de tentativas |
| `.../test/.../security/TokensTest.java` | Novo | Testes do token |
| `.../test/.../validation/ValidacaoTest.java` | Alterado | Teste de senha previsível |

Na tabela, `...` abrevia `cade-meu-dinheiro/backend/src/main` (arquivos de código e `resources`) ou `cade-meu-dinheiro/backend/src/test/java/br/cademeudinheiro` (arquivos de teste).

## 11. Conclusão

O MVP já tinha uma base sólida: consultas parametrizadas, isolamento entre usuários, BCrypt e token assinado. Os problemas encontrados estavam principalmente em configuração e em práticas de publicação. As correções fecham os riscos de maior impacto (conta de teste pública, força bruta e rotas abertas por padrão), reforçam a configuração e deixam documentados os riscos que continuam abertos.

## Referências

- OWASP Top 10:2021, https://owasp.org/Top10/
- OWASP Cheat Sheet Series: Authentication, Password Storage e Session Management, https://cheatsheetseries.owasp.org/
- Documentação do Spring Boot, ciclo de suporte das versões, https://spring.io/projects/spring-boot#support
- Documentos do próprio projeto: `docs/01-requisitos.md` (RN01, RN06), `docs/02-requisitos-nao-funcionais.md` (RNF09 a RNF13, RNF16) e `docs/06-seguranca.md`
