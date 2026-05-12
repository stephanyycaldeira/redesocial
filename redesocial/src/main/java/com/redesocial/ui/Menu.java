package com.redesocial.ui;

import com.redesocial.model.Usuario;
import com.redesocial.service.RedeSocialService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Menu {

    private final RedeSocialService service;
    private final Scanner scanner;

    public Menu() {
        this.service = new RedeSocialService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║      REDE SOCIAL — GRAFOS + Neo4j    ║
                ╚══════════════════════════════════════╝
                """);

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            int opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                // ── Vértices ──────────────────────────
                case 1  -> cadastrarUsuario();
                case 2  -> listarUsuarios();
                case 3  -> buscarUsuario();
                case 4  -> excluirUsuario();
                // ── Arestas ───────────────────────────
                case 5  -> criarAmizade();
                case 6  -> seguirUsuario();
                case 7  -> removerRelacionamento();
                case 8  -> listarRelacionamentos();
                // ── Consultas de Grafo ─────────────────
                case 9  -> listarAmigos();
                case 10 -> sugerirAmigos();
                case 11 -> caminhoMaisCurto();
                case 12 -> exibirEstatisticas();
                // ── Sair ──────────────────────────────
                case 0  -> {
                    System.out.println("Até logo!");
                    rodando = false;
                }
                default -> System.out.println("Opção inválida.");
            }
            System.out.println();
        }
    }

    // ══════════════════════════════════════════════
    //  MENU PRINCIPAL
    // ══════════════════════════════════════════════
    private void exibirMenu() {
        System.out.println("""
                ┌─────────────────────────────────────┐
                │          MENU PRINCIPAL              │
                ├──────────────────┬──────────────────┤
                │  VÉRTICES        │  ARESTAS          │
                │  1. Cadastrar    │  5. Criar amizade │
                │     usuário      │  6. Seguir usuário│
                │  2. Listar todos │  7. Remover relac.│
                │  3. Buscar por ID│  8. Listar relac. │
                │  4. Excluir      ├──────────────────┤
                ├──────────────────│  CONSULTAS GRAFO  │
                │                  │  9. Ver amigos    │
                │                  │ 10. Sugerir amigos│
                │                  │ 11. Caminho curto │
                │                  │ 12. Estatísticas  │
                ├──────────────────┴──────────────────┤
                │  0. Sair                             │
                └─────────────────────────────────────┘""");
    }

    // ══════════════════════════════════════════════
    //  AÇÕES — VÉRTICES
    // ══════════════════════════════════════════════

    private void cadastrarUsuario() {
        System.out.println("\n─── Cadastrar Usuário ───");
        String nome   = lerString("Nome:   ");
        String email  = lerString("E-mail: ");
        int    idade  = lerInteiro("Idade:  ");
        String cidade = lerString("Cidade: ");

        Usuario u = service.cadastrarUsuario(nome, email, idade, cidade);
        System.out.println("📋 ID gerado: " + u.getId());
    }

    private void listarUsuarios() {
        System.out.println("\n─── Lista de Usuários ───");
        List<Usuario> lista = service.listarUsuarios();
        if (lista.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
        } else {
            lista.forEach(u -> System.out.printf(
                    "  [%s] %s | %s | %d anos | %s%n",
                    u.getId(), u.getNome(), u.getEmail(), u.getIdade(), u.getCidade()));
        }
    }

    private void buscarUsuario() {
        System.out.println("\n─── Buscar Usuário ───");
        String id = lerString("ID do usuário: ");
        Optional<Usuario> opt = service.buscarUsuario(id);
        opt.ifPresentOrElse(
                u -> System.out.println("✅ Encontrado: " + u),
                ()  -> System.out.println("❌ Usuário não encontrado.")
        );
    }

    private void excluirUsuario() {
        System.out.println("\n─── Excluir Usuário ───");
        String id = lerString("ID do usuário: ");
        service.excluirUsuario(id);
    }

    // ══════════════════════════════════════════════
    //  AÇÕES — ARESTAS
    // ══════════════════════════════════════════════

    private void criarAmizade() {
        System.out.println("\n─── Criar Amizade (AMIGO) ───");
        String id1 = lerString("ID do 1º usuário: ");
        String id2 = lerString("ID do 2º usuário: ");
        service.criarAmizade(id1, id2);
    }

    private void seguirUsuario() {
        System.out.println("\n─── Seguir Usuário (SEGUE) ───");
        String seguidor = lerString("ID de quem vai seguir:  ");
        String seguido  = lerString("ID de quem será seguido: ");
        service.seguirUsuario(seguidor, seguido);
    }

    private void removerRelacionamento() {
        System.out.println("\n─── Remover Relacionamento ───");
        String id1 = lerString("ID do 1º usuário: ");
        String id2 = lerString("ID do 2º usuário: ");
        service.removerAmizade(id1, id2);
    }

    private void listarRelacionamentos() {
        System.out.println("\n─── Todos os Relacionamentos (Arestas) ───");
        List<String> rels = service.listarRelacionamentos();
        if (rels.isEmpty()) {
            System.out.println("Nenhum relacionamento cadastrado.");
        } else {
            rels.forEach(r -> System.out.println("  " + r));
        }
    }

    // ══════════════════════════════════════════════
    //  AÇÕES — CONSULTAS DE GRAFO
    // ══════════════════════════════════════════════

    private void listarAmigos() {
        System.out.println("\n─── Amigos / Seguindo ───");
        String id = lerString("ID do usuário: ");
        List<Usuario> amigos = service.listarAmigos(id);
        if (amigos.isEmpty()) {
            System.out.println("Sem conexões encontradas.");
        } else {
            amigos.forEach(a -> System.out.printf(
                    "  → %s (%s)%n", a.getNome(), a.getCidade()));
        }
    }

    private void sugerirAmigos() {
        System.out.println("\n─── Sugestões de Amigos ───");
        String id = lerString("ID do usuário: ");
        List<Usuario> sugestoes = service.sugerirAmigos(id);
        if (sugestoes.isEmpty()) {
            System.out.println("Nenhuma sugestão disponível.");
        } else {
            System.out.println("💡 Você pode conhecer:");
            sugestoes.forEach(s -> System.out.printf(
                    "  → %s (%s)%n", s.getNome(), s.getCidade()));
        }
    }

    private void caminhoMaisCurto() {
        System.out.println("\n─── Caminho Mais Curto entre Usuários ───");
        String idOrigem  = lerString("ID de origem:  ");
        String idDestino = lerString("ID de destino: ");
        service.caminhoMaisCurto(idOrigem, idDestino);
    }

    private void exibirEstatisticas() {
        System.out.println("\n─── Estatísticas de Conexões ───");
        String id = lerString("ID do usuário: ");
        service.exibirEstatisticas(id);
    }

    // ══════════════════════════════════════════════
    //  HELPERS DE LEITURA
    // ══════════════════════════════════════════════

    private String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int valor = Integer.parseInt(scanner.nextLine().trim());
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("⚠️  Digite um número válido.");
            }
        }
    }
}
