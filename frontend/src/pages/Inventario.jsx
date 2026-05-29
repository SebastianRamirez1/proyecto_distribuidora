import { useEffect, useState } from 'react'
import { obtenerInventario, cargarInventarioBulk, ajustarInventario } from '../api/inventarioApi'
import Card from '../components/ui/Card'
import Button from '../components/ui/Button'
import Alert from '../components/ui/Alert'
import Spinner from '../components/ui/Spinner'

const TIPOS = [
  { key: 'stockExtra', tipo: 'EXTRA', label: 'EXTRA',  color: 'amber',  emoji: '🥚', tieneMedia: true  },
  { key: 'stockAA',    tipo: 'AA',    label: 'AA',     color: 'yellow', emoji: '🥚', tieneMedia: true  },
  { key: 'stockA',     tipo: 'A',     label: 'A',      color: 'blue',   emoji: '🥚', tieneMedia: false },
  { key: 'stockB',     tipo: 'B',     label: 'B',      color: 'slate',  emoji: '🥚', tieneMedia: false },
]

// Formatea el stock: 80.0 → "80", 79.5 → "79.5"
const fmtStock = (v) => (v == null ? 0 : Number.isInteger(Number(v)) ? Number(v) : Number(v).toFixed(1))

const colorMap = {
  amber:  { bg: 'bg-amber-100',  text: 'text-amber-600'  },
  yellow: { bg: 'bg-yellow-100', text: 'text-yellow-600' },
  blue:   { bg: 'bg-blue-100',   text: 'text-blue-600'   },
  slate:  { bg: 'bg-slate-100',  text: 'text-slate-600'  },
}

const initAgregar = { EXTRA: '', AA: '', A: '', B: '' }

