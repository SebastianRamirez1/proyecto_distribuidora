import api from './axios'

export const reporteCajaHoy = () => api.get('/reportes/caja/hoy').then(r => r.data)
