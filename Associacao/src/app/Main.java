package app;

import view.TelaLogin;
import controller.ControllerLogin;
import model.UsuarioDAO;

public class Main {

    public static void main(String[] args) {

        System.out.println("APP INICIOU");

        TelaLogin view = new TelaLogin();
        UsuarioDAO dao = new UsuarioDAO();

        new ControllerLogin(view, dao);

        view.setVisible(true);
    }
}
