import axios from 'axios'

const TOKEN_KEY = 'dist_token'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
})

// Agrega el token JWT a cada request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Manejo de errores globales
api.interceptors.response.use(
  (res) => res,
  (err) => {
    // Si el token expiró o es inválido → limpiar y redirigir al login
    if (err.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('dist_user')
      window.location.href = '/login'
      return Promise.reject(new Error('Sesión expirada. Por favor inicia sesión nuevamente.'))
    }
    const msg =
      err.response?.data?.mensaje ||
      err.response?.data?.message ||
      err.message ||
      'Error de conexión con el servidor'
    return Promise.reject(new Error(msg))
  },
)

export default api
