package com.redesocial.repository;

import com.redesocial.model.Usuario;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    private final Neo4jConexao conexao;

    public UsuarioRepository() {
        this.conexao = Neo4jConexao.getInstance();
    }

    public void cadastrar(Usuario usuario) {
        String query = """
                MERGE (u:Usuario {id: $id})
                SET u.nome   = $nome,
                    u.email  = $email,
                    u.idade  = $idade,
                    u.cidade = $cidade
                """;

        try (Session session = conexao.abrirSessao()) {
            session.run(query, Values.parameters(
                    "id",     usuario.getId(),
                    "nome",   usuario.getNome(),
                    "email",  usuario.getEmail(),
                    "idade",  usuario.getIdade(),
                    "cidade", usuario.getCidade()
            ));
            System.out.println("Usuário cadastrado: " + usuario.getNome());
        }
    }

    public Optional<Usuario> buscarPorId(String id) {
        String query = "MATCH (u:Usuario {id: $id}) RETURN u";

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query, Values.parameters("id", id));
            if (result.hasNext()) {
                return Optional.of(mapearUsuario(result.next()));
            }
        }
        return Optional.empty();
    }

    public List<Usuario> listarTodos() {
        String query = "MATCH (u:Usuario) RETURN u ORDER BY u.nome";
        List<Usuario> usuarios = new ArrayList<>();

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                usuarios.add(mapearUsuario(result.next()));
            }
        }
        return usuarios;
    }
    public boolean excluir(String id) {
        String query = "MATCH (u:Usuario {id: $id}) DETACH DELETE u";

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query, Values.parameters("id", id));
            long deletados = result.consume().counters().nodesDeleted();
            return deletados > 0;
        }
    }

    public List<Usuario> buscarAmigos(String idUsuario) {
        String query = """
                MATCH (u:Usuario {id: $id})-[:AMIGO|SEGUE]->(amigo:Usuario)
                RETURN amigo
                ORDER BY amigo.nome
                """;
        List<Usuario> amigos = new ArrayList<>();

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query, Values.parameters("id", idUsuario));
            while (result.hasNext()) {
                amigos.add(mapearUsuario(result.next(), "amigo"));
            }
        }
        return amigos;
    }

        public List<Usuario> sugerirAmigos(String idUsuario) {
        String query = """
                MATCH (u:Usuario {id: $id})-[:AMIGO|SEGUE]->(:Usuario)-[:AMIGO|SEGUE]->(sugerido:Usuario)
                WHERE sugerido.id <> $id
                  AND NOT (u)-[:AMIGO|SEGUE]->(sugerido)
                RETURN DISTINCT sugerido
                ORDER BY sugerido.nome
                LIMIT 10
                """;
        List<Usuario> sugeridos = new ArrayList<>();

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query, Values.parameters("id", idUsuario));
            while (result.hasNext()) {
                sugeridos.add(mapearUsuario(result.next(), "sugerido"));
            }
        }
        return sugeridos;
    }
    public void caminhoMaisCurto(String idOrigem, String idDestino) {
        String query = """
                MATCH (origem:Usuario {id: $idOrigem}),
                      (destino:Usuario {id: $idDestino}),
                      path = shortestPath((origem)-[*]-(destino))
                RETURN [node IN nodes(path) | node.nome] AS caminho,
                       length(path) AS distancia
                """;

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query, Values.parameters(
                    "idOrigem",  idOrigem,
                    "idDestino", idDestino
            ));

            if (result.hasNext()) {
                Record record = result.next();
                List<Object> caminho = record.get("caminho").asList();
                int distancia = record.get("distancia").asInt();
                System.out.println("Caminho mais curto (" + distancia + " passo(s)): " + caminho);
            } else {
                System.out.println("Nenhum caminho encontrado entre os usuários.");
            }
        }
    }

    private Usuario mapearUsuario(Record record) {
        return mapearUsuario(record, "u");
    }

    private Usuario mapearUsuario(Record record, String alias) {
        var node = record.get(alias).asNode();
        return new Usuario(
                node.get("id").asString(),
                node.get("nome").asString(),
                node.get("email").asString(""),
                node.get("idade").asInt(0),
                node.get("cidade").asString("")
        );
    }
}
