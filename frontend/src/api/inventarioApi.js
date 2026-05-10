import api from './axios'

export const obtenerInventario = () => api.get('/inventario').then(r => r.data)

export const cargarInventario = (data) => api.post('/inventario/cargar', data).then(r => r.data)

/** Carga los 4 tipos en una sola llamada atómica: { extra, aa, a, b } */
export const cargarInventarioBulk = (data) => api.post('/inventario/cargar-bulk', data).then(r => r.data)
