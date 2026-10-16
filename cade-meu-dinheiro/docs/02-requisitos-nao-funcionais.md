# 2. Requisitos não funcionais (RNF)

Os valores abaixo são metas propostas pela equipe e devem ser validadas durante os testes.

| ID | Categoria | Requisito mensurável |
|---|---|---|
| RNF01 | Usabilidade | Registrar um gasto deve exigir no máximo 3 toques (valor, categoria, salvar) e ser concluído em menos de 10 segundos por um usuário já ambientado. |
| RNF02 | Usabilidade | Em teste com 5 pessoas que nunca usaram o app, pelo menos 4 devem registrar o primeiro gasto sem ajuda. |
| RNF03 | Usabilidade | A interface deve funcionar em telas de 360 px de largura ou mais, sem rolagem horizontal. |
| RNF04 | Acessibilidade | Texto e controles devem ter contraste mínimo de 4,5:1 (WCAG 2.1 AA) e alvos de toque de pelo menos 44 × 44 px. |
| RNF05 | Desempenho | O carregamento inicial (LCP) deve ser menor que 2,5 s em conexão 4G. |
| RNF06 | Desempenho | O tempo de resposta do endpoint de salvar gasto deve ser menor que 500 ms no percentil 95, com 100 usuários simultâneos. |
| RNF07 | Desempenho | O resumo mensal deve carregar em menos de 1 s para um usuário com até 1.000 gastos no mês. |
| RNF08 | Disponibilidade | O sistema deve ter disponibilidade mensal de pelo menos 99%. |
| RNF09 | Segurança | Toda comunicação deve usar HTTPS com TLS 1.2 ou superior. |
| RNF10 | Segurança | Senhas devem ser armazenadas apenas como hash com sal (bcrypt ou Argon2); nunca em texto puro. |
| RNF11 | Segurança | Cada usuário só acessa os próprios dados, garantido pela API, que filtra toda consulta pelo usuário autenticado, com testes automatizados que comprovem a negativa de acesso. |
| RNF12 | Segurança | A sessão deve expirar 7 dias após o login. |
| RNF13 | Confiabilidade | Deve haver backup diário do banco, com retenção de 7 dias. |
| RNF14 | Compatibilidade | O app deve funcionar nas duas últimas versões do Chrome, Safari e Firefox, em Android e iOS. |
| RNF15 | Manutenibilidade | As regras de negócio RN01 a RN08 devem ter cobertura de testes automatizados de pelo menos 70%. |
| RNF16 | Privacidade | O usuário deve poder excluir sua conta e todos os seus gastos, e a exclusão deve ser concluída em até 30 dias, conforme a LGPD. |
