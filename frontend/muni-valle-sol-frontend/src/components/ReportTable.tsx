'use client'

import { useEffect, useState } from 'react'
import { Report } from '@/types/Report'
import { eliminarReporte, actualizarReporte, emitirAlerta } from '@/services/reportService'
import useAuthStore from '@/store/useAuthStore'

interface ReportTableProps {
  reports?: Report[]
  reportes?: Report[]
  onReportesChange?: (reportes: Report[]) => void
}

const colorEstado = (estado: string): string => {
  switch (estado) {
    case 'ACTIVO':
      return 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
    case 'EN_ATENCION':
      return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
    case 'RESUELTO':
      return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
  }
}

const ReportTable = ({ reports, reportes, onReportesChange }: ReportTableProps) => {
  const { usuario } = useAuthStore()
  const isAdmin = usuario?.rol === 'ADMIN'
  const canEmitir = usuario?.rol === 'ADMIN' || usuario?.rol === 'BRIGADISTA'
  const [localReports, setLocalReports] = useState<Report[]>(reports ?? reportes ?? [])
  const [editingId, setEditingId] = useState<number | null>(null)
  const [editingTitulo, setEditingTitulo] = useState('')
  const [loading, setLoading] = useState(false)
  const [emitiendo, setEmitiendo] = useState<number | null>(null)

  useEffect(() => {
    setLocalReports(reports ?? reportes ?? [])
  }, [reports, reportes])

  const handleEliminar = async (id: number) => {
    if (!confirm('Estas seguro de eliminar este reporte?')) return
    try {
      setLoading(true)
      await eliminarReporte(id)
      const updatedReports = localReports.filter((r) => r.id !== id)
      setLocalReports(updatedReports)
      onReportesChange?.(updatedReports)
    } catch {
      alert('Error al eliminar el reporte')
    } finally {
      setLoading(false)
    }
  }

  const handleEditar = (reporte: Report) => {
    setEditingId(reporte.id)
    setEditingTitulo(reporte.titulo)
  }

  const handleGuardar = async (id: number) => {
    try {
      setLoading(true)
      const actualizado = await actualizarReporte(id, editingTitulo)
      const updatedReports = localReports.map((r) => (r.id === id ? actualizado : r))
      setLocalReports(updatedReports)
      onReportesChange?.(updatedReports)
      setEditingId(null)
    } catch {
      alert('Error al actualizar el reporte')
    } finally {
      setLoading(false)
    }
  }

  const handleCancelarEdicion = () => {
    setEditingId(null)
    setEditingTitulo('')
  }

  const handleEmitirAlerta = async (id: number) => {
    if (!confirm('¿Emitir alerta pública a partir de este reporte?')) return
    try {
      setEmitiendo(id)
      await emitirAlerta(id)
      alert('Alerta emitida correctamente')
    } catch {
      alert('Error al emitir la alerta')
    } finally {
      setEmitiendo(null)
    }
  }

  return (
    <div className="overflow-x-auto">
      <h2 className="text-lg font-bold text-gray-800 dark:text-white mb-3">
        Reportes de Incendios
      </h2>

      {localReports.length === 0 ? (
        <p className="text-gray-500 dark:text-gray-400 text-sm">
          No hay reportes disponibles.
        </p>
      ) : (
        <table className="w-full text-sm text-left">
          <thead className="bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300">
            <tr>
              <th className="px-3 md:px-4 py-2">Titulo</th>
              <th className="px-3 md:px-4 py-2 hidden sm:table-cell">Comuna</th>
              <th className="px-3 md:px-4 py-2">Estado</th>
              <th className="px-3 md:px-4 py-2 hidden md:table-cell">Fecha</th>
              {canEmitir && <th className="px-4 py-2">Acciones</th>}
            </tr>
          </thead>
          <tbody>
            {localReports.map((reporte) => (
              <tr
                key={reporte.id}
                className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800"
              >
                <td className="px-3 md:px-4 py-2 font-medium text-gray-800 dark:text-white max-w-[140px] md:max-w-none">
                  {isAdmin && editingId === reporte.id ? (
                    <input
                      type="text"
                      value={editingTitulo}
                      onChange={(e) => setEditingTitulo(e.target.value)}
                      className="w-full rounded border border-gray-200 px-2 py-1 text-sm outline-none focus:border-gray-900 dark:border-gray-600 dark:bg-gray-900 dark:text-white"
                    />
                  ) : (
                    <span className="block truncate">{reporte.titulo}</span>
                  )}
                </td>
                <td className="px-3 md:px-4 py-2 text-gray-600 dark:text-gray-300 hidden sm:table-cell">
                  {reporte.ubicacion?.comuna ?? '-'}
                </td>
                <td className="px-3 md:px-4 py-2">
                  <span className={`px-2 py-1 rounded text-xs font-semibold ${colorEstado(reporte.estado)}`}>
                    {reporte.estado}
                  </span>
                </td>
                <td className="px-3 md:px-4 py-2 text-gray-600 dark:text-gray-300 hidden md:table-cell">
                  {reporte.fechaCreacion
                    ? new Date(reporte.fechaCreacion).toLocaleDateString('es-CL')
                    : '-'}
                </td>
                {canEmitir && (
                  <td className="px-3 md:px-4 py-2">
                    <div className="flex items-center gap-1 md:gap-2 flex-wrap">
                      {isAdmin && (
                        <>
                          {editingId === reporte.id ? (
                            <>
                              <button
                                onClick={() => handleGuardar(reporte.id)}
                                disabled={loading}
                                className="rounded bg-green-600 px-2 py-1 text-xs text-white transition hover:bg-green-700 disabled:opacity-40"
                              >
                                Guardar
                              </button>
                              <button
                                onClick={handleCancelarEdicion}
                                className="rounded bg-gray-200 px-2 py-1 text-xs text-gray-700 transition hover:bg-gray-300 dark:bg-gray-700 dark:text-gray-200"
                              >
                                Cancelar
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                onClick={() => handleEditar(reporte)}
                                className="rounded bg-blue-100 px-2 py-1 text-xs text-blue-700 transition hover:bg-blue-200 dark:bg-blue-900 dark:text-blue-200"
                              >
                                Editar
                              </button>
                              <button
                                onClick={() => handleEliminar(reporte.id)}
                                disabled={loading}
                                className="rounded bg-red-100 px-2 py-1 text-xs text-red-700 transition hover:bg-red-200 disabled:opacity-40 dark:bg-red-900 dark:text-red-200"
                              >
                                Eliminar
                              </button>
                            </>
                          )}
                        </>
                      )}
                      <button
                        onClick={() => handleEmitirAlerta(reporte.id)}
                        disabled={emitiendo === reporte.id}
                        className="rounded bg-orange-100 px-2 py-1 text-xs text-orange-700 transition hover:bg-orange-200 disabled:opacity-40 dark:bg-orange-900 dark:text-orange-200"
                      >
                        {emitiendo === reporte.id ? 'Emitiendo...' : 'Emitir Alerta'}
                      </button>
                    </div>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default ReportTable