# ADR-003: Backend em Java com Spring Boot, expondo uma API REST – Aprovado

## Contexto
O projeto exige uma camada de backend, separada do frontend, que implemente as regras de negócio, as validações e o acesso ao banco, em uma linguagem que a equipe domina (Java). O orçamento do MVP é próximo de zero.

## Decisão
Usar Java 17 com Spring Boot 3, JDBC e Maven, expondo uma API REST em JSON (porta 8080). A autenticação usa token assinado (HMAC-SHA256) com validade de 7 dias, e as senhas são armazenadas com hash BCrypt. A hospedagem em nuvem fica como etapa posterior e não faz parte do MVP.

## Consequências (trade-offs)
**Vantagens**
- Controle total das regras de negócio e das validações.
- Isolamento dos dados de cada usuário garantido na própria API (RN01).
- Testes automatizados com JUnit e MockMvc.
- Sem dependência de fornecedor e custo zero.

**Desvantagens**
- Mais código para escrever e manter do que com um backend gerenciado.
- É preciso instalar JDK e Maven.
- Autenticação, HTTPS e backups ficam sob responsabilidade da equipe.
