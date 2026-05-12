package com.redesocial.repository;

import com.redesocial.model.Amizade;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.List;

public class AmizadeRepository {

    private final Neo4jConexao conexao;

    public AmizadeRepository() {
        this.conexao = Neo4jConexao.getInstance();
    }

    public boolean cadastrarAmizade(Amizade amizade) {
        String verificar = """
                MATCH (a:Usuario {id: $id1}), (b:Usuario {id: $id2})
                RETURN count(*) AS total
                """;

        try (Session session = conexao.abrirSessao()) {
            Result check = session.run(verificar, Values.parameters(
                    "id1", amizade.getIdUsuario1(),
                    "id2", amizade.getIdUsuario2()
            ));
            int total = check.single().get("total").asInt();
            if (total == 0) {
                System.out.println("Um ou ambos os usuários não existem.");
                return false;
            }
        }

        String relType = amizade.getTipo().equalsIgnoreCase("SEGUE") ? "SEGUE" : "AMIGO";

        String query = String.format("""
                MATCH (a:Usuario {id: $id1}), (b:Usuario {id: $id2})
                MERGE (a)-[r:%s]->(b)
                SET r.desde = $desde, r.tipo = $tipo
                """, relType);

        try (Session session = conexao.abrirSessao()) {
            session.run(query, Values.parameters(
                    "id1",   amizade.getIdUsuario1(),
                    "id2",   amizade.getIdUsuario2(),
                    "desde", amizade.getDataCriacao().toString(),
                    "tipo",  amizade.getTipo()
            ));
            System.out.printf("Relacionamento [%s] criado entre %s e %s%n",
                    relType, amizade.getIdUsuario1(), amizade.getIdUsuario2());
            return true;
        }
    }
    public boolean removerAmizade(String idUsuario1, String idUsuario2) {
        String query = """
                MATCH (a:Usuario {id: $id1})-[r]-(b:Usuario {id: $id2})
                DELETE r
                """;

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query, Values.parameters(
                    "id1", idUsuario1,
                    "id2", idUsuario2
            ));
            int deletados = result.consume().counters().relationshipsDeleted();
            return deletados > 0;
        }
    }

    public List<String> listarTodas() {
        String query = """
                MATCH (a:Usuario)-[r]->(b:Usuario)
                RETURN a.nome AS origem, type(r) AS tipo, b.nome AS destino, r.desde AS desde
                ORDER BY a.nome
                """;
        List<String> relacionamentos = new ArrayList<>();

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                Record rec = result.next();
                relacionamentos.add(String.format(
                        "%s --[%s]--> %s (desde: %s)",
                        rec.get("origem").asString(),
                        rec.get("tipo").asString(),
                        rec.get("destino").asString(),
                        rec.get("desde").asString("")
                ));
            }
        }
        return relacionamentos;
    }

    public boolean existeAmizade(String idUsuario1, String idUsuario2) {
        String query = """
                MATCH (a:Usuario {id: $id1})-[r]-(b:Usuario {id: $id2})
                RETURN count(r) AS total
                """;

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query, Values.parameters(
                    "id1", idUsuario1,
                    "id2", idUsuario2
            ));
            return result.single().get("total").asInt() > 0;
        }
    }

    public void exibirEstatisticas(String idUsuario) {
        String query = """
                MATCH (u:Usuario {id: $id})
                OPTIONAL MATCH (u)-[saida]->(:Usuario)
                OPTIONAL MATCH (:Usuario)-[entrada]->(u)
                RETURN u.nome AS nome,
                       count(DISTINCT saida)  AS conexoesSaindo,
                       count(DISTINCT entrada) AS conexoesEntrando
                """;

        try (Session session = conexao.abrirSessao()) {
            Result result = session.run(query, Values.parameters("id", idUsuario));
            if (result.hasNext()) {
                Record rec = result.next();
                System.out.printf("""
                            Estatísticas de %s:
                           → Conexões saindo  (out-degree): %d
                           → Conexões entrando (in-degree): %d
                        """,
                        rec.get("nome").asString(),
                        rec.get("conexoesSaindo").asInt(),
                        rec.get("conexoesEntrando").asInt()
                );
            }
        }
    }
}