export default function Inventario() {
  const [inventario, setInventario] = useState(null)
  const [loading, setLoading]     = useState(true)
  const [error, setError]         = useState('')
  const [success, setSuccess]     = useState('')
  const [saving, setSaving]       = useState(false)
  const [tab, setTab]             = useState('agregar') // 'agregar' | 'ajustar'

  // form "Agregar" — cantidades a sumar
  const [formAgregar, setFormAgregar] = useState(initAgregar)

  // form "Ajustar" — valores exactos (se pre-rellena con el stock actual)
  const [formAjustar, setFormAjustar] = useState({ stockExtra: '', stockAA: '', stockA: '', stockB: '' })

  const load = async () => {
    try {
      const inv = await obtenerInventario()
      setInventario(inv)
      // Pre-rellena el form de ajuste con los valores actuales
      setFormAjustar({
        stockExtra: inv.stockExtra,
        stockAA:    inv.stockAA,
        stockA:     inv.stockA,
        stockB:     inv.stockB,
      })
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  // ── Cargar (sumar) ──────────────────────────────────────────────────────

  const handleAgregar = async (e) => {
    e.preventDefault()
    const valores = {
      EXTRA: formAgregar.EXTRA ? Number(formAgregar.EXTRA) : 0,
      AA:    formAgregar.AA    ? Number(formAgregar.AA)    : 0,
      A:     formAgregar.A     ? Number(formAgregar.A)     : 0,
      B:     formAgregar.B     ? Number(formAgregar.B)     : 0,
    }
    if (!Object.values(valores).some(v => v > 0)) {
      setError('Ingresa al menos una cantidad mayor a 0')
      return
    }
    setSaving(true)
    setError('')
    try {
      const inv = await cargarInventarioBulk({ extra: valores.EXTRA, aa: valores.AA, a: valores.A, b: valores.B })
      setInventario(inv)
      setFormAjustar({ stockExtra: inv.stockExtra, stockAA: inv.stockAA, stockA: inv.stockA, stockB: inv.stockB })
      setSuccess('Inventario cargado correctamente ✅')
      setFormAgregar(initAgregar)
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  // ── Ajustar (setear valor exacto) ───────────────────────────────────────

  const handleAjustar = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError('')
    try {
      const inv = await ajustarInventario({
        stockExtra: Number(formAjustar.stockExtra),
        stockAA:    Number(formAjustar.stockAA),
        stockA:     Number(formAjustar.stockA),
        stockB:     Number(formAjustar.stockB),
      })
      setInventario(inv)
      setFormAjustar({ stockExtra: inv.stockExtra, stockAA: inv.stockAA, stockA: inv.stockA, stockB: inv.stockB })
      setSuccess('Stock corregido correctamente ✅')
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  // ── Render ──────────────────────────────────────────────────────────────

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Inventario</h1>
        <p className="text-slate-500 text-sm mt-1">Stock actual y gestión de canastas</p>
      </div>

      <Alert type="error"   message={error}   onClose={() => setError('')} />
      <Alert type="success" message={success} onClose={() => setSuccess('')} />

      {loading ? <Spinner /> : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">

          {/* ── Stock actual ── */}
          <div className="space-y-4">
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider">Stock actual</h2>

            {TIPOS.map(({ key, label, color, tieneMedia }) => {
              const stock = inventario?.[key] ?? 0
              const { bg, text } = colorMap[color]
              const dot = stock > 10 ? 'bg-emerald-400' : stock > 0 ? 'bg-amber-400' : 'bg-red-400'
              const tieneMediaAbierta = tieneMedia && !Number.isInteger(Number(stock))
              return (
                <Card key={key} className="flex items-center gap-4">
                  <div className={`w-14 h-14 rounded-full ${bg} flex items-center justify-center text-3xl flex-shrink-0`}>
                    🥚
                  </div>
                  <div className="flex-1">
                    <p className="text-slate-500 text-sm">Canastas {label}</p>
                    <p className={`text-4xl font-bold ${text}`}>{fmtStock(stock)}</p>
                    <p className="text-xs text-slate-400 mt-1">
                      {tieneMediaAbierta ? '½ canasta abierta incluida' : 'unidades disponibles'}
                    </p>
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

          {/* ── Formularios ── */}
          <div>
            {/* Tabs */}
            <div className="flex mb-4 bg-slate-100 rounded-lg p-1">
              <button
                onClick={() => setTab('agregar')}
                className={`flex-1 py-2 text-sm font-medium rounded-md transition-all ${tab === 'agregar' ? 'bg-white shadow text-amber-600' : 'text-slate-500 hover:text-slate-700'}`}
              >
                📦 Agregar canastas
              </button>
              <button
                onClick={() => setTab('ajustar')}
                className={`flex-1 py-2 text-sm font-medium rounded-md transition-all ${tab === 'ajustar' ? 'bg-white shadow text-rose-600' : 'text-slate-500 hover:text-slate-700'}`}
              >
                ✏️ Ajustar stock
              </button>
            </div>

            {tab === 'agregar' ? (
              <Card>
                <h3 className="font-semibold text-slate-700 mb-1">Cargar inventario</h3>
                <p className="text-xs text-slate-400 mb-4">Suma canastas al stock actual.</p>
                <form onSubmit={handleAgregar}>
                  {TIPOS.map(({ tipo, label, key }) => {
                    const stockActual = inventario?.[key] ?? 0
                    const aAgregar   = formAgregar[tipo] ? Number(formAgregar[tipo]) : 0
                    const nuevoTotal = stockActual + (aAgregar > 0 ? aAgregar : 0)
                    return (
                      <div key={tipo} className="mb-3">
                        <label className="label">
                          Canastas {label} a agregar
                          <span className="ml-2 text-xs font-normal text-slate-400">(actual: {stockActual})</span>
                        </label>
                        <input
                          className="input"
                          type="number" min="0"
                          placeholder="Ej: 100"
                          value={formAgregar[tipo]}
                          onChange={e => setFormAgregar(p => ({ ...p, [tipo]: e.target.value }))}
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
                    ℹ️ Los valores se <strong>suman</strong> al stock actual.
                  </div>
                  <Button type="submit" loading={saving} className="w-full">
                    📦 Cargar inventario
                  </Button>
                </form>
              </Card>
            ) : (
              <Card>
                <h3 className="font-semibold text-slate-700 mb-1">Ajustar stock</h3>
                <p className="text-xs text-slate-400 mb-4">
                  Corrige el stock a un valor exacto. Úsalo cuando alguien tomó canastas sin registrar una venta, hubo una merma, o el conteo físico no coincide.
                </p>
                <form onSubmit={handleAjustar}>
                  {TIPOS.map(({ key, label, tieneMedia }) => {
                    const actual   = inventario?.[key] ?? 0
                    const nuevo    = formAjustar[key] !== '' ? Number(formAjustar[key]) : actual
                    const diff     = Number((nuevo - actual).toFixed(1))
                    const diffLabel = diff === 0 ? null
                      : diff > 0 ? `+${diff} vs actual`
                      : `${diff} vs actual`
                    const diffColor = diff > 0 ? 'text-emerald-600' : diff < 0 ? 'text-rose-600' : ''
                    return (
                      <div key={key} className="mb-3">
                        <label className="label">
                          Canastas {label}
                          <span className="ml-2 text-xs font-normal text-slate-400">(actual: {fmtStock(actual)})</span>
                        </label>
                        <input
                          className="input"
                          type="number" min="0"
                          step={tieneMedia ? '0.5' : '1'}
                          placeholder={String(fmtStock(actual))}
                          value={formAjustar[key]}
                          onChange={e => setFormAjustar(p => ({ ...p, [key]: e.target.value }))}
                        />
                        {diffLabel && (
                          <p className={`text-xs mt-1 font-medium ${diffColor}`}>
                            Quedará en <strong>{fmtStock(nuevo)}</strong> canastas ({diffLabel})
                          </p>
                        )}
                      </div>
                    )
                  })}
                  <div className="bg-rose-50 border border-rose-200 rounded-lg p-3 text-xs text-rose-700 mb-4">
                    ⚠️ Esta acción <strong>reemplaza</strong> el stock actual con los valores ingresados.
                    No genera ningún movimiento de caja ni venta.
                  </div>
                  <Button type="submit" loading={saving} variant="danger" className="w-full">
                    ✏️ Guardar ajuste
                  </Button>
                </form>
              </Card>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
