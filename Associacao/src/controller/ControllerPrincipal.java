package controller;

import view.TelaLogin;
import view.TelaPrincipal;
import model.UsuarioDAO;
import model.AssociadoDAO;

import javax.swing.*;

public class ControllerPrincipal {

    private TelaPrincipal tela;

    public ControllerPrincipal() {

        // 1. Instancia a tela oficial do sistema
        tela = new TelaPrincipal();

        // 2. Cria o DAO necessário para os dados de associados
        AssociadoDAO associadoDao = new AssociadoDAO();

        // 3. Inicializa o Controller responsável pela lógica de gravação do formulário
        if (tela.getPainelCadastro() != null && tela.getPainelSenha() != null) {
            new ControllerCadastroAssociado(
                    tela,
                    tela.getPainelCadastro(),
                    tela.getPainelSenha(),
                    associadoDao
            );
        }

        // 4. Configura os eventos complementares (como o botão Sair)
        eventos();

        // 5. Exibe a tela após tudo configurado e acoplado
        tela.setVisible(true);
    }

    private void eventos() {

        // Evento customizado para o menu de Associados (recarregar dados do banco ao clicar)
        tela.getBtAssociados().addActionListener(e -> {
            if (tela.getControllerListar() != null) {
                tela.getControllerListar().carregarAssociados();
            }
        });

        // Evento customizado para o menu Financeiro
        tela.getBtFinanceiro().addActionListener(e -> {
            // Caso queira efetuar lógicas adicionais como atualizar balanços ou logs ao abrir a aba
            System.out.println("Aba Movimentação Financeira acessada pelo usuário.");
        });

        // Evento de clique para o botão de Desconexão (Sair)
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