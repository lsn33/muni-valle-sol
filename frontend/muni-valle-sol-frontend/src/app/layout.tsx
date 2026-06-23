import type { Metadata } from 'next'
import 'leaflet/dist/leaflet.css'
import './globals.css'
import Header from '@/components/Header'
import Footer from '@/components/Footer'

export const metadata: Metadata = {
  title: 'Municipalidad Valle del Sol',
  description: 'Plataforma de Gestion de Incendios',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="es">
      <body className="flex flex-col min-h-screen bg-gray-100 dark:bg-gray-900">
        <Header />
        <div className="flex flex-1 overflow-hidden">
          {children}
        </div>
        <Footer />
      </body>
    </html>
  )
}