package controller;

import model.Associado;
import model.AssociadoDAO;
import model.Endereco;
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

        // Captura o ArrayList compatível retornado pelo DAO
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

        // Limpa listeners antigos do painel editar para evitar duplicações de ações na memória
        for (java.awt.event.ActionListener al : editar.getBtnSalvar().getActionListeners()) {
            editar.getBtnSalvar().removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : editar.getBtnCancelar().getActionListeners()) {
            editar.getBtnCancelar().removeActionListener(al);
        }

        // ===================================
        // AÇÕES DA TABELA DE VISUALIZAÇÃO
        // ===================================
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

        // =========================
        // BOTÃO SALVAR (EDIÇÃO)
        // =========================
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

            Associado a = new Associado();
            a.setCpf(cpf);
            a.setNome(nome);
            a.setEndereco(end);
            a.setTipoAssociado("Associado");

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

        // =========================
        // CANCELAR EDIÇÃO
        // =========================
        editar.getBtnCancelar().addActionListener(e -> {
            frame.getCard().show(
                    frame.getPainelConteudo(),
                    "listarAssociados"
            );
        });
    }
}