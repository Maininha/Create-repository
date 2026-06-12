package controller;

import view.PainelRedefinirSenhaGestor;
import model.UsuarioDAO;
import javax.swing.JOptionPane;

public class RedefinirSenhaController {

    private PainelRedefinirSenhaGestor tela;
    private UsuarioDAO dao;

    public RedefinirSenhaController(PainelRedefinirSenhaGestor tela) {
        this.tela = tela;
        this.dao = new UsuarioDAO();


        this.tela.getBtnAtualizar().addActionListener(e -> executarRedefinicao());
    }


    private void executarRedefinicao() {
        String cpf = tela.getTxtCpf().getText().trim();
        String novaSenha = new String(tela.getTxtNovaSenha().getPassword());
        String confirmacao = new String(tela.getTxtConfirmarNovaSenha().getPassword());

        if (cpf.isEmpty() || novaSenha.isEmpty() || confirmacao.isEmpty()) {
            JOptionPane.showMessageDialog(tela, "Por favor, preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!novaSenha.equals(confirmacao)) {
            JOptionPane.showMessageDialog(tela, "As senhas informadas não coincidem!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int resultado = dao.redefinirSenha(cpf, novaSenha);

        switch (resultado) {
            case 1:
                JOptionPane.showMessageDialog(tela, "Senha alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparCamposTela();
                break;
            case 0:
                JOptionPane.showMessageDialog(tela, "O CPF informado não foi encontrado no sistema.", "Erro", JOptionPane.ERROR_MESSAGE);
                break;
            case -1:
                JOptionPane.showMessageDialog(tela, "Ocorreu um erro interno no banco de dados.", "Erro Técnico", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    private void limparCamposTela() {
        tela.getTxtCpf().setText("");
        tela.getTxtNovaSenha().setText("");
        tela.getTxtConfirmarNovaSenha().setText("");
    }
}