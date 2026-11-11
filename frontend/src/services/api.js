const API_URL = "http://localhost:8080";
const STORAGE_KEY = "autogestao_sessao";

export async function apiFetch(endpoint, options = {}) {
  const dados = sessionStorage.getItem(STORAGE_KEY);

  let token = null;

  if (dados) {
    try {
      const sessao = JSON.parse(dados);
      token = sessao.token;
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
