<!--
Modelo de especificação.

Para criar uma especificação nova:
1. copie este arquivo para docs/spec/NNNN-titulo-em-kebab-case.md, usando o próximo número
   da sequência;
2. preencha as seções e remova os textos de orientação em itálico;
3. abra um PR da feature-branch para development, com o status em Rascunho.

O processo está no README deste diretório.
-->

# SPEC NNNN — Nome da funcionalidade

- **Status:** Rascunho | Aprovada | Implementada | Substituída por NNNN
- **Data:** AAAA-MM-DD
- **Autores:** quem escreveu
- **Contextos afetados:** Reserva | Catálogo | Notificação

## Objetivo

*O que a funcionalidade permite fazer e para qual perfil de usuário. Uma ou duas frases.*

## Decisões relacionadas

*Os ADRs que restringem esta funcionalidade, com link e uma frase sobre o que cada um impõe aqui.
Se nenhum se aplica, escrever que não há.*

## Escopo

*O que entra e o que não entra. O que fica de fora é tão importante quanto o que entra, porque
delimita a revisão.*

## Comportamento

*O fluxo principal, passo a passo. Depois, as regras que se aplicam a ele.*

*Quando o fluxo atravessa mais de um contexto, indicar em qual contexto cada passo ocorre e se a
comunicação é síncrona ou assíncrona.*

## Casos de borda e erro

*Entradas inválidas, concorrência, indisponibilidade de outro contexto, e o comportamento esperado
em cada situação. Se não houver decisão para um caso, registrar como questão em aberto em vez de
deixar implícito.*

## Critérios de aceitação

*Lista verificável. Cada item descreve uma entrada ou situação e o resultado esperado, de forma que
a revisão possa confirmar se foi atendido.*

- [ ] Dado *situação*, quando *ação*, então *resultado esperado*.
- [ ] ...

## Questões em aberto

*O que ainda não foi decidido e bloqueia ou condiciona a implementação. Remover a seção se não
houver.*
