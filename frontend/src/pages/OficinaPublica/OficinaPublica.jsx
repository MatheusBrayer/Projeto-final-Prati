import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

export default function OficinaPublica() {
  const { slugOficina } = useParams();

  const [oficina, setOficina] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  useEffect(() => {
    let ativo = true;

    async function buscarOficina() {
      setCarregando(true);
      setErro("");
      setOficina(null);

      try {
        const response = await fetch(
          `http://localhost:8080/api/oficinas/slug/${encodeURIComponent(slugOficina)}`,
        );

        if (!response.ok) {
          throw new Error("Oficina não encontrada.");
        }

        const dados = await response.json();

        if (ativo) {
          setOficina(dados);
        }
      } catch (error) {
        if (ativo) {
          setErro(error.message);
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    }

    buscarOficina();

    return () => {
      ativo = false;
    };
  }, [slugOficina]);

  if (carregando) {
    return <p>Carregando oficina...</p>;
  }

  if (erro) {
    return <p>{erro}</p>;
  }

  return (
    <main style={{ padding: "40px", textAlign: "center" }}>
      <h1>{oficina.nome}</h1>

      <p>Bem-vindo ao portal da oficina!</p>

      <p>Entre na sua conta para acompanhar seus serviços.</p>

      <Link to={`/login?oficina=${encodeURIComponent(slugOficina)}`}>
        Entrar
      </Link>

      <br />

      <Link to={`/primeiro-acesso?oficina=${encodeURIComponent(slugOficina)}`}>
        Primeiro acesso
      </Link>
    </main>
  );
}
