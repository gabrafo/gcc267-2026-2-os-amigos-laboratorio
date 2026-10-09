import { useEffect, useState, type FormEvent } from 'react'
import {
  cadastrarRecurso,
  listarRecursos,
  mudarEstado,
  nomesDosEstados,
  nomesDosTipos,
  type AcaoEstado,
  type NovoRecurso,
  type Recurso,
  type TipoRecurso,
} from './api'

const formularioVazio: NovoRecurso = {
  nome: '',
  tipo: 'LABORATORIO',
  localizacao: '',
  capacidade: 1,
}

function mensagemDe(erro: unknown) {
  return erro instanceof Error ? erro.message : 'Erro inesperado.'
}

export default function CatalogoPage() {
  const [recursos, setRecursos] = useState<Recurso[]>([])
  const [formulario, setFormulario] = useState<NovoRecurso>(formularioVazio)
  const [erro, setErro] = useState('')

  useEffect(() => {
    listarRecursos()
      .then(setRecursos)
      .catch((e) => setErro(mensagemDe(e)))
  }, [])

  function mudarTipo(tipo: TipoRecurso) {
    // Equipamento sempre tem capacidade 1 (SPEC 0001).
    const capacidade = tipo === 'EQUIPAMENTO' ? 1 : formulario.capacidade
    setFormulario({ ...formulario, tipo, capacidade })
  }

  async function cadastrar(evento: FormEvent) {
    evento.preventDefault()
    setErro('')
    try {
      await cadastrarRecurso(formulario)
      setFormulario(formularioVazio)
      setRecursos(await listarRecursos())
    } catch (e) {
      setErro(mensagemDe(e))
    }
  }

  async function executar(recurso: Recurso, acao: AcaoEstado) {
    if (acao === 'baixa' && !window.confirm(`Dar baixa em "${recurso.nome}"? Não dá para desfazer.`)) {
      return
    }
    setErro('')
    try {
      const atualizado = await mudarEstado(recurso.id, acao)
      setRecursos(recursos.map((r) => (r.id === atualizado.id ? atualizado : r)))
    } catch (e) {
      setErro(mensagemDe(e))
    }
  }

  return (
    <section>
      <h2>Catálogo de recursos</h2>

      <form className="formulario" onSubmit={cadastrar}>
        <input
          placeholder="Nome"
          value={formulario.nome}
          onChange={(e) => setFormulario({ ...formulario, nome: e.target.value })}
        />
        <select value={formulario.tipo} onChange={(e) => mudarTipo(e.target.value as TipoRecurso)}>
          {Object.entries(nomesDosTipos).map(([valor, nome]) => (
            <option key={valor} value={valor}>
              {nome}
            </option>
          ))}
        </select>
        <input
          placeholder="Localização"
          value={formulario.localizacao}
          onChange={(e) => setFormulario({ ...formulario, localizacao: e.target.value })}
        />
        <input
          type="number"
          min={1}
          max={100}
          title="Capacidade"
          value={formulario.capacidade}
          disabled={formulario.tipo === 'EQUIPAMENTO'}
          onChange={(e) => setFormulario({ ...formulario, capacidade: Number(e.target.value) })}
        />
        <button type="submit">Cadastrar</button>
      </form>

      {erro && <p className="erro">{erro}</p>}

      {recursos.length === 0 ? (
        <p>Nenhum recurso cadastrado.</p>
      ) : (
        <table className="tabela">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Tipo</th>
              <th>Localização</th>
              <th>Capacidade</th>
              <th>Estado</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {recursos.map((recurso) => (
              <tr key={recurso.id}>
                <td>{recurso.nome}</td>
                <td>{nomesDosTipos[recurso.tipo]}</td>
                <td>{recurso.localizacao}</td>
                <td>{recurso.capacidade}</td>
                <td>{nomesDosEstados[recurso.estado]}</td>
                <td className="acoes">
                  {recurso.estado === 'DISPONIVEL' && (
                    <button onClick={() => executar(recurso, 'manutencao')}>Manutenção</button>
                  )}
                  {recurso.estado === 'EM_MANUTENCAO' && (
                    <button onClick={() => executar(recurso, 'liberacao')}>Liberar</button>
                  )}
                  {recurso.estado !== 'BAIXADO' && (
                    <button onClick={() => executar(recurso, 'baixa')}>Dar baixa</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}
