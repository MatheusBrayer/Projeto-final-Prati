import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./OficinaCadastro.css";

const API_URL = (
  import.meta.env.VITE_API_URL || "http://localhost:8080"
).replace(/\/$/, "");

const dadosIniciais = {
  nome: "",
  cnpj: "",
  telefone: "",
  endereco: "",
  email: "",
  emailAdministrador: "",
};

function obterToken() {
  try {
    const sessao = sessionStorage.getItem("autogestao_sessao");
    return sessao ? JSON.parse(sessao).token || null : null;
  } catch {
    return null;
  }
}

function somenteNumeros(valor) {
  return valor.replace(/\D/g, "");
}

function formatarCnpj(valor) {
  const numeros = somenteNumeros(valor).slice(0, 14);

  return numeros
    .replace(/^(\d{2})(\d)/, "$1.$2")
    .replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3")
    .replace(/\.(\d{3})(\d)/, ".$1/$2")
    .replace(/(\d{4})(\d)/, "$1-$2");
}

function formatarTelefone(valor) {
  const numeros = somenteNumeros(valor).slice(0, 11);

  if (numeros.length <= 2) {
    return numeros ? `(${numeros}` : "";
  }

  const ddd = numeros.slice(0, 2);
  const restante = numeros.slice(2);

  if (restante.length <= 4) {
    return `(${ddd}) ${restante}`;
  }

  const tamanhoPrefixo = restante.length > 8 ? 5 : 4;

  return `(${ddd}) ${restante.slice(0, tamanhoPrefixo)}-${restante.slice(tamanhoPrefixo)}`;
}

function extrairMensagemErro(dados) {
  if (!dados || typeof dados !== "object") return null;

  if (typeof dados.message === "string") return dados.message;
  if (typeof dados.mensagem === "string") return dados.mensagem;
  if (typeof dados.detail === "string") return dados.detail;

  if (Array.isArray(dados.errors) && dados.errors.length) {
    return dados.errors
      .map((erro) =>
        typeof erro === "string" ? erro : erro.defaultMessage || erro.message,
      )
      .filter(Boolean)
      .join(" ");
  }

  return null;
}

