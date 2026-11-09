# 6. Segurança

Documento de apoio ao relatório de segurança do MVP (Univille). Resume os controles que existem no código, como configurá-los e o que fazer antes de publicar o sistema.

## 6.1 Controles implementados

| Controle | Onde | Observação |
|---|---|---|
| Senhas com BCrypt (RNF10) | `SegurancaConfig`, `AuthService` | O login também gasta o tempo do BCrypt quando o e-mail não existe (evita descobrir quem tem conta pelo tempo de resposta). |
| Senha não pode ser previsível (RN06) | `Validacao.senhaForte` | Recusa senhas comuns, um único caractere repetido e senha igual ao e-mail. A lista é curta; para produção, use uma lista maior. |
| Token assinado com HMAC-SHA256 | `Tokens` | Validade padrão de 7 dias (RNF12), configurável por `SESSAO_HORAS`. `APP_SECRET` precisa ter 32 caracteres ou mais. |
| Todas as rotas `/api/**` protegidas | `WebConfig` | Exceções explícitas: `/api/auth/**` e `/api/docs`. Rota nova nasce protegida. |
| Limite de tentativas | `LimiteTentativas` | Bloqueio temporário (HTTP 429) após falhas de login por e-mail ou por IP, e limite de cadastros por IP. Em memória: vale para uma instância do backend. |
| Isolamento entre usuários (RN01, RNF11) | `GastoRepository` | Toda consulta, edição e exclusão filtra por `usuario_id`. |
| Consultas parametrizadas | repositórios | Nenhuma consulta concatena texto vindo do usuário. |
| Cabeçalhos de segurança | `CabecalhosSeguranca` | `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`, CSP restritiva da API, `Cache-Control: no-store` e HSTS (somente quando a requisição chega por HTTPS). |
| CORS desligado por padrão | `WebConfig` | Só é ligado se `CORS_ORIGENS` listar os domínios do frontend. Nunca use `*`. |
| Usuário de teste só no perfil `dev` | `DadosIniciais`, `application-dev.properties` | Fora do perfil `dev` nenhuma conta com senha conhecida é criada. |

## 6.2 Perfis e variáveis de ambiente

- **Desenvolvimento:** `npm run dev` já ativa o perfil `dev`. No IntelliJ, defina *Active profiles* = `dev` na configuração de execução.
- **Produção:** não use o perfil `dev`. Defina `APP_SECRET`, o banco PostgreSQL (`SPRING_DATASOURCE_*`) e, se o frontend ficar em outro domínio, `CORS_ORIGENS`.
- Atrás de um proxy reverso (Nginx, load balancer), o backend enxerga o IP do proxy. Defina `SERVER_FORWARD_HEADERS_STRATEGY=native` para ele usar o IP real do cliente (e saber que a conexão original é HTTPS). Só faça isso se o backend **não** estiver exposto diretamente à internet, senão o cabeçalho `X-Forwarded-For` pode ser forjado.

## 6.3 Checklist antes de publicar

1. HTTPS em toda a comunicação (RNF09), com redirecionamento de HTTP para HTTPS.
2. `APP_SECRET` definido com 32 caracteres ou mais, guardado fora do repositório.
3. PostgreSQL com usuário e senha próprios (o H2 em arquivo é só para desenvolvimento) e backup diário (RNF13).
4. Perfil `dev` desligado e `DADOS_TESTE` ausente ou `false`.
5. Cabeçalhos do frontend configurados no servidor que entrega os arquivos estáticos (exemplo abaixo).
6. `npm audit` e o OWASP Dependency-Check sem vulnerabilidades altas ou críticas.

Exemplo de cabeçalhos para o servidor do frontend (Nginx). Teste no navegador: se algum recurso for bloqueado, ajuste a política.

```
add_header Content-Security-Policy "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; connect-src 'self'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-Frame-Options "DENY" always;
add_header Referrer-Policy "no-referrer" always;
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
```

## 6.4 Verificação de dependências

```
# backend (precisa de internet na primeira execução)
mvn -f backend/pom.xml org.owasp:dependency-check-maven:check

# frontend
npm audit --prefix frontend
npm install --prefix frontend vite@latest @vitejs/plugin-react@latest   # depois rode os testes
```

## 6.5 Riscos conhecidos e trabalho futuro

- **Token no `localStorage`:** um XSS futuro poderia roubá-lo. Hoje o React escapa o conteúdo e não há `dangerouslySetInnerHTML`. Melhoria futura: cookie `HttpOnly` + `Secure` + `SameSite` (exige tratar CSRF) ou token de curta duração com refresh.
- **Sem revogação de sessão:** o token é sem estado; não há logout no servidor nem invalidação antes de expirar.
- **E-mail já cadastrado (409):** o cadastro informa que o e-mail existe. O limite por IP reduz o abuso; a solução completa é confirmar o e-mail por mensagem.
- **Sem recuperação de senha, verificação de e-mail e exclusão de conta (RNF16).**
- **Limite de tentativas em memória:** com várias instâncias do backend, mover o contador para um armazenamento compartilhado (ex.: Redis).
- **Spring Boot 3.5** terminou o suporte gratuito em junho de 2026. Planejar a migração para a linha 4.x.
