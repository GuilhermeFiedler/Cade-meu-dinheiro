# 1. Requisitos funcionais, histórias de usuário e regras de negócio

## 1.1 Requisitos funcionais (RF)

| ID | Requisito | História |
|---|---|---|
| RF01 | O sistema deve permitir criar conta com nome, e-mail e senha. | US01 |
| RF02 | O sistema deve autenticar o usuário por e-mail e senha e encerrar a sessão sob demanda. | US01 |
| RF03 | O sistema deve permitir registrar um gasto informando valor, categoria, data e descrição opcional. | US02 |
| RF04 | O sistema deve oferecer categorias predefinidas para classificar os gastos. | US02 |
| RF05 | O sistema deve listar os gastos do mês selecionado, do mais recente para o mais antigo. | US03 |
| RF06 | O sistema deve permitir editar qualquer campo de um gasto já registrado. | US04 |
| RF07 | O sistema deve permitir excluir um gasto, mediante confirmação. | US04 |
| RF08 | O sistema deve calcular e exibir o total gasto no mês selecionado. | US06 |
| RF09 | O sistema deve exibir o total por categoria no mês, ordenado do maior para o menor. | US05 |
| RF10 | O sistema deve permitir ao usuário trocar o mês exibido no histórico e no resumo. | US03, US05 |

## 1.2 Histórias de usuário e critérios de aceite

### US01 – Criar conta e entrar
Como usuário, quero criar uma conta e entrar no app para que meus gastos fiquem guardados e só eu os veja.

- **CA01.1** Dado que não tenho conta, quando informo nome, e-mail válido e senha com pelo menos 8 caracteres, então a conta é criada e eu entro no app.
- **CA01.2** Dado que informo um e-mail já cadastrado, quando tento criar a conta, então vejo a mensagem "Este e-mail já está em uso" e nenhuma conta é criada.
- **CA01.3** Dado que tenho conta, quando informo e-mail e senha corretos, então entro no app na tela Novo gasto.
- **CA01.4** Dado que tenho conta, quando informo uma senha incorreta, então vejo "E-mail ou senha incorretos" sem revelar qual dos dois está errado.

### US02 – Registrar um gasto rapidamente
Como usuário, quero registrar um gasto em poucos toques para não esquecer de anotar.

- **CA02.1** Dado que estou na tela Novo gasto, quando informo um valor maior que zero, escolho uma categoria e toco em Salvar, então o gasto é gravado com a data de hoje e vejo a confirmação "Gasto salvo".
- **CA02.2** Dado que não informei valor ou ele é zero, quando toco em Salvar, então vejo "Informe um valor maior que zero" e nada é gravado.
- **CA02.3** Dado que não escolhi categoria, quando toco em Salvar, então vejo "Escolha uma categoria" e nada é gravado.
- **CA02.4** Dado que escolhi uma data anterior a hoje, quando salvo, então o gasto é gravado com essa data.

### US03 – Ver meus gastos do mês
Como usuário, quero ver a lista dos meus gastos para conferir o que já anotei.

- **CA03.1** Dado que tenho gastos no mês, quando abro o Histórico, então vejo descrição, categoria, data e valor de cada um, do mais recente para o mais antigo.
- **CA03.2** Dado que não tenho gastos no mês, quando abro o Histórico, então vejo "Nenhum gasto neste mês" e um caminho para registrar o primeiro.
- **CA03.3** Dado que troco o mês, quando a lista é carregada, então ela mostra somente os gastos daquele mês.

### US04 – Corrigir ou apagar um gasto
Como usuário, quero editar ou excluir um gasto para corrigir erros de digitação.

- **CA04.1** Dado que toco em Editar num gasto, quando altero o valor e salvo, então o histórico, o total e o resumo refletem o novo valor.
- **CA04.2** Dado que toco em Excluir, quando confirmo, então o gasto some do histórico e dos totais.
- **CA04.3** Dado que toco em Excluir, quando cancelo a confirmação, então o gasto permanece.
- **CA04.4** Dado que existe um gasto de outro usuário, quando tento acessá-lo ou alterá-lo, então o sistema nega o acesso.

### US05 – Descobrir para onde o dinheiro vai
Como usuário, quero ver quanto gastei em cada categoria para entender onde o dinheiro está indo.

- **CA05.1** Dado que tenho gastos no mês, quando abro o Resumo, então vejo cada categoria com seu total e uma barra proporcional, da maior para a menor.
- **CA05.2** Dado que uma categoria não tem gastos no mês, quando abro o Resumo, então ela não aparece.
- **CA05.3** Dado que edito ou excluo um gasto, quando volto ao Resumo, então os valores estão atualizados.

### US06 – Saber quanto gastei no mês
Como usuário, quero ver o total do mês para saber o tamanho do meu gasto.

- **CA06.1** Dado que tenho gastos no mês, quando abro o Resumo, então vejo a soma de todos eles.
- **CA06.2** Dado que não tenho gastos no mês, quando abro o Resumo, então o total é R$ 0,00.
- **CA06.3** Dado que a soma por categoria é calculada, então ela é igual ao total do mês.

## 1.3 Regras de negócio (RN)

| ID | Regra |
|---|---|
| RN01 | Todo gasto pertence a exatamente um usuário e só esse usuário pode vê-lo, editá-lo ou excluí-lo. |
| RN02 | O valor de um gasto deve ser maior que zero, com no máximo duas casas decimais e limite de R$ 9.999.999,99. |
| RN03 | Todo gasto deve ter uma categoria; no MVP as categorias são fixas: Alimentação, Transporte, Lazer, Moradia, Saúde e Outros. |
| RN04 | A data do gasto não pode ser posterior à data atual; se não for informada, assume o dia de hoje. |
| RN05 | A descrição é opcional e tem no máximo 100 caracteres. |
| RN06 | O e-mail é único no sistema e a senha deve ter no mínimo 8 caracteres. |
| RN07 | Os totais do mês consideram a data do gasto, e não a data em que ele foi registrado. |
| RN08 | A exclusão de um gasto é definitiva e exige confirmação do usuário. |
| RN09 | O sistema não se conecta a bancos nem a instituições financeiras; todos os dados são informados manualmente. |
| RN10 | Todos os valores são exibidos em reais (R$), no formato brasileiro (1.234,56). |
