import { useEffect, useState } from 'react'
import { obtenerPrecioPublico, actualizarPrecioPublico } from '../api/preciosApi'
import Card from '../components/ui/Card'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Alert from '../components/ui/Alert'
import Spinner from '../components/ui/Spinner'

const TIPOS = [
  { key: 'precioExtra', field: 'precioExtra', label: 'EXTRA',  color: 'amber',   emoji: '💛' },
  { key: 'precioAA',    field: 'precioAA',    label: 'AA',     color: 'yellow',  emoji: '🌟' },
  { key: 'precioA',     field: 'precioA',     label: 'A',      color: 'blue',    emoji: '🤍' },
  { key: 'precioB',     field: 'precioB',     label: 'B',      color: 'slate',   emoji: '⚪' },
]

const colorMap = {
  amber:  { bg: 'bg-amber-100',  text: 'text-amber-600'  },
  yellow: { bg: 'bg-yellow-100', text: 'text-yellow-600' },
  blue:   { bg: 'bg-blue-100',   text: 'text-blue-600'   },
  slate:  { bg: 'bg-slate-100',  text: 'text-slate-600'  },
}

const initForm = { precioExtra: '', precioAA: '', precioA: '', precioB: '' }

export default function Precios() {
  const [precios, setPrecios]   = useState(null)
  const [loading, setLoading]   = useState(true)
  const [error, setError]       = useState('')
  const [success, setSuccess]   = useState('')
  const [saving, setSaving]     = useState(false)
  const [form, setForm]         = useState(initForm)

  const load = async () => {
    try {
      const p = await obtenerPrecioPublico()
      setPrecios(p)
      setForm({
        precioExtra: p.precioExtra ?? '',
        precioAA:    p.precioAA    ?? '',
        precioA:     p.precioA     ?? '',
        precioB:     p.precioB     ?? '',
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
      await actualizarPrecioPublico({
        precioExtra: Number(form.precioExtra),
        precioAA:    Number(form.precioAA),
        precioA:     Number(form.precioA),
        precioB:     Number(form.precioB),
      })
      await load()
      setSuccess('Precios actualizados correctamente ✅')
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-800">Precios Públicos</h1>
        <p className="text-slate-500 text-sm mt-1">Precio de venta al público por tipo de huevo</p>
      </div>

      <Alert type="error"   message={error}   onClose={() => setError('')} />
      <Alert type="success" message={success} onClose={() => setSuccess('')} />

      {loading ? <Spinner /> : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Precios vigentes */}
          <div className="space-y-4">
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider">Precios vigentes</h2>

            {TIPOS.map(({ key, label, color, emoji }) => {
              const { bg, text } = colorMap[color]
              return (
                <Card key={key} className="flex items-center gap-4">
                  <div className={`w-14 h-14 rounded-full ${bg} flex items-center justify-center text-2xl flex-shrink-0`}>
                    {emoji}
                  </div>
                  <div>
                    <p className="text-slate-500 text-sm">Canasta {label}</p>
                    <p className={`text-3xl font-bold ${text}`}>
                      S/ {Number(precios?.[key] ?? 0).toFixed(2)}
                    </p>
                    <p className="text-xs text-slate-400 mt-1">precio por canasta</p>
                  </div>
                </Card>
              )
            })}

            <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 text-xs text-blue-700">
              ℹ️ Estos precios aplican a clientes de tipo <strong>NORMAL</strong>. Los clientes <strong>ESPECIAL</strong> tienen precios personalizados en su perfil.
            </div>
          </div>

          {/* Formulario actualizar */}
          <div>
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-4">Actualizar precios</h2>
            <Card>
              <form onSubmit={handleActualizar}>
                {TIPOS.map(({ field, label }) => (
                  <Input
                    key={field}
                    label={`Precio canasta ${label} (S/)`}
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
                  💰 Actualizar precios
                </Button>
              </form>
            </Card>
          </div>
        </div>
      )}
    </div>
  )
}
