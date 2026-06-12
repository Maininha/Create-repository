package model;

public class Usuario extends Associado {

    private String senha;
    private String tipoPerfil;

    public Usuario() {
    }

    public Usuario(String cpf, String senha) {
        super();
        setCpf(cpf);
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoPerfil() {
        return tipoPerfil;
    }

    public void setTipoPerfil(String tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }
}