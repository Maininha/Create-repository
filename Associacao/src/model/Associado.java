package model;

import java.sql.Timestamp;

public class Associado {

    private String nome;
    private String cpf;
    private Timestamp dataCadastro;
    private String tipoAssociado;
    private Endereco endereco;

    public Associado() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Timestamp getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Timestamp dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getTipoAssociado() {
        return tipoAssociado;
    }

    public void setTipoAssociado(String tipoAssociado) {
        this.tipoAssociado = tipoAssociado;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}