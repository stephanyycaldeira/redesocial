package com.redesocial.service;

import com.redesocial.model.Amizade;
import com.redesocial.model.Usuario;
import com.redesocial.repository.AmizadeRepository;
import com.redesocial.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RedeSocialService {

    private final UsuarioRepository usuarioRepo;
    private final AmizadeRepository amizadeRepo;

    public RedeSocialService() {
        this.usuarioRepo = new UsuarioRepository();
        this.amizadeRepo = new AmizadeRepository();
    }

    public Usuario cadastrarUsuario(String nome, String email, int idade, String cidade) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Usuario usuario = new Usuario(id, nome, email, idade, cidade);
        usuarioRepo.cadastrar(usuario);
        return usuario;
    }

    public Optional<Usuario> buscarUsuario(String id) {
        return usuarioRepo.buscarPorId(id);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepo.listarTodos();
    }

    public boolean excluirUsuario(String id) {
        boolean removido = usuarioRepo.excluir(id);
        if (removido) {
            System.out.println("️ Usuário removido com sucesso (e suas conexões).");
        } else {
            System.out.println(" Usuário não encontrado.");
        }
        return removido;
    }

    public boolean criarAmizade(String idU1, String idU2) {
        if (amizadeRepo.existeAmizade(idU1, idU2)) {
            System.out.println("Relacionamento já existe entre os usuários.");
            return false;
        }
        Amizade amizade = new Amizade(idU1, idU2, "AMIGO");
        return amizadeRepo.cadastrarAmizade(amizade);
    }

    public boolean seguirUsuario(String idSeguidor, String idSeguido) {
        Amizade seguir = new Amizade(idSeguidor, idSeguido, "SEGUE");
        return amizadeRepo.cadastrarAmizade(seguir);
    }

    public boolean removerAmizade(String idU1, String idU2) {
        boolean removido = amizadeRepo.removerAmizade(idU1, idU2);
        if (removido) {
            System.out.println("Relacionamento removido com sucesso.");
        } else {
            System.out.println("Relacionamento não encontrado.");
        }
        return removido;
    }

    public List<String> listarRelacionamentos() {
        return amizadeRepo.listarTodas();
    }

    public List<Usuario> listarAmigos(String idUsuario) {
        return usuarioRepo.buscarAmigos(idUsuario);
    }

    public List<Usuario> sugerirAmigos(String idUsuario) {
        return usuarioRepo.sugerirAmigos(idUsuario);
    }

    public void caminhoMaisCurto(String idOrigem, String idDestino) {
        usuarioRepo.caminhoMaisCurto(idOrigem, idDestino);
    }

    public void exibirEstatisticas(String idUsuario) {
        amizadeRepo.exibirEstatisticas(idUsuario);
    }
}
