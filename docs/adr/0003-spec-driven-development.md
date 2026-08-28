# ADR 0003 — Adotar Spec Driven Development

- **Status:** Aceita
- **Data:** 2026-08-28
- **Decisores:** Gabriel Fagundes, Hugo Pontello, João Gherardi

## Contexto

O [ADR 0002](0002-ddd-no-backend.md) define como o código é estruturado, mas não como a equipe
decide o que construir. As regras do domínio não são óbvias e envolvem casos de borda —
concorrência na agenda, restrições temporais, efeito da indisponibilidade de um recurso sobre
agendamentos existentes — que costumam aparecer só durante a implementação, quando corrigir custa
mais.

Com três pessoas trabalhando em todas as partes do sistema, o entendimento do comportamento
esperado precisa estar escrito. Combinação feita por chat não fica disponível para quem revisa o
PR nem para quem mexe no mesmo trecho semanas depois.

## Decisão

Adotar Spec Driven Development: cada funcionalidade começa por uma especificação escrita e revisada
antes da implementação.

A especificação define o comportamento esperado, as regras aplicáveis e os critérios de aceitação. O
código é escrito para satisfazê-la, e a revisão do PR verifica o código contra ela.

- As especificações ficam em `docs/spec/`, uma por funcionalidade, seguindo o modelo em
  `docs/spec/template/`.
- Cada especificação referencia os ADRs que a restringem.
- O processo — nomeação de branch, revisão e integração — está em
  [`docs/spec/README.md`](../spec/README.md).

Especificação e ADR têm finalidades distintas e não se substituem: o ADR registra uma decisão
estrutural e não é editado depois de aceito; a especificação descreve o comportamento de uma
funcionalidade e acompanha as mudanças dela.

## Consequências

- O comportamento é discutido antes da implementação, quando alterá-lo custa pouco.
- A revisão de PR passa a ter uma referência objetiva, em vez de depender do julgamento de quem
  revisa.
- Os critérios de aceitação da especificação servem de base para os testes.
- Há um passo a mais por funcionalidade. Em mudanças pequenas, o custo da especificação supera o
  retorno, e nesses casos ela é dispensada.
- Especificação desatualizada em relação ao código induz a erro. Toda mudança de comportamento
  atualiza a especificação no mesmo PR.
- Nenhum integrante da equipe trabalhou assim antes; as primeiras especificações provavelmente serão
  imprecisas.
