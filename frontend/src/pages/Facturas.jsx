import { useEffect, useState, useMemo } from 'react'
import {
  listarFacturas, generarFactura, descargarPdfFactura,
  obtenerConfiguracion, actualizarConfiguracion,
} from '../api/facturasApi'
import Alert from '../components/ui/Alert'
import Spinner from '../components/ui/Spinner'
import Button from '../components/ui/Button'
import { fmt } from '../utils/fmt'

const fmtFecha = (iso) => iso ? new Date(iso).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' }) : '—'

const TIPO_COLOR  = { MANUAL: 'bg-slate-100 text-slate-700', ELECTRONICA: 'bg-blue-100 text-blue-700' }
const ESTADO_COLOR = { EMITIDA: 'bg-emerald-100 text-emerald-700', ENVIADA_DIAN: 'bg-blue-100 text-blue-700', ANULADA: 'bg-red-100 text-red-600' }

const REGIMENES = [
  'No responsable de IVA',
  'Responsable de IVA – Régimen Ordinario',
  'Régimen Simple de Tributación (SIMPLE)',
  'Gran contribuyente',
]

export default function Facturas() {
  const [tab, setTab] = useState('facturas')  // 'facturas' | 'config'

  // ── Facturas ────────────────────────────────────────────────────────────────
  const [facturas, setFacturas] = useState([])
  const [loading, setLoading]   = useState(true)
  const [error, setError]       = useState('')
  const [success, setSuccess]   = useState('')
  const [busqueda, setBusqueda] = useState('')
  const [descargando, setDescargando] = useState(null)

  // Modal generar factura
  const [modalGenerar, setModalGenerar] = useState(null) // { ventaId, nombreCliente, total }
  const [nitCliente, setNitCliente]     = useState('')
  const [tipoFactura, setTipoFactura]   = useState('MANUAL')
  const [generando, setGenerando]       = useState(false)

  // ── Configuración ────────────────────────────────────────────────────────────
  const [config, setConfig]       = useState(null)
  const [formConfig, setFormConfig] = useState({})
  const [guardando, setGuardando] = useState(false)

  // ── Carga inicial ────────────────────────────────────────────────────────────
  useEffect(() => {
    const init = async () => {
      try {
        const [f, c] = await Promise.all([listarFacturas(), obtenerConfiguracion()])
        setFacturas(f)
        setConfig(c)
        setFormConfig(c)
      } catch (e) {
        setError(e.message)
      } finally {
        setLoading(false)
      }
    }
    init()
  }, [])

  const recargarFacturas = async () => {
    try { setFacturas(await listarFacturas()) } catch (e) { setError(e.message) }
  }

  // ── Filtro ───────────────────────────────────────────────────────────────────
  const filtradas = useMemo(() => {
    const q = busqueda.toLowerCase().trim()
    if (!q) return facturas
    return facturas.filter(f =>
      f.numero?.toLowerCase().includes(q) ||
      f.nombreCliente?.toLowerCase().includes(q)
    )
  }, [facturas, busqueda])

  // ── Generar factura ──────────────────────────────────────────────────────────
  const confirmarGenerar = async () => {
    if (!modalGenerar) return
    setGenerando(true)
    setError('')
    try {
      const nueva = await generarFactura({
        ventaId:    modalGenerar.ventaId,
        nitCliente: nitCliente.trim() || null,
        tipo:       tipoFactura,
      })
      setSuccess(`Factura ${nueva.numero} generada correctamente ✅`)
      setModalGenerar(null)
      setNitCliente('')
      setTipoFactura('MANUAL')
      await recargarFacturas()
    } catch (e) {
      setError(e.message)
    } finally {
      setGenerando(false)
    }
  }

  // ── Descargar PDF ────────────────────────────────────────────────────────────
  const handleDescargar = async (f) => {
    setDescargando(f.id)
    try { await descargarPdfFactura(f.id, f.numero) }
    catch (e) { setError(e.message) }
    finally { setDescargando(null) }
  }

  // ── Guardar configuración ────────────────────────────────────────────────────
  const handleGuardarConfig = async (e) => {
    e.preventDefault()
    setGuardando(true)
    setError('')
    try {
      const saved = await actualizarConfiguracion({
        ...formConfig,
        resolucionDesde: formConfig.resolucionDesde ? Number(formConfig.resolucionDesde) : 1,
        resolucionHasta: formConfig.resolucionHasta ? Number(formConfig.resolucionHasta) : 9999,
        resolucionFecha: formConfig.resolucionFecha || null,
      })
      setConfig(saved)
      setFormConfig(saved)
      setSuccess('Configuración guardada correctamente ✅')
    } catch (e) {
      setError(e.message)
    } finally {
      setGuardando(false)
    }
  }

  const fc = (field) => (e) => setFormConfig(p => ({ ...p, [field]: e.target.value }))

  // ── Render ───────────────────────────────────────────────────────────────────
  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Facturación</h1>
          <p className="text-slate-500 text-sm mt-1">Facturas de venta — Normatividad colombiana</p>
        </div>
        {!config?.configurada && (
          <span className="bg-amber-100 text-amber-700 text-xs font-semibold px-3 py-1.5 rounded-full">
            ⚠️ Configura los datos del negocio primero
          </span>
        )}
      </div>

      <Alert type="error"   message={error}   onClose={() => setError('')} />
      <Alert type="success" message={success} onClose={() => setSuccess('')} />

      {/* Tabs */}
      <div className="flex mb-6 bg-slate-100 rounded-lg p-1 max-w-sm">
        <button
          onClick={() => setTab('facturas')}
          className={`flex-1 py-2 text-sm font-medium rounded-md transition-all ${tab === 'facturas' ? 'bg-white shadow text-amber-600' : 'text-slate-500'}`}
        >🧾 Facturas</button>
        <button
          onClick={() => setTab('config')}
          className={`flex-1 py-2 text-sm font-medium rounded-md transition-all ${tab === 'config' ? 'bg-white shadow text-amber-600' : 'text-slate-500'}`}
        >⚙️ Configuración</button>
      </div>

      {/* ── Tab: Facturas ─────────────────────────────────────────────────────── */}
      {tab === 'facturas' && (
        loading ? <Spinner /> : (
          <>
            {/* Buscador */}
            <div className="mb-4">
              <input
                type="text"
                className="input max-w-sm"
                placeholder="🔍 Buscar por número o cliente..."
                value={busqueda}
                onChange={e => setBusqueda(e.target.value)}
              />
            </div>

            {filtradas.length === 0 ? (
              <div className="bg-white rounded-xl border border-slate-200 p-12 text-center text-slate-400">
                {busqueda
                  ? 'No se encontraron facturas con ese criterio.'
                  : '🧾 No hay facturas emitidas aún. Genera facturas desde la página de Ventas.'}
              </div>
            ) : (
              <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead className="bg-slate-50 border-b border-slate-200">
                      <tr>
                        <th className="text-left px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">N° Factura</th>
                        <th className="text-left px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">Cliente</th>
                        <th className="text-left px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">Fecha</th>
                        <th className="text-right px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">Total</th>
                        <th className="text-center px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">Tipo</th>
                        <th className="text-center px-2 py-2.5 text-xs font-semibold text-slate-500 uppercase tracking-wider">Estado</th>
                        <th className="px-2 py-2.5"></th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {filtradas.map((f, i) => (
                        <tr key={f.id} className={i % 2 === 0 ? 'bg-white' : 'bg-slate-50/50'}>
                          <td className="px-2 py-2.5 font-mono font-semibold text-amber-600">{f.numero}</td>
                          <td className="px-2 py-2.5">
                            <p className="font-medium text-slate-800">{f.nombreCliente}</p>
                            <p className="text-xs text-slate-400">{f.nitCliente !== 'Sin NIT' ? `NIT: ${f.nitCliente}` : 'Sin NIT'}</p>
                          </td>
                          <td className="px-2 py-2.5 text-slate-500 text-xs">{fmtFecha(f.fechaEmision)}</td>
                          <td className="px-2 py-2.5 text-right font-semibold text-slate-800">{fmt(f.total)}</td>
                          <td className="px-2 py-2.5 text-center">
                            <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${TIPO_COLOR[f.tipo] ?? 'bg-slate-100 text-slate-600'}`}>
                              {f.tipo === 'ELECTRONICA' ? '⚡ Electrónica' : '📄 Manual'}
                            </span>
                          </td>
                          <td className="px-2 py-2.5 text-center">
                            <span className={`text-xs font-medium px-2.5 py-1 rounded-full ${ESTADO_COLOR[f.estado] ?? 'bg-slate-100 text-slate-600'}`}>
                              {f.estado === 'EMITIDA' ? 'Emitida' : f.estado === 'ENVIADA_DIAN' ? 'Enviada DIAN' : 'Anulada'}
                            </span>
                          </td>
                          <td className="px-2 py-2.5">
                            <button
                              onClick={() => handleDescargar(f)}
                              disabled={descargando === f.id}
                              className="text-xs bg-amber-100 text-amber-700 hover:bg-amber-200 font-medium px-3 py-1.5 rounded-lg transition-colors disabled:opacity-50"
                            >
                              {descargando === f.id ? '⏳' : '📥 PDF'}
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}
          </>
        )
      )}

      {/* ── Tab: Configuración ───────────────────────────────────────────────── */}
      {tab === 'config' && (
        <div className="max-w-2xl">
          <div className="bg-blue-50 border border-blue-200 rounded-xl p-4 mb-6 text-sm text-blue-800">
            <p className="font-semibold mb-1">📋 Datos requeridos por la DIAN</p>
            <p>Estos datos aparecerán en todas las facturas. La razón social y el NIT son obligatorios según la normatividad tributaria colombiana (Res. 042/2020).</p>
          </div>

          {!formConfig ? <Spinner /> : (
            <form onSubmit={handleGuardarConfig} className="space-y-6">
              {/* Datos del negocio */}
              <div className="bg-white border border-slate-200 rounded-xl p-5">
                <h3 className="font-semibold text-slate-700 mb-4">🏢 Datos del emisor</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="label">Razón social *</label>
                    <input className="input" value={formConfig.razonSocial ?? ''} onChange={fc('razonSocial')} required placeholder="Ej: Distribuidora de Huevos La Golondrina" />
                  </div>
                  <div>
                    <label className="label">NIT *</label>
                    <input className="input" value={formConfig.nit ?? ''} onChange={fc('nit')} required placeholder="Ej: 900123456-7" />
                  </div>
                  <div>
                    <label className="label">Dirección</label>
                    <input className="input" value={formConfig.direccion ?? ''} onChange={fc('direccion')} placeholder="Ej: Cra 5 N° 12-34" />
                  </div>
                  <div>
                    <label className="label">Ciudad</label>
                    <input className="input" value={formConfig.ciudad ?? ''} onChange={fc('ciudad')} placeholder="Ej: Bogotá, D.C." />
                  </div>
                  <div>
                    <label className="label">Teléfono</label>
                    <input className="input" value={formConfig.telefono ?? ''} onChange={fc('telefono')} placeholder="Ej: 3001234567" />
                  </div>
                  <div>
                    <label className="label">Régimen tributario</label>
                    <select className="input" value={formConfig.regimen ?? ''} onChange={fc('regimen')}>
                      {REGIMENES.map(r => <option key={r} value={r}>{r}</option>)}
                    </select>
                  </div>
                </div>
              </div>

              {/* Resolución DIAN */}
              <div className="bg-white border border-slate-200 rounded-xl p-5">
                <h3 className="font-semibold text-slate-700 mb-1">📜 Resolución DIAN</h3>
                <p className="text-xs text-slate-500 mb-4">Si aún no tienes resolución, déjalo en blanco. Puedes completarlo cuando la obtengas.</p>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="label">N° Resolución</label>
                    <input className="input" value={formConfig.resolucionNumero ?? ''} onChange={fc('resolucionNumero')} placeholder="Ej: 18764000001234" />
                  </div>
                  <div>
                    <label className="label">Fecha resolución</label>
                    <input type="date" className="input" value={formConfig.resolucionFecha ?? ''} onChange={fc('resolucionFecha')} />
                  </div>
                  <div>
                    <label className="label">Prefijo</label>
                    <input className="input" value={formConfig.resolucionPrefijo ?? 'FAC'} onChange={fc('resolucionPrefijo')} placeholder="FAC" maxLength={10} />
                  </div>
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="label">Desde N°</label>
                      <input type="number" className="input" value={formConfig.resolucionDesde ?? 1} onChange={fc('resolucionDesde')} min={1} />
                    </div>
                    <div>
                      <label className="label">Hasta N°</label>
                      <input type="number" className="input" value={formConfig.resolucionHasta ?? 9999} onChange={fc('resolucionHasta')} min={1} />
                    </div>
                  </div>
                </div>
              </div>

              <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-xs text-amber-800">
                ℹ️ Los huevos están <strong>excluidos de IVA</strong> según el Art. 424 del E.T. (canasta familiar). Las facturas se generan sin IVA automáticamente.
              </div>

              <Button type="submit" loading={guardando} className="w-full md:w-auto px-8">
                💾 Guardar configuración
              </Button>
            </form>
          )}
        </div>
      )}

      {/* ── Modal: Generar factura ─────────────────────────────────────────────── */}
      {modalGenerar && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-sm p-6">
            <h2 className="text-lg font-bold text-slate-800 mb-1">Generar factura</h2>
            <p className="text-sm text-slate-500 mb-4">{modalGenerar.nombreCliente}</p>

            <div className="bg-slate-50 rounded-lg px-4 py-2 mb-4 flex justify-between items-center">
              <span className="text-sm text-slate-600">Total venta #{modalGenerar.ventaId}</span>
              <span className="font-bold text-slate-800 text-lg">{fmt(modalGenerar.total)}</span>
            </div>

            <label className="label">NIT / CC del cliente (opcional)</label>
            <input
              type="text"
              className="input mb-4"
              placeholder="Ej: 800123456-1 o 1012345678"
              value={nitCliente}
              onChange={e => setNitCliente(e.target.value)}
            />

            <label className="label">Tipo de factura</label>
            <div className="flex gap-2 mb-5">
              {[
                { val: 'MANUAL',     label: '📄 Manual',     desc: 'PDF local, sin DIAN' },
                { val: 'ELECTRONICA', label: '⚡ Electrónica', desc: 'PDF + registro DIAN' },
              ].map(op => (
                <button
                  key={op.val}
                  type="button"
                  onClick={() => setTipoFactura(op.val)}
                  className={`flex-1 py-2 rounded-lg text-xs font-medium border transition-colors text-center ${
                    tipoFactura === op.val
                      ? 'bg-amber-500 border-amber-500 text-white'
                      : 'border-slate-300 text-slate-600 hover:bg-slate-50'
                  }`}
                >
                  <div>{op.label}</div>
                  <div className={`text-xs mt-0.5 ${tipoFactura === op.val ? 'text-amber-100' : 'text-slate-400'}`}>{op.desc}</div>
                </button>
              ))}
            </div>

            {tipoFactura === 'ELECTRONICA' && (
              <div className="bg-blue-50 border border-blue-200 rounded-lg px-3 py-2 text-xs text-blue-700 mb-4">
                ℹ️ La integración con DIAN vía PTH está en desarrollo. Por ahora se genera el PDF y se registra como factura electrónica pendiente de envío.
              </div>
            )}

            <div className="flex gap-3">
              <button
                onClick={() => { setModalGenerar(null); setNitCliente(''); setTipoFactura('MANUAL') }}
                className="flex-1 px-4 py-2 rounded-lg border border-slate-300 text-slate-600 text-sm font-medium hover:bg-slate-50"
              >Cancelar</button>
              <Button onClick={confirmarGenerar} loading={generando} className="flex-1">
                Generar y descargar
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

// Exporta la función para abrir el modal desde Ventas.jsx
export { }
