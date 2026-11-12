import { BrowserRouter } from "react-router-dom";
import AppRoutes from "./routes/AppRoutes.jsx";

import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Dashboard from './pages/Dashboard/Dashboard';
import Clients from './pages/Clients/Clients';
import Vehicles from './pages/Vehicles/Vehicles';
import ServiceOrders from './pages/ServiceOrders/ServiceOrders';

function App() {
  return (
    <Router>
      <div style={{ display: 'flex', minHeight: '100vh', background: '#f4f6f9', fontFamily: 'Inter, system-ui, sans-serif' }}>
        
        {/* Barra Lateral Moderna */}
        <nav style={{ 
          width: '260px', 
          background: '#1e293b', 
          color: '#f8fafc', 
          padding: '24px 16px', 
          display: 'flex', 
          flexDirection: 'column', 
          gap: '24px',
          boxShadow: '4px 0 10px rgba(0,0,0,0.05)'
        }}>
          <div>
            <h2 style={{ fontSize: '20px', fontWeight: '700', color: '#ffffff', margin: '0 0 4px 0', letterSpacing: '0.5px' }}>🚗 Oficina OS</h2>
            <span style={{ fontSize: '12px', color: '#94a3b8' }}>Gestão Automotiva</span>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
            <span style={{ fontSize: '11px', fontWeight: '600', color: '#64748b', textTransform: 'uppercase', letterSpacing: '1px', paddingLeft: '12px', marginBottom: '4px' }}>Menu Principal</span>
            
            <Link to="/" style={{ color: '#cbd5e1', textDecoration: 'none', padding: '10px 12px', borderRadius: '6px', fontSize: '14px', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '10px', transition: 'all 0.2s' }} onMouseOver={(e) => e.currentTarget.style.background = '#334155'} onMouseOut={(e) => e.currentTarget.style.background = 'transparent'}>
              📊 Dashboard
            </Link>
            
            <Link to="/clients" style={{ color: '#cbd5e1', textDecoration: 'none', padding: '10px 12px', borderRadius: '6px', fontSize: '14px', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '10px', transition: 'all 0.2s' }} onMouseOver={(e) => e.currentTarget.style.background = '#334155'} onMouseOut={(e) => e.currentTarget.style.background = 'transparent'}>
              👤 Clientes
            </Link>
            
            <Link to="/vehicles" style={{ color: '#cbd5e1', textDecoration: 'none', padding: '10px 12px', borderRadius: '6px', fontSize: '14px', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '10px', transition: 'all 0.2s' }} onMouseOver={(e) => e.currentTarget.style.background = '#334155'} onMouseOut={(e) => e.currentTarget.style.background = 'transparent'}>
              🚙 Veículos
            </Link>
            
            <Link to="/service-orders" style={{ color: '#cbd5e1', textDecoration: 'none', padding: '10px 12px', borderRadius: '6px', fontSize: '14px', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '10px', transition: 'all 0.2s' }} onMouseOver={(e) => e.currentTarget.style.background = '#334155'} onMouseOut={(e) => e.currentTarget.style.background = 'transparent'}>
              🛠️ Ordens de Serviço
            </Link>
          </div>
        </nav>

        {/* Área Principal */}
        <main style={{ flex: 1, overflowY: 'auto' }}>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/clients" element={<Clients />} />
            <Route path="/vehicles" element={<Vehicles />} />
            <Route path="/service-orders" element={<ServiceOrders />} />
          </Routes>
        </main>

      </div>
    </Router>
  );

}

export default App;
