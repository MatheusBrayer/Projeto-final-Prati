const API_URL = "http://localhost:8080";
const STORAGE_KEY = "autogestao_sessao";

// Função central para requisições autenticadas
export async function apiFetch(endpoint, options = {}) {
  const dados = sessionStorage.getItem(STORAGE_KEY);
  let token = null;

  if (dados) {
    try {
      token = JSON.parse(dados).token;
    } catch {
      sessionStorage.removeItem(STORAGE_KEY);
    }
  }

  const headers = new Headers(options.headers);

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  if (options.body && !(options.body instanceof FormData)) {
    if (!headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }
  }

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 401 && token) {
    sessionStorage.removeItem(STORAGE_KEY);
    window.dispatchEvent(new Event("auth:unauthorized"));
  }

  return response;
}

// CLIENTES

export async function getClientes() {
  const response = await apiFetch("/api/clientes");

  if (!response.ok) {
    throw new Error("Erro ao buscar clientes");
  }

  return response.json();
}

export async function createCliente(cliente) {
  const response = await apiFetch("/api/clientes", {
    method: "POST",
    body: JSON.stringify(cliente),
  });

  if (!response.ok) {
    throw new Error("Erro ao criar cliente");
  }

  return response.json();
}

// VEÍCULOS

export async function getVeiculos() {
  const response = await apiFetch("/api/veiculos");

  if (!response.ok) {
    throw new Error("Erro ao buscar veículos");
  }

  return response.json();
}

// ORDENS DE SERVIÇO

export async function getOrdensServico() {
  const response = await apiFetch("/api/ordens");

  if (!response.ok) {
    throw new Error("Erro ao buscar ordens de serviço");
  }

  return response.json();
}

export async function createOrdemServico(ordem) {
  const response = await apiFetch("/api/ordens", {
    method: "POST",
    body: JSON.stringify(ordem),
  });

  if (!response.ok) {
    throw new Error("Erro ao criar ordem de serviço");
  }

  return response.json();
}

export async function finalizarOrdemServico(id) {
  const response = await apiFetch(`/api/ordens/${id}/finalizar`, {
    method: "PUT",
  });

  if (!response.ok) {
    throw new Error("Erro ao finalizar ordem de serviço");
  }

  return response.json();
}
