import { useEffect, useState } from "react";
import { getClientes, createCliente } from "../../services/api";

export default function Clients() {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [nome, setNome] = useState("");
  const [telefone, setTelefone] = useState("");
  const [email, setEmail] = useState("");
  const [cidade, setCidade] = useState("");
  const [estado, setEstado] = useState("");
  const [placa, setPlaca] = useState("");

  async function fetchClientes() {
    try {
      const data = await getClientes();
      setClientes(data);
    } catch (error) {
      console.error("Erro ao carregar clientes:", error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    let ativo = true;

    async function carregarClientes() {
      try {
        const data = await getClientes();

        if (ativo) {
          setClientes(data);
        }
      } catch (error) {
        console.error("Erro ao carregar clientes:", error);
      } finally {
        if (ativo) {
          setLoading(false);
        }
      }
    }

    carregarClientes();

    return () => {
      ativo = false;
    };
  }, []);

  async function handleSubmit(e) {
    e.preventDefault();
    try {
      const novoCliente = {
        nome,
        telefone,
        email,
        cidade,
        estado,
        placa,
      };

      await createCliente(novoCliente);

      setIsModalOpen(false);
      setNome("");
      setTelefone("");
      setEmail("");
      setCidade("");
      setEstado("");
      setPlaca("");

      fetchClientes();
    } catch (error) {
      console.error("Erro ao cadastrar cliente:", error);
      alert("Erro ao cadastrar cliente.");
    }
  }

  return (
    <div style={{ padding: "32px", maxWidth: "1200px", margin: "0 auto" }}>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "24px",
        }}
      >
        <div>
          <h1
            style={{
              fontSize: "26px",
              fontWeight: "700",
              color: "#1e293b",
              margin: 0,
            }}
          >
            Gerenciamento de Clientes
          </h1>
          <p style={{ fontSize: "14px", color: "#64748b", marginTop: "4px" }}>
            Cadastre e consulte os clientes e os veículos associados.
          </p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          style={{
            backgroundColor: "#2563eb",
            color: "#ffffff",
            fontWeight: "600",
            padding: "10px 16px",
            borderRadius: "8px",
            border: "none",
            cursor: "pointer",
            fontSize: "14px",
          }}
        >
          + Novo Cliente
        </button>
      </div>

      <div
        style={{
          backgroundColor: "#ffffff",
          boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)",
          borderRadius: "12px",
          overflow: "hidden",
          border: "1px solid #e2e8f0",
        }}
      >
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
                  padding: "16px",
                  fontSize: "12px",
                  fontWeight: "600",
                  color: "#64748b",
                  textTransform: "uppercase",
                }}
              >
                ID
              </th>
              <th
                style={{
                  padding: "16px",
                  fontSize: "12px",
                  fontWeight: "600",
                  color: "#64748b",
                  textTransform: "uppercase",
                }}
              >
                Nome
              </th>
              <th
                style={{
                  padding: "16px",
                  fontSize: "12px",
                  fontWeight: "600",
                  color: "#64748b",
                  textTransform: "uppercase",
                }}
              >
                Telefone
              </th>
              <th
                style={{
                  padding: "16px",
                  fontSize: "12px",
                  fontWeight: "600",
                  color: "#64748b",
                  textTransform: "uppercase",
                }}
              >
                E-mail
              </th>
              <th
                style={{
                  padding: "16px",
                  fontSize: "12px",
                  fontWeight: "600",
                  color: "#64748b",
                  textTransform: "uppercase",
                }}
              >
                Cidade/Estado
              </th>
              <th
                style={{
                  padding: "16px",
                  fontSize: "12px",
                  fontWeight: "600",
                  color: "#64748b",
                  textTransform: "uppercase",
                }}
              >
                Placa do Carro
              </th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td
                  colSpan="6"
                  style={{
                    padding: "24px",
                    textAlign: "center",
                    color: "#64748b",
                    fontSize: "14px",
                  }}
                >
                  Carregando...
                </td>
              </tr>
            ) : clientes.length === 0 ? (
              <tr>
                <td
                  colSpan="6"
                  style={{
                    padding: "24px",
                    textAlign: "center",
                    color: "#64748b",
                    fontSize: "14px",
                  }}
                >
                  Nenhum cliente cadastrado.
                </td>
              </tr>
            ) : (
              clientes.map((cli) => (
                <tr key={cli.id} style={{ borderBottom: "1px solid #e2e8f0" }}>
                  <td
                    style={{
                      padding: "16px",
                      fontSize: "14px",
                      fontWeight: "700",
                      color: "#1e293b",
                    }}
                  >
                    {String(cli.id).padStart(3, "0")}
                  </td>
                  <td
                    style={{
                      padding: "16px",
                      fontSize: "14px",
                      fontWeight: "600",
                      color: "#334155",
                    }}
                  >
                    {cli.nome}
                  </td>
                  <td
                    style={{
                      padding: "16px",
                      fontSize: "14px",
                      color: "#64748b",
                    }}
                  >
                    {cli.telefone || "-"}
                  </td>
                  <td
                    style={{
                      padding: "16px",
                      fontSize: "14px",
                      color: "#64748b",
                    }}
                  >
                    {cli.email || "-"}
                  </td>
                  <td
                    style={{
                      padding: "16px",
                      fontSize: "14px",
                      color: "#64748b",
                    }}
                  >
                    {cli.cidade ? `${cli.cidade}/${cli.estado || ""}` : "-"}
                  </td>
                  <td style={{ padding: "16px", fontSize: "14px" }}>
                    <span
                      style={{
                        padding: "4px 10px",
                        fontSize: "12px",
                        fontWeight: "700",
                        borderRadius: "9999px",
                        backgroundColor: "#e0f2fe",
                        color: "#0369a1",
                      }}
                    >
                      {cli.placa || "N/A"}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {isModalOpen && (
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
              maxWidth: "500px",
              padding: "24px",
              boxShadow: "0 20px 25px -5px rgba(0,0,0,0.1)",
            }}
          >
            <h2
              style={{
                fontSize: "20px",
                fontWeight: "700",
                color: "#1e293b",
                marginBottom: "16px",
              }}
            >
              Novo Cliente
            </h2>

            <form
              onSubmit={handleSubmit}
              style={{ display: "flex", flexDirection: "column", gap: "14px" }}
            >
              <div>
                <label
                  style={{
                    display: "block",
                    fontSize: "12px",
                    fontWeight: "700",
                    color: "#475569",
                    marginBottom: "4px",
                    textTransform: "uppercase",
                  }}
                >
                  Nome Completo
                </label>
                <input
                  type="text"
                  required
                  placeholder="Ex: Eduardo Vilches"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  style={{
                    width: "100%",
                    padding: "10px",
                    borderRadius: "6px",
                    border: "1px solid #cbd5e1",
                    fontSize: "14px",
                    boxSizing: "border-box",
                  }}
                />
              </div>

              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "1fr 1fr",
                  gap: "12px",
                }}
              >
                <div>
                  <label
                    style={{
                      display: "block",
                      fontSize: "12px",
                      fontWeight: "700",
                      color: "#475569",
                      marginBottom: "4px",
                      textTransform: "uppercase",
                    }}
                  >
                    Telefone
                  </label>
                  <input
                    type="text"
                    placeholder="(51) 99999-9999"
                    value={telefone}
                    onChange={(e) => setTelefone(e.target.value)}
                    style={{
                      width: "100%",
                      padding: "10px",
                      borderRadius: "6px",
                      border: "1px solid #cbd5e1",
                      fontSize: "14px",
                      boxSizing: "border-box",
                    }}
                  />
                </div>
                <div>
                  <label
                    style={{
                      display: "block",
                      fontSize: "12px",
                      fontWeight: "700",
                      color: "#475569",
                      marginBottom: "4px",
                      textTransform: "uppercase",
                    }}
                  >
                    Placa do Carro
                  </label>
                  <input
                    type="text"
                    required
                    placeholder="Ex: BYY5D73"
                    value={placa}
                    onChange={(e) => setPlaca(e.target.value)}
                    style={{
                      width: "100%",
                      padding: "10px",
                      borderRadius: "6px",
                      border: "1px solid #cbd5e1",
                      fontSize: "14px",
                      boxSizing: "border-box",
                    }}
                  />
                </div>
              </div>

              <div>
                <label
                  style={{
                    display: "block",
                    fontSize: "12px",
                    fontWeight: "700",
                    color: "#475569",
                    marginBottom: "4px",
                    textTransform: "uppercase",
                  }}
                >
                  E-mail
                </label>
                <input
                  type="email"
                  placeholder="cliente@email.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  style={{
                    width: "100%",
                    padding: "10px",
                    borderRadius: "6px",
                    border: "1px solid #cbd5e1",
                    fontSize: "14px",
                    boxSizing: "border-box",
                  }}
                />
              </div>

              <div
                style={{
                  display: "grid",
                  gridTemplateColumns: "2fr 1fr",
                  gap: "12px",
                }}
              >
                <div>
                  <label
                    style={{
                      display: "block",
                      fontSize: "12px",
                      fontWeight: "700",
                      color: "#475569",
                      marginBottom: "4px",
                      textTransform: "uppercase",
                    }}
                  >
                    Cidade
                  </label>
                  <input
                    type="text"
                    placeholder="Porto Alegre"
                    value={cidade}
                    onChange={(e) => setCidade(e.target.value)}
                    style={{
                      width: "100%",
                      padding: "10px",
                      borderRadius: "6px",
                      border: "1px solid #cbd5e1",
                      fontSize: "14px",
                      boxSizing: "border-box",
                    }}
                  />
                </div>
                <div>
                  <label
                    style={{
                      display: "block",
                      fontSize: "12px",
                      fontWeight: "700",
                      color: "#475569",
                      marginBottom: "4px",
                      textTransform: "uppercase",
                    }}
                  >
                    Estado
                  </label>
                  <input
                    type="text"
                    placeholder="RS"
                    value={estado}
                    onChange={(e) => setEstado(e.target.value)}
                    style={{
                      width: "100%",
                      padding: "10px",
                      borderRadius: "6px",
                      border: "1px solid #cbd5e1",
                      fontSize: "14px",
                      boxSizing: "border-box",
                    }}
                  />
                </div>
              </div>

              <div
                style={{
                  display: "flex",
                  justifyContent: "flex-end",
                  gap: "10px",
                  marginTop: "16px",
                }}
              >
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  style={{
                    padding: "10px 16px",
                    borderRadius: "6px",
                    border: "1px solid #cbd5e1",
                    backgroundColor: "#fff",
                    color: "#334155",
                    fontWeight: "600",
                    cursor: "pointer",
                    fontSize: "14px",
                  }}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  style={{
                    padding: "10px 16px",
                    borderRadius: "6px",
                    border: "none",
                    backgroundColor: "#2563eb",
                    color: "#fff",
                    fontWeight: "600",
                    cursor: "pointer",
                    fontSize: "14px",
                  }}
                >
                  Salvar Cliente
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
