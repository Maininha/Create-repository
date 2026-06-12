package model;

public class Endereco {
    private String cidade;
    private String estado;
    private String referencia;
    private String logradouro;

    public Endereco(String cidade, String estado, String referencia, String logradouro) {
        this.cidade = cidade;
        this.estado = estado;
        this.referencia = referencia;
        this.logradouro = logradouro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }
}
