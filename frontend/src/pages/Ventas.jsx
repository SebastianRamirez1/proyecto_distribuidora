import { useEffect, useState } from 'react'
import { ventasHoy, registrarVenta, registrarAbono } from '../api/ventasApi'
import { listarClientes } from '../api/clientesApi'
import Card from '../components/ui/Card'
import Button from '../components/ui/Button'
import Select from '../components/ui/Select'
import Input from '../components/ui/Input'
import Alert from '../components/ui/Alert'
import Badge from '../components/ui/Badge'
import Spinner from '../components/ui/Spinner'

const tipoPagoColor = { EFECTIVO: 'emerald', TRANSFERENCIA: 'blue', FIADO: 'rose', ABONO: 'purple' }
const tipoColor = { EXTRA: 'amber', AA: 'yellow', A: 'blue', B: 'slate' }
const fmt = (n) => n != null ? `S/ ${Number(n).toFixed(2)}` : 'S/ 0.00'

const initVenta = { clienteId: '', tipoProducto: 'EXTRA', cantidad: '', tipoPago: 'EFECTIVO', precioManual: '' }
const initAbono = { clienteId: '', monto: '', medioPago: 'EFECTIVO' }

export default function Ventas() {
  const [ventas, setVentas] = useState([])
  const [clientes, setClientes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [formVenta, setFormVenta] = useState(initVenta)
  const [formAbono, setFormAbono] = useState(initAbono)
  const [savingV, setSavingV] = useState(false)
  const [savingA, setSavingA] = useState(false)
  const [tab, setTab] = useState('venta') // 'venta' | 'abono'
  const [mostrarPrecioManual, setMostrarPrecioManual] = useState(false)

  const loadVentas = async () => {
    try {
      setVentas(await ventasHoy())
    } catch (e) {
      setError(e.message)
    }
  }

  useEffect(() => {
    const load = async () => {
      try {
        const [v, c] = await Promise.all([ventasHoy(), listarClientes()])
        setVentas(v)
        setClientes(c)
      } catch (e) {
        setError(e.message)
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  const handleVenta = async (e) => {
    e.preventDefault()
    setSavingV(true)
    setError('')
    try {
      const payload = {
        clienteId:    Number(formVenta.clienteId),
        tipoProducto: formVenta.tipoProducto,
        cantidad:     Number(formVenta.cantidad),
        tipoPago:     formVenta.tipoPago,
      }
      if (mostrarPrecioManual && formVenta.precioManual !== '') {
        payload.precioManual = Number(formVenta.precioManual)
      }
      await registrarVenta(payload)
      setFormVenta(initVenta)
      setMostrarPrecioManual(false)
      await loadVentas()
      setSuccess('Venta registrada correctamente ✅')
    } catch (e) {
      setError(e.message)
    } finally {
      setSavingV(false)
    }
  }

  const handleAbono = async (e) => {
    e.preventDefault()
    setSavingA(true)
    setError('')
    try {
      await registrarAbono({
        clienteId: Number(formAbono.clienteId),
        monto:     Number(formAbono.monto),
        medioPago: formAbono.medioPago,
      })
      setFormAbono(initAbono)
      await loadVentas()
      setSuccess('Abono registrado correctamente ✅')
    } catch (e) {
      setError(e.message)
    } finally {
      setSavingA(false)
    }
  }

  const togglePrecioManual = () => {
    setMostrarPrecioManual(v => !v)
    setFormVenta(p => ({ ...p, precioManual: '' }))
  }

  const totalHoy = ventas.reduce((acc, v) => acc + Number(v.total || 0), 0)

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Ventas</h1>
        <p className="text-slate-500 text-sm mt-1">Registrar ventas y abonos — ventas de hoy</p>
      </div>

      <Alert type="error"   message={error}   onClose={() => setError('')} />
      <Alert type="success" message={success} onClose={() => setSuccess('')} />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">
        {/* Formulario */}
        <div className="lg:col-span-1">
          {/* Tabs */}
          <div className="flex mb-4 bg-slate-100 rounded-lg p-1">
            <button
              onClick={() => setTab('venta')}
              className={`flex-1 py-2 text-sm font-medium rounded-md transition-all ${tab === 'venta' ? 'bg-white shadow text-amber-600' : 'text-slate-500 hover:text-slate-700'}`}
            >🛒 Venta</button>
            <button
              onClick={() => setTab('abono')}
              className={`flex-1 py-2 text-sm font-medium rounded-md transition-all ${tab === 'abono' ? 'bg-white shadow text-purple-600' : 'text-slate-500 hover:text-slate-700'}`}
            >💳 Abono</button>
          </div>

          {tab === 'venta' ? (
            <Card>
              <h3 className="font-semibold text-slate-700 mb-4">Registrar venta</h3>
              <form onSubmit={handleVenta}>
                <Select
                  label="Cliente"
                  value={formVenta.clienteId}
                  onChange={e => setFormVenta(p => ({ ...p, clienteId: e.target.value }))}
                  required
                >
                  <option value="">— Seleccionar cliente —</option>
                  {clientes.map(c => (
                    <option key={c.id} value={c.id}>{c.nombre} ({c.tipo})</option>
                  ))}
                </Select>
                <Select
                  label="Tipo de canasta"
                  value={formVenta.tipoProducto}
                  onChange={e => setFormVenta(p => ({ ...p, tipoProducto: e.target.value }))}
                >
                  <option value="EXTRA">🥚 EXTRA</option>
                  <option value="AA">🥚 AA</option>
                  <option value="A">🥚 A</option>
                  <option value="B">🥚 B</option>
                </Select>
                <Input
                  label="Cantidad (canastas)"
                  type="number" min="1"
                  placeholder="Ej: 5"
                  value={formVenta.cantidad}
                  onChange={e => setFormVenta(p => ({ ...p, cantidad: e.target.value }))}
                  required
                />
                <Select
                  label="Tipo de pago"
                  value={formVenta.tipoPago}
                  onChange={e => setFormVenta(p => ({ ...p, tipoPago: e.target.value }))}
                >
                  <option value="EFECTIVO">💵 Efectivo</option>
                  <option value="TRANSFERENCIA">📲 Transferencia</option>
                  <option value="FIADO">📋 Fiado</option>
                  <option value="ABONO">💳 Abono a deuda</option>
                </Select>

                {/* Precio manual — rebaja puntual */}
                <div className="mt-3 mb-1">
                  <button
                    type="button"
                    onClick={togglePrecioManual}
                    className={`flex items-center gap-2 text-xs font-medium px-3 py-1.5 rounded-full border transition-colors ${
                      mostrarPrecioManual
                        ? 'bg-orange-100 border-orange-300 text-orange-700'
                        : 'bg-slate-100 border-slate-300 text-slate-500 hover:text-slate-700'
                    }`}
                  >
                    <span>{mostrarPrecioManual ? '✕' : '🏷️'}</span>
                    {mostrarPrecioManual ? 'Quitar rebaja' : 'Aplicar rebaja puntual'}
                  </button>
                </div>

                {mostrarPrecioManual && (
                  <div className="border border-orange-200 bg-orange-50 rounded-lg p-3 mt-2">
                    <p className="text-xs text-orange-700 mb-2 font-medium">
                      🏷️ Este precio reemplaza el precio normal del cliente, solo para esta venta.
                    </p>
                    <Input
                      label="Precio por canasta (S/)"
                      type="number" step="0.01" min="0.01"
                      placeholder="0.00"
                      value={formVenta.precioManual}
                      onChange={e => setFormVenta(p => ({ ...p, precioManual: e.target.value }))}
                      required
                    />
                  </div>
                )}

                <Button type="submit" loading={savingV} className="w-full mt-3">
                  Registrar venta
                </Button>
              </form>
            </Card>
          ) : (
            <Card>
              <h3 className="font-semibold text-slate-700 mb-4">Registrar abono</h3>
              <form onSubmit={handleAbono}>
                <Select
                  label="Cliente"
                  value={formAbono.clienteId}
                  onChange={e => setFormAbono(p => ({ ...p, clienteId: e.target.value }))}
                  required
                >
                  <option value="">— Seleccionar cliente —</option>
                  {clientes.map(c => (
                    <option key={c.id} value={c.id}>{c.nombre}</option>
                  ))}
                </Select>
                <Input
                  label="Monto del abono (S/)"
                  type="number" step="0.01" min="0.01"
                  placeholder="0.00"
                  value={formAbono.monto}
                  onChange={e => setFormAbono(p => ({ ...p, monto: e.target.value }))}
                  required
                />
                <Select
                  label="¿Cómo pagó?"
                  value={formAbono.medioPago}
                  onChange={e => setFormAbono(p => ({ ...p, medioPago: e.target.value }))}
                >
                  <option value="EFECTIVO">💵 Efectivo</option>
                  <option value="TRANSFERENCIA">📲 Transferencia</option>
                </Select>
                <div className={`text-xs rounded-lg px-3 py-2 mb-3 ${
                  formAbono.medioPago === 'EFECTIVO'
                    ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                    : 'bg-blue-50 text-blue-700 border border-blue-200'
                }`}>
                  {formAbono.medioPago === 'EFECTIVO'
                    ? '💵 Este abono sumará al efectivo del día'
                    : '📲 Este abono sumará a las transferencias del día'}
                </div>
                <Button type="submit" loading={savingA} variant="success" className="w-full">
                  Registrar abono
                </Button>
              </form>
            </Card>
          )}
        </div>

        {/* Lista de ventas */}
        <div className="lg:col-span-2">
          <div className="flex items-center justify-between mb-3">
            <h3 className="font-semibold text-slate-700">Ventas de hoy</h3>
            <span className="text-sm text-slate-500">Total: <span className="font-bold text-slate-800">{fmt(totalHoy)}</span></span>
          </div>
          {loading ? <Spinner /> : (
            <Card className="p-0 overflow-hidden">
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
                  {ventas.length === 0 ? (
                    <tr><td colSpan={7} className="text-center text-slate-400 py-8">No hay ventas hoy</td></tr>
                  ) : ventas.map((v) => (
                    <tr key={v.id} className="hover:bg-slate-50">
                      <td className="table-cell font-medium">{v.nombreCliente}</td>
                      <td className="table-cell">
                        <Badge color={tipoColor[v.tipoProducto]}>{v.tipoProducto}</Badge>
                      </td>
                      <td className="table-cell text-right">{v.cantidad}</td>
                      <td className="table-cell text-right text-slate-500">{fmt(v.precioUnitario)}</td>
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
              </table>
            </Card>
          )}
        </div>
      </div>
    </div>
  )
}
