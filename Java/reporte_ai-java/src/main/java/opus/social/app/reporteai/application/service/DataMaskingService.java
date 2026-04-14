package opus.social.app.reporteai.application.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Serviço para mascarar dados sensíveis em logs e respostas
 * Implementa GDPR/LGPD compliance para proteção de dados pessoais
 */
@Service
public class DataMaskingService {

    // Padrões regex para detecção de dados sensíveis
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "([a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "(\\+?[0-9]{10,15})"
    );

    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile(
        "\\b(?:\\d{4}[\\s-]?){3}\\d{4}\\b"
    );

    private static final Pattern CPF_PATTERN = Pattern.compile(
        "(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})"
    );

    private static final Pattern JWT_PATTERN = Pattern.compile(
        "Bearer\\s+([A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+)"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "password[\\s]*:[\\s]*[\"']([^\"']+)[\"']",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern API_KEY_PATTERN = Pattern.compile(
        "api[_-]?key[\\s]*[:=][\\s]*[\"']?([^\"'\\s,}]+)[\"']?",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Mascara email, deixando visível apenas o domínio
     * Exemplo: user@example.com -> u***@example.com
     */
    public String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email;
        }

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (localPart.length() <= 1) {
            return "*" + domain;
        }

        String masked = localPart.charAt(0) + "*".repeat(localPart.length() - 1) + domain;
        return masked;
    }

    /**
     * Mascara número de telefone
     * Exemplo: +55 11 98765-4321 -> +55 11 9****-****
     */
    public String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }

        return phone.substring(0, Math.min(4, phone.length())) +
               "*".repeat(Math.max(0, phone.length() - 4));
    }

    /**
     * Mascara número de cartão de crédito
     * Exemplo: 1234 5678 9012 3456 -> **** **** **** 3456
     */
    public String maskCreditCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }

        String cleaned = cardNumber.replaceAll("\\s+", "");
        if (cleaned.length() < 4) {
            return "****";
        }

        String lastFour = cleaned.substring(cleaned.length() - 4);
        return "**** **** **** " + lastFour;
    }

    /**
     * Mascara CPF/CNPJ
     * Exemplo: 123.456.789-00 -> ***.***.***-**
     */
    public String maskCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.length() < 4) {
            return "****";
        }

        return "*".repeat(cpfCnpj.length() - 4) + cpfCnpj.substring(cpfCnpj.length() - 4);
    }

    /**
     * Mascara JWT Token
     * Exemplo: eyJhbGc... -> eyJ***...***
     */
    public String maskJwtToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }

        return token.substring(0, 4) + "***" +
               token.substring(Math.max(0, token.length() - 4));
    }

    /**
     * Mascara chave API
     * Exemplo: sk_live_abc123def456 -> sk_live_***
     */
    public String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 4) {
            return "***";
        }

        int visibleChars = Math.min(10, apiKey.length() / 3);
        return apiKey.substring(0, visibleChars) + "***";
    }

    /**
     * Mascara URL contendo credenciais
     * Exemplo: https://user:pass@example.com -> https://***:***@example.com
     */
    public String maskUrl(String url) {
        if (url == null) {
            return null;
        }

        // Mascara credenciais em URLs
        return url.replaceAll("://[^:/@]+:[^/@]+@", "://***:***@");
    }

    /**
     * Remove/mascara todos os dados sensíveis de uma string
     * Aplica todos os padrões de mascaramento
     */
    public String maskAllSensitiveData(String text) {
        if (text == null) {
            return null;
        }

        // Mascara emails
        text = EMAIL_PATTERN.matcher(text).replaceAll(match ->
            maskEmail(match.group(1))
        );

        // Mascara telefones
        text = PHONE_PATTERN.matcher(text).replaceAll(match ->
            maskPhoneNumber(match.group(1))
        );

        // Mascara cartões de crédito
        text = CREDIT_CARD_PATTERN.matcher(text).replaceAll(match ->
            maskCreditCard(match.group(0))
        );

        // Mascara CPF/CNPJ
        text = CPF_PATTERN.matcher(text).replaceAll(match ->
            maskCpfCnpj(match.group(1))
        );

        // Mascara JWT tokens
        text = JWT_PATTERN.matcher(text).replaceAll(match ->
            "Bearer " + maskJwtToken(match.group(1))
        );

        // Mascara senhas em strings JSON
        text = PASSWORD_PATTERN.matcher(text).replaceAll("password:\"***\"");

        // Mascara API keys
        text = API_KEY_PATTERN.matcher(text).replaceAll(match ->
            "api_key=" + maskApiKey(match.group(1))
        );

        return text;
    }

    /**
     * Mascara informações de usuário para logging
     */
    public String maskUserInfo(String username, String email) {
        return String.format(
            "username=%s | email=%s",
            maskUsername(username),
            maskEmail(email)
        );
    }

    /**
     * Mascara username
     * Exemplo: joao.silva -> j***a
     */
    public String maskUsername(String username) {
        if (username == null || username.length() < 2) {
            return "***";
        }

        return username.charAt(0) +
               "*".repeat(Math.max(0, username.length() - 2)) +
               username.charAt(username.length() - 1);
    }

    /**
     * Verifica se um texto contém dados sensíveis
     */
    public boolean containsSensitiveData(String text) {
        if (text == null) {
            return false;
        }

        return EMAIL_PATTERN.matcher(text).find() ||
               PHONE_PATTERN.matcher(text).find() ||
               CREDIT_CARD_PATTERN.matcher(text).find() ||
               CPF_PATTERN.matcher(text).find() ||
               JWT_PATTERN.matcher(text).find() ||
               PASSWORD_PATTERN.matcher(text).find() ||
               API_KEY_PATTERN.matcher(text).find();
    }
}
