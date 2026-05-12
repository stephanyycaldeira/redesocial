# 🌐 Rede Social — Grafos com Neo4j

Aplicação Java que modela uma **rede social** utilizando conceitos de **Grafos**, com persistência em **Neo4j** e containerização via **Docker**.

---

## 📐 Modelagem do Grafo

| Conceito de Grafo | Representação no Sistema |
|-------------------|--------------------------|
| **Vértice (nó)**  | `Usuario` (id, nome, email, idade, cidade) |
| **Aresta**        | `AMIGO` (bidirecional) ou `SEGUE` (direcional) |

```
(Alice) --[AMIGO]--> (Bob)
(Alice) --[SEGUE]--> (Carlos)
(Bob)   --[AMIGO]--> (Diana)
```

---

## 🛠️ Tecnologias

- **Java 17**
- **Maven 3.x**
- **Neo4j 5.x** (banco de dados orientado a grafos)
- **Docker** (containerização do Neo4j)
- **Neo4j Java Driver 5.18.0**

---

## 🐳 Subindo o Neo4j com Docker

Execute o comando abaixo para iniciar o contêiner Neo4j:

```bash
docker run \
  --name neo4j \
  -p7474:7474 -p7687:7687 \
  -d \
  -e NEO4J_AUTH=neo4j/senha123 \
  neo4j
```

Acesse o **Neo4j Browser** em: [http://localhost:7474](http://localhost:7474)

| Campo   | Valor      |
|---------|------------|
| Usuário | `neo4j`    |
| Senha   | `senha123` |

---

## 🚀 Como compilar e executar

### 1. Compilar o projeto

```bash
mvn clean package -DskipTests
```

### 2. Executar a aplicação

```bash
java -jar target/redesocial.jar
```

---

## 📋 Funcionalidades

### Vértices (Usuários)
| Opção | Descrição |
|-------|-----------|
| 1     | Cadastrar novo usuário |
| 2     | Listar todos os usuários |
| 3     | Buscar usuário por ID |
| 4     | Excluir usuário (e suas conexões) |

### Arestas (Relacionamentos)
| Opção | Descrição |
|-------|-----------|
| 5     | Criar amizade `AMIGO` entre dois usuários |
| 6     | Seguir usuário (`SEGUE` — direcional) |
| 7     | Remover relacionamento |
| 8     | Listar todos os relacionamentos |

### Consultas de Grafo
| Opção | Descrição |
|-------|-----------|
| 9     | Listar amigos/seguindo de um usuário |
| 10    | Sugerir amigos (amigos dos amigos) |
| 11    | Caminho mais curto entre dois usuários |
| 12    | Estatísticas de conexões (grau do vértice) |

---

## 🗂️ Estrutura do Projeto

```
redesocial/
├── pom.xml
├── README.md
└── src/
    └── main/
        └── java/
            └── com/redesocial/
                ├── Main.java
                ├── model/
                │   ├── Usuario.java       ← Vértice
                │   └── Amizade.java       ← Aresta
                ├── repository/
                │   ├── Neo4jConexao.java  ← Conexão com banco
                │   ├── UsuarioRepository.java
                │   └── AmizadeRepository.java
                ├── service/
                │   └── RedeSocialService.java
                └── ui/
                    └── Menu.java          ← Interface CLI
```

---

## 🔍 Queries Cypher utilizadas

```cypher
-- Criar usuário (vértice)
MERGE (u:Usuario {id: $id})
SET u.nome = $nome, u.email = $email, u.idade = $idade, u.cidade = $cidade

-- Criar amizade (aresta)
MATCH (a:Usuario {id: $id1}), (b:Usuario {id: $id2})
MERGE (a)-[r:AMIGO]->(b)

-- Sugestão de amigos (amigos dos amigos)
MATCH (u:Usuario {id: $id})-[:AMIGO|SEGUE]->(:Usuario)-[:AMIGO|SEGUE]->(sugerido:Usuario)
WHERE sugerido.id <> $id AND NOT (u)-[:AMIGO|SEGUE]->(sugerido)
RETURN DISTINCT sugerido LIMIT 10

-- Caminho mais curto
MATCH path = shortestPath((origem:Usuario {id: $idOrigem})-[*]-(destino:Usuario {id: $idDestino}))
RETURN [node IN nodes(path) | node.nome] AS caminho, length(path) AS distancia
```

---

## 👥 Grupo

Atividade desenvolvida em grupo de até 4 alunos.

---

## 📚 Referências

- [Neo4j Documentation](https://neo4j.com/docs/)
- [Neo4j Java Driver](https://neo4j.com/docs/java-manual/current/)
- [Cypher Query Language](https://neo4j.com/docs/cypher-manual/current/)
