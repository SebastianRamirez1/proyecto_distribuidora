import api from './axios'

export const reporteCajaHoy = () => api.get('/reportes/caja/hoy').then(r => r.data)

export const reporteCajaPorFecha = (fecha) =>
  api.get('/reportes/caja', { params: { fecha } }).then(r => r.data)
