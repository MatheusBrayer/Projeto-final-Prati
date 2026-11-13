import { useSearchParams } from "react-router-dom";

function Cliente() {
  const [searchParams] = useSearchParams();
  const slugOficina = searchParams.get("oficina");

  return (
    <main>
      <h1>Portal do Cliente</h1>

      {slugOficina ? (
        <p>Oficina selecionada: {slugOficina}</p>
      ) : (
        <p>Nenhuma oficina selecionada.</p>
      )}
    </main>
  );
}

export default Cliente;
