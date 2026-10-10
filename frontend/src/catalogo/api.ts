export type TipoRecurso = 'LABORATORIO' | 'BANCADA' | 'EQUIPAMENTO'
export type EstadoOperacional = 'DISPONIVEL' | 'EM_MANUTENCAO' | 'BAIXADO'
export type AcaoEstado = 'manutencao' | 'liberacao' | 'baixa'

export type Recurso = {
  id: string
  nome: string
  tipo: TipoRecurso
  localizacao: string
  capacidade: number
  estado: EstadoOperacional
}

export type NovoRecurso = Omit<Recurso, 'id' | 'estado'>

export const nomesDosTipos: Record<TipoRecurso, string> = {
  LABORATORIO: 'Laboratório',
  BANCADA: 'Bancada',
  EQUIPAMENTO: 'Equipamento',
}

export const nomesDosEstados: Record<EstadoOperacional, string> = {
  DISPONIVEL: 'Disponível',
  EM_MANUTENCAO: 'Em manutenção',
  BAIXADO: 'Baixado',
}

const URL = '/api/catalogo/recursos'

async function lerResposta<T>(resposta: Response): Promise<T> {
  if (!resposta.ok) {
    const erro = await resposta.json().catch(() => null)
    throw new Error(erro?.mensagem ?? 'Não foi possível falar com a API.')
  }
  return resposta.json()
}

export async function listarRecursos(): Promise<Recurso[]> {
  return lerResposta(await fetch(URL))
}

export async function cadastrarRecurso(novo: NovoRecurso): Promise<Recurso> {
  const resposta = await fetch(URL, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(novo),
  })
  return lerResposta(resposta)
}

export async function mudarEstado(id: string, acao: AcaoEstado): Promise<Recurso> {
  return lerResposta(await fetch(`${URL}/${id}/${acao}`, { method: 'POST' }))
}
