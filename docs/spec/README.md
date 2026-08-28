# Especificações

Este diretório contém as especificações de funcionalidade do projeto. A prática está registrada no
[ADR 0003](../adr/0003-spec-driven-development.md).

Cada funcionalidade é descrita em uma especificação antes de ser implementada. A especificação
define o comportamento esperado e os critérios de aceitação; a implementação é escrita para
satisfazê-la, e a revisão verifica uma contra a outra.

## Processo

### 1. Uma feature-branch por especificação

Cada especificação tem a própria branch, criada a partir de `development`:

```bash
git switch development
git pull
git switch -c feat/conflito-de-agenda
```

O nome da branch identifica a funcionalidade, não o número da especificação. Uma branch trata de
uma especificação apenas.

### 2. Escreva a especificação e vincule os ADRs

Copie `template/template.md` para `docs/spec/NNNN-titulo-em-kebab-case.md`, usando o próximo número
da sequência, e preencha as seções do modelo.

A seção **Decisões relacionadas** lista os ADRs que restringem a funcionalidade, com link. Se a
implementação exigir uma decisão estrutural que ainda não existe — banco de dados, protocolo entre
serviços, estratégia de consistência —, essa decisão vira um ADR antes da especificação ser
aprovada, e não uma escolha tomada durante a implementação.

Se nenhum ADR se aplica, escreva que não há, em vez de deixar a seção vazia.

### 3. Siga o modelo

Todas as especificações usam as mesmas seções, na mesma ordem, conforme `template/template.md`.
Seção que não se aplica é removida com uma justificativa de uma linha, não deixada em branco.

Os critérios de aceitação são verificáveis: cada um descreve uma entrada e o resultado esperado, de
forma que a revisão possa confirmar se foi atendido.

### 4. Pull Request para `development`

Abra o PR da feature-branch para `development`, com CI verde e uma aprovação de outro integrante.

A especificação pode ser enviada em um PR próprio, antes da implementação, quando o comportamento
ainda estiver em discussão. É o caminho preferível para funcionalidades com regra de negócio
relevante: separa a discussão sobre o que construir da revisão de como foi construído.

Quando especificação e implementação vão no mesmo PR, a descrição aponta a especificação e a
revisão confirma que o código atende os critérios de aceitação.

## Estados

| Estado | Significado |
| --- | --- |
| Rascunho | em escrita ou em discussão; ainda não orienta implementação |
| Aprovada | revisada e mesclada; pode ser implementada |
| Implementada | o comportamento descrito está no código e coberto por testes |
| Substituída por NNNN | uma especificação posterior descreve o comportamento atual |

Diferente do ADR, a especificação é editada enquanto o comportamento evolui. Mudança de
comportamento atualiza a especificação no mesmo PR da mudança de código.

## Estrutura

```
docs/spec/
├── README.md              este arquivo
├── template/template.md   modelo
└── NNNN-titulo.md         as especificações
```
