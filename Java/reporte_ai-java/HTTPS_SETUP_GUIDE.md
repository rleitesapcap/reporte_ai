# Guia de Configuração HTTPS/SSL - ReporteAI

## Overview

Este guia descreve como configurar HTTPS/SSL/TLS para o ReporteAI em ambiente de produção.

## 1. Gerar Certificado SSL

### Opção 1: Usando Let's Encrypt (Recomendado para Produção)

```bash
# Instalar Certbot
sudo apt-get install certbot python3-certbot-nginx

# Gerar certificado
sudo certbot certonly --standalone -d seu-dominio.com -d www.seu-dominio.com

# Os certificados estarão em:
# /etc/letsencrypt/live/seu-dominio.com/fullchain.pem
# /etc/letsencrypt/live/seu-dominio.com/privkey.pem
```

### Opção 2: Usando OpenSSL (Para Testes/Desenvolvimento)

```bash
# Gerar private key
openssl genrsa -out private.key 2048

# Gerar certificate signing request
openssl req -new -key private.key -out request.csr

# Gerar certificado auto-assinado (válido por 365 dias)
openssl x509 -req -days 365 -in request.csr -signkey private.key -out certificate.crt

# Combinar em PKCS12 keystore
openssl pkcs12 -export -in certificate.crt -inkey private.key \
  -out keystore.p12 -name tomcat -passout pass:changeit
```

## 2. Preparar o Keystore

### Converter PEM para PKCS12 (Let's Encrypt)

```bash
# Combinar certificado e chave privada
openssl pkcs12 -export \
  -in /etc/letsencrypt/live/seu-dominio.com/fullchain.pem \
  -inkey /etc/letsencrypt/live/seu-dominio.com/privkey.pem \
  -out keystore.p12 \
  -name tomcat \
  -passout pass:seu-password-seguro
```

### Verificar Keystore

```bash
keytool -list -v -keystore keystore.p12 -storetype PKCS12 -storepass seu-password-seguro
```

## 3. Configurar a Aplicação

### 3.1 Colocar Keystore em Local Seguro

```bash
# Criar diretório seguro
sudo mkdir -p /etc/reporteai/ssl
sudo chmod 700 /etc/reporteai/ssl

# Copiar keystore
sudo cp keystore.p12 /etc/reporteai/ssl/
sudo chmod 600 /etc/reporteai/ssl/keystore.p12
```

### 3.2 Configurar Variáveis de Ambiente

```bash
# .env ou arquivo de configuração de produção
export SSL_KEYSTORE_PATH=/etc/reporteai/ssl/keystore.p12
export SSL_KEYSTORE_PASSWORD=seu-password-seguro
export SSL_KEY_ALIAS=tomcat
export DB_HOST=seu-db-host
export DB_PORT=5432
export DB_NAME=reporteai_db
export DB_USER=reporteai
export DB_PASSWORD=sua-senha-db
export JWT_SECRET=sua-chave-jwt-muito-segura-e-longa
```

### 3.3 Executar com Perfil de Produção

```bash
# Com Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Ou com JAR
java -Dspring.profiles.active=prod -jar reporteai-1.0.0.jar
```

## 4. Configuração Nginx (Reverse Proxy)

Se usar Nginx como reverse proxy (recomendado):

```nginx
# /etc/nginx/sites-available/reporteai

server {
    listen 80;
    server_name seu-dominio.com www.seu-dominio.com;
    return 301 https://$server_name$request_uri;  # Redirecionar para HTTPS
}

server {
    listen 443 ssl http2;
    server_name seu-dominio.com www.seu-dominio.com;

    # Certificados SSL
    ssl_certificate /etc/letsencrypt/live/seu-dominio.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/seu-dominio.com/privkey.pem;

    # Configurações SSL
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # Headers de segurança
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    add_header Permissions-Policy "geolocation=(), microphone=(), camera=()" always;

    # Logs
    access_log /var/log/nginx/reporteai_access.log;
    error_log /var/log/nginx/reporteai_error.log;

    # Proxy
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $server_name;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Limite de upload
    client_max_body_size 100M;
}
```

Habilitar a configuração:

```bash
sudo ln -s /etc/nginx/sites-available/reporteai /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

## 5. Renovação Automática de Certificados (Let's Encrypt)

```bash
# Crontab para renovação automática
sudo crontab -e

# Adicionar a linha:
0 0 1 * * certbot renew --quiet && systemctl restart nginx
```

## 6. Testes de Configuração HTTPS

### Verificar Certificado

```bash
# Via curl
curl -I https://seu-dominio.com

# Ver detalhes do certificado
openssl s_client -connect seu-dominio.com:443
```

### Testar com OpenSSL

```bash
openssl s_client -connect seu-dominio.com:443 -tls1_2
```

### Verificar Score SSL (Online)

Usar https://www.ssllabs.com/ssltest/ para verificar a qualidade do certificado SSL.

## 7. Configuração de Firewall

```bash
# UFW (Ubuntu)
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# Verificar
sudo ufw status
```

## 8. Health Check HTTPS

```bash
# Health endpoint (sem autenticação)
curl --insecure https://seu-dominio.com/api/v1/auth/health

# Com autenticação
TOKEN=$(curl --insecure -X POST https://seu-dominio.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r '.accessToken')

curl --insecure -H "Authorization: Bearer $TOKEN" \
  https://seu-dominio.com/api/v1/dashboard
```

## 9. Manutenção e Troubleshooting

### Ver logs de erro
```bash
sudo journalctl -u reporteai -f
sudo tail -f /var/log/nginx/reporteai_error.log
```

### Reiniciar aplicação
```bash
sudo systemctl restart reporteai
sudo systemctl restart nginx
```

### Verificar portas em uso
```bash
sudo netstat -tulpn | grep LISTEN
```

### Reverificar certificado
```bash
sudo certbot renew --dry-run
```

## 10. Checklist de Segurança em Produção

- [ ] HTTPS configurado com certificado válido
- [ ] HTTP redireciona para HTTPS
- [ ] Certificado SSL com score A+ no SSL Labs
- [ ] HSTS (Strict-Transport-Security) habilitado
- [ ] Firewall configurado (apenas portas 80, 443)
- [ ] JWT_SECRET alterado para valor seguro
- [ ] Banco de dados em servidor privado
- [ ] Backups automáticos habilitados
- [ ] Monitoring e alertas configurados
- [ ] Rate limiting ativo
- [ ] Logs de segurança sendo monitorados
- [ ] Certificado SSL com renovação automática
- [ ] CORS restritivo (apenas origens conhecidas)
- [ ] Headers de segurança configurados
- [ ] Senhas de teste removidas do banco

## 11. Performance e Otimização

### Compressão GZIP

Já ativada no `application-prod.yml`:

```yaml
server:
  compression:
    enabled: true
    min-response-size: 1024
```

### Cache HTTP

```nginx
# No Nginx
location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
    expires 365d;
    add_header Cache-Control "public, immutable";
}
```

### Connection Pooling

Configurado no `application-prod.yml`:

```yaml
datasource:
  hikari:
    maximum-pool-size: 20
    minimum-idle: 5
```

## Referências

- [Spring Boot HTTPS Configuration](https://spring.io/guides/tutorials/https/)
- [Let's Encrypt](https://letsencrypt.org/)
- [SSL Labs Best Practices](https://github.com/ssllabs/research/wiki/SSL-and-TLS-Deployment-Best-Practices)
- [OWASP Transport Layer Protection](https://owasp.org/www-project-web-security-testing-guide/latest/4-Web_Application_Security_Testing/07-Input_Validation_Testing/07-Testing_for_Command_Injection)
