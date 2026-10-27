import { useState } from "react";
import { Link } from "react-router-dom";
import { ArrowUpRight, Menu, X } from "lucide-react";
import { Button } from "./ui/Button.jsx";
import { Brand } from "./Brand.jsx";
import { HIRE_MESSAGE, whatsappUrl } from "../lib/contact.js";

const navLinks = [
  { to: "/", hash: "servicos", label: "Serviços" },
  { to: "/", hash: "oficina", label: "A oficina" },
  { to: "/planos", label: "Planos" },
  { to: "/", hash: "contato", label: "Contato" },
];

export function Header({ current }) {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="site-header">
      <div className="page-container header-inner">
        <Brand />
        <nav className="desktop-nav" aria-label="Navegação principal">
          {navLinks.map((link) => (
            <Link
              key={link.label}
              to={link.to}
              hash={link.hash}
              aria-current={link.to === current && !link.hash ? "page" : undefined}
            >
              {link.label}
            </Link>
          ))}
        </nav>
        <div className="flex items-center gap-2">
          <Button asChild variant="technical">
            <a href={whatsappUrl(HIRE_MESSAGE)} target="_blank" rel="noopener noreferrer">
              Quero contratar <ArrowUpRight />
            </a>
          </Button>
          <Button asChild variant="technicalOutline" className="header-quote">
            <Link to="/" hash="orcamento">
              Orçamento
            </Link>
          </Button>
          <Button
            variant="ghost"
            size="icon"
            className="mobile-menu-button"
            aria-label={menuOpen ? "Fechar menu" : "Abrir menu"}
            aria-expanded={menuOpen}
            aria-controls="mobile-nav"
            onClick={() => setMenuOpen(!menuOpen)}
          >
            {menuOpen ? <X /> : <Menu />}
          </Button>
        </div>
      </div>
      {menuOpen && (
        <nav id="mobile-nav" className="mobile-nav" aria-label="Navegação móvel">
          {navLinks.map((link) => (
            <Link key={link.label} to={link.to} hash={link.hash} onClick={() => setMenuOpen(false)}>
              {link.label}
            </Link>
          ))}
        </nav>
      )}
    </header>
  );
}
