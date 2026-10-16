# ADR-004: Registro manual de gastos, sem integração bancária – Aprovado

## Contexto
O diferencial do produto é a simplicidade. Integrar com bancos exigiria Open Finance, certificações, tratamento de dados sensíveis e conflita com a proposta de privacidade.

## Decisão
Todos os gastos são informados manualmente pelo usuário. O sistema não acessa contas, cartões ou extratos.

## Consequências (trade-offs)
**Vantagens**
- Menor complexidade técnica e jurídica.
- Mais privacidade e confiança.
- MVP entregue mais rápido.
- Cadastro sem senha de banco.

**Desvantagens**
- Depende da disciplina do usuário para anotar.
- Os dados podem ficar incompletos ou imprecisos.
- Sem importação automática, o app perde um atrativo comum nos concorrentes.
