import { useState } from "react";
import "./PrimeiroAcesso.css";
import {
  solicitarPrimeiroAcesso,
  confirmarPrimeiroAcesso,
} from "../../services/authService.js";
import { Link, useNavigate, useSearchParams } from "react-router-dom";

function PrimeiroAcesso() {
  const [etapa, setEtapa] = useState(1);
  const [email, setEmail] = useState("");
  const [codigo, setCodigo] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const slugOficina = searchParams.get("oficina");

  const rotaLogin = slugOficina
    ? `/login?oficina=${encodeURIComponent(slugOficina)}`
    : "/login";

  function validarSenhas() {
    if (senha !== confirmarSenha) {
      setErro("As senhas não coincidem.");
      return false;
    }

    if (senha.length < 8) {
      setErro("A senha deve ter pelo menos 8 caracteres.");
      return false;
    }

    return true;
  }

  async function handleAtivacao(event) {
    event.preventDefault();

    setErro("");
    setMensagem("");

    if (!validarSenhas()) {
      return;
    }

    setCarregando(true);

    try {
      const resposta = await confirmarPrimeiroAcesso(email, codigo, senha);

      alert(resposta.mensagem || "Primeiro acesso realizado com sucesso!");

      navigate(rotaLogin, { replace: true });
    } catch (error) {
      setErro(error.message);
    } finally {
      setCarregando(false);
    }
  }

  function avancarEtapa(event) {
    event.preventDefault();
    setEtapa((etapaAtual) => etapaAtual + 1);
  }

  function voltarEtapa() {
    setErro("");
    setMensagem("");
    setEtapa((etapaAtual) => Math.max(1, etapaAtual - 1));
  }

  async function handleSolicitarCodigo(event) {
    event.preventDefault();

    setErro("");
    setMensagem("");
    setCarregando(true);

    try {
      const resposta = await solicitarPrimeiroAcesso(email);

      setMensagem(resposta.mensagem);
      setEtapa(2);
    } catch (error) {
      setErro(error.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <main className="primeiro-acesso-page">
      <section className="primeiro-acesso-card">
        <header className="primeiro-acesso-header">
          <h1>Ative sua conta</h1>
          <p>Siga as etapas para criar seu acesso ao AutoGestão Pro.</p>
        </header>

        {/* Indicador de progresso */}
        <div className="primeiro-acesso-progress">
          {[1, 2, 3]
            .map((numero) => (
              <div
                key={numero}
                className={`progress-step ${
                  etapa === numero
                    ? "active"
                    : etapa > numero
                      ? "completed"
                      : ""
                }`}
              >
                {numero}
              </div>
            ))
            .reduce((elementos, elemento, index) => {
              if (index === 0) return [elemento];

              return [
                ...elementos,
                <div
                  key={`linha-${index}`}
                  className={`progress-line ${
                    etapa > index ? "completed" : ""
                  }`}
                />,
                elemento,
              ];
            }, [])}
        </div>

        {/* Etapa 1 */}
        {etapa === 1 && (
          <form
            className="primeiro-acesso-form"
            onSubmit={handleSolicitarCodigo}
          >
            <div className="primeiro-acesso-field">
              <label htmlFor="email">E-mail cadastrado na oficina</label>
              <input
                type="email"
                id="email"
                name="email"
                placeholder="Digite seu e-mail"
                required
                value={email}
                onChange={(event) => setEmail(event.target.value)}
              />
            </div>

            {erro && (
              <p role="alert" style={{ color: "var(--color-danger)" }}>
                {erro}
              </p>
            )}

            <button
              type="submit"
              className="primeiro-acesso-button"
              disabled={carregando}
            >
              {carregando ? "Enviando..." : "Enviar código"}
            </button>
          </form>
        )}

        {/* Etapa 2 */}
        {etapa === 2 && (
          <form className="primeiro-acesso-form" onSubmit={avancarEtapa}>
            <div className="primeiro-acesso-field">
              <label htmlFor="codigo">Código de verificação</label>
              <input
                type="text"
                id="codigo"
                name="codigo"
                placeholder="Digite o código recebido"
                required
                value={codigo}
                onChange={(event) => setCodigo(event.target.value)}
              />
            </div>

            {mensagem && <p>{mensagem}</p>}

            <button
              type="button"
              className="primeiro-acesso-button-secondary"
              onClick={voltarEtapa}
            >
              Voltar
            </button>

            <button type="submit" className="primeiro-acesso-button">
              Continuar
            </button>
          </form>
        )}

        {/* Etapa 3 */}
        {etapa === 3 && (
          <form className="primeiro-acesso-form" onSubmit={handleAtivacao}>
            <div className="primeiro-acesso-field">
              <label htmlFor="senha">Nova senha</label>
              <input
                type="password"
                id="senha"
                name="senha"
                placeholder="Digite sua senha"
                required
                value={senha}
                onChange={(event) => setSenha(event.target.value)}
              />
            </div>

            <div className="primeiro-acesso-field">
              <label htmlFor="confirmarSenha">Confirmar senha</label>
              <input
                type="password"
                id="confirmarSenha"
                name="confirmarSenha"
                placeholder="Digite novamente sua senha"
                required
                value={confirmarSenha}
                onChange={(event) => setConfirmarSenha(event.target.value)}
              />
            </div>

            {erro && (
              <p role="alert" style={{ color: "var(--color-danger)" }}>
                {erro}
              </p>
            )}

            <button
              type="button"
              className="primeiro-acesso-button-secondary"
              onClick={voltarEtapa}
              disabled={carregando}
            >
              Voltar
            </button>

            <button
              type="submit"
              className="primeiro-acesso-button"
              disabled={carregando}
            >
              {carregando ? "Ativando conta..." : "Ativar minha conta"}
            </button>
          </form>
        )}

        <footer className="primeiro-acesso-footer">
          <footer className="primeiro-acesso-footer">
            <Link to={rotaLogin}>Voltar para o login</Link>
          </footer>
        </footer>
      </section>
    </main>
  );
}

export default PrimeiroAcesso;
