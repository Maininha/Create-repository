package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.plaf.basic.BasicScrollBarUI;

import controller.RelatorioController;
import model.Financeiro;
import model.FinanceiroDAO;
import model.Relatorio;
import util.GeradorPdfRelatorio;

public class PainelResumoFinanceiro extends JPanel {

    private static PainelResumoFinanceiro instanciaAtiva;

    private JLabel lblValorEntrada;
    private JLabel lblValorSaida;
    private JLabel lblValorSaldo;
    private JTable tableResumo;
    private JTable tableMov;
    private JComboBox<String> cbPeriodo;
    private JButton btnGerarRelatorio;

    private RelatorioController relatorioController;

    public PainelResumoFinanceiro() {
        instanciaAtiva = this;
        this.relatorioController = new RelatorioController();

        setLayout(new BorderLayout());
        setBackground(new Color(248, 245, 240));

        JPanel painelFinanceiroUnico = inicializarInterfaceFinanceira();
        add(painelFinanceiroUnico, BorderLayout.CENTER);

        recarregarDadosBanco();
    }

    public static void dispararAtualizacaoAutomatica() {
        if (instanciaAtiva != null) {
            SwingUtilities.invokeLater(() -> instanciaAtiva.recarregarDadosBanco());
        }
    }

    private JPanel inicializarInterfaceFinanceira() {
        JPanel abaFin = new JPanel(null);
        abaFin.setBackground(new Color(248, 245, 240));

        JLabel titulo = new JLabel("Resumo Financeiro");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(70, 40, 15));
        titulo.setBounds(40, 25, 400, 40);
        abaFin.add(titulo);

        JLabel subtitulo = new JLabel("Visualize indicadores e movimentações financeiras.");
        subtitulo.setForeground(Color.GRAY);
        subtitulo.setBounds(40, 60, 400, 20);
        abaFin.add(subtitulo);

        JLabel lbPeriodo = new JLabel("Período");
        lbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbPeriodo.setForeground(Color.GRAY);
        lbPeriodo.setBounds(40, 95, 100, 20);
        abaFin.add(lbPeriodo);

        String[] opcoesPeriodo = {"Este mês", "Últimos 30 dias", "Este ano"};
        cbPeriodo = new JComboBox<>(opcoesPeriodo);
        cbPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbPeriodo.setBackground(Color.WHITE);
        cbPeriodo.setBounds(40, 120, 280, 40);
        cbPeriodo.setBorder(new LineBorder(new Color(180, 180, 180), 1));
        abaFin.add(cbPeriodo);

        btnGerarRelatorio = new JButton("Gerar Relatório");
        btnGerarRelatorio.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGerarRelatorio.setBackground(new Color(205, 145, 55));
        btnGerarRelatorio.setForeground(Color.WHITE);
        btnGerarRelatorio.setBorder(null);
        btnGerarRelatorio.setFocusPainted(false);
        btnGerarRelatorio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGerarRelatorio.setBounds(335, 120, 160, 40);
        abaFin.add(btnGerarRelatorio);

        lblValorEntrada = new JLabel("R$ 0,00");
        criarCard(abaFin, "Total de entrada", lblValorEntrada, new Color(90, 150, 40), 40, 185);

        lblValorSaida = new JLabel("R$ 0,00");
        criarCard(abaFin, "Total saída", lblValorSaida, Color.RED, 410, 185);

        lblValorSaldo = new JLabel("R$ 0,00");
        criarCard(abaFin, "Saldo atual", lblValorSaldo, Color.BLUE, 780, 185);

        JLabel atualizacao = new JLabel("Última atualização.");
        atualizacao.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        atualizacao.setForeground(Color.GRAY);
        atualizacao.setBounds(40, 315, 400, 20);
        abaFin.add(atualizacao);

        JLabel tituloResumo = new JLabel("Resumo do Período");
        tituloResumo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloResumo.setForeground(new Color(35, 18, 4));
        tituloResumo.setBounds(40, 340, 250, 25);
        abaFin.add(tituloResumo);

