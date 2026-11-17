# Atividade Prática 1: JavaFX + MVC

Projeto desktop para cadastro e gerenciamento de livros da Biblioteca Cultural, desenvolvido em Java 21 com JavaFX, FXML, arquitetura MVC e persistência JPA/Hibernate.

## Funcionalidades

- **Cadastrar:** registra título, autor, ano e quantidade de páginas.
- **Listar:** apresenta os livros cadastrados em uma `TableView`.
- **Buscar:** localiza um livro pelo título, sem diferenciar letras maiúsculas e minúsculas.
- **Editar:** permite alterar os dados do livro selecionado.
- **Excluir:** remove o livro selecionado após confirmação.
- **Validar:** verifica campos vazios e valores numéricos inválidos antes de salvar.
- **Persistir:** guarda os dados em um banco H2 local, sem exigir instalação de MySQL ou PostgreSQL.

## Tecnologias

- Java 21
- JavaFX 21 e FXML
- JPA 3.1 com Hibernate ORM 6
- H2 Database
- Maven
- JUnit 5

## Organização do projeto

```text
Atividade-JavaFX/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── application/App.java
    │   │   ├── controller/LivroController.java
    │   │   ├── model/ItemAcervo.java
    │   │   ├── model/Livro.java
    │   │   ├── persistence/JpaUtil.java
    │   │   └── service/LivroService.java
    │   └── resources/
    │       ├── META-INF/persistence.xml
    │       └── view/LivroView.fxml
    └── test/java/service/LivroServiceTest.java
```

## Pré-requisitos

1. JDK 21 instalado.
2. Apache Maven 3.9 ou superior instalado e disponível no PATH.
3. Conexão com a internet na primeira compilação, para o Maven baixar as dependências.

## Como executar

Abra o terminal dentro desta pasta (`Atividade-JavaFX`) e execute:

```bash
mvn clean javafx:run
```

Na primeira execução, o Hibernate cria a estrutura das tabelas automaticamente. O banco de dados será armazenado na pasta `data/`, criada na raiz do projeto. Essa pasta está no `.gitignore` porque contém os dados locais de execução e não precisa ser enviada ao GitHub.

## Como executar os testes

```bash
mvn clean test
```

Os testes verificam cadastro, listagem, busca por título, atualização, exclusão e validações dos dados. Eles usam um banco H2 em memória separado do banco utilizado pela aplicação.

## Arquitetura MVC

- **Model:** `ItemAcervo.java` reúne os campos comuns dos itens do acervo; `Livro.java` herda essa classe e representa a entidade persistida na tabela `livros`.
- **View:** `LivroView.fxml` define os componentes visuais da tela.
- **Controller:** `LivroController.java` recebe os eventos da interface e chama a camada de serviço.
- **Service/Persistence:** `LivroService.java` concentra as operações de negócio e JPA; `JpaUtil.java` administra a fábrica de `EntityManager`.

## Observação sobre o banco de dados

Foi escolhido o H2 em modo arquivo para facilitar a execução e a apresentação do trabalho. Assim, não é necessário instalar um servidor de banco de dados nem alterar credenciais. A configuração está em `src/main/resources/META-INF/persistence.xml`.
