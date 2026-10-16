
package com.prati.backend.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorTest {

    @Test
    void deveRetornarTrueQuandoCpfForValido() {
        assertTrue(CpfValidator.isValid("52998224725"));
    }

    @Test
    void deveRetornarFalseQuandoCpfForInvalido() {
        assertFalse(CpfValidator.isValid("12345678900"));
    }

    @Test
    void deveRetornarFalseQuandoCpfTiverTodosOsDigitosIguais() {
        assertFalse(CpfValidator.isValid("11111111111"));
    }

    @Test
    void deveRetornarFalseQuandoCpfTiverMenosDeOnzeDigitos() {
        assertFalse(CpfValidator.isValid("123456789"));
    }

    @Test
    void deveRetornarFalseQuandoCpfForNulo() {
        assertFalse(CpfValidator.isValid(null));
    }
}
