# ADR-002: PostgreSQL como banco de dados relacional – Substituído pelo ADR-006

> **Status: Substituído.** Esta decisão não está mais em uso como foi escrita. Veja o [ADR-006](ADR-006-banco-h2-postgresql.md).

## Contexto
Os dados são estruturados e ligados entre si (usuário, categoria e gasto) e o produto depende de somas e agrupamentos por mês e categoria. Valores em dinheiro exigem precisão exata.

## Decisão
Usar PostgreSQL, com o tipo `NUMERIC(12,2)` para valores monetários.

## Consequências (trade-offs)
**Vantagens**
- Consultas de soma e agrupamento simples e eficientes.
- Integridade garantida por chaves estrangeiras e restrições (`CHECK valor > 0`).
- Políticas de segurança por linha (RLS) para isolar os dados de cada usuário.
- Amplamente conhecido.

**Desvantagens**
- O esquema é mais rígido e exige migrações a cada mudança.
- A escala horizontal é mais trabalhosa, o que não é problema para o volume do MVP.

## Motivo da substituição
O MVP passou a rodar sem instalar um servidor de banco (H2 por padrão), e o isolamento dos dados por usuário passou a ser feito na API, não por RLS. O PostgreSQL continua suportado por configuração. Veja o ADR-006.
