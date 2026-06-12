package model;

import java.util.Date;

public class Atividade {
    private Date dataHora;
    private String category; // Caso prefira manter como categoria
    private String categoria;
    private String descricao;

    public Atividade(Date dataHora, String categoria, String descricao) {
        this.dataHora = dataHora;
        this.categoria = categoria;
        this.descricao = descricao;
    }

    public Date getDataHora() { return dataHora; }
    public void setDataHora(Date dataHora) { this.dataHora = dataHora; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}