import { NavLink } from 'react-router-dom'

const links = [
  { to: '/dashboard',  label: 'Dashboard',   icon: '📊' },
  { to: '/ventas',     label: 'Ventas',       icon: '🛒' },
  { to: '/clientes',   label: 'Clientes',     icon: '👥' },
  { to: '/inventario', label: 'Inventario',   icon: '📦' },
  { to: '/precios',    label: 'Precios',      icon: '💰' },
  { to: '/reportes',   label: 'Reportes',     icon: '📈' },
]

export default function Sidebar() {
  return (
    <aside className="w-60 min-h-screen bg-slate-900 flex flex-col fixed top-0 left-0 z-30">
      {/* Logo */}
      <div className="flex items-center gap-3 px-5 py-6 border-b border-slate-700">
        <span className="text-3xl">🥚</span>
        <div>
          <p className="text-white font-bold text-sm leading-tight">Distribuidora</p>
          <p className="text-amber-400 font-semibold text-xs">de Huevos</p>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 py-4 px-3 space-y-1">
        {links.map(({ to, label, icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-2.5 rounded-lg text-sm font-medium transition-all duration-150 ${
                isActive
                  ? 'bg-amber-500 text-white shadow-md'
                  : 'text-slate-400 hover:bg-slate-800 hover:text-white'
              }`
            }
          >
            <span className="text-base">{icon}</span>
            {label}
          </NavLink>
        ))}
      </nav>

      {/* Footer */}
      <div className="px-5 py-4 border-t border-slate-700">
        <p className="text-slate-500 text-xs">© 2025 Distribuidora</p>
      </div>
    </aside>
  )
}
