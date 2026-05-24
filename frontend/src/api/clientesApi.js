import api from './axios'

export const listarClientes = () => api.get('/clientes').then(r => r.data)

export const crearCliente = (data) => api.post('/clientes', data).then(r => r.data)

export const actualizarPrecioEspecial = (id, data) =>
  api.put(`/clientes/${id}/precio-especial`, data).then(r => r.data)
