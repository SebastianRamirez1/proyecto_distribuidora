import { useEffect, useState } from 'react'
import { listarClientes, crearCliente, actualizarPrecioEspecial } from '../api/clientesApi'
import { obtenerCredito } from '../api/creditosApi'
import Card from '../components/ui/Card'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'
import Select from '../components/ui/Select'
import Modal from '../components/ui/Modal'
import Alert from '../components/ui/Alert'
import Badge from '../components/ui/Badge'
import Spinner from '../components/ui/Spinner'

const tipoColor = { NORMAL: 'slate', ESPECIAL: 'amber' }

const initCrear = { nombre: '', tipo: 'NORMAL' }
const initPrecio = { precioEspecialExtra: '', precioEspecialNormal: '' }

export default function Clientes() {
  const [clientes, setClientes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const [modalCrear, setModalCrear] = useState(false)
  const [modalPrecio, setModalPrecio] = useState(false)
  const [modalCredito, setModalCredito] = useState(false)
  const [selectedCliente, setSelectedCliente] = useState(null)
  const [credito, setCredito] = useState(null)

  const [formCrear, setFormCrear] = useState(initCrear)
  const [formPrecio, setFormPrecio] = useState(initPrecio)
  const [saving, setSaving] = useState(false)

  const load = async () => {
    try {
      setLoading(true)
      setClientes(await listarClientes())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleCrear = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await crearCliente({
        nombre: formCrear.nombre,
        tipo: formCrear.tipo,
      })
      setModalCrear(false)
      setFormCrear(initCrear)
      await load()
      setSuccess('Cliente creado correctamente')
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  const handlePrecio = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await actualizarPrecioEspecial(selectedCliente.id, {
        precioEspecialExtra: Number(formPrecio.precioEspecialExtra),
        precioEspecialNormal: Number(formPrecio.precioEspecialNormal),
      })
      setModalPrecio(false)
      await load()
      setSuccess('Precio especial actualizado')
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  const openPrecio = (c) => {
    setSelectedCliente(c)
    setFormPrecio({
      precioEspecialExtra: c.precioEspecialExtra ?? '',
      precioEspecialNormal: c.precioEspecialNormal ?? '',
    })
    setModalPrecio(true)
  }

  const openCredito = async (c) => {
    setSelectedCliente(c)
    setCredito(null)
    setModalCredito(true)
    try {
      setCredito(await obtenerCredito(c.id))
    } catch (e) {
      setCredito({ error: e.message })
    }
  }

  const fmt = (n) => n != null ? `S/ ${Number(n).toFixed(2)}` : '-'

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Clientes</h1>
          <p className="text-slate-500 text-sm mt-1">Gestión de clientes de la distribuidora</p>
        </div>
        <Button onClick={() => setModalCrear(true)}>+ Nuevo cliente</Button>
      </div>

      <Alert type="error"   message={error}   onClose={() => setError('')} />
      <Alert type="success" message={success} onClose={() => setSuccess('')} />

      {loading ? <Spinner /> : (
        <Card className="p-0 overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="table-head">
                <th className="px-4 py-3 text-left">ID</th>
                <th className="px-4 py-3 text-left">Nombre</th>
                <th className="px-4 py-3 text-left">Tipo</th>
                <th className="px-4 py-3 text-right">P. Especial Extra</th>
                <th className="px-4 py-3 text-right">P. Especial Normal</th>
                <th className="px-4 py-3 text-center">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {clientes.length === 0 ? (
                <tr><td colSpan={6} className="text-center text-slate-400 py-8">No hay clientes registrados</td></tr>
              ) : clientes.map((c) => (
                <tr key={c.id} className="hover:bg-slate-50">
                  <td className="table-cell text-slate-400">#{c.id}</td>
                  <td className="table-cell font-medium">{c.nombre}</td>
                  <td className="table-cell">
                    <Badge color={tipoColor[c.tipo]}>{c.tipo}</Badge>
                  </td>
                  <td className="table-cell text-right">{fmt(c.precioEspecialExtra)}</td>
                  <td className="table-cell text-right">{fmt(c.precioEspecialNormal)}</td>
                  <td className="table-cell text-center">
                    <div className="flex gap-2 justify-center">
                      {c.tipo === 'ESPECIAL' && (
                        <Button variant="secondary" className="text-xs py-1 px-2" onClick={() => openPrecio(c)}>
                          💲 Precio
                        </Button>
                      )}
                      <Button variant="secondary" className="text-xs py-1 px-2" onClick={() => openCredito(c)}>
                        📋 Crédito
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}

      {/* Modal Crear */}
      <Modal isOpen={modalCrear} onClose={() => setModalCrear(false)} title="Nuevo cliente">
        <form onSubmit={handleCrear}>
          <Input
            label="Nombre del cliente"
            placeholder="Ej: Tienda La Esperanza"
            value={formCrear.nombre}
            onChange={e => setFormCrear(p => ({ ...p, nombre: e.target.value }))}
            required
          />
          <Select
            label="Tipo de cliente"
            value={formCrear.tipo}
            onChange={e => setFormCrear(p => ({ ...p, tipo: e.target.value }))}
          >
            <option value="NORMAL">NORMAL (precio público)</option>
            <option value="ESPECIAL">ESPECIAL (precio personalizado)</option>
          </Select>
          <div className="flex gap-3 justify-end mt-4">
            <Button type="button" variant="secondary" onClick={() => setModalCrear(false)}>Cancelar</Button>
            <Button type="submit" loading={saving}>Crear cliente</Button>
          </div>
        </form>
      </Modal>

      {/* Modal Precio Especial */}
      <Modal isOpen={modalPrecio} onClose={() => setModalPrecio(false)} title={`Precio especial — ${selectedCliente?.nombre}`}>
        <form onSubmit={handlePrecio}>
          <Input
            label="Precio por canasta EXTRA (S/)"
            type="number" step="0.01" min="0"
            placeholder="0.00"
            value={formPrecio.precioEspecialExtra}
            onChange={e => setFormPrecio(p => ({ ...p, precioEspecialExtra: e.target.value }))}
            required
          />
          <Input
            label="Precio por canasta NORMAL (S/)"
            type="number" step="0.01" min="0"
            placeholder="0.00"
            value={formPrecio.precioEspecialNormal}
            onChange={e => setFormPrecio(p => ({ ...p, precioEspecialNormal: e.target.value }))}
            required
          />
          <div className="flex gap-3 justify-end mt-4">
            <Button type="button" variant="secondary" onClick={() => setModalPrecio(false)}>Cancelar</Button>
            <Button type="submit" loading={saving}>Guardar precios</Button>
          </div>
        </form>
      </Modal>

      {/* Modal Crédito */}
      <Modal isOpen={modalCredito} onClose={() => setModalCredito(false)} title={`Crédito — ${selectedCliente?.nombre}`}>
        {!credito ? <Spinner size="sm" /> : credito.error ? (
          <Alert type="info" message={credito.error} />
        ) : (
          <div className="space-y-3">
            <div className="flex justify-between py-2 border-b">
              <span className="text-slate-500">Monto total fiado</span>
              <span className="font-semibold">S/ {Number(credito.montoTotal).toFixed(2)}</span>
            </div>
            <div className="flex justify-between py-2 border-b">
              <span className="text-slate-500">Monto pagado</span>
              <span className="font-semibold text-emerald-600">S/ {Number(credito.montoPagado).toFixed(2)}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-700 font-medium">Saldo pendiente</span>
              <span className={`text-lg font-bold ${Number(credito.saldoPendiente) > 0 ? 'text-red-600' : 'text-emerald-600'}`}>
                S/ {Number(credito.saldoPendiente).toFixed(2)}
              </span>
            </div>
          </div>
        )}
      </Modal>
    </div>
  )
}
