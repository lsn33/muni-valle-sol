export interface Location {
  id: number
  lat: number
  lng: number
  direccion: string
  comuna: string
  region: string
}

export interface Report {
  id: number
  titulo: string
  descripcion: string
  tipo: 'INCENDIO' | 'HUMO' | 'SOSPECHOSO'
  estado: 'ACTIVO' | 'EN_REVISION' | 'PENDIENTE'
  emailUsuario: string
  fechaCreacion: string
  ubicacion: Location | null
}