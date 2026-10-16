# ADR-002: Banco de dados relacional, com H2 no desenvolvimento e PostgreSQL em produção – Aprovado

## Contexto
Os dados são estruturados e ligados entre si (usuário, categoria e gasto) e o produto depende de somas e agrupamentos por mês e categoria. Valores em dinheiro exigem precisão exata.

## Decisão
Usar um banco relacional acessado via JDBC, com o tipo `NUMERIC(12,2)` para valores monetários: H2 em arquivo (modo PostgreSQL) por padrão, para o MVP rodar sem instalar nada, e PostgreSQL por configuração (variáveis `SPRING_DATASOURCE_*`). O mesmo `schema.sql` serve para os dois.

## Consequências (trade-offs)
**Vantagens**
- Consultas de soma e agrupamento simples e eficientes.
- Integridade garantida por chaves estrangeiras e restrições (`CHECK valor > 0`).
- O mesmo esquema funciona em H2 e PostgreSQL, o que permite trocar de banco sem alterar o código.
- Amplamente conhecido.

**Desvantagens**
- O esquema é mais rígido e exige migrações a cada mudança.
- A escala horizontal é mais trabalhosa, o que não é problema para o volume do MVP.
- O H2 em arquivo não é indicado para produção.
- A regra de data não futura (RN04) é validada na API, porque o H2 não aceita essa restrição no banco.
