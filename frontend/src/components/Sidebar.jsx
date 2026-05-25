import { NavLink, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const links = [
  { to: '/dashboard',  label: 'Dashboard',  icon: '📊' },
  { to: '/ventas',     label: 'Ventas',      icon: '🛒' },
  { to: '/clientes',   label: 'Clientes',    icon: '👥' },
  { to: '/inventario', label: 'Inventario',  icon: '📦' },
  { to: '/precios',    label: 'Precios',     icon: '💰' },
  { to: '/deudores',   label: 'Deudores',    icon: '🏦' },
  { to: '/reportes',   label: 'Reportes',    icon: '📈' },
]

export default function Sidebar({ collapsed, onToggle, mobileOpen, onClose }) {
  const { username, cerrarSesion } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const handleLogout = () => {
    cerrarSesion()
    navigate('/login', { replace: true })
  }

  return (
    <>
      {/* Backdrop móvil */}
      <div
        onClick={onClose}
        className={`fixed inset-0 bg-black/50 z-40 lg:hidden transition-opacity duration-300 ${
          mobileOpen ? 'opacity-100 pointer-events-auto' : 'opacity-0 pointer-events-none'
        }`}
      />

      {/* Sidebar */}
      <aside
        className={`
          fixed top-0 left-0 h-full bg-slate-900 flex flex-col z-50
          transition-all duration-300 ease-in-out
          ${collapsed ? 'lg:w-16' : 'lg:w-60'}
          w-64
          ${mobileOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
        `}
      >
        {/* Logo */}
        <div className={`flex items-center border-b border-slate-700 flex-shrink-0 ${
          collapsed ? 'lg:justify-center lg:px-3 px-5 py-5' : 'gap-3 px-5 py-5'
        }`}>
          <span className="text-3xl flex-shrink-0">🥚</span>
          <div className={`${collapsed ? 'lg:hidden' : ''}`}>
            <p className="text-white font-bold text-sm leading-tight">La Golondrina</p>
            <p className="text-amber-400 font-semibold text-xs">Dist. de Huevos</p>
          </div>
          {/* Botón cerrar — solo móvil */}
          <button
            onClick={onClose}
            className="ml-auto text-slate-400 hover:text-white lg:hidden p-1"
            aria-label="Cerrar menú"
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Nav */}
        <nav className="flex-1 py-4 px-2 space-y-1 overflow-y-auto">
          {links.map(({ to, label, icon }) => (
            <NavLink
              key={to}
              to={to}
              onClick={onClose}
              title={collapsed ? label : undefined}
              aria-current={location.pathname === to ? 'page' : undefined}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-lg text-sm font-medium transition-all duration-150
                ${collapsed ? 'lg:justify-center lg:px-0 px-3 py-2.5' : 'px-3 py-2.5'}
                ${isActive
                  ? 'bg-amber-500 text-white shadow-md'
                  : 'text-slate-400 hover:bg-slate-800 hover:text-white'
                }`
              }
            >
              <span className="text-lg flex-shrink-0">{icon}</span>
              <span className={`${collapsed ? 'lg:hidden' : ''}`}>{label}</span>
            </NavLink>
          ))}
        </nav>

        {/* Footer */}
        <div className="border-t border-slate-700 flex-shrink-0">
          {/* Usuario + logout */}
          <div className={`px-3 py-2 ${collapsed ? 'lg:hidden' : ''}`}>
            <div className="flex items-center justify-between gap-2 px-2 py-2 rounded-lg bg-slate-800">
              <div className="flex items-center gap-2 min-w-0">
                <span className="text-base flex-shrink-0">👤</span>
                <span className="text-slate-300 text-xs font-medium truncate">{username}</span>
              </div>
              <button
                onClick={handleLogout}
                title="Cerrar sesión"
                className="text-slate-400 hover:text-red-400 transition-colors flex-shrink-0 p-1"
              >
                <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round"
                    d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
              </button>
            </div>
          </div>

          {/* Logout solo ícono cuando colapsado */}
          <button
            onClick={handleLogout}
            title="Cerrar sesión"
            className={`${collapsed ? 'lg:flex' : 'hidden'} hidden w-full items-center justify-center py-3 text-slate-400 hover:text-red-400 hover:bg-slate-800 transition-colors`}
          >
            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
              <path strokeLinecap="round" strokeLinejoin="round"
                d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
          </button>

          {/* Toggle colapsar — solo desktop */}
          <button
            onClick={onToggle}
            title={collapsed ? 'Expandir menú' : 'Colapsar menú'}
            className="hidden lg:flex w-full items-center gap-3 px-4 py-3 text-slate-400 hover:text-white hover:bg-slate-800 transition-colors text-sm border-t border-slate-700"
          >
            <svg
              className={`w-4 h-4 flex-shrink-0 transition-transform duration-300 ${collapsed ? 'rotate-180' : ''}`}
              fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}
            >
              <path strokeLinecap="round" strokeLinejoin="round" d="M11 19l-7-7 7-7M18 19l-7-7 7-7" />
            </svg>
            <span className={`${collapsed ? 'lg:hidden' : ''}`}>Colapsar menú</span>
          </button>
        </div>
      </aside>
    </>
  )
}
