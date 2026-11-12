import { Routes, Route, Navigate } from "react-router-dom";

import ProtectedRoute from "../components/ProtectedRoute/ProtectedRoute.jsx";

import Admin from "../pages/Admin/Admin.jsx";
import Gestao from "../pages/Gestao/Gestao.jsx";
import Cliente from "../pages/Cliente/Cliente.jsx";
import Login from "../pages/Login/Login.jsx";
import PrimeiroAcesso from "../pages/PrimeiroAcesso/PrimeiroAcesso.jsx";

import Dashboard from "../pages/Dashboard/Dashboard.jsx";
import Clients from "../pages/Clients/Clients.jsx";
import Vehicles from "../pages/Vehicles/Vehicles.jsx";
import ServiceOrders from "../pages/ServiceOrders/ServiceOrders.jsx";

function AppRoutes() {
  return (
    <Routes>
      {/* Rotas públicas */}
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/primeiro-acesso" element={<PrimeiroAcesso />} />

      {/* Administrador do sistema */}
      <Route element={<ProtectedRoute perfisPermitidos={["ADM_SISTEMA"]} />}>
        <Route path="/admin" element={<Admin />} />
      </Route>

      {/* Administrador da oficina */}
      <Route element={<ProtectedRoute perfisPermitidos={["ADM_OFICINA"]} />}>
        <Route path="/gestao" element={<Gestao />}>
          <Route index element={<Dashboard />} />
          <Route path="clientes" element={<Clients />} />
          <Route path="veiculos" element={<Vehicles />} />
          <Route path="ordens" element={<ServiceOrders />} />
        </Route>
      </Route>

      {/* Cliente */}
      <Route element={<ProtectedRoute perfisPermitidos={["CLIENTE"]} />}>
        <Route path="/cliente" element={<Cliente />} />
      </Route>
    </Routes>
  );
}

export default AppRoutes;
