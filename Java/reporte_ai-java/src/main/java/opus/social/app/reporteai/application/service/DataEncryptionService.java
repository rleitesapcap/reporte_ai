package opus.social.app.reporteai.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Serviço para criptografia de dados sensíveis em repouso
 * Utiliza AES-256-GCM para maior segurança
 * Implementa autenticação de dados para detectar tampering
 */
@Service
public class DataEncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(DataEncryptionService.class);

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int ENCRYPTION_BUFFER_SIZE = 1024;

    @Value("${app.encryption.key:${JWT_SECRET:mySecureSecretKeyForEncryptionAndDecryption1234567890}}")
    private String encryptionKey;

    private SecretKey secretKey;

    /**
     * Inicializa a chave de criptografia
     */
    private SecretKey getSecretKey() {
        if (secretKey == null) {
            // Gera uma chave derivada a partir do encryption key
            byte[] decodedKey = encryptionKey.getBytes();
            // Garante que a chave tenha 32 bytes (256 bits)
            byte[] keyBytes = new byte[32];
            System.arraycopy(decodedKey, 0, keyBytes, 0, Math.min(decodedKey.length, 32));
            secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, ALGORITHM);
        }
        return secretKey;
    }

    /**
     * Criptografa uma string
     * Retorna uma string Base64 contendo: IV + Ciphertext + AuthTag
     */
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            SecureRandom random = new SecureRandom();

            // Gera IV aleatório
            byte[] iv = new byte[GCM_IV_LENGTH];
            random.nextBytes(iv);

            // Inicializa cipher em modo de criptografia
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), spec);

            // Criptografa os dados
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

            // Combina IV + Ciphertext
            byte[] encryptedData = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(ciphertext, 0, encryptedData, iv.length, ciphertext.length);

            // Codifica em Base64 para armazenamento
            return Base64.getEncoder().encodeToString(encryptedData);

        } catch (Exception ex) {
            logger.error("Erro ao criptografar dados", ex);
            throw new RuntimeException("Falha ao criptografar dados", ex);
        }
    }

    /**
     * Descriptografa uma string
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }

        try {
            // Decodifica de Base64
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);

            if (decodedData.length < GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Dados criptografados inválidos");
            }

            // Extrai IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(decodedData, 0, iv, 0, GCM_IV_LENGTH);

            // Extrai Ciphertext
            byte[] ciphertext = new byte[decodedData.length - GCM_IV_LENGTH];
            System.arraycopy(decodedData, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);

            // Inicializa cipher em modo de descriptografia
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);

            // Descriptografa
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext);

        } catch (Exception ex) {
            logger.error("Erro ao descriptografar dados", ex);
            throw new RuntimeException("Falha ao descriptografar dados", ex);
        }
    }

    /**
     * Criptografa um valor de campo sensível (ex: número de telefone, endereço)
     */
    public String encryptField(String fieldValue) {
        return encrypt(fieldValue);
    }

    /**
     * Descriptografa um valor de campo sensível
     */
    public String decryptField(String encryptedValue) {
        return decrypt(encryptedValue);
    }

    /**
     * Gera um hash criptográfico para armazenar de forma irreversível
     * Útil para dados que nunca precisam ser recuperados
     */
    public String hashSensitiveData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            logger.error("Erro ao fazer hash de dados sensíveis", ex);
            throw new RuntimeException("Falha ao fazer hash de dados", ex);
        }
    }

    /**
     * Verifica a integridade de dados criptografados
     */
    public boolean verifyEncryptedData(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return true;
        }

        try {
            decrypt(encryptedData);
            return true;
        } catch (Exception ex) {
            logger.warn("Falha na verificação de dados criptografados: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Criptografa um conjunto de dados (ex: JSON)
     */
    public String encryptJson(String jsonData) {
        return encrypt(jsonData);
    }

    /**
     * Descriptografa um conjunto de dados
     */
    public String decryptJson(String encryptedData) {
        return decrypt(encryptedData);
    }

    /**
     * Verifica se a chave de criptografia está configurada corretamente
     */
    public boolean isKeyConfigured() {
        try {
            SecretKey key = getSecretKey();
            return key != null && key.getEncoded().length == 32;
        } catch (Exception ex) {
            logger.error("Erro ao verificar configuração de chave de criptografia", ex);
            return false;
        }
    }
}
