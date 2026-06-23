import { Alert } from '@/types/Alert'

const BASE_URL = process.env.NEXT_PUBLIC_BFF_URL

export const obtenerAlertas = async (): Promise<Alert[]> => {
  const response = await fetch(`${BASE_URL}/api/alertas`, {
    credentials: 'include',
  })
  if (!response.ok) {
    throw new Error('Error al obtener alertas')
  }
  return response.json()
}

export const crearAlerta = async (data: {
  titulo: string
  descripcion: string
  severidad: 'ALTA' | 'MEDIA' | 'BAJA'
  latitud?: number
  longitud?: number
}) => {
  const response = await fetch(`${BASE_URL}/api/alertas`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
    credentials: 'include',
  })
  if (!response.ok) {
    throw new Error('Error al crear alerta')
  }
  return response.json()
}