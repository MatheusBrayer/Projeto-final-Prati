
package com.prati.backend.validation;

public final class CpfValidator {

    private CpfValidator() {
    }

    public static boolean isValid(String cpf) {

        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }

        // Rejeita CPFs com todos os dígitos iguais.
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        int primeiroDigito = calcularDigito(cpf, 9);
        int segundoDigito = calcularDigito(cpf, 10);

        return primeiroDigito == Character.getNumericValue(cpf.charAt(9))
                && segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    private static int calcularDigito(String cpf, int quantidadeDigitos) {

        int soma = 0;

        for (int i = 0; i < quantidadeDigitos; i++) {
            soma += Character.getNumericValue(cpf.charAt(i))
                    * (quantidadeDigitos + 1 - i);
        }

        int resto = soma % 11;

        return resto < 2 ? 0 : 11 - resto;
    }
}