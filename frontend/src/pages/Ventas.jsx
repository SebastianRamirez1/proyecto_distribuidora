import { useEffect, useState } from 'react'
import { ventasPorFecha, registrarVenta, registrarAbono, anularVenta } from '../api/ventasApi'
import { listarClientes } from '../api/clientesApi'
import { generarFactura, descargarPdfFactura } from '../api/facturasApi'
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
  const [anulando, setAnulando] = useState(false)
  const [ventaAAnular, setVentaAAnular]   = useState(null)  // { id, nombreCliente, total }
  const [ventaAFacturar, setVentaAFacturar] = useState(null) // { id, nombreCliente, total }
  const [facturaForm, setFacturaForm]     = useState({ nitCliente: '', tipo: 'MANUAL' })
  const [generandoFactura, setGenerandoFactura] = useState(false)
  const [facturaGenerada, setFacturaGenerada]   = useState(null) // { id, numero }
  const [fechaSeleccionada, setFechaSeleccionada] = useState(
    new Date().toISOString().split('T')[0]  // hoy en formato YYYY-MM-DD
  )

  const loadVentas = async (fecha = fechaSeleccionada) => {
    try {
      setVentas(await ventasPorFecha(fecha))
    } catch (e) {
      setError(e.message)
    }
  }

  useEffect(() => {
    const load = async () => {
      try {
        const hoy = new Date().toISOString().split('T')[0]
        const [v, c] = await Promise.all([ventasPorFecha(hoy), listarClientes()])
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

  const handleFechaChange = async (e) => {
    const fecha = e.target.value
    setFechaSeleccionada(fecha)
    setLoading(true)
    try {
      setVentas(await ventasPorFecha(fecha))
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

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
      await loadVentas(new Date().toISOString().split('T')[0])
      setFechaSeleccionada(new Date().toISOString().split('T')[0])
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

  const confirmarAnulacion = async () => {
    if (!ventaAAnular) return
    setAnulando(true)
    setError('')
    try {
      await anularVenta(ventaAAnular.id)
      setVentaAAnular(null)
      await loadVentas()
      setSuccess(`Venta #${ventaAAnular.id} anulada correctamente ✅`)
    } catch (e) {
      setError(e.response?.data?.mensaje || e.message)
      setVentaAAnular(null)
    } finally {
      setAnulando(false)
    }
  }

  const handleFacturar = async (e) => {
    e.preventDefault()
    if (!ventaAFacturar) return
    setGenerandoFactura(true)
    setError('')
    try {
      const result = await generarFactura({
        ventaId:    ventaAFacturar.id,
        nitCliente: facturaForm.nitCliente || null,
        tipo:       facturaForm.tipo,
      })
      setFacturaGenerada({ id: result.id, numero: result.numero })
    } catch (e) {
      setError(e.response?.data?.mensaje || e.message)
      setVentaAFacturar(null)
      setFacturaGenerada(null)
    } finally {
      setGenerandoFactura(false)
    }
  }

  const cerrarModalFactura = () => {
    setVentaAFacturar(null)
    setFacturaGenerada(null)
    setFacturaForm({ nitCliente: '', tipo: 'MANUAL' })
  }

  const totalDia = ventas.reduce((acc, v) => acc + Number(v.total || 0), 0)
  const hoy = new Date().toISOString().split('T')[0]
  const esHoy = fechaSeleccionada === hoy

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Ventas</h1>
        <p className="text-slate-500 text-sm mt-1">Registrar ventas y abonos</p>
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
          <div className="flex items-center justify-between mb-3 gap-3 flex-wrap">
            <div className="flex items-center gap-3">
              <h3 className="font-semibold text-slate-700">
                {esHoy ? 'Ventas de hoy' : `Ventas del ${new Date(fechaSeleccionada + 'T12:00:00').toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' })}`}
              </h3>
              {!esHoy && (
                <button
                  onClick={() => { setFechaSeleccionada(hoy); loadVentas(hoy) }}
                  className="text-xs text-amber-600 hover:text-amber-700 font-medium"
                >
                  ← Volver a hoy
                </button>
              )}
            </div>
            <div className="flex items-center gap-3">
              <input
                type="date"
                value={fechaSeleccionada}
                max={hoy}
                onChange={handleFechaChange}
                className="text-sm border border-slate-300 rounded-lg px-3 py-1.5 text-slate-700 focus:outline-none focus:ring-2 focus:ring-amber-400"
              />
              <span className="text-sm text-slate-500 whitespace-nowrap">
                Total: <span className="font-bold text-slate-800">{fmt(totalDia)}</span>
              </span>
            </div>
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
                    <th className="px-4 py-3 text-right">Ganancia</th>
                    <th className="px-4 py-3 text-left">Pago</th>
                    <th className="px-4 py-3 text-left">Hora</th>
                    <th className="px-4 py-3"></th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {ventas.length === 0 ? (
                    <tr><td colSpan={9} className="text-center text-slate-400 py-8">No hay ventas hoy</td></tr>
                  ) : ventas.map((v) => (
                    <tr key={v.id} className={`hover:bg-slate-50 ${v.anulada ? 'opacity-40 line-through' : ''}`}>
                      <td className="table-cell font-medium">{v.nombreCliente}</td>
                      <td className="table-cell">
                        <Badge color={tipoColor[v.tipoProducto]}>{v.tipoProducto}</Badge>
                      </td>
                      <td className="table-cell text-right">{v.cantidad}</td>
                      <td className="table-cell text-right text-slate-500">{fmt(v.precioUnitario)}</td>
                      <td className="table-cell text-right font-semibold">{fmt(v.total)}</td>
                      <td className="table-cell text-right text-emerald-600 text-xs">
                        {v.ganancia != null ? fmt(v.ganancia) : <span className="text-slate-300">—</span>}
                      </td>
                      <td className="table-cell">
                        <Badge color={tipoPagoColor[v.tipoPago]}>{v.tipoPago}</Badge>
                      </td>
                      <td className="table-cell text-slate-400 text-xs">
                        {v.fecha ? new Date(v.fecha).toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' }) : '-'}
                      </td>
                      <td className="table-cell">
                        {!v.anulada && (
                          <div className="flex items-center gap-1">
                            <button
                              onClick={() => setVentaAFacturar({ id: v.id, nombreCliente: v.nombreCliente, total: v.total })}
                              title="Generar factura"
                              className="text-slate-300 hover:text-amber-500 transition-colors min-w-[44px] min-h-[44px] flex items-center justify-center rounded"
                            >
                              🧾
                            </button>
                            <button
                              onClick={() => setVentaAAnular({ id: v.id, nombreCliente: v.nombreCliente, total: v.total })}
                              title="Anular venta"
                              className="text-slate-300 hover:text-rose-500 transition-colors min-w-[44px] min-h-[44px] flex items-center justify-center rounded"
                            >
                              🗑️
                            </button>
                          </div>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </Card>
          )}
        </div>
      </div>

      {/* Modal generar factura */}
      {ventaAFacturar && !facturaGenerada && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm">
            <div className="flex items-center gap-3 mb-4">
              <span className="text-2xl">🧾</span>
              <h3 className="text-lg font-bold text-slate-800">Generar factura</h3>
            </div>
            <p className="text-sm text-slate-600 mb-1">
              Cliente: <span className="font-semibold">{ventaAFacturar.nombreCliente}</span>
            </p>
            <p className="text-sm text-slate-500 mb-4">
              Total: <span className="font-semibold text-amber-600">{fmt(ventaAFacturar.total)}</span>
            </p>
            <form onSubmit={handleFacturar} className="space-y-3">
              <div>
                <label className="block text-xs font-medium text-slate-600 mb-1">
                  NIT / CC del comprador <span className="text-slate-400">(opcional)</span>
                </label>
                <input
                  type="text"
                  placeholder="Ej: 900123456-1"
                  value={facturaForm.nitCliente}
                  onChange={e => setFacturaForm(p => ({ ...p, nitCliente: e.target.value }))}
                  className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-slate-600 mb-1">Tipo de factura</label>
                <select
                  value={facturaForm.tipo}
                  onChange={e => setFacturaForm(p => ({ ...p, tipo: e.target.value }))}
                  className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
                >
                  <option value="MANUAL">📄 Factura de venta (manual)</option>
                  <option value="ELECTRONICA">💻 Factura electrónica (DIAN)</option>
                </select>
                {facturaForm.tipo === 'ELECTRONICA' && (
                  <p className="text-xs text-blue-600 mt-1">
                    ℹ️ Se genera el PDF. La transmisión a la DIAN requiere un PTH habilitado.
                  </p>
                )}
              </div>
              <div className="flex gap-3 pt-1">
                <button
                  type="button"
                  onClick={cerrarModalFactura}
                  disabled={generandoFactura}
                  className="flex-1 py-2 rounded-lg border border-slate-300 text-slate-600 text-sm font-medium hover:bg-slate-50 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={generandoFactura}
                  className="flex-1 py-2 rounded-lg bg-amber-500 text-white text-sm font-medium hover:bg-amber-600 transition-colors disabled:opacity-60"
                >
                  {generandoFactura ? 'Generando…' : 'Generar factura'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal factura generada — descargar PDF */}
      {facturaGenerada && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm text-center">
            <div className="text-4xl mb-3">✅</div>
            <h3 className="text-lg font-bold text-slate-800 mb-1">¡Factura generada!</h3>
            <p className="text-sm text-slate-500 mb-1">Número de factura:</p>
            <p className="text-xl font-bold text-amber-600 mb-5">{facturaGenerada.numero}</p>
            <div className="flex gap-3">
              <button
                onClick={cerrarModalFactura}
                className="flex-1 py-2 rounded-lg border border-slate-300 text-slate-600 text-sm font-medium hover:bg-slate-50 transition-colors"
              >
                Cerrar
              </button>
              <button
                onClick={async () => {
                  try {
                    await descargarPdfFactura(facturaGenerada.id, facturaGenerada.numero)
                  } catch (e) {
                    setError('Error al descargar PDF: ' + (e.message || 'Error desconocido'))
                  }
                }}
                className="flex-1 py-2 rounded-lg bg-amber-500 text-white text-sm font-medium hover:bg-amber-600 transition-colors"
              >
                📥 Descargar PDF
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal confirmación de anulación */}
      {ventaAAnular && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm">
            <div className="flex items-center gap-3 mb-4">
              <span className="text-2xl">⚠️</span>
              <h3 className="text-lg font-bold text-slate-800">Anular venta</h3>
            </div>
            <p className="text-sm text-slate-600 mb-1">
              ¿Estás seguro de anular la venta <span className="font-semibold">#{ventaAAnular.id}</span>?
            </p>
            <p className="text-sm text-slate-500 mb-1">
              Cliente: <span className="font-medium">{ventaAAnular.nombreCliente}</span>
            </p>
            <p className="text-sm text-slate-500 mb-4">
              Total: <span className="font-medium text-rose-600">{fmt(ventaAAnular.total)}</span>
            </p>
            <div className="bg-amber-50 border border-amber-200 rounded-lg px-3 py-2 text-xs text-amber-700 mb-5">
              Se revertirá el stock, el movimiento de caja y (si era fiado) el crédito del cliente.
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => setVentaAAnular(null)}
                disabled={anulando}
                className="flex-1 py-2 rounded-lg border border-slate-300 text-slate-600 text-sm font-medium hover:bg-slate-50 transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={confirmarAnulacion}
                disabled={anulando}
                className="flex-1 py-2 rounded-lg bg-rose-600 text-white text-sm font-medium hover:bg-rose-700 transition-colors disabled:opacity-60"
              >
                {anulando ? 'Anulando…' : 'Sí, anular'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
