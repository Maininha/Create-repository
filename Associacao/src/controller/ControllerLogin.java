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
    }

    private void autenticar() {

        // 🔥 remove pontos e traços do CPF
        String cpf = view.getTxUsuario().getText().replaceAll("\\D", "");
        String senha = new String(view.getTxSenha().getPassword());

        if (cpf.isEmpty() || senha.isEmpty()) {
            view.exibirMensagem("Preencha todos os campos!");
            return;
        }

        Usuario u = dao.autenticar(cpf, senha);

        if (u != null) {
            view.exibirMensagem("Login realizado com sucesso!");

            view.dispose();

            new ControllerPrincipal();

        } else {
            view.exibirMensagem("CPF ou senha inválidos!");
        }
    }
}