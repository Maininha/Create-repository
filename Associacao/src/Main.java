//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.awt.*;
import javax.swing.*;

import model.Autenticacao;
import view.TelaLogin;
import controller.ControllerLogin;
import model.UsuarioDAO;

import controller.ControllerLogin;
import model.UsuarioDAO;
import view.TelaLogin;

public class Main {

    public static void main(String[] args) {

        System.out.println("APP INICIOU");

        TelaLogin view = new TelaLogin();
        UsuarioDAO dao = new UsuarioDAO();

        new ControllerLogin(view, dao);

        view.setVisible(true);
    }
}
