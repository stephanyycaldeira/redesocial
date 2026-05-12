package com.redesocial.model;

import java.time.LocalDate;

public class Amizade {

    private String idUsuario1;
    private String idUsuario2;
    private LocalDate dataCriacao;
    private String tipo; // AMIGO, SEGUIDOR, COLEGA, etc.

    public Amizade(String idUsuario1, String idUsuario2, String tipo) {
        this.idUsuario1 = idUsuario1;
        this.idUsuario2 = idUsuario2;
        this.tipo = tipo;
        this.dataCriacao = LocalDate.now();
    }

    public String getIdUsuario1()      { return idUsuario1; }
    public String getIdUsuario2()      { return idUsuario2; }
    public LocalDate getDataCriacao()  { return dataCriacao; }
    public String getTipo()            { return tipo; }

    @Override
    public String toString() {
        return String.format("Amizade{de='%s', para='%s', tipo='%s', desde=%s}",
                idUsuario1, idUsuario2, tipo, dataCriacao);
    }
}
