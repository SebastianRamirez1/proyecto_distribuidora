import api from './axios'

export const ventasHoy = () => api.get('/ventas/hoy').then(r => r.data)

export const ventasPorFecha = (fecha) =>
  api.get('/ventas', { params: { fecha } }).then(r => r.data)

export const registrarVenta = (data) => api.post('/ventas', data).then(r => r.data)

export const registrarAbono = (data) => api.post('/ventas/abono', data).then(r => r.data)
