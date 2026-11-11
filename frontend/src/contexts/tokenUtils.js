export function tokenExpirado(token) {
  try {
    if (!token) return true;

    const partes = token.split(".");
    if (partes.length !== 3) return true;

    const payloadBase64 = partes[1].replace(/-/g, "+").replace(/_/g, "/");

    const payload = JSON.parse(atob(payloadBase64));

    if (typeof payload.exp !== "number") {
      return true;
    }

    return Date.now() >= payload.exp * 1000;
  } catch {
    return true;
  }
}
