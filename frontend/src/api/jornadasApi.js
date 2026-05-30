import api from './axiosConfig'

export const obtenerJornadaActiva = () =>
  api.get('/jornadas/activa').then(r => r.data)

export const liquidarJornada = () =>
  api.post('/jornadas/liquidar').then(r => r.data)
