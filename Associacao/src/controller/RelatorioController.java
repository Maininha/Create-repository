package controller;

import model.Atividade;
import model.Relatorio;
import model.RelatorioDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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

        // Calcula entradas, saídas e saldo
        relatorioDAO.preencherDadosFinanceiros(relatorio);

        // Salva no histórico da tabela relatorio
        relatorioDAO.salvarNoBanco(relatorio);

        return relatorio;
    }

    public Relatorio generateRelatorioPorPeriodo(Date inicio, Date fim, String tipo) {
        return gerarRelatorioPorPeriodo(inicio, fim, tipo);
    }

    public void exportarPDF(Relatorio relatorio) {

        if (relatorio == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Nenhum relatório foi gerado.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        relatorio.emitirPDF();
    }

    public void exportarHistoricoAtividadesPDF(List<Atividade> listaAtividades) {

        String[] colunas = {
                "Data / Hora",
                "Módulo / Categoria",
                "Descrição Detalhada"
        };

        DefaultTableModel modeloTabela =
                new DefaultTableModel(colunas, 0);

        SimpleDateFormat formatoData =
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        for (Atividade atividade : listaAtividades) {

            modeloTabela.addRow(new Object[]{
                    formatoData.format(atividade.getDataHora()),
                    atividade.getCategoria(),
                    atividade.getDescricao()
            });
        }

        JTable tabela = new JTable(modeloTabela);

        try {

            MessageFormat cabecalho =
                    new MessageFormat("Histórico de Atividades");

            MessageFormat rodape =
                    new MessageFormat("Página {0}");

            tabela.print(
                    JTable.PrintMode.FIT_WIDTH,
                    cabecalho,
                    rodape
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Erro ao imprimir: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}