        JPanel containerResumo = new JPanel(null);
        containerResumo.setBounds(40, 370, 520, 250);
        containerResumo.setBackground(Color.WHITE);
        containerResumo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String[] colunasResumo = {"Descrição", "Valor"};
        tableResumo = estilizarTabela(new Object[0][2], colunasResumo);

        JScrollPane scrollResumo = new JScrollPane(tableResumo);
        scrollResumo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollResumo.setBorder(null);
        scrollResumo.setBounds(10, 10, 495, 230);
        scrollResumo.setBackground(Color.WHITE);
        scrollResumo.getViewport().setBackground(Color.WHITE);
        scrollResumo.getVerticalScrollBar().setUI(new ScrollBarProfissionalUI());
        scrollResumo.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        containerResumo.add(scrollResumo);
        abaFin.add(containerResumo);

        JLabel tituloMov = new JLabel("Últimas Movimentações");
        tituloMov.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloMov.setForeground(new Color(35, 18, 4));
        tituloMov.setBounds(600, 340, 250, 25);
        abaFin.add(tituloMov);

        JPanel containerMovimentacoes = new JPanel(null);
        containerMovimentacoes.setBounds(600, 370, 520, 250);
        containerMovimentacoes.setBackground(Color.WHITE);
        containerMovimentacoes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String[] colunasMov = {"Data", "Tipo", "Valor"};
        tableMov = estilizarTabela(new Object[0][3], colunasMov);

        JScrollPane scrollMov = new JScrollPane(tableMov);
        scrollMov.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMov.setBorder(null);
        scrollMov.setBounds(10, 10, 495, 230);
        scrollMov.setBackground(Color.WHITE);
        scrollMov.getViewport().setBackground(Color.WHITE);
        scrollMov.getVerticalScrollBar().setUI(new ScrollBarProfissionalUI());
        scrollMov.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        containerMovimentacoes.add(scrollMov);
        abaFin.add(containerMovimentacoes);

        cbPeriodo.addActionListener(e -> recarregarDadosBanco());
        btnGerarRelatorio.addActionListener(e -> acionarGeracaoPDF());

