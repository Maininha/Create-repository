package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
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
    private JButton btnVoltarLink; // 🔥 O link "Voltar" (implementado como JButton transparente)

    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> sorter;

    private final String PLACEHOLDER = "Buscar por Nome ou CPF";

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
        setLayout(null);
        setBackground(new Color(248, 245, 240));

        criarTitulo();
        criarPesquisa();
        criarTabela();
    }

    private void criarTitulo() {
        JLabel titulo = new JLabel("Associados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(70, 40, 15));
        titulo.setBounds(40, 25, 300, 40);
        add(titulo);
    }

    private void criarPesquisa() {
        // Retornou ao tamanho original de 760 de largura
        pesquisa = new JTextField(PLACEHOLDER);
        pesquisa.setBounds(40, 98, 760, 40);
        pesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pesquisa.setForeground(Color.GRAY);
        pesquisa.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 10, 0, 0)
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
        add(pesquisa);

        // 🔥 NOVO: Link discreto "Voltar" posicionado logo abaixo do campo de busca (Y: 140)
        btnVoltarLink = new JButton("← Voltar para todos os associados");
        btnVoltarLink.setBounds(40, 140, 250, 22);
        btnVoltarLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVoltarLink.setForeground(new Color(185, 120, 30)); // Cor dourada padrão
        btnVoltarLink.setContentAreaFilled(false);
        btnVoltarLink.setBorderPainted(false);
        btnVoltarLink.setFocusPainted(false);
        btnVoltarLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarLink.setHorizontalAlignment(SwingConstants.LEFT);
        btnVoltarLink.setVisible(false); // Fica oculto até uma busca ser feita

        // Efeito Hover para o Link
        btnVoltarLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnVoltarLink.setForeground(new Color(70, 40, 15)); // Muda para o marrom escuro
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnVoltarLink.setForeground(new Color(185, 120, 30)); // Volta ao dourado
            }
        });

        btnVoltarLink.addActionListener(e -> limparFiltroCompleto());
        add(btnVoltarLink);

        // Botão Buscar
        buscar = new JButton("Buscar");
        buscar.setBounds(820, 98, 140, 40);
        buscar.setBackground(new Color(185, 120, 30));
        buscar.setForeground(Color.WHITE);
        buscar.setFocusPainted(false);
        buscar.setBorderPainted(false);
        buscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        buscar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buscar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                buscar.setBackground(new Color(205, 145, 55));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                buscar.setBackground(new Color(185, 120, 30));
            }
        });
        add(buscar);

        // Botão Cadastrar
        JButton btnNovoAssociado = new JButton("Cadastrar");
        btnNovoAssociado.setBounds(980, 98, 140, 40);
        btnNovoAssociado.setBackground(new Color(185, 120, 30));
        btnNovoAssociado.setForeground(Color.WHITE);
        btnNovoAssociado.setFocusPainted(false);
        btnNovoAssociado.setBorderPainted(false);
        btnNovoAssociado.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNovoAssociado.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnNovoAssociado.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnNovoAssociado.setBackground(new Color(205, 145, 55));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnNovoAssociado.setBackground(new Color(185, 120, 30));
            }
        });

        btnNovoAssociado.addActionListener(e -> {
            TelaPrincipal tela = (TelaPrincipal) SwingUtilities.getWindowAncestor(PainelAssociados.this);
            if (tela != null) {
                tela.getCard().show(tela.getPainelConteudo(), "cadastros");
                try {
                    tela.selecionarBotao(null);
                } catch (Exception ex) {}
            }
        });
        add(btnNovoAssociado);
    }

    private void criarTabela() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBounds(40, 170, 1080, 420);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        String[] colunas = {"Nome", "CPF", "Endereço", "Data", "Ações"};

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
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 245, 240));
                }
                return c;
            }
        };

        tabela.setRowHeight(45);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.getTableHeader().setBackground(new Color(205, 145, 55));
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        sorter = new TableRowSorter<>(modelo);
        tabela.setRowSorter(sorter);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 4; i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(center);
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

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        card.add(scroll, BorderLayout.CENTER);
        add(card);

        buscar.addActionListener(e -> filtrar());
        pesquisa.addActionListener(e -> filtrar());
    }

    // ================= FILTRO COM LINK DINÂMICO =================
    private void filtrar() {
        String txt = pesquisa.getText().trim();
        if (txt.isEmpty() || txt.equals(PLACEHOLDER)) {
            limparFiltroCompleto();
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txt, 0, 1));
            btnVoltarLink.setVisible(true); // Faz o link "Voltar" aparecer abaixo da caixa
            repaint();
        }
    }

    private void limparFiltroCompleto() {
        sorter.setRowFilter(null);
        pesquisa.setText(PLACEHOLDER);
        pesquisa.setForeground(Color.GRAY);
        btnVoltarLink.setVisible(false); // Oculta o link pois a lista já está completa
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

    private class PainelAcoes extends JPanel {
        JButton editar = new JButton("Editar");
        JButton excluir = new JButton("Excluir");
        JLabel divisor = new JLabel("|");

        public PainelAcoes() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 5));
            setBackground(Color.WHITE);
            setOpaque(true);

            configurar(editar, new Color(205, 145, 55));
            divisor.setForeground(new Color(210, 210, 210));
            divisor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            configurar(excluir, new Color(70, 40, 15));

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
        }
    }

    private class AcoesRenderer extends PainelAcoes implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Color bg = isSelected
                    ? table.getSelectionBackground()
                    : (row % 2 == 0 ? Color.WHITE : new Color(248, 245, 240));
            setBackground(bg);
            return this;
        }
    }
}