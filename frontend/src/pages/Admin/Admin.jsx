import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import "./Admin.css";

const API_URL = (
  import.meta.env.VITE_API_URL || "http://localhost:8080"
).replace(/\/$/, "");

const obterToken = () => {
  try {
    const sessao = sessionStorage.getItem("autogestao_sessao");
    if (!sessao) return null;

    const dados = JSON.parse(sessao);
    return dados.token || null;
  } catch {
    return null;
  }
};

async function buscarOficinas(signal) {
  const token = obterToken();

  if (!token) {
    throw new Error("Sessão não encontrada. Faça login novamente.");
  }

  const response = await fetch(`${API_URL}/api/oficinas`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    signal,
  });

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("Sua sessão expirou. Faça login novamente.");
    }

    if (response.status === 403) {
      throw new Error("Você não tem permissão para consultar oficinas.");
    }

    throw new Error("Não foi possível carregar as oficinas.");
  }

  const dados = await response.json();

  if (!Array.isArray(dados)) {
    throw new Error("Formato de resposta inesperado.");
  }

  return dados;
}

const Icon = ({ name, size = 20 }) => {
  const paths = {
    building: (
      <>
        <rect x="4" y="3" width="16" height="18" rx="2" />
        <path d="M9 21v-4h6v4M8 7h1M15 7h1M8 11h1M15 11h1" />
      </>
    ),
    plus: <path d="M12 5v14M5 12h14" />,
    search: (
      <>
        <circle cx="11" cy="11" r="7" />
        <path d="m20 20-4-4" />
      </>
    ),
    check: <path d="m5 12 4 4L19 6" />,
    pause: (
      <>
        <rect x="6" y="4" width="4" height="16" rx="1" />
        <rect x="14" y="4" width="4" height="16" rx="1" />
      </>
    ),
    copy: (
      <>
        <rect x="8" y="8" width="12" height="12" rx="2" />
        <path d="M16 8V6a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h2" />
      </>
    ),
    external: (
      <>
        <path d="M13 5h6v6M19 5l-9 9" />
        <path d="M19 13v5a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1h5" />
      </>
    ),
    refresh: (
      <>
        <path d="M20 7v5h-5M4 17v-5h5" />
        <path d="M5 9a8 8 0 0 1 14-2l1 5M4 12l1 5a8 8 0 0 0 14-2" />
      </>
    ),
    alert: (
      <>
        <circle cx="12" cy="12" r="10" />
        <path d="M12 7v6M12 17h.01" />
      </>
    ),
    close: <path d="M18 6 6 18M6 6l12 12" />,
    link: (
      <>
        <path d="M10 13a5 5 0 0 0 7.5.5l3-3a5 5 0 0 0-7-7l-1.7 1.7" />
        <path d="M14 11a5 5 0 0 0-7.5-.5l-3 3a5 5 0 0 0 7 7l1.7-1.7" />
      </>
    ),
    trash: (
      <>
        <path d="M3 6h18M8 6V4h8v2M5 6l1 15h12l1-15" />
        <path d="M10 10v7M14 10v7" />
      </>
    ),
  };

  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      {paths[name]}
    </svg>
  );
};

