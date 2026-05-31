import { useEffect, useState } from 'react'
import { ventasPorFecha, registrarVenta, registrarAbono, anularVenta } from '../api/ventasApi'
import { listarClientes } from '../api/clientesApi'
import { generarFactura, descargarPdfFactura } from '../api/facturasApi'
import { obtenerEstadoJornadas, liquidarJornada, cerrarJornada } from '../api/jornadasApi'
import Card from '../components/ui/Card'
import Button from '../components/ui/Button'
import Select from '../components/ui/Select'
import Input from '../components/ui/Input'
import Alert from '../components/ui/Alert'
import Badge from '../components/ui/Badge'
import Spinner from '../components/ui/Spinner'
import { fmt, parsePrecio } from '../utils/fmt'

const tipoPagoColor = { EFECTIVO: 'emerald', TRANSFERENCIA: 'blue', FIADO: 'rose', ABONO: 'purple' }
const tipoColor = { EXTRA: 'amber', AA: 'yellow', A: 'blue', B: 'slate', EXTRA_MEDIA: 'orange', AA_MEDIA: 'lime' }
const tipoLabel = { EXTRA: 'EXTRA', AA: 'AA', A: 'A', B: 'B', EXTRA_MEDIA: '½ EXTRA', AA_MEDIA: '½ AA' }

const initVenta = { clienteId: '', tipoProducto: 'EXTRA', cantidad: '', tipoPago: 'EFECTIVO', precioManual: '' }
const initAbono = { clienteId: '', monto: '', medioPago: 'EFECTIVO' }

