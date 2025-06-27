# 🔐 API de Autenticação JWT - AV2 (Spring Boot 3)

## 🚀 Visão Geral

Este projeto é uma continuação da API JWT desenvolvida na AV1, agora incorporando recursos mais avançados de segurança, testes automatizados, monitoramento e deploy em ambiente de produção.

### Principais Funcionalidades:

* Autenticação baseada em JWT, com geração e validação dos tokens no backend
* Proteção de rotas com base em permissões (roles) de usuários
* Testes unitários com JUnit e Mockito
* Testes de desempenho com JMeter
* Monitoramento usando Actuator, Prometheus e Grafana
* Containerização via Docker e deploy em nuvem

---

## 📆 Tecnologias e Dependências

* `Spring Boot Starter Web`
* `Spring Boot Starter Security`
* `Spring Boot Starter OAuth2 Resource Server`
* `Spring Boot Starter Data JPA`
* `H2 Database` (para testes)
* `Springdoc OpenAPI` (Swagger)
* `Lombok`
* `Spring Boot DevTools`
* `Spring Boot Starter Test` (JUnit + Mockito)
* `Spring Boot Actuator`
* `Prometheus` e `Grafana` (para métricas e dashboards)
* `Docker`
* `Apache JMeter`

---

## ⚙️ Configuração do Ambiente

```yaml
# src/main/resources/application.yml

server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true

jwt:
  secret: umaChaveSecretaMuitoLongaEComplexaParaAssinarTokensJWT
  expiration: 3600000

springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

---

## 🔐 Autenticação e Controle de Acesso

### Endpoints:

* `POST /auth/login` – autentica usuário e retorna o token JWT
* `POST /auth/register` – cadastra novos usuários
* `POST /auth/validate` – verifica a validade de um token JWT

### Segurança:

* Endpoints protegidos por autenticação JWT
* Acesso autorizado com base nas roles (`USER`, `ADMIN`)
* Tokens gerados e validados com HMAC SHA-256

---

## 🧹 Endpoints CRUD Protegidos

* Operações de **leitura** acessíveis a qualquer usuário autenticado
* Ações de **escrita** (POST, PUT, DELETE) restritas a usuários com role `ADMIN`
* Todos os endpoints REST estão protegidos por JWT

---

## 🧊 Testes Automatizados

* Implementação com `JUnit 5` e `MockMvc`
* Casos de teste para autenticação, validação de tokens e controle de acesso
* Comando para rodar os testes:

```bash
./mvnw test
```

---

## 📊 Testes de Carga com JMeter

### Como executar:

1. Abra o JMeter e crie um **Thread Group** (ex: 200 usuários, 10 repetições)

2. Adicione um **HTTP Request** para o endpoint `POST /auth/login`

3. Configure os parâmetros: `username=admin`, `password=123456`

4. Insira os listeners:

   * Summary Report
   * View Results Tree

5. Execute e avalie os seguintes indicadores:

   * ✅ Throughput
   * ⏱️ Tempo médio de resposta
   * ❌ Porcentagem de erros

> Arquivo de teste: `jmeter-tests/login_stress_test.jmx`

---

## 📚 Documentação da API

Documentação interativa disponível via Swagger:

* Acesse: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* Suporte à autenticação JWT diretamente na interface

---

## 🧺 Monitoramento da Aplicação

Endpoints disponibilizados pelo **Spring Boot Actuator**:

* `/actuator/health`
* `/actuator/info`
* `/actuator/metrics`
* `/actuator/prometheus`

### Integrações:

* **Prometheus** coleta métricas da API via `/actuator/prometheus`
* **Grafana** exibe dashboards com:

  * Requisições por segundo
  * Tempo médio de resposta
  * Uso de recursos (CPU/memória)

---

## 🐳 Docker e Deploy

### Dockerfile:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
COPY target/auth-api.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Build e execução:

```bash
./mvnw clean package
docker build -t auth-api .
docker run -p 8080:8080 auth-api
```

### Hospedagem recomendada:

* **Render** – integração com GitHub para CI/CD
* **Railway** – suporte nativo para Spring Boot e Docker

---

## ⚖️ Execução Local

1. Clone o projeto:

```bash
git clone https://github.com/seu-usuario/auth-api-av2.git
cd auth-api-av2
```

2. Inicie a aplicação:

```bash
./mvnw spring-boot:run
```

3. Acesse as interfaces:

* Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* H2 Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

---

## 👥 Usuários Padrão

| Usuário | Senha    | Role  |
| ------- | -------- | ----- |
| admin   | 123456   | ADMIN |
| user    | password | USER  |

* Acesso de leitura: disponível para usuários autenticados
* Acesso de escrita: exclusivo para administradores (`ADMIN`)

---
