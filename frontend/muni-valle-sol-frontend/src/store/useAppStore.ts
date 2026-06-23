import { create } from 'zustand'

interface UserLocation {
  lat: number
  lng: number
}

interface AppState {
  darkMode: boolean
  toggleDarkMode: () => void
  userLocation: UserLocation | null
  setUserLocation: (location: UserLocation) => void
}

const useAppStore = create<AppState>((set) => ({
  darkMode: false,
  toggleDarkMode: () => set((state) => ({ darkMode: !state.darkMode })),
  userLocation: null,
  setUserLocation: (location) => set({ userLocation: location }),
}))

export default useAppStore
