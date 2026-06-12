package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import controller.InicioController;
import controller.AtividadeController;
import model.Atividade;
import model.Associado;
import model.AssociadoDAO;
import model.Financeiro;
import model.FinanceiroDAO;
import util.GeradorPdfRelatorio;

public class PainelInicio extends JPanel {

    private static PainelInicio instanciaAtiva;

    // Componentes dinâmicos do banco
    private JLabel lblContadorAssociados;
    private JLabel lblContadorCadastros;
    private JLabel lblContadorRelatorios;

    // Elementos gráficos para injeção assíncrona pura
    private JLabel fotoBanner;
    private JLabel lblIconeCard1;
    private JLabel lblIconeCard2;
    private JLabel lblIconeCard3;
    private JLabel lblIconeRel1;
    private JLabel lblIconeRel2;
    private JLabel lblIconeRel3;
    private JPanel bannerContainer;

    private InicioController inicioController;
    private TelaPrincipal telaPrincipal;

    // BORDA DE SUBSTITUIÇÃO: Uma linha clean que consome 0% de processamento gráfico
    private final Border BORDA_CLEAN = BorderFactory.createLineBorder(new Color(230, 225, 218), 1, true);

    public PainelInicio(TelaPrincipal telaPrincipal) {
        instanciaAtiva = this;
        this.telaPrincipal = telaPrincipal;
        this.inicioController = new InicioController();

        setLayout(null);
        setBackground(new Color(248, 245, 240));

        int larguraDinamica = Toolkit.getDefaultToolkit().getScreenSize().width - 220;
        setPreferredSize(new Dimension(larguraDinamica, 850));

        // 1. Montagem estrutural instantânea
        criarBanner();
        criarCards();
        criarRelatorios();

        // 2. Dispara a carga pesada de dados e imagens em background isolado
        inicializarCargaParalelaCompleta();
    }

    public static void dispararAtualizacaoAutomatica() {
        if (instanciaAtiva != null) {
            instanciaAtiva.recarregarDadosBanco();
        }
    }

