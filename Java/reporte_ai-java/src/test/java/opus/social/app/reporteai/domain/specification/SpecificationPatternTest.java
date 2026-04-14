package opus.social.app.reporteai.domain.specification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Specification Pattern Testes Unitários
 *
 * Valida:
 * - Validação de senha forte
 * - Validação de email
 * - Validação de username único
 * - Composição de especificações (AND, OR, NOT)
 * - Reutilização de regras de validação
 */
@DisplayName("Specification Pattern Tests")
class SpecificationPatternTest {

    @Test
    @DisplayName("StrongPasswordSpecification - Deve aceitar senha forte")
    void testStrongPasswordValid() {
        StrongPasswordSpecification spec = new StrongPasswordSpecification();
        assertTrue(spec.isSatisfiedBy("StrongPass123!@"));
        assertTrue(spec.isSatisfiedBy("MySecurePassword2024!"));
    }

    @Test
    @DisplayName("StrongPasswordSpecification - Deve rejeitar senha muito curta")
    void testStrongPasswordTooShort() {
        StrongPasswordSpecification spec = new StrongPasswordSpecification();
        assertFalse(spec.isSatisfiedBy("Short1!"));
        assertFalse(spec.isSatisfiedBy(""));
    }

    @Test
    @DisplayName("StrongPasswordSpecification - Deve rejeitar senha sem números")
    void testStrongPasswordNoNumbers() {
        StrongPasswordSpecification spec = new StrongPasswordSpecification();
        assertFalse(spec.isSatisfiedBy("NoNumbersPass!@"));
    }

    @Test
    @DisplayName("StrongPasswordSpecification - Deve rejeitar senha sem maiúsculas")
    void testStrongPasswordNoUppercase() {
        StrongPasswordSpecification spec = new StrongPasswordSpecification();
        assertFalse(spec.isSatisfiedBy("nouppercase123!"));
    }

    @Test
    @DisplayName("StrongPasswordSpecification - Deve rejeitar senha sem minúsculas")
    void testStrongPasswordNoLowercase() {
        StrongPasswordSpecification spec = new StrongPasswordSpecification();
        assertFalse(spec.isSatisfiedBy("NOLOWERCASE123!"));
    }

    @Test
    @DisplayName("StrongPasswordSpecification - Deve rejeitar senha sem caracteres especiais")
    void testStrongPasswordNoSpecialChars() {
        StrongPasswordSpecification spec = new StrongPasswordSpecification();
        assertFalse(spec.isSatisfiedBy("NoSpecialChars123"));
    }

    @Test
    @DisplayName("ValidEmailSpecification - Deve aceitar email válido")
    void testValidEmailCorrect() {
        ValidEmailSpecification spec = new ValidEmailSpecification();
        assertTrue(spec.isSatisfiedBy("user@example.com"));
        assertTrue(spec.isSatisfiedBy("test.user+tag@domain.co.uk"));
    }

    @Test
    @DisplayName("ValidEmailSpecification - Deve rejeitar email inválido")
    void testValidEmailInvalid() {
        ValidEmailSpecification spec = new ValidEmailSpecification();
        assertFalse(spec.isSatisfiedBy("invalid.email"));
        assertFalse(spec.isSatisfiedBy("@example.com"));
        assertFalse(spec.isSatisfiedBy("user@"));
    }

    @Test
    @DisplayName("ValidEmailSpecification - Deve rejeitar email null")
    void testValidEmailNull() {
        ValidEmailSpecification spec = new ValidEmailSpecification();
        assertFalse(spec.isSatisfiedBy(null));
    }

    @Test
    @DisplayName("Composição AND - Deve retornar true quando ambas especificações são satisfeitas")
    void testCompositionAndBothSatisfied() {
        Specification<String> strongPassword = new StrongPasswordSpecification();
        Specification<String> composedSpec = strongPassword.and(strongPassword);

        assertTrue(composedSpec.isSatisfiedBy("StrongPass123!@"));
    }

    @Test
    @DisplayName("Composição AND - Deve retornar false quando uma especificação não é satisfeita")
    void testCompositionAndOneFails() {
        Specification<String> strongPassword = new StrongPasswordSpecification();
        Specification<String> validEmail = new ValidEmailSpecification();
        Specification<String> composedSpec = strongPassword.and(validEmail);

        assertFalse(composedSpec.isSatisfiedBy("StrongPass123!@"));  // Password OK, mas é senha não email
    }

    @Test
    @DisplayName("Composição OR - Deve retornar true quando uma especificação é satisfeita")
    void testCompositionOrOneSuccess() {
        Specification<String> spec1 = new StrongPasswordSpecification();
        Specification<String> spec2 = new ValidEmailSpecification();
        Specification<String> composedSpec = spec1.or(spec2);

        assertTrue(composedSpec.isSatisfiedBy("user@example.com"));
        assertTrue(composedSpec.isSatisfiedBy("StrongPass123!@"));
    }

    @Test
    @DisplayName("Composição OR - Deve retornar false quando nenhuma especificação é satisfeita")
    void testCompositionOrBothFail() {
        Specification<String> spec1 = new StrongPasswordSpecification();
        Specification<String> spec2 = new ValidEmailSpecification();
        Specification<String> composedSpec = spec1.or(spec2);

        assertFalse(composedSpec.isSatisfiedBy("weak"));
    }

    @Test
    @DisplayName("Composição NOT - Deve inverter resultado da especificação")
    void testCompositionNot() {
        Specification<String> strongPassword = new StrongPasswordSpecification();
        Specification<String> notStrongPassword = strongPassword.not();

        assertFalse(notStrongPassword.isSatisfiedBy("StrongPass123!@"));
        assertTrue(notStrongPassword.isSatisfiedBy("weak"));
    }

    @Test
    @DisplayName("Composição complexa - AND com OR")
    void testComplexComposition() {
        Specification<String> strongPassword = new StrongPasswordSpecification();
        Specification<String> validEmail = new ValidEmailSpecification();

        // (StrongPassword OR ValidEmail) AND StrongPassword
        Specification<String> complex = strongPassword.or(validEmail).and(strongPassword);

        assertTrue(complex.isSatisfiedBy("StrongPass123!@"));
        assertFalse(complex.isSatisfiedBy("user@example.com"));
    }

    @Test
    @DisplayName("Especificação com getDescription")
    void testSpecificationDescription() {
        StrongPasswordSpecification spec = new StrongPasswordSpecification();
        assertNotNull(spec.getDescription());
        assertTrue(spec.getDescription().length() > 0);
    }
}
