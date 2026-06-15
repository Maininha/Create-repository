package controller;

import model.Associado;
import model.AssociadoDAO;
import model.Endereco;
import model.Usuario; // ✏️ IMPORTANTE: Importar o modelo Usuario
import view.PainelAssociados;
import view.PainelEditarAssociado;
import view.TelaPrincipal;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ControllerListarAssociado {

    private PainelAssociados painel;
    private PainelEditarAssociado editar;
    private TelaPrincipal frame;
    private AssociadoDAO dao;

    public ControllerListarAssociado(
            PainelAssociados painel,
            PainelEditarAssociado editar,
            TelaPrincipal frame,
            AssociadoDAO dao
    ) {
        this.painel = painel;
        this.editar = editar;
        this.frame = frame;
        this.dao = dao;

        carregarAssociados();
        eventos();
    }

    public void carregarAssociados() {
        painel.limparTabela();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        ArrayList<Associado> listaAssociados = dao.listar();
        if (listaAssociados == null) return;

        for (Associado a : listaAssociados) {

            String data = (a.getDataCadastro() != null)
                    ? sdf.format(a.getDataCadastro())
                    : "";

            String endereco = (a.getEndereco() != null)
                    ? a.getEndereco().getLogradouro()
                    : "";

            painel.adicionarLinha(new Object[]{
                    a.getNome(),
                    a.getCpf(),
                    endereco,
                    data
            });
        }
    }

    private void eventos() {


        for (java.awt.event.ActionListener al : editar.getBtnSalvar().getActionListeners()) {
            editar.getBtnSalvar().removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : editar.getBtnCancelar().getActionListeners()) {
            editar.getBtnCancelar().removeActionListener(al);
        }

        painel.setAcoesListener(new PainelAssociados.AcoesListener() {

            @Override
            public void editar(int rowModel) {
                if (rowModel < 0) return;

                String cpf = painel.getTabela()
                        .getModel()
                        .getValueAt(rowModel, 1)
                        .toString();

                Associado a = dao.buscarPorCpf(cpf);
                if (a == null) return;

                editar.getTxtCpf().setText(a.getCpf());
                editar.getTxtNome().setText(a.getNome());

                if (a.getEndereco() != null) {
                    editar.getTxtLogradouro().setText(a.getEndereco().getLogradouro());
                    editar.getTxtCidade().setText(a.getEndereco().getCidade());
                    editar.getTxtEstado().setText(a.getEndereco().getEstado());
                    editar.getTxtReferencia().setText(a.getEndereco().getReferencia());
                }

                frame.getCard().show(
                        frame.getPainelConteudo(),
                        "editarAssociado"
                );

                frame.getPainelConteudo().revalidate();
                frame.getPainelConteudo().repaint();
            }

            @Override
            public void excluir(int rowModel) {
                if (rowModel < 0) return;

                String cpf = painel.getTabela()
                        .getModel()
                        .getValueAt(rowModel, 1)
                        .toString();


                Usuario logado = Usuario.getUsuarioLogado();
                if (logado != null && cpf.equals(logado.getCpf())) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Você não pode excluir o seu próprio usuário logado!",
                            "Ação Negada",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int resp = JOptionPane.showConfirmDialog(
                        frame,
                        "Tem certeza que deseja excluir este associado?",
                        "Confirmar Exclusão",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (resp == JOptionPane.YES_OPTION) {
                    if (dao.excluir(cpf)) {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Registro excluído com sucesso!"
                        );
                        carregarAssociados();
                    } else {
                        JOptionPane.showMessageDialog(
                                frame,
                                "Erro ao excluir registro.",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });

        editar.getBtnSalvar().addActionListener(e -> {
            String cpf = editar.getTxtCpf().getText();
            String nome = editar.getTxtNome().getText();

            String logradouro = editar.getTxtLogradouro().getText();
            String cidade = editar.getTxtCidade().getText();
            String estado = editar.getTxtEstado().getText();
            String referencia = editar.getTxtReferencia().getText();

            if (nome.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        frame,
                        "O campo Nome é obrigatório",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Endereco end = new Endereco(cidade, estado, referencia, logradouro);


            Associado original = dao.buscarPorCpf(cpf);
            Associado a;

            if (original instanceof Usuario) {
                // Se era um usuário/gestor, mantemos como Usuario para não perder a senha
                Usuario u = new Usuario();
                u.setSenha(((Usuario) original).getSenha());
                u.setId(((Usuario) original).getId());
                a = u;
                a.setTipoAssociado(original.getTipoAssociado()); // Mantém se era "Gestor", etc.
            } else {
                // Se era um associado comum, continua sendo
                a = new Associado();
                a.setTipoAssociado("Associado");
            }

            a.setCpf(cpf);
            a.setNome(nome);
            a.setEndereco(end);
            if (original != null) {
                a.setDataCadastro(original.getDataCadastro()); // Preserva a data original de cadastro
            }

            boolean ok = dao.atualizarCompleto(a);

            if (ok) {
                JOptionPane.showMessageDialog(frame, "Atualizado com sucesso!");
                carregarAssociados();

                frame.getCard().show(
                        frame.getPainelConteudo(),
                        "listarAssociados"
                );
            } else {
                JOptionPane.showMessageDialog(
                        frame,
                        "Erro ao atualizar",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        editar.getBtnCancelar().addActionListener(e -> {
            frame.getCard().show(
                    frame.getPainelConteudo(),
                    "listarAssociados"
            );
        });
    }
}