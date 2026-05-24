import api from './axios'

export const obtenerCredito = (clienteId) =>
  api.get(`/creditos/${clienteId}`).then(r => r.data)
