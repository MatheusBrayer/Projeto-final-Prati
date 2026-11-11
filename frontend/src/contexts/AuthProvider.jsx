import { realizarLogin } from "../services/authService.js";
import { AuthContext } from "./AuthContext.js";
import { obterExpiracaoToken, tokenExpirado } from "./tokenUtils.js";
import { useEffect, useState } from "react";

const STORAGE_KEY = "autogestao_sessao";

function carregarSessao() {
  try {
    const dados = sessionStorage.getItem(STORAGE_KEY);

    if (!dados) return null;

    const sessao = JSON.parse(dados);

    if (tokenExpirado(sessao?.token)) {
      sessionStorage.removeItem(STORAGE_KEY);
      return null;
    }

    return sessao;
  } catch {
    sessionStorage.removeItem(STORAGE_KEY);
    return null;
  }
}

export function AuthProvider({ children }) {
  const [sessao, setSessao] = useState(carregarSessao);

  async function login(email, senha) {
    const resposta = await realizarLogin(email, senha);

    if (!resposta.token || !resposta.perfil || tokenExpirado(resposta.token)) {
      throw new Error("Resposta de autenticação inválida ou token expirado.");
    }

    const novaSessao = {
      token: resposta.token,
      usuarioId: resposta.usuarioId,
      email: resposta.email,
      perfil: resposta.perfil,
    };

    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(novaSessao));
    setSessao(novaSessao);

    return novaSessao;
  }

  function logout() {
    sessionStorage.removeItem(STORAGE_KEY);
    setSessao(null);
  }

  useEffect(() => {
    if (!sessao?.token) return;

    const expiracao = obterExpiracaoToken(sessao.token);

    function verificarExpiracao() {
      if (expiracao === null || Date.now() >= expiracao) {
        sessionStorage.removeItem(STORAGE_KEY);
        setSessao(null);
        return;
      }

      const tempoRestante = expiracao - Date.now();
      const tempoMaximo = 2147483647;

      timeout = setTimeout(
        verificarExpiracao,
        Math.min(tempoRestante, tempoMaximo),
      );
    }

    let timeout = setTimeout(verificarExpiracao, 0);

    return () => clearTimeout(timeout);
  }, [sessao?.token]);

  useEffect(() => {
    function handleUnauthorized() {
      logout();
    }

    window.addEventListener("auth:unauthorized", handleUnauthorized);

    return () => {
      window.removeEventListener("auth:unauthorized", handleUnauthorized);
    };
  }, []);

  return (
    <AuthContext.Provider
      value={{
        usuario: sessao,
        token: sessao?.token ?? null,
        autenticado: Boolean(sessao?.token) && !tokenExpirado(sessao?.token),
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
