import { describe, it, expect, beforeEach } from 'vitest'
import { act } from '@testing-library/react'
import useAppStore from '@/store/useAppStore'

describe('useAppStore', () => {
  beforeEach(() => {
    act(() => {
      useAppStore.setState({ darkMode: false, userLocation: null })
    })
  })

  it('debería iniciar con darkMode false y ubicación null', () => {
    const { darkMode, userLocation } = useAppStore.getState()
    expect(darkMode).toBe(false)
    expect(userLocation).toBeNull()
  })

  it('debería activar darkMode al llamar toggleDarkMode()', () => {
    act(() => {
      useAppStore.getState().toggleDarkMode()
    })
    expect(useAppStore.getState().darkMode).toBe(true)
  })

  it('debería alternar darkMode en cada llamada a toggleDarkMode()', () => {
    act(() => {
      useAppStore.getState().toggleDarkMode()
      useAppStore.getState().toggleDarkMode()
      useAppStore.getState().toggleDarkMode()
    })
    expect(useAppStore.getState().darkMode).toBe(true)
  })

  it('debería establecer la ubicación del usuario con setUserLocation()', () => {
    const location = { lat: -33.4569, lng: -70.6483 }
    act(() => {
      useAppStore.getState().setUserLocation(location)
    })
    const { userLocation } = useAppStore.getState()
    expect(userLocation).toEqual(location)
    expect(userLocation?.lat).toBe(-33.4569)
    expect(userLocation?.lng).toBe(-70.6483)
  })

  it('debería actualizar la ubicación al llamar setUserLocation() de nuevo', () => {
    const ubicacion1 = { lat: -33.4569, lng: -70.6483 }
    const ubicacion2 = { lat: -33.5000, lng: -70.7000 }
    act(() => {
      useAppStore.getState().setUserLocation(ubicacion1)
      useAppStore.getState().setUserLocation(ubicacion2)
    })
    expect(useAppStore.getState().userLocation).toEqual(ubicacion2)
  })

  it('debería mantener darkMode al actualizar ubicación', () => {
    act(() => {
      useAppStore.getState().toggleDarkMode()
      useAppStore.getState().setUserLocation({ lat: -33.0, lng: -70.0 })
    })
    expect(useAppStore.getState().darkMode).toBe(true)
    expect(useAppStore.getState().userLocation).not.toBeNull()
  })
})
