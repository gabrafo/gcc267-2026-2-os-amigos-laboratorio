# gcc267-2026-2-os-amigos-laboratorio

[![CI](https://github.com/gabrafo/gcc267-2026-2-os-amigos-laboratorio/actions/workflows/ci.yml/badge.svg)](https://github.com/gabrafo/gcc267-2026-2-os-amigos-laboratorio/actions/workflows/ci.yml)

Repositório para a disciplina Projeto Integrador I (GCC267) do curso de Sistemas de Informação da
Universidade Federal de Lavras (UFLA).

**Domínio:** reserva de laboratórios e equipamentos.

## Documentação

| Documento | Conteúdo |
| --- | --- |
| [docs/equipe.md](docs/equipe.md) | Integrantes, papéis e organização da equipe |
| [docs/visao-produto.md](docs/visao-produto.md) | Problema, público, contextos delimitados e a transação que os atravessa |
| [docs/adr/](docs/adr/) | Registros de decisão de arquitetura |
| [docs/adr/template/](docs/adr/template/template.md) | Modelo para novos ADRs |
| [docs/spec/](docs/spec/README.md) | Especificações de funcionalidade e o processo de SDD |

### Decisões registradas

- [ADR 0000](docs/adr/0000-registro-de-decisoes.md) — Registrar decisões de arquitetura como ADRs
- [ADR 0001](docs/adr/0001-stack-tecnologica.md) — TypeScript + React no front-end, Java + Spring Boot no back-end
- [ADR 0002](docs/adr/0002-ddd-no-backend.md) — Estruturar o back-end com Domain-Driven Design
- [ADR 0003](docs/adr/0003-spec-driven-development.md) — Adotar Spec Driven Development

## Estrutura

```
.
├── backend/    API em Java 21 + Spring Boot 4 (Maven)
├── frontend/   Aplicação web em TypeScript + React (Vite)
└── docs/       Documentação do projeto e ADRs
```

Ambos os módulos estão no estado de esqueleto: o objetivo desta etapa é ter a estrutura e o
pipeline de integração contínua funcionando. O código de domínio ainda será escrito.

## Pré-requisitos

- **JDK 21** — o Maven vem junto, via *wrapper* (`./mvnw`), não precisa ser instalado.
- **Node.js 22** ou superior.

## Como rodar

### Back-end

```bash
cd backend
./mvnw spring-boot:run       # sobe a API em http://localhost:8080
./mvnw verify                # build + testes
./mvnw checkstyle:check      # lint
```

### Front-end

```bash
cd frontend
npm install
npm run dev                  # servidor de desenvolvimento
npm run build                # checagem de tipos + build de produção
npm run lint                 # lint
```

## Integração contínua

O workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) roda a cada push e a cada Pull
Request, em dois jobs paralelos:

| Job | Passos |
| --- | --- |
| **Back-end** | Checkstyle → `mvnw verify` → publica o jar |
| **Front-end** | `npm ci` → oxlint → `tsc` + build do Vite → publica o bundle |

`main` só recebe código via Pull Request com a CI verde.

## Licença

[GNU GPL v3](LICENSE).
