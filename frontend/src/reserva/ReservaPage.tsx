import { useEffect, useState, type FormEvent } from 'react'
import { listarRecursos, nomesDosTipos, type Recurso } from '../catalogo/api'
import {
  agendaDoRecurso,
  cancelarReserva,
  formatarHorario,
  nomesDosEstados,
  nomesDosPerfis,
  reservasDoSolicitante,
  solicitarReserva,
  type Perfil,
  type Reserva,
} from './api'

function mensagemDe(erro: unknown) {
  return erro instanceof Error ? erro.message : 'Erro inesperado.'
}

function mesmaPessoa(a: string, b: string) {
  return a.trim().toLowerCase() === b.trim().toLowerCase()
}

export default function ReservaPage() {
  const [recursos, setRecursos] = useState<Recurso[]>([])
  const [recursoId, setRecursoId] = useState('')
  const [agenda, setAgenda] = useState<Reserva[]>([])
  const [minhas, setMinhas] = useState<Reserva[]>([])

  const [solicitante, setSolicitante] = useState('')
  const [perfil, setPerfil] = useState<Perfil>('DOCENTE')
  const [data, setData] = useState('')
  const [horaInicio, setHoraInicio] = useState('14:00')
  const [horaFim, setHoraFim] = useState('16:00')

  const [erro, setErro] = useState('')
  const [aviso, setAviso] = useState('')

  // Recarrega ao abrir a lista, para enxergar recursos cadastrados no catálogo depois que a tela abriu.
  function carregarRecursos() {
    listarRecursos()
      .then((lista) => {
        setRecursos(lista)
        setRecursoId((atual) => atual || (lista[0]?.id ?? ''))
      })
      .catch((e) => setErro(mensagemDe(e)))
  }

  useEffect(carregarRecursos, [])

  useEffect(() => {
    if (!recursoId) return
    agendaDoRecurso(recursoId)
      .then(setAgenda)
      .catch((e) => setErro(mensagemDe(e)))
  }, [recursoId])

  async function atualizarListas() {
    if (recursoId) setAgenda(await agendaDoRecurso(recursoId))
    if (solicitante.trim()) setMinhas(await reservasDoSolicitante(solicitante))
  }

  async function solicitar(evento: FormEvent) {
    evento.preventDefault()
    setErro('')
    setAviso('')
    try {
      await solicitarReserva({
        recursoId,
        solicitante,
        perfil,
        inicio: `${data}T${horaInicio}`,
        fim: `${data}T${horaFim}`,
      })
      setAviso('Reserva confirmada.')
      await atualizarListas()
    } catch (e) {
      setErro(mensagemDe(e))
    }
  }

  async function buscarMinhas() {
    setErro('')
    try {
      setMinhas(await reservasDoSolicitante(solicitante))
    } catch (e) {
      setErro(mensagemDe(e))
    }
  }

  async function cancelar(reserva: Reserva) {
    if (!window.confirm(`Cancelar a reserva de ${formatarHorario(reserva.inicio)}?`)) return
    setErro('')
    setAviso('')
    try {
      await cancelarReserva(reserva.id, solicitante)
      setAviso('Reserva cancelada.')
      await atualizarListas()
    } catch (e) {
      setErro(mensagemDe(e))
    }
  }

  const nomeDoRecurso = (id: string) => recursos.find((r) => r.id === id)?.nome ?? id

  function tabela(reservas: Reserva[], mostrarRecurso: boolean) {
    if (reservas.length === 0) return <p>Nenhuma reserva.</p>
    return (
      <table className="tabela">
        <thead>
          <tr>
            {mostrarRecurso && <th>Recurso</th>}
            <th>Início</th>
            <th>Fim</th>
            <th>Solicitante</th>
            <th>Estado</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {reservas.map((reserva) => (
            <tr key={reserva.id} className={reserva.estado === 'CANCELADA' ? 'cancelada' : ''}>
              {mostrarRecurso && <td>{nomeDoRecurso(reserva.recursoId)}</td>}
              <td>{formatarHorario(reserva.inicio)}</td>
              <td>{formatarHorario(reserva.fim).slice(-5)}</td>
              <td>
                {reserva.solicitante} ({nomesDosPerfis[reserva.perfil]})
              </td>
              <td>{nomesDosEstados[reserva.estado]}</td>
              <td className="acoes">
                {reserva.estado === 'CONFIRMADA' && mesmaPessoa(reserva.solicitante, solicitante) && (
                  <button onClick={() => cancelar(reserva)}>Cancelar</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    )
  }

  return (
    <section>
      <h2>Reservas</h2>

      <form className="formulario" onSubmit={solicitar}>
        <select
          value={recursoId}
          onFocus={carregarRecursos}
          onChange={(e) => setRecursoId(e.target.value)}
          title="Recurso"
        >
          {recursos.length === 0 && <option value="">Cadastre um recurso no catálogo</option>}
          {recursos.map((r) => (
            <option key={r.id} value={r.id}>
              {r.nome} ({nomesDosTipos[r.tipo]})
            </option>
          ))}
        </select>
        <input
          placeholder="E-mail ou matrícula"
          value={solicitante}
          onChange={(e) => setSolicitante(e.target.value)}
        />
        <select value={perfil} onChange={(e) => setPerfil(e.target.value as Perfil)} title="Perfil">
          {Object.entries(nomesDosPerfis).map(([valor, nome]) => (
            <option key={valor} value={valor}>
              {nome}
            </option>
          ))}
        </select>
        <input type="date" title="Data" value={data} onChange={(e) => setData(e.target.value)} />
        <input type="time" title="Início" value={horaInicio} onChange={(e) => setHoraInicio(e.target.value)} />
        <input type="time" title="Fim" value={horaFim} onChange={(e) => setHoraFim(e.target.value)} />
        <button type="submit" disabled={!recursoId}>
          Reservar
        </button>
      </form>

      {erro && <p className="erro">{erro}</p>}
      {aviso && <p className="aviso">{aviso}</p>}

      <h3>Agenda de {recursoId ? nomeDoRecurso(recursoId) : '—'}</h3>
      {tabela(agenda, false)}

      <h3>Minhas reservas</h3>
      <p className="dica">
        <button onClick={buscarMinhas} disabled={!solicitante.trim()}>
          Buscar reservas de {solicitante.trim() || '…'}
        </button>
      </p>
      {tabela(minhas, true)}
    </section>
  )
}
