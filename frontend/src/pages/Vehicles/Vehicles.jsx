import { useEffect, useState } from "react";
import { getVeiculos, getClientes } from "../../services/api";

export default function Vehicles() {
  const [veiculos, setVeiculos] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const [veiculosData, clientesData] = await Promise.all([
          getVeiculos(),
          getClientes(),
        ]);
        setVeiculos(veiculosData);
        setClientes(clientesData);
      } catch (error) {
        console.error("Erro ao carregar dados:", error);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  const getNomeCliente = (clienteId) => {
    const cliente = clientes.find((c) => c.id === clienteId);
    return cliente ? cliente.nome : `Cliente #${clienteId}`;
  };

  return (
    <div className="p-8 max-w-7xl mx-auto">
      {/* Cabeçalho da página */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-gray-800 tracking-tight">
            Gestão de Veículos
          </h1>
          <p className="text-gray-500 text-sm mt-1">
            Veículos cadastrados e vinculados aos clientes na base de dados.
          </p>
        </div>
      </div>

      {/* Tabela com melhor espaçamento */}
      <div className="bg-white shadow-lg rounded-xl overflow-hidden border border-gray-100">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50/75">
            <tr>
              <th className="px-8 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                ID
              </th>
              <th className="px-8 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Placa
              </th>
              <th className="px-8 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Marca
              </th>
              <th className="px-8 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Modelo
              </th>
              <th className="px-8 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Ano
              </th>
              <th className="px-8 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Cliente Proprietário
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {loading ? (
              <tr>
                <td
                  colSpan="6"
                  className="px-8 py-6 text-center text-sm text-gray-500"
                >
                  Carregando veículos...
                </td>
              </tr>
            ) : veiculos.length === 0 ? (
              <tr>
                <td
                  colSpan="6"
                  className="px-8 py-6 text-center text-sm text-gray-500"
                >
                  Nenhum veículo cadastrado.
                </td>
              </tr>
            ) : (
              veiculos.map((veiculo) => (
                <tr
                  key={veiculo.id}
                  className="hover:bg-gray-50/50 transition-colors"
                >
                  <td className="px-8 py-5 whitespace-nowrap text-sm font-medium text-gray-900">
                    {veiculo.id}
                  </td>
                  <td className="px-8 py-5 whitespace-nowrap text-sm font-semibold text-gray-800">
                    <span className="bg-gray-100 px-2.5 py-1 rounded-md border border-gray-200">
                      {veiculo.placa}
                    </span>
                  </td>
                  <td className="px-8 py-5 whitespace-nowrap text-sm text-gray-600">
                    {veiculo.marca}
                  </td>
                  <td className="px-8 py-5 whitespace-nowrap text-sm text-gray-600">
                    {veiculo.modelo}
                  </td>
                  <td className="px-8 py-5 whitespace-nowrap text-sm text-gray-600">
                    {veiculo.ano}
                  </td>
                  <td className="px-8 py-5 whitespace-nowrap text-sm font-medium text-gray-900">
                    {getNomeCliente(veiculo.clienteId)}
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
