import { useEffect, useState } from "react";
import { getOrdensServico, createOrdemServico } from "../../services/api";

export default function Orders() {
  const [ordens, setOrdens] = useState([]);
  const [loading, setLoading] = useState(true);

  // Estados para controlar o Modal de Nova OS
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [veiculo, setVeiculo] = useState("");
  const [cliente, setCliente] = useState("");
  const [problema, setProblema] = useState("");
  const [status, setStatus] = useState("EM ANDAMENTO");
  const [valorTotal, setValorTotal] = useState("");

  // Função para buscar as ordens do backend
  async function fetchOrdens() {
    try {
      const data = await getOrdensServico();
      setOrdens(data);
    } catch (error) {
      console.error("Erro ao carregar ordens de serviço:", error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    let ativo = true;

    async function carregarOrdens() {
      try {
        const data = await getOrdensServico();

        if (ativo) {
          setOrdens(data);
        }
      } catch (error) {
        console.error("Erro ao carregar ordens de serviço:", error);
      } finally {
        if (ativo) {
          setLoading(false);
        }
      }
    }

    carregarOrdens();

    return () => {
      ativo = false;
    };
  }, []);

  // Função para salvar a nova OS
  async function handleSubmit(e) {
    e.preventDefault();
    try {
      const novaOrdem = {
        veiculo,
        cliente,
        problema,
        status,
        valorTotal: parseFloat(valorTotal) || 0,
      };

      await createOrdemServico(novaOrdem);

      // Fecha o modal, limpa os campos e recarrega a lista
      setIsModalOpen(false);
      setVeiculo("");
      setCliente("");
      setProblema("");
      setStatus("EM ANDAMENTO");
      setValorTotal("");

      fetchOrdens();
    } catch (error) {
      console.error("Erro ao criar ordem de serviço:", error);
      alert("Erro ao criar ordem de serviço.");
    }
  }

  return (
    <div className="p-8 max-w-7xl mx-auto">
      {/* Cabeçalho da página */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-gray-800 tracking-tight">
            Ordens de Serviço
          </h1>
          <p className="text-gray-500 text-sm mt-1">
            Acompanhe o andamento dos serviços e manutenções na oficina.
          </p>
        </div>
        <button
          onClick={() => setIsModalOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2.5 rounded-lg shadow transition-colors flex items-center gap-2 text-sm"
        >
          + Nova Ordem de Serviço
        </button>
      </div>

      {/* Tabela de Ordens */}
      <div className="bg-white shadow-lg rounded-xl overflow-hidden border border-gray-100">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50/75">
            <tr>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                OS #
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Veículo / Cliente
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Problema Relatado
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Valor Total
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {loading ? (
              <tr>
                <td
                  colSpan="5"
                  className="px-6 py-6 text-center text-sm text-gray-500"
                >
                  Carregando ordens de serviço...
                </td>
              </tr>
            ) : ordens.length === 0 ? (
              <tr>
                <td
                  colSpan="5"
                  className="px-6 py-6 text-center text-sm text-gray-500"
                >
                  Nenhuma ordem de serviço cadastrada.
                </td>
              </tr>
            ) : (
              ordens.map((os) => (
                <tr
                  key={os.id}
                  className="hover:bg-gray-50/50 transition-colors"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-bold text-gray-900">
                    {String(os.id).padStart(3, "0")}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-800">
                    <div className="font-semibold">{os.veiculo}</div>
                    <div className="text-gray-500 text-xs">{os.cliente}</div>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600 max-w-xs truncate">
                    {os.problema}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <span
                      className={`px-2.5 py-1 text-xs font-semibold rounded-full ${
                        os.status === "FINALIZADA"
                          ? "bg-green-100 text-green-800"
                          : "bg-yellow-100 text-yellow-800"
                      }`}
                    >
                      {os.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-bold text-gray-900">
                    R$ {Number(os.valorTotal).toFixed(2)}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Modal de Cadastro de Nova OS */}
      {isModalOpen && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl shadow-2xl max-w-lg w-full p-6 relative">
            <h2 className="text-xl font-bold text-gray-800 mb-4">
              Nova Ordem de Serviço
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                  Veículo (Modelo e Placa)
                </label>
                <input
                  type="text"
                  required
                  placeholder="Ex: Ford Fiesta (EMM2B24)"
                  value={veiculo}
                  onChange={(e) => setVeiculo(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                  Cliente Proprietário
                </label>
                <input
                  type="text"
                  required
                  placeholder="Ex: Jeferson Oliveira Forte"
                  value={cliente}
                  onChange={(e) => setCliente(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                  Problema Relatado
                </label>
                <textarea
                  required
                  rows="3"
                  placeholder="Descreva o problema ou serviço solicitado..."
                  value={problema}
                  onChange={(e) => setProblema(e.target.value)}
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                    Status
                  </label>
                  <select
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="EM ANDAMENTO">EM ANDAMENTO</option>
                    <option value="FINALIZADA">FINALIZADA</option>
                    <option value="PENDENTE">PENDENTE</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-gray-600 uppercase mb-1">
                    Valor Total (R$)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    placeholder="0.00"
                    value={valorTotal}
                    onChange={(e) => setValorTotal(e.target.value)}
                    className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-3 mt-6">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 border border-gray-300 rounded-lg text-sm font-semibold text-gray-700 hover:bg-gray-50 transition-colors"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm font-semibold transition-colors shadow"
                >
                  Salvar OS
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
