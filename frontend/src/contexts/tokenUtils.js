export function obterExpiracaoToken(token) {
  try {
    if (!token) return null;

    const partes = token.split(".");

    if (partes.length !== 3) return null;

    const payloadBase64 = partes[1].replace(/-/g, "+").replace(/_/g, "/");

    const payload = JSON.parse(atob(payloadBase64));

    if (typeof payload.exp !== "number" || !Number.isFinite(payload.exp)) {
      return null;
    }

    return payload.exp * 1000;
  } catch {
    return null;
  }
}

export function tokenExpirado(token) {
  const expiracao = obterExpiracaoToken(token);

  return expiracao === null || Date.now() >= expiracao;
}
