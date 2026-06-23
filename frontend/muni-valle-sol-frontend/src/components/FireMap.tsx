'use client'

import { useEffect, useRef } from 'react'
import maplibregl from 'maplibre-gl'
import 'maplibre-gl/dist/maplibre-gl.css'
import { Alert } from '@/types/Alert'
import useUserLocation from '@/hooks/useUserLocation'

interface FireMapProps {
  alerts: Alert[]
}

const FireMap = ({ alerts }: FireMapProps) => {
  const mapContainer = useRef<HTMLDivElement>(null)
  const map = useRef<maplibregl.Map | null>(null)
  const userMarker = useRef<maplibregl.Marker | null>(null)
  const mapReady = useRef(false)
  const { userLocation } = useUserLocation()

  // Inicializar mapa
  useEffect(() => {
    if (!mapContainer.current) return

    map.current = new maplibregl.Map({
      container: mapContainer.current,
      style: 'https://basemaps.cartocdn.com/gl/positron-gl-style/style.json',
      center: [-70.6483, -33.4569],
      zoom: 11,
    })

    map.current.addControl(new maplibregl.NavigationControl(), 'top-right')

    map.current.on('load', () => {
      mapReady.current = true
    })

    return () => {
      userMarker.current?.remove()
      map.current?.remove()
    }
  }, [])

  // Marcador de ubicacion del usuario
  useEffect(() => {
    if (!map.current || !userLocation) return

    // Crear elemento visual del marcador (punto azul pulsante)
    const el = document.createElement('div')
    el.style.cssText = `
      width: 20px;
      height: 20px;
      border-radius: 50%;
      background: #3B82F6;
      border: 3px solid white;
      box-shadow: 0 0 0 2px #3B82F6;
      animation: pulse-user 2s ease-in-out infinite;
    `

    // Inyectar keyframes si no existen
    if (!document.getElementById('user-marker-style')) {
      const style = document.createElement('style')
      style.id = 'user-marker-style'
      style.textContent = `
        @keyframes pulse-user {
          0%   { box-shadow: 0 0 0 2px rgba(59,130,246,0.8); }
          50%  { box-shadow: 0 0 0 10px rgba(59,130,246,0); }
          100% { box-shadow: 0 0 0 2px rgba(59,130,246,0.8); }
        }
      `
      document.head.appendChild(style)
    }

    // Eliminar marcador anterior si existe
    userMarker.current?.remove()

    userMarker.current = new maplibregl.Marker({ element: el, anchor: 'center' })
      .setLngLat([userLocation.lng, userLocation.lat])
      .setPopup(
        new maplibregl.Popup({ offset: 16 }).setHTML(
          '<strong>Tu ubicación</strong>'
        )
      )
      .addTo(map.current)

    // Volar a la ubicacion del usuario
    map.current.flyTo({
      center: [userLocation.lng, userLocation.lat],
      zoom: 13,
      speed: 1.5,
    })
  }, [userLocation])

  // Agregar circulos de alertas en el mapa
  useEffect(() => {
    if (!map.current) return

    const agregarCirculos = () => {
      if (!map.current) return

      // Eliminar capas anteriores si existen
      if (map.current.getSource('incendios')) {
        map.current.removeLayer('incendios-circle')
        map.current.removeLayer('incendios-pulse')
        map.current.removeSource('incendios')
      }

      const geojson: GeoJSON.FeatureCollection = {
        type: 'FeatureCollection',
        features: alerts
          .filter((a) => a.latitud != null && a.longitud != null)
          .map((a) => ({
            type: 'Feature',
            geometry: {
              type: 'Point',
              coordinates: [a.longitud!, a.latitud!],
            },
            properties: {
              titulo: a.titulo,
              severidad: a.severidad,
            },
          })),
      }

      map.current.addSource('incendios', {
        type: 'geojson',
        data: geojson,
      })

      // Circulo exterior (efecto pulso)
      map.current.addLayer({
        id: 'incendios-pulse',
        type: 'circle',
        source: 'incendios',
        paint: {
          'circle-radius': 20,
          'circle-color': '#FF0000',
          'circle-opacity': 0.2,
          'circle-stroke-width': 0,
        },
      })

      // Circulo principal rojo
      map.current.addLayer({
        id: 'incendios-circle',
        type: 'circle',
        source: 'incendios',
        paint: {
          'circle-radius': 10,
          'circle-color': '#FF0000',
          'circle-opacity': 0.85,
          'circle-stroke-width': 2,
          'circle-stroke-color': '#CC0000',
        },
      })

      // Popup al hacer click
      map.current.on('click', 'incendios-circle', (e) => {
        if (!e.features || !e.features[0]) return
        const props = e.features[0].properties
        const coords = (e.features[0].geometry as GeoJSON.Point).coordinates

        new maplibregl.Popup()
          .setLngLat([coords[0], coords[1]])
          .setHTML(`
            <strong>${props.titulo}</strong><br/>
            Severidad: ${props.severidad}
          `)
          .addTo(map.current!)
      })

      map.current.on('mouseenter', 'incendios-circle', () => {
        if (map.current) map.current.getCanvas().style.cursor = 'pointer'
      })

      map.current.on('mouseleave', 'incendios-circle', () => {
        if (map.current) map.current.getCanvas().style.cursor = ''
      })
    }

    if (map.current.isStyleLoaded()) {
      agregarCirculos()
    } else {
      map.current.on('load', agregarCirculos)
    }
  }, [alerts])

  return (
    <div
      ref={mapContainer}
      style={{ width: '100%', height: '100%', minHeight: '500px' }}
    />
  )
}

export default FireMap