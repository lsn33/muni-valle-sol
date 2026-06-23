'use client'

import { useState } from 'react'
import useAppStore from '@/store/useAppStore'
import useAuthStore from '@/store/useAuthStore'

const Sidebar = () => {
  const { darkMode } = useAppStore()
  const { usuario, isAuthenticated, logout } = useAuthStore()
  const [isOpen, setIsOpen] = useState(false)

  return (
    <>
      {/* Botón hamburguesa — solo mobile */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="md:hidden fixed top-4 left-4 z-50 w-8 h-8 flex flex-col items-center justify-center gap-1.5"
        aria-label="Abrir menu"
      >
        <span className={`block w-6 h-0.5 bg-white transition-all ${isOpen ? 'rotate-45 translate-y-2' : ''}`} />
        <span className={`block w-6 h-0.5 bg-white transition-all ${isOpen ? 'opacity-0' : ''}`} />
        <span className={`block w-6 h-0.5 bg-white transition-all ${isOpen ? '-rotate-45 -translate-y-2' : ''}`} />
      </button>

      {/* Overlay — solo mobile cuando está abierto */}
      {isOpen && (
        <div
          className="md:hidden fixed inset-0 bg-black/50 z-30"
          onClick={() => setIsOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside className={`
        fixed md:static top-0 left-0 z-40
        w-60 h-full bg-gray-800 text-white
        flex flex-col py-6 px-4 shadow-lg
        transition-transform duration-300
        ${isOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}
      `}>

        {/* Perfil superior */}
        <div className="flex flex-col items-center gap-3 border-b border-gray-700 pb-6 mb-6 mt-8 md:mt-0">
          {isAuthenticated ? (
            <>
              <div className="w-16 h-16 rounded-full bg-blue-600 flex items-center justify-center text-2xl font-bold shadow-lg">
                {usuario?.nombre?.charAt(0).toUpperCase()}
              </div>
              <div className="text-center">
                <p className="text-sm font-semibold">{usuario?.nombre}</p>
                <p className="text-xs text-gray-400">{usuario?.rol}</p>
              </div>
            </>
          ) : (
            <>
              <div className="w-16 h-16 rounded-full bg-gray-600 flex items-center justify-center shadow-lg">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="w-8 h-8 text-gray-300"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                >
                  <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"/>
                </svg>
              </div>
              <div className="text-center">
                <p className="text-sm text-gray-400">No has iniciado sesion</p>
              </div>
            </>
          )}
        </div>

        {/* Spacer */}
        <div className="flex-1" />

        {/* Cerrar sesion o iniciar sesion */}
        <div className="border-t border-gray-700 pt-4">
          {isAuthenticated ? (
            <button
              onClick={logout}
              className="w-full px-4 py-2 rounded-lg text-sm text-red-400 hover:bg-gray-700 transition text-left"
            >
              Cerrar sesion
            </button>
          ) : (
            <a
              href="/login"
              className="w-full block px-4 py-2 rounded-lg text-sm text-blue-400 hover:bg-gray-700 transition"
            >
              Iniciar sesion
            </a>
          )}
        </div>
      </aside>
    </>
  )
}

export default Sidebar