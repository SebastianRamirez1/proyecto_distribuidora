import api from './axios'

export const obtenerCredito          = (clienteId) =>
  api.get(`/creditos/${clienteId}`).then(r => r.data)

export const obtenerDeudores         = () =>
  api.get('/creditos/deudores').then(r => r.data)

export const obtenerHistorialAbonos  = (clienteId) =>
  api.get(`/creditos/${clienteId}/abonos`).then(r => r.data)
