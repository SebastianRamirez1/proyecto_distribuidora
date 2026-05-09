import { useEffect, useState } from 'react'
import { reporteCajaHoy } from '../api/reportesApi'
import { ventasHoy } from '../api/ventasApi'
import Card from '../components/ui/Card'
import Alert from '../components/ui/Alert'
import Badge from '../components/ui/Badge'
import Spinner from '../components/ui/Spinner'

const fmt = (n) => `S/ ${Number(n ?? 0).toFixed(2)}`

function CajaRow({ icon, label, value, highlight = false }) {
  return (
    <div className={`flex items-center justify-between py-3 ${highlight ? 'border-t-2 border-slate-200 mt-2 pt-4' : 'border-b border-slate-100'}`}>
      <div className="flex items-center gap-2 text-slate-600">
        <span className="text-lg">{icon}</span>
        <span className={`text-sm ${highlight ? 'font-semibold text-slate-800' : ''}`}>{label}</span>
      </div>
      <span className={`font-bold ${highlight ? 'text-xl text-amber-600' : 'text-slate-800'}`}>{fmt(value)}</span>
    </div>
  )
}

const tipoPagoColor = { EFECTIVO: 'emerald', TRANSFERENCIA: 'blue', FIADO: 'rose', ABONO: 'purple' }
const tipoColor = { EXTRA: 'amber', NORMAL: 'slate' }

export default function Reportes() {
  const [caja, setCaja] = useState(null)
  const [ventas, setVentas] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const load = async () => {
      try {
        const [c, v] = await Promise.all([reporteCajaHoy(), ventasHoy()])
        setCaja(c)
        setVentas(v)
      } catch (e) {
        setError(e.message)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const today = new Date().toLocaleDateString('es-PE', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })

  // Stats from ventas
  const ventasExtra  = ventas.filter(v => v.tipoProducto === 'EXTRA')
  const ventasNormal = ventas.filter(v => v.tipoProducto === 'NORMAL')
  const totalCanastas = ventas.reduce((a, v) => a + v.cantidad, 0)

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Reporte del día</h1>
        <p className="text-slate-500 text-sm mt-1 capitalize">{today}</p>
      </div>

      <Alert type="error" message={error} onClose={() => setError('')} />

      {loading ? <Spinner /> : (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Caja */}
          <div>
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-3">💰 Cierre de caja</h2>
            <Card>
              <CajaRow icon="💵" label="Efectivo"       value={caja?.totalEfectivo} />
              <CajaRow icon="📲" label="Transferencia"  value={caja?.totalTransferencia} />
              <CajaRow icon="💳" label="Abonos recibidos" value={caja?.totalAbonos} />
              <CajaRow icon="📋" label="Fiado (pendiente)" value={caja?.totalFiado} />
              <CajaRow icon="🏦" label="Total cobrado" value={caja?.totalCobrado} highlight />
            </Card>
          </div>

          {/* Resumen ventas */}
          <div>
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-3">📦 Resumen de ventas</h2>
            <Card>
              <div className="grid grid-cols-3 gap-3 mb-4">
                <div className="text-center bg-slate-50 rounded-lg p-3">
                  <p className="text-2xl font-bold text-slate-700">{ventas.length}</p>
                  <p className="text-xs text-slate-400 mt-1">Ventas</p>
                </div>
                <div className="text-center bg-amber-50 rounded-lg p-3">
                  <p className="text-2xl font-bold text-amber-600">{ventasExtra.reduce((a, v) => a + v.cantidad, 0)}</p>
                  <p className="text-xs text-slate-400 mt-1">Can. Extra</p>
                </div>
                <div className="text-center bg-blue-50 rounded-lg p-3">
                  <p className="text-2xl font-bold text-blue-600">{ventasNormal.reduce((a, v) => a + v.cantidad, 0)}</p>
                  <p className="text-xs text-slate-400 mt-1">Can. Normal</p>
                </div>
              </div>

              {/* Desglose por tipo de pago */}
              <div className="space-y-2">
                {['EFECTIVO', 'TRANSFERENCIA', 'FIADO', 'ABONO'].map(tipo => {
                  const count = ventas.filter(v => v.tipoPago === tipo).length
                  if (count === 0) return null
                  return (
                    <div key={tipo} className="flex items-center justify-between text-sm">
                      <div className="flex items-center gap-2">
                        <Badge color={tipoPagoColor[tipo]}>{tipo}</Badge>
                        <span className="text-slate-400">{count} venta{count !== 1 ? 's' : ''}</span>
                      </div>
                      <span className="font-medium text-slate-700">
                        {fmt(ventas.filter(v => v.tipoPago === tipo).reduce((a, v) => a + Number(v.total || 0), 0))}
                      </span>
                    </div>
                  )
                })}
              </div>
            </Card>
          </div>

          {/* Detalle ventas */}
          <div className="lg:col-span-2">
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-3">📋 Detalle de ventas ({ventas.length})</h2>
            <Card className="p-0 overflow-hidden">
              <table className="w-full text-sm">
                <thead>
                  <tr className="table-head">
                    <th className="px-4 py-3 text-left">#</th>
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
                  {ventas.length === 0 ? (
                    <tr><td colSpan={8} className="text-center text-slate-400 py-8">No hay ventas registradas hoy</td></tr>
                  ) : ventas.map((v, i) => (
                    <tr key={v.id} className="hover:bg-slate-50">
                      <td className="table-cell text-slate-400">{i + 1}</td>
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
                      <td className="table-cell text-slate-400 text-xs">
                        {v.fecha ? new Date(v.fecha).toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' }) : '-'}
                      </td>
                    </tr>
                  ))}
                </tbody>
                {ventas.length > 0 && (
                  <tfoot>
                    <tr className="bg-slate-50 font-semibold">
                      <td colSpan={3} className="px-4 py-3 text-slate-600 text-sm">Total ({totalCanastas} canastas)</td>
                      <td className="px-4 py-3 text-right text-sm">{totalCanastas}</td>
                      <td></td>
                      <td className="px-4 py-3 text-right text-amber-600">
                        {fmt(ventas.reduce((a, v) => a + Number(v.total || 0), 0))}
                      </td>
                      <td colSpan={2}></td>
                    </tr>
                  </tfoot>
                )}
              </table>
            </Card>
          </div>
        </div>
      )}
    </div>
  )
}
