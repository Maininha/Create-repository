package view;

import controller.FinanceiroController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class PainelFinanceiro extends JPanel {

    private DefaultTableModel modelo;
    private FinanceiroController controller;

    private JTextField data1;
    private JTextField data2;
    private JComboBox<String> combo;
    private JTable tabela;

    public PainelFinanceiro() {

        this.controller = new FinanceiroController();

        setLayout(null);
        setBackground(new Color(248, 245, 240));

        JLabel titulo = new JLabel("Financeiro");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(70, 40, 15));
        titulo.setBounds(40, 25, 300, 40);
        add(titulo);

        JLabel subtitulo = new JLabel("Consulte as movimentações financeiras.");
        subtitulo.setForeground(Color.GRAY);
        subtitulo.setBounds(40, 60, 400, 20);
        add(subtitulo);

        JLabel tipo = new JLabel("Tipo de Movimentação");
        tipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tipo.setForeground(Color.GRAY);
        tipo.setBounds(40, 95, 250, 20);
        add(tipo);

        combo = new JComboBox<>(new String[]{"Todos", "Entrada", "Saída"});
        combo.setBounds(40, 120, 280, 40);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(35, 18, 4));
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(combo);

        combo.addActionListener(e -> executarConsultaAtual());

        JLabel periodo = new JLabel("Início do Período");
        periodo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        periodo.setForeground(Color.GRAY);
        periodo.setBounds(340, 95, 190, 20);
        add(periodo);

        data1 = new JTextField();
        data1.setBounds(340, 120, 145, 40);
        data1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        data1.setEditable(false);
        data1.setBackground(Color.WHITE);
        data1.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(data1);

        // CORREÇÃO: Caractere limpo, sem margens e ouvinte para fechar o calendário ao clicar fora
        JButton btnCal1 = new JButton("▼");
        btnCal1.setBounds(485, 120, 45, 40);
        btnCal1.setBackground(new Color(205, 145, 55));
        btnCal1.setForeground(Color.WHITE);
        btnCal1.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnCal1.setMargin(new Insets(0, 0, 0, 0));
        btnCal1.setFocusPainted(false);
        btnCal1.setBorderPainted(false);
        btnCal1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCal1.addActionListener(e -> {
            JFrame topo = (JFrame) SwingUtilities.getWindowAncestor(this);
            String selecionada = new DatePickerNativo().exibirCalendario(topo, data1);
            if (!selecionada.equals("")) data1.setText(selecionada);

            // Ativa o monitoramento de clique externo imediatamente após abrir o calendário
            vincularFechamentoAutomatico();
        });
        add(btnCal1);

        JLabel fimPeriodo = new JLabel("Fim do Período");
        fimPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fimPeriodo.setForeground(Color.GRAY);
        fimPeriodo.setBounds(550, 95, 190, 20);
        add(fimPeriodo);

        data2 = new JTextField();
        data2.setBounds(550, 120, 145, 40);
        data2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        data2.setEditable(false);
        data2.setBackground(Color.WHITE);
        data2.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(data2);

        // CORREÇÃO: Caractere limpo, sem margens e ouvinte para fechar o calendário ao clicar fora
        JButton btnCal2 = new JButton("▼");
        btnCal2.setBounds(695, 120, 45, 40);
        btnCal2.setBackground(new Color(205, 145, 55));
        btnCal2.setForeground(Color.WHITE);
        btnCal2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnCal2.setMargin(new Insets(0, 0, 0, 0));
        btnCal2.setFocusPainted(false);
        btnCal2.setBorderPainted(false);
        btnCal2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCal2.addActionListener(e -> {
            JFrame topo = (JFrame) SwingUtilities.getWindowAncestor(this);
            String selecionada = new DatePickerNativo().exibirCalendario(topo, data2);
            if (!selecionada.equals("")) data2.setText(selecionada);

            // Ativa o monitoramento de clique externo imediatamente após abrir o calendário
            vincularFechamentoAutomatico();
        });
        add(btnCal2);

        String colunas[] = {"ID", "Data", "Tipo", "Classificação", "Descrição", "Valor"};
        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabela = new JTable(modelo) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 245, 240));
                }
                return c;
            }
        };

        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);
        tabela.getColumnModel().getColumn(0).setWidth(0);

        DefaultTableCellRenderer centralizado = new DefaultTableCellRenderer();
        centralizado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centralizado);
        }

        tabela.setRowHeight(45);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setShowGrid(true);
        tabela.setGridColor(new Color(235, 235, 235));
        tabela.getTableHeader().setBackground(new Color(205, 145, 55));
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(Color.WHITE);
        popupMenu.setBorder(BorderFactory.createLineBorder(new Color(185, 120, 30), 1));

        JMenuItem menuEditar = new JMenuItem("✏️  Editar Registro");
        menuEditar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuEditar.setBackground(Color.WHITE);
        menuEditar.setForeground(new Color(70, 40, 15));

        JMenuItem menuExcluir = new JMenuItem("🗑️  Excluir Registro");
        menuExcluir.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuExcluir.setBackground(Color.WHITE);
        menuExcluir.setForeground(new Color(185, 120, 30));

        JSeparator divisor = new JSeparator();
        divisor.setForeground(new Color(240, 240, 240));

        menuEditar.addActionListener(ev -> {
            int linha = tabela.getSelectedRow();
            if (linha != -1) {
                try {
                    int idMov = Integer.parseInt(tabela.getValueAt(linha, 0).toString());
                    String data = tabela.getValueAt(linha, 1).toString();
                    String tipoReg = tabela.getValueAt(linha, 2).toString();
                    String categoria = tabela.getValueAt(linha, 3).toString();
                    String descricao = tabela.getValueAt(linha, 4).toString();
                    String valor = tabela.getValueAt(linha, 5).toString();

                    TelaPrincipal tela = (TelaPrincipal) SwingUtilities.getWindowAncestor(this);
                    if (tela != null) {
                        tela.getPainelNovaMovimentacao().preencherCamposParaEdicao(idMov, data, tipoReg, categoria, descricao, valor);
                        tela.getCard().show(tela.getPainelConteudo(), "novaMovimentacao");
                        tela.selecionarBotao(tela.getBtFinanceiro());
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        menuExcluir.addActionListener(ev -> {
            int líneaSelecionada = tabela.getSelectedRow();
            if (líneaSelecionada != -1) {
                int idMov = Integer.parseInt(tabela.getValueAt(líneaSelecionada, 0).toString());
                String descricao = tabela.getValueAt(líneaSelecionada, 4).toString();

                int confirmacao = JOptionPane.showConfirmDialog(
                        this,
                        "Tem certeza que deseja excluir a movimentação: \"" + descricao + "\"?",
                        "Confirmar Exclusão",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirmacao == JOptionPane.YES_OPTION) {
                    if (controller.excluirMovimentacao(idMov)) {
                        executarConsultaAtual();
                    }
                }
            }
        });

        popupMenu.add(menuEditar);
        popupMenu.add(divisor);
        popupMenu.add(menuExcluir);

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { checarCliquePopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { checarCliquePopup(e); }

            private void checarCliquePopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tabela.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < tabela.getRowCount()) {
                        tabela.setRowSelectionInterval(row, row);
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JPanel tabelaCard = new JPanel(new BorderLayout());
        tabelaCard.setBounds(40, 170, 1080, 420);
        tabelaCard.setBackground(Color.WHITE);
        tabelaCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel tituloTabela = new JLabel("Movimentações Financeiras");
        tituloTabela.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloTabela.setForeground(new Color(35, 18, 4));
        tituloTabela.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JScrollPane scroll = new JScrollPane(tabela);

        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(Color.WHITE);
        scroll.getViewport().setBackground(Color.WHITE);

        scroll.getVerticalScrollBar().setUI(new ScrollBarProfissionalUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setBackground(Color.WHITE);

        scroll.getHorizontalScrollBar().setUI(new ScrollBarProfissionalUI());
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
        scroll.getHorizontalScrollBar().setBackground(Color.WHITE);

        tabelaCard.add(tituloTabela, BorderLayout.NORTH);
        tabelaCard.add(scroll, BorderLayout.CENTER);
        add(tabelaCard);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.setBounds(760, 120, 140, 40);
        btnConsultar.setBackground(new Color(185, 120, 30));
        btnConsultar.setForeground(Color.WHITE);
        btnConsultar.setFocusPainted(false);
        btnConsultar.setBorderPainted(false);
        btnConsultar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConsultar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConsultar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnConsultar.setBackground(new Color(205, 145, 55)); }
            @Override
            public void mouseExited(MouseEvent e) { btnConsultar.setBackground(new Color(185, 120, 30)); }
        });

        btnConsultar.addActionListener(e -> executarConsultaAtual());
        add(btnConsultar);

        JButton btnNovaMovimentacao = new JButton("Nova Movimentação");
        btnNovaMovimentacao.setBounds(920, 120, 200, 40);
        btnNovaMovimentacao.setBackground(new Color(185, 120, 30));
        btnNovaMovimentacao.setForeground(Color.WHITE);
        btnNovaMovimentacao.setFocusPainted(false);
        btnNovaMovimentacao.setBorderPainted(false);
        btnNovaMovimentacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNovaMovimentacao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnNovaMovimentacao);

        btnNovaMovimentacao.addActionListener(e -> {
            TelaPrincipal tela = (TelaPrincipal) SwingUtilities.getWindowAncestor(PainelFinanceiro.this);
            if (tela != null) {
                tela.getCard().show(tela.getPainelConteudo(), "novaMovimentacao");
                tela.selecionarBotao(tela.getBtFinanceiro());
            }
        });

        executarConsultaAtual();
    }

    public void executarConsultaAtual() {
        String tipoSelecionado = (String) combo.getSelectedItem();
        controller.atualizarTabela(modelo, tipoSelecionado, data1.getText(), data2.getText());
    }

    // 🔥 METODO AUXILIAR: Captura qualquer clique fora do calendário e força a perda do foco para fechá-lo
    private void vincularFechamentoAutomatico() {
        Window janelaTopo = SwingUtilities.getWindowAncestor(this);
        if (janelaTopo == null) return;

        // Procura todas as janelas ativas (como o popup do calendário aberto)
        for (Window w : janelaTopo.getOwnedWindows()) {
            if (w.isVisible()) {
                // Instancia um ouvinte que fecha ao clicar fora
                AWTEventListener ouvinteCliqueExterno = new AWTEventListener() {
                    @Override
                    public void eventDispatched(AWTEvent event) {
                        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                            MouseEvent me = (MouseEvent) event;
                            // Se o clique ocorreu fora dos limites físicos do calendário aberto
                            if (!w.getBounds().contains(me.getLocationOnScreen())) {
                                w.setVisible(false);
                                w.dispose();
                                // Desvincula o ouvinte para economizar processamento
                                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                            }
                        }
                    }
                };
                Toolkit.getDefaultToolkit().addAWTEventListener(ouvinteCliqueExterno, AWTEvent.MOUSE_EVENT_MASK);
            }
        }
    }

    private class ScrollBarProfissionalUI extends BasicScrollBarUI {

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.setOpaque(false);
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !c.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isThumbRollover()) {
                g2.setColor(new Color(165, 105, 20));
            } else {
                g2.setColor(new Color(205, 145, 55));
            }

            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 8, 8);
            g2.dispose();
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return criarBotaoInvisivel();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return criarBotaoInvisivel();
        }

        private JButton criarBotaoInvisivel() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }
    }
}