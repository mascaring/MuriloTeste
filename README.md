Este repositório contém o código-fonte para o Desafio Técnico da posição de Desenvolvedor Fullstack Sênior. A aplicação consiste em uma API RESTful completa para um sistema de gerenciamento de tarefas, construída com Java e Spring Boot e uma aplicação web utilizando Angular e TypeScript

1. Contexto do Desafio

O objetivo foi desenvolver uma aplicação completa (Backend e Frontend) para um sistema de gerenciamento de tarefas (To-Do List), seguindo os requisitos funcionais e técnicos especificados, com foco em boas práticas de arquitetura, segurança e qualidade de código.    

Funcionalidades Implementadas
A API atende a todos os requisitos funcionais solicitados:    

Gerenciamento de Usuários:

Criação de conta de usuário.

Login com autenticação segura baseada em JSON Web Tokens (JWT).

Gerenciamento de Tarefas (CRUD):

Usuários autenticados podem criar, listar, atualizar e excluir suas próprias tarefas.

A lógica de autorização garante que um usuário não possa visualizar ou modificar as tarefas de outro.

Atributos da Tarefa:

Cada tarefa contém título, descrição, data de criação, data de vencimento e um status (ex: TO_DO, IN_PROGRESS, DONE).

2. Tech Stack (Backend)

A API foi construída utilizando tecnologias modernas e robustas do ecossistema Java, conforme solicitado no desafio:    

Linguagem: Java 17+

Framework: Spring Boot 3+

Segurança: Spring Security 6 com autenticação e autorização baseadas em JWT.

Banco de Dados: PostgreSQL (com suporte para H2 em ambiente de testes).

Persistência de Dados: Spring Data JPA / Hibernate.

Documentação da API: SpringDoc OpenAPI v2 (Swagger).

Testes: JUnit 5 e Mockito.

Build Tool: Maven.

Utilitários: Lombok para redução de código boilerplate.

2. Tecnologias

Linguagem: Java 21

Framework: Spring Boot 3+

Segurança: Spring Security 6 com autenticação e autorização baseadas em JWT.

Banco de Dados: PostgreSQL (com suporte para H2 em ambiente de testes).

Persistência de Dados: Spring Data JPA / Hibernate.

Documentação da API: SpringDoc OpenAPI v2 (Swagger).

Testes: JUnit 5 e Mockito.

Build Tool: Maven.

Angular CLI: 20.3.3

Node: 22.16.0

Package Manager: npm 10.9.2


Utilitários: Lombok para redução de código boilerplate.

3. Pré-requisitos

Para executar o projeto localmente, você precisará ter as seguintes ferramentas instaladas:

JDK 21

Maven 3.8+: Link para download

PostgreSQL 15+: Para o banco de dados.

Angular: 20.x

Node: 22.x

npm 10.x

Como essa é uma aplicação somente para o teste técnico, as informações de conexão no banco está no seguinte arquivo src/main/resources/application.yml

É possivel acessar o swagger no seguinte endereço: http://localhost:8080/swagger-ui/index.html#/

4. Execução

No backend basta executar o arquivo ToDoListApiApplication.java

No fronte end, basta executar os seguintes comandos

npm install

ng serve

5. Arquitetura

Arquitetura em Camadas: O backend segue uma arquitetura em camadas clássica e robusta (Controller -> Service -> Repository). Essa separação de responsabilidades torna o código mais organizado, testável e fácil de manter. A camada de Controller é responsável apenas por gerenciar as requisições HTTP, enquanto toda a lógica de negócio reside na camada de Service

Uso de DTOs (Data Transfer Objects): A API utiliza DTOs para desacoplar a representação dos dados expostos nos endpoints do modelo de dados interno (entidades JPA). Isso oferece segurança (evitando a exposição de campos sensíveis como senhas), flexibilidade para evoluir a API sem quebrar o contrato com o cliente e resolve problemas de serialização, como referências circulares.

Segurança por Identidade do Token: Toda a lógica de autorização (verificar se um usuário pode acessar ou modificar um recurso) é baseada na identidade extraída diretamente do token JWT no backend.

6. Uso de IA no Desenvolvimento

Durante o desenvolvimento deste projeto, utilizei o IA como uma ferramenta de aceleração e pesquisa. O principal uso foi na geração de código repetitivo (boilerplate), como a criação de DTOs, a estrutura inicial de classes de teste e a implementação de alguns métodos. Tambem utilizei para auxiliar na estilização da UI, refinando alguns pontos e facilitando para criar algumas partes dos arquivos de scss e html. Outro ponto onde foi utilizado, durante o início do projeto angular, auxiliando nas rotas e estruturação inicial. A IA foi muito util como uma assistente de produtividade.
