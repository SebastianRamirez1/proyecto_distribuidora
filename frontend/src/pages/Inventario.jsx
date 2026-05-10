import { useEffect, useState } from 'react'
import { obtenerInventario, cargarInventarioBulk } from '../api/inventarioApi'
import Card from '../components/ui/Card'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Alert from '../components/ui/Alert'
import Spinner from '../components/ui/Spinner'

const TIPOS = [
  { key: 'stockExtra', tipo: 'EXTRA', label: 'EXTRA',  color: 'amber',   emoji: '🥚' },
  { key: 'stockAA',    tipo: 'AA',    label: 'AA',     color: 'yellow',  emoji: '🥚' },
  { key: 'stockA',     tipo: 'A',     label: 'A',      color: 'blue',    emoji: '🥚' },
  { key: 'stockB',     tipo: 'B',     label: 'B',      color: 'slate',   emoji: '🥚' },
]

const colorMap = {
  amber:  { bg: 'bg-amber-100',  text: 'text-amber-600'  },
  yellow: { bg: 'bg-yellow-100', text: 'text-yellow-600' },
  blue:   { bg: 'bg-blue-100',   text: 'text-blue-600'   },
  slate:  { bg: 'bg-slate-100',  text: 'text-slate-600'  },
}

export default function Inventario() {
  const [inventario, setInventario] = useState(null)
  const [loading, setLoading]     = useState(true)
  const [error, setError]         = useState('')
  const [success, setSuccess]     = useState('')
  const [saving, setSaving]       = useState(false)

  const [form, setForm] = useState({ EXTRA: '', AA: '', A: '', B: '' })

  const load = async () => {
    try {
      setInventario(await obtenerInventario())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleCargar = async (e) => {
    e.preventDefault()
    const valores = {
      EXTRA: form.EXTRA ? Number(form.EXTRA) : 0,
      AA:    form.AA    ? Number(form.AA)    : 0,
      A:     form.A     ? Number(form.A)     : 0,
      B:     form.B     ? Number(form.B)     : 0,
    }
    const hayAlguno = Object.values(valores).some(v => v > 0)
    if (!hayAlguno) {
      setError('Ingresa al menos una cantidad mayor a 0')
      return
    }
    setSaving(true)
    setError('')
    try {
      // Una sola llamada atómica: todos los tipos en una transacción
      await cargarInventarioBulk({
        extra: valores.EXTRA,
        aa:    valores.AA,
        a:     valores.A,
        b:     valores.B,
      })
      // Siempre refrescar desde la BD para garantizar datos reales
      await load()
      setSuccess('Inventario cargado correctamente ✅')
      setForm({ EXTRA: '', AA: '', A: '', B: '' })
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Inventario</h1>
        <p className="text-slate-500 text-sm mt-1">Stock actual y carga de canastas</p>
      </div>

      <Alert type="error"   message={error}   onClose={() => setError('')} />
      <Alert type="success" message={success} onClose={() => setSuccess('')} />

      {loading ? <Spinner /> : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Stock actual */}
          <div className="space-y-4">
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider">Stock actual</h2>

            {TIPOS.map(({ key, label, color }) => {
              const stock = inventario?.[key] ?? 0
              const { bg, text } = colorMap[color]
              const dot = stock > 10 ? 'bg-emerald-400' : stock > 0 ? 'bg-amber-400' : 'bg-red-400'
              return (
                <Card key={key} className="flex items-center gap-4">
                  <div className={`w-14 h-14 rounded-full ${bg} flex items-center justify-center text-3xl flex-shrink-0`}>
                    🥚
                  </div>
                  <div className="flex-1">
                    <p className="text-slate-500 text-sm">Canastas {label}</p>
                    <p className={`text-4xl font-bold ${text}`}>{stock}</p>
                    <p className="text-xs text-slate-400 mt-1">unidades disponibles</p>
                  </div>
                  <div className={`w-3 h-3 rounded-full ${dot}`} />
                </Card>
              )
            })}

            <div className="bg-slate-50 rounded-lg p-4 text-sm text-slate-500">
              <p className="flex items-center gap-2"><span className="w-3 h-3 rounded-full bg-emerald-400 inline-block" /> &gt; 10 canastas — Stock suficiente</p>
              <p className="flex items-center gap-2 mt-1"><span className="w-3 h-3 rounded-full bg-amber-400 inline-block" /> 1 a 10 canastas — Stock bajo</p>
              <p className="flex items-center gap-2 mt-1"><span className="w-3 h-3 rounded-full bg-red-400 inline-block" /> 0 canastas — Sin stock</p>
            </div>
          </div>

          {/* Formulario carga */}
          <div>
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-4">Cargar inventario</h2>
            <Card>
              <form onSubmit={handleCargar}>
                {TIPOS.map(({ tipo, label, key }) => {
                  const stockActual = inventario?.[key] ?? 0
                  const aAgregar   = form[tipo] ? Number(form[tipo]) : 0
                  const nuevoTotal = stockActual + (aAgregar > 0 ? aAgregar : 0)
                  return (
                    <div key={tipo} className="mb-3">
                      <label className="label">
                        Canastas {label} a agregar
                        <span className="ml-2 text-xs font-normal text-slate-400">
                          (actual: {stockActual})
                        </span>
                      </label>
                      <input
                        className="input"
                        type="number" min="0"
                        placeholder="Ej: 100"
                        value={form[tipo]}
                        onChange={e => setForm(p => ({ ...p, [tipo]: e.target.value }))}
                      />
                      {aAgregar > 0 && (
                        <p className="text-xs text-emerald-600 mt-1 font-medium">
                          {stockActual} + {aAgregar} = <strong>{nuevoTotal}</strong> canastas
                        </p>
                      )}
                    </div>
                  )
                })}
                <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-xs text-amber-700 mb-4">
                  ℹ️ Los valores ingresados se <strong>suman</strong> al stock actual.
                  Si quieres tener exactamente 100 canastas y ahora tienes 30, ingresa 70.
                </div>
                <Button type="submit" loading={saving} className="w-full">
                  📦 Cargar inventario
                </Button>
              </form>
            </Card>
          </div>
        </div>
      )}
    </div>
  )
}
