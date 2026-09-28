package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class PainelAssociados extends JPanel {

    private JTable tabela;
    private JTextField pesquisa;
    private JButton buscar;
    private JButton btnLimparBusca;
    private JButton btnVoltarLink;
    private JLabel lbEmptyState;

    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> sorter;

    private final String PLACEHOLDER = "Buscar por Nome ou CPF...";
    private final Border BORDA_CLEAN = BorderFactory.createLineBorder(new Color(230, 225, 218), 1, true);

    private final Color COR_PRIMARIA = new Color(43, 22, 7);
    private final Color COR_DESTAQUE = new Color(185, 120, 30);
    private final Color COR_HOVER = new Color(205, 145, 55);
    private final Color COR_EXCLUIR = new Color(211, 47, 47);

    public interface AcoesListener {
        void editar(int rowModel);
        void excluir(int rowModel);
    }

    private AcoesListener listener;

    public void setAcoesListener(AcoesListener listener) {
        this.listener = listener;
    }

    public JTable getTabela() { return tabela; }
    public JButton getBtnBuscar() { return buscar; }
    public JTextField getTxtBusca() { return pesquisa; }

    public PainelAssociados() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        criarCabecalhoEPesquisa();
        criarPainelTabela();
    }

    private void criarCabecalhoEPesquisa() {
        JPanel painelSuperior = new JPanel();
        painelSuperior.setLayout(new BoxLayout(painelSuperior, BoxLayout.Y_AXIS));
        painelSuperior.setOpaque(false);

        JLabel titulo = new JLabel("Gestão de Associados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(COR_PRIMARIA);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelSuperior.add(titulo);
        painelSuperior.add(Box.createVerticalStrut(15));

        JPanel linhaComandos = new JPanel(new BorderLayout(15, 0));
        linhaComandos.setOpaque(false);
        linhaComandos.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel painelInput = new JPanel(new BorderLayout());
        painelInput.setBackground(Color.WHITE);
        painelInput.setBorder(BorderFactory.createCompoundBorder(
                BORDA_CLEAN,
                BorderFactory.createEmptyBorder(0, 12, 0, 5)
        ));

        pesquisa = new JTextField(PLACEHOLDER);
        pesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pesquisa.setForeground(Color.GRAY);
        pesquisa.setBorder(null);

        // Botão de limpar pesquisa
        btnLimparBusca = new JButton();
        URL imgUrl = getClass().getResource("/imagens/fechar.png");

        if (imgUrl != null) {
            ImageIcon iconeOriginal = new ImageIcon(imgUrl);
            Image imgRedimensionada = iconeOriginal.getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH);
            btnLimparBusca.setIcon(new ImageIcon(imgRedimensionada));
        } else {
            btnLimparBusca.setText("✕");
            btnLimparBusca.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnLimparBusca.setForeground(Color.GRAY);
        }

        btnLimparBusca.setContentAreaFilled(false);
        btnLimparBusca.setBorderPainted(false);
        btnLimparBusca.setFocusPainted(false);
        btnLimparBusca.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimparBusca.setPreferredSize(new Dimension(24, 24));
        btnLimparBusca.setVisible(false);
        btnLimparBusca.addActionListener(e -> limparFiltroCompleto());

        pesquisa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { atualizarVisibilidade(); }
            @Override
            public void removeUpdate(DocumentEvent e) { atualizarVisibilidade(); }
            @Override
            public void changedUpdate(DocumentEvent e) { atualizarVisibilidade(); }

            private void atualizarVisibilidade() {
                SwingUtilities.invokeLater(() -> {
                    String txt = pesquisa.getText().trim();
                    boolean temTextoValido = !txt.isEmpty() && !txt.equals(PLACEHOLDER);
                    btnLimparBusca.setVisible(temTextoValido);
                });
            }
        });

        pesquisa.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (pesquisa.getText().equals(PLACEHOLDER)) {
                    pesquisa.setText("");
                    pesquisa.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (pesquisa.getText().trim().isEmpty()) {
                    pesquisa.setText(PLACEHOLDER);
                    pesquisa.setForeground(Color.GRAY);
                    btnLimparBusca.setVisible(false);
                }
            }
        });

        painelInput.add(pesquisa, BorderLayout.CENTER);
        painelInput.add(btnLimparBusca, BorderLayout.EAST);
        linhaComandos.add(painelInput, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setOpaque(false);

        // Botão Buscar (Estilo Secundário/Outline)
        buscar = new JButton("Buscar");
        estilizarBotaoSecundario(buscar);
        buscar.addActionListener(e -> filtrar());
        pesquisa.addActionListener(e -> filtrar());
        painelBotoes.add(buscar);

        // Botão Cadastrar (Estilo Primário Chamativo)
        JButton btnNovoAssociado = new JButton("Cadastrar");
        estilizarBotaoPrimario(btnNovoAssociado);
        btnNovoAssociado.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(PainelAssociados.this);
            if (window instanceof TelaPrincipal) {
                TelaPrincipal tela = (TelaPrincipal) window;
                tela.getCard().show(tela.getPainelConteudo(), "cadastroAssociado");
                tela.alternarCorBotao(null);
            } else if (window instanceof TelaCadastroAssociado) {
                TelaCadastroAssociado telaSecundaria = (TelaCadastroAssociado) window;
                telaSecundaria.getCard().show(telaSecundaria.getPainelConteudo(), "criarCadastro");
                telaSecundaria.alternarFocoMenu(null);
            }
        });
        painelBotoes.add(btnNovoAssociado);

        linhaComandos.add(painelBotoes, BorderLayout.EAST);
        painelSuperior.add(linhaComandos);
        painelSuperior.add(Box.createVerticalStrut(8));

        btnVoltarLink = new JButton("← Voltar para todos os associados");
        btnVoltarLink.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVoltarLink.setForeground(COR_DESTAQUE);
        btnVoltarLink.setContentAreaFilled(false);
        btnVoltarLink.setBorderPainted(false);
        btnVoltarLink.setFocusPainted(false);
        btnVoltarLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarLink.setHorizontalAlignment(SwingConstants.LEFT);
        btnVoltarLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVoltarLink.setVisible(false);

        btnVoltarLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnVoltarLink.setForeground(COR_PRIMARIA); }
            @Override
            public void mouseExited(MouseEvent e) { btnVoltarLink.setForeground(COR_DESTAQUE); }
        });

        btnVoltarLink.addActionListener(e -> limparFiltroCompleto());
        painelSuperior.add(btnVoltarLink);

        add(painelSuperior, BorderLayout.NORTH);
    }

    private void criarPainelTabela() {
        JPanel cardContainer = new JPanel(new CardLayout());
        cardContainer.setBackground(Color.WHITE);
        cardContainer.setBorder(BorderFactory.createCompoundBorder(
                BORDA_CLEAN,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String[] colunas = {"Nome", "CPF", "Endereço", "Data de Inclusão", "Ações"};

        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modelo) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(252, 250, 246));
                }
                return c;
            }
        };

        tabela.setRowHeight(50);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setSelectionBackground(new Color(245, 240, 232));
        tabela.setSelectionForeground(COR_PRIMARIA);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(COR_PRIMARIA);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(modelo);
        tabela.setRowSorter(sorter);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        tabela.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        tabela.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        tabela.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tabela.getColumnModel().getColumn(4).setCellRenderer(new AcoesRenderer());

        tabela.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(150);

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rowVisual = tabela.getSelectedRow();
                int col = tabela.columnAtPoint(e.getPoint());

                if (col == 4 && rowVisual != -1) {
                    int modelRow = tabela.convertRowIndexToModel(rowVisual);
                    int cliqueX = e.getX() - tabela.getCellRect(rowVisual, col, false).x;
                    int larguraCelula = tabela.getColumnModel().getColumn(4).getWidth();

                    if (cliqueX < larguraCelula / 2) {
                        if (listener != null) listener.editar(modelRow);
                    } else {
                        if (listener != null) {
                            String nome = (String) modelo.getValueAt(modelRow, 0);

                            // Opções personalizadas para o diálogo em Português
                            Object[] opcoes = {"Sim", "Não"};

                            int opt = JOptionPane.showOptionDialog(
                                    PainelAssociados.this,
                                    "Deseja realmente excluir o associado " + (nome != null ? "\"" + nome + "\"" : "") + "?",
                                    "Confirmar Exclusão",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE,
                                    null,
                                    opcoes,
                                    opcoes[1] // Mantém o "Não" focado por padrão
                            );

                            if (opt == 0) { // 0 corresponde a "Sim"
                                listener.excluir(modelRow);
                                atualizarEmptyState();
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        lbEmptyState = new JLabel("Nenhum associado cadastrado.", SwingConstants.CENTER);
        lbEmptyState.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbEmptyState.setForeground(Color.GRAY);

        cardContainer.add(scroll, "tabela");
        cardContainer.add(lbEmptyState, "empty");

        add(cardContainer, BorderLayout.CENTER);
    }

    private void estilizarBotaoPrimario(JButton btn) {
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setBackground(COR_DESTAQUE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(COR_HOVER); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(COR_DESTAQUE); }
        });
    }

    private void estilizarBotaoSecundario(JButton btn) {
        btn.setPreferredSize(new Dimension(110, 40));
        btn.setBackground(Color.WHITE);
        btn.setForeground(COR_DESTAQUE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(COR_DESTAQUE, 1));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(250, 245, 238));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
    }

    private void filtrar() {
        String txt = pesquisa.getText().trim();
        if (txt.isEmpty() || txt.equals(PLACEHOLDER)) {
            limparFiltroCompleto();
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txt, 0, 1));
            btnVoltarLink.setVisible(true);
            btnLimparBusca.setVisible(true);
            atualizarEmptyState();
        }
    }

    private void limparFiltroCompleto() {
        sorter.setRowFilter(null);
        pesquisa.setText(PLACEHOLDER);
        pesquisa.setForeground(Color.GRAY);
        btnVoltarLink.setVisible(false);
        btnLimparBusca.setVisible(false);
        atualizarEmptyState();
    }

    private void atualizarEmptyState() {
        CardLayout cl = (CardLayout) lbEmptyState.getParent().getLayout();
        if (tabela.getRowCount() == 0) {
            lbEmptyState.setText("Nenhum associado cadastrado.");
            cl.show(lbEmptyState.getParent(), "empty");
        } else if (sorter.getRowFilter() != null && tabela.getRowCount() == 0) {
            lbEmptyState.setText("Nenhum resultado encontrado para o termo pesquisado.");
            cl.show(lbEmptyState.getParent(), "empty");
        } else {
            cl.show(lbEmptyState.getParent(), "tabela");
        }
    }

    public void limparTabela() {
        modelo.setRowCount(0);
        atualizarEmptyState();
    }

    public void adicionarLinha(Object[] tableRowData) {
        Object[] nova = new Object[5];
        for (int i = 0; i < tableRowData.length && i < 4; i++) {
            nova[i] = tableRowData[i];
        }

        // Formatação visual do CPF na tabela se vier numérico de 11 dígitos
        if (nova[1] != null) {
            String cpfStr = nova[1].toString().replaceAll("[^0-9]", "");
            if (cpfStr.length() == 11) {
                nova[1] = String.format("%s.%s.%s-%s",
                        cpfStr.substring(0, 3),
                        cpfStr.substring(3, 6),
                        cpfStr.substring(6, 9),
                        cpfStr.substring(9, 11));
            }
        }

        nova[4] = "";
        modelo.addRow(nova);
        atualizarEmptyState();
    }

    private class PainelAcoes extends JPanel {
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        JLabel divisor = new JLabel("|");

        public PainelAcoes() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            setOpaque(true);

            configurar(editar, COR_DESTAQUE);
            divisor.setForeground(new Color(210, 205, 195));
            divisor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            configurar(excluir, COR_EXCLUIR);

            add(editar);
            add(divisor);
            add(excluir);
        }

        private void configurar(JButton btn, Color cor) {
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setForeground(cor);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    private class AcoesRenderer extends PainelAcoes implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Color bg = isSelected
                    ? table.getSelectionBackground()
                    : (row % 2 == 0 ? Color.WHITE : new Color(252, 250, 246));
            setBackground(bg);
            return this;
        }
    }
}