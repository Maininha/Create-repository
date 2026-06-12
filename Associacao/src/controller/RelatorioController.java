package controller;

import model.Atividade;
import model.Relatorio;
import model.RelatorioDAO;
import util.GeradorPdfRelatorio;

import javax.swing.*;
import java.util.Date;
import java.util.List;

public class RelatorioController {

    private RelatorioDAO relatorioDAO;

    public RelatorioController() {
        this.relatorioDAO = new RelatorioDAO();
    }

    public Relatorio gerarRelatorioPorPeriodo(Date inicio, Date fim, String tipo) {
        Relatorio relatorio = new Relatorio();
        relatorio.setPeriodoInicial(inicio);
        relatorio.setPeriodoFinal(fim);

        // Calcula entradas, saídas e saldo de forma otimizada no banco
        relatorioDAO.preencherDadosFinanceiros(relatorio);

        // Salva no histórico da tabela relatorio
        relatorioDAO.salvarNoBanco(relatorio);

        return relatorio;
    }

    public void exportarPDF(Relatorio relatorio) {
        if (relatorio == null) {
            JOptionPane.showMessageDialog(null, "Nenhum relatório foi gerado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        relatorio.emitirPDF();
    }

    /**
     * 🔥 ALINHAMENTO DE ARQUITETURA: Agora utiliza o GeradorPdfRelatorio nativo (OpenPDF)
     * mantendo a paleta de cores marrom/dourada e rodando em background seguro.
     */
    public void exportarHistoricoAtividadesPDF(List<Atividade> listaAtividades, JButton botaoGatilho) {
        if (botaoGatilho != null) {
            botaoGatilho.setEnabled(false);
            botaoGatilho.setText("Processando...");
        }

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private boolean sucesso = true;
            private String erroMsg = "";

            @Override
            protected Void doInBackground() {
                try {
                    // Invoca o gerador customizado baseado na paleta visual corporativa
                    GeradorPdfRelatorio.gerarRelatorioAtividades(listaAtividades);
                } catch (Exception e) {
                    sucesso = false;
                    erroMsg = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (botaoGatilho != null) {
                    botaoGatilho.setEnabled(true);
                    botaoGatilho.setText("Visualizar relatório");
                }

                if (!sucesso) {
                    JOptionPane.showMessageDialog(null,
                            "Erro ao exportar histórico de atividades: " + erroMsg,
                            "Erro de Exportação", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}