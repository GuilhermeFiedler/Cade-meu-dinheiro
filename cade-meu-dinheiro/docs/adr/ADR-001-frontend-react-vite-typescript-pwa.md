# ADR-001: Frontend em React com Vite e TypeScript, entregue como PWA – Substituído pelo ADR-005

> **Status: Substituído.** Esta decisão não está mais em uso. Veja o [ADR-005](ADR-005-frontend-react-vite-javascript.md).

## Contexto
O público usa o celular para anotar gastos na hora em que acontecem. É preciso uma interface mobile-first, rápida de construir por uma equipe pequena e sem a burocracia de publicar em lojas de aplicativos.

## Decisão
Usar React com Vite e TypeScript, entregue como aplicativo web responsivo com suporte a PWA, para ser instalado na tela inicial do celular.

## Consequências (trade-offs)
**Vantagens**
- Um único código atende Android, iOS e desktop.
- Ecossistema grande e boa documentação.
- Atualizações sem passar por loja.
- O Vite dá recarga rápida no desenvolvimento.

**Desvantagens**
- Acesso limitado a recursos nativos, como notificações no iOS.
- O desempenho é inferior ao de um app nativo.
- A equipe precisa conhecer React e TypeScript.

## Motivo da substituição
Na implementação, o frontend foi feito em JavaScript e sem o recurso de PWA. Veja o ADR-005.
