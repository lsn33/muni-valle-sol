import { useState, useEffect } from 'react'
import { Report } from '@/types/Report'
import { obtenerReportes } from '@/services/reportService'

interface UseReportsReturn {
  reports: Report[]
  loading: boolean
  error: string | null
}

const useReports = (): UseReportsReturn => {
  const [reports, setReports] = useState<Report[]>([])
  const [loading, setLoading] = useState<boolean>(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchReports = async () => {
      try {
        setLoading(true)
        const data = await obtenerReportes()
        setReports(data)
      } catch {
        setError('Error al obtener reportes')
      } finally {
        setLoading(false)
      }
    }
    fetchReports()
  }, [])

  return { reports, loading, error }
}

export default useReports
