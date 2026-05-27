import api from './axios'

export const listarClientes = () => api.get('/clientes').then(r => r.data)

export const crearCliente = (data) => api.post('/clientes', data).then(r => r.data)

export const actualizarCliente = (id, data) =>
  api.put(`/clientes/${id}`, data).then(r => r.data)

export const actualizarPrecioEspecial = (id, data) =>
  api.put(`/clientes/${id}/precio-especial`, data).then(r => r.data)

export const eliminarCliente = (id) =>
  api.delete(`/clientes/${id}`).then(r => r.data)

/** Carga un saldo deudor anterior sin crear una venta (migración de cuaderno). */
export const cargarSaldoAnterior = (id, data) =>
  api.post(`/clientes/${id}/saldo-anterior`, data).then(r => r.data)

/** Devuelve el estado de cuenta completo del cliente (cargas + ventas fiado + abonos). */
export const obtenerEstadoCuenta = (id) =>
  api.get(`/clientes/${id}/estado-cuenta`).then(r => r.data)
