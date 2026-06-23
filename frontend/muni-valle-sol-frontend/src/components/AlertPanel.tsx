'use client'

import { Alert } from '@/types/Alert'

interface AlertPanelProps {
  alerts: Alert[]
}

const colorSeveridad = (severidad: string): string => {
  switch (severidad) {
    case 'ALTA':
      return 'bg-red-100 border-red-500 text-red-800 dark:bg-red-900 dark:text-red-200'
    case 'MEDIA':
      return 'bg-yellow-100 border-yellow-500 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
    case 'BAJA':
      return 'bg-green-100 border-green-500 text-green-800 dark:bg-green-900 dark:text-green-200'
    default:
      return 'bg-gray-100 border-gray-400 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
  }
}

const AlertPanel = ({ alerts }: AlertPanelProps) => {
  return (
    <div className="flex flex-col h-full">
      <h2 className="text-lg font-bold text-gray-800 dark:text-white mb-3 flex-shrink-0">
        Alertas Activas
        {alerts.length > 0 && (
          <span className="ml-2 text-sm font-normal text-gray-500 dark:text-gray-400">
            ({alerts.length})
          </span>
        )}
      </h2>

      <div className="flex-1 overflow-y-auto pr-1 flex flex-col gap-3">
        {alerts.length === 0 ? (
          <p className="text-gray-500 dark:text-gray-400 text-sm">
            No hay alertas activas en este momento.
          </p>
        ) : (
          alerts.map((alerta) => (
            <div
              key={alerta.id}
              className={`border-l-4 rounded p-3 text-sm flex-shrink-0 ${colorSeveridad(alerta.severidad)}`}
            >
              <p className="font-semibold">{alerta.titulo}</p>
              <p className="text-xs mt-1">{alerta.descripcion}</p>
              <p className="text-xs mt-1 opacity-70">{alerta.fecha}</p>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

export default AlertPanel