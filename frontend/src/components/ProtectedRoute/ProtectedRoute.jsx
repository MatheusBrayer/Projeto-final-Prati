import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../contexts/useAuth.js";

function ProtectedRoute({ perfisPermitidos }) {
  const { autenticado, usuario } = useAuth();

  // Usuário não autenticado
  if (!autenticado) {
    return <Navigate to="/login" replace />;
  }

  // Usuário autenticado, mas sem permissão
  if (!perfisPermitidos.includes(usuario?.perfil)) {
    const rotasPorPerfil = {
      ADM_SISTEMA: "/admin",
      ADM_OFICINA: "/gestao",
      CLIENTE: "/cliente",
    };

    const destino = rotasPorPerfil[usuario?.perfil] ?? "/login";

    return <Navigate to={destino} replace />;
  }

  // Usuário autenticado e autorizado
  return <Outlet />;
}

export default ProtectedRoute;
