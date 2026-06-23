import { describe, it, expect } from 'vitest'
import { Alert } from '@/types/Alert'
import { Report, Location } from '@/types/Report'
import { User } from '@/types/User'

describe('Tipo Alert', () => {
  it('debería aceptar severidad ALTA', () => {
    const a: Alert = { id: '1', titulo: 'T', descripcion: 'D', severidad: 'ALTA', fecha: '2024-01-15' }
    expect(a.severidad).toBe('ALTA')
  })
  it('debería aceptar severidad MEDIA', () => {
    const a: Alert = { id: '2', titulo: 'T', descripcion: 'D', severidad: 'MEDIA', fecha: '2024-01-15' }
    expect(a.severidad).toBe('MEDIA')
  })
  it('debería aceptar severidad BAJA', () => {
    const a: Alert = { id: '3', titulo: 'T', descripcion: 'D', severidad: 'BAJA', fecha: '2024-01-15' }
    expect(a.severidad).toBe('BAJA')
  })
  it('debería tener todos los campos requeridos', () => {
    const a: Alert = { id: '1', titulo: 'T', descripcion: 'D', severidad: 'ALTA', fecha: '2024-01-15' }
    expect(a).toHaveProperty('id')
    expect(a).toHaveProperty('titulo')
    expect(a).toHaveProperty('severidad')
  })
})

describe('Tipo Report', () => {
  const ubicacion: Location = { id: 1, lat: -33.4569, lng: -70.6483, direccion: 'Cerro', comuna: 'Vitacura', region: 'Metropolitana' }
  const base = { id: 1, titulo: 'T', descripcion: 'D', emailUsuario: 'e@gmail.com', fechaCreacion: '2024' }

  it('debería aceptar ubicación válida', () => {
    const r: Report = { ...base, tipo: 'INCENDIO', estado: 'ACTIVO', ubicacion }
    expect(r.ubicacion?.comuna).toBe('Vitacura')
  })
  it('debería aceptar ubicación null', () => {
    const r: Report = { ...base, tipo: 'HUMO', estado: 'PENDIENTE', ubicacion: null }
    expect(r.ubicacion).toBeNull()
  })
  it('debería aceptar tipo INCENDIO', () => {
    const r: Report = { ...base, tipo: 'INCENDIO', estado: 'ACTIVO', ubicacion: null }
    expect(r.tipo).toBe('INCENDIO')
  })
  it('debería aceptar tipo HUMO', () => {
    const r: Report = { ...base, tipo: 'HUMO', estado: 'ACTIVO', ubicacion: null }
    expect(r.tipo).toBe('HUMO')
  })
  it('debería aceptar tipo SOSPECHOSO', () => {
    const r: Report = { ...base, tipo: 'SOSPECHOSO', estado: 'ACTIVO', ubicacion: null }
    expect(r.tipo).toBe('SOSPECHOSO')
  })
  it('debería aceptar estado ACTIVO', () => {
    const r: Report = { ...base, tipo: 'INCENDIO', estado: 'ACTIVO', ubicacion: null }
    expect(r.estado).toBe('ACTIVO')
  })
  it('debería aceptar estado EN_REVISION', () => {
    const r: Report = { ...base, tipo: 'INCENDIO', estado: 'EN_REVISION', ubicacion: null }
    expect(r.estado).toBe('EN_REVISION')
  })
  it('debería aceptar estado PENDIENTE', () => {
    const r: Report = { ...base, tipo: 'INCENDIO', estado: 'PENDIENTE', ubicacion: null }
    expect(r.estado).toBe('PENDIENTE')
  })
})

describe('Tipo User', () => {
  it('debería aceptar rol CIUDADANO', () => {
    const u: User = { id: 1, nombre: 'Juan', email: 'juan@gmail.com', rol: 'CIUDADANO' }
    expect(u.rol).toBe('CIUDADANO')
  })
  it('debería aceptar rol ADMIN', () => {
    const u: User = { id: 2, nombre: 'Admin', email: 'admin@gmail.com', rol: 'ADMIN' }
    expect(u.rol).toBe('ADMIN')
  })
  it('debería aceptar rol BRIGADISTA', () => {
    const u: User = { id: 3, nombre: 'Brig', email: 'brig@gmail.com', rol: 'BRIGADISTA' }
    expect(u.rol).toBe('BRIGADISTA')
  })
  it('debería tener id numérico', () => {
    const u: User = { id: 99, nombre: 'Test', email: 'test@gmail.com', rol: 'CIUDADANO' }
    expect(typeof u.id).toBe('number')
  })
})
