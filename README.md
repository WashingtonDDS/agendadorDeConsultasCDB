


# 🩺 Agendador de Consultas

Sistema de agendamento de consultas médicas desenvolvido em **Java + Spring Boot** seguindo a **Arquitetura Hexagonal**.  
A aplicação permite criar, buscar, atualizar, cancelar e excluir consultas, garantindo separação clara entre regras de negócio e infraestrutura.  

## 📊 Badges
[![Java](https://img.shields.io/badge/Java-21-red)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue)](https://www.docker.com)
[![License](https://img.shields.io/badge/license-MIT-yellow)](./LICENSE)
---

## 🛠️ Tecnologias Utilizadas
- **Java 21**
- **Spring Boot**
- **Maven**
- **PostgreSQL** (via Docker)
- **MapStruct**
- **Beekeeper Studio**
- **Persistência com JDBC puro (sem JPA/Hibernate)**



## 📂 Estrutura do Projeto (Arquitetura Hexagonal)



src/main/java/br/com/cdb/agendadorDeConsultas
├── adapter
│   ├── input (Controllers, Mappers, DTOs)
│   └── output (Entities, Repositories)
├── core
│   ├── domain (Models de negócio)
│   └── usecase (Casos de uso)
├── port (Interfaces entre camadas)
├── infrastructure (Configurações)
└── util (Classes utilitárias)



---

## 🚀 Como Executar

### 1. Clonar o repositório

git clone https://github.com/WashingtonDDS/agendadorDeConsultasCDB


### 2. Subir o banco com Docker

```bash
docker-compose up -d
```

### 3. Rodar a aplicação

```bash
mvn spring-boot:run
```

---

## 📌 Endpoints

| Método | Endpoint                       | Descrição                                 |
| ------ | ------------------------------ | ----------------------------------------- |
| POST   | `/api/consultas`               | Criar uma nova consulta                   |
| GET    | `/api/consultas`               | Buscar todas as consultas                 |
| GET    | `/api/consultas/futuras`       | Buscar consultas futuras (não canceladas) |
| GET    | `/api/consultas/{id}`          | Detalhar consulta por ID                  |
| PUT    | `/api/consultas/{id}`          | Atualizar consulta                        |
| PUT    | `/api/consultas/{id}/cancelar` | Cancelar consulta                         |
| DELETE | `/api/consultas/{id}`          | Deletar consulta                          |

---

## 📄 Exemplo de Requisição

### Criar uma consulta

```http
POST /api/consultas
Content-Type: application/json

{
  "doctorName": "Dr. João Silva",
  "patientName": "Maria Souza",
  "patientNumber": "11999999999",
  "speciality": "Cardiologia",
  "description": "Consulta de rotina",
  "consultationDateTime": "2025-09-10T14:00:00"
}
```

### Resposta

```json
{
  "id": "f4a2d7f1-8c3a-4d71-bd77-f01c4a6e56c8",
  "doctorName": "Dr. João Silva",
  "patientName": "Maria Souza",
  "status": "AGENDADA",
  "consultationDateTime": "2025-09-10T14:00:00"
}
```

---

## 🗄️ Banco de Dados

* O banco de dados **PostgreSQL** é executado via **Docker**.
* Utilize o **Beekeeper Studio** para gerenciar e visualizar os dados.
* Toda a persistência é feita com **JDBC puro**, sem o uso de **ORMs** como JPA ou Hibernate.

  > Essa abordagem garante maior controle sobre as queries SQL, mais proximidade com o banco e performance ajustada ao projeto.

### Estrutura da Tabela `consulta`

```sql
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE consulta (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    doctorName VARCHAR(100) NOT NULL,
    patientName VARCHAR(100) NOT NULL,
    patientNumber VARCHAR(20) NOT NULL,
    speciality VARCHAR(100) NOT NULL,
    description VARCHAR(300) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
    consultationDateTime TIMESTAMP NOT NULL
);
```

### Comandos Docker

1. **Subir o container do banco**:

   ```bash
   docker-compose up -d
   ```
2. **Parar o container**:

   ```bash
   docker-compose down
   ```

---

## 📝 Mapeamento com MapStruct

O **MapStruct** é utilizado para converter objetos entre camadas, facilitando a conversão de **Entity → DTO** e **DTO → Entity** sem a necessidade de código boilerplate.

Exemplo do `ConsultaMapper`:

```java
package br.com.cdb.agendadorDeConsultas.adapter.input.mapper;

import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaDetails;
import br.com.cdb.agendadorDeConsultas.adapter.input.request.ConsultaResponse;
import br.com.cdb.agendadorDeConsultas.core.domain.model.Consulta;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultaMapper {

    ConsultaResponse toResponse(Consulta consulta);

    ConsultaDetails toDetails(Consulta consulta);
}
```

### 🔎 Observação

Este projeto **não utiliza JPA/Hibernate** para persistência.
Toda a comunicação com o banco de dados é feita utilizando **JDBC puro**, garantindo maior controle sobre as queries SQL e performance.

---

## 📌 Roadmap Futuro

* ✅ CRUD de consultas
* ⏳ Integração com envio de lembretes por e-mail
* ⏳ Autenticação de usuários (Spring Security + JWT)
* ⏳ Front-end em React para consumo da API

---

## 📄 Licença

Este projeto está sob a licença **MIT**.
Desenvolvido por [WashingtonDDS](https://github.com/WashingtonDDS) 🚀

```

