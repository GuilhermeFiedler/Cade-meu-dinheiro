# ADR-003: Supabase como backend gerenciado, com frontend hospedado na Vercel – Substituído pelo ADR-007

> **Status: Substituído.** Esta decisão não está mais em uso. Veja o [ADR-007](ADR-007-backend-java-spring-boot.md).

## Contexto
A equipe é pequena, tem prazo curto e precisa de autenticação, banco e API sem montar e operar servidores próprios. O orçamento do MVP é próximo de zero.

## Decisão
Usar o Supabase (PostgreSQL gerenciado, autenticação e API automática) como backend, e a Vercel para hospedar o frontend, ambos em planos gratuitos no MVP.

## Consequências (trade-offs)
**Vantagens**
- Autenticação, banco e API prontos.
- HTTPS e backups incluídos.
- Deploy automático a cada push no repositório.
- Custo inicial zero.

**Desvantagens**
- Dependência de fornecedores externos (vendor lock-in), embora o banco seja PostgreSQL padrão e possa ser migrado.
- Limites dos planos gratuitos, como pausa de projetos inativos.
- Menos controle sobre a infraestrutura.

## Motivo da substituição
A atividade exige um backend próprio, com API, regras de negócio e acesso ao banco desenvolvidos pela equipe, e a equipe optou por Java. Supabase e Vercel não são usados. Veja o ADR-007.
