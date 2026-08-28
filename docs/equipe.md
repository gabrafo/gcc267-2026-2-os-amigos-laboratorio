# Equipe — Os Amigos do Laboratório

Projeto Integrador I (GCC267) — Sistemas de Informação, UFLA — 2026/2.

## Integrantes

| Nome | GitHub | Papel |
| --- | --- | --- |
| Gabriel Fagundes Mesquita Sousa | [@gabrafo](https://github.com/gabrafo) | Desenvolvimento full stack · responsável pelo repositório e pela CI |
| Hugo Dias Pontello | [@DPontello](https://github.com/DPontello) | Desenvolvimento full stack |
| João Amâncio Gherardi | [@gherardijoao](https://github.com/gherardijoao) | Desenvolvimento full stack |

## Papéis

A equipe não adota separação fixa entre front-end, back-end e infraestrutura. Todos os integrantes
trabalham em todas as partes do sistema: interface, API, modelagem de domínio, CI e documentação.

Os motivos são dois. Primeiro, o objetivo da disciplina é passar por todas as etapas da construção
de um sistema distribuído. Segundo, com três pessoas, a especialização criaria dependência de um
único integrante para cada parte do sistema.

Há uma responsabilidade rotativa por entrega: a cada desafio, um integrante assume a função de
integrador, acompanhando as tarefas em aberto, verificando o estado da CI antes do prazo e
publicando a entrega. O rodízio segue a ordem da tabela acima.

## Organização

### Comunicação

- **WhatsApp** — combinações rápidas e avisos de impedimento.
- **Discord** — sessões de trabalho conjunto, usadas em tarefas de modelagem.
- **Issues do GitHub** — decisões que afetam o código. O que for combinado por chat e alterar o
  sistema deve ser registrado em issue ou em ADR.

### Ritmo

- **Reunião semanal** de 30 minutos, remota: o que foi feito, o que vem a seguir e o que está
  impedido.
- **Ciclo por desafio.** Cada entrega da disciplina é a unidade de planejamento. As issues são
  criadas no início do ciclo e fechadas até o prazo.
- **Congelamento** nas 24 horas anteriores à entrega: apenas correções.

### Fluxo no Git

- `main` é protegida e recebe código apenas via Pull Request com CI verde.
- Uma branch por tarefa, nomeada pelo desafio ou pelo assunto: `desafio-01`, `feat/reserva-conflito`,
  `fix/lint-frontend`.
- Commits em inglês, seguindo [Conventional Commits](https://www.conventionalcommits.org):
  `feat: add schedule conflict validation`, `fix: reject reservation with inverted period`,
  `docs: add ADR on messaging broker`. Commits pequenos, com uma intenção cada.
- Pull Requests descrevem o que muda e por quê, com link para a issue, e exigem uma aprovação de
  outro integrante antes do merge.
- Merge por *squash*.

### Qualidade

- A CI (build e lint do front-end e do back-end) roda a cada push e a cada Pull Request. PR com CI
  vermelha não é revisado.
- Decisões de arquitetura são registradas em [docs/adr/](adr/), conforme o
  [ADR 0000](adr/0000-registro-de-decisoes.md).

### Divergências

A discussão técnica ocorre na issue ou no PR. Sem consenso, a equipe decide por maioria simples e
registra o resultado, incluindo a alternativa descartada, em um ADR. Decisão registrada só é
reaberta diante de fato novo.
