import { describe, it, expect, beforeEach } from 'vitest'
import { act } from '@testing-library/react'
import useAuthStore from '@/store/useAuthStore'
import { User } from '@/types/User'

const mockUser: User = {
  id: 1,
  nombre: 'Juan Pérez',
  email: 'juan@gmail.com',
  rol: 'CIUDADANO',
}

describe('useAuthStore', () => {
  beforeEach(() => {
    act(() => {
      useAuthStore.setState({ usuario: null, isAuthenticated: false })
    })
  })

  it('debería iniciar con usuario null y no autenticado', () => {
    const { usuario, isAuthenticated } = useAuthStore.getState()
    expect(usuario).toBeNull()
    expect(isAuthenticated).toBe(false)
  })

  it('debería autenticar al usuario al llamar login()', () => {
    act(() => {
      useAuthStore.getState().login(mockUser)
    })
    const { usuario, isAuthenticated } = useAuthStore.getState()
    expect(usuario).toEqual(mockUser)
    expect(isAuthenticated).toBe(true)
  })

  it('debería limpiar el usuario al llamar logout()', () => {
    act(() => {
      useAuthStore.getState().login(mockUser)
      useAuthStore.getState().logout()
    })
    const { usuario, isAuthenticated } = useAuthStore.getState()
    expect(usuario).toBeNull()
    expect(isAuthenticated).toBe(false)
  })

  it('debería preservar todos los campos del usuario en el store', () => {
    act(() => {
      useAuthStore.getState().login(mockUser)
    })
    const { usuario } = useAuthStore.getState()
    expect(usuario?.id).toBe(1)
    expect(usuario?.nombre).toBe('Juan Pérez')
    expect(usuario?.email).toBe('juan@gmail.com')
    expect(usuario?.rol).toBe('CIUDADANO')
  })

  it('debería sobrescribir el usuario si se llama login() dos veces', () => {
    const otroUser: User = { id: 2, nombre: 'Admin', email: 'admin@gmail.com', rol: 'ADMIN' }
    act(() => {
      useAuthStore.getState().login(mockUser)
      useAuthStore.getState().login(otroUser)
    })
    const { usuario } = useAuthStore.getState()
    expect(usuario?.id).toBe(2)
    expect(usuario?.rol).toBe('ADMIN')
  })

  it('debería manejar usuarios con rol ADMIN correctamente', () => {
    const adminUser: User = { ...mockUser, rol: 'ADMIN' }
    act(() => {
      useAuthStore.getState().login(adminUser)
    })
    expect(useAuthStore.getState().usuario?.rol).toBe('ADMIN')
  })

  it('debería manejar usuarios con rol BRIGADISTA correctamente', () => {
    const brigadistaUser: User = { ...mockUser, rol: 'BRIGADISTA' }
    act(() => {
      useAuthStore.getState().login(brigadistaUser)
    })
    expect(useAuthStore.getState().usuario?.rol).toBe('BRIGADISTA')
  })
})
