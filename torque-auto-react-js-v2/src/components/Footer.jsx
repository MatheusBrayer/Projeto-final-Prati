import { Link } from "react-router-dom";
import { Brand } from "./Brand.jsx";
import { WHATSAPP_DISPLAY } from "../lib/contact.js";

export function Footer() {
  return (
    <footer className="site-footer">
      <div className="page-container footer-inner">
        <div>
          <Brand />
          <p className="footer-description">WhatsApp: {WHATSAPP_DISPLAY} · atendimento pelo aplicativo.</p>
        </div>
        <nav className="footer-nav" aria-label="Navegação do rodapé">
          <Link to="/">Início</Link>
          <Link to="/planos">Planos</Link>
          <Link to="/" hash="orcamento">
            Orçamento
          </Link>
        </nav>
      </div>
      <div className="footer-bottom">
        <p>© 2026 Torque Auto · Oficina mecânica · Marca ilustrativa</p>
      </div>
    </footer>
  );
}