    private void inicializarCargaParalelaCompleta() {
        new Thread(() -> {
            try {
                // Carregamento e redimensionamento do Banner em segundo plano
                java.net.URL urlBanner = getClass().getResource("/imagens/banner.jpeg");
                ImageIcon iconBannerFinal = null;
                if (urlBanner != null) {
                    ImageIcon orig = new ImageIcon(urlBanner);
                    Image img = orig.getImage().getScaledInstance(1100, 250, Image.SCALE_SMOOTH);
                    iconBannerFinal = new ImageIcon(img);
                }

                // Carregamento ultra rápido dos ícones em lote
                ImageIcon ic1 = redimensionarIconeEficaz("/imagens/pessoas.png", 30, 30);
                ImageIcon ic2 = redimensionarIconeEficaz("/imagens/adicionarPessoas.png", 30, 30);
                ImageIcon ic3 = redimensionarIconeEficaz("/imagens/relatorioat.png", 30, 30);

                ImageIcon ir1 = redimensionarIconeEficaz("/imagens/pessoas.png", 22, 22);
                ImageIcon ir2 = redimensionarIconeEficaz("/imagens/cifrao.png", 22, 22);
                ImageIcon ir3 = redimensionarIconeEficaz("/imagens/relatorio.jpg", 22, 22);

                // 🔥 AJUSTADO AQUI: Chamadas sincronizadas com o InicioController padrão
                int totalAssociados = inicioController.obterTotalAssociados();
                int cadastrosMes = inicioController.obterCadastrosMes();
                int relatoriosMes = inicioController.obterRelatoriosMes();

                // Entrega dos dados montados de forma síncrona com a UI (sem causar gargalos)
                final ImageIcon bannerFinal = iconBannerFinal;
                SwingUtilities.invokeLater(() -> {
                    if (bannerFinal != null) {
                        fotoBanner.setIcon(bannerFinal);
                        bannerContainer.setOpaque(false);
                    }

                    if (ic1 != null) lblIconeCard1.setIcon(ic1);
                    if (ic2 != null) lblIconeCard2.setIcon(ic2);
                    if (ic3 != null) lblIconeCard3.setIcon(ic3);

                    if (ir1 != null) lblIconeRel1.setIcon(ir1);
                    if (ir2 != null) lblIconeRel2.setIcon(ir2);
                    if (ir3 != null) lblIconeRel3.setIcon(ir3);

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
                return new ImageIcon(original.getImage().getScaledInstance(w, h, Image.SCALE_FAST));
            }
        } catch (Exception e) {}
        return null;
    }

    public void recarregarDadosBanco() {
        new Thread(() -> {
            try {
                // 🔥 AJUSTADO AQUI TAMBÉM: Igualando as chamadas de método
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

    private void criarBanner() {
        bannerContainer = new JPanel();
        bannerContainer.setLayout(null);
        bannerContainer.setBounds(40, 20, 1100, 250);
        bannerContainer.setOpaque(true);
        bannerContainer.setBackground(new Color(60, 40, 20));
        bannerContainer.setBorder(BORDA_CLEAN);

        fotoBanner = new JLabel();
        fotoBanner.setBounds(0, 0, 1100, 250);

        JPanel sombra = new JPanel();
        sombra.setBackground(new Color(0, 0, 0, 110));
        sombra.setBounds(0, 0, 1100, 250);

        JLabel texto1 = new JLabel(
                "<html>" +
                        "FORTALECENDO A <font color='#f5a623'>ORGANIZAÇÃO</font><br>" +
                        "E A <font color='#f5a623'>TRANSPARÊNCIA</font><br>" +
                        "DE NOSSA COMUNIDADE." +
                        "</html>"
        );
        texto1.setForeground(Color.WHITE);
        texto1.setFont(new Font("Segoe UI", Font.BOLD, 26));
        texto1.setBounds(40, 35, 600, 110);

        JLabel texto2 = new JLabel("Gestão eficiente • Participação activa • Futuro melhor.");
        texto2.setForeground(Color.WHITE);
        texto2.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        texto2.setBounds(40, 155, 600, 30);

        JPanel linha = new JPanel();
        linha.setBackground(new Color(245, 166, 35));
        linha.setBounds(40, 200, 280, 2);

        bannerContainer.add(texto1);
        bannerContainer.add(texto2);
        bannerContainer.add(linha);
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

        JPanel c1 = criarCard("Quantidade de Associados", lblContadorAssociados, "Total de clientes cadastrados na comunidade.", new Color(245, 238, 218), lblIconeCard1);
        JPanel c2 = criarCard("Cadastros", lblContadorCadastros, "Cadastros realizados este mês.", new Color(228, 243, 232), lblIconeCard2);
        JPanel c3 = criarCard("Relatórios", lblContadorRelatorios, "Relatórios salvos no sistema.", new Color(228, 233, 247), lblIconeCard3);

        c1.setBounds(40, 290, 340, 200);
        c2.setBounds(420, 290, 340, 200);
        c3.setBounds(800, 290, 340, 200);

        add(c1);
        add(c2);
        add(c3);
    }

    private JPanel criarCard(String titulo, JLabel lblValor, String descricao, Color corIconeFundo, JLabel lblIcone) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setBorder(BORDA_CLEAN);

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
        painelIcone.setBounds(20, 25, 60, 60);
        painelIcone.add(lblIcone);

        JLabel lbTitulo = new JLabel(titulo);
        lbTitulo.setBounds(95, 25, 230, 25);
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbTitulo.setForeground(new Color(50, 50, 50));

        lblValor.setBounds(95, 50, 150, 45);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValor.setForeground(new Color(60, 40, 20));

        JLabel lbDesc = new JLabel("<html>" + descricao + "</html>");
        lbDesc.setBounds(95, 105, 220, 40);
        lbDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbDesc.setForeground(Color.GRAY);
        lbDesc.setVerticalAlignment(SwingConstants.TOP);

        JLabel seta = new JLabel(">");
        seta.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        seta.setForeground(new Color(200, 170, 140));
        seta.setBounds(310, 145, 20, 20);

        card.add(painelIcone);
        card.add(lbTitulo);
        card.add(lblValor);
        card.add(lbDesc);
        card.add(seta);

        return card;
    }

    private void criarRelatorios() {
        JLabel lbTituloSecao = new JLabel("Resumo de Relatórios");
        lbTituloSecao.setBounds(40, 510, 300, 30);
        lbTituloSecao.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbTituloSecao.setForeground(new Color(40, 40, 40));
        add(lbTituloSecao);

        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(null);
        painelPrincipal.setBackground(Color.WHITE);
        painelPrincipal.setBounds(40, 550, 1100, 160);
        painelPrincipal.setOpaque(true);
        painelPrincipal.setBorder(BORDA_CLEAN);

        lblIconeRel1 = new JLabel("", SwingConstants.CENTER);
        lblIconeRel2 = new JLabel("", SwingConstants.CENTER);
        lblIconeRel3 = new JLabel("", SwingConstants.CENTER);

        JPanel r1 = criarLinhaRelatorio("Relatório de Associados", "Visualize a lista completa de associados e suas informações.", lblIconeRel1);
        JPanel r2 = criarLinhaRelatorio("Relatório Financeiro", "Acompanhe entradas, saídas e saldo financeiro.", lblIconeRel2);
        JPanel r3 = criarLinhaRelatorio("Relatório de Atividades", "Consulte as atividades administrativas realizadas na comunidade.", lblIconeRel3);

        r1.setBounds(15, 15, 345, 130);
        r2.setBounds(375, 15, 345, 130);
        r3.setBounds(735, 15, 345, 130);

        painelPrincipal.add(r1);
        painelPrincipal.add(r2);
        painelPrincipal.add(r3);

        add(painelPrincipal);
    }

    private JPanel criarLinhaRelatorio(String titulo, String descricao, JLabel lblIcone) {
        JPanel subPainel = new JPanel();
        subPainel.setLayout(null);
        subPainel.setBackground(Color.WHITE);

        JPanel circuloIcone = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(242, 242, 245));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        circuloIcone.setLayout(new BorderLayout());
        circuloIcone.setBounds(10, 10, 45, 45);
        circuloIcone.add(lblIcone);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setBounds(65, 10, 260, 20);
        lblTitulo.setForeground(new Color(50, 50, 50));

        JLabel lblDesc = new JLabel("<html>" + descricao + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Color.GRAY);
        lblDesc.setBounds(65, 32, 260, 35);
        lblDesc.setVerticalAlignment(SwingConstants.TOP);

        JButton btnVisualizar = new JButton("Visualizar relatório");
        btnVisualizar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVisualizar.setForeground(new Color(80, 50, 20));
        btnVisualizar.setBackground(new Color(245, 238, 228));
        btnVisualizar.setBorder(new LineBorder(new Color(225, 215, 200), 1, true));
        btnVisualizar.setFocusPainted(false);
        btnVisualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVisualizar.setBounds(65, 75, 140, 28);

        btnVisualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (telaPrincipal != null) {
                    new Thread(() -> {
                        if (titulo.contains("Associados")) {
                            AssociadoDAO associadoDAO = new AssociadoDAO();
                            List<Associado> listaMembros = associadoDAO.listar();
                            GeradorPdfRelatorio.gerarRelatorioAssociados(listaMembros);

                        } else if (titulo.contains("Financeiro")) {
                            FinanceiroDAO financeiroDAO = new FinanceiroDAO();
                            List<Financeiro> listaFinancas = financeiroDAO.listar("Todos", null, null);
                            GeradorPdfRelatorio.gerarRelatorioFinanceiro(listaFinancas);

                        } else if (titulo.contains("Atividades")) {
                            AtividadeController atividadeController = new AtividadeController();
                            List<Atividade> listaCronograma = atividadeController.obterCronogramaAtividades();
                            GeradorPdfRelatorio.gerarRelatorioAtividades(listaCronograma);
                        }
                        recarregarDadosBanco();
                    }).start();
                }
            }
        });

        subPainel.add(circuloIcone);
        subPainel.add(lblTitulo);
        subPainel.add(lblDesc);
        subPainel.add(btnVisualizar);

        return subPainel;
    }
}