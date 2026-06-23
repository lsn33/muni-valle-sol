export interface User {
  id: number;
  nombre: string;
  email: string;
  rol: 'ADMIN' | 'BRIGADISTA' | 'CIUDADANO';
}
