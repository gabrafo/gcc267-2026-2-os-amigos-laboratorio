# ADR 0000 — Registrar decisões de arquitetura como ADRs

- **Status:** Aceita
- **Data:** 2026-08-27
- **Decisores:** Gabriel Fagundes, Hugo Pontello, João Gherardi

## Contexto

O projeto dura um semestre e passa por decisões estruturais tomadas em momentos diferentes e por
pessoas diferentes. O código registra o que foi decidido, mas não o motivo nem as opções
descartadas. Sem esse registro, a mesma discussão é refeita mais adiante.

## Decisão

Toda decisão de arquitetura significativa é registrada como um ADR em `docs/adr/`, no formato
proposto por Michael Nygard.

**Critério.** É significativa a decisão cara de reverter ou que restringe decisões futuras:
linguagem, framework, banco de dados, divisão em contextos, forma de comunicação entre serviços,
estratégia de consistência. Não se registram escolhas de nomenclatura, formatação (verificada pelo
lint) ou bibliotecas de fácil substituição.

**Formato.** Arquivo `docs/adr/NNNN-titulo-em-kebab-case.md`, com numeração sequencial e as seções
Contexto, Decisão e Consequências. O modelo está em
[`docs/adr/template/`](template/template.md).

**Ciclo.** Quem propõe abre um PR com o ADR em status Proposta. A discussão ocorre na revisão do PR.
Com o aval da equipe, o status passa a Aceita. ADR aceito não é editado: uma mudança de decisão gera
um ADR novo, e o anterior passa a Substituída por ADR NNNN.

## Consequências

- O motivo de cada decisão fica versionado junto ao código.
- Escrever o contexto obriga a explicitar restrições que ficariam implícitas.
- Cada ADR custa entre 20 e 40 minutos de escrita.
- Um critério aplicado de forma frouxa gera registros de baixo valor, que deixam de ser consultados.

## Referência

Michael Nygard, *Documenting Architecture Decisions* (2011) —
<https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions>
