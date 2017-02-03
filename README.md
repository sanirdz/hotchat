## DESAFIO DE PROGRAMAÇÃO - HOTMART
Aplicação Web de Chat utilizando a linguagem de programação Java e as tecnologias WebSocket e APIs RESTful

### Stack de Tecnologias

* Java 1.8
* Spring Boot 1.4.4 (websocket, security, data-jpa, thymeleaf)
* Webjars (angularjs 1.6.1 + ng-stomp, bootstrap 3.3.7-1, jquery 2.2.4, font-awesome 4.7.0, animate.css 3.5.2)
* Swagger 2.6.1
* Banco de dados em memória H2 (`http://localhost:8080/hotchat/h2-console`)
  * jdbc-url: `jdbc:h2:mem:test` 
  * Usuario: sa
  * Senha: (em branco)
  * A estrutura é criada no momento que sobe a aplicação.

#### Executar a aplicação: `mvn spring-boot:run`

* URL: `http://localhost:8080/hotchat`
* Documentação da API: `http://localhost:8080/hotchat/swagger-ui.html`

#### Informações:

* Foi criada uma aplicação estilo whatsapp-web com notificações em tempo real de usuário logado/deslogado e novas mensagens.
* São criados 100 usuários com a possibilidade de cadastrar novos:
  * login: usuario(0-99)
  * senha: senha
* Aprendi Angular e WebSocket nesse desafio, portanto pode ter alguma coisa esquisita... 

#### Pontos extras implementados:

* Criar tela de histórico de mensagens de usuário.
* Criação de lista de contatos. (Precisa de mais refinamento)
* Bloqueio e desbloqueio de usuários.
