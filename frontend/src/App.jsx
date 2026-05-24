import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Clientes from './pages/Clientes'
import Ventas from './pages/Ventas'
import Inventario from './pages/Inventario'
import Precios from './pages/Precios'
import Reportes from './pages/Reportes'
import Deudores from './pages/Deudores'

function RutaProtegida({ children }) {
  const { estaAutenticado } = useAuth()
  return estaAutenticado ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={
            <RutaProtegida><Layout /></RutaProtegida>
          }>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard"  element={<Dashboard />} />
            <Route path="clientes"   element={<Clientes />} />
            <Route path="ventas"     element={<Ventas />} />
            <Route path="inventario" element={<Inventario />} />
            <Route path="precios"    element={<Precios />} />
            <Route path="deudores"   element={<Deudores />} />
            <Route path="reportes"   element={<Reportes />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
