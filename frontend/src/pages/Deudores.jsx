import { useEffect, useState, useMemo } from 'react'
import { obtenerDeudores } from '../api/creditosApi'
import { registrarAbono } from '../api/ventasApi'
import Alert from '../components/ui/Alert'
import Spinner from '../components/ui/Spinner'
import Button from '../components/ui/Button'

const fmt = (n) =>
  new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(n)

export default function Deudores() {
  const [deudores, setDeudores] = useState([])
  const [loading, setLoading]   = useState(true)
  const [error, setError]       = useState('')
  const [success, setSuccess]   = useState('')
  const [busqueda, setBusqueda] = useState('')

  // Modal abono
  const [modal, setModal]     = useState(null)   // { clienteId, nombre, saldo }
  const [monto, setMonto]     = useState('')
  const [saving, setSaving]   = useState(false)

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

  const abrirModal = (d) => {
    setModal({ clienteId: d.clienteId, nombre: d.nombreCliente, saldo: d.saldoPendiente })
    setMonto('')
  }

  const confirmarAbono = async () => {
    const valor = Number(monto)
    if (!valor || valor <= 0) { setError('Ingresa un monto válido'); return }
    if (valor > Number(modal.saldo)) { setError(`El abono no puede superar el saldo de ${fmt(modal.saldo)}`); return }
    setSaving(true)
    setError('')
    try {
      await registrarAbono({ clienteId: modal.clienteId, monto: valor })
      setSuccess(`Abono de ${fmt(valor)} registrado para ${modal.nombre} ✅`)
      setModal(null)
      await cargar()
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

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
            <div className="bg-white rounded-xl border border-slate-200 p-4 shadow-sm">
              <p className="text-xs text-slate-500 uppercase tracking-wider mb-1">Total deudores</p>
              <p className="text-3xl font-bold text-slate-800">{filtrados.length}</p>
            </div>
            <div className="bg-white rounded-xl border border-red-100 p-4 shadow-sm sm:col-span-2">
              <p className="text-xs text-slate-500 uppercase tracking-wider mb-1">
                {busqueda ? 'Deuda filtrada' : 'Deuda total pendiente'}
              </p>
              <p className="text-3xl font-bold text-red-600">{fmt(totalDeuda)}</p>
            </div>
          </div>

          {/* Buscador */}
          <div className="mb-4">
            <input
              type="text"
              className="input max-w-sm"
              placeholder="🔍 Buscar cliente..."
              value={busqueda}
              onChange={e => setBusqueda(e.target.value)}
            />
          </div>

          {/* Tabla */}
          {filtrados.length === 0 ? (
            <div className="bg-white rounded-xl border border-slate-200 p-12 text-center text-slate-400">
              {busqueda
                ? 'No se encontraron deudores con ese nombre.'
                : '🎉 ¡No hay deudores pendientes!'}
            </div>
          ) : (
            <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead className="bg-slate-50 border-b border-slate-200">
                    <tr>
                      <th className="text-left px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Cliente</th>
                      <th className="text-right px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Deuda total</th>
                      <th className="text-right px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Ya pagó</th>
                      <th className="text-right px-4 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">Saldo pendiente</th>
                      <th className="px-4 py-3"></th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {filtrados.map((d, i) => {
                      const pct = Number(d.montoTotal) > 0
                        ? Math.round((Number(d.montoPagado) / Number(d.montoTotal)) * 100)
                        : 0
                      return (
                        <tr key={d.clienteId} className={i % 2 === 0 ? 'bg-white' : 'bg-slate-50/50'}>
                          <td className="px-4 py-3 font-medium text-slate-800">
                            {d.nombreCliente}
                          </td>
                          <td className="px-4 py-3 text-right text-slate-600">
                            {fmt(d.montoTotal)}
                          </td>
                          <td className="px-4 py-3 text-right">
                            <span className="text-emerald-600 font-medium">{fmt(d.montoPagado)}</span>
                            <div className="mt-1 h-1.5 w-20 ml-auto bg-slate-200 rounded-full overflow-hidden">
                              <div
                                className="h-full bg-emerald-400 rounded-full"
                                style={{ width: `${pct}%` }}
                              />
                            </div>
                          </td>
                          <td className="px-4 py-3 text-right font-bold text-red-600">
                            {fmt(d.saldoPendiente)}
                          </td>
                          <td className="px-4 py-3 text-right">
                            <button
                              onClick={() => abrirModal(d)}
                              className="text-xs bg-amber-100 text-amber-700 hover:bg-amber-200 font-medium px-3 py-1.5 rounded-lg transition-colors"
                            >
                              💵 Registrar abono
                            </button>
                          </td>
                        </tr>
                      )
                    })}
                  </tbody>
                  <tfoot className="bg-slate-50 border-t-2 border-slate-200">
                    <tr>
                      <td className="px-4 py-3 font-semibold text-slate-600">
                        Total ({filtrados.length} clientes)
                      </td>
                      <td className="px-4 py-3 text-right font-semibold text-slate-600">
                        {fmt(filtrados.reduce((s, d) => s + Number(d.montoTotal), 0))}
                      </td>
                      <td className="px-4 py-3 text-right font-semibold text-emerald-600">
                        {fmt(filtrados.reduce((s, d) => s + Number(d.montoPagado), 0))}
                      </td>
                      <td className="px-4 py-3 text-right font-bold text-red-600">
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

      {/* Modal abono */}
      {modal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6">
            <h2 className="text-lg font-bold text-slate-800 mb-1">Registrar abono</h2>
            <p className="text-sm text-slate-500 mb-4">{modal.nombre}</p>

            <div className="bg-red-50 rounded-lg px-4 py-2 mb-4 flex justify-between items-center">
              <span className="text-sm text-slate-600">Saldo pendiente</span>
              <span className="font-bold text-red-600 text-lg">{fmt(modal.saldo)}</span>
            </div>

            <label className="label">Monto del abono</label>
            <input
              type="number"
              min="1"
              max={modal.saldo}
              className="input mb-4"
              placeholder="Ej: 50000"
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
    </div>
  )
}
