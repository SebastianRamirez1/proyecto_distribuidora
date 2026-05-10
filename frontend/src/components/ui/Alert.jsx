export default function Alert({ type = 'error', message, onClose }) {
  if (!message) return null

  const styles = {
    error:   'bg-red-50 border-red-300 text-red-700',
    success: 'bg-emerald-50 border-emerald-300 text-emerald-700',
    info:    'bg-blue-50 border-blue-300 text-blue-700',
    warning: 'bg-amber-50 border-amber-300 text-amber-700',
  }

  const icons = {
    error:   '❌',
    success: '✅',
    info:    'ℹ️',
    warning: '⚠️',
  }

  return (
    <div className={`flex items-start gap-2 border rounded-lg px-4 py-3 text-sm mb-4 ${styles[type]}`}>
      <span>{icons[type]}</span>
      <span className="flex-1">{message}</span>
      {onClose && (
        <button onClick={onClose} className="ml-2 opacity-60 hover:opacity-100 text-lg leading-none">×</button>
      )}
    </div>
  )
}
