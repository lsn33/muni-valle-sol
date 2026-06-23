import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { obtenerReportes, eliminarReporte, actualizarReporte, crearReporte } from '@/services/reportService'
import { Report } from '@/types/Report'

const BASE_URL = 'http://localhost:8080'

const mockReporte: Report = {
  id: 1,
  titulo: 'Incendio cerro',
  descripcion: 'Fuego activo en el cerro',
  tipo: 'INCENDIO',
  estado: 'ACTIVO',
  emailUsuario: 'juan@gmail.com',
  fechaCreacion: '2024-01-15T10:00:00',
  ubicacion: { id: 1, lat: -33.4569, lng: -70.6483, direccion: 'Cerro', comuna: 'Vitacura', region: 'Metropolitana' },
}

describe('reportService', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn())
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('obtenerReportes()', () => {
    it('debería retornar el listado de reportes', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => [mockReporte] } as Response)
      const result = await obtenerReportes()
      expect(result).toHaveLength(1)
      expect(result[0]).toEqual(mockReporte)
    })

    it('debería llamar al endpoint correcto con credentials: include', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => [] } as Response)
      await obtenerReportes()
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/reportes`, { credentials: 'include' })
    })

    it('debería lanzar error si la respuesta no es ok', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      await expect(obtenerReportes()).rejects.toThrow('Error al obtener reportes')
    })

    it('debería retornar array vacío si no hay reportes', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => [] } as Response)
      expect(await obtenerReportes()).toEqual([])
    })
  })

  describe('eliminarReporte()', () => {
    it('debería hacer DELETE al endpoint correcto', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true } as Response)
      await eliminarReporte(1)
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/reportes/1`, { method: 'DELETE', credentials: 'include' })
    })

    it('debería completar sin retornar nada (void)', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true } as Response)
      expect(await eliminarReporte(1)).toBeUndefined()
    })

    it('debería lanzar error si el servidor rechaza la eliminación', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      await expect(eliminarReporte(1)).rejects.toThrow('Error al eliminar reporte')
    })

    it('debería construir la URL correctamente para distintos IDs', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true } as Response)
      await eliminarReporte(99)
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/reportes/99`, expect.objectContaining({ method: 'DELETE' }))
    })
  })

  describe('actualizarReporte()', () => {
    it('debería hacer PUT con el nuevo título y retornar el reporte actualizado', async () => {
      const actualizado = { ...mockReporte, titulo: 'Nuevo título' }
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => actualizado } as Response)
      const result = await actualizarReporte(1, 'Nuevo título')
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/reportes/1`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ titulo: 'Nuevo título' }),
        credentials: 'include',
      })
      expect(result.titulo).toBe('Nuevo título')
    })

    it('debería lanzar error si la actualización falla', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      await expect(actualizarReporte(1, 'Título')).rejects.toThrow('Error al actualizar reporte')
    })
  })

  describe('crearReporte()', () => {
    const nuevoReporteData = {
      titulo: 'Nuevo incendio',
      descripcion: 'Descripción',
      latitud: -33.4569,
      longitud: -70.6483,
      tipo: 'INCENDIO' as const,
      emailUsuario: 'juan@gmail.com',
    }

    it('debería crear un reporte y retornarlo', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => mockReporte } as Response)
      const result = await crearReporte(nuevoReporteData)
      expect(result).toEqual(mockReporte)
    })

    it('debería hacer POST con todos los campos', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => mockReporte } as Response)
      await crearReporte(nuevoReporteData)
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/reportes`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevoReporteData),
        credentials: 'include',
      })
    })

    it('debería lanzar error si la creación falla', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      await expect(crearReporte(nuevoReporteData)).rejects.toThrow('Error al crear reporte')
    })
  })
})
