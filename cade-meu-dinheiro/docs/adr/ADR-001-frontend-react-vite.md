# ADR-001: Frontend em React com Vite (JavaScript), consumindo a API do backend – Aprovado

## Contexto
O público usa o celular para anotar gastos na hora em que acontecem. É preciso uma interface mobile-first, rápida de construir por uma equipe pequena e sem a burocracia de publicar em lojas de aplicativos.

## Decisão
Usar React 18 com Vite, em JavaScript, como aplicativo web responsivo (mobile-first). O frontend é um projeto separado do backend e chama a API REST por meio de um proxy configurado no Vite (`/api` para `http://localhost:8080`) durante o desenvolvimento.

## Consequências (trade-offs)
**Vantagens**
- Um único código atende Android, iOS e desktop.
- Ecossistema grande e boa documentação.
- Atualizações sem passar por loja.
- O Vite dá recarga rápida no desenvolvimento.

**Desvantagens**
- Acesso limitado a recursos nativos, como notificações no iOS.
- O desempenho é inferior ao de um app nativo.
- A equipe precisa conhecer React; e usar JavaScript em vez de TypeScript abre mão da checagem de tipos.
