import { apiFetch } from "./api.js";

export async function buscarClientePorCpf(cpf) {
  const response = await apiFetch(
    `/api/clientes/buscar-por-cpf/${encodeURIComponent(cpf)}`,
  );

  if (!response.ok) {
    throw new Error(`Erro ao buscar cliente. Status: ${response.status}`);
  }

  return response.json();
}
