package controller;

import model.Usuario;
import model.Endereco;
import model.AssociadoDAO;
import view.PainelCadastroAssociado;
import view.PainelCriarUsuarioSenha;
import view.TelaPrincipal;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ControllerCadastroAssociado {

    private TelaPrincipal telaCadastro;
    private PainelCadastroAssociado painelCadastro;
    private PainelCriarUsuarioSenha painelSenha;
    private AssociadoDAO dao;

    private String nomeTemp;
    private String cpfTemp;
    private Endereco enderecoTemp;

    private ActionListener acaoCadastrar;
    private ActionListener acaoFinalizar;

    public ControllerCadastroAssociado(
            TelaPrincipal telaCadastro,
            PainelCadastroAssociado painelCadastro,
            PainelCriarUsuarioSenha painelSenha,
            AssociadoDAO dao
    ) {
        this.telaCadastro = telaCadastro;
        this.painelCadastro = painelCadastro;
        this.painelSenha = painelSenha;
        this.dao = dao;

        configurarEventos();
    }

    private void configurarEventos() {
        if (acaoCadastrar != null) {
            painelCadastro.getBtnCadastrar().removeActionListener(acaoCadastrar);
        }
        if (acaoFinalizar != null) {
            painelSenha.getBtnFinalizar().removeActionListener(acaoFinalizar);
        }

        for (ActionListener al : painelCadastro.getBtnCadastrar().getActionListeners()) {
            painelCadastro.getBtnCadastrar().removeActionListener(al);
        }
        for (ActionListener al : painelSenha.getBtnFinalizar().getActionListeners()) {
            painelSenha.getBtnFinalizar().removeActionListener(al);
        }

        acaoCadastrar = e -> processarPrimeiraEtapa();
        acaoFinalizar = e -> finalizarCadastroGestor();

        painelCadastro.getBtnCadastrar().addActionListener(acaoCadastrar);
        painelSenha.getBtnFinalizar().addActionListener(acaoFinalizar);
    }

    private void processarPrimeiraEtapa() {
        String nome = painelCadastro.getTxtNome().getText().trim();
        String cpfRaw = painelCadastro.getTxtCpf().getText().trim();
        String logradouro = painelCadastro.getTxtLogradouro().getText().trim();
        String city = painelCadastro.getTxtCidade().getText().trim();
        String estado = painelCadastro.getTxtEstado().getText().trim();
        String referencia = painelCadastro.getTxtReferencia().getText().trim();

        if (nome.isEmpty() || cpfRaw.isEmpty() || logradouro.isEmpty() || city.isEmpty() || estado.isEmpty()) {
            JOptionPane.showMessageDialog(
                    telaCadastro,
                    "Preencha todos os campos obrigatórios!",
                    "Campos Vazios",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Padroniza removendo pontos e traços
        String cpf = cpfRaw.replaceAll("[^0-9]", "");
        if (cpf.length() > 11) {
            cpf = cpf.substring(0, 11);
        }

        // CORREÇÃO CRÍTICA [RF003]: Validação do formato e estrutura matemática do CPF
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            JOptionPane.showMessageDialog(
                    telaCadastro,
                    "O CPF informado é inválido! Digite um CPF com 11 dígitos válidos.",
                    "Formato de CPF Inválido",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (dao.existeCpf(cpf)) {
            JOptionPane.showMessageDialog(
                    telaCadastro,
                    "Não é possível cadastrar! O CPF '" + cpfRaw + "' já se encontra registrado no sistema.",
                    "CPF Duplicado",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        nomeTemp = nome;
        cpfTemp = cpf;
        enderecoTemp = new Endereco(city, estado, referencia, logradouro);

        if (painelCadastro.getRbAssociado().isSelected()) {
            Usuario u = new Usuario(cpfTemp, "");
            u.setNome(nomeTemp);
            u.setTipoPerfil("Associado");
            u.setEndereco(enderecoTemp);

            boolean ok = dao.inserir(u);

            if (ok) {
                JOptionPane.showMessageDialog(telaCadastro, "Associado cadastrado com sucesso!");
                atualizarListagemTelas();
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(telaCadastro, "Erro ao salvar associado!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (painelCadastro.getRbGestor().isSelected()) {
            if (telaCadastro != null && telaCadastro.getCard() != null) {
                telaCadastro.getCard().show(telaCadastro.getPainelConteudo(), "criarUsuarioSenha");
                telaCadastro.getPainelConteudo().revalidate();
                telaCadastro.getPainelConteudo().repaint();
            }
        }
    }

    private void finalizarCadastroGestor() {
        String _senha = new String(painelSenha.getTxtSenha().getPassword()).trim();
        String confirmar = new String(painelSenha.getTxtConfirmarSenha().getPassword()).trim();

        if (_senha.isEmpty() || confirmar.isEmpty()) {
            JOptionPane.showMessageDialog(telaCadastro, "Preencha os campos de senha obrigatórios!", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!_senha.equals(confirmar)) {
            JOptionPane.showMessageDialog(telaCadastro, "As senhas digitadas não conferem!", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario u = new Usuario(cpfTemp, _senha);
        u.setNome(nomeTemp);
        u.setTipoPerfil("Gestor");
        u.setEndereco(enderecoTemp);

        boolean ok = dao.inserir(u);

        if (ok) {
            JOptionPane.showMessageDialog(telaCadastro, "Gestor cadastrado com sucesso!");

            atualizarListagemTelas();
            limparFormulario();

            painelSenha.getTxtSenha().setText("");
            painelSenha.getTxtConfirmarSenha().setText("");

            if (telaCadastro != null && telaCadastro.getCard() != null) {
                telaCadastro.getCard().show(telaCadastro.getPainelConteudo(), "listarAssociados");
                telaCadastro.getPainelConteudo().revalidate();
                telaCadastro.getPainelConteudo().repaint();
            }
        } else {
            JOptionPane.showMessageDialog(telaCadastro, "Erro ao salvar gestor no banco de dados!", "Erro de Execução", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarListagemTelas() {
        if (telaCadastro != null && telaCadastro.getControllerListar() != null) {
            telaCadastro.getControllerListar().carregarAssociados();
        }
    }

    private void limparFormulario() {
        painelCadastro.getTxtNome().setText("");
        painelCadastro.getTxtCpf().setText("");
        painelCadastro.getTxtLogradouro().setText("");
        painelCadastro.getTxtCidade().setText("");
        painelCadastro.getTxtEstado().setText("");
        painelCadastro.getTxtReferencia().setText("");
        painelCadastro.getRbAssociado().setSelected(true);

        nomeTemp = null;
        cpfTemp = null;
        enderecoTemp = null;
    }
}