export default function Select({ label, error, children, className = '', ...props }) {
  return (
    <div className="mb-3">
      {label && <label className="label">{label}</label>}
      <select
        className={`input ${error ? 'border-red-400 focus:ring-red-400' : ''} ${className}`}
        {...props}
      >
        {children}
      </select>
      {error && <p className="text-xs text-red-500 mt-1">{error}</p>}
    </div>
  )
}
