import { useEffect, useState } from 'react'
import {
  obtenerPrecioPublico, actualizarPrecioPublico,
  obtenerPrecioCosto,  actualizarPrecioCosto,
} from '../api/preciosApi'
import Card from '../components/ui/Card'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Alert from '../components/ui/Alert'
import Spinner from '../components/ui/Spinner'
import { fmt, parsePrecio } from '../utils/fmt'

const TIPOS_PUBLICO = [
  { key: 'precioExtra', field: 'precioExtra', label: 'EXTRA', color: 'amber',  emoji: '💛' },
  { key: 'precioAA',    field: 'precioAA',    label: 'AA',    color: 'yellow', emoji: '🌟' },
  { key: 'precioA',     field: 'precioA',     label: 'A',     color: 'blue',   emoji: '🤍' },
  { key: 'precioB',     field: 'precioB',     label: 'B',     color: 'slate',  emoji: '⚪' },
]

const TIPOS_COSTO = [
  { key: 'costoExtra', field: 'costoExtra', label: 'EXTRA', color: 'amber',  emoji: '💛' },
  { key: 'costoAA',    field: 'costoAA',    label: 'AA',    color: 'yellow', emoji: '🌟' },
  { key: 'costoA',     field: 'costoA',     label: 'A',     color: 'blue',   emoji: '🤍' },
  { key: 'costoB',     field: 'costoB',     label: 'B',     color: 'slate',  emoji: '⚪' },
]

const colorMap = {
  amber:  { bg: 'bg-amber-100',  text: 'text-amber-600'  },
  yellow: { bg: 'bg-yellow-100', text: 'text-yellow-600' },
  blue:   { bg: 'bg-blue-100',   text: 'text-blue-600'   },
  slate:  { bg: 'bg-slate-100',  text: 'text-slate-600'  },
}

const initFormPublico = { precioExtra: '', precioAA: '', precioA: '', precioB: '' }
const initFormCosto   = { costoExtra:  '', costoAA:  '', costoA:  '', costoB:  '' }

