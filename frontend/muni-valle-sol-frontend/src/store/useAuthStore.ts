import { create } from 'zustand'
import { User } from '@/types/User'

interface AuthState {
  usuario: User | null
  isAuthenticated: boolean
  login: (usuario: User) => void
  logout: () => void
}

const useAuthStore = create<AuthState>((set) => ({
  usuario: null,
  isAuthenticated: false,
  login: (usuario) => set({ usuario, isAuthenticated: true }),
  logout: () => set({ usuario: null, isAuthenticated: false }),
}))

export default useAuthStore