export default function Ventas() {
  const [ventas, setVentas] = useState([])
  const [clientes, setClientes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [tab, setTab] = useState('venta')  // 'venta' | 'abono'
  const [formVenta, setFormVenta] = useState(initVenta)
  const [formAbono, setFormAbono] = useState(initAbono)
  const [savingV, setSavingV] = useState(false)
  const [savingA, setSavingA] = useState(false)
  const [mostrarPrecioManual, setMostrarPrecioManual] = useState(false)

  // Jornadas
  const [jornada, setJornada]               = useState(null)   // ABIERTA
  const [jornadaEnCierre, setJornadaEnCierre] = useState(null) // EN_CIERRE (puede ser null)
  const [liquidando, setLiquidando]         = useState(false)
  const [cerrando, setCerrando]             = useState(false)
  const [modalLiquidar, setModalLiquidar]   = useState(false)
  const [modalCerrar, setModalCerrar]       = useState(false)
  // jornadaIdVenta: null = usar jornada ABIERTA, o ID de la jornada EN_CIERRE
  const [jornadaIdVenta, setJornadaIdVenta] = useState(null)
  // jornadaIdAbono: igual que jornadaIdVenta pero para abonos
  const [jornadaIdAbono, setJornadaIdAbono] = useState(null)

  const [anulando, setAnulando] = useState(false)
  const [ventaAAnular, setVentaAAnular]     = useState(null)
  const [ventaAFacturar, setVentaAFacturar] = useState(null)
  const [facturaForm, setFacturaForm]       = useState({ nombreCliente: '', nitCliente: '', tipo: 'MANUAL' })
  const [generandoFactura, setGenerandoFactura] = useState(false)
  const [facturaGenerada, setFacturaGenerada]   = useState(null)
  const [fechaSeleccionada, setFechaSeleccionada] = useState(
    new Date().toISOString().split('T')[0]
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
        const [v, c, estado] = await Promise.all([ventasPorFecha(hoy), listarClientes(), obtenerEstadoJornadas()])
        setVentas(v)
        setClientes(c)
        setJornada(estado.abierta)
        setJornadaEnCierre(estado.enCierre ?? null)
        // Mostrar por defecto la fecha de la jornada activa
        if (estado.abierta && estado.abierta.fecha !== hoy) setFechaSeleccionada(estado.abierta.fecha)
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
        clienteId:    formVenta.clienteId !== '' ? Number(formVenta.clienteId) : null,
        tipoProducto: formVenta.tipoProducto,
        cantidad:     Number(formVenta.cantidad),
        tipoPago:     formVenta.tipoPago,
      }
      if (mostrarPrecioManual && formVenta.precioManual !== '') {
        payload.precioManual = parsePrecio(formVenta.precioManual)
      }
      if (jornadaIdVenta !== null) {
        payload.jornadaId = jornadaIdVenta
      }
      await registrarVenta(payload)
      setFormVenta(initVenta)
      setMostrarPrecioManual(false)
      // Recargar la fecha correspondiente a la jornada usada
      const fechaRecarga = jornadaIdVenta !== null && jornadaEnCierre
        ? jornadaEnCierre.fecha
        : (jornada?.fecha ?? new Date().toISOString().split('T')[0])
      await loadVentas(fechaRecarga)
      setFechaSeleccionada(fechaRecarga)
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
      const payload = {
        clienteId: Number(formAbono.clienteId),
        monto:     Number(formAbono.monto),
        medioPago: formAbono.medioPago,
      }
      if (jornadaIdAbono !== null) {
        payload.jornadaId = jornadaIdAbono
      }
      await registrarAbono(payload)
      setFormAbono(initAbono)
      // Recargar la fecha de la jornada donde se registró el abono
      const fechaRecarga = jornadaIdAbono !== null && jornadaEnCierre
        ? jornadaEnCierre.fecha
        : (jornada?.fecha ?? new Date().toISOString().split('T')[0])
      await loadVentas(fechaRecarga)
      setFechaSeleccionada(fechaRecarga)
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
        ventaId:       ventaAFacturar.id,
        nombreCliente: facturaForm.nombreCliente || null,
        nitCliente:    facturaForm.nitCliente || null,
        tipo:          facturaForm.tipo,
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
    setFacturaForm({ nombreCliente: '', nitCliente: '', tipo: 'MANUAL' })
  }

  const handleLiquidar = async () => {
    setLiquidando(true)
    setError('')
    try {
      const nuevaJornada = await liquidarJornada()
      // La jornada que estaba ABIERTA pasa a EN_CIERRE; la nueva es ABIERTA
      setJornadaEnCierre(jornada)
      setJornada(nuevaJornada)
      setModalLiquidar(false)
      setFechaSeleccionada(nuevaJornada.fecha)
      await loadVentas(nuevaJornada.fecha)
      setSuccess(`✅ Jornada liquidada. Nueva hoja: ${fmtFechaJornada(nuevaJornada.fecha)}`)
    } catch (e) {
      setError(e.response?.data?.mensaje || e.message)
    } finally {
      setLiquidando(false)
    }
  }

  const handleCerrar = async () => {
    setCerrando(true)
    setError('')
    try {
      await cerrarJornada()
      setJornadaEnCierre(null)
      setJornadaIdVenta(null)
      setJornadaIdAbono(null)
      setModalCerrar(false)
      setSuccess('✅ Hoja anterior cerrada definitivamente')
    } catch (e) {
      setError(e.response?.data?.mensaje || e.message)
    } finally {
      setCerrando(false)
    }
  }

  const fmtFechaJornada = (fecha) =>
    new Date(fecha + 'T12:00:00').toLocaleDateString('es-CO', {
      weekday: 'long', day: 'numeric', month: 'long'
    })

  const totalDia = ventas.reduce((acc, v) => acc + Number(v.total || 0), 0)
  const hoy = new Date().toISOString().split('T')[0]
  const esHoy = fechaSeleccionada === hoy

  return (
    <div className="h-full flex flex-col gap-3">
      <div className="flex items-start justify-between gap-4 flex-wrap flex-shrink-0">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Ventas</h1>
          <p className="text-slate-500 text-sm mt-1">Registrar ventas y abonos</p>
        </div>
        {/* Banners de jornada */}
        <div className="flex flex-col gap-2 items-end">
          {/* Jornada EN_CIERRE (hoja anterior con ventas pendientes) */}
          {jornadaEnCierre && (
            <div className="flex items-center gap-3 px-4 py-2 rounded-xl border bg-amber-50 border-amber-200 text-sm">
              <span>📋</span>
              <div>
                <span className="font-semibold capitalize text-amber-800">{fmtFechaJornada(jornadaEnCierre.fecha)}</span>
                <span className="ml-2 text-xs px-2 py-0.5 rounded-full font-semibold bg-amber-100 text-amber-700">
                  EN CIERRE
                </span>
              </div>
              <button
                onClick={() => setModalCerrar(true)}
                className="ml-1 px-3 py-1 text-xs font-semibold bg-slate-600 hover:bg-slate-700 text-white rounded-lg transition-colors"
              >
                Cerrar definitivamente
              </button>
            </div>
          )}
          {/* Jornada ABIERTA (hoja actual) */}
          {jornada && (
            <div className="flex items-center gap-3 px-4 py-2 rounded-xl border bg-emerald-50 border-emerald-200 text-sm">
              <span>📋</span>
              <div>
                <span className="font-semibold capitalize text-emerald-800">{fmtFechaJornada(jornada.fecha)}</span>
                <span className="ml-2 text-xs px-2 py-0.5 rounded-full font-semibold bg-emerald-100 text-emerald-700">
                  ABIERTA
                </span>
              </div>
              <button
                onClick={() => setModalLiquidar(true)}
                className="ml-1 px-3 py-1 text-xs font-semibold bg-amber-500 hover:bg-amber-600 text-white rounded-lg transition-colors"
              >
                Liquidar
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="flex-shrink-0">
        <Alert type="error"   message={error}   onClose={() => setError('')} />
        <Alert type="success" message={success} onClose={() => setSuccess('')} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 flex-1 min-h-0">

        {/* ── Panel izquierdo: formularios ── */}
        <div className="lg:col-span-1 lg:self-start">
          <Card className="p-0 overflow-hidden">
            {/* Tabs */}
            <div className="flex border-b border-slate-100">
              <button
                onClick={() => setTab('venta')}
                className={`flex-1 py-3 text-sm font-medium transition-colors ${
                  tab === 'venta'
                    ? 'bg-amber-50 text-amber-600 border-b-2 border-amber-400'
                    : 'text-slate-500 hover:text-slate-700 hover:bg-slate-50'
                }`}
              >
                🛒 Venta
              </button>
              <button
                onClick={() => setTab('abono')}
                className={`flex-1 py-3 text-sm font-medium transition-colors ${
                  tab === 'abono'
                    ? 'bg-purple-50 text-purple-600 border-b-2 border-purple-400'
                    : 'text-slate-500 hover:text-slate-700 hover:bg-slate-50'
                }`}
              >
                💳 Abono
              </button>
            </div>

            <div className="p-4">
              {tab === 'venta' ? (
                <form onSubmit={handleVenta}>
                  {/* Indicador de hoja activa — solo cuando hay dos jornadas */}
                  {jornadaEnCierre && (
                    <div className={`flex items-center gap-2 px-3 py-2 rounded-lg border text-xs font-medium mb-3 ${
                      jornadaIdVenta === null
                        ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
                        : 'bg-amber-50 border-amber-200 text-amber-800'
                    }`}>
                      <span>📋</span>
                      <span>
                        Hoja {jornadaIdVenta === null ? 'actual' : 'anterior'} —{' '}
                        <span className="capitalize font-normal">
                          {fmtFechaJornada(jornadaIdVenta === null ? jornada?.fecha ?? '' : jornadaEnCierre.fecha)}
                        </span>
                      </span>
                    </div>
                  )}
                  <Select
                    label="Cliente"
                    value={formVenta.clienteId}
                    onChange={e => setFormVenta(p => ({ ...p, clienteId: e.target.value }))}
                  >
                    <option value="">Público General (precio público)</option>
                    {clientes.map(c => (
                      <option key={c.id} value={c.id}>{c.nombre} ({c.tipo})</option>
                    ))}
                  </Select>
                  <Select
                    label="Tipo de producto"
                    value={formVenta.tipoProducto}
                    onChange={e => setFormVenta(p => ({ ...p, tipoProducto: e.target.value }))}
                  >
                    <option value="EXTRA">🥚 EXTRA</option>
                    <option value="AA">🥚 AA</option>
                    <option value="A">🥚 A</option>
                    <option value="B">🥚 B</option>
                    <option disabled>──────────────</option>
                    <option value="EXTRA_MEDIA">🥚 ½ EXTRA (media canasta)</option>
                    <option value="AA_MEDIA">🥚 ½ AA (media canasta)</option>
                  </Select>
                  <Input
                    label={`Cantidad (${formVenta.tipoProducto.endsWith('_MEDIA') ? 'medias canastas' : 'canastas'})`}
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

                  {/* Precio manual */}
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
                        label="Precio por canasta ($)"
                        type="number" step="0.01" min="0.01"
                        placeholder="0.00"
                        value={formVenta.precioManual}
                        onChange={e => setFormVenta(p => ({ ...p, precioManual: e.target.value }))}
                        required
                      />
                    </div>
                  )}

                  <Button type="submit" loading={savingV} className="w-full mt-4">
                    🛒 Registrar venta
                  </Button>
                </form>
              ) : (
                <form onSubmit={handleAbono}>
                  {/* Indicador de hoja activa — solo cuando hay dos jornadas */}
                  {jornadaEnCierre && (
                    <div className={`flex items-center gap-2 px-3 py-2 rounded-lg border text-xs font-medium mb-3 ${
                      jornadaIdAbono === null
                        ? 'bg-emerald-50 border-emerald-200 text-emerald-800'
                        : 'bg-amber-50 border-amber-200 text-amber-800'
                    }`}>
                      <span>📋</span>
                      <span>
                        Hoja {jornadaIdAbono === null ? 'actual' : 'anterior'} —{' '}
                        <span className="capitalize font-normal">
                          {fmtFechaJornada(jornadaIdAbono === null ? jornada?.fecha ?? '' : jornadaEnCierre.fecha)}
                        </span>
                      </span>
                    </div>
                  )}
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
                    label="Monto del abono ($)"
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
                    💳 Registrar abono
                  </Button>
                </form>
              )}
            </div>
          </Card>
        </div>

        {/* ── Panel derecho: tabla de ventas (scroll solo aquí) ── */}
        <div className="lg:col-span-3 flex flex-col min-h-0">
          <div className="flex items-center justify-between mb-3 gap-3 flex-wrap flex-shrink-0">
            <div className="flex items-center gap-2 flex-wrap">
              {/* Switch rápido de hojas — aparece solo cuando hay dos jornadas activas */}
              {jornadaEnCierre ? (
                <>
                  <button
                    onClick={() => {
                      setFechaSeleccionada(jornada.fecha); loadVentas(jornada.fecha)
                      setJornadaIdVenta(null); setJornadaIdAbono(null)
                    }}
                    className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors border ${
                      fechaSeleccionada === jornada?.fecha
                        ? 'bg-emerald-50 border-emerald-300 text-emerald-700'
                        : 'border-slate-200 text-slate-500 hover:bg-slate-50'
                    }`}
                  >
                    📋 Hoja actual
                    <span className="ml-1.5 text-xs opacity-70 capitalize">{fmtFechaJornada(jornada.fecha)}</span>
                  </button>
                  <button
                    onClick={() => {
                      setFechaSeleccionada(jornadaEnCierre.fecha); loadVentas(jornadaEnCierre.fecha)
                      setJornadaIdVenta(jornadaEnCierre.id); setJornadaIdAbono(jornadaEnCierre.id)
                    }}
                    className={`px-3 py-1.5 rounded-lg text-sm font-medium transition-colors border ${
                      fechaSeleccionada === jornadaEnCierre?.fecha
                        ? 'bg-amber-50 border-amber-300 text-amber-700'
                        : 'border-slate-200 text-slate-500 hover:bg-slate-50'
                    }`}
                  >
                    📋 Hoja anterior
                    <span className="ml-1.5 text-xs opacity-70 capitalize">{fmtFechaJornada(jornadaEnCierre.fecha)}</span>
                  </button>
                </>
              ) : (
                <h3 className="font-semibold text-slate-700">
                  {fechaSeleccionada === jornada?.fecha
                    ? 'Ventas de hoy'
                    : `Ventas del ${new Date(fechaSeleccionada + 'T12:00:00').toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' })}`}
                  {fechaSeleccionada !== jornada?.fecha && (
                    <button
                      onClick={() => { const f = jornada?.fecha ?? hoy; setFechaSeleccionada(f); loadVentas(f) }}
                      className="ml-3 text-xs text-amber-600 hover:text-amber-700 font-medium"
                    >
                      ← Volver a hoy
                    </button>
                  )}
                </h3>
              )}
            </div>
            <div className="flex items-center gap-3">
              <input
                type="date"
                value={fechaSeleccionada}
                max={jornada?.fecha ?? hoy}
                onChange={handleFechaChange}
                className="text-sm border border-slate-300 rounded-lg px-3 py-1.5 text-slate-700 focus:outline-none focus:ring-2 focus:ring-amber-400"
              />
              <span className="text-sm text-slate-500 whitespace-nowrap">
                Total: <span className="font-bold text-slate-800">{fmt(totalDia)}</span>
              </span>
            </div>
          </div>

          {loading ? (
            <div className="flex items-center justify-center py-16 flex-1"><Spinner /></div>
          ) : (
            <Card className="p-0 overflow-hidden flex-1 min-h-0 flex flex-col">
              <div className="overflow-auto flex-1">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="table-head sticky top-0 z-10">
                      <th className="px-3 py-2.5 text-left">Cliente</th>
                      <th className="px-3 py-2.5 text-left">Tipo</th>
                      <th className="px-3 py-2.5 text-right">Cant.</th>
                      <th className="px-3 py-2.5 text-right">P/U</th>
                      <th className="px-3 py-2.5 text-right">Total</th>
                      <th className="px-3 py-2.5 text-right">Ganancia</th>
                      <th className="px-3 py-2.5 text-left">Pago</th>
                      <th className="px-3 py-2.5 text-left">Hora</th>
                      <th className="px-3 py-2.5"></th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {ventas.length === 0 ? (
                      <tr><td colSpan={9} className="text-center text-slate-400 py-8">
                        {fechaSeleccionada === (jornada?.fecha ?? hoy) ? 'No hay ventas hoy' : 'No hay ventas en esta fecha'}
                      </td></tr>
                    ) : ventas.map((v) => (
                      <tr key={v.id} className={`hover:bg-slate-50 ${v.anulada ? 'opacity-40 line-through' : ''}`}>
                        <td className="table-cell font-medium">{v.nombreCliente}</td>
                        <td className="table-cell">
                          <Badge color={tipoColor[v.tipoProducto]}>{tipoLabel[v.tipoProducto] ?? v.tipoProducto}</Badge>
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
                                onClick={() => {
                                  setVentaAFacturar({ id: v.id, nombreCliente: v.nombreCliente, total: v.total })
                                  const esPublico = !v.clienteId
                                  setFacturaForm(f => ({ ...f, nombreCliente: esPublico ? '' : (v.nombreCliente || '') }))
                                }}
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
              </div>
            </Card>
          )}
        </div>
      </div>

      {/* ── Modal generar factura ── */}
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
                  Nombre en la factura <span className="text-slate-400">(opcional)</span>
                </label>
                <input
                  type="text"
                  placeholder={!ventaAFacturar?.clienteId ? 'Consumidor Final' : ventaAFacturar.nombreCliente}
                  value={facturaForm.nombreCliente}
                  onChange={e => setFacturaForm(p => ({ ...p, nombreCliente: e.target.value }))}
                  className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
                />
                <p className="text-xs text-slate-400 mt-1">
                  Si lo dejas vacío se usará "{!ventaAFacturar?.clienteId ? 'Consumidor Final' : ventaAFacturar?.nombreCliente}"
                </p>
              </div>
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

      {/* ── Modal factura generada ── */}
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

      {/* ── Modal cerrar definitivamente ── */}
      {modalCerrar && jornadaEnCierre && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm">
            <div className="flex items-center gap-3 mb-4">
              <span className="text-2xl">📋</span>
              <h3 className="text-lg font-bold text-slate-800">Cerrar hoja anterior</h3>
            </div>
            <p className="text-sm text-slate-600 mb-4">
              Vas a cerrar definitivamente la hoja del{' '}
              <span className="font-semibold capitalize">{fmtFechaJornada(jornadaEnCierre.fecha)}</span>.
              Ya no se podrán registrar ventas en esa hoja.
            </p>
            <div className="bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-xs text-slate-600 mb-5">
              ✅ Usá este botón cuando hayas terminado todas las ventas rezagadas de esa hoja.
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => setModalCerrar(false)}
                disabled={cerrando}
                className="flex-1 py-2 rounded-lg border border-slate-300 text-slate-600 text-sm font-medium hover:bg-slate-50 transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={handleCerrar}
                disabled={cerrando}
                className="flex-1 py-2 rounded-lg bg-slate-700 text-white text-sm font-medium hover:bg-slate-800 transition-colors disabled:opacity-60"
              >
                {cerrando ? 'Cerrando…' : 'Cerrar hoja'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── Modal liquidar jornada ── */}
      {modalLiquidar && jornada && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 px-4">
          <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm">
            <div className="flex items-center gap-3 mb-4">
              <span className="text-2xl">📋</span>
              <h3 className="text-lg font-bold text-slate-800">Liquidar jornada</h3>
            </div>
            <p className="text-sm text-slate-600 mb-1">
              Vas a cerrar la jornada del{' '}
              <span className="font-semibold capitalize">{fmtFechaJornada(jornada.fecha)}</span>.
            </p>
            <p className="text-sm text-slate-500 mb-4">
              Se abrirá automáticamente la jornada del{' '}
              <span className="font-semibold capitalize">
                {fmtFechaJornada(
                  new Date(new Date(jornada.fecha + 'T12:00:00').getTime() + 86400000)
                    .toISOString().split('T')[0]
                )}
              </span>.
            </p>
            <div className="bg-amber-50 border border-amber-200 rounded-lg px-3 py-2 text-xs text-amber-700 mb-5">
              ⚠️ Las nuevas ventas y abonos quedarán registradas en el día siguiente a partir de este momento.
            </div>
            <div className="flex gap-3">
              <button
                onClick={() => setModalLiquidar(false)}
                disabled={liquidando}
                className="flex-1 py-2 rounded-lg border border-slate-300 text-slate-600 text-sm font-medium hover:bg-slate-50 transition-colors"
              >
                Cancelar
              </button>
              <button
                onClick={handleLiquidar}
                disabled={liquidando}
                className="flex-1 py-2 rounded-lg bg-amber-500 text-white text-sm font-medium hover:bg-amber-600 transition-colors disabled:opacity-60"
              >
                {liquidando ? 'Liquidando…' : 'Sí, liquidar'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ── Modal confirmación de anulación ── */}
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
