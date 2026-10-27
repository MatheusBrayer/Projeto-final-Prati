export const WHATSAPP_DISPLAY = "+55 (51) 98352-4813";
const WHATSAPP_DIGITS = "5551983524813";
export const HIRE_MESSAGE =
  "Olá! Vim pelo site da Torque Auto e quero contratar um plano de manutenção.";

export function whatsappUrl(message) {
  return `https://wa.me/${WHATSAPP_DIGITS}?text=${encodeURIComponent(message)}`;
}

export function quoteMessage({ name, vehicle, problem }) {
  return [
    "Olá! Vim pelo site da Torque Auto e quero um orçamento.",
    "",
    `Nome: ${name}`,
    `Veículo: ${vehicle}`,
    `Serviço ou problema: ${problem}`,
  ].join("\n");
}
