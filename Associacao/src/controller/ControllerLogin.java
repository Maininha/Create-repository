package controller;

import model.Usuario;
import model.UsuarioDAO;
import view.TelaLogin;

public class ControllerLogin {

    private TelaLogin view;
    private UsuarioDAO dao;

    public ControllerLogin(TelaLogin view, UsuarioDAO dao) {
        this.view = view;
        this.dao = dao;

        eventos();
    }

    private void eventos() {
        view.getBtEntrar().addActionListener(e -> autenticar());
        view.getBtLimpar().addActionListener(e -> view.limparCampos());

        // Garante que se o usuário apertar "Enter" dentro do campo de senha, o login seja disparado
        view.getTxSenha().addActionListener(e -> autenticar());
    }

    private void autenticar() {
        // Remove quaisquer pontos, traços ou espaços em branco do campo de usuário
        String cpf = view.getTxUsuario().getText().replaceAll("\\D", "").trim();
        String senha = new String(view.getTxSenha().getPassword()).trim();

        if (cpf.isEmpty() || senha.isEmpty()) {
            view.exibirMensagem("Preencha todos os campos!");
            return;
        }

        // Executa a nova autenticação com suporte a criptografia híbrida
        Usuario u = dao.autenticar(cpf, senha);

        if (u != null) {
            view.exibirMensagem("Login realizado com sucesso!");
            view.dispose(); // Fecha a tela de login com segurança

            // Abre o painel/controller principal do sistema
            new ControllerPrincipal();
        } else {
            view.exibirMensagem("CPF ou senha inválidos!");
        }
    }
}