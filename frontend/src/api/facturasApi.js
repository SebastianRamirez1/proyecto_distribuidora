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
  let res
  try {
    res = await api.get(`/facturas/${id}/pdf`, { responseType: 'blob' })
  } catch (err) {
    // Cuando el servidor devuelve error con responseType:'blob',
    // el body llega como Blob — lo leemos para mostrar el mensaje real
    if (err.response?.data instanceof Blob) {
      const text = await err.response.data.text()
      try {
        const json = JSON.parse(text)
        throw new Error(json.mensaje || json.message || text)
      } catch (parseErr) {
        if (parseErr instanceof SyntaxError) throw new Error(text)
        throw parseErr
      }
    }
    throw err
  }
  const url = URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }))
  const a   = document.createElement('a')
  a.href     = url
  a.download = `factura-${numero}.pdf`
  a.click()
  URL.revokeObjectURL(url)
}
