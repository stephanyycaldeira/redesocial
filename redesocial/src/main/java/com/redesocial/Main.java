package com.redesocial;

import com.redesocial.repository.Neo4jConexao;
import com.redesocial.ui.Menu;

public class 1Main {

    public static void main(String[] args) {
        try {
            Menu menu = new Menu();
            menu.iniciar();
        } catch (Exception e) {
            System.err.println("Erro ao conectar com o Neo4j: " + e.getMessage());
            System.err.println("Certifique-se de que o Docker com Neo4j está rodando:");
            System.err.println("docker run --name neo4j -p7474:7474 -p7687:7687 -d -e NEO4J_AUTH=neo4j/senha123 neo4j");
        } finally {
            // Fecha a conexão ao encerrar
            try {
                Neo4jConexao.getInstance().close();
            } catch (Exception ignored) {}
        }
    }
}
