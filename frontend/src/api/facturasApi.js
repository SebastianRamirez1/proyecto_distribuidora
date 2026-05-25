import api from './axios'

export const obtenerConfiguracion  = () =>
  api.get('/facturas/configuracion').then(r => r.data)

export const actualizarConfiguracion = (data) =>
  api.put('/facturas/configuracion', data).then(r => r.data)

export const listarFacturas = () =>
  api.get('/facturas').then(r => r.data)

export const generarFactura = (data) =>
  api.post('/facturas/generar', data).then(r => r.data)

export const descargarPdfFactura = async (id, numero) => {
  const res = await api.get(`/facturas/${id}/pdf`, { responseType: 'blob' })
  const url = URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }))
  const a   = document.createElement('a')
  a.href     = url
  a.download = `factura-${numero}.pdf`
  a.click()
  URL.revokeObjectURL(url)
}
