package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PainelAssociados extends JPanel {

    private JTable tabela;
    private JTextField pesquisa;
    private JButton buscar;
    private JButton btnVoltarLink;

    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> sorter;

    private final String PLACEHOLDER = "Buscar por Nome ou CPF";
    private final Border BORDA_CLEAN = BorderFactory.createLineBorder(new Color(230, 225, 218), 1, true);

    // Cores Premium do Sistema unificadas
    private final Color COR_PRIMARIA = new Color(43, 22, 7);
    private final Color COR_DESTAQUE = new Color(185, 120, 30);
    private final Color COR_HOVER = new Color(205, 145, 55);

    // ================= MVC LISTENER =================
    public interface AcoesListener {
        void editar(int rowModel);
        void excluir(int rowModel);
    }

    private AcoesListener listener;

    public void setAcoesListener(AcoesListener listener) {
        this.listener = listener;
    }

    public JTable getTabela() {
        return tabela;
    }

    public JButton getBtnBuscar() {
        return buscar;
    }

    public JTextField getTxtBusca() {
        return pesquisa;
    }

    // ================= CONSTRUTOR =================
    public PainelAssociados() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Construção das seções estruturadas (Fluidez em resoluções altas)
        criarCabecalhoEPesquisa();
        criarPainelTabela();
    }

    private void criarCabecalhoEPesquisa() {
        JPanel painelSuperior = new JPanel();
        painelSuperior.setLayout(new BoxLayout(painelSuperior, BoxLayout.Y_AXIS));
        painelSuperior.setOpaque(false);

        // Título da Seção
        JLabel titulo = new JLabel("Gestão de Associados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(COR_PRIMARIA);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelSuperior.add(titulo);
        painelSuperior.add(Box.createVerticalStrut(15));

        // Linha de Comandos (Pesquisa + Botões de Ação)
        JPanel linhaComandos = new JPanel(new BorderLayout(15, 0));
        linhaComandos.setOpaque(false);
        linhaComandos.setAlignmentX(Component.LEFT_ALIGNMENT);

        pesquisa = new JTextField(PLACEHOLDER);
        pesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pesquisa.setForeground(Color.GRAY);
        pesquisa.setBorder(BorderFactory.createCompoundBorder(
                BORDA_CLEAN,
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));

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
                }
            }
        });
        linhaComandos.add(pesquisa, BorderLayout.CENTER);

        // Agrupamento dos botões à direita
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setOpaque(false);

        buscar = new JButton("Buscar");
        estilizarBotaoAcao(buscar);
        buscar.addActionListener(e -> filtrar());
        pesquisa.addActionListener(e -> filtrar());
        painelBotoes.add(buscar);

        JButton btnNovoAssociado = new JButton("Cadastrar");
        estilizarBotaoAcao(btnNovoAssociado);
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

        // Link para limpar filtros e retornar à listagem padrão
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
            public void mouseEntered(MouseEvent e) {
                btnVoltarLink.setForeground(COR_PRIMARIA);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnVoltarLink.setForeground(COR_DESTAQUE);
            }
        });

        btnVoltarLink.addActionListener(e -> limparFiltroCompleto());
        painelSuperior.add(btnVoltarLink);

        add(painelSuperior, BorderLayout.NORTH);
    }

    private void criarPainelTabela() {
        JPanel cardContainer = new JPanel(new BorderLayout());
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

        // Customização do Cabeçalho
        JTableHeader header = tabela.getTableHeader();
        header.setBackground(COR_PRIMARIA);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(modelo);
        tabela.setRowSorter(sorter);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 4; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tabela.getColumnModel().getColumn(4).setCellRenderer(new AcoesRenderer());

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
                        if (listener != null) listener.excluir(modelRow);
                    }
                }
            }
        });

        // Configuração do ScrollPane com barras finas e fluidas premium
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
        scroll.getHorizontalScrollBar().setUI(new ScrollBarCustomUI());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));

        cardContainer.add(scroll, BorderLayout.CENTER);
        add(cardContainer, BorderLayout.CENTER);
    }

    private void estilizarBotaoAcao(JButton btn) {
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setBackground(COR_DESTAQUE);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COR_DESTAQUE);
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
            revalidate();
            repaint();
        }
    }

    private void limparFiltroCompleto() {
        sorter.setRowFilter(null);
        pesquisa.setText(PLACEHOLDER);
        pesquisa.setForeground(Color.GRAY);
        btnVoltarLink.setVisible(false);
        revalidate();
        repaint();
    }

    public void limparTabela() {
        modelo.setRowCount(0);
    }

    public void adicionarLinha(Object[] tableRowData) {
        Object[] nova = new Object[5];
        for (int i = 0; i < tableRowData.length && i < 4; i++) {
            nova[i] = tableRowData[i];
        }
        nova[4] = "";
        modelo.addRow(nova);
    }

    // ================= COMPONENTS DE RENDERIZAÇÃO DE CELL =================
    private class PainelAcoes extends JPanel {
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        JLabel divisor = new JLabel("|");

        public PainelAcoes() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 8));
            setOpaque(true);

            configurar(editar, COR_DESTAQUE);
            divisor.setForeground(new Color(210, 205, 195));
            divisor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            configurar(excluir, COR_PRIMARIA);

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