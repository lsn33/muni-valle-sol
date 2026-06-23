import { useState, useEffect } from 'react'
import { obtenerAlertas } from '@/services/alertService'
import useAlertStore from '@/store/useAlertStore'

interface UseAlertsReturn {
  loading: boolean
  error: string | null
}

const useAlerts = (): UseAlertsReturn => {
  const { setAlertasActivas } = useAlertStore()
  const [loading, setLoading] = useState<boolean>(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchAlertas = async () => {
      try {
        setLoading(true)
        const data = await obtenerAlertas()
        setAlertasActivas(data)
      } catch {
        setError('Error al obtener alertas')
      } finally {
        setLoading(false)
      }
    }
    fetchAlertas()
  }, [])

  return { loading, error }
}

export default useAlerts
