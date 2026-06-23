export interface Alert {
  id: string;
  titulo: string;
  descripcion: string;
  severidad: 'ALTA' | 'MEDIA' | 'BAJA';
  fecha: string;
  latitud?: number | null;
  longitud?: number | null;
}