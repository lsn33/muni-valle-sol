import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { act } from '@testing-library/react'
import ReportTable from '@/components/ReportTable'
import useAuthStore from '@/store/useAuthStore'
import * as reportService from '@/services/reportService'
import { Report } from '@/types/Report'

vi.mock('@/services/reportService', () => ({
  eliminarReporte: vi.fn(),
  actualizarReporte: vi.fn(),
}))

const mockReportes: Report[] = [
  {
    id: 1,
    titulo: 'Incendio cerro',
    descripcion: 'Fuego activo',
    tipo: 'INCENDIO',
    estado: 'ACTIVO',
    emailUsuario: 'juan@gmail.com',
    fechaCreacion: '2024-01-15T10:00:00',
    ubicacion: { id: 1, lat: -33.4, lng: -70.6, direccion: 'Cerro', comuna: 'Vitacura', region: 'Metropolitana' },
  },
  {
    id: 2,
    titulo: 'Humo zona sur',
    descripcion: 'Humo detectado',
    tipo: 'HUMO',
    estado: 'EN_REVISION',
    emailUsuario: 'maria@gmail.com',
    fechaCreacion: '2024-01-16T08:00:00',
    ubicacion: null,
  },
]

describe('ReportTable', () => {
  beforeEach(() => {
    act(() => {
      useAuthStore.setState({ usuario: null, isAuthenticated: false })
    })
    vi.clearAllMocks()
  })

  it('debería mostrar el título "Reportes de Incendios"', () => {
    render(<ReportTable reportes={mockReportes} />)
    expect(screen.getByText('Reportes de Incendios')).toBeInTheDocument()
  })

  it('debería mostrar mensaje vacío cuando no hay reportes', () => {
    render(<ReportTable reportes={[]} />)
    expect(screen.getByText('No hay reportes disponibles.')).toBeInTheDocument()
  })

  it('debería mostrar los títulos de los reportes', () => {
    render(<ReportTable reportes={mockReportes} />)
    expect(screen.getByText('Incendio cerro')).toBeInTheDocument()
    expect(screen.getByText('Humo zona sur')).toBeInTheDocument()
  })

  it('debería mostrar la comuna si existe', () => {
    render(<ReportTable reportes={mockReportes} />)
    expect(screen.getByText('Vitacura')).toBeInTheDocument()
  })

  it('debería mostrar "-" cuando la ubicación es null', () => {
    render(<ReportTable reportes={mockReportes} />)
    expect(screen.getByText('-')).toBeInTheDocument()
  })

  it('debería mostrar el estado del reporte', () => {
    render(<ReportTable reportes={mockReportes} />)
    expect(screen.getByText('ACTIVO')).toBeInTheDocument()
    expect(screen.getByText('EN_REVISION')).toBeInTheDocument()
  })

  it('debería aplicar clase roja al estado ACTIVO', () => {
    render(<ReportTable reportes={[mockReportes[0]]} />)
    expect(screen.getByText('ACTIVO').className).toContain('bg-red-100')
  })

  it('NO debería mostrar acciones para un CIUDADANO', () => {
    act(() => {
      useAuthStore.setState({ usuario: { id: 1, nombre: 'Juan', email: 'juan@gmail.com', rol: 'CIUDADANO' }, isAuthenticated: true })
    })
    render(<ReportTable reportes={mockReportes} />)
    expect(screen.queryByText('Editar')).not.toBeInTheDocument()
    expect(screen.queryByText('Eliminar')).not.toBeInTheDocument()
  })

  it('debería mostrar acciones para un ADMIN', () => {
    act(() => {
      useAuthStore.setState({ usuario: { id: 1, nombre: 'Admin', email: 'admin@gmail.com', rol: 'ADMIN' }, isAuthenticated: true })
    })
    render(<ReportTable reportes={mockReportes} />)
    expect(screen.getAllByText('Editar')).toHaveLength(2)
    expect(screen.getAllByText('Eliminar')).toHaveLength(2)
  })

  it('debería entrar en modo edición al hacer click en Editar', () => {
    act(() => {
      useAuthStore.setState({ usuario: { id: 1, nombre: 'Admin', email: 'admin@gmail.com', rol: 'ADMIN' }, isAuthenticated: true })
    })
    render(<ReportTable reportes={[mockReportes[0]]} />)
    fireEvent.click(screen.getByText('Editar'))
    expect(screen.getByDisplayValue('Incendio cerro')).toBeInTheDocument()
    expect(screen.getByText('Guardar')).toBeInTheDocument()
  })

  it('debería cancelar la edición al hacer click en Cancelar', () => {
    act(() => {
      useAuthStore.setState({ usuario: { id: 1, nombre: 'Admin', email: 'admin@gmail.com', rol: 'ADMIN' }, isAuthenticated: true })
    })
    render(<ReportTable reportes={[mockReportes[0]]} />)
    fireEvent.click(screen.getByText('Editar'))
    fireEvent.click(screen.getByText('Cancelar'))
    expect(screen.queryByText('Guardar')).not.toBeInTheDocument()
  })

  it('debería llamar a actualizarReporte() al guardar', async () => {
    vi.mocked(reportService.actualizarReporte).mockResolvedValueOnce({ ...mockReportes[0], titulo: 'Título editado' })
    act(() => {
      useAuthStore.setState({ usuario: { id: 1, nombre: 'Admin', email: 'admin@gmail.com', rol: 'ADMIN' }, isAuthenticated: true })
    })
    render(<ReportTable reportes={[mockReportes[0]]} />)
    fireEvent.click(screen.getByText('Editar'))
    fireEvent.change(screen.getByDisplayValue('Incendio cerro'), { target: { value: 'Título editado' } })
    fireEvent.click(screen.getByText('Guardar'))
    await waitFor(() => {
      expect(reportService.actualizarReporte).toHaveBeenCalledWith(1, 'Título editado')
    })
  })

  it('debería aceptar la prop "reports" además de "reportes"', () => {
    render(<ReportTable reports={mockReportes} />)
    expect(screen.getByText('Incendio cerro')).toBeInTheDocument()
  })
})
