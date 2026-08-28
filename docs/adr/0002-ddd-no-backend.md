# ADR 0002 — Estruturar o back-end com Domain-Driven Design

- **Status:** Aceita
- **Data:** 2026-08-27
- **Decisores:** Gabriel Fagundes, Hugo Pontello, João Gherardi

## Contexto

O back-end abriga três contextos — Reserva, Catálogo e Notificação — que hoje estão na mesma
aplicação e devem poder ser extraídos como serviços ao longo do semestre.

O domínio tem regras que vão além de cadastro e consulta: exclusão mútua na agenda de um recurso,
restrições de antecedência e de duração, ciclo de vida com transições válidas e inválidas, e efeito
da indisponibilidade de um recurso sobre agendamentos já existentes.

Sem uma estrutura definida, essas regras tendem a se espalhar por classes de serviço e a se
duplicar, e os contextos ficam sem fronteira explícita. Isso dificulta a extração dos serviços, que
é o objetivo do trabalho.

## Decisão

Adotar DDD, com divisão em contextos delimitados e padrões táticos aplicados onde houver regra de
negócio.

### Contextos

Cada contexto é um pacote de primeiro nível, com modelo próprio:

```
br.ufla.gcc267.laboratorio
├── reserva/
├── catalogo/
└── notificacao/
```

Regras de fronteira:

- Um contexto não importa classes de domínio de outro. A comunicação entre contextos ocorre por
  interface explícita ou por evento, e as referências são feitas por identificador.
- Não há modelo compartilhado entre contextos. Um mesmo termo do negócio pode designar conceitos
  distintos em cada contexto, com atributos e comportamento próprios.

### Camadas

Dentro de cada contexto:

```
<contexto>/
├── domain/          modelo do negócio e interfaces de repositório
├── application/     casos de uso, orquestração e portas para outros contextos
├── infrastructure/  implementação de repositório, clientes HTTP, mensageria
└── api/             controladores REST e DTOs
```

Regra de dependência: `domain` não depende de nenhuma outra camada nem de framework; `application`
depende de `domain`; `infrastructure` e `api` dependem das duas.

### Padrões táticos

- Agregados que protegem as próprias invariantes. As mudanças de estado ocorrem por métodos
  nomeados segundo o domínio, que rejeitam transições inválidas, sem setters públicos de estado.
- Objetos de valor imutáveis, implementados como `record` e validados na construção.
- Repositórios declarados como interface no domínio e implementados na infraestrutura.
- Eventos de domínio para o que atravessa a fronteira entre contextos. Inicialmente publicados no
  mesmo processo; posteriormente, como mensagens em broker.
- Linguagem ubíqua em português, com os mesmos termos usados na comunicação com os usuários do
  sistema.

A identificação dos agregados, objetos de valor e eventos de cada contexto não faz parte deste ADR.

Não serão adotados por ora: CQRS, Event Sourcing e camada de anticorrupção formal.

## Consequências

- Cada regra de negócio fica concentrada em um único ponto.
- As fronteiras entre contextos existem desde o início, o que torna a extração dos serviços
  incremental.
- O domínio, sem dependência de framework, é testável por teste unitário, com execução rápida na CI.
- Há mais arquivos e mais indireção por caso de uso, inclusive nos casos que são apenas cadastro.
- Nenhum integrante da equipe aplicou DDD antes; a modelagem inicial provavelmente será revista.

Fica em aberto se o domínio permanecerá livre de anotações de persistência. A decisão será tomada
quando o banco de dados for definido.

## Referências

- Eric Evans, *Domain-Driven Design* (2003).
- Vaughn Vernon, *Implementing Domain-Driven Design* (2013).
