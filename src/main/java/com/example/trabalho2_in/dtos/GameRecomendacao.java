package com.example.trabalho2_in.dtos;

import java.util.List;

public class GameRecomendacao {

    private String grupo;
    private String descricao;
    private List<String> jogosIndicados;

    public GameRecomendacao() {
    }

    public GameRecomendacao(String grupo, String descricao, List<String> jogosIndicados) {
        this.grupo = grupo;
        this.descricao = descricao;
        this.jogosIndicados = jogosIndicados;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getJogosIndicados() {
        return jogosIndicados;
    }

    public void setJogosIndicados(List<String> jogosIndicados) {
        this.jogosIndicados = jogosIndicados;
    }

}
