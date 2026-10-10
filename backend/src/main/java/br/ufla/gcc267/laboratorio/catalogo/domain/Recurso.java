package br.ufla.gcc267.laboratorio.catalogo.domain;

import java.util.UUID;

/** Agregado do Catalogo: um laboratorio, bancada ou equipamento que pode ser reservado. */
public class Recurso {

    private static final int NOME_MIN = 3;
    private static final int NOME_MAX = 100;
    private static final int LOCALIZACAO_MAX = 100;
    private static final int CAPACIDADE_MAX = 100;

    private final UUID id;
    private final String nome;
    private final TipoRecurso tipo;
    private final String localizacao;
    private final int capacidade;
    private EstadoOperacional estado;

    private Recurso(String nome, TipoRecurso tipo, String localizacao, int capacidade) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.tipo = tipo;
        this.localizacao = localizacao;
        this.capacidade = capacidade;
        this.estado = EstadoOperacional.DISPONIVEL;
    }

    public static Recurso cadastrar(String nome, TipoRecurso tipo, String localizacao, int capacidade) {
        String nomeLimpo = nome == null ? "" : nome.strip();
        String localizacaoLimpa = localizacao == null ? "" : localizacao.strip();

        if (nomeLimpo.length() < NOME_MIN || nomeLimpo.length() > NOME_MAX) {
            throw new DadosInvalidosException("O nome deve ter entre 3 e 100 caracteres.");
        }
        if (tipo == null) {
            throw new DadosInvalidosException("O tipo é obrigatório.");
        }
        if (localizacaoLimpa.isEmpty() || localizacaoLimpa.length() > LOCALIZACAO_MAX) {
            throw new DadosInvalidosException("A localização é obrigatória e deve ter até 100 caracteres.");
        }
        if (capacidade < 1 || capacidade > CAPACIDADE_MAX) {
            throw new DadosInvalidosException("A capacidade deve estar entre 1 e 100.");
        }
        if (tipo == TipoRecurso.EQUIPAMENTO && capacidade != 1) {
            throw new DadosInvalidosException("A capacidade de um equipamento deve ser 1.");
        }
        return new Recurso(nomeLimpo, tipo, localizacaoLimpa, capacidade);
    }

    public void enviarParaManutencao() {
        if (estado != EstadoOperacional.DISPONIVEL) {
            throw new TransicaoInvalidaException("Só um recurso disponível pode ir para manutenção.");
        }
        estado = EstadoOperacional.EM_MANUTENCAO;
    }

    public void liberar() {
        if (estado != EstadoOperacional.EM_MANUTENCAO) {
            throw new TransicaoInvalidaException("Só um recurso em manutenção pode ser liberado.");
        }
        estado = EstadoOperacional.DISPONIVEL;
    }

    public void darBaixa() {
        if (estado == EstadoOperacional.BAIXADO) {
            throw new TransicaoInvalidaException("O recurso já foi baixado.");
        }
        estado = EstadoOperacional.BAIXADO;
    }

    public boolean podeSerReservadoPor(Perfil perfil) {
        return estado == EstadoOperacional.DISPONIVEL && tipo.aceitaPerfil(perfil);
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoRecurso getTipo() {
        return tipo;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public EstadoOperacional getEstado() {
        return estado;
    }
}
