# Municipalidad Valle del Sol — Plataforma de Gestión de Incendios

Plataforma de microservicios para que la Municipalidad Valle del Sol digitalice el reporte de incendios, el monitoreo geográfico de focos y brigadas, y el envío de alertas a la comunidad. Reemplaza el flujo actual basado en llamadas telefónicas y redes sociales por un sistema con trazabilidad, ubicación en tiempo real y comunicación oficial.

Desarrollado para la asignatura Desarrollo Fullstack III (DSY1106), Duoc UC.

## Estructura del repositorio

```
/backend
  /bff              → Backend For Frontend (Spring Boot)
  /ms-usuarios      → Autenticación y roles (JWT RSA RS256)
  /ms-reportes      → Registro de reportes de incendios
  /ms-brigadas      → Gestión de brigadas de emergencia
  /ms-alertas       → Alertas a la comunidad (MongoDB)
/frontend           → Aplicación Next.js (mapa, dashboard, reportes)
/docs
  /diagramas        → C1, C2, C3
  report.pdf        → Informe técnico
  presentation.pdf  → Presentación
  caso-estudio.pdf  → Caso de estudio original
```

## Repositorios originales (historial Git Flow completo)

| Componente | Repositorio |
|---|---|
| BFF | github.com/JOAKOO123/muni-valle-sol-bff |
| Frontend | github.com/JOAKOO123/muni-valle-sol-frontend |
| MS-Alertas | github.com/JOAKOO123/muni-valle-sol-ms-alertas |
| MS-Usuarios | github.com/lsn33/muni-valle-sol-ms-usuarios |
| MS-Reportes | github.com/lsn33/muni-valle-sol-ms-reportes |
| MS-Brigadas | github.com/lsn33/muni-valle-sol-ms-brigadas |

Este repositorio reorganiza esos 6 componentes en la estructura de entrega exigida. El desarrollo de cada uno, con su flujo de ramas y pull requests, vive en su repositorio original.

## Cómo correr el proyecto

**Local (Docker Compose):**
```bash
cd backend/bff
docker compose up --build
```

**Kubernetes:**
```bash
kubectl apply -f k8s/ -n muni-valle-sol
```

## Stack principal

Spring Boot · Java 21 · Next.js · KrakenD · NeonDB (PostgreSQL) · MongoDB Atlas · Docker · Kubernetes