function OficinaCadastro() {
  const navigate = useNavigate();

  const [formulario, setFormulario] = useState(dadosIniciais);
  const [mesmoEmail, setMesmoEmail] = useState(false);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState(null);

  const alterarCampo = (evento) => {
    const { name, value } = evento.target;

    let valor = value;

    if (name === "cnpj") {
      valor = formatarCnpj(value);
    }

    if (name === "telefone") {
      valor = formatarTelefone(value);
    }

    setFormulario((anterior) => ({
      ...anterior,
      [name]: valor,
    }));
  };

  const emailAdministrador = mesmoEmail
    ? formulario.email
    : formulario.emailAdministrador;

  const validarFormulario = () => {
    if (somenteNumeros(formulario.cnpj).length !== 14) {
      return "O CNPJ deve conter 14 dígitos.";
    }

    const telefone = somenteNumeros(formulario.telefone);

    if (telefone.length < 10 || telefone.length > 11) {
      return "Informe um telefone com DDD válido.";
    }

    if (!emailAdministrador.trim()) {
      return "Informe o e-mail do administrador.";
    }

    return null;
  };

  const cadastrarOficina = async (evento) => {
    evento.preventDefault();

    if (salvando) return;

    setErro("");

    const erroValidacao = validarFormulario();

    if (erroValidacao) {
      setErro(erroValidacao);
      return;
    }

    const token = obterToken();

    if (!token) {
      setErro("Sessão não encontrada. Faça login novamente.");
      return;
    }

    const payload = {
      nome: formulario.nome.trim(),
      cnpj: somenteNumeros(formulario.cnpj),
      telefone: formulario.telefone.trim(),
      endereco: formulario.endereco.trim(),
      email: formulario.email.trim().toLowerCase(),
      emailAdministrador: emailAdministrador.trim().toLowerCase(),
    };

    setSalvando(true);

    try {
      const response = await fetch(`${API_URL}/api/oficinas`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("Sua sessão expirou. Faça login novamente.");
        }

        if (response.status === 403) {
          throw new Error("Você não tem permissão para cadastrar oficinas.");
        }

        const dadosErro = await response.json().catch(() => null);

        throw new Error(
          extrairMensagemErro(dadosErro) ||
            (response.status === 409
              ? "Já existe um cadastro com esses dados."
              : "Não foi possível cadastrar a oficina."),
        );
      }

      const oficina = await response.json();

      setSucesso(oficina);
      setFormulario(dadosIniciais);
      setMesmoEmail(false);
    } catch (error) {
      setErro(
        error instanceof Error ? error.message : "Ocorreu um erro inesperado.",
      );
    } finally {
      setSalvando(false);
    }
  };

  return (
    <div className="cadastro-page">
      <header className="admin-topbar">
        <div className="admin-topbar-inner">
          <div className="admin-brand">
            <span className="admin-brand-icon">
              <svg
                width="23"
                height="23"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="1.8"
                strokeLinecap="round"
                strokeLinejoin="round"
                aria-hidden="true"
              >
                <rect x="4" y="3" width="16" height="18" rx="2" />
                <path d="M9 21v-4h6v4M8 7h1M15 7h1M8 11h1M15 11h1" />
              </svg>
            </span>

            <div>
              <strong>AutoGestão Pro</strong>
              <small>Administração da plataforma</small>
            </div>
          </div>

          <span className="admin-user-badge">ADM_SISTEMA</span>
        </div>
      </header>

      <main className="cadastro-container">
        <button
          type="button"
          className="cadastro-voltar"
          onClick={() => navigate("/admin")}
        >
          <span aria-hidden="true">←</span>
          Voltar para oficinas
        </button>

        <div className="cadastro-heading">
          <span className="admin-eyebrow">GESTÃO DE OFICINAS</span>
          <h1>Cadastrar nova oficina</h1>
          <p>
            Preencha os dados da oficina e defina o administrador responsável
            pelo primeiro acesso.
          </p>
        </div>

        {erro && (
          <div className="cadastro-alert cadastro-alert-error" role="alert">
            <span>{erro}</span>
            <button
              type="button"
              onClick={() => setErro("")}
              aria-label="Fechar erro"
            >
              ×
            </button>
          </div>
        )}

        {sucesso ? (
          <section className="cadastro-card cadastro-success">
            <div className="cadastro-success-icon">✓</div>

            <h2>Oficina cadastrada com sucesso!</h2>

            <p>
              A oficina <strong>{sucesso.nome}</strong> foi cadastrada e está
              aguardando a liberação do administrador do sistema.
            </p>

            <div className="cadastro-success-details">
              <span>Link público da oficina</span>
              <code>/o/{sucesso.slug}</code>
            </div>

            <p className="cadastro-success-note">
              O administrador da oficina deverá concluir o primeiro acesso para
              configurar sua senha. A confirmação do e-mail não ativa
              automaticamente a oficina.
            </p>

            <div className="cadastro-form-actions">
              <button
                type="button"
                className="admin-btn admin-btn-outline"
                onClick={() => setSucesso(null)}
              >
                Cadastrar outra
              </button>

              <button
                type="button"
                className="admin-btn admin-btn-primary"
                onClick={() => navigate("/admin")}
              >
                Voltar ao painel
              </button>
            </div>
          </section>
        ) : (
          <form onSubmit={cadastrarOficina} className="cadastro-card">
            <div className="cadastro-section-heading">
              <span className="cadastro-section-icon">01</span>

              <div>
                <h2>Dados da oficina</h2>
                <p>Informações cadastrais e de contato.</p>
              </div>
            </div>

            <div className="cadastro-grid">
              <div className="cadastro-field cadastro-full">
                <label htmlFor="oficina-nome">
                  Nome da oficina <span>*</span>
                </label>
                <input
                  id="oficina-nome"
                  name="nome"
                  type="text"
                  placeholder="Ex.: Oficina Mecânica Central"
                  value={formulario.nome}
                  onChange={alterarCampo}
                  maxLength={150}
                  required
                />
              </div>

              <div className="cadastro-field">
                <label htmlFor="oficina-cnpj">
                  CNPJ <span>*</span>
                </label>
                <input
                  id="oficina-cnpj"
                  name="cnpj"
                  type="text"
                  inputMode="numeric"
                  placeholder="00.000.000/0000-00"
                  value={formulario.cnpj}
                  onChange={alterarCampo}
                  maxLength={18}
                  required
                />
              </div>

              <div className="cadastro-field">
                <label htmlFor="oficina-telefone">
                  Telefone <span>*</span>
                </label>
                <input
                  id="oficina-telefone"
                  name="telefone"
                  type="tel"
                  placeholder="(51) 99999-9999"
                  value={formulario.telefone}
                  onChange={alterarCampo}
                  maxLength={15}
                  required
                />
              </div>

              <div className="cadastro-field">
                <label htmlFor="oficina-email">
                  E-mail comercial <span>*</span>
                </label>
                <input
                  id="oficina-email"
                  name="email"
                  type="email"
                  placeholder="contato@oficina.com.br"
                  value={formulario.email}
                  onChange={alterarCampo}
                  maxLength={150}
                  required
                />
              </div>

              <div className="cadastro-field">
                <label htmlFor="oficina-endereco">Endereço</label>
                <input
                  id="oficina-endereco"
                  name="endereco"
                  type="text"
                  placeholder="Rua, número, bairro, cidade..."
                  value={formulario.endereco}
                  onChange={alterarCampo}
                  maxLength={255}
                />
              </div>
            </div>

            <div className="cadastro-divider" />

            <div className="cadastro-section-heading">
              <span className="cadastro-section-icon">02</span>

              <div>
                <h2>Administrador da oficina</h2>
                <p>Responsável por acessar e gerenciar a oficina.</p>
              </div>
            </div>

            <label className="cadastro-checkbox">
              <input
                type="checkbox"
                checked={mesmoEmail}
                onChange={(evento) => setMesmoEmail(evento.target.checked)}
              />

              <span>O administrador utiliza o mesmo e-mail da oficina</span>
            </label>

            <div className="cadastro-field">
              <label htmlFor="administrador-email">
                E-mail do administrador <span>*</span>
              </label>
              <input
                id="administrador-email"
                name="emailAdministrador"
                type="email"
                placeholder="administrador@oficina.com.br"
                value={emailAdministrador}
                onChange={alterarCampo}
                disabled={mesmoEmail}
                maxLength={150}
                required={!mesmoEmail}
              />

              <small>
                Este e-mail será utilizado para o primeiro acesso e a
                autenticação do administrador.
              </small>
            </div>

            <div className="cadastro-info">
              <div className="cadastro-info-icon">i</div>

              <div>
                <strong>Como funciona a ativação?</strong>

                <p>
                  A oficina será criada como inativa. O administrador poderá
                  confirmar seu e-mail e configurar sua senha pelo fluxo de
                  primeiro acesso, mas somente o ADM_SISTEMA poderá liberar a
                  oficina.
                </p>
              </div>
            </div>

            <div className="cadastro-form-actions">
              <button
                type="button"
                className="admin-btn admin-btn-outline"
                onClick={() => navigate("/admin")}
                disabled={salvando}
              >
                Cancelar
              </button>

              <button
                type="submit"
                className="admin-btn admin-btn-primary"
                disabled={salvando}
              >
                {salvando ? "Cadastrando..." : "+ Cadastrar oficina"}
              </button>
            </div>
          </form>
        )}
      </main>
    </div>
  );
}

export default OficinaCadastro;
