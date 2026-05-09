import { useEffect, useState } from 'react'
import { obtenerPrecioPublico, actualizarPrecioPublico } from '../api/preciosApi'
import Card from '../components/ui/Card'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Alert from '../components/ui/Alert'
import Spinner from '../components/ui/Spinner'

export default function Precios() {
  const [precios, setPrecios] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [saving, setSaving] = useState(false)

  const [form, setForm] = useState({ precioExtra: '', precioNormal: '' })

  const load = async () => {
    try {
      const p = await obtenerPrecioPublico()
      setPrecios(p)
      setForm({ precioExtra: p.precioExtra ?? '', precioNormal: p.precioNormal ?? '' })
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
        precioNormal: Number(form.precioNormal),
      })
      setSuccess('Precios actualizados correctamente ✅')
      load()
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
        <p className="text-slate-500 text-sm mt-1">Precio de venta al público para clientes NORMAL</p>
      </div>

      <Alert type="error"   message={error}   onClose={() => setError('')} />
      <Alert type="success" message={success} onClose={() => setSuccess('')} />

      {loading ? <Spinner /> : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Precios actuales */}
          <div className="space-y-4">
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider">Precios vigentes</h2>

            <Card className="flex items-center gap-4">
              <div className="w-14 h-14 rounded-full bg-amber-100 flex items-center justify-center text-2xl flex-shrink-0">
                💛
              </div>
              <div>
                <p className="text-slate-500 text-sm">Canasta EXTRA</p>
                <p className="text-3xl font-bold text-amber-600">
                  S/ {Number(precios?.precioExtra ?? 0).toFixed(2)}
                </p>
                <p className="text-xs text-slate-400 mt-1">precio por canasta</p>
              </div>
            </Card>

            <Card className="flex items-center gap-4">
              <div className="w-14 h-14 rounded-full bg-blue-100 flex items-center justify-center text-2xl flex-shrink-0">
                🤍
              </div>
              <div>
                <p className="text-slate-500 text-sm">Canasta NORMAL</p>
                <p className="text-3xl font-bold text-blue-600">
                  S/ {Number(precios?.precioNormal ?? 0).toFixed(2)}
                </p>
                <p className="text-xs text-slate-400 mt-1">precio por canasta</p>
              </div>
            </Card>

            <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 text-xs text-blue-700">
              ℹ️ Estos precios aplican únicamente a clientes de tipo <strong>NORMAL</strong>. Los clientes <strong>ESPECIAL</strong> tienen precios personalizados configurados en cada perfil.
            </div>
          </div>

          {/* Formulario actualizar */}
          <div>
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-4">Actualizar precios</h2>
            <Card>
              <form onSubmit={handleActualizar}>
                <Input
                  label="Nuevo precio canasta EXTRA (S/)"
                  type="number" step="0.01" min="0"
                  placeholder="0.00"
                  value={form.precioExtra}
                  onChange={e => setForm(p => ({ ...p, precioExtra: e.target.value }))}
                  required
                />
                <Input
                  label="Nuevo precio canasta NORMAL (S/)"
                  type="number" step="0.01" min="0"
                  placeholder="0.00"
                  value={form.precioNormal}
                  onChange={e => setForm(p => ({ ...p, precioNormal: e.target.value }))}
                  required
                />
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
