const API_URL = "http://localhost:8080";

export async function solicitarPrimeiroAcesso(email) {
  const response = await fetch(
    `${API_URL}/api/auth/primeiro-acesso/solicitar`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email }),
    },
  );

  if (!response.ok) {
    throw new Error("Não foi possível solicitar o código. Tente novamente.");
  }

  return await response.json();
}

export async function confirmarPrimeiroAcesso(email, codigo, senha) {
  const response = await fetch(
    `${API_URL}/api/auth/primeiro-acesso/confirmar`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email,
        codigo,
        senha,
      }),
    },
  );

  const data = await response.json();

  if (!response.ok) {
    throw new Error(
      data.message ||
        "Não foi possível ativar sua conta. Verifique os dados e tente novamente.",
    );
  }

  return data;
}

export async function realizarLogin(email, senha) {
  const response = await fetch(`${API_URL}/api/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ email, senha }),
  });

  const data = await response.json();

  if (!response.ok) {
    throw new Error(
      data.message ||
        data.mensagem ||
        "Não foi possível realizar o login. Verifique suas credenciais.",
    );
  }

  return data;
}
