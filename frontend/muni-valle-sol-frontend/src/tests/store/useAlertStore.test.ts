import { describe, it, expect, beforeEach } from 'vitest'
import { act } from '@testing-library/react'
import useAlertStore from '@/store/useAlertStore'
import { Alert } from '@/types/Alert'

const mockAlerta: Alert = {
  id: '1',
  titulo: 'Incendio en Sector Norte',
  descripcion: 'Se reporta humo en el cerro',
  severidad: 'ALTA',
  fecha: '2024-01-15',
}

const mockAlertaMedia: Alert = {
  id: '2',
  titulo: 'Alerta de Humo',
  descripcion: 'Humo detectado en zona residencial',
  severidad: 'MEDIA',
  fecha: '2024-01-15',
}

describe('useAlertStore', () => {
  beforeEach(() => {
    act(() => {
      useAlertStore.setState({ alertasActivas: [] })
    })
  })

  it('debería iniciar con alertas vacías', () => {
    const { alertasActivas } = useAlertStore.getState()
    expect(alertasActivas).toEqual([])
    expect(alertasActivas).toHaveLength(0)
  })

  it('debería establecer múltiples alertas con setAlertasActivas()', () => {
    act(() => {
      useAlertStore.getState().setAlertasActivas([mockAlerta, mockAlertaMedia])
    })
    const { alertasActivas } = useAlertStore.getState()
    expect(alertasActivas).toHaveLength(2)
    expect(alertasActivas[0]).toEqual(mockAlerta)
    expect(alertasActivas[1]).toEqual(mockAlertaMedia)
  })

  it('debería agregar una alerta individual con agregarAlerta()', () => {
    act(() => {
      useAlertStore.getState().agregarAlerta(mockAlerta)
    })
    const { alertasActivas } = useAlertStore.getState()
    expect(alertasActivas).toHaveLength(1)
    expect(alertasActivas[0]).toEqual(mockAlerta)
  })

  it('debería acumular alertas al llamar agregarAlerta() múltiples veces', () => {
    act(() => {
      useAlertStore.getState().agregarAlerta(mockAlerta)
      useAlertStore.getState().agregarAlerta(mockAlertaMedia)
    })
    const { alertasActivas } = useAlertStore.getState()
    expect(alertasActivas).toHaveLength(2)
  })

  it('debería limpiar todas las alertas con limpiarAlertas()', () => {
    act(() => {
      useAlertStore.getState().setAlertasActivas([mockAlerta, mockAlertaMedia])
      useAlertStore.getState().limpiarAlertas()
    })
    expect(useAlertStore.getState().alertasActivas).toHaveLength(0)
  })

  it('debería reemplazar alertas al llamar setAlertasActivas() sobre alertas existentes', () => {
    act(() => {
      useAlertStore.getState().setAlertasActivas([mockAlerta])
      useAlertStore.getState().setAlertasActivas([mockAlertaMedia])
    })
    const { alertasActivas } = useAlertStore.getState()
    expect(alertasActivas).toHaveLength(1)
    expect(alertasActivas[0].id).toBe('2')
  })

  it('debería no mutar el array al agregar una alerta (inmutabilidad)', () => {
    act(() => {
      useAlertStore.getState().setAlertasActivas([mockAlerta])
    })
    const antesRef = useAlertStore.getState().alertasActivas
    act(() => {
      useAlertStore.getState().agregarAlerta(mockAlertaMedia)
    })
    const despuesRef = useAlertStore.getState().alertasActivas
    expect(antesRef).not.toBe(despuesRef)
  })
})
