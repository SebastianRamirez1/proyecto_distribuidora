import { useEffect, useState } from 'react'
import {
  listarClientes, crearCliente, actualizarCliente, actualizarPrecioEspecial,
  eliminarCliente, cargarSaldoAnterior, obtenerEstadoCuenta,
} from '../api/clientesApi'
import { fmt, parsePrecio } from '../utils/fmt'
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

const tipoMovLabel = {
  MIGRACION:   { label: 'Saldo anterior', color: 'text-orange-600',  bg: 'bg-orange-50'  },
  VENTA_FIADO: { label: 'Venta fiado',    color: 'text-red-600',     bg: 'bg-red-50'     },
  ABONO:       { label: 'Abono',          color: 'text-emerald-600', bg: 'bg-emerald-50' },
}

const initCrear = {
  nombre: '', tipo: 'NORMAL',
  precioEspecialExtra: '', precioEspecialAA: '', precioEspecialA: '', precioEspecialB: '',
  notas: '',
}
const initPrecio = { precioEspecialExtra: '', precioEspecialAA: '', precioEspecialA: '', precioEspecialB: '' }
const initSaldo  = { monto: '', descripcion: '' }

export default function Clientes() {
  const [clientes, setClientes] = useState([])
  const [loading, setLoading]   = useState(true)
  const [error, setError]       = useState('')
  const [success, setSuccess]   = useState('')

  const [modalCrear,    setModalCrear]    = useState(false)
  const [modalEditar,   setModalEditar]   = useState(false)
  const [modalPrecio,   setModalPrecio]   = useState(false)
  const [modalCredito,  setModalCredito]  = useState(false)
  const [modalEliminar, setModalEliminar] = useState(false)
  const [modalSaldo,    setModalSaldo]    = useState(false)
  const [modalCuenta,   setModalCuenta]   = useState(false)

  const [selectedCliente, setSelectedCliente] = useState(null)
  const [credito,         setCredito]         = useState(null)
  const [estadoCuenta,    setEstadoCuenta]     = useState(null)

  const [formCrear,  setFormCrear]  = useState(initCrear)
  const [formEditar, setFormEditar] = useState(initCrear)
  const [formPrecio, setFormPrecio] = useState(initPrecio)
  const [formSaldo,  setFormSaldo]  = useState(initSaldo)
  const [saving,     setSaving]     = useState(false)

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

  // ── Crear ────────────────────────────────────────────────────────────────

  const handleCrear = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = { nombre: formCrear.nombre, tipo: formCrear.tipo, notas: formCrear.notas || null }
      if (formCrear.tipo === 'ESPECIAL') {
        payload.precioEspecialExtra = parsePrecio(formCrear.precioEspecialExtra)
        payload.precioEspecialAA    = parsePrecio(formCrear.precioEspecialAA)
        payload.precioEspecialA     = parsePrecio(formCrear.precioEspecialA)
        payload.precioEspecialB     = parsePrecio(formCrear.precioEspecialB)
      }
      await crearCliente(payload)
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

  // ── Editar ───────────────────────────────────────────────────────────────

  const openEditar = (c) => {
    setSelectedCliente(c)
    setFormEditar({
      nombre: c.nombre,
      tipo:   c.tipo,
      precioEspecialExtra: c.precioEspecialExtra ?? '',
      precioEspecialAA:    c.precioEspecialAA    ?? '',
      precioEspecialA:     c.precioEspecialA     ?? '',
      precioEspecialB:     c.precioEspecialB     ?? '',
      notas: c.notas ?? '',
    })
    setModalEditar(true)
  }

  const handleEditar = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = { nombre: formEditar.nombre, tipo: formEditar.tipo, notas: formEditar.notas || null }
      if (formEditar.tipo === 'ESPECIAL') {
        payload.precioEspecialExtra = parsePrecio(formEditar.precioEspecialExtra)
        payload.precioEspecialAA    = parsePrecio(formEditar.precioEspecialAA)
        payload.precioEspecialA     = parsePrecio(formEditar.precioEspecialA)
        payload.precioEspecialB     = parsePrecio(formEditar.precioEspecialB)
      }
      await actualizarCliente(selectedCliente.id, payload)
      setModalEditar(false)
      await load()
      setSuccess('Cliente actualizado correctamente')
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  // ── Precio especial ──────────────────────────────────────────────────────

  const openPrecio = (c) => {
    setSelectedCliente(c)
    setFormPrecio({
      precioEspecialExtra: c.precioEspecialExtra ?? '',
      precioEspecialAA:    c.precioEspecialAA    ?? '',
      precioEspecialA:     c.precioEspecialA     ?? '',
      precioEspecialB:     c.precioEspecialB     ?? '',
    })
    setModalPrecio(true)
  }

  const handlePrecio = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await actualizarPrecioEspecial(selectedCliente.id, {
        precioEspecialExtra: parsePrecio(formPrecio.precioEspecialExtra),
        precioEspecialAA:    parsePrecio(formPrecio.precioEspecialAA),
        precioEspecialA:     parsePrecio(formPrecio.precioEspecialA),
        precioEspecialB:     parsePrecio(formPrecio.precioEspecialB),
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

  // ── Crédito ──────────────────────────────────────────────────────────────

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

  // ── Cargar saldo anterior ────────────────────────────────────────────────

  const openSaldo = (c) => {
    setSelectedCliente(c)
    setFormSaldo(initSaldo)
    setModalSaldo(true)
  }

  const handleSaldo = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await cargarSaldoAnterior(selectedCliente.id, {
        monto:       parsePrecio(formSaldo.monto),
        descripcion: formSaldo.descripcion || null,
      })
      setModalSaldo(false)
      setSuccess(`Saldo anterior cargado para ${selectedCliente.nombre}`)
    } catch (e) {
      setError(e.response?.data?.mensaje || e.message)
    } finally {
      setSaving(false)
    }
  }

  // ── Estado de cuenta ─────────────────────────────────────────────────────

  const openCuenta = async (c) => {
    setSelectedCliente(c)
    setEstadoCuenta(null)
    setModalCuenta(true)
    try {
      setEstadoCuenta(await obtenerEstadoCuenta(c.id))
    } catch (e) {
      setEstadoCuenta({ error: e.message })
    }
  }

  // ── Eliminar ─────────────────────────────────────────────────────────────

  const handleEliminar = async () => {
    setSaving(true)
    try {
      await eliminarCliente(selectedCliente.id)
      setModalEliminar(false)
      await load()
      setSuccess(`Cliente "${selectedCliente.nombre}" eliminado correctamente`)
    } catch (e) {
      setModalEliminar(false)
      setError(e.response?.data?.mensaje || e.message)
    } finally {
      setSaving(false)
    }
  }

  // ── Render ───────────────────────────────────────────────────────────────

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
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="table-head">
                  <th className="px-2 py-2.5 text-left">ID</th>
                  <th className="px-2 py-2.5 text-left">Nombre</th>
                  <th className="px-2 py-2.5 text-left">Tipo</th>
                  <th className="px-2 py-2.5 text-right">P. Extra</th>
                  <th className="px-2 py-2.5 text-right">P. AA</th>
                  <th className="px-2 py-2.5 text-right">P. A</th>
                  <th className="px-2 py-2.5 text-right">P. B</th>
                  <th className="px-2 py-2.5 text-center">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {clientes.length === 0 ? (
                  <tr><td colSpan={8} className="text-center text-slate-400 py-8">No hay clientes registrados</td></tr>
                ) : clientes.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-50">
                    <td className="table-cell text-slate-400">#{c.id}</td>
                    <td className="table-cell">
                      <div className="font-medium">{c.nombre}</div>
                      {c.notas && (
                        <div className="text-xs text-slate-400 mt-0.5 truncate max-w-[180px]" title={c.notas}>
                          📝 {c.notas}
                        </div>
                      )}
                    </td>
                    <td className="table-cell">
                      <Badge color={tipoColor[c.tipo]}>{c.tipo}</Badge>
                    </td>
                    <td className="table-cell text-right">{fmt(c.precioEspecialExtra, '—')}</td>
                    <td className="table-cell text-right">{fmt(c.precioEspecialAA, '—')}</td>
                    <td className="table-cell text-right">{fmt(c.precioEspecialA, '—')}</td>
                    <td className="table-cell text-right">{fmt(c.precioEspecialB, '—')}</td>
                    <td className="table-cell text-center">
                      <div className="flex gap-1.5 justify-center flex-wrap">
                        <Button variant="secondary" className="text-xs py-1 px-2" onClick={() => openEditar(c)}>
                          ✏️ Editar
                        </Button>
                        <Button variant="secondary" className="text-xs py-1 px-2 text-orange-600 hover:bg-orange-50" onClick={() => openSaldo(c)}>
                          📒 Saldo ant.
                        </Button>
                        <Button variant="secondary" className="text-xs py-1 px-2" onClick={() => openCuenta(c)}>
                          📊 Cuenta
                        </Button>
                        <Button variant="secondary" className="text-xs py-1 px-2" onClick={() => openCredito(c)}>
                          💳 Crédito
                        </Button>
                        <Button
                          variant="secondary"
                          className="text-xs py-1 px-2 text-red-600 hover:bg-red-50"
                          onClick={() => { setSelectedCliente(c); setModalEliminar(true) }}
                        >
                          🗑️
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      )}

      {/* ── Modal Editar ── */}
      <Modal isOpen={modalEditar} onClose={() => setModalEditar(false)} title={`Editar — ${selectedCliente?.nombre}`}>
        <form onSubmit={handleEditar}>
          <Input
            label="Nombre del cliente"
            placeholder="Ej: Tienda La Esperanza"
            value={formEditar.nombre}
            onChange={e => setFormEditar(p => ({ ...p, nombre: e.target.value }))}
            required
          />
          <Select
            label="Tipo de cliente"
            value={formEditar.tipo}
            onChange={e => setFormEditar(p => ({ ...p, tipo: e.target.value }))}
          >
            <option value="NORMAL">NORMAL (precio público)</option>
            <option value="ESPECIAL">ESPECIAL (precio personalizado)</option>
          </Select>

          {formEditar.tipo === 'ESPECIAL' && (
            <div className="mt-3 border border-amber-200 bg-amber-50 rounded-lg p-3">
              <p className="text-xs font-semibold text-amber-700 mb-3">💲 Precios personalizados por canasta</p>
              {[
                { field: 'precioEspecialExtra', label: 'EXTRA' },
                { field: 'precioEspecialAA',    label: 'AA'    },
                { field: 'precioEspecialA',     label: 'A'     },
                { field: 'precioEspecialB',     label: 'B'     },
              ].map(({ field, label }) => (
                <Input
                  key={field}
                  label={`Precio canasta ${label} ($)`}
                  type="number" step="0.01" min="0"
                  placeholder="0.00"
                  value={formEditar[field]}
                  onChange={e => setFormEditar(p => ({ ...p, [field]: e.target.value }))}
                  required
                />
              ))}
            </div>
          )}

          <div className="mt-3">
            <label className="block text-sm font-medium text-slate-700 mb-1">
              Notas internas <span className="text-slate-400 font-normal">(opcional)</span>
            </label>
            <textarea
              className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
              rows={3}
              placeholder="Ej: Paga cada 15 días. Precio EXTRA acordado $15.500..."
              value={formEditar.notas}
              onChange={e => setFormEditar(p => ({ ...p, notas: e.target.value }))}
            />
          </div>

          <div className="flex gap-3 justify-end mt-4">
            <Button type="button" variant="secondary" onClick={() => setModalEditar(false)}>Cancelar</Button>
            <Button type="submit" loading={saving}>Guardar cambios</Button>
          </div>
        </form>
      </Modal>

      {/* ── Modal Crear ── */}
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

          {formCrear.tipo === 'ESPECIAL' && (
            <div className="mt-3 border border-amber-200 bg-amber-50 rounded-lg p-3">
              <p className="text-xs font-semibold text-amber-700 mb-3">💲 Precios personalizados por canasta</p>
              {[
                { field: 'precioEspecialExtra', label: 'EXTRA' },
                { field: 'precioEspecialAA',    label: 'AA'    },
                { field: 'precioEspecialA',     label: 'A'     },
                { field: 'precioEspecialB',     label: 'B'     },
              ].map(({ field, label }) => (
                <Input
                  key={field}
                  label={`Precio canasta ${label} ($)`}
                  type="number" step="0.01" min="0"
                  placeholder="0.00"
                  value={formCrear[field]}
                  onChange={e => setFormCrear(p => ({ ...p, [field]: e.target.value }))}
                  required
                />
              ))}
            </div>
          )}

          <div className="mt-3">
            <label className="block text-sm font-medium text-slate-700 mb-1">
              Notas internas <span className="text-slate-400 font-normal">(opcional)</span>
            </label>
            <textarea
              className="w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
              rows={3}
              placeholder="Ej: Paga cada 15 días. Precio especial acordado..."
              value={formCrear.notas}
              onChange={e => setFormCrear(p => ({ ...p, notas: e.target.value }))}
            />
          </div>

          <div className="flex gap-3 justify-end mt-4">
            <Button type="button" variant="secondary" onClick={() => setModalCrear(false)}>Cancelar</Button>
            <Button type="submit" loading={saving}>Crear cliente</Button>
          </div>
        </form>
      </Modal>

      {/* ── Modal Precio Especial ── */}
      <Modal isOpen={modalPrecio} onClose={() => setModalPrecio(false)} title={`Precio especial — ${selectedCliente?.nombre}`}>
        <form onSubmit={handlePrecio}>
          {[
            { field: 'precioEspecialExtra', label: 'EXTRA' },
            { field: 'precioEspecialAA',    label: 'AA'    },
            { field: 'precioEspecialA',     label: 'A'     },
            { field: 'precioEspecialB',     label: 'B'     },
          ].map(({ field, label }) => (
            <Input
              key={field}
              label={`Precio por canasta ${label} ($)`}
              type="number" step="0.01" min="0"
              placeholder="0.00"
              value={formPrecio[field]}
              onChange={e => setFormPrecio(p => ({ ...p, [field]: e.target.value }))}
              required
            />
          ))}
          <div className="flex gap-3 justify-end mt-4">
            <Button type="button" variant="secondary" onClick={() => setModalPrecio(false)}>Cancelar</Button>
            <Button type="submit" loading={saving}>Guardar precios</Button>
          </div>
        </form>
      </Modal>

      {/* ── Modal Cargar saldo anterior ── */}
      <Modal isOpen={modalSaldo} onClose={() => setModalSaldo(false)} title={`Cargar saldo anterior — ${selectedCliente?.nombre}`}>
        <div className="mb-4 p-3 bg-orange-50 border border-orange-200 rounded-lg text-sm text-orange-800">
          <p className="font-semibold mb-1">¿Para qué sirve esto?</p>
          <p>Carga una deuda que el cliente ya tenía en el cuaderno <strong>sin crear una venta nueva</strong>.
          No afecta el inventario ni los reportes del día. Úsalo para migrar saldos históricos al sistema.</p>
        </div>
        <form onSubmit={handleSaldo}>
          <Input
            label="Monto de la deuda ($)"
            type="number"
            min="1"
            placeholder="Ej: 45000"
            value={formSaldo.monto}
            onChange={e => setFormSaldo(p => ({ ...p, monto: e.target.value }))}
            required
          />
          <Input
            label="Descripción (opcional)"
            placeholder="Ej: Deuda cuaderno al 26/05/2025"
            value={formSaldo.descripcion}
            onChange={e => setFormSaldo(p => ({ ...p, descripcion: e.target.value }))}
          />
          <div className="flex gap-3 justify-end mt-4">
            <Button type="button" variant="secondary" onClick={() => setModalSaldo(false)}>Cancelar</Button>
            <Button type="submit" loading={saving}>Cargar saldo</Button>
          </div>
        </form>
      </Modal>

      {/* ── Modal Estado de cuenta ── */}
      <Modal isOpen={modalCuenta} onClose={() => setModalCuenta(false)} title={`Estado de cuenta — ${selectedCliente?.nombre}`}>
        {!estadoCuenta ? (
          <Spinner size="sm" />
        ) : estadoCuenta.error ? (
          <Alert type="info" message={estadoCuenta.error} />
        ) : estadoCuenta.length === 0 ? (
          <p className="text-slate-400 text-sm text-center py-6">Sin movimientos registrados</p>
        ) : (
          <div className="space-y-1 max-h-96 overflow-y-auto">
            {estadoCuenta.map((m, i) => {
              const meta = tipoMovLabel[m.tipo] ?? { label: m.tipo, color: 'text-slate-600', bg: 'bg-slate-50' }
              return (
                <div key={i} className={`flex items-start justify-between p-2.5 rounded-lg ${meta.bg}`}>
                  <div className="flex-1 min-w-0">
                    <span className={`text-xs font-semibold ${meta.color}`}>{meta.label}</span>
                    <p className="text-xs text-slate-600 mt-0.5 truncate">{m.descripcion}</p>
                    <p className="text-xs text-slate-400">
                      {new Date(m.fecha).toLocaleDateString('es-CO', { day: '2-digit', month: 'short', year: 'numeric' })}
                    </p>
                  </div>
                  <span className={`text-sm font-bold ml-3 whitespace-nowrap ${m.esDebito ? 'text-red-600' : 'text-emerald-600'}`}>
                    {m.esDebito ? '+' : '−'} {fmt(m.monto)}
                  </span>
                </div>
              )
            })}
          </div>
        )}
      </Modal>

      {/* ── Modal Eliminar ── */}
      <Modal isOpen={modalEliminar} onClose={() => setModalEliminar(false)} title="Eliminar cliente">
        <p className="text-slate-600 mb-4">
          ¿Estás seguro de que deseas eliminar al cliente <span className="font-semibold">{selectedCliente?.nombre}</span>?
          Se eliminarán también todas sus ventas, facturas, abonos y crédito. Esta acción no se puede deshacer.
        </p>
        <div className="flex gap-3 justify-end">
          <Button type="button" variant="secondary" onClick={() => setModalEliminar(false)}>Cancelar</Button>
          <Button type="button" variant="danger" loading={saving} onClick={handleEliminar}>
            Eliminar
          </Button>
        </div>
      </Modal>

      {/* ── Modal Crédito (resumen rápido) ── */}
      <Modal isOpen={modalCredito} onClose={() => setModalCredito(false)} title={`Crédito — ${selectedCliente?.nombre}`}>
        {!credito ? <Spinner size="sm" /> : credito.error ? (
          <Alert type="info" message={credito.error} />
        ) : (
          <div className="space-y-3">
            <div className="flex justify-between py-2 border-b">
              <span className="text-slate-500">Monto total fiado</span>
              <span className="font-semibold">{fmt(credito.montoTotal)}</span>
            </div>
            <div className="flex justify-between py-2 border-b">
              <span className="text-slate-500">Monto pagado</span>
              <span className="font-semibold text-emerald-600">{fmt(credito.montoPagado)}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-700 font-medium">Saldo pendiente</span>
              <span className={`text-lg font-bold ${Number(credito.saldoPendiente) > 0 ? 'text-red-600' : 'text-emerald-600'}`}>
                {fmt(credito.saldoPendiente)}
              </span>
            </div>
          </div>
        )}
      </Modal>
    </div>
  )
}
