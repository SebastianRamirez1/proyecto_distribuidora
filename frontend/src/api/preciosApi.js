import api from './axios'

export const obtenerPrecioPublico = () => api.get('/precios/publico').then(r => r.data)

export const actualizarPrecioPublico = (data) =>
  api.put('/precios/publico', data).then(r => r.data)

export const obtenerPrecioCosto = () => api.get('/precios/costo').then(r => r.data)

export const actualizarPrecioCosto = (data) =>
  api.put('/precios/costo', data).then(r => r.data)
