package controller;

import view.TelaLogin;
import view.TelaPrincipal;
import model.UsuarioDAO;
import model.AssociadoDAO;

import javax.swing.*;

public class ControllerPrincipal {

    private TelaPrincipal tela;

    public ControllerPrincipal() {


        tela = new TelaPrincipal();


        AssociadoDAO associadoDao = new AssociadoDAO();


        if (tela.getPainelCadastro() != null && tela.getPainelSenha() != null) {
            new ControllerCadastroAssociado(
                    tela,
                    tela.getPainelCadastro(),
                    tela.getPainelSenha(),
                    associadoDao
            );
        }


        eventos();


        tela.setVisible(true);
    }

    private void eventos() {


        tela.getBtAssociados().addActionListener(e -> {
            new Thread(() -> {
                if (tela.getControllerListar() != null) {
                    tela.getControllerListar().carregarAssociados();
                }
            }).start();
        });


        tela.getBtFinanceiro().addActionListener(e -> {
            System.out.println("Aba Movimentação Financeira acessada pelo usuário.");
        });


        tela.getBtSair().addActionListener(e -> {

            int opcao = JOptionPane.showConfirmDialog(
                    tela,
                    "Deseja realmente sair?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcao == JOptionPane.YES_OPTION) {
                tela.dispose();

                TelaLogin view = new TelaLogin();
                UsuarioDAO dao = new UsuarioDAO();

                new ControllerLogin(view, dao);
                view.setVisible(true);
            }
        });
    }
}