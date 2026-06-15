package model;

import java.util.Date;

public class Financeiro {

    private int idMov;
    private Date data;
    private double valor;
    private String desc;
    private String cat;
    private String tipo;
    private String cpfAssociado; // 🛠️ Nova propriedade para a Chave Estrangeira

    public Financeiro() {}

    public Financeiro(int idMov, Date data, double valor, String desc, String cat, String tipo, String cpfAssociado) {
        this.idMov = idMov;
        this.data = data;
        this.valor = valor;
        this.desc = desc;
        this.cat = cat;
        this.tipo = tipo;
        this.cpfAssociado = cpfAssociado;
    }

    // Getters e Setters
    public String getCpfAssociado() {
        return cpfAssociado;
    }

    public void setCpfAssociado(String cpfAssociado) {
        this.cpfAssociado = cpfAssociado;
    }

    public int getIdMov() {
        return idMov;
    }

    public void setIdMov(int idMov) {
        this.idMov = idMov;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}