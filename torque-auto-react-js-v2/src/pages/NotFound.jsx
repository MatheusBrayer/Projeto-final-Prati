import { Link } from "react-router-dom";
import { Button } from "../components/ui/Button.jsx";
import { useDocumentTitle } from "../hooks/useDocumentTitle.js";

export default function NotFound() {
  useDocumentTitle("Página não encontrada | Torque Auto");

  return (
    <div className="grid-bg min-h-screen">
      <div className="page-container section-space text-center">
        <p className="eyebrow justify-center">
          <span className="signal-dot" />
          Erro 404
        </p>
        <h1 className="plan-title">Essa página não existe.</h1>
        <p className="body-copy hero-description">
          O endereço que você abriu não faz parte da oficina. Volte ao início e escolha um serviço.
        </p>
        <div className="hero-actions justify-center">
          <Button asChild variant="technical" size="lg">
            <Link to="/">Voltar ao início</Link>
          </Button>
          <Button asChild variant="technicalOutline" size="lg">
            <Link to="/planos">Ver planos</Link>
          </Button>
        </div>
      </div>
    </div>
  );
}
