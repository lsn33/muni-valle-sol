'use client'

import Link from 'next/link'
import { useRouter } from 'next/navigation'
import useAppStore from '@/store/useAppStore'
import useAuthStore from '@/store/useAuthStore'
import { logout as logoutService } from '@/services/authService'

const Header = () => {
  const router = useRouter()
  const { darkMode, toggleDarkMode } = useAppStore()
  const { usuario, isAuthenticated, logout } = useAuthStore()

  const handleLogout = async () => {
    try {
      await logoutService()
    } catch {
      // Si falla el BFF igual limpiamos el estado local
    } finally {
      logout()
      router.push('/login')
    }
  }

  return (
    <header className="w-full h-16 bg-gray-900 text-white flex items-center justify-between px-4 md:px-6 shadow-md">
      <div className="flex items-center gap-2 min-w-0">
        <span className="text-base md:text-xl font-bold truncate">
          Municipalidad Valle del Sol
        </span>
        <span className="hidden md:inline text-sm text-gray-300">
          | Gestion de Incendios
        </span>
      </div>

      
    </header>
  )
}

export default Header
