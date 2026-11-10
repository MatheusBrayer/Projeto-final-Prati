import { Routes, Route, Navigate } from "react-router-dom";

import Login from "../pages/Login/Login.jsx";
import PrimeiroAcesso from "../pages/PrimeiroAcesso/PrimeiroAcesso.jsx";

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />

      <Route path="/login" element={<Login />} />

      <Route path="/primeiro-acesso" element={<PrimeiroAcesso />} />
    </Routes>
  );
}

export default AppRoutes;
