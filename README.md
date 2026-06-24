# Pet Shop Management System

Sistema desenvolvido para o gerenciamento de um Pet Shop, focado na organização de proprietários, 
animais e histórico de atendimentos. Este projeto foi desenvolvido como requisito avaliativo da 
disciplina de Programação Orientada a Objetos.

## Tecnologias Utilizadas
* **Java 21+**
* **Spring Boot** (MVC, Data JPA)
* **MySQL** (Banco de dados relacional)
* **Thymeleaf** (Engine de template para interface web)
* **Maven** (Gerenciamento de dependências)

## Funcionalidades Principais
- **Gestão Completa (CRUD):** Cadastro de animais, proprietários e serviços.
- **Segurança de Dados:** Validação estrita de duplicidade (CPF para proprietários, nome para serviços, registro único de serviço por animal/dia).
- **Relatórios:** Filtros personalizados por cliente, data e tipo de serviço com cálculo automático de faturamento.
- **Upload de Fotos:** Armazenamento local de imagens dos animais.

## Como Executar
1. Clone este repositório: `git clone [URL-DO-SEU-REPO]`
2. Configure o seu banco de dados MySQL criando um banco chamado `petshop_db`.
3. Ajuste as credenciais no arquivo `src/main/resources/application.properties`.
4. Execute o projeto via terminal: `./mvnw spring-boot:run`
5. Acesse no navegador: `http://localhost:8080`

## Autor
Desenvolvido por **Lucas Cimino** - Ciência da Computação (UNIPAC Barbacena).