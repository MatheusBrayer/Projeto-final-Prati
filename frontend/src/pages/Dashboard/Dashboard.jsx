import React, { useEffect, useState } from "react";
import { getOrdensServico } from "../../services/api";

export default function Dashboard() {
  const [ordens, setOrdens] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalType, setModalType] = useState(null); // 'andamento', 'finalizadas' ou 'ticket'

  async function fetchOrdens() {
    try {
      const data = await getOrdensServico();
      setOrdens(data || []);
    } catch (error) {
      console.error("Erro ao carregar dados do dashboard:", error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchOrdens();
  }, []);

  async function handleFinalizar(id) {
    try {
      // Ajustado para a rota padrão do Spring Boot (confirme se o controller usa /api/ordens ou /api/ordens-servico)
      const response = await fetch(
        `http://localhost:8080/api/ordens/${id}/finalizar`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
        },
      );

      if (response.ok) {
        fetchOrdens();
      } else {
        alert("Erro ao finalizar a ordem de serviço no servidor Java.");
      }
    } catch (error) {
      console.error("Erro ao finalizar a ordem de serviço:", error);
      alert("Erro de conexão ao tentar finalizar.");
    }
  }

  // Cálculos das métricas compatíveis com os enums do Java (FINALIZADA ou CONLUIDA)
  const ordensEmAndamentoList = ordens.filter(
    (os) => os.status !== "FINALIZADA" && os.status !== "CONLUIDA",
  );

  const ordensFinalizadasList = ordens.filter(
    (os) => os.status === "FINALIZADA" || os.status === "CONLUIDA",
  );

  const faturamentoTotal = ordensFinalizadasList.reduce(
    (acc, os) =>
      acc + (Number(os.valor || os.valorTotal || os.valor_total) || 0),
    0,
  );

  const ticketMedio =
    ordensFinalizadasList.length > 0
      ? faturamentoTotal / ordensFinalizadasList.length
      : 0;

  return (
    <div style={{ padding: "32px", maxWidth: "1200px", margin: "0 auto" }}>
      <div style={{ marginBottom: "24px" }}>
        <h1
          style={{
            fontSize: "26px",
            fontWeight: "700",
            color: "#1e293b",
            margin: 0,
          }}
        >
          Dashboard da Oficina
        </h1>
        <p style={{ fontSize: "14px", color: "#64748b", marginTop: "4px" }}>
          Acompanhe o status das ordens de serviço e o faturamento. Clique nos
          cards para ver os detalhes.
        </p>
      </div>

      {loading ? (
        <p style={{ color: "#64748b", fontSize: "14px" }}>
          Carregando métricas...
        </p>
      ) : (
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
            gap: "20px",
          }}
        >
          {/* Card OS em Andamento */}
          <div
            onClick={() => setModalType("andamento")}
            style={{
              backgroundColor: "#ffffff",
              padding: "24px",
              borderRadius: "12px",
              border: "1px solid #e2e8f0",
              boxShadow: "0 4px 6px -1px rgba(0,0,0,0.05)",
              cursor: "pointer",
              transition: "transform 0.2s, box-shadow 0.2s",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-3px)";
              e.currentTarget.style.boxShadow =
                "0 10px 15px -3px rgba(0,0,0,0.1)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0)";
              e.currentTarget.style.boxShadow =
                "0 4px 6px -1px rgba(0,0,0,0.05)";
            }}
          >
            <span
              style={{
                fontSize: "12px",
                fontWeight: "700",
                color: "#64748b",
                textTransform: "uppercase",
              }}
            >
              Ordens em Andamento
            </span>
            <h2
              style={{
                fontSize: "32px",
                fontWeight: "800",
                color: "#d97706",
                margin: "8px 0 0 0",
              }}
            >
              {ordensEmAndamentoList.length}
            </h2>
          </div>

          {/* Card OS Finalizadas */}
          <div
            onClick={() => setModalType("finalizadas")}
            style={{
              backgroundColor: "#ffffff",
              padding: "24px",
              borderRadius: "12px",
              border: "1px solid #e2e8f0",
              boxShadow: "0 4px 6px -1px rgba(0,0,0,0.05)",
              cursor: "pointer",
              transition: "transform 0.2s, box-shadow 0.2s",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-3px)";
              e.currentTarget.style.boxShadow =
                "0 10px 15px -3px rgba(0,0,0,0.1)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0)";
              e.currentTarget.style.boxShadow =
                "0 4px 6px -1px rgba(0,0,0,0.05)";
            }}
          >
            <span
              style={{
                fontSize: "12px",
                fontWeight: "700",
                color: "#64748b",
                textTransform: "uppercase",
              }}
            >
              Ordens Finalizadas
            </span>
            <h2
              style={{
                fontSize: "32px",
                fontWeight: "800",
                color: "#16a34a",
                margin: "8px 0 0 0",
              }}
            >
              {ordensFinalizadasList.length}
            </h2>
          </div>

          {/* Card Faturamento Total */}
          <div
            onClick={() => setModalType("finalizadas")}
            style={{
              backgroundColor: "#ffffff",
              padding: "24px",
              borderRadius: "12px",
              border: "1px solid #e2e8f0",
              boxShadow: "0 4px 6px -1px rgba(0,0,0,0.05)",
              cursor: "pointer",
              transition: "transform 0.2s, box-shadow 0.2s",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-3px)";
              e.currentTarget.style.boxShadow =
                "0 10px 15px -3px rgba(0,0,0,0.1)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0)";
              e.currentTarget.style.boxShadow =
                "0 4px 6px -1px rgba(0,0,0,0.05)";
            }}
          >
            <span
              style={{
                fontSize: "12px",
                fontWeight: "700",
                color: "#64748b",
                textTransform: "uppercase",
              }}
            >
              Faturamento Total
            </span>
            <h2
              style={{
                fontSize: "32px",
                fontWeight: "800",
                color: "#2563eb",
                margin: "8px 0 0 0",
              }}
            >
              R$ {faturamentoTotal.toFixed(2)}
            </h2>
          </div>

          {/* Card Ticket Médio (TKM) */}
          <div
            onClick={() => setModalType("ticket")}
            style={{
              backgroundColor: "#ffffff",
              padding: "24px",
              borderRadius: "12px",
              border: "1px solid #e2e8f0",
              boxShadow: "0 4px 6px -1px rgba(0,0,0,0.05)",
              cursor: "pointer",
              transition: "transform 0.2s, box-shadow 0.2s",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = "translateY(-3px)";
              e.currentTarget.style.boxShadow =
                "0 10px 15px -3px rgba(0,0,0,0.1)";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = "translateY(0)";
              e.currentTarget.style.boxShadow =
                "0 4px 6px -1px rgba(0,0,0,0.05)";
            }}
          >
            <span
              style={{
                fontSize: "12px",
                fontWeight: "700",
                color: "#64748b",
                textTransform: "uppercase",
              }}
            >
              Ticket Médio (TKM)
            </span>
            <h2
              style={{
                fontSize: "32px",
                fontWeight: "800",
                color: "#7c3aed",
                margin: "8px 0 0 0",
              }}
            >
              R$ {ticketMedio.toFixed(2)}
            </h2>
          </div>
        </div>
      )}

      {/* MODAL DE DETALHAMENTO */}
      {modalType && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            backgroundColor: "rgba(0,0,0,0.5)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 50,
            padding: "16px",
          }}
        >
          <div
            style={{
              backgroundColor: "#ffffff",
              borderRadius: "12px",
              width: "100%",
              maxWidth: "850px",
              maxHeight: "85vh",
              overflowY: "auto",
              padding: "24px",
              boxShadow: "0 20px 25px -5px rgba(0,0,0,0.1)",
            }}
          >
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                marginBottom: "20px",
                borderBottom: "1px solid #e2e8f0",
                paddingBottom: "12px",
              }}
            >
              <h2
                style={{
                  fontSize: "20px",
                  fontWeight: "700",
                  color: "#1e293b",
                  margin: 0,
                }}
              >
                {modalType === "andamento"
                  ? "Ordens de Serviço em Andamento"
                  : modalType === "finalizadas"
                    ? "Ordens de Serviço Finalizadas"
                    : "Detalhamento do Ticket Médio (TKM)"}
              </h2>
              <button
                onClick={() => setModalType(null)}
                style={{
                  backgroundColor: "#f1f5f9",
                  border: "none",
                  borderRadius: "6px",
                  padding: "6px 12px",
                  fontWeight: "600",
                  cursor: "pointer",
                  color: "#475569",
                }}
              >
                Fechar
              </button>
            </div>

            {modalType === "ticket" && (
              <div
                style={{
                  backgroundColor: "#f8fafc",
                  padding: "16px",
                  borderRadius: "8px",
                  marginBottom: "20px",
                  border: "1px solid #e2e8f0",
                }}
              >
                <p style={{ margin: 0, fontSize: "14px", color: "#334155" }}>
                  <strong>Cálculo do Ticket Médio:</strong> Faturamento Total (
                  <strong>R$ {faturamentoTotal.toFixed(2)}</strong>) dividido
                  pelo total de ordens finalizadas (
                  <strong>{ordensFinalizadasList.length}</strong>).
                </p>
              </div>
            )}

            {/* Listagem dentro do Modal */}
            {(modalType === "andamento"
              ? ordensEmAndamentoList
              : ordensFinalizadasList
            ).length === 0 ? (
              <p
                style={{
                  textAlign: "center",
                  color: "#64748b",
                  padding: "24px",
                }}
              >
                Nenhuma ordem encontrada nesta categoria.
              </p>
            ) : (
              <table
                style={{
                  width: "100%",
                  borderCollapse: "collapse",
                  textAlign: "left",
                }}
              >
                <thead>
                  <tr
                    style={{
                      backgroundColor: "#f8fafc",
                      borderBottom: "1px solid #e2e8f0",
                    }}
                  >
                    <th
                      style={{
                        padding: "12px",
                        fontSize: "12px",
                        color: "#64748b",
                      }}
                    >
                      OS #
                    </th>
                    <th
                      style={{
                        padding: "12px",
                        fontSize: "12px",
                        color: "#64748b",
                      }}
                    >
                      Veículo
                    </th>
                    <th
                      style={{
                        padding: "12px",
                        fontSize: "12px",
                        color: "#64748b",
                      }}
                    >
                      Cliente
                    </th>
                    <th
                      style={{
                        padding: "12px",
                        fontSize: "12px",
                        color: "#64748b",
                      }}
                    >
                      Descrição
                    </th>
                    <th
                      style={{
                        padding: "12px",
                        fontSize: "12px",
                        color: "#64748b",
                      }}
                    >
                      Valor
                    </th>
                    <th
                      style={{
                        padding: "12px",
                        fontSize: "12px",
                        color: "#64748b",
                        textAlign: "center",
                      }}
                    >
                      Ação
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {(modalType === "andamento"
                    ? ordensEmAndamentoList
                    : ordensFinalizadasList
                  ).map((os) => (
                    <tr
                      key={os.id}
                      style={{ borderBottom: "1px solid #e2e8f0" }}
                    >
                      <td
                        style={{
                          padding: "12px",
                          fontWeight: "700",
                          color: "#1e293b",
                        }}
                      >
                        {String(os.id).padStart(3, "0")}
                      </td>
                      <td style={{ padding: "12px" }}>
                        {/* Lida com propriedades do Java (modelo/placa ou objeto veiculo) */}
                        <div style={{ fontWeight: "600" }}>
                          {os.veiculo?.modelo || os.modelo || "Veículo"}
                        </div>
                        <div style={{ fontSize: "11px", color: "#64748b" }}>
                          {os.veiculo?.placa || os.placa || ""}
                        </div>
                      </td>
                      <td style={{ padding: "12px" }}>
                        {/* Lida com propriedades do Java (cliente ou objeto cliente) */}
                        <div style={{ fontWeight: "600" }}>
                          {os.cliente?.nome || os.cliente || "Cliente"}
                        </div>
                        <div style={{ fontSize: "11px", color: "#64748b" }}>
                          {os.cliente?.telefone || os.telefone || ""}
                        </div>
                      </td>
                      <td
                        style={{
                          padding: "12px",
                          color: "#64748b",
                          maxWidth: "180px",
                        }}
                      >
                        {os.descricao || os.problema || "Sem descrição"}
                      </td>
                      <td
                        style={{
                          padding: "12px",
                          fontWeight: "700",
                          color: "#16a34a",
                        }}
                      >
                        R${" "}
                        {Number(
                          os.valor || os.valorTotal || os.valor_total || 0,
                        ).toFixed(2)}
                      </td>
                      <td style={{ padding: "12px", textAlign: "center" }}>
                        {modalType === "andamento" ? (
                          <button
                            onClick={() => handleFinalizar(os.id)}
                            style={{
                              backgroundColor: "#16a34a",
                              color: "#fff",
                              border: "none",
                              padding: "6px 10px",
                              borderRadius: "6px",
                              fontSize: "12px",
                              fontWeight: "600",
                              cursor: "pointer",
                            }}
                          >
                            Finalizar
                          </button>
                        ) : (
                          <span
                            style={{
                              padding: "4px 8px",
                              fontSize: "11px",
                              fontWeight: "700",
                              borderRadius: "9999px",
                              backgroundColor: "#dcfce7",
                              color: "#166534",
                            }}
                          >
                            Concluída
                          </span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
