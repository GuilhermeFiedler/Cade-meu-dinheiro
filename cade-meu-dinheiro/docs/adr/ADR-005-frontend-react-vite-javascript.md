# ADR-005: Frontend em React com Vite (JavaScript), separado do backend – Aprovado

> Substitui o [ADR-001](ADR-001-frontend-react-vite-typescript-pwa.md).

## Contexto
O ADR-001 previa React com TypeScript e PWA. Na implementação, o frontend passou a ser um projeto separado que conversa com a API Java, e o escopo do MVP não precisa de instalação na tela inicial nem de uso offline. O público continua usando o celular, então a interface segue mobile-first.

## Decisão
Usar React 18 com Vite, em JavaScript, como aplicativo web responsivo. Durante o desenvolvimento, o Vite repassa as chamadas `/api` para o backend em `http://localhost:8080`. O visual é definido por tokens de design em CSS (veja [design-system.md](../../../../../Downloads/cade-meu-dinheiro-react-java/cade-meu-dinheiro/docs/design-system.md)), sem biblioteca de componentes.

## Consequências (trade-offs)
**Vantagens**
- Menos configuração e menos código para o MVP.
- Um único código atende Android, iOS e desktop, sem passar por lojas.
- O Vite dá recarga rápida no desenvolvimento.
- Sem dependência de biblioteca de interface: o CSS é pequeno e fácil de alterar.

**Desvantagens**
- Sem a checagem de tipos do TypeScript.
- Não instala na tela inicial nem funciona offline (sem PWA).
- Acesso limitado a recursos nativos, como notificações no iOS.
