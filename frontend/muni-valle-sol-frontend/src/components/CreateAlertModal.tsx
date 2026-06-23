'use client'

import { useState } from 'react'
import { crearAlerta } from '@/services/alertService'
import useAlertStore from '@/store/useAlertStore'

interface CreateAlertModalProps {
  onClose: () => void
  onSuccess: () => void
}

const CreateAlertModal = ({ onClose, onSuccess }: CreateAlertModalProps) => {
  const { agregarAlerta } = useAlertStore()
  const [titulo, setTitulo] = useState('')
  const [descripcion, setDescripcion] = useState('')
  const [severidad, setSeveridad] = useState<'ALTA' | 'MEDIA' | 'BAJA'>('MEDIA')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (!titulo.trim() || !descripcion.trim()) {
      setError('Todos los campos son requeridos')
      return
    }

    setLoading(true)
    try {
      const nueva = await crearAlerta({ titulo, descripcion, severidad })
      agregarAlerta(nueva)
      onSuccess()
      onClose()
    } catch (err: any) {
      setError(err.message || 'Error al crear alerta')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
        <h2 className="text-xl font-bold text-gray-900 mb-6">Nueva Alerta</h2>

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
              placeholder="Titulo de la alerta"
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
              placeholder="Descripcion de la alerta"
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              rows={3}
              className="px-4 py-2.5 rounded-lg text-sm outline-none border border-gray-200 text-gray-900 placeholder-gray-400 focus:border-gray-900 resize-none"
            />
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-xs font-semibold uppercase tracking-wide text-gray-500">
              Severidad
            </label>
            <select
              value={severidad}
              onChange={(e) => setSeveridad(e.target.value as 'ALTA' | 'MEDIA' | 'BAJA')}
              className="px-4 py-2.5 rounded-lg text-sm outline-none border border-gray-200 text-gray-900 focus:border-gray-900"
            >
              <option value="ALTA">Alta</option>
              <option value="MEDIA">Media</option>
              <option value="BAJA">Baja</option>
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
              {loading ? 'Guardando...' : 'Crear alerta'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default CreateAlertModal