function Admin() {
  const [oficinas, setOficinas] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");
  const [busca, setBusca] = useState("");
  const [filtro, setFiltro] = useState("todas");

  // modal: { tipo: "ativacao" | "exclusao", oficina }
  const [modal, setModal] = useState(null);
  const [confirmacaoExclusao, setConfirmacaoExclusao] = useState("");
  const [salvando, setSalvando] = useState(false);
  const [aviso, setAviso] = useState("");

  const requisicaoRef = useRef(null);

  useEffect(() => {
    const controller = new AbortController();
    requisicaoRef.current = controller;

    buscarOficinas(controller.signal)
      .then((dados) => {
        if (controller.signal.aborted) return;

        setOficinas(dados);
        setErro("");
      })
      .catch((error) => {
        if (controller.signal.aborted) return;
        setErro(error.message);
      })
      .finally(() => {
        if (controller.signal.aborted) return;
        setCarregando(false);
      });

    return () => {
      controller.abort();
    };
  }, []);

  const atualizarOficinas = useCallback(async () => {
    requisicaoRef.current?.abort();

    const controller = new AbortController();
    requisicaoRef.current = controller;

    setCarregando(true);
    setErro("");

    try {
      const dados = await buscarOficinas(controller.signal);

      if (controller.signal.aborted) return;

      setOficinas(dados);
    } catch (error) {
      if (controller.signal.aborted) return;
      setErro(error.message);
    } finally {
      if (!controller.signal.aborted) {
        setCarregando(false);
      }
    }
  }, []);

  const indicadores = useMemo(() => {
    const ativas = oficinas.filter((oficina) => oficina.ativo).length;

    return {
      total: oficinas.length,
      ativas,
      inativas: oficinas.length - ativas,
    };
  }, [oficinas]);

  const oficinasFiltradas = useMemo(() => {
    const termo = busca.trim().toLowerCase();

    return oficinas.filter((oficina) => {
      const correspondeBusca = [oficina.nome, oficina.cnpj, oficina.slug].some(
        (valor) =>
          String(valor || "")
            .toLowerCase()
            .includes(termo),
      );

      const correspondeFiltro =
        filtro === "todas" ||
        (filtro === "ativas" && oficina.ativo) ||
        (filtro === "inativas" && !oficina.ativo);

      return correspondeBusca && correspondeFiltro;
    });
  }, [oficinas, busca, filtro]);

  const linkOficina = (slug) =>
    `${window.location.origin}/o/${encodeURIComponent(slug)}`;

  const copiarLink = async (slug) => {
    try {
      await navigator.clipboard.writeText(linkOficina(slug));
      setAviso("Link copiado para a área de transferência.");
    } catch {
      setAviso("Não foi possível copiar o link.");
    }
  };

  const abrirModalAtivacao = (oficina) => {
    setErro("");
    setAviso("");
    setModal({ tipo: "ativacao", oficina });
  };

  const abrirModalExclusao = (oficina) => {
    setErro("");
    setAviso("");
    setConfirmacaoExclusao("");
    setModal({ tipo: "exclusao", oficina });
  };

  const fecharModal = () => {
    if (salvando) return;

    setModal(null);
    setConfirmacaoExclusao("");
  };

  const alterarAtivacao = async () => {
    if (!modal || modal.tipo !== "ativacao" || salvando) return;

    const oficinaSelecionada = modal.oficina;

    setSalvando(true);
    setErro("");

    try {
      const token = obterToken();

      if (!token) {
        throw new Error("Sessão não encontrada. Faça login novamente.");
      }

      const novoStatus = !oficinaSelecionada.ativo;

      const response = await fetch(
        `${API_URL}/api/oficinas/${oficinaSelecionada.id}/ativacao`,
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ ativo: novoStatus }),
        },
      );

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("Sua sessão expirou. Faça login novamente.");
        }

        if (response.status === 403) {
          throw new Error("Você não tem permissão para alterar esta oficina.");
        }

        throw new Error("Não foi possível alterar a situação da oficina.");
      }

      setOficinas((anteriores) =>
        anteriores.map((oficina) =>
          oficina.id === oficinaSelecionada.id
            ? { ...oficina, ativo: novoStatus }
            : oficina,
        ),
      );

      setAviso(
        novoStatus
          ? `Oficina "${oficinaSelecionada.nome}" ativada com sucesso.`
          : `Oficina "${oficinaSelecionada.nome}" pausada com sucesso.`,
      );

      setModal(null);
    } catch (error) {
      setErro(error.message);
      setModal(null);
    } finally {
      setSalvando(false);
    }
  };

  // Exclusão definitiva da oficina.
  const excluirOficina = async () => {
    if (
      !modal ||
      modal.tipo !== "exclusao" ||
      salvando ||
      confirmacaoExclusao.trim() !== "EXCLUIR"
    ) {
      return;
    }

    const oficinaSelecionada = modal.oficina;

    setSalvando(true);
    setErro("");

    try {
      const token = obterToken();

      if (!token) {
        throw new Error("Sessão não encontrada. Faça login novamente.");
      }

      const response = await fetch(
        `${API_URL}/api/oficinas/${oficinaSelecionada.id}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("Sua sessão expirou. Faça login novamente.");
        }

        if (response.status === 403) {
          throw new Error("Você não tem permissão para excluir oficinas.");
        }

        if (response.status === 404) {
          throw new Error(
            "Esta oficina não foi encontrada. Atualize a listagem.",
          );
        }

        throw new Error("Não foi possível excluir a oficina. Tente novamente.");
      }

      // Remove da listagem apenas após sucesso do backend.
      setOficinas((anteriores) =>
        anteriores.filter((oficina) => oficina.id !== oficinaSelecionada.id),
      );

      setAviso(
        `Oficina "${oficinaSelecionada.nome}" excluída definitivamente.`,
      );

      setModal(null);
      setConfirmacaoExclusao("");
    } catch (error) {
      setErro(error.message);
      setModal(null);
      setConfirmacaoExclusao("");
    } finally {
      setSalvando(false);
    }
  };

  const modalExclusao = modal?.tipo === "exclusao";
  const oficinaModal = modal?.oficina;

  return (
    <div className="admin-page">
      <header className="admin-topbar">
        <div className="admin-topbar-inner">
          <div className="admin-brand">
            <span className="admin-brand-icon">
              <Icon name="building" size={23} />
            </span>

            <div>
              <strong>AutoGestão Pro</strong>
              <small>Administração da plataforma</small>
            </div>
          </div>

          <span className="admin-user-badge">ADM_SISTEMA</span>
        </div>
      </header>

      <main className="admin-container">
        <section className="admin-heading">
          <div>
            <span className="admin-eyebrow">PAINEL ADMINISTRATIVO</span>

            <h1>Gestão de oficinas</h1>

            <p>
              Gerencie os cadastros, acompanhe os acessos e controle a situação
              das oficinas.
            </p>
          </div>

          <button
            type="button"
            className="admin-btn admin-btn-primary"
            onClick={() => window.location.assign("/admin/oficinas/nova")}
          >
            <Icon name="plus" size={19} />
            Nova oficina
          </button>
        </section>

        {aviso && (
          <div className="admin-notice" role="status">
            <Icon name="check" size={18} />
            <span>{aviso}</span>

            <button
              type="button"
              onClick={() => setAviso("")}
              aria-label="Fechar aviso"
            >
              <Icon name="close" size={17} />
            </button>
          </div>
        )}

        {erro && (
          <div className="admin-error" role="alert">
            <Icon name="alert" size={19} />
            <span>{erro}</span>

            <button
              type="button"
              onClick={() => setErro("")}
              aria-label="Fechar erro"
            >
              <Icon name="close" size={17} />
            </button>
          </div>
        )}

        <section className="admin-stats">
          <div className="admin-stat">
            <div className="admin-stat-icon admin-stat-blue">
              <Icon name="building" />
            </div>

            <span>Total de oficinas</span>
            <strong>{carregando ? "—" : indicadores.total}</strong>
            <small>Cadastradas na plataforma</small>
          </div>

          <div className="admin-stat">
            <div className="admin-stat-icon admin-stat-green">
              <Icon name="check" />
            </div>

            <span>Oficinas ativas</span>
            <strong>{carregando ? "—" : indicadores.ativas}</strong>
            <small>Com acesso liberado</small>
          </div>

          <div className="admin-stat">
            <div className="admin-stat-icon admin-stat-orange">
              <Icon name="pause" />
            </div>

            <span>Oficinas inativas</span>
            <strong>{carregando ? "—" : indicadores.inativas}</strong>
            <small>Aguardando liberação ou pausadas</small>
          </div>
        </section>

        <section className="admin-panel">
          <div className="admin-panel-heading">
            <div>
              <h2>Oficinas cadastradas</h2>
              <p>Consulte e gerencie todas as oficinas da plataforma.</p>
            </div>

            <button
              type="button"
              className="admin-btn admin-btn-outline"
              onClick={atualizarOficinas}
              disabled={carregando || salvando}
            >
              <Icon name="refresh" size={17} />
              Atualizar
            </button>
          </div>

          <div className="admin-toolbar">
            <div className="admin-search">
              <Icon name="search" size={19} />

              <input
                type="search"
                value={busca}
                onChange={(event) => setBusca(event.target.value)}
                placeholder="Buscar por nome, CNPJ ou slug..."
                aria-label="Buscar oficinas"
              />
            </div>

            <div className="admin-filters">
              {[
                ["todas", "Todas"],
                ["ativas", "Ativas"],
                ["inativas", "Inativas"],
              ].map(([valor, label]) => (
                <button
                  type="button"
                  key={valor}
                  className={filtro === valor ? "active" : ""}
                  onClick={() => setFiltro(valor)}
                  aria-pressed={filtro === valor}
                >
                  {label}
                </button>
              ))}
            </div>
          </div>

          <div className="admin-result-count">
            {carregando
              ? "Carregando oficinas..."
              : `${oficinasFiltradas.length} oficina(s) encontrada(s)`}
          </div>

          {carregando ? (
            <div className="admin-empty">Carregando oficinas...</div>
          ) : erro && oficinas.length === 0 ? (
            <div className="admin-empty">
              Não foi possível carregar a listagem.
              <button
                type="button"
                className="admin-btn admin-btn-outline"
                onClick={atualizarOficinas}
              >
                Tentar novamente
              </button>
            </div>
          ) : oficinasFiltradas.length === 0 ? (
            <div className="admin-empty">
              Nenhuma oficina encontrada para os filtros selecionados.
            </div>
          ) : (
            <div className="admin-offices">
              {oficinasFiltradas.map((oficina) => (
                <article className="admin-office" key={oficina.id}>
                  <div className="admin-office-main">
                    <div className="admin-office-avatar">
                      <Icon name="building" size={22} />
                    </div>

                    <div className="admin-office-info">
                      <div className="admin-office-title">
                        <h3>{oficina.nome}</h3>

                        <span
                          className={`admin-status ${
                            oficina.ativo ? "is-active" : "is-inactive"
                          }`}
                        >
                          <span className="admin-status-dot" />
                          {oficina.ativo ? "Ativa" : "Inativa"}
                        </span>
                      </div>

                      <p>CNPJ: {oficina.cnpj}</p>
                      <p>{oficina.email}</p>
                    </div>
                  </div>

                  <div className="admin-office-link">
                    <span>
                      <Icon name="link" size={16} />
                      Link público da oficina
                    </span>

                    <code>/o/{oficina.slug}</code>
                  </div>

                  <div className="admin-office-actions">
                    <div className="admin-link-actions">
                      <button
                        type="button"
                        className="admin-btn admin-btn-outline"
                        onClick={() => copiarLink(oficina.slug)}
                      >
                        <Icon name="copy" size={16} />
                        Copiar link
                      </button>

                      <a
                        className="admin-btn admin-btn-outline"
                        href={linkOficina(oficina.slug)}
                        target="_blank"
                        rel="noopener noreferrer"
                      >
                        <Icon name="external" size={16} />
                        Abrir página
                      </a>
                    </div>

                    <div
                      style={{
                        display: "flex",
                        gap: "8px",
                        flexWrap: "wrap",
                      }}
                    >
                      <button
                        type="button"
                        className={`admin-btn ${
                          oficina.ativo
                            ? "admin-btn-danger"
                            : "admin-btn-primary"
                        }`}
                        onClick={() => abrirModalAtivacao(oficina)}
                        disabled={salvando}
                      >
                        <Icon
                          name={oficina.ativo ? "pause" : "check"}
                          size={17}
                        />

                        {oficina.ativo ? "Pausar acesso" : "Ativar oficina"}
                      </button>

                      <button
                        type="button"
                        className="admin-btn admin-btn-danger"
                        onClick={() => abrirModalExclusao(oficina)}
                        disabled={salvando}
                        title="Excluir definitivamente a oficina"
                      >
                        <Icon name="trash" size={17} />
                        Excluir
                      </button>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          )}
        </section>
      </main>

      {modal && oficinaModal && (
        <div
          className="admin-modal-backdrop"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) {
              fecharModal();
            }
          }}
        >
          <div
            className="admin-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="admin-modal-title"
          >
            {modalExclusao ? (
              <>
                <div className="admin-modal-icon admin-modal-warning">
                  <Icon name="trash" size={26} />
                </div>

                <h2 id="admin-modal-title">Excluir oficina definitivamente?</h2>

                <p>
                  Você está prestes a excluir a oficina{" "}
                  <strong>"{oficinaModal.nome}"</strong>.
                </p>

                <div
                  role="alert"
                  style={{
                    marginTop: "16px",
                    marginBottom: "16px",
                    padding: "14px",
                    border: "1px solid #dc2626",
                    borderRadius: "8px",
                  }}
                >
                  <strong>Atenção: esta ação é irreversível.</strong>

                  <p style={{ marginTop: "8px" }}>
                    Serão excluídos permanentemente:
                  </p>

                  <ul style={{ paddingLeft: "20px", marginTop: "8px" }}>
                    <li>O cadastro da oficina</li>
                    <li>Os administradores vinculados à oficina</li>
                    <li>Os veículos cadastrados nessa oficina</li>
                    <li>As ordens de serviço dessa oficina</li>
                    <li>Os vínculos dos clientes com essa oficina</li>
                  </ul>

                  <p style={{ marginTop: "8px" }}>
                    Os clientes globais e os dados de outras oficinas serão
                    preservados.
                  </p>
                </div>

                <p>
                  Para confirmar, digite <strong>EXCLUIR</strong>:
                </p>

                <input
                  type="text"
                  value={confirmacaoExclusao}
                  onChange={(event) =>
                    setConfirmacaoExclusao(event.target.value)
                  }
                  placeholder="Digite EXCLUIR"
                  aria-label="Confirmação de exclusão"
                  autoComplete="off"
                  disabled={salvando}
                  style={{
                    width: "100%",
                    padding: "12px",
                    marginTop: "10px",
                    marginBottom: "16px",
                    borderRadius: "8px",
                    border: "1px solid #94a3b8",
                    boxSizing: "border-box",
                  }}
                />

                <div className="admin-modal-actions">
                  <button
                    type="button"
                    className="admin-btn admin-btn-outline"
                    onClick={fecharModal}
                    disabled={salvando}
                  >
                    Cancelar
                  </button>

                  <button
                    type="button"
                    className="admin-btn admin-btn-danger"
                    onClick={excluirOficina}
                    disabled={
                      salvando || confirmacaoExclusao.trim() !== "EXCLUIR"
                    }
                  >
                    <Icon name="trash" size={17} />

                    {salvando ? "Excluindo..." : "Excluir definitivamente"}
                  </button>
                </div>
              </>
            ) : (
              <>
                <div
                  className={`admin-modal-icon ${
                    oficinaModal.ativo
                      ? "admin-modal-warning"
                      : "admin-modal-success"
                  }`}
                >
                  <Icon
                    name={oficinaModal.ativo ? "pause" : "check"}
                    size={26}
                  />
                </div>

                <h2 id="admin-modal-title">
                  {oficinaModal.ativo
                    ? "Pausar acesso da oficina?"
                    : "Ativar oficina?"}
                </h2>

                <p>
                  {oficinaModal.ativo
                    ? `A oficina "${oficinaModal.nome}" terá seu acesso às funcionalidades protegidas bloqueado.`
                    : `Deseja liberar o acesso da oficina "${oficinaModal.nome}" à plataforma?`}
                </p>

                <div className="admin-modal-actions">
                  <button
                    type="button"
                    className="admin-btn admin-btn-outline"
                    onClick={fecharModal}
                    disabled={salvando}
                  >
                    Cancelar
                  </button>

                  <button
                    type="button"
                    className={`admin-btn ${
                      oficinaModal.ativo
                        ? "admin-btn-danger"
                        : "admin-btn-primary"
                    }`}
                    onClick={alterarAtivacao}
                    disabled={salvando}
                  >
                    {salvando
                      ? "Salvando..."
                      : oficinaModal.ativo
                        ? "Confirmar pausa"
                        : "Confirmar ativação"}
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default Admin;