        return abaFin;
    }

    private void acionarGeracaoPDF() {
        btnGerarRelatorio.setEnabled(false);
        btnGerarRelatorio.setText("Processando...");

        String itemSelecionado = (String) cbPeriodo.getSelectedItem();
        Calendar calendar = Calendar.getInstance();
        Date dataFim = calendar.getTime();

        if ("Últimos 30 dias".equals(itemSelecionado)) {
            calendar.add(Calendar.DAY_OF_YEAR, -30);
        } else if ("Este ano".equals(itemSelecionado)) {
            calendar.set(Calendar.DAY_OF_YEAR, 1);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        Date dataInicio = calendar.getTime();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private boolean sucesso = true;
            private String mensagemErro = "";

            @Override
            protected Void doInBackground() {
                try {
                    relatorioController.gerarRelatorioPorPeriodo(dataInicio, dataFim, "Mensal");

                    FinanceiroDAO financeiroDAO = new FinanceiroDAO();
                    List<Financeiro> movimentacoesPeriodo = financeiroDAO.listar("Todos", dataInicio, dataFim);

                    if (movimentacoesPeriodo != null) {
                        GeradorPdfRelatorio.gerarRelatorioFinanceiro(movimentacoesPeriodo);
                    }
                } catch (Exception e) {
                    sucesso = false;
                    mensagemErro = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                btnGerarRelatorio.setEnabled(true);
                btnGerarRelatorio.setText("Gerar Relatório");

                if (sucesso) {
                    JOptionPane.showMessageDialog(PainelResumoFinanceiro.this,
                            "PDF exportado com sucesso!\nVerifique a sua pasta de Downloads.",
                            "Exportação Realizada", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(PainelResumoFinanceiro.this,
                            "Erro ao exportar o documento: " + mensagemErro,
                            "Erro de Exportação", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    public void recarregarDadosBanco() {
        String itemSelecionado = (String) cbPeriodo.getSelectedItem();
        Calendar calendar = Calendar.getInstance();
        Date dataFim = calendar.getTime();

        if ("Últimos 30 dias".equals(itemSelecionado)) {
            calendar.add(Calendar.DAY_OF_YEAR, -30);
        } else if ("Este ano".equals(itemSelecionado)) {
            calendar.set(Calendar.DAY_OF_YEAR, 1);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        Date dataInicio = calendar.getTime();

        SwingWorker<Relatorio, Void> worker = new SwingWorker<>() {
            private List<Financeiro> listaMovimentos;

            @Override
            protected Relatorio doInBackground() throws Exception {
                FinanceiroDAO financeiroDAO = new FinanceiroDAO();
                listaMovimentos = financeiroDAO.listar("Todos", dataInicio, dataFim);
                return relatorioController.gerarRelatorioPorPeriodo(dataInicio, dataFim, "Mensal");
            }

            @Override
            protected void done() {
                try {
                    Relatorio relatorio = get();

                    lblValorEntrada.setText(String.format("R$ %.2f", relatorio.getTotalEntradas()));
                    lblValorSaida.setText(String.format("R$ %.2f", relatorio.getTotalSaidas()));
                    lblValorSaldo.setText(String.format("R$ %.2f", relatorio.getSaldoFinal()));

                    DefaultTableModel modelResumo = (DefaultTableModel) tableResumo.getModel();
                    modelResumo.setRowCount(0);
                    modelResumo.addRow(new Object[]{" Total de entradas", String.format("R$ %.2f ", relatorio.getTotalEntradas())});
                    modelResumo.addRow(new Object[]{" Total de saídas", String.format("R$ %.2f ", relatorio.getTotalSaidas())});
                    modelResumo.addRow(new Object[]{" Saldo do período", String.format("R$ %.2f ", relatorio.getSaldoFinal())});

                    DefaultTableModel modelMov = (DefaultTableModel) tableMov.getModel();
                    modelMov.setRowCount(0);

                    SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
                    if (listaMovimentos != null) {
                        for (Financeiro f : listaMovimentos) {
                            modelMov.addRow(new Object[]{
                                    " " + formatoData.format(f.getData()),
                                    " " + f.getTipo(),
                                    String.format("R$ %.2f ", f.getValor())
                            });
                        }
                    }

                    tableResumo.revalidate();
                    tableResumo.repaint();
                    tableMov.revalidate();
                    tableMov.repaint();
                    revalidate();
                    repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void criarCard(JPanel container, String texto, JLabel labelValor, Color cor, int x, int y) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBounds(x, y, 340, 125);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titulo = new JLabel(texto);
        titulo.setForeground(cor);
        titulo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titulo.setBounds(20, 15, 200, 20);

        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        labelValor.setForeground(Color.BLACK);
        labelValor.setBounds(20, 50, 220, 45);

        card.add(titulo);
        card.add(labelValor);
        container.add(card);
    }

    private JTable estilizarTabela(Object[][] dados, String[] colunas) {
        DefaultTableModel model = new DefaultTableModel(dados, colunas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabela = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 245, 240));
                }
                return c;
            }
        };

        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setRowHeight(45);
        tabela.setGridColor(new Color(235, 235, 235));
        tabela.getTableHeader().setBackground(new Color(205, 145, 55));
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.setShowGrid(true);

        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centralizado);
        }

        return tabela;
    }

    private class ScrollBarProfissionalUI extends BasicScrollBarUI {
        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.setOpaque(false);
        }
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {}
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !c.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isThumbRollover() ? new Color(165, 105, 20) : new Color(205, 145, 55));
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 8, 8);
            g2.dispose();
        }
        @Override
        protected JButton createDecreaseButton(int orientation) { return criarBotaoInvisivel(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return criarBotaoInvisivel(); }
        private JButton criarBotaoInvisivel() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            return btn;
        }
    }
}