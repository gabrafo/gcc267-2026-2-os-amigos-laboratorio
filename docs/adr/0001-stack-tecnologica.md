# ADR 0001 — Adotar TypeScript com React no front-end e Java com Spring Boot no back-end

- **Status:** Aceita
- **Data:** 2026-08-27
- **Decisores:** Gabriel Fagundes, Hugo Pontello, João Gherardi

## Contexto

O sistema tem uma interface web com bastante estado no lado do cliente e um back-end que deve
evoluir de aplicação única para três serviços.

Restrições: prazo de um semestre, três pessoas, todas atuando em todas as partes do sistema. O foco
da disciplina é a arquitetura distribuída, e não a linguagem de implementação.

## Decisão

**Front-end:** TypeScript e React, com Vite para build e servidor de desenvolvimento, e oxlint como
linter.

**Back-end:** Java 21 e Spring Boot 4, com Maven e o *wrapper* versionado no repositório, para que a
CI e as máquinas da equipe usem a mesma versão.

O critério principal foi a experiência prévia da equipe com Java, Spring e React, que reduz o tempo
de aprendizado no início do projeto. Além disso:

- Tipagem estática nas duas pontas, com TypeScript em modo `strict`, reduz o custo de refatoração em
  um código tocado por três pessoas.
- Java 21 oferece `record`, *sealed types* e *pattern matching*, úteis na modelagem do domínio.
- Spring Boot já inclui o que será necessário quando os contextos se separarem: APIs REST,
  validação, cliente HTTP e integração com broker de mensagens.

Cada contexto começa como módulo da mesma aplicação e será extraído para serviço próprio quando
houver necessidade. Este ADR define linguagem e framework, não o número de processos em execução.

## Consequências

- A equipe começa a implementar sem período prévio de estudo de linguagem.
- São dois ecossistemas de dependências para manter atualizados.
- Os DTOs existem em duplicata, em Java e em TypeScript, sem verificação automática de equivalência.
  Enquanto forem poucos, a manutenção é manual.
- Várias aplicações Spring Boot em execução local consomem mais memória do que o equivalente em
  Node.

Banco de dados e broker de mensagens ainda não foram escolhidos.
