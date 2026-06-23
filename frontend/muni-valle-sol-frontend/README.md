# Frontend — Municipalidad Valle del Sol

Aplicación web responsiva para la plataforma de gestión de incendios de la Municipalidad Valle del Sol. Permite a ciudadanos reportar incendios, visualizarlos en un mapa interactivo y a funcionarios gestionar alertas y brigadas.

## Tabla Técnica

| Ítem | Detalle |
|---|---|
| Lenguaje | TypeScript 5 |
| Framework | Next.js 16.2.4 |
| UI Library | React 19 |
| Estilos | TailwindCSS 4 |
| Mapas | MapLibre GL 5 + React Leaflet |
| Estado global | Zustand 5 |
| Validación | Zod 4 |
| Testing | Vitest 3 + Testing Library + jsdom |
| Puerto | 3000 |
| Patrones de diseño | Singleton (Zustand stores), Facade (servicios API) |

## Librerías principales

- `next:16.2.4`
- `react:19.2.4`
- `maplibre-gl:5.24.0`
- `react-leaflet:5.0.0`
- `zustand:5.0.14`
- `zod:4.4.3`
- `tailwindcss:4`
- `vitest:3.2.4`
- `@testing-library/react:16.3.0`

## Requisitos

- Node.js 20+
- npm

## Instalación y ejecución

```bash
# Clonar el repositorio
git clone https://github.com/JOAKOO123/muni-valle-sol-frontend.git
cd muni-valle-sol-frontend

# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm run dev
```

La aplicación estará disponible en `http://localhost:3000`

## Variables de entorno

Crea un archivo `.env.local` en la raíz del proyecto:

```env
NEXT_PUBLIC_BFF_URL=http://localhost:8080
```

## Scripts disponibles

```bash
npm run dev          # Servidor de desarrollo
npm run build        # Build de producción
npm run start        # Servidor de producción
npm run lint         # Linter ESLint
npm test             # Ejecutar tests
npm run test:watch   # Tests en modo watch
npm run test:coverage # Tests con reporte de cobertura
```

## Ejecutar pruebas y cobertura

```bash
# Ejecutar todos los tests
npm test

# Generar reporte de cobertura
npm run test:coverage
```

## Docker

```bash
# Build
docker build \
  --build-arg NEXT_PUBLIC_BFF_URL=http://localhost:8080 \
  -t frontend .

# Run
docker run -p 3000:3000 frontend
```

## Estructura principal

```
src/
├── app/          # Rutas Next.js (App Router)
├── components/   # Componentes React reutilizables
├── services/     # Clientes HTTP hacia el BFF
├── stores/       # Estado global con Zustand
└── types/        # Tipos TypeScript
```