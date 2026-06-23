import { Report } from '@/types/Report'
import { Alert } from '@/types/Alert'

const BASE_URL = process.env.NEXT_PUBLIC_BFF_URL

export const obtenerReportes = async (): Promise<Report[]> => {
  const response = await fetch(`${BASE_URL}/api/reportes`, {
    credentials: 'include',
  })
  if (!response.ok) {
    throw new Error('Error al obtener reportes')
  }
  return response.json()
}

export const eliminarReporte = async (id: number): Promise<void> => {
  const response = await fetch(`${BASE_URL}/api/reportes/${id}`, {
    method: 'DELETE',
    credentials: 'include',
  })
  if (!response.ok) {
    throw new Error('Error al eliminar reporte')
  }
}

export const actualizarReporte = async (id: number, titulo: string): Promise<Report> => {
  const response = await fetch(`${BASE_URL}/api/reportes/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ titulo }),
    credentials: 'include',
  })
  if (!response.ok) {
    throw new Error('Error al actualizar reporte')
  }
  return response.json()
}

export const crearReporte = async (data: {
  titulo: string
  descripcion: string
  latitud: number
  longitud: number
  tipo: 'INCENDIO' | 'HUMO' | 'SOSPECHOSO'
  emailUsuario: string
}): Promise<Report> => {
  const response = await fetch(`${BASE_URL}/api/reportes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
    credentials: 'include',
  })
  if (!response.ok) {
    throw new Error('Error al crear reporte')
  }
  return response.json()
}

export const emitirAlerta = async (id: number): Promise<Alert> => {
  const response = await fetch(`${BASE_URL}/api/reportes/${id}/emitir-alerta`, {
    method: 'POST',
    credentials: 'include',
  })
  if (!response.ok) {
    throw new Error('Error al emitir alerta')
  }
  return response.json()
}