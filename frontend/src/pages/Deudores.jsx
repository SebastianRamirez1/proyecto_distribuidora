import { useEffect, useState, useMemo } from 'react'
import { obtenerDeudores, obtenerHistorialAbonos } from '../api/creditosApi'
import { registrarAbono } from '../api/ventasApi'
import Alert from '../components/ui/Alert'
import Spinner from '../components/ui/Spinner'
import Button from '../components/ui/Button'
import { fmt } from '../utils/fmt'

const fmtFecha = (iso) => {
  if (!iso) return '-'
  return new Date(iso).toLocaleDateString('es-PE', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

const MEDIO_COLOR = {
  EFECTIVO:      'bg-emerald-100 text-emerald-700',
  TRANSFERENCIA: 'bg-blue-100 text-blue-700',
}

export default function Deudores() {
  const [deudores, setDeudores] = useState([])
  const [loading, setLoading]   = useState(true)
  const [error, setError]       = useState('')
  const [success, setSuccess]   = useState('')
  const [busqueda, setBusqueda] = useState('')

  // Modal abono
  const [modal, setModal]         = useState(null)   // { clienteId, nombre, saldo }
  const [monto, setMonto]         = useState('')
  const [medioPago, setMedioPago] = useState('EFECTIVO')
  const [saving, setSaving]       = useState(false)

  // Modal historial
  const [historialModal, setHistorialModal]     = useState(null)  // { clienteId, nombre }
  const [historial, setHistorial]               = useState([])
  const [loadingHistorial, setLoadingHistorial] = useState(false)

  const cargar = async () => {
    setLoading(true)
    try {
      setDeudores(await obtenerDeudores())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { cargar() }, [])

  const filtrados = useMemo(() => {
    const q = busqueda.toLowerCase().trim()
    if (!q) return deudores
    return deudores.filter(d => d.nombreCliente.toLowerCase().includes(q))
  }, [deudores, busqueda])

  const totalDeuda = useMemo(
    () => filtrados.reduce((sum, d) => sum + Number(d.saldoPendiente), 0),
    [filtrados]
  )

  // ── Abono ─────────────────────────────────────────────────────────────────

  const abrirModal = (d) => {
    setModal({ clienteId: d.clienteId, nombre: d.nombreCliente, saldo: d.saldoPendiente })
    setMonto('')
    setMedioPago('EFECTIVO')
  }

  const confirmarAbono = async () => {
    const valor = Number(monto)
    if (!valor || valor <= 0) { setError('Ingresa un monto válido'); return }
    if (valor > Number(modal.saldo)) { setError(`El abono no puede superar el saldo de ${fmt(modal.saldo)}`); return }
    setSaving(true)
    setError('')
    try {
      await registrarAbono({ clienteId: modal.clienteId, monto: valor, medioPago })
      setSuccess(`Abono de ${fmt(valor)} registrado para ${modal.nombre}`)
      setModal(null)
      await cargar()
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  // ── Historial ─────────────────────────────────────────────────────────────

  const abrirHistorial = async (d) => {
    setHistorialModal({ clienteId: d.clienteId, nombre: d.nombreCliente })
    setHistorial([])
    setLoadingHistorial(true)
    try {
      setHistorial(await obtenerHistorialAbonos(d.clienteId))
    } catch (e) {
      setError(e.message)
    } finally {
      setLoadingHistorial(false)
    }
  }

  const totalHistorial = useMemo(
    () => historial.reduce((s, a) => s + Number(a.monto), 0),
    [historial]
  )

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Reporte de deudores</h1>
        <p className="text-slate-500 text-sm mt-1">Clientes con saldo fiado pendiente</p>
      </div>

      <Alert type="error"   message={error}   onClose={() => setError('')} />
      <Alert type="success" message={success} onClose={() => setSuccess('')} />

      {loading ? <Spinner /> : (
        <>
          {/* Tarjetas resumen */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
            <div className="card border-l-4 border-l-blue-500">
              <p className="text-xs text-slate-500 uppercase tracking-wider mb-1">Total deudores</p>
              <p className="text-2xl font-bold text-blue-600">{filtrados.length}</p>
            </div>
            <div className="card border-l-4 border-l-rose-500 sm:col-span-2">
              <p className="text-xs text-slate-500 uppercase tracking-wider mb-1">
                {busqueda ? 'Deuda filtrada' : 'Deuda total pendiente'}
              </p>
              <p className="text-2xl font-bold text-rose-600">{fmt(totalDeuda)}</p>
            </div>
          </div>

          {/* Buscador */}
          <div className="mb-4">
            <input
              type="text"
              className="input max-w-sm"
              placeholder="Buscar cliente..."
              value={busqueda}
              onChange={e => setBusqueda(e.target.value)}
            />
          </div>

          {/* Tabla */}
          {filtrados.length === 0 ? (
            <div className="bg-white rounded-xl border border-slate-200 p-12 text-center text-slate-400">
              {busqueda
                ? 'No se encontraron deudores con ese nombre.'
                : 'No hay deudores pendientes.'}
            </div>
          ) : (
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead className="bg-slate-50 border-b border-slate-200">
                    <tr>
                      <th className="text-left px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">Cliente</th>
                      <th className="text-right px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">Deuda total</th>
                      <th className="text-right px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">Ya pagó</th>
                      <th className="text-right px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">Saldo pendiente</th>
                      <th className="px-2 py-2.5"></th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {filtrados.map((d, i) => {
                      const pct = Number(d.montoTotal) > 0
                        ? Math.round((Number(d.montoPagado) / Number(d.montoTotal)) * 100)
                        : 0
                      return (
                        <tr key={d.clienteId} className={i % 2 === 0 ? 'bg-white' : 'bg-slate-50/50'}>
                          <td className="px-2 py-2.5 font-medium text-slate-800">
                            {d.nombreCliente}
                          </td>
                          <td className="px-2 py-2.5 text-right text-slate-600">
                            {fmt(d.montoTotal)}
                          </td>
                          <td className="px-2 py-2.5 text-right">
                            <span className="text-emerald-600 font-medium">{fmt(d.montoPagado)}</span>
                            <div className="mt-1 h-1.5 w-20 ml-auto bg-slate-200 rounded-full overflow-hidden">
                              <div
                                className="h-full bg-emerald-400 rounded-full"
                                style={{ width: `${pct}%` }}
                              />
                            </div>
                          </td>
                          <td className="px-2 py-2.5 text-right font-bold text-rose-600">
                            {fmt(d.saldoPendiente)}
                          </td>
                          <td className="px-2 py-2.5">
                            <div className="flex items-center justify-end gap-2">
                              <button
                                onClick={() => abrirHistorial(d)}
                                className="text-xs bg-slate-100 text-slate-600 hover:bg-slate-200 font-medium px-3 py-1.5 rounded-lg transition-colors min-h-[36px]"
                                title="Ver historial de abonos"
                              >
                                Historial
                              </button>
                              <button
                                onClick={() => abrirModal(d)}
                                className="text-xs bg-amber-100 text-amber-700 hover:bg-amber-200 font-medium px-3 py-1.5 rounded-lg transition-colors min-h-[36px]"
                              >
                                Abonar
                              </button>
                            </div>
                          </td>
                        </tr>
                      )
                    })}
                  </tbody>
                  <tfoot className="bg-slate-50 border-t-2 border-slate-200">
                    <tr>
                      <td className="px-2 py-2.5 font-semibold text-slate-600">
                        Total ({filtrados.length} clientes)
                      </td>
                      <td className="px-2 py-2.5 text-right font-semibold text-slate-600">
                        {fmt(filtrados.reduce((s, d) => s + Number(d.montoTotal), 0))}
                      </td>
                      <td className="px-2 py-2.5 text-right font-semibold text-emerald-600">
                        {fmt(filtrados.reduce((s, d) => s + Number(d.montoPagado), 0))}
                      </td>
                      <td className="px-2 py-2.5 text-right font-bold text-rose-600">
                        {fmt(totalDeuda)}
                      </td>
                      <td />
                    </tr>
                  </tfoot>
                </table>
              </div>
            </div>
          )}
        </>
      )}

      {/* ── Modal: registrar abono ─────────────────────────────────────────── */}
      {modal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-xl shadow-lg border border-slate-200 w-full max-w-sm p-6">
            <h2 className="text-lg font-bold text-slate-800 mb-1">Registrar abono</h2>
            <p className="text-sm text-slate-500 mb-4">{modal.nombre}</p>

            <div className="bg-red-50 rounded-lg px-4 py-2 mb-4 flex justify-between items-center">
              <span className="text-sm text-slate-600">Saldo pendiente</span>
              <span className="font-bold text-red-600 text-lg">{fmt(modal.saldo)}</span>
            </div>

            <label className="label">Medio de pago</label>
            <div className="flex gap-2 mb-4">
              {['EFECTIVO', 'TRANSFERENCIA'].map(op => (
                <button
                  key={op}
                  type="button"
                  onClick={() => setMedioPago(op)}
                  className={`flex-1 py-2 rounded-lg text-sm font-medium border transition-colors ${
                    medioPago === op
                      ? 'bg-amber-500 border-amber-500 text-white'
                      : 'border-slate-300 text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  {op === 'EFECTIVO' ? 'Efectivo' : 'Transferencia'}
                </button>
              ))}
            </div>

            <label className="label">Monto del abono</label>
            <input
              type="number"
              min="1"
              max={modal.saldo}
              className="input mb-4"
              placeholder="Ej: 50.00"
              value={monto}
              onChange={e => setMonto(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && confirmarAbono()}
              autoFocus
            />

            <div className="flex gap-3">
              <button
                onClick={() => setModal(null)}
                className="flex-1 px-4 py-2 rounded-lg border border-slate-300 text-slate-600 hover:bg-slate-50 text-sm font-medium"
              >
                Cancelar
              </button>
              <Button onClick={confirmarAbono} loading={saving} className="flex-1">
                Confirmar abono
              </Button>
            </div>
          </div>
        </div>
      )}

      {/* ── Modal: historial de abonos ────────────────────────────────────── */}
      {historialModal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-xl shadow-lg border border-slate-200 w-full max-w-lg p-6 max-h-[85vh] flex flex-col">
            {/* Header */}
            <div className="flex items-start justify-between mb-4">
              <div>
                <h2 className="text-lg font-bold text-slate-800">Historial de abonos</h2>
                <p className="text-sm text-slate-500">{historialModal.nombre}</p>
              </div>
              <button
                onClick={() => setHistorialModal(null)}
                className="text-slate-400 hover:text-slate-600 p-1 min-w-[44px] min-h-[44px] flex items-center justify-center rounded"
                aria-label="Cerrar historial"
              >
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            {/* Contenido scrolleable */}
            <div className="overflow-y-auto flex-1">
              {loadingHistorial ? (
                <div className="flex justify-center py-10">
                  <div className="animate-spin h-6 w-6 border-2 border-amber-500 border-t-transparent rounded-full" />
                </div>
              ) : historial.length === 0 ? (
                <div className="text-center text-slate-400 py-10">
                  <p className="text-sm">Este cliente no tiene abonos registrados.</p>
                </div>
              ) : (
                <div className="space-y-2">
                  {historial.map((a, i) => (
                    <div
                      key={a.id}
                      className="flex items-center justify-between px-4 py-3 rounded-lg bg-slate-50 border border-slate-100"
                    >
                      <div className="flex items-center gap-3">
                        <span className="text-slate-400 text-xs font-mono w-5 text-right">{i + 1}</span>
                        <div>
                          <p className="text-sm font-semibold text-slate-800">{fmt(a.monto)}</p>
                          <p className="text-xs text-slate-400">{fmtFecha(a.fecha)}</p>
                        </div>
                      </div>
                      <span className={`text-xs font-medium px-2 py-0.5 rounded ${MEDIO_COLOR[a.medioPago] ?? 'bg-slate-100 text-slate-600'}`}>
                        {a.medioPago === 'EFECTIVO' ? 'Efectivo' : 'Transferencia'}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Footer con total */}
            {historial.length > 0 && (
              <div className="border-t border-slate-200 pt-4 mt-4 flex justify-between items-center">
                <span className="text-sm text-slate-500">
                  {historial.length} abono{historial.length !== 1 ? 's' : ''} registrado{historial.length !== 1 ? 's' : ''}
                </span>
                <span className="font-bold text-emerald-600">{fmt(totalHistorial)} pagado</span>
              </div>
            )}

            <button
              onClick={() => setHistorialModal(null)}
              className="mt-4 w-full py-2 rounded-lg border border-slate-300 text-slate-600 hover:bg-slate-50 text-sm font-medium transition-colors"
            >
              Cerrar
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
