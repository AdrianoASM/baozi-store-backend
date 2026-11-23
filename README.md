# Baozi Store

Trabalho de Desenvolvimento Web Back End: API REST de uma loja de pão chinês, com cadastro de clientes, produtos e pedidos.

O projeto usa Java 17, Spring Boot 3.2.0, Spring Data JPA e banco H2. Cada pedido contém um cliente, um produto e uma quantidade.

## Executar

Com JDK 17 ou 21 e Maven 3.9 instalados, abra um terminal na pasta do projeto:

```shell
mvn test
mvn spring-boot:run
```

A API fica em `http://localhost:8080`. Para gerar e iniciar o JAR:

```shell
mvn clean package
java -jar target/baozi-store-1.0.0.jar
```

## Rotas

| Recurso | Criar | Listar | Consultar | Excluir |
|---|---|---|---|---|
| Cliente | POST /clientes | GET /clientes | GET /clientes/{id} | DELETE /clientes/{id} |
| Produto | POST /produtos | GET /produtos | GET /produtos/{id} | DELETE /produtos/{id} |
| Pedido | POST /pedidos | GET /pedidos | GET /pedidos/{id} | DELETE /pedidos/{id} |

Os POSTs recebem JSON com `Content-Type: application/json`. Exemplos:

```json
{"nome":"Cliente de exemplo","clienteDesde":"2026-11-17"}
```

```json
{"nome":"Baozi de porco","preco":8.50,"estoque":true}
```

```json
{"clienteId":1,"produtoId":1,"quantidade":3}
```

Use os IDs retornados pelo cadastro. As respostas são 201 ao criar, 200 ao consultar e 204 ao excluir. Dados inválidos retornam 400; registros inexistentes, 404; conflitos, 409. PUT é opcional no trabalho e não foi implementado.

## Banco e regras

O H2 salva os dados em `data/baozi`, relativo à pasta de execução. O console fica em `/h2-console`: URL `jdbc:h2:file:./data/baozi`, usuário `sa`, senha `senha123`. A configuração é para uso local e não inclui autenticação.

O campo `estoque` indica disponibilidade, sem controlar o número de unidades. Não é possível criar pedido de produto indisponível nem excluir cliente ou produto com pedidos vinculados. Exclua primeiro o pedido.

## Organização e testes

`model` contém as entidades; `repository`, o acesso ao banco; `service`, as regras e transações; `controller`, as rotas; e `dto`, os dados recebidos para criar pedidos.

Importe `postman/BaoziStore.postman_collection.json` no Postman. Execute os cadastros, as consultas e, por último, as exclusões. A coleção salva os IDs dos cadastros automaticamente. Não repita um DELETE depois do retorno 204: a próxima tentativa retornará 404.

Os três testes de integração em `src/test` usam H2 em memória, separado dos dados da aplicação. Verificam o CRUD, as validações e os vínculos entre as entidades.
