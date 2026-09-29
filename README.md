# prontus

MVP de triagem inteligente e fila dinâmica em tempo real para o SUS.

## 💡 Funcionalidade principal

Classifica pacientes em 5 cores (Manchester adaptado) com um motor determinístico, enfileira via Kafka e **reprioriza a fila a cada 60 segundos** (score + tempo de espera) para eliminar a inanição de pacientes moderados/leves. Médico chama o próximo pelo maior score; painéis recebem atualizações em tempo real via SSE.

## 🛠️ Tecnologias

- Java 21 + Spring Boot (Web, Security, Data JPA, Scheduling)
- PostgreSQL 16
- Apache Kafka
- SSE (SseEmitter)
- JWT + RBAC
- Swagger/OpenAPI
- JUnit 5
- Docker Compose

## 🏗️ Arquitetura

Monólito Modular:
```text
br.com.fiap.prontus/
├── auth      # JWT, RBAC
├── patient   # Cadastro de pacientes
├── triage    # Triagem + TriageEngine
├── queue     # Fila: consumer Kafka, scheduler, SSE
└── shared    # Eventos e segurança
```
## Fluxo

triagem → PostgreSQL → evento Kafka → fila dinâmica → scheduler (60s) → SSE → chamada

## 🚀 Como subir
```bash
docker compose up -d      # PostgreSQL + Kafka
./mvnw spring-boot:run    # Aplicação
