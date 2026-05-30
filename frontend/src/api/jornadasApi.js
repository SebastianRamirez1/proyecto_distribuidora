import api from './axios'

/** Retorna { abierta, enCierre } — enCierre puede ser null */
export const obtenerEstadoJornadas = () =>
  api.get('/jornadas/estado').then(r => r.data)

export const liquidarJornada = () =>
  api.post('/jornadas/liquidar').then(r => r.data)

export const cerrarJornada = () =>
  api.post('/jornadas/cerrar').then(r => r.data)
