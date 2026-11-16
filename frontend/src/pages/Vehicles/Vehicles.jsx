import { useEffect, useState } from "react";
import { getClientes } from "../../services/api";

export default function Vehicles() {
  const [veiculos, setVeiculos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let ativo = true;

    async function fetchData() {
      try {
        const clientesData = await getClientes();
        if (ativo) {
          const lista = clientesData || [];
          const comVeiculo = lista.filter(
            (cli) => cli.placa_carro || cli.placa || cli.modelo_veiculo || cli.modelo || cli.veiculo
          );
          setVeiculos(comVeiculo);
        }
      } catch (error) {
        console.error("Erro ao carregar veículos:", error);
      } finally {
        if (ativo) {
          setLoading(false);
        }
      }
    }

    fetchData();

    return () => {
      ativo = false;
    };
  }, []);

  return (
    <div style={{ padding: "32px", maxWidth: "1200px", margin: "0 auto" }}>
      <div style={{ marginBottom: "24px" }}>
        <h1 style={{ fontSize: "26px", fontWeight: "700", color: "#1e293b", margin: 0 }}>
          Gestão de Veículos
        </h1>
        <p style={{ fontSize: "14px", color: "#64748b", marginTop: "4px" }}>
          Veículos cadastrados e vinculados aos clientes na base de dados.
        </p>
      </div>

      <div style={{ backgroundColor: "#ffffff", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.1)", borderRadius: "12px", overflow: "hidden", border: "1px solid #e2e8f0" }}>
        <table style={{ width: "100%", borderCollapse: "collapse", textAlign: "left" }}>
          <thead>
            <tr style={{ backgroundColor: "#f8fafc", borderBottom: "1px solid #e2e8f0" }}>
              <th style={{ padding: "16px", fontSize: "12px", fontWeight: "600", color: "#64748b", textTransform: "uppercase" }}>ID Cliente</th>
              <th style={{ padding: "16px", fontSize: "12px", fontWeight: "600", color: "#64748b", textTransform: "uppercase" }}>Placa</th>
              <th style={{ padding: "16px", fontSize: "12px", fontWeight: "600", color: "#64748b", textTransform: "uppercase" }}>Modelo do Veículo</th>
              <th style={{ padding: "16px", fontSize: "12px", fontWeight: "600", color: "#64748b", textTransform: "uppercase" }}>Telefone</th>
              <th style={{ padding: "16px", fontSize: "12px", fontWeight: "600", color: "#64748b", textTransform: "uppercase" }}>Cliente Proprietário</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="5" style={{ padding: "24px", textAlign: "center", color: "#64748b", fontSize: "14px" }}>
                  Carregando veículos...
                </td>
              </tr>
            ) : veiculos.length === 0 ? (
              <tr>
                <td colSpan="5" style={{ padding: "24px", textAlign: "center", color: "#64748b", fontSize: "14px" }}>
                  Nenhum veículo cadastrado. Cadastre um cliente com placa e modelo na aba Clientes.
                </td>
              </tr>
            ) : (
              veiculos.map((veiculo) => (
                <tr key={veiculo.id} style={{ borderBottom: "1px solid #e2e8f0" }}>
                  <td style={{ padding: "16px", fontSize: "14px", fontWeight: "700", color: "#1e293b" }}>
                    {String(veiculo.id).padStart(3, "0")}
                  </td>
                  <td style={{ padding: "16px", fontSize: "14px" }}>
                    <span style={{ padding: "4px 10px", fontSize: "12px", fontWeight: "700", borderRadius: "9999px", backgroundColor: "#e0f2fe", color: "#0369a1" }}>
                      {veiculo.placa || veiculo.placa_carro || veiculo.veiculo?.placa || "N/A"}
                    </span>
                  </td>
                  <td style={{ padding: "16px", fontSize: "14px", fontWeight: "600", color: "#334155" }}>
                    {veiculo.modeloVeiculo || veiculo.modelo_veiculo || veiculo.modelo || veiculo.veiculo?.modelo || "Não informado"}
                  </td>
                  <td style={{ padding: "16px", fontSize: "14px", color: "#64748b" }}>
                    {veiculo.telefone || "-"}
                  </td>
                  <td style={{ padding: "16px", fontSize: "14px", fontWeight: "600", color: "#334155" }}>
                    {veiculo.nome || "-"}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}