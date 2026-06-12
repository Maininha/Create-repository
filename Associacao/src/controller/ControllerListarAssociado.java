package controller;

import model.Associado;
import model.AssociadoDAO;
import model.Endereco;
import view.PainelAssociados;
import view.PainelEditarAssociado;
import view.TelaPrincipal;

import javax.swing.*;
import java.text.SimpleDateFormat;

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

        for (Associado a : dao.listar()) {
            String data = a.getDataCadastro() != null ? sdf.format(a.getDataCadastro()) : "";
            String endereco = a.getEndereco() != null ? a.getEndereco().getLogradouro() : "";

            painel.adicionarLinha(new Object[]{
                    a.getNome(),
                    a.getCpf(),
                    endereco,
                    data
            });
        }
    }

    private void eventos() {
        // Limpa os listeners antigos para evitar execuções duplicadas ao salvar
        for (java.awt.event.ActionListener al : editar.getBtnSalvar().getActionListeners()) {
            editar.getBtnSalvar().removeActionListener(al);
        }
        for (java.awt.event.ActionListener al : editar.getBtnCancelar().getActionListeners()) {
            editar.getBtnCancelar().removeActionListener(al);
        }

        // Configura as ações de Editar e Excluir disparadas pela View
        painel.setAcoesListener(new PainelAssociados.AcoesListener() {
            @Override
            public void editar(int rowModel) {
                if (rowModel < 0) return;

                // 🔥 SOLUÇÃO DO ERRO: Acessa o arquivo direto do MODELO (.getModel())
                // Isso impede o Swing de tentar re-filtrar o índice que a View já tratou de forma absoluta.
                String cpf = painel.getTabela().getModel().getValueAt(rowModel, 1).toString();

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

                frame.getCard().show(frame.getPainelConteudo(), "editar");
            }

            @Override
            public void excluir(int rowModel) {
                if (rowModel < 0) return;

                // 🔥 SOLUÇÃO DO ERRO: Lendo o dado bruto direto do MODELO estável da tabela
                String cpf = painel.getTabela().getModel().getValueAt(rowModel, 1).toString();

                Object[] opcoes = {"Sim", "Não"};

                int resp = JOptionPane.showOptionDialog(
                        frame,
                        "Tem certeza que deseja excluir este associado?",
                        "Confirmar Exclusão",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        opcoes,
                        opcoes[0]
                );

                if (resp == JOptionPane.YES_OPTION) {
                    if (dao.excluir(cpf)) {
                        JOptionPane.showMessageDialog(frame, "Registro excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        carregarAssociados();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Não foi possível excluir o registro.", "Erro de Operação", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Evento do botão Salvar da tela de Edição
        editar.getBtnSalvar().addActionListener(e -> {
            String cpf = editar.getTxtCpf().getText();
            String nome = editar.getTxtNome().getText();
            String logradouro = editar.getTxtLogradouro().getText();
            String cidade = editar.getTxtCidade().getText();
            String estado = editar.getTxtEstado().getText();
            String referencia = editar.getTxtReferencia().getText();

            if (nome.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "O campo 'Nome' é obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(frame, "Dados atualizados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarAssociados();
                frame.getCard().show(frame.getPainelConteudo(), "associados");
            } else {
                JOptionPane.showMessageDialog(frame, "Erro ao atualizar os dados do associado.", "Erro de Operação", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Evento do botão Cancelar da tela de Edição
        editar.getBtnCancelar().addActionListener(e -> {
            frame.getCard().show(frame.getPainelConteudo(), "associados");
        });
    }
}