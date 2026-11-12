import { useState } from "react";
import "./Login.css";
import { useAuth } from "../../contexts/useAuth.js";
import { Link, useNavigate } from "react-router-dom";

function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  async function handleLogin(event) {
    event.preventDefault();

    setErro("");
    setCarregando(true);

    try {
      const usuario = await login(email, senha);

      const rotasPorPerfil = {
        ADM_SISTEMA: "/admin",
        ADM_OFICINA: "/gestao",
        CLIENTE: "/cliente",
      };

      const destino = rotasPorPerfil[usuario.perfil];

      if (!destino) {
        setErro("Perfil de usuário não reconhecido.");
        return;
      }

      navigate(destino, { replace: true });
    } catch (error) {
      setErro(error.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <main className="login-page">
      {/* Painel de apresentação */}
      <section className="login-brand">
        <h1>AutoGestão Pro</h1>

        <div className="login-brand-content">
          <h2>Gestão inteligente para sua oficina.</h2>

          <p>
            Simplifique processos, acompanhe resultados e tenha o controle da
            sua oficina em um só lugar.
          </p>
        </div>

        <footer className="login-brand-footer">
          © 2026 AutoGestão Pro. Todos os direitos reservados.
        </footer>
      </section>

      {/* Painel de autenticação */}
      <section className="login-form-container">
        <div className="login-form-content">
          <header className="login-form-header">
            <h2>Bem-vindo de volta!</h2>
            <p>Entre com suas credenciais para acessar sua conta.</p>
          </header>

          <form onSubmit={handleLogin}>
            <div className="login-field">
              <label htmlFor="email">E-mail</label>

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

            <div className="login-field">
              <label htmlFor="senha">Senha</label>

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

            {erro && (
              <p role="alert" style={{ color: "var(--color-danger)" }}>
                {erro}
              </p>
            )}

            <button
              type="submit"
              className="login-button"
              disabled={carregando}
            >
              {carregando ? "Entrando..." : "Entrar"}
            </button>
            <div className="login-first-access">
              <span>Primeiro acesso?</span>
              <Link to="/primeiro-acesso">Ative sua conta</Link>
            </div>
          </form>
        </div>
      </section>
    </main>
  );
}

export default Login;
