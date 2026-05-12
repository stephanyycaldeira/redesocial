package com.redesocial.repository;

import org.neo4j.driver.*;

public class Neo4jConexao implements AutoCloseable {

    private static Neo4jConexao instancia;
    private final Driver driver;

    private Neo4jConexao(String uri, String usuario, String senha) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(usuario, senha));
        this.driver.verifyConnectivity();
        System.out.println("Conexão com Neo4j estabelecida com sucesso!");
    }

    public static Neo4jConexao getInstance() {
        if (instancia == null) {
            instancia = new Neo4jConexao(
                    "bolt://localhost:7687",
                    "neo4j",
                    "senha123"
            );
        }
        return instancia;
    }

    public Session abrirSessao() {
        return driver.session();
    }

    @Override
    public void close() {
        driver.close();
        System.out.println("Conexão com Neo4j encerrada.");
    }
}
