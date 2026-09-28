package view;

import controller.FinanceiroController;
import model.Financeiro;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class PainelFinanceiro extends JPanel {

    private DefaultTableModel modelo;
    private FinanceiroController controller;

    private JTextField data1;
    private JTextField data2;
    private JComboBox<String> combo;
    private JTable tabela;
    private SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

    // Cores do Tema Quilombola / UI
    private final Color COR_PRIMARIA = new Color(35, 18, 4);
    private final Color COR_DESTAQUE = new Color(185, 120, 30);
    private final Color COR_HOVER = new Color(205, 145, 55);
    private final Color COR_VERDE = new Color(34, 139, 34);
    private final Color COR_VERMELHO = new Color(178, 34, 34);

    // Proteção para evitar múltiplas queries concorrentes na tabela
    private SwingWorker<List<Financeiro>, Void> workerConsultaAtual = null;

    public PainelFinanceiro() {

        this.controller = new FinanceiroController();

        setLayout(null);
        setBackground(new Color(248, 245, 240));

        // ==========================================
        // CABEÇALHO
        // ==========================================
        JLabel titulo = new JLabel("Financeiro");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(new Color(70, 40, 15));
        titulo.setBounds(40, 20, 300, 35);
        add(titulo);

        JLabel subtitulo = new JLabel("Consulte as movimentações financeiras.");
        subtitulo.setForeground(Color.GRAY);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setBounds(40, 52, 400, 20);
        add(subtitulo);

        // ==========================================
        // FILTROS E CONTROLES (Reajustados para y = 90)
        // ==========================================
        int yFiltros = 90;

        JLabel tipo = new JLabel("Tipo de Movimentação");
        tipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tipo.setForeground(Color.GRAY);
        tipo.setBounds(40, yFiltros, 200, 18);
        add(tipo);

        combo = new JComboBox<>(new String[]{"Todos", "Entrada", "Saída"});
        combo.setBounds(40, yFiltros + 20, 230, 38);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(COR_PRIMARIA);
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(combo);

        combo.addActionListener(e -> executarConsultaAtual());

        JLabel periodo = new JLabel("Início do Período");
        periodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        periodo.setForeground(Color.GRAY);
        periodo.setBounds(285, yFiltros, 150, 18);
        add(periodo);

        data1 = new JTextField();
        data1.setBounds(285, yFiltros + 20, 120, 38);
        data1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        data1.setEditable(false);
        data1.setBackground(Color.WHITE);
        data1.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(data1);

        JButton btnCal1 = new JButton("▼");
        btnCal1.setBounds(405, yFiltros + 20, 38, 38);
        btnCal1.setBackground(COR_DESTAQUE);
        btnCal1.setForeground(Color.WHITE);
        btnCal1.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnCal1.setFocusPainted(false);
        btnCal1.setBorderPainted(false);
        btnCal1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCal1.addActionListener(e -> {
            JFrame topo = (JFrame) SwingUtilities.getWindowAncestor(this);
            String selecionada = new DatePickerNativo().exibirCalendario(topo, data1);
            if (!selecionada.equals("")) data1.setText(selecionada);
            vincularFechamentoAutomatico();
        });
        add(btnCal1);

        JLabel fimPeriodo = new JLabel("Fim do Período");
        fimPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fimPeriodo.setForeground(Color.GRAY);
        fimPeriodo.setBounds(455, yFiltros, 150, 18);
        add(fimPeriodo);

        data2 = new JTextField();
        data2.setBounds(455, yFiltros + 20, 120, 38);
        data2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        data2.setEditable(false);
        data2.setBackground(Color.WHITE);
        data2.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(data2);

        JButton btnCal2 = new JButton("▼");
        btnCal2.setBounds(575, yFiltros + 20, 38, 38);
        btnCal2.setBackground(COR_DESTAQUE);
        btnCal2.setForeground(Color.WHITE);
        btnCal2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnCal2.setFocusPainted(false);
        btnCal2.setBorderPainted(false);
        btnCal2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCal2.addActionListener(e -> {
            JFrame topo = (JFrame) SwingUtilities.getWindowAncestor(this);
            String selecionada = new DatePickerNativo().exibirCalendario(topo, data2);
            if (!selecionada.equals("")) data2.setText(selecionada);
            vincularFechamentoAutomatico();
        });
        add(btnCal2);

        // --- BOTÃO SECUNDÁRIO: CONSULTAR ---
        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setBounds(630, yFiltros + 20, 130, 38);
        btnConsultar.setBackground(Color.WHITE);
        btnConsultar.setForeground(COR_DESTAQUE);
        btnConsultar.setFocusPainted(false);
        btnConsultar.setBorder(BorderFactory.createLineBorder(COR_DESTAQUE, 1));
        btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConsultar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConsultar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnConsultar.setBackground(new Color(250, 245, 238)); }
            @Override
            public void mouseExited(MouseEvent e) { btnConsultar.setBackground(Color.WHITE); }
        });
        btnConsultar.addActionListener(e -> executarConsultaAtual());
        add(btnConsultar);

        // --- BOTÃO PRIMÁRIO: NOVA MOVIMENTAÇÃO ---
        JButton btnNovaMovimentacao = new JButton("+ Nova Movimentação");
        btnNovaMovimentacao.setBounds(910, yFiltros + 20, 210, 38);
        btnNovaMovimentacao.setBackground(COR_DESTAQUE);
        btnNovaMovimentacao.setForeground(Color.WHITE);
        btnNovaMovimentacao.setFocusPainted(false);
        btnNovaMovimentacao.setBorderPainted(false);
        btnNovaMovimentacao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNovaMovimentacao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovaMovimentacao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnNovaMovimentacao.setBackground(COR_HOVER); }
            @Override
            public void mouseExited(MouseEvent e) { btnNovaMovimentacao.setBackground(COR_DESTAQUE); }
        });
        btnNovaMovimentacao.addActionListener(e -> {
            TelaPrincipal tela = (TelaPrincipal) SwingUtilities.getWindowAncestor(PainelFinanceiro.this);
            if (tela != null) {
                tela.getCard().show(tela.getPainelConteudo(), "novaMovimentacao");
                tela.selecionarBotao(tela.getBtFinanceiro());
            }
        });
        add(btnNovaMovimentacao);

        // ==========================================
        // TABELA E COLUNAS (Subiu para y = 160 e altura expandida)
        // ==========================================
        String colunas[] = {"ID", "Data", "Tipo", "Classificação", "Descrição", "Valor", "Ações"};
        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(modelo) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 247, 242));

                    if (column == 5) { // Valor
                        String fluxo = String.valueOf(tabela.getValueAt(row, 2));
                        if (fluxo.equalsIgnoreCase("Entrada")) {
                            c.setForeground(COR_VERDE);
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(COR_VERMELHO);
                            c.setFont(c.getFont().deriveFont(Font.BOLD));
                        }
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        };

        // Ocultar coluna de ID
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setWidth(0);

        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < 6; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centralizado);
        }

        // Renderizador da coluna de Ações
        tabela.getColumnModel().getColumn(6).setCellRenderer(new AcoesRenderer());
        tabela.getColumnModel().getColumn(6).setPreferredWidth(100);

        tabela.setRowHeight(45);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.getTableHeader().setBackground(COR_DESTAQUE);
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Menu de Contexto (Botão Direito)
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(Color.WHITE);
        popupMenu.setBorder(BorderFactory.createLineBorder(COR_DESTAQUE, 1));

        JMenuItem menuEditar = new JMenuItem("✏️  Editar Registro");
        menuEditar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuEditar.setBackground(Color.WHITE);
        menuEditar.setForeground(new Color(70, 40, 15));

        JMenuItem menuExcluir = new JMenuItem("🗑️  Excluir Registro");
        menuExcluir.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuExcluir.setBackground(Color.WHITE);
        menuExcluir.setForeground(COR_DESTAQUE);

        JSeparator divisor = new JSeparator();
        divisor.setForeground(new Color(240, 240, 240));

        menuEditar.addActionListener(ev -> acaoEditarLinha(tabela.getSelectedRow()));
        menuExcluir.addActionListener(ev -> acaoExcluirLinha(tabela.getSelectedRow()));

        popupMenu.add(menuEditar);
        popupMenu.add(divisor);
        popupMenu.add(menuExcluir);

        // Ações de clique na tabela
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tratarCliquePopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (tratarCliquePopup(e)) return;

                int rowVisual = tabela.rowAtPoint(e.getPoint());
                int col = tabela.columnAtPoint(e.getPoint());

                if (rowVisual >= 0 && rowVisual < tabela.getRowCount()) {
                    tabela.setRowSelectionInterval(rowVisual, rowVisual);
                }

                // Clique na coluna de Ações (Editar / Excluir)
                if (col == 6 && rowVisual != -1 && SwingUtilities.isLeftMouseButton(e)) {
                    Rectangle retanguloCelula = tabela.getCellRect(rowVisual, col, false);
                    int cliqueX = e.getX() - retanguloCelula.x;
                    int larguraCelula = retanguloCelula.width;

                    if (cliqueX < larguraCelula / 2) {
                        acaoEditarLinha(rowVisual);
                    } else {
                        acaoExcluirLinha(rowVisual);
                    }
                }
            }

            private boolean tratarCliquePopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int rowVisual = tabela.rowAtPoint(e.getPoint());
                    if (rowVisual >= 0 && rowVisual < tabela.getRowCount()) {
                        tabela.setRowSelectionInterval(rowVisual, rowVisual);
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    return true;
                }
                return false;
            }
        });

        // Container Card Tabela (Ajustado para y = 160, ganho de altura para 450)
        JPanel tabelaCard = new JPanel(new BorderLayout());
        tabelaCard.setBounds(40, 160, 1080, 450);
        tabelaCard.setBackground(Color.WHITE);
        tabelaCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 225, 218), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel tituloTabela = new JLabel("Movimentações Financeiras");
        tituloTabela.setFont(new Font("Segoe UI", Font.BOLD, 17));
        tituloTabela.setForeground(COR_PRIMARIA);
        tituloTabela.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(Color.WHITE);
        scroll.getViewport().setBackground(Color.WHITE);

        scroll.getVerticalScrollBar().setUI(new ScrollBarProfissionalUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setBackground(Color.WHITE);

        tabelaCard.add(tituloTabela, BorderLayout.NORTH);
        tabelaCard.add(scroll, BorderLayout.CENTER);
        add(tabelaCard);

        executarConsultaAtual();
    }

    // ==========================================
    // MÉTODOS DE AÇÃO (EDITAR / EXCLUIR)
    // ==========================================
    private void acaoEditarLinha(int linha) {
        if (linha < 0 || linha >= tabela.getRowCount()) return;

        try {
            int idMov = Integer.parseInt(tabela.getValueAt(linha, 0).toString());
            String data = tabela.getValueAt(linha, 1).toString();
            String tipoReg = tabela.getValueAt(linha, 2).toString();
            String categoria = tabela.getValueAt(linha, 3).toString();
            String descricao = tabela.getValueAt(linha, 4).toString();
            String valorBruto = tabela.getValueAt(linha, 5).toString();

            String valorLimpo = valorBruto.replaceAll("[R$\\s\\+\\-]", "");

            if (valorLimpo.contains(",") && valorLimpo.contains(".")) {
                valorLimpo = valorLimpo.replace(".", "").replace(",", ".");
            } else if (valorLimpo.contains(",")) {
                valorLimpo = valorLimpo.replace(",", ".");
            }

            TelaPrincipal tela = (TelaPrincipal) SwingUtilities.getWindowAncestor(this);
            if (tela != null) {
                tela.getPainelNovaMovimentacao().preencherCamposParaEdicao(idMov, data, tipoReg, categoria, descricao, valorLimpo);
                tela.getCard().show(tela.getPainelConteudo(), "novaMovimentacao");
                tela.selecionarBotao(tela.getBtFinanceiro());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados de edição: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void acaoExcluirLinha(int linha) {
        if (linha < 0 || linha >= tabela.getRowCount()) return;

        int idMov = Integer.parseInt(tabela.getValueAt(linha, 0).toString());
        String descricao = tabela.getValueAt(linha, 4).toString();

        // Opções personalizadas em Português
        Object[] opcoes = {"Sim", "Não"};

        int confirmacao = JOptionPane.showOptionDialog(
                this,
                "Tem certeza que deseja excluir a movimentação: \"" + descricao + "\"?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opcoes,
                opcoes[1] // Mantém o "Não" selecionado por padrão para maior segurança
        );

        if (confirmacao == 0) { // 0 corresponde à opção "Sim"
            if (controller.excluirMovimentacao(idMov)) {
                executarConsultaAtual();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir a movimentação.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // CONSULTA ASSÍNCRONA
    // ==========================================
    public void executarConsultaAtual() {
        if (workerConsultaAtual != null && !workerConsultaAtual.isDone()) {
            workerConsultaAtual.cancel(true);
        }

        String tipoSelecionado = (String) combo.getSelectedItem();
        String filtroDataInicio = data1.getText();
        String filtroDataFim = data2.getText();

        workerConsultaAtual = new SwingWorker<>() {
            @Override
            protected List<Financeiro> doInBackground() throws Exception {
                return controller.buscarMovimentacoesFiltradas(tipoSelecionado, filtroDataInicio, filtroDataFim);
            }

            @Override
            protected void done() {
                if (isCancelled()) return;

                try {
                    List<Financeiro> movimentacoes = get();
                    modelo.setRowCount(0);

                    if (movimentacoes != null && !movimentacoes.isEmpty()) {
                        for (Financeiro f : movimentacoes) {
                            String dataStr = (f.getData() != null) ? formatoData.format(f.getData()) : "";
                            double valorLimpo = Math.abs(f.getValor());
                            String valorFormatado = String.format("R$ %.2f", valorLimpo);

                            modelo.addRow(new Object[]{
                                    f.getIdMov(),
                                    dataStr,
                                    f.getTipo(),
                                    f.getCat(),
                                    f.getDesc(),
                                    valorFormatado,
                                    ""
                            });
                        }
                    }

                    tabela.revalidate();
                    tabela.repaint();
                } catch (Exception e) {
                    System.err.println("Erro na atualização do modelo: " + e.getMessage());
                }
            }
        };
        workerConsultaAtual.execute();
    }

    private void vincularFechamentoAutomatico() {
        Window JANELAtopo = SwingUtilities.getWindowAncestor(this);
        if (JANELAtopo == null) return;

        for (Window w : JANELAtopo.getOwnedWindows()) {
            if (w.isVisible()) {
                AWTEventListener ouvinteCliqueExterno = new AWTEventListener() {
                    @Override
                    public void eventDispatched(AWTEvent event) {
                        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                            MouseEvent me = (MouseEvent) event;
                            if (!w.getBounds().contains(me.getLocationOnScreen())) {
                                w.setVisible(false);
                                w.dispose();
                                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                            }
                        }
                    }
                };
                Toolkit.getDefaultToolkit().addAWTEventListener(ouvinteCliqueExterno, AWTEvent.MOUSE_EVENT_MASK);
            }
        }
    }

    // ==========================================
    // RENDERIZADORES AUXILIARES
    // ==========================================
    private class AcoesRenderer extends JPanel implements TableCellRenderer {
        private final JLabel btnEditar = new JLabel("Editar");
        private final JLabel btnExcluir = new JLabel("Excluir");

        public AcoesRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 8));
            setOpaque(true);

            btnEditar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnEditar.setForeground(COR_DESTAQUE);

            JLabel divisor = new JLabel("|");
            divisor.setForeground(new Color(210, 205, 195));

            btnExcluir.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnExcluir.setForeground(COR_VERMELHO);

            add(btnEditar);
            add(divisor);
            add(btnExcluir);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Color bg = isSelected
                    ? table.getSelectionBackground()
                    : (row % 2 == 0 ? Color.WHITE : new Color(250, 247, 242));
            setBackground(bg);
            return this;
        }
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
            g2.setColor(isThumbRollover() ? new Color(165, 105, 20) : COR_HOVER);
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