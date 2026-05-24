import { useState } from 'react'
import { Outlet } from 'react-router-dom'
import Sidebar from './Sidebar'

export default function Layout() {
  const [collapsed, setCollapsed]   = useState(false)
  const [mobileOpen, setMobileOpen] = useState(false)

  return (
    <div className="min-h-screen bg-slate-50">
      <Sidebar
        collapsed={collapsed}
        onToggle={() => setCollapsed(c => !c)}
        mobileOpen={mobileOpen}
        onClose={() => setMobileOpen(false)}
      />

      {/* Top bar móvil */}
      <div className="lg:hidden fixed top-0 left-0 right-0 z-30 bg-slate-900 flex items-center gap-3 px-4 h-14 shadow-lg">
        <button
          onClick={() => setMobileOpen(true)}
          className="text-slate-300 hover:text-white p-1"
          aria-label="Abrir menú"
        >
          <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
            <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
        <span className="text-2xl">🥚</span>
        <div>
          <p className="text-white font-bold text-sm leading-tight">Distribuidora</p>
          <p className="text-amber-400 font-semibold text-xs">de Huevos</p>
        </div>
      </div>

      {/* Contenido principal */}
      <main
        className={`
          transition-all duration-300 ease-in-out
          min-h-screen p-4 md:p-6
          pt-20 lg:pt-6
          ${collapsed ? 'lg:ml-16' : 'lg:ml-60'}
        `}
      >
        <Outlet />
      </main>
    </div>
  )
}
