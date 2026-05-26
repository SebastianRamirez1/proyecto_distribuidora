// Formatea valores monetarios en pesos colombianos (COP).
// Ejemplo: 17000 → "$ 17.000", 1500000 → "$ 1.500.000"
const _nf = new Intl.NumberFormat('es-CO', {
  minimumFractionDigits: 0,
  maximumFractionDigits: 0,
})

/**
 * Formatea un valor numérico como COP.
 * @param {number|string|null} n  Valor a formatear.
 * @param {string} fallback       Qué mostrar si n es null/undefined (por defecto "$ 0").
 */
export const fmt = (n, fallback = '$ 0') => {
  if (n == null) return fallback
  return `$ ${_nf.format(Number(n))}`
}

/**
 * Parsea un valor ingresado por el usuario como COP.
 * Acepta tanto "16500" como "16.500" (punto como separador de miles colombiano).
 * Ejemplo: "16.500" → 16500, "1.500.000" → 1500000, "16500" → 16500
 * @param {string|number} v  Valor a parsear.
 */
export const parsePrecio = (v) => {
  if (v == null || v === '') return 0
  return Number(String(v).replace(/\./g, '')) || 0
}
