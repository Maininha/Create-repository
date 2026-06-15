package model;

// Herança da classe pai 'Associado'
public class Usuario extends Associado {
    private int id;
    private String senha;

    private static Usuario usuarioLogado;

    // Construtor vazio essencial para o preenchimento do DAO
    public Usuario() {
        super(); // Chama o construtor da classe pai Associado
    }

    // Construtor com parâmetros para uso em outras partes do sistema
    public Usuario(String cpf, String senha) {
        this(); // Chama o construtor vazio local
        this.setCpf(cpf); // Define o CPF usando o método herdado de Associado
        this.senha = senha;
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}