export default function Precios() {
  const [precios, setPrecios]     = useState(null)
  const [costos, setCostos]       = useState(null)
  const [loading, setLoading]     = useState(true)
  const [error, setError]         = useState('')
  const [success, setSuccess]     = useState('')
  const [saving, setSaving]       = useState(false)
  const [savingC, setSavingC]     = useState(false)
  const [form, setForm]           = useState(initFormPublico)
  const [formC, setFormC]         = useState(initFormCosto)

  const load = async () => {
    try {
      const [p, c] = await Promise.all([obtenerPrecioPublico(), obtenerPrecioCosto()])
      setPrecios(p)
      setForm({
        precioExtra: p.precioExtra ?? '',
        precioAA:    p.precioAA    ?? '',
        precioA:     p.precioA     ?? '',
        precioB:     p.precioB     ?? '',
      })
      setCostos(c)
      setFormC({
        costoExtra: c.costoExtra ?? '',
        costoAA:    c.costoAA    ?? '',
        costoA:     c.costoA     ?? '',
        costoB:     c.costoB     ?? '',
      })
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleActualizar = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      const guardados = await actualizarPrecioPublico({
        precioExtra: parsePrecio(form.precioExtra),
        precioAA:    parsePrecio(form.precioAA),
        precioA:     parsePrecio(form.precioA),
        precioB:     parsePrecio(form.precioB),
      })
      setPrecios(guardados)
      setSuccess('Precios públicos actualizados correctamente')
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  const handleActualizarCosto = async (e) => {
    e.preventDefault()
    setSavingC(true)
    setError('')
    try {
      const guardados = await actualizarPrecioCosto({
        costoExtra: parsePrecio(formC.costoExtra),
        costoAA:    parsePrecio(formC.costoAA),
        costoA:     parsePrecio(formC.costoA),
        costoB:     parsePrecio(formC.costoB),
      })
      setCostos(guardados)
      setSuccess('Precios de liquidación actualizados correctamente')
    } catch (e) {
      setError(e.message)
    } finally {
      setSavingC(false)
    }
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Precios</h1>
        <p className="text-slate-500 text-sm mt-1">Precios de venta y de liquidación por tipo de huevo</p>
      </div>

      <Alert type="error"   message={error}   onClose={() => setError('')} />
      <Alert type="success" message={success} onClose={() => setSuccess('')} />

      {loading ? <Spinner /> : (
        <div className="space-y-10">

          {/* ── Precios públicos ── */}
          <section>
            <h2 className="text-base font-bold text-slate-700 mb-4">💰 Precios de venta al público</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-4">
                <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider">Vigentes</h3>
                {TIPOS_PUBLICO.map(({ key, label, color, emoji }) => {
                  const { bg, text } = colorMap[color]
                  return (
                    <Card key={key} className="flex items-center gap-4">
                      <div className={`w-14 h-14 rounded-full ${bg} flex items-center justify-center text-2xl flex-shrink-0`}>
                        {emoji}
                      </div>
                      <div>
                        <p className="text-slate-500 text-sm">Canasta {label}</p>
                        <p className={`text-3xl font-bold ${text}`}>
                          {fmt(precios?.[key] ?? 0)}
                        </p>
                        <p className="text-xs text-slate-400 mt-1">precio por canasta</p>
                      </div>
                    </Card>
                  )
                })}
                {/* Precios derivados de media canasta (solo informativo, se calculan automáticamente) */}
                {[
                  { label: '½ EXTRA', precio: Number(precios?.precioExtra ?? 0) / 2, bg: 'bg-orange-100', text: 'text-orange-500' },
                  { label: '½ AA',    precio: Number(precios?.precioAA    ?? 0) / 2, bg: 'bg-lime-100',   text: 'text-lime-600'  },
                ].map(({ label, precio, bg, text }) => precio > 0 && (
                  <Card key={label} className="flex items-center gap-4 border border-dashed border-slate-200">
                    <div className={`w-14 h-14 rounded-full ${bg} flex items-center justify-center text-xl font-bold flex-shrink-0 ${text}`}>½</div>
                    <div>
                      <p className="text-slate-500 text-sm">Media canasta {label}</p>
                      <p className={`text-3xl font-bold ${text}`}>{fmt(precio)}</p>
                      <p className="text-xs text-slate-400 mt-1">derivado automático (÷ 2)</p>
                    </div>
                  </Card>
                ))}
                <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 text-xs text-blue-700">
                  ℹ️ Aplica a clientes <strong>NORMAL</strong>. Los clientes <strong>ESPECIAL</strong> tienen precios personalizados en su perfil.
                  Las medias canastas usan siempre la mitad del precio de su tipo padre.
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-4">Actualizar</h3>
                <Card>
                  <form onSubmit={handleActualizar}>
                    {TIPOS_PUBLICO.map(({ field, label }) => (
                      <Input
                        key={field}
                        label={`Precio canasta ${label} ($)`}
                        type="number" step="0.01" min="0"
                        placeholder="0.00"
                        value={form[field]}
                        onChange={e => setForm(p => ({ ...p, [field]: e.target.value }))}
                        required
                      />
                    ))}
                    <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-xs text-amber-700 mb-4">
                      ⚠️ Cambiar los precios afectará todas las nuevas ventas a partir de este momento.
                    </div>
                    <Button type="submit" loading={saving} className="w-full">
                      💰 Actualizar precios públicos
                    </Button>
                  </form>
                </Card>
              </div>
            </div>
          </section>

          {/* ── Precio de liquidación / costo ── */}
          <section>
            <h2 className="text-base font-bold text-slate-700 mb-4">📦 Precio de liquidación (costo)</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-4">
                <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider">Vigentes</h3>
                {TIPOS_COSTO.map(({ key, label, color, emoji }) => {
                  const { bg, text } = colorMap[color]
                  return (
                    <Card key={key} className="flex items-center gap-4">
                      <div className={`w-14 h-14 rounded-full ${bg} flex items-center justify-center text-2xl flex-shrink-0`}>
                        {emoji}
                      </div>
                      <div>
                        <p className="text-slate-500 text-sm">Canasta {label}</p>
                        <p className={`text-3xl font-bold ${text}`}>
                          {fmt(costos?.[key] ?? 0)}
                        </p>
                        <p className="text-xs text-slate-400 mt-1">costo por canasta</p>
                      </div>
                    </Card>
                  )
                })}
                {[
                  { label: '½ EXTRA', costo: Number(costos?.costoExtra ?? 0) / 2, bg: 'bg-orange-100', text: 'text-orange-500' },
                  { label: '½ AA',    costo: Number(costos?.costoAA    ?? 0) / 2, bg: 'bg-lime-100',   text: 'text-lime-600'  },
                ].map(({ label, costo, bg, text }) => costo > 0 && (
                  <Card key={label} className="flex items-center gap-4 border border-dashed border-slate-200">
                    <div className={`w-14 h-14 rounded-full ${bg} flex items-center justify-center text-xl font-bold flex-shrink-0 ${text}`}>½</div>
                    <div>
                      <p className="text-slate-500 text-sm">Media canasta {label}</p>
                      <p className={`text-3xl font-bold ${text}`}>{fmt(costo)}</p>
                      <p className="text-xs text-slate-400 mt-1">costo derivado (÷ 2)</p>
                    </div>
                  </Card>
                ))}
                <div className="bg-emerald-50 border border-emerald-200 rounded-lg p-3 text-xs text-emerald-700">
                  ℹ️ El costo se guarda en cada venta al momento de registrarla. Cambiar este valor no afecta ventas pasadas.
                  El costo de medias canastas se calcula automáticamente como la mitad del tipo padre.
                </div>
              </div>

              <div>
                <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-4">Actualizar</h3>
                <Card>
                  <form onSubmit={handleActualizarCosto}>
                    {TIPOS_COSTO.map(({ field, label }) => (
                      <Input
                        key={field}
                        label={`Costo canasta ${label} ($)`}
                        type="number" step="0.01" min="0"
                        placeholder="0.00"
                        value={formC[field]}
                        onChange={e => setFormC(p => ({ ...p, [field]: e.target.value }))}
                        required
                      />
                    ))}
                    <div className="bg-emerald-50 border border-emerald-200 rounded-lg p-3 text-xs text-emerald-700 mb-4">
                      ℹ️ Este precio se usa para calcular la ganancia neta por venta.
                    </div>
                    <Button type="submit" loading={savingC} className="w-full">
                      📦 Actualizar precio de costo
                    </Button>
                  </form>
                </Card>
              </div>
            </div>
          </section>

        </div>
      )}
    </div>
  )
}
