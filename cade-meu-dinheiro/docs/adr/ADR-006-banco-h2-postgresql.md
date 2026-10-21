# ADR-006: Banco relacional com H2 por padrão e PostgreSQL por configuração – Aprovado

> Substitui o [ADR-002](ADR-002-banco-postgresql.md).

## Contexto
O ADR-002 escolheu PostgreSQL. Para que qualquer pessoa rode o MVP sem instalar um servidor de banco, e como o isolamento por usuário passou a ser feito na API (e não por RLS), é preciso um banco que funcione sem instalação, mantendo o caminho para PostgreSQL.

## Decisão
Usar um banco relacional acessado via JDBC, com `NUMERIC(12,2)` para valores monetários: H2 em arquivo (modo PostgreSQL) por padrão e PostgreSQL por configuração (variáveis `SPRING_DATASOURCE_*`). O mesmo `schema.sql` serve para os dois.

## Consequências (trade-offs)
**Vantagens**
- O MVP roda sem instalar nada.
- Consultas de soma e agrupamento simples e eficientes.
- Integridade garantida por chaves estrangeiras e restrições (`CHECK valor > 0`).
- Dá para trocar para PostgreSQL sem alterar o código.

**Desvantagens**
- O H2 em arquivo não é indicado para produção.
- O esquema é mais rígido e exige migrações a cada mudança.
- A regra de data não futura (RN04) é validada na API, porque o H2 não aceita essa restrição no banco.
