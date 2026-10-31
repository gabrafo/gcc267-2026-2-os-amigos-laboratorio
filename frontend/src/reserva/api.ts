export type Perfil = 'DOCENTE' | 'DISCENTE' | 'TECNICO' | 'COORDENACAO'
export type EstadoReserva = 'CONFIRMADA' | 'CANCELADA'

export type Reserva = {
  id: string
  recursoId: string
  solicitante: string
  perfil: Perfil
  inicio: string
  fim: string
  estado: EstadoReserva
}

export type NovaReserva = Omit<Reserva, 'id' | 'estado'>

export const nomesDosPerfis: Record<Perfil, string> = {
  DOCENTE: 'Docente',
  DISCENTE: 'Discente',
  TECNICO: 'Técnico(a)',
  COORDENACAO: 'Coordenação',
}

export const nomesDosEstados: Record<EstadoReserva, string> = {
  CONFIRMADA: 'Confirmada',
  CANCELADA: 'Cancelada',
}

const URL = '/api/reservas'

async function lerResposta<T>(resposta: Response): Promise<T> {
  if (!resposta.ok) {
    const erro = await resposta.json().catch(() => null)
    throw new Error(erro?.mensagem ?? 'Não foi possível falar com a API.')
  }
  return resposta.json()
}

export async function agendaDoRecurso(recursoId: string): Promise<Reserva[]> {
  return lerResposta(await fetch(`${URL}?recursoId=${encodeURIComponent(recursoId)}`))
}

export async function reservasDoSolicitante(solicitante: string): Promise<Reserva[]> {
  return lerResposta(await fetch(`${URL}?solicitante=${encodeURIComponent(solicitante)}`))
}

export async function solicitarReserva(nova: NovaReserva): Promise<Reserva> {
  const resposta = await fetch(URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(nova),
  })
  return lerResposta(resposta)
}

export async function cancelarReserva(id: string, solicitante: string): Promise<Reserva> {
  const resposta = await fetch(`${URL}/${id}/cancelamento`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ solicitante }),
  })
  return lerResposta(resposta)
}

/** "2026-10-20T14:00:00" -> "20/10/2026 14:00". A API envia o horario do campus, sem fuso. */
export function formatarHorario(dataHora: string) {
  const [data, hora] = dataHora.split('T')
  const [ano, mes, dia] = data.split('-')
  return `${dia}/${mes}/${ano} ${hora.slice(0, 5)}`
}
