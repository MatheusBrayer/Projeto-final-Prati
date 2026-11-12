import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/useAuth.js";
import "./Gestao.css";

const linksMenu = [
  { caminho: "/gestao", texto: "📊 Dashboard", fim: true },
  { caminho: "/gestao/clientes", texto: "👤 Clientes" },
  { caminho: "/gestao/veiculos", texto: "🚙 Veículos" },
  { caminho: "/gestao/ordens", texto: "🛠️ Ordens de Serviço" },
];

function Gestao() {
  const navigate = useNavigate();
  const { usuario, logout } = useAuth();

  function handleLogout() {
    logout();
    navigate("/login", { replace: true });
  }

  return (
    <div className="gestao-container">
      {/* Menu lateral */}
      <aside className="gestao-sidebar">
        {/* Identificação do sistema */}
        <div className="gestao-brand">
          <h2>🚗 AutoGestão Pro</h2>
          <span>Gestão Automotiva</span>
        </div>

        {/* Navegação */}
        <nav className="gestao-menu" aria-label="Menu da oficina">
          <span className="gestao-menu-titulo">Menu Principal</span>

          {linksMenu.map((link) => (
            <NavLink
              key={link.caminho}
              to={link.caminho}
              end={link.fim}
              className={({ isActive }) =>
                `gestao-menu-link ${isActive ? "gestao-menu-link-ativo" : ""}`
              }
            >
              {link.texto}
            </NavLink>
          ))}
        </nav>

        {/* Usuário autenticado e logout */}
        <div className="gestao-usuario-area">
          <p className="gestao-usuario-email">{usuario?.email}</p>

          <button
            type="button"
            onClick={handleLogout}
            className="gestao-botao-sair"
          >
            Sair
          </button>
        </div>
      </aside>

      {/* Conteúdo das rotas filhas */}
      <main className="gestao-conteudo">
        <Outlet />
      </main>
    </div>
  );
}

export default Gestao;
