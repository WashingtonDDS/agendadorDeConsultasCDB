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

## 🎨 Padrões de Projeto

### Padrão Criacional: Prototype
O padrão **Prototype** é utilizado para criar novos objetos a partir de um modelo (ou protótipo) existente, evitando o custo de criar um objeto do zero.

- **Implementação**: A classe `Consulta` implementa a interface `Cloneable` e sobrescreve o método `clone()`.
- **Objetivo**: Facilitar a criação de consultas de retorno. Em vez de preencher manualmente todos os campos, uma consulta existente é clonada e apenas as informações necessárias (como a data) são alteradas.

```java
@Override
public Consulta clone() {
    try {
        return (Consulta) super.clone();
    } catch (CloneNotSupportedException e) {
        throw new AssertionError();
    }
}
```

### Padrão Estrutural: Proxy (Cache)
O padrão **Proxy** é utilizado para fornecer um substituto ou um espaço reservado para outro objeto, controlando o acesso a ele. Neste projeto, ele foi implementado como um **Proxy de Cache** para otimizar as consultas ao banco de dados.

- **Implementação**: A classe `ConsultaRepositoryProxy` atua como um proxy para o `ConsultaRepository`.
- **Objetivo**: Reduzir o número de acessos ao banco de dados através de um cache em memória.
  - **Leitura**: Ao buscar uma consulta, o proxy primeiro verifica se ela está no cache. Se estiver, retorna o dado em cache, evitando a leitura do banco.
  - **Escrita/Exclusão**: Ao salvar ou excluir uma consulta, o proxy remove a entrada correspondente do cache para evitar dados obsoletos.

```java
public class ConsultaRepositoryProxy implements ConsultaOutputPort {
    private final ConsultaOutputPort realRepository;
    private final Map<UUID, Consulta> cache = new ConcurrentHashMap<>();

    @Override
    public Optional<Consulta> findById(UUID id) {
        if (cache.containsKey(id)) {
            logger.info("PROXY HIT: Retornando consulta {} do cache.", id);
            return Optional.of(cache.get(id));
        }

        logger.info("PROXY MISS: Buscando consulta {} no repositório real.", id);
        Optional<Consulta> consultaDoBanco = realRepository.findById(id);
        consultaDoBanco.ifPresent(c -> cache.put(id, c));
        return consultaDoBanco;
    }

    @Override
    public Consulta save(Consulta consulta) {
        logger.info("PROXY: Invalidando cache para a consulta {}.", consulta.getId());
        cache.remove(consulta.getId());
        return realRepository.save(consulta);
    }
}
```

---

## 📂 Estrutura do Projeto (Arquitetura Hexagonal)

src/main/java/br/com/cdb/agendadorDeConsultas
├── adapter
│   ├── input (Controllers, Mappers, DTOs)
│   └── output (Entities, Repositories, Proxies)
├── core
│   ├── domain (Models de negócio)
│   └── usecase (Casos de uso)
├── port (Interfaces entre camadas)
├── infrastructure (Configurações)
└── util (Classes utilitárias)

---

## 🚀 Como Executar

### 1. Clonar o repositório
```bash
git clone https://github.com/WashingtonDDS/agendadorDeConsultasCDB
```

### 2. Subir o banco com Docker
```bash
docker-compose up -d
```

### 3. Rodar a aplicação
```bash
mvn spring-boot:run
```

---

## 📖 Documentação da API (Swagger)
A documentação completa da API foi gerada com **Swagger (OpenAPI)** e pode ser acessada de forma interativa no seu navegador.

Após iniciar a aplicação, acesse:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Lá você encontrará todos os endpoints, detalhes sobre os parâmetros, exemplos de requisições e respostas.

---

## 🗄️ Banco de Dados

* O banco de dados **PostgreSQL** é executado via **Docker**.
* Toda a persistência é feita com **JDBC puro**, sem o uso de **ORMs** como JPA ou Hibernate.

  > Essa abordagem garante maior controle sobre as queries SQL, mais proximidade com o banco e performance ajustada ao projeto.

---

## 📝 Mapeamento com MapStruct

O **MapStruct** é utilizado para converter objetos entre camadas, facilitando a conversão de **Entity → DTO** e **DTO → Entity** sem a necessidade de código boilerplate.

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
