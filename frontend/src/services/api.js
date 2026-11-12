const API_URL = 'http://localhost:8080';

// --- CLIENTES ---
export async function getClientes() {
  try {
    const response = await fetch(`${API_URL}/api/clientes`);
    if (!response.ok) throw new Error('Erro ao buscar clientes');
    return await response.json();
  } catch (error) {
    console.error('Erro na API de Clientes:', error);
    throw error;
  }
}

export async function createCliente(cliente) {
  try {
    const response = await fetch(`${API_URL}/api/clientes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cliente),
    });
    if (!response.ok) throw new Error('Erro ao criar cliente');
    return await response.json();
  } catch (error) {
    console.error('Erro ao salvar cliente:', error);
    throw error;
  }
}

// --- VEÍCULOS ---
export async function getVeiculos() {
  try {
    const response = await fetch(`${API_URL}/api/veiculos`);
    if (!response.ok) throw new Error('Erro ao buscar veículos');
    return await response.json();
  } catch (error) {
    console.error('Erro na API de Veículos:', error);
    throw error;
  }
}

// --- ORDENS DE SERVIÇO ---
export async function getOrdensServico() {
  try {
    const response = await fetch(`${API_URL}/api/ordens`);
    if (!response.ok) throw new Error('Erro ao buscar ordens de serviço');
    return await response.json();
  } catch (error) {
    console.error('Erro na API de Ordens:', error);
    throw error;
  }
}

export async function createOrdemServico(ordem) {
  try {
    const response = await fetch(`${API_URL}/api/ordens`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(ordem),
    });
    if (!response.ok) throw new Error('Erro ao criar ordem de serviço');
    return await response.json();
  } catch (error) {
    console.error('Erro ao salvar OS:', error);
    throw error;
  }
}

export async function finalizarOrdemServico(id) {
  try {
    const response = await fetch(`${API_URL}/api/ordens/${id}/finalizar`, {
      method: 'PUT',
    });
    if (!response.ok) throw new Error('Erro ao finalizar ordem de serviço');
    return await response.json();
  } catch (error) {
    console.error('Erro ao finalizar OS:', error);
    throw error;
  }
}