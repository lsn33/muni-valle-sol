import { useEffect } from 'react'
import useAppStore from '@/store/useAppStore'

const useUserLocation = () => {
  const { userLocation, setUserLocation } = useAppStore()

  useEffect(() => {
    if (!navigator.geolocation) return

    const watcher = navigator.geolocation.watchPosition(
      (position) => {
        setUserLocation({
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        })
      },
      (error) => {
        console.error('Error al obtener ubicacion:', error)
      }
    )

    return () => navigator.geolocation.clearWatch(watcher)
  }, [])

  return { userLocation }
}

export default useUserLocation
