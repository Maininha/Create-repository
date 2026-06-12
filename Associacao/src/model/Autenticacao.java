package model;
import java.util.ArrayList;

public class Autenticacao {
    private ArrayList<Usuario> usuarios;

    public Autenticacao() {
        this.usuarios = new ArrayList<>();

        usuarios.add(
                new Usuario(
                        "111.222.333-44",
                        "admin"
                )
        );
    }

    public boolean autenticar(String cpf, String senha){

        for(Usuario usuario : usuarios){

            if(usuario.getCpf().equals(cpf) &&
                    usuario.getSenha().equals(senha)){

                return true;
            }
        }

        return false;
    }
}

