# Baozi Store

API REST acadêmica para cadastrar clientes, produtos e pedidos de uma pequena loja de pão chinês. Implementada com Java 17, Spring Boot 3.2.0, Spring Data JPA e H2. Usa os pacotes `model`, `repository`, `service` e `controller`.

## Executar

Requisitos: JDK 17 ou 21 e Maven 3.9.

```shell
mvn test
mvn spring-boot:run
```

Ou gere o executável:

```shell
mvn clean package
java -jar target/baozi-store-1.0.0.jar
```

A API atende em `http://localhost:8080`. O banco H2 persiste em `data/baozi`. O console local está em `/h2-console`, com JDBC URL `jdbc:h2:file:./data/baozi`, usuário `sa` e senha `senha123`. Essa configuração é exclusivamente didática, para execução local, sem autenticação. Não exponha o servidor à internet.

## Endpoints

| Entidade | Criar | Listar | Consultar por ID | Excluir |
|---|---|---|---|---|
| Cliente | POST /clientes | GET /clientes | GET /clientes/{id} | DELETE /clientes/{id} |
| Produto | POST /produtos | GET /produtos | GET /produtos/{id} | DELETE /produtos/{id} |
| Pedido | POST /pedidos | GET /pedidos | GET /pedidos/{id} | DELETE /pedidos/{id} |

POST retorna 201 e cabeçalho Location; GET retorna 200; DELETE retorna 204 sem corpo. São usados 400 para dados inválidos, 404 para recurso inexistente e 409 para conflitos de integridade ou produto indisponível. PUT é opcional no enunciado e não foi implementado.

## Exemplos de entrada

Envie o cabeçalho `Content-Type: application/json`. O ID é gerado pelo banco.

Cliente:

```json
{"nome":"Cliente de exemplo","clienteDesde":"2026-11-17"}
```

Produto:

```json
{"nome":"Baozi de porco","preco":8.50,"estoque":true}
```

Pedido, usando IDs existentes:

```json
{"clienteId":1,"produtoId":1,"quantidade":3}
```

A resposta de um pedido inclui o cliente e o produto associados. O campo `estoque` é booleano e indica disponibilidade; não representa contagem e não é decrementado. Cada pedido compra um único produto. Um cliente pode ter vários pedidos, assim como um produto pode aparecer em vários pedidos. Não há coleção reversa nas entidades, evitando referências circulares no JSON.

## Postman

Importe `postman/BaoziStore.postman_collection.json`. A coleção contém as 12 requisições obrigatórias em ordem. A variável `baseUrl` vale `http://localhost:8080`. Os scripts dos POSTs salvam automaticamente `clienteId`, `produtoId` e `pedidoId`. As últimas três requisições excluem os registros criados no roteiro: primeiro o pedido, depois o cliente e o produto. Capture as respostas antes de executar as exclusões.

Não é permitido excluir clientes ou produtos ainda vinculados a pedidos: a API retorna 409. Exclua primeiro o pedido. Os testes JUnit usam banco em memória isolado; não alteram o H2 de execução normal.

## Organização e fundamentos

- `model`: entidades JPA e relacionamentos (Aula 4).
- `repository`: interfaces JpaRepository (Aula 6 e prática 6).
- `service`: regras de negócios e transações (Aula 4, Temas 4 e 5).
- `controller`: rotas HTTP e respostas JSON (Aulas 3 e 6).
- `dto`: entrada do pedido com IDs e quantidade; evita aceitar objetos relacionados arbitrários.
- `src/test`: três testes de integração com JUnit e MockMvc, cobrindo o fluxo CRUD e regras de integridade.

As classes de serviço usam injeção por construtor. Operações de escrita usam `@Transactional`. A validação usa Bean Validation com `@Valid`. O tratamento de erros retorna JSON legível.

## Escopo

Projeto de estudo baseado no enunciado API REST Baozi Store e nos exemplos das aulas da disciplina Desenvolvimento Web Back End. Não inclui interface HTML, pagamento, autenticação, frete, múltiplos itens por pedido ou gestão quantitativa de estoque, pois não são requisitos do trabalho.
