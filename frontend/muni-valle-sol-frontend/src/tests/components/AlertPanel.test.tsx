import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import AlertPanel from '@/components/AlertPanel'
import { Alert } from '@/types/Alert'

const mockAlertas: Alert[] = [
  { id: '1', titulo: 'Incendio en Sector Norte', descripcion: 'Se reporta fuego activo en el cerro', severidad: 'ALTA', fecha: '2024-01-15' },
  { id: '2', titulo: 'Alerta de Humo', descripcion: 'Humo detectado en zona residencial', severidad: 'MEDIA', fecha: '2024-01-15' },
  { id: '3', titulo: 'Revisión preventiva', descripcion: 'Zona bajo observación', severidad: 'BAJA', fecha: '2024-01-15' },
]

describe('AlertPanel', () => {
  it('debería mostrar el título "Alertas Activas"', () => {
    render(<AlertPanel alerts={[]} />)
    expect(screen.getByText('Alertas Activas')).toBeInTheDocument()
  })

  it('debería mostrar mensaje vacío cuando no hay alertas', () => {
    render(<AlertPanel alerts={[]} />)
    expect(screen.getByText('No hay alertas activas en este momento.')).toBeInTheDocument()
  })

  it('debería renderizar el listado de alertas', () => {
    render(<AlertPanel alerts={mockAlertas} />)
    expect(screen.getByText('Incendio en Sector Norte')).toBeInTheDocument()
    expect(screen.getByText('Alerta de Humo')).toBeInTheDocument()
    expect(screen.getByText('Revisión preventiva')).toBeInTheDocument()
  })

  it('debería mostrar la descripción de cada alerta', () => {
    render(<AlertPanel alerts={mockAlertas} />)
    expect(screen.getByText('Se reporta fuego activo en el cerro')).toBeInTheDocument()
    expect(screen.getByText('Humo detectado en zona residencial')).toBeInTheDocument()
  })

  it('debería mostrar la fecha de cada alerta', () => {
    render(<AlertPanel alerts={[mockAlertas[0]]} />)
    expect(screen.getByText('2024-01-15')).toBeInTheDocument()
  })

  it('debería aplicar clase roja para severidad ALTA', () => {
    render(<AlertPanel alerts={[mockAlertas[0]]} />)
    const tarjeta = screen.getByText('Incendio en Sector Norte').closest('div')
    expect(tarjeta?.className).toContain('border-red-500')
  })

  it('debería aplicar clase amarilla para severidad MEDIA', () => {
    render(<AlertPanel alerts={[mockAlertas[1]]} />)
    const tarjeta = screen.getByText('Alerta de Humo').closest('div')
    expect(tarjeta?.className).toContain('border-yellow-500')
  })

  it('debería aplicar clase verde para severidad BAJA', () => {
    render(<AlertPanel alerts={[mockAlertas[2]]} />)
    const tarjeta = screen.getByText('Revisión preventiva').closest('div')
    expect(tarjeta?.className).toContain('border-green-500')
  })

  it('no debería mostrar el mensaje vacío cuando hay alertas', () => {
    render(<AlertPanel alerts={mockAlertas} />)
    expect(screen.queryByText('No hay alertas activas en este momento.')).not.toBeInTheDocument()
  })
})
