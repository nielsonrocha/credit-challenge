# Credits Challenge API

Uma API REST para consulta de créditos constituídos, desenvolvida como parte de um desafio técnico. O sistema permite consultar créditos por número da NFS-e ou por número do crédito específico, com integração ao Apache Kafka para auditoria de consultas.

### Principais Características

- **API RESTful** com documentação OpenAPI/Swagger
- **Integração com Apache Kafka** para eventos de auditoria
- **Banco de dados PostgreSQL** com migrações Flyway
- **Testes abrangentes** (unitários e integração) com TestContainers
- **Observabilidade** com Actuator e métricas Prometheus
- **Containerização** completa com Docker

## Tecnologias Utilizadas

### Backend
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Kafka** - Integração com Apache Kafka
- **Spring Validation** - Validação de dados
- **Spring Actuator** - Monitoramento e métricas

### Banco de Dados
- **PostgreSQL 15** - Banco de dados principal
- **Flyway** - Controle de migrações

### Mensageria
- **Apache Kafka 3.7.0** - Sistema de mensageria
- **Zookeeper** - Coordenação do Kafka

### Utilitários
- **MapStruct 1.5.5** - Mapeamento de objetos
- **Lombok** - Redução de boilerplate
- **SpringDoc OpenAPI 2.8.4** - Documentação da API

### Testes
- **JUnit 5** - Framework de testes
- **TestContainers 1.21.1** - Testes de integração
- **REST Assured 5.5.5** - Testes de API
- **Mockito** - Mocks para testes unitários

### Build e Deploy
- **Maven 3.9.10** - Gerenciamento de dependências
- **Docker & Docker Compose** - Containerização
- **JaCoCo** - Cobertura de testes

### Monitoramento
- **Micrometer** - Métricas da aplicação
- **Prometheus** - Coleta de métricas
- **Kafka UI** - Interface para monitoramento do Kafka

## Funcionalidades

### Consultas de Créditos
-  **Buscar créditos por NFS-e**: Retorna lista de créditos associados a um número de NFS-e
-  **Buscar crédito específico**: Retorna detalhes de um crédito pelo seu número
-  **Tratamento de erros**: Respostas padronizadas para casos de erro
-  **Validação de entrada**: Validação automática dos parâmetros de entrada

### Auditoria e Observabilidade
-  **Eventos de auditoria**: Registro assíncrono das consultas realizadas
-  **Health checks**: Verificação de saúde da aplicação e dependências
-  **Métricas**: Exposição de métricas para monitoramento
-  **Logs estruturados**: Sistema de logs para debugging e monitoramento

## Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- **Docker** (versão 20.10+) e **Docker Compose** (versão 2.0+)
- **Java 21** (para desenvolvimento local)
- **Maven 3.9+** (para desenvolvimento local)
- **Git** (para clonagem do repositório)

##  Instalação e Execução

### 1. Clone o Repositório

```bash
git clone <url-do-repositorio>
cd credits-challenge-api
```

### 2. Execução com Docker (Recomendado)

Execute toda a stack com um único comando:

```bash
docker-compose up -d
```

Isso irá iniciar:
- **PostgreSQL** na porta `5433`
- **Zookeeper** na porta `2181`
- **Kafka** na porta `9092`
- **Kafka UI** na porta `8090`
- **Backend API** na porta `8080`

### 3. Verificação dos Serviços

Aguarde alguns minutos para que todos os serviços inicializem e verifique:

```bash
# Status dos containers
docker-compose ps

# Logs da aplicação
docker-compose logs -f backend

# Health check da API
curl http://localhost:8080/actuator/health
```

### 4. Execução Local para Desenvolvimento

Se preferir executar localmente para desenvolvimento:

```bash
# Inicie apenas as dependências
docker-compose up -d postgres kafka zookeeper

# Execute a aplicação
cd backend
./mvnw spring-boot:run
```

## Endpoints da API

### Base URL
```
http://localhost:8080
```

### Consulta de Créditos

#### Buscar créditos por NFS-e
```http
GET /api/creditos/{numeroNfse}
```

**Exemplo:**
```bash
curl http://localhost:8080/api/creditos/7891011
```

#### Buscar crédito específico
```http
GET /api/creditos/credito/{numeroCredito}
```

**Exemplo:**
```bash
curl http://localhost:8080/api/creditos/credito/123456
```

### Documentação Interativa

Acesse a documentação completa da API:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Monitoramento

- **Health Check**: http://localhost:8080/actuator/health
- **Métricas**: http://localhost:8080/actuator/metrics
- **Prometheus**: http://localhost:8080/actuator/prometheus
- **Kafka UI**: http://localhost:8090

## Testes

### Executar Todos os Testes

```bash
cd backend
./mvnw test
```

### Testes com Cobertura

```bash
./mvnw clean test jacoco:report
```

O relatório de cobertura estará disponível em:
```
backend/target/site/jacoco/index.html
```

### Tipos de Testes

- **Testes Unitários**: Testam componentes isoladamente
- **Testes de Integração**: Testam a aplicação completa com TestContainers
- **Testes de API**: Validam contratos e schemas JSON com REST Assured
- **Testes de Kafka**: Verificam integração com sistema de mensageria

## Monitoramento

### Health Checks

A aplicação expõe endpoints de saúde que verificam:
- Conectividade com PostgreSQL
- Conectividade com Kafka
- Status geral da aplicação

### Métricas Disponíveis

- **Métricas da JVM**: Memória, threads, garbage collection
- **Métricas HTTP**: Requests, responses, latência
- **Métricas customizadas**: Consultas de créditos, eventos Kafka
- **Métricas do banco**: Pool de conexões, queries

### Kafka UI

Interface web para monitoramento do Kafka disponível em http://localhost:8090:
- Visualização de tópicos e mensagens
- Monitoring de consumers e producers
- Métricas de performance


```

### Logs e Debug

```bash
# Logs em tempo real
docker-compose logs -f

# Logs de um serviço específico
docker-compose logs -f backend

# Logs com timestamp
docker-compose logs -f -t
```

## Dados de Teste

A aplicação já vem com dados de exemplo inseridos automaticamente:

### Créditos Disponíveis

| Número do Crédito | NFS-e | Valor ISSQN | Tipo |
|------------------|-------|-------------|------|
| 123456 | 7891011 | R$ 1.500,75 | ISSQN |
| 789012 | 7891011 | R$ 1.200,50 | ISSQN |
| 654321 | 1122334 | R$ 800,50 | Outros |

### Exemplos de Consulta

```bash
# Buscar créditos da NFS-e 7891011 (retorna 2 créditos)
curl http://localhost:8080/api/creditos/7891011

# Buscar crédito específico 123456
curl http://localhost:8080/api/creditos/credito/123456

# Buscar NFS-e inexistente (retorna 404)
curl http://localhost:8080/api/creditos/999999
```