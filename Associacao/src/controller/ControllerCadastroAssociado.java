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

    // Referências explícitas para controle seguro de listeners do Swing
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
        // 1. Remove as ações anteriores mapeadas por este escopo de controller
        if (acaoCadastrar != null) {
            painelCadastro.getBtnCadastrar().removeActionListener(acaoCadastrar);
        }
        if (acaoFinalizar != null) {
            painelSenha.getBtnFinalizar().removeActionListener(acaoFinalizar);
        }

        // 2. Limpa de forma absoluta qualquer listener órfão persistido nas Views estáticas
        for (ActionListener al : painelCadastro.getBtnCadastrar().getActionListeners()) {
            painelCadastro.getBtnCadastrar().removeActionListener(al);
        }
        for (ActionListener al : painelSenha.getBtnFinalizar().getActionListeners()) {
            painelSenha.getBtnFinalizar().removeActionListener(al);
        }

        // 3. Instancia as lógicas operacionais de clique
        acaoCadastrar = e -> processarPrimeiraEtapa();
        acaoFinalizar = e -> finalizarCadastroGestor();

        // 4. Vincula as novas ações limpas aos respectivos botões
        painelCadastro.getBtnCadastrar().addActionListener(acaoCadastrar);
        painelSenha.getBtnFinalizar().addActionListener(acaoFinalizar);
    }

    // ================= CADASTRO ETAPA 1 =================
    private void processarPrimeiraEtapa() {

        String nome = painelCadastro.getTxtNome().getText().trim();
        String cpf = painelCadastro.getTxtCpf().getText().trim();
        String logradouro = painelCadastro.getTxtLogradouro().getText().trim();
        String city = painelCadastro.getTxtCidade().getText().trim();
        String estado = painelCadastro.getTxtEstado().getText().trim();
        String referencia = painelCadastro.getTxtReferencia().getText().trim();

        // Validação de campos obrigatórios
        if (nome.isEmpty() || cpf.isEmpty() || logradouro.isEmpty() || city.isEmpty() || estado.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Preencha todos os campos obrigatórios!",
                    "Erro",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 🛑 REGRA DE SEGURANÇA: Bloqueia o avanço imediatamente se o CPF já existir na base
        if (dao.existeCpf(cpf)) {
            JOptionPane.showMessageDialog(
                    null,
                    "Não é possível cadastrar! O CPF '" + cpf + "' já se encontra registrado no sistema.",
                    "CPF Duplicado",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Armazena temporariamente os dados validados em memória
        nomeTemp = nome;
        cpfTemp = cpf;
        enderecoTemp = new Endereco(city, estado, referencia, logradouro);

        // CASO 1: ASSOCIADO NORMAL (Salva direto no banco)
        if (painelCadastro.getRbAssociado().isSelected()) {

            Usuario u = new Usuario(cpfTemp, "");
            u.setNome(nomeTemp);
            u.setTipoPerfil("Associado");
            u.setEndereco(enderecoTemp);

            boolean ok = dao.inserir(u);

            if (ok) {
                JOptionPane.showMessageDialog(null, "Associado cadastrado com sucesso!");

                // Sincroniza a JTable de listagem em tempo real
                if (telaCadastro != null && telaCadastro.getControllerListar() != null) {
                    telaCadastro.getControllerListar().carregarAssociados();
                }

                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(null, "Erro ao salvar associado!");
            }
        }

        // CASO 2: GESTOR → Transiciona para a tela de criação de credenciais (Senha)
        else if (painelCadastro.getRbGestor().isSelected()) {

            telaCadastro.getCard().show(
                    telaCadastro.getPainelConteudo(),
                    "criarSenha"
            );

            // Redesenha a árvore gráfica de componentes imediatamente
            telaCadastro.getPainelConteudo().revalidate();
            telaCadastro.getPainelConteudo().repaint();
        }
    }

    // ================= FINALIZA GESTOR (ETAPA 2) =================
    private void finalizarCadastroGestor() {

        String senha = new String(painelSenha.getTxtSenha().getPassword()).trim();
        String confirmar = new String(painelSenha.getTxtConfirmarSenha().getPassword()).trim();

        if (senha.isEmpty() || confirmar.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Preencha a senha!");
            return;
        }

        if (!senha.equals(confirmar)) {
            JOptionPane.showMessageDialog(null, "Senhas não conferem!");
            return;
        }

        // Monta o objeto completo unindo os dados da Etapa 1 + Senha da Etapa 2
        Usuario u = new Usuario(cpfTemp, senha);
        u.setNome(nomeTemp);
        u.setTipoPerfil("Gestor");
        u.setEndereco(enderecoTemp);

        boolean ok = dao.inserir(u);

        if (ok) {
            JOptionPane.showMessageDialog(null, "Gestor cadastrado com sucesso!");

            // Atualiza a tabela geral de visualização
            if (telaCadastro != null && telaCadastro.getControllerListar() != null) {
                telaCadastro.getControllerListar().carregarAssociados();
            }

            // Reseta todos os formulários e campos de texto
            limparFormulario();
            painelSenha.getTxtSenha().setText("");
            painelSenha.getTxtConfirmarSenha().setText("");

            // Redireciona o fluxo de volta para o painel inicial de cadastros
            telaCadastro.getCard().show(
                    telaCadastro.getPainelConteudo(),
                    "cadastros"
            );

            telaCadastro.getPainelConteudo().revalidate();
            telaCadastro.getPainelConteudo().repaint();
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao salvar gestor!",
                    "Erro de Execução",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= LIMPAR CAMPOS =================
    private void limparFormulario() {
        painelCadastro.getTxtNome().setText("");
        painelCadastro.getTxtCpf().setText("");
        painelCadastro.getTxtLogradouro().setText("");
        painelCadastro.getTxtCidade().setText("");
        painelCadastro.getTxtEstado().setText("");
        painelCadastro.getTxtReferencia().setText("");
        painelCadastro.getRbAssociado().setSelected(true);
    }
}