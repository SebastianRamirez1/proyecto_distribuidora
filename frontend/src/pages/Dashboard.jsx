import { useEffect, useState } from 'react'
import { obtenerInventario } from '../api/inventarioApi'
import { reporteCajaHoy } from '../api/reportesApi'
import { ventasHoy } from '../api/ventasApi'
import Card from '../components/ui/Card'
import Spinner from '../components/ui/Spinner'
import Alert from '../components/ui/Alert'
import Badge from '../components/ui/Badge'

function StatCard({ icon, label, value, sub, color = 'amber' }) {
  const colors = {
    amber:   'from-amber-400 to-amber-600',
    emerald: 'from-emerald-400 to-emerald-600',
    blue:    'from-blue-400 to-blue-600',
    rose:    'from-rose-400 to-rose-600',
    purple:  'from-purple-400 to-purple-600',
  }
  return (
    <div className={`bg-gradient-to-br ${colors[color]} rounded-xl p-5 text-white shadow-md`}>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-white/80 text-xs font-medium uppercase tracking-wide mb-1">{label}</p>
          <p className="text-2xl font-bold">{value}</p>
          {sub && <p className="text-white/70 text-xs mt-1">{sub}</p>}
        </div>
        <span className="text-3xl opacity-80">{icon}</span>
      </div>
    </div>
  )
}

const fmt = (n) => n != null ? `S/ ${Number(n).toFixed(2)}` : 'S/ 0.00'

export default function Dashboard() {
  const [inventario, setInventario] = useState(null)
  const [caja, setCaja] = useState(null)
  const [ventas, setVentas] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const [inv, rep, v] = await Promise.all([
          obtenerInventario(),
          reporteCajaHoy(),
          ventasHoy(),
        ])
        setInventario(inv)
        setCaja(rep)
        setVentas(v)
      } catch (e) {
        setError(e.message)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  if (loading) return <Spinner />

  const tipoPagoColor = { EFECTIVO: 'emerald', TRANSFERENCIA: 'blue', FIADO: 'rose', ABONO: 'purple' }
  const tipoColor = { EXTRA: 'amber', NORMAL: 'slate' }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Dashboard</h1>
        <p className="text-slate-500 text-sm mt-1">Resumen del día de hoy</p>
      </div>

      <Alert type="error" message={error} onClose={() => setError('')} />

      {/* Inventario */}
      <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-3">📦 Stock actual</h2>
      <div className="grid grid-cols-2 gap-4 mb-6">
        <StatCard icon="🥚" label="Canastas Extra" value={inventario?.stockExtra ?? 0} color="amber" />
        <StatCard icon="🥚" label="Canastas Normal" value={inventario?.stockNormal ?? 0} color="blue" />
      </div>

      {/* Caja */}
      <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-3">💰 Caja de hoy</h2>
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <StatCard icon="💵" label="Efectivo" value={fmt(caja?.totalEfectivo)} color="emerald" />
        <StatCard icon="📲" label="Transferencia" value={fmt(caja?.totalTransferencia)} color="blue" />
        <StatCard icon="📋" label="Fiado" value={fmt(caja?.totalFiado)} color="rose" />
        <StatCard icon="💳" label="Abonos" value={fmt(caja?.totalAbonos)} color="purple" />
      </div>
      <div className="grid grid-cols-1 gap-4 mb-8">
        <StatCard icon="🏦" label="Total cobrado hoy" value={fmt(caja?.totalCobrado)} sub="Efectivo + Transferencia + Abonos" color="amber" />
      </div>

      {/* Últimas ventas */}
      <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-3">🛒 Ventas de hoy ({ventas.length})</h2>
      <Card className="p-0 overflow-hidden">
        {ventas.length === 0 ? (
          <p className="text-slate-400 text-sm text-center py-8">No hay ventas registradas hoy</p>
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="table-head">
                <th className="px-4 py-3 text-left">Cliente</th>
                <th className="px-4 py-3 text-left">Tipo</th>
                <th className="px-4 py-3 text-right">Cant.</th>
                <th className="px-4 py-3 text-right">P/U</th>
                <th className="px-4 py-3 text-right">Total</th>
                <th className="px-4 py-3 text-left">Pago</th>
                <th className="px-4 py-3 text-left">Hora</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {ventas.map((v) => (
                <tr key={v.id} className="hover:bg-slate-50">
                  <td className="table-cell font-medium">{v.nombreCliente}</td>
                  <td className="table-cell">
                    <Badge color={tipoColor[v.tipoProducto]}>{v.tipoProducto}</Badge>
                  </td>
                  <td className="table-cell text-right">{v.cantidad}</td>
                  <td className="table-cell text-right">{fmt(v.precioUnitario)}</td>
                  <td className="table-cell text-right font-semibold">{fmt(v.total)}</td>
                  <td className="table-cell">
                    <Badge color={tipoPagoColor[v.tipoPago]}>{v.tipoPago}</Badge>
                  </td>
                  <td className="table-cell text-slate-400">
                    {v.fecha ? new Date(v.fecha).toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' }) : '-'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  )
}
