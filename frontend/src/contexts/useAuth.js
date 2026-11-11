import { useContext } from "react";
import { AuthContext } from "./AuthContext.js";

export function useAuth() {
  const contexto = useContext(AuthContext);

  if (!contexto) {
    throw new Error("useAuth deve ser utilizado dentro do AuthProvider.");
  }

  return contexto;
}
