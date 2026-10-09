package br.ufla.gcc267.laboratorio.catalogo.infrastructure;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import br.ufla.gcc267.laboratorio.catalogo.domain.Recurso;
import br.ufla.gcc267.laboratorio.catalogo.domain.RecursoRepository;

/** Guarda os recursos em memoria ate o banco de dados ser definido em ADR. */
@Repository
public class RecursoRepositoryEmMemoria implements RecursoRepository {

    // Collator ordena como no dicionario: ignora caixa e coloca "Optica" com acento antes de "Zoologia".
    private static final Collator ORDEM_ALFABETICA = Collator.getInstance(Locale.of("pt", "BR"));

    private final Map<UUID, Recurso> recursos = new ConcurrentHashMap<>();

    @Override
    public void salvar(Recurso recurso) {
        recursos.put(recurso.getId(), recurso);
    }

    @Override
    public Optional<Recurso> buscarPorId(UUID id) {
        return Optional.ofNullable(recursos.get(id));
    }

    @Override
    public List<Recurso> listarTodos() {
        List<Recurso> lista = new ArrayList<>(recursos.values());
        lista.sort(Comparator.comparing(Recurso::getNome, ORDEM_ALFABETICA));
        return lista;
    }

    @Override
    public boolean existeComNome(String nome) {
        String nomeLimpo = nome == null ? "" : nome.strip();
        return recursos.values().stream().anyMatch(r -> r.getNome().equalsIgnoreCase(nomeLimpo));
    }
}
