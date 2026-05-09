import api from './axios'

export const obtenerInventario = () => api.get('/inventario').then(r => r.data)

export const cargarInventario = (data) => api.post('/inventario/cargar', data).then(r => r.data)
