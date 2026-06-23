import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { login, register, logout, getMe } from '@/services/authService'

const BASE_URL = 'http://localhost:8080'

describe('authService', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', vi.fn())
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  describe('login()', () => {
    it('debería llamar al endpoint correcto con credenciales', async () => {
      const mockResponse = { id: 1, nombre: 'Juan', email: 'juan@gmail.com', rol: 'CIUDADANO' }
      vi.mocked(fetch).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      } as Response)

      const result = await login('juan@gmail.com', '12345678A!')
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: 'juan@gmail.com', password: '12345678A!' }),
        credentials: 'include',
      })
      expect(result).toEqual(mockResponse)
    })

    it('debería lanzar error cuando las credenciales son incorrectas', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      await expect(login('wrong@gmail.com', 'wrongpass')).rejects.toThrow('Credenciales incorrectas')
    })

    it('debería incluir credentials: include en la petición', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => ({}) } as Response)
      await login('juan@gmail.com', '12345678A!')
      const callArgs = vi.mocked(fetch).mock.calls[0][1] as RequestInit
      expect(callArgs.credentials).toBe('include')
    })
  })

  describe('register()', () => {
    it('debería registrar al usuario con rol CIUDADANO por defecto', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => ({ id: 2 }) } as Response)
      await register('Juan Pérez', 'juan@gmail.com', '12345678A!')
      const callArgs = vi.mocked(fetch).mock.calls[0][1] as RequestInit
      const body = JSON.parse(callArgs.body as string)
      expect(body.rol).toBe('CIUDADANO')
      expect(body.nombre).toBe('Juan Pérez')
    })

    it('debería lanzar error si el registro falla', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      await expect(register('Juan', 'juan@gmail.com', 'pass')).rejects.toThrow('Error al registrar usuario')
    })

    it('debería hacer POST al endpoint correcto', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => ({}) } as Response)
      await register('Juan', 'juan@gmail.com', '12345678A!')
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/auth/register`, expect.objectContaining({ method: 'POST' }))
    })
  })

  describe('logout()', () => {
    it('debería hacer POST al endpoint de logout con credentials include', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true } as Response)
      await logout()
      expect(fetch).toHaveBeenCalledWith(`${BASE_URL}/api/auth/logout`, {
        method: 'POST',
        credentials: 'include',
      })
    })

    it('debería completar sin lanzar error aunque el servidor falle', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      await expect(logout()).resolves.not.toThrow()
    })
  })

  describe('getMe()', () => {
    it('debería retornar los datos del usuario autenticado', async () => {
      const mockUser = { id: 1, nombre: 'Juan', email: 'juan@gmail.com', rol: 'CIUDADANO' }
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => mockUser } as Response)
      const result = await getMe()
      expect(result).toEqual(mockUser)
    })

    it('debería retornar null cuando la sesión no es válida', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: false } as Response)
      const result = await getMe()
      expect(result).toBeNull()
    })

    it('debería llamar al endpoint con credentials: include', async () => {
      vi.mocked(fetch).mockResolvedValueOnce({ ok: true, json: async () => ({}) } as Response)
      await getMe()
      const callArgs = vi.mocked(fetch).mock.calls[0][1] as RequestInit
      expect(callArgs.credentials).toBe('include')
    })
  })
})
