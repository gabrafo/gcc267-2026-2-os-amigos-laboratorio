import { useEffect, useState } from 'react'
import './App.css'

type StatusApi = 'verificando' | 'online' | 'offline'

function App() {
  const [statusApi, setStatusApi] = useState<StatusApi>('verificando')

  useEffect(() => {
    fetch('/api/health')
      .then((resposta) => setStatusApi(resposta.ok ? 'online' : 'offline'))
      .catch(() => setStatusApi('offline'))
  }, [])

  return (
    <>
      <header className="cabecalho">
        <h1>Reserva de Laboratórios</h1>
        <p>Laboratórios e equipamentos do departamento em um só lugar.</p>
        <p className={`status status-${statusApi}`}>API: {statusApi}</p>
      </header>

      <main className="conteudo">
        <p>Em breve: catálogo de recursos e reservas.</p>
      </main>
    </>
  )
}

export default App
