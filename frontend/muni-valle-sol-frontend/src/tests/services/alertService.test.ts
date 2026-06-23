import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { obtenerAlertas, crearAlerta } from '@/services/alertService'
import { Alert } from '@/types/Alert'

const BASE_URL = 'http://localhost:8080'

const mockAlertas: Alert[] = [
  { id: '1', titulo: 'Incendio Norte', descripcion: 'Fuego activo', severidad: 'ALTA', fecha: '2024-01-15' },
  { id: '2', titulo: 'Humo sector sur', descripcion: 'Humo detectado', severidad: 'MEDIA', fecha: '2024-01-15' },
]

describe('alertService', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn())
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('obtenerAlertas()', () => {
    it('debería retornar el listado de alertas correctamente', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => mockAlertas } as Response)
      const result = await obtenerAlertas()
      expect(result).toEqual(mockAlertas)
      expect(result).toHaveLength(2)
    })

    it('debería hacer GET al endpoint correcto con credentials: include', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => [] } as Response)
      await obtenerAlertas()
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/alertas`, { credentials: 'include' })
    })

    it('debería lanzar error cuando la respuesta no es ok', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      await expect(obtenerAlertas()).rejects.toThrow('Error al obtener alertas')
    })

    it('debería retornar array vacío si no hay alertas', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => [] } as Response)
      const result = await obtenerAlertas()
      expect(result).toEqual([])
    })
  })

  describe('crearAlerta()', () => {
    const nuevaAlertaData = { titulo: 'Nueva alerta', descripcion: 'Descripción', severidad: 'ALTA' as const }

    it('debería crear una alerta y retornarla', async () => {
      const alertaCreada: Alert = { id: '3', ...nuevaAlertaData, fecha: '2024-01-16' }
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => alertaCreada } as Response)
      const result = await crearAlerta(nuevaAlertaData)
      expect(result).toEqual(alertaCreada)
    })

    it('debería hacer POST al endpoint correcto con los datos', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => ({}) } as Response)
      await crearAlerta(nuevaAlertaData)
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/alertas`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevaAlertaData),
        credentials: 'include',
      })
    })

    it('debería lanzar error si la creación falla', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      await expect(crearAlerta(nuevaAlertaData)).rejects.toThrow('Error al crear alerta')
    })

    it('debería enviar severidad MEDIA correctamente', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => ({}) } as Response)
      await crearAlerta({ ...nuevaAlertaData, severidad: 'MEDIA' })
      const body = JSON.parse((vi.mocked(fetch).mock.calls[0][1] as RequestInit).body as string)
      expect(body.severidad).toBe('MEDIA')
    })

    it('debería enviar severidad BAJA correctamente', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => ({}) } as Response)
      await crearAlerta({ ...nuevaAlertaData, severidad: 'BAJA' })
      const body = JSON.parse((vi.mocked(fetch).mock.calls[0][1] as RequestInit).body as string)
      expect(body.severidad).toBe('BAJA')
    })
  })
})
