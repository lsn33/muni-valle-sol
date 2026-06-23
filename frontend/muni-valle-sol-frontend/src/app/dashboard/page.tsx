'use client'

import dynamic from 'next/dynamic'
import { useEffect, useState } from 'react'
import Sidebar from '@/components/Sidebar'
import AlertPanel from '@/components/AlertPanel'
import ReportTable from '@/components/ReportTable'
import CreateAlertModal from '@/components/CreateAlertModal'
import CreateReportModal from '@/components/CreateReportModal'
import useAlertStore from '@/store/useAlertStore'
import useAppStore from '@/store/useAppStore'
import useAuthStore from '@/store/useAuthStore'
import { obtenerReportes } from '@/services/reportService'
import { obtenerAlertas } from '@/services/alertService'
import { Report } from '@/types/Report'

const FireMap = dynamic(() => import('@/components/FireMap'), {
  ssr: false,
  loading: () => (
    <div className="w-full h-full flex items-center justify-center bg-gray-100 rounded-lg">
      <p className="text-gray-500">Cargando mapa...</p>
    </div>
  ),
})

const DashboardPage = () => {
  const { alertasActivas, setAlertasActivas } = useAlertStore()
  const { darkMode } = useAppStore()
  const { usuario } = useAuthStore()

  const [reportes, setReportes] = useState<Report[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [showAlertModal, setShowAlertModal] = useState(false)
  const [showReportModal, setShowReportModal] = useState(false)

  const isAdmin = usuario?.rol === 'ADMIN'

  const fetchData = async () => {
    try {
      setLoading(true)
      const alertasData = await obtenerAlertas()
      setAlertasActivas(alertasData)

      if (isAdmin) {
        const reportesData = await obtenerReportes()
        setReportes(reportesData)
      }
    } catch {
      setError('Error al cargar datos')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
    const interval = setInterval(fetchData, 30000)
    return () => clearInterval(interval)
  }, [isAdmin])

  return (
    <div className={`flex w-full min-h-screen ${darkMode ? 'dark' : ''}`}>
      <Sidebar />

      {/* Para ciudadano: main ocupa toda la altura y usa flex-col para que el mapa crezca */}
      <main
        className={`flex-1 flex flex-col p-3 md:p-4 gap-4 md:ml-0 ${
          isAdmin ? 'overflow-auto' : 'h-screen overflow-hidden'
        }`}
      >
        {/* Header del dashboard */}
        <div className="flex items-center justify-between mt-10 md:mt-0 flex-shrink-0">
          <h1 className="text-lg md:text-xl font-bold text-gray-800 dark:text-white">
            Panel de Control
          </h1>
          <div className="flex items-center gap-3">
            {isAdmin && (
              <button
                onClick={() => setShowAlertModal(true)}
                className="rounded-lg bg-red-600 px-3 md:px-4 py-2 text-xs md:text-sm font-semibold text-white transition hover:bg-red-700"
              >
                Nueva Alerta
              </button>
            )}
            {usuario && !isAdmin && (
              <button
                onClick={() => setShowReportModal(true)}
                className="rounded-lg bg-orange-600 px-3 md:px-4 py-2 text-xs md:text-sm font-semibold text-white transition hover:bg-orange-700"
              >
                Crear Reporte
              </button>
            )}
            {loading && (
              <span className="text-xs md:text-sm text-gray-500">Actualizando...</span>
            )}
            {error && <span className="text-xs md:text-sm text-red-500">{error}</span>}
          </div>
        </div>

        {/* Mapa + Alertas */}
        {/* Ciudadano: flex-1 + min-h-0 para ocupar todo el espacio restante sin scroll */}
        {/* Admin: minHeight fijo porque debajo viene la tabla */}
        <div
          className={`flex flex-col lg:flex-row gap-4 ${
            isAdmin ? '' : 'flex-1 min-h-0'
          }`}
          style={isAdmin ? { minHeight: '420px' } : undefined}
        >
          {/* Mapa */}
          <div
            className={`bg-white rounded-lg shadow overflow-hidden ${
              isAdmin ? 'flex-1' : 'flex-1 min-h-0'
            }`}
            style={isAdmin ? { minHeight: '320px' } : undefined}
          >
            <FireMap alerts={alertasActivas} />
          </div>

          {/* Panel de alertas */}
          <div
            className={`w-full lg:w-72 bg-white rounded-lg shadow p-4 ${
              isAdmin ? 'overflow-hidden' : 'flex flex-col min-h-0 overflow-hidden'
            }`}
            style={isAdmin ? { minHeight: '320px', maxHeight: '500px' } : undefined}
          >
            <AlertPanel alerts={alertasActivas} />
          </div>
        </div>

        {/* Tabla de reportes — solo ADMIN */}
        {isAdmin && (
          <div className="bg-white rounded-lg shadow p-4">
            <ReportTable reportes={reportes} onReportesChange={setReportes} />
          </div>
        )}
      </main>

      {showAlertModal && (
        <CreateAlertModal
          onClose={() => setShowAlertModal(false)}
          onSuccess={fetchData}
        />
      )}

      {showReportModal && (
        <CreateReportModal
          onClose={() => setShowReportModal(false)}
          onSuccess={fetchData}
        />
      )}
    </div>
  )
}

export default DashboardPage