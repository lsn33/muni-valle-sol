import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import Footer from '@/components/Footer'

describe('Footer', () => {
  it('debería renderizar el footer correctamente', () => {
    render(<Footer />)
    expect(screen.getByRole('contentinfo')).toBeInTheDocument()
  })

  it('debería mostrar el nombre de la municipalidad', () => {
    render(<Footer />)
    expect(screen.getByText(/Municipalidad Valle del Sol/i)).toBeInTheDocument()
  })

  it('debería mostrar el nombre de la plataforma', () => {
    render(<Footer />)
    expect(screen.getByText(/Plataforma de Gestion de Incendios/i)).toBeInTheDocument()
  })

  it('debería tener fondo oscuro (bg-gray-900)', () => {
    render(<Footer />)
    expect(screen.getByRole('contentinfo').className).toContain('bg-gray-900')
  })
})
