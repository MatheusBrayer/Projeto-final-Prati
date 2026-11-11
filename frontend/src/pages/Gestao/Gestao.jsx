import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/useAuth.js";
import { buscarClientePorCpf } from "../../services/clienteService.js";

function Gestao() {
  const navigate = useNavigate();
  const { usuario, logout } = useAuth();

  const [cpf, setCpf] = useState("");
  const [resultado, setResultado] = useState("");
  const [carregando, setCarregando] = useState(false);

  function handleLogout() {
    logout();
    navigate("/login", { replace: true });
  }

  async function handleBuscarCliente(event) {
    event.preventDefault();
    setResultado("");
    setCarregando(true);

    try {
      const cliente = await buscarClientePorCpf(cpf);
      setResultado(
        `Cliente encontrado: ${cliente.nome ?? "Consulta realizada"}`,
      );
    } catch (error) {
      setResultado(error.message);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <main style={{ padding: "30px" }}>
      <h1>Painel de Gestão da Oficina</h1>

      <p>Usuário: {usuario?.email}</p>
      <p>Perfil: {usuario?.perfil}</p>

      <form onSubmit={handleBuscarCliente}>
        <input
          type="text"
          placeholder="CPF do cliente"
          value={cpf}
          onChange={(event) => setCpf(event.target.value)}
          required
        />

        <button type="submit" disabled={carregando}>
          {carregando ? "Buscando..." : "Buscar cliente"}
        </button>
      </form>

      <p>{resultado}</p>

      <button onClick={handleLogout}>Sair</button>
    </main>
  );
}

export default Gestao;
