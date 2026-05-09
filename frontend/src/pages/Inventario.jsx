import { useEffect, useState } from 'react'
import { obtenerInventario, cargarInventario } from '../api/inventarioApi'
import Card from '../components/ui/Card'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Alert from '../components/ui/Alert'
import Spinner from '../components/ui/Spinner'

export default function Inventario() {
  const [inventario, setInventario] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [saving, setSaving] = useState(false)

  const [form, setForm] = useState({ cantidadExtra: '', cantidadNormal: '' })

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
    if (!form.cantidadExtra && !form.cantidadNormal) {
      setError('Ingresa al menos una cantidad')
      return
    }
    setSaving(true)
    setError('')
    try {
      const updated = await cargarInventario({
        cantidadExtra: form.cantidadExtra ? Number(form.cantidadExtra) : 0,
        cantidadNormal: form.cantidadNormal ? Number(form.cantidadNormal) : 0,
      })
      setInventario(updated)
      setSuccess('Inventario cargado correctamente ✅')
      setForm({ cantidadExtra: '', cantidadNormal: '' })
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

            <Card className="flex items-center gap-4">
              <div className="w-14 h-14 rounded-full bg-amber-100 flex items-center justify-center text-3xl flex-shrink-0">
                🥚
              </div>
              <div className="flex-1">
                <p className="text-slate-500 text-sm">Canastas EXTRA</p>
                <p className="text-4xl font-bold text-amber-600">{inventario?.stockExtra ?? 0}</p>
                <p className="text-xs text-slate-400 mt-1">unidades disponibles</p>
              </div>
              <div className={`w-3 h-3 rounded-full ${(inventario?.stockExtra ?? 0) > 10 ? 'bg-emerald-400' : (inventario?.stockExtra ?? 0) > 0 ? 'bg-amber-400' : 'bg-red-400'}`} />
            </Card>

            <Card className="flex items-center gap-4">
              <div className="w-14 h-14 rounded-full bg-blue-100 flex items-center justify-center text-3xl flex-shrink-0">
                🥚
              </div>
              <div className="flex-1">
                <p className="text-slate-500 text-sm">Canastas NORMAL</p>
                <p className="text-4xl font-bold text-blue-600">{inventario?.stockNormal ?? 0}</p>
                <p className="text-xs text-slate-400 mt-1">unidades disponibles</p>
              </div>
              <div className={`w-3 h-3 rounded-full ${(inventario?.stockNormal ?? 0) > 10 ? 'bg-emerald-400' : (inventario?.stockNormal ?? 0) > 0 ? 'bg-amber-400' : 'bg-red-400'}`} />
            </Card>

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
                <Input
                  label="Canastas EXTRA a agregar"
                  type="number" min="0"
                  placeholder="Ej: 100"
                  value={form.cantidadExtra}
                  onChange={e => setForm(p => ({ ...p, cantidadExtra: e.target.value }))}
                />
                <Input
                  label="Canastas NORMAL a agregar"
                  type="number" min="0"
                  placeholder="Ej: 150"
                  value={form.cantidadNormal}
                  onChange={e => setForm(p => ({ ...p, cantidadNormal: e.target.value }))}
                />
                <div className="bg-amber-50 border border-amber-200 rounded-lg p-3 text-xs text-amber-700 mb-4">
                  ℹ️ Los valores ingresados se <strong>suman</strong> al stock actual. Para reponer 100 canastas, ingresa 100.
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
