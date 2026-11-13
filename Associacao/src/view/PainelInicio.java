package view;

import controller.AtividadeController;
import controller.InicioController;
import model.Associado;
import model.AssociadoDAO;
import model.Atividade;
import model.Financeiro;
import model.FinanceiroDAO;
import util.GeradorPdfRelatorio;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PainelInicio extends JPanel {

    private static PainelInicio instanciaAtiva;
    private TelaPrincipal telaPrincipal;
    private InicioController inicioController;

    private JPanel bannerContainer;
    private JLabel fotoBanner;

    private JLabel lblContadorAssociados;
    private JLabel lblContadorCadastros;
    private JLabel lblContadorRelatorios;

    private JLabel lblIconeCard1;
    private JLabel lblIconeCard2;
    private JLabel lblIconeCard3;

    private JButton btnRelatorioAssociados;
    private JButton btnRelatorioFinanceiro;
    private JButton btnRelatorioAtividades;

    private static final Color COR_FUNDO_CREME = new Color(250, 246, 240);
    private static final Color COR_MARROM_MEDIO = new Color(92, 58, 33);
    private static final Color COR_BOTAO_BEGE = new Color(245, 238, 228);
    private static final Color COR_BOTAO_HOVER = new Color(235, 224, 208);
    private static final Color COR_BORDA_SUAVE = new Color(225, 215, 200);

    public PainelInicio(TelaPrincipal telaPrincipal) {
        instanciaAtiva = this;
        this.telaPrincipal = telaPrincipal;
        this.inicioController = new InicioController();

        setLayout(null);
        setBackground(COR_FUNDO_CREME);
        setPreferredSize(new Dimension(1180, 760));

        criarBanner();
        criarCards();
        criarRelatorios();

        configurarAtalhosTeclado();

        inicializarCargaParalelaCompleta();
    }

    private void criarBanner() {
        bannerContainer = new JPanel();
        bannerContainer.setLayout(null);
        bannerContainer.setBounds(40, 20, 1100, 240);
        bannerContainer.setOpaque(true);
        bannerContainer.setBackground(COR_MARROM_MEDIO);
        bannerContainer.setBorder(new BordaSombra());

        fotoBanner = new JLabel();
        fotoBanner.setBounds(0, 0, 1100, 240);

        JPanel sombra = new JPanel();
        sombra.setBackground(new Color(0, 0, 0, 115));
        sombra.setBounds(0, 0, 1100, 240);

        JLabel texto1 = new JLabel(
                "<html>" +
                        "FORTALECENDO A <font color='#f5a623'>ORGANIZAÇÃO</font><br>" +
                        "E A <font color='#f5a623'>TRANSPARÊNCIA</font><br>" +
                        "DE NOSSA COMUNIDADE." +
                        "</html>"
        );
        texto1.setForeground(Color.WHITE);
        texto1.setFont(new Font("Segoe UI", Font.BOLD, 26));
        texto1.setBounds(40, 32, 620, 108);

        JLabel texto2 = new JLabel("Gestão eficiente • Participação ativa • Futuro melhor.");
        texto2.setForeground(new Color(240, 240, 240));
        texto2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        texto2.setBounds(40, 150, 600, 28);

        JPanel line = new JPanel();
        line.setBackground(new Color(245, 166, 35));
        line.setBounds(40, 192, 280, 2);

        bannerContainer.add(texto1);
        bannerContainer.add(texto2);
        bannerContainer.add(line);
        bannerContainer.add(sombra);
        bannerContainer.add(fotoBanner);

        add(bannerContainer);
    }

    private void criarCards() {
        lblContadorAssociados = new JLabel("...");
        lblContadorCadastros = new JLabel("...");
        lblContadorRelatorios = new JLabel("...");

        lblIconeCard1 = new JLabel("", SwingConstants.CENTER);
        lblIconeCard2 = new JLabel("", SwingConstants.CENTER);
        lblIconeCard3 = new JLabel("", SwingConstants.CENTER);

        JPanel c1 = criarCard(
                "Quantidade de Clientes", lblContadorAssociados,
                "Total de clientes cadastrados na comunidade.",
                new Color(245, 238, 218), lblIconeCard1,
                "<html><b>Total de Associados</b><br>Clique para abrir a lista completa de associados cadastrados.</html>",
                () -> {
                    if (telaPrincipal != null) {
                        telaPrincipal.navegarPara("listarAssociados", telaPrincipal.getBtAssociados());
                    }
                }
        );

        JPanel c2 = criarCard(
                "Realizar Cadastros", lblContadorCadastros,
                "Cadastros realizados este mês.",
                new Color(228, 243, 232), lblIconeCard2,
                "<html><b>Novos Cadastros</b><br>Clique para abrir a tela de cadastro de associados.</html>",
                () -> {
                    if (telaPrincipal != null) {
                        telaPrincipal.navegarPara("cadastroAssociado", telaPrincipal.getBtAssociados());
                    }
                }
        );

        JPanel c3 = criarCard(
                "Relatórios", lblContadorRelatorios,
                "Relatórios gerados este mês.",
                new Color(228, 233, 247), lblIconeCard3,
                "<html><b>Resumo Financeiro & Relatórios</b><br>Clique para acessar o painel de relatórios ou use <b>Ctrl+1..3</b>.</html>",
                () -> {
                    if (telaPrincipal != null) {
                        telaPrincipal.navegarPara("resumoFinanceiro", telaPrincipal.getBtRelatorios());
                        PainelResumoFinanceiro.dispararAtualizacaoAutomatica();
                    }
                }
        );

        c1.setBounds(40, 280, 340, 190);
        c2.setBounds(420, 280, 340, 190);
        c3.setBounds(800, 280, 340, 190);

        add(c1);
        add(c2);
        add(c3);
    }

    private JPanel criarCard(String titulo, JLabel lblValor, String descricao, Color corIconeFundo,
                             JLabel lblIcone, String tooltipAjuda, Runnable acaoClique) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setOpaque(false);
        card.setBorder(new BordaSombra());
        card.setToolTipText(tooltipAjuda);

        if (acaoClique != null) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    acaoClique.run();
                }
            });
        }

        JPanel painelIcone = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(corIconeFundo);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        painelIcone.setLayout(new BorderLayout());
        painelIcone.setBounds(20, 22, 58, 58);
        painelIcone.add(lblIcone);

        JLabel lbTitulo = new JLabel(titulo);
        lbTitulo.setBounds(92, 22, 230, 24);
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbTitulo.setForeground(new Color(50, 50, 50));

        lblValor.setBounds(92, 46, 150, 44);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblValor.setForeground(COR_MARROM_MEDIO);

        JLabel lbDesc = new JLabel("<html>" + descricao + "</html>");
        lbDesc.setBounds(92, 98, 220, 38);
        lbDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbDesc.setForeground(Color.GRAY);
        lbDesc.setVerticalAlignment(SwingConstants.TOP);

        JLabel seta = new JLabel("›");
        seta.setFont(new Font("Segoe UI", Font.BOLD, 18));
        seta.setForeground(new Color(200, 170, 140));
        seta.setBounds(310, 142, 20, 20);

        card.add(painelIcone);
        card.add(lbTitulo);
        card.add(lblValor);
        card.add(lbDesc);
        card.add(seta);

        return card;
    }

    private void criarRelatorios() {
        JLabel lbTituloSecao = new JLabel("Resumo de Relatórios");
        lbTituloSecao.setBounds(40, 490, 260, 28);
        lbTituloSecao.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbTituloSecao.setForeground(new Color(40, 40, 40));
        add(lbTituloSecao);

        JLabel lblDicaAtalhos = new JLabel("Atalhos rápidos:  Ctrl+1 Associados   •   Ctrl+2 Financeiro   •   Ctrl+3 Atividades   •   F1 Ajuda");
        lblDicaAtalhos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDicaAtalhos.setForeground(new Color(125, 110, 95));
        lblDicaAtalhos.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDicaAtalhos.setBounds(520, 494, 620, 22);
        add(lblDicaAtalhos);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(null);
        painelPrincipal.setBackground(Color.WHITE);
        painelPrincipal.setBounds(40, 528, 1100, 155);
        painelPrincipal.setOpaque(false);
        painelPrincipal.setBorder(new BordaSombra());

        JPanel r1 = criarLinhaRelatorio("Relatório de Associados", "Visualize a lista completa de associados e suas informações.", new Color(185, 120, 30), "Ctrl+1", 1);
        JPanel r2 = criarLinhaRelatorio("Relatório Financeiro", "Acompanhe entradas, saídas e saldo financeiro.", new Color(46, 125, 50), "Ctrl+2", 2);
        JPanel r3 = criarLinhaRelatorio("Relatório de Atividades", "Consulte as atividades realizadas na comunidade.", new Color(21, 101, 192), "Ctrl+3", 3);

        r1.setBounds(15, 14, 345, 126);
        r2.setBounds(375, 14, 345, 126);
        r3.setBounds(735, 14, 345, 126);

        painelPrincipal.add(r1);
        painelPrincipal.add(r2);
        painelPrincipal.add(r3);

        add(painelPrincipal);
    }

    private JPanel criarLinhaRelatorio(String titulo, String descricao, Color corTemaIndicador, String atalho, int indiceBotao) {
        JPanel subPainel = new JPanel();
        subPainel.setLayout(null);
        subPainel.setBackground(Color.WHITE);

        JPanel circuloIcone = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(corTemaIndicador.getRed(), corTemaIndicador.getGreen(), corTemaIndicador.getBlue(), 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                desenharIconeVetorial(g2, TipoIcone.DOCUMENTO, 12, 12, 16, corTemaIndicador);
                g2.dispose();
            }
        };
        circuloIcone.setBounds(15, 14, 40, 40);
        circuloIcone.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setBounds(65, 10, 260, 20);
        lblTitulo.setForeground(new Color(50, 50, 50));

        JLabel lblDesc = new JLabel("<html>" + descricao + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Color.GRAY);
        lblDesc.setBounds(65, 32, 260, 35);
        lblDesc.setVerticalAlignment(SwingConstants.TOP);

        JButton btnVisualizar = new JButton("Visualizar relatório", criarIconeBotao(TipoIcone.DOWNLOAD, new Color(80, 50, 20)));
        btnVisualizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVisualizar.setForeground(new Color(80, 50, 20));
        btnVisualizar.setBackground(COR_BOTAO_BEGE);
        btnVisualizar.setBorder(new LineBorder(COR_BORDA_SUAVE, 1, true));
        btnVisualizar.setFocusPainted(false);
        btnVisualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVisualizar.setIconTextGap(8);
        btnVisualizar.setBounds(65, 76, 165, 30);
        btnVisualizar.setToolTipText("<html><b>" + titulo + " (" + atalho + ")</b><br>Exporta o relatório em PDF diretamente para sua pasta Downloads.</html>");

        if (indiceBotao == 1) btnRelatorioAssociados = btnVisualizar;
        else if (indiceBotao == 2) btnRelatorioFinanceiro = btnVisualizar;
        else if (indiceBotao == 3) btnRelatorioAtividades = btnVisualizar;

        btnVisualizar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (btnVisualizar.isEnabled()) btnVisualizar.setBackground(COR_BOTAO_HOVER); }
            @Override public void mouseExited(MouseEvent e)  { if (btnVisualizar.isEnabled()) btnVisualizar.setBackground(COR_BOTAO_BEGE); }
        });

        btnVisualizar.addActionListener(e -> {
            if (telaPrincipal != null) {
                btnVisualizar.setEnabled(false);
                btnVisualizar.setText("Gerando PDF...");

                new Thread(() -> {
                    boolean sucesso = true;
                    String msgErro = "";

                    try {
                        if (titulo.contains("Associados")) {
                            List<Associado> listaMembros = new AssociadoDAO().listar();
                            GeradorPdfRelatorio.gerarRelatorioAssociados(listaMembros);
                        } else if (titulo.contains("Financeiro")) {
                            List<Financeiro> listaFinancas = new FinanceiroDAO().listar("Todos", null, null);
                            GeradorPdfRelatorio.gerarRelatorioFinanceiro(listaFinancas);
                        } else if (titulo.contains("Atividades")) {
                            List<Atividade> listaCronograma = new AtividadeController().obterCronogramaAtividades();
                            GeradorPdfRelatorio.gerarRelatorioAtividades(listaCronograma);
                        }
                    } catch (Exception ex) {
                        sucesso = false;
                        msgErro = ex.getMessage();
                    }

                    final boolean resultadoFinal = sucesso;
                    final String erroTexto = msgErro;

                    SwingUtilities.invokeLater(() -> {
                        btnVisualizar.setEnabled(true);
                        btnVisualizar.setText("Visualizar relatório");

                        if (resultadoFinal) {
                            JOptionPane.showMessageDialog(PainelInicio.this,
                                    "PDF gerado com sucesso!\nVerifique a sua pasta de Downloads.",
                                    "Exportação Concluída", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(PainelInicio.this,
                                    "Erro ao gerar o PDF: " + erroTexto,
                                    "Falha na Exportação", JOptionPane.ERROR_MESSAGE);
                        }
                        recarregarDadosBanco();
                    });
                }).start();
            }
        });

        subPainel.add(circuloIcone);
        subPainel.add(lblTitulo);
        subPainel.add(lblDesc);
        subPainel.add(btnVisualizar);

        return subPainel;
    }

    private enum TipoIcone { DOWNLOAD, DOCUMENTO }

    private Icon criarIconeBotao(TipoIcone tipo, Color cor) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                desenharIconeVetorial(g2, tipo, x, y, 14, cor);
                g2.dispose();
            }
            @Override public int getIconWidth()  { return 14; }
            @Override public int getIconHeight() { return 14; }
        };
    }

    private void desenharIconeVetorial(Graphics2D g2, TipoIcone tipo, int x, int y, int size, Color cor) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (tipo) {
            case DOWNLOAD:
                int mid = x + size / 2;
                g2.drawLine(mid, y + 2, mid, y + size - 4);
                g2.drawLine(mid - 3, y + size - 7, mid, y + size - 4);
                g2.drawLine(mid + 3, y + size - 7, mid, y + size - 4);
                g2.drawLine(x + 2, y + size - 1, x + size - 2, y + size - 1);
                break;

            case DOCUMENTO:
                g2.drawRoundRect(x + 2, y + 1, size - 4, size - 2, 3, 3);
                g2.drawLine(x + 5, y + 5, x + size - 5, y + 5);
                g2.drawLine(x + 5, y + 8, x + size - 5, y + 8);
                g2.drawLine(x + 5, y + 11, x + size - 7, y + 11);
                break;
        }
    }

    private void configurarAtalhosTeclado() {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK), "rel1");
        am.put("rel1", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (btnRelatorioAssociados != null && btnRelatorioAssociados.isEnabled()) btnRelatorioAssociados.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK), "rel2");
        am.put("rel2", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (btnRelatorioFinanceiro != null && btnRelatorioFinanceiro.isEnabled()) btnRelatorioFinanceiro.doClick();
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK), "rel3");
        am.put("rel3", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (btnRelatorioAtividades != null && btnRelatorioAtividades.isEnabled()) btnRelatorioAtividades.doClick();
            }
        });
    }

    // =========================================================================
    // MÉTODOS DE CARGA ASSÍNCRONA DO BANCO
    // =========================================================================
    public static void dispararAtualizacaoAutomatica() {
        if (instanciaAtiva != null) {
            instanciaAtiva.recarregarDadosBanco();
        }
    }

    private void inicializarCargaParalelaCompleta() {
        new Thread(() -> {
            try {
                java.net.URL urlBanner = getClass().getResource("/imagens/banner.jpeg");
                ImageIcon iconBannerFinal = null;
                if (urlBanner != null) {
                    ImageIcon orig = new ImageIcon(urlBanner);
                    Image img = orig.getImage().getScaledInstance(1100, 240, Image.SCALE_SMOOTH);
                    iconBannerFinal = new ImageIcon(img);
                }

                ImageIcon ic1 = redimensionarIconeEficaz("/imagens/pessoas.png", 30, 30);
                ImageIcon ic2 = redimensionarIconeEficaz("/imagens/adicionarPessoas.png", 30, 30);
                ImageIcon ic3 = redimensionarIconeEficaz("/imagens/relatorioat.png", 30, 30);

                int totalAssociados = inicioController.obterTotalAssociados();
                int cadastrosMes = inicioController.obterCadastrosMes();
                int relatoriosMes = inicioController.obterRelatoriosMes();

                final ImageIcon bannerFinal = iconBannerFinal;
                SwingUtilities.invokeLater(() -> {
                    if (bannerFinal != null) {
                        fotoBanner.setIcon(bannerFinal);
                        bannerContainer.setOpaque(false);
                    }
                    if (ic1 != null) lblIconeCard1.setIcon(ic1);
                    if (ic2 != null) lblIconeCard2.setIcon(ic2);
                    if (ic3 != null) lblIconeCard3.setIcon(ic3);

                    lblContadorAssociados.setText(String.valueOf(totalAssociados));
                    lblContadorCadastros.setText(String.valueOf(cadastrosMes));
                    lblContadorRelatorios.setText(String.valueOf(relatoriosMes));
                });

            } catch (Exception e) {
                System.out.println("Erro na carga paralela do painel: " + e.getMessage());
            }
        }).start();
    }

    private ImageIcon redimensionarIconeEficaz(String caminho, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(caminho);
            if (url != null) {
                ImageIcon original = new ImageIcon(url);
                return new ImageIcon(original.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {}
        return null;
    }

    public void recarregarDadosBanco() {
        new Thread(() -> {
            try {
                int totalAssociados = inicioController.obterTotalAssociados();
                int cadastrosMes = inicioController.obterCadastrosMes();
                int relatoriosMes = inicioController.obterRelatoriosMes();

                SwingUtilities.invokeLater(() -> {
                    lblContadorAssociados.setText(String.valueOf(totalAssociados));
                    lblContadorCadastros.setText(String.valueOf(cadastrosMes));
                    lblContadorRelatorios.setText(String.valueOf(relatoriosMes));
                });
            } catch (Exception e) {
                System.out.println("Erro ao atualizar dados: " + e.getMessage());
            }
        }).start();
    }
}