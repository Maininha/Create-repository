package app;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

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
