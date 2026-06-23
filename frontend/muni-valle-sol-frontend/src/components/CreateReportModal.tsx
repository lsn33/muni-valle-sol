'use client'

import { useState } from 'react'
import { crearReporte } from '@/services/reportService'
import useAuthStore from '@/store/useAuthStore'
import useUserLocation from '@/hooks/useUserLocation'

interface CreateReportModalProps {
  onClose: () => void
  onSuccess: () => void
}

const CreateReportModal = ({ onClose, onSuccess }: CreateReportModalProps) => {
  const { usuario } = useAuthStore()
  const { userLocation } = useUserLocation()
  const [titulo, setTitulo] = useState('')
  const [descripcion, setDescripcion] = useState('')
  const [tipo, setTipo] = useState<'INCENDIO' | 'HUMO' | 'SOSPECHOSO'>('INCENDIO')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (!titulo.trim() || !descripcion.trim()) {
      setError('Todos los campos son requeridos')
      return
    }

    if (!usuario?.email) {
      setError('Debes iniciar sesión para crear un reporte')
      return
    }

    setLoading(true)
    try {
      await crearReporte({
        titulo,
        descripcion,
        tipo,
        emailUsuario: usuario.email,
        latitud: userLocation?.lat ?? -33.4569,
        longitud: userLocation?.lng ?? -70.6483,
      })
      onSuccess()
      onClose()
    } catch (err: any) {
      setError(err.message || 'Error al crear reporte')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
        <h2 className="text-xl font-bold text-gray-900 mb-6">Crear Reporte</h2>

        {error && (
          <div className="mb-4 px-4 py-3 rounded-lg text-sm bg-red-50 border border-red-200 text-red-600">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold uppercase tracking-wide text-gray-500">
              Titulo
            </label>
            <input
              type="text"
              placeholder="Titulo del reporte"
              value={titulo}
              onChange={(e) => setTitulo(e.target.value)}
              className="px-4 py-2.5 rounded-lg text-sm outline-none border border-gray-200 text-gray-900 placeholder-gray-400 focus:border-gray-900"
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold uppercase tracking-wide text-gray-500">
              Descripcion
            </label>
            <textarea
              placeholder="Descripcion del incidente"
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              rows={3}
              className="px-4 py-2.5 rounded-lg text-sm outline-none border border-gray-200 text-gray-900 placeholder-gray-400 focus:border-gray-900 resize-none"
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold uppercase tracking-wide text-gray-500">
              Tipo
            </label>
            <select
              value={tipo}
              onChange={(e) => setTipo(e.target.value as 'INCENDIO' | 'HUMO' | 'SOSPECHOSO')}
              className="px-4 py-2.5 rounded-lg text-sm outline-none border border-gray-200 text-gray-900 focus:border-gray-900"
            >
              <option value="INCENDIO">Incendio</option>
              <option value="HUMO">Humo</option>
              <option value="SOSPECHOSO">Sospechoso</option>
            </select>
          </div>

          <div className="flex gap-3 mt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-3 rounded-lg font-semibold text-sm border border-gray-300 text-gray-600 hover:bg-gray-50 transition"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 py-3 rounded-lg font-semibold text-sm bg-gray-900 text-white hover:bg-gray-800 disabled:opacity-40 transition"
            >
              {loading ? 'Guardando...' : 'Crear reporte'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default CreateReportModal