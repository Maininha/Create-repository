package view;

import controller.ControllerListarAssociado;
import controller.ControllerCadastroAssociado;
import model.AssociadoDAO;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TelaPrincipal extends JFrame {

    // Botões do Menu Lateral
    private JButton btInicio;
    private JButton btAssociados;
    private JButton btFinanceiro;
    private JButton btRelatorios;
    private JButton btSair;
    private JButton[] todosBotoes;

    // Gerenciamento de Layout
    private JPanel painelConteudo;
    private CardLayout card;

    // Sub-painéis (Ligados com o Banco de Dados e Controllers)
    private PainelCadastroAssociado painelCadastro;
    private PainelCriarUsuarioSenha painelSenha;
    private PainelAssociados painelAssociados;
    private PainelEditarAssociado painelEditar;
    private PainelFinanceiro painelFinanceiro;
    private PainelNovaMovimentacao painelNovaMovimentacao;
    private PainelResumoFinanceiro painelResumoFinanceiro;

    // Controllers
    private ControllerListarAssociado controllerListar;
    private ControllerCadastroAssociado controllerCadastro;

    // Cores Customizadas da Identidade Visual Premium Aplicadas
    private final Color COR_MENU = new Color(43, 22, 7);
    private final Color COR_ATIVO = new Color(185, 120, 30); // Dourado Premium Restaurado

    public TelaPrincipal() {
        setTitle("Sistema de Gestão Quilombola");
        setSize(1366, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        // 1. Inicializa e monta a interface gráfica com as estilizações avançadas
        configurarMenuLateral();
        configurarTopo();
        criarConteudo();

        // 2. Exibe a tela imediatamente
        setVisible(true);

        // 3. Processamento pesado de Banco de Dados executado em Background Thread
        new Thread(() -> {
            try {
                ConfigurarAcoesMenu();

                // Inicialização do DAO de Associados
                AssociadoDAO dao = new AssociadoDAO();

                // Inicializa o controlador de listagem
                controllerListar = new ControllerListarAssociado(painelAssociados, painelEditar, this, dao);

                // Atualiza de forma assíncrona os contadores do painel inicial
                if (painelConteudo != null) {
                    Component[] componentes = painelConteudo.getComponents();
                    for (Component comp : componentes) {
                        if (comp instanceof JScrollPane) {
                            Component view = ((JScrollPane) comp).getViewport().getView();
                            if (view instanceof PainelInicio) {
                                ((PainelInicio) view).recarregarDadosBanco();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro no carregamento assíncrono de dados: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void configurarMenuLateral() {
        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(null); // Layout absoluto para bater com as coordenadas da estilização
        menuLateral.setPreferredSize(new Dimension(220, 0));
        menuLateral.setBackground(COR_MENU);
        menuLateral.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(70, 40, 15)));

        // Faixa dourada decorativa no topo do menu
        JPanel faixa = new JPanel();
        faixa.setBackground(new Color(205, 145, 55));
        faixa.setBounds(0, 0, 220, 5);
        menuLateral.add(faixa);

        // JLabel dinâmico para receber a imagem completa da logo ou texto fallback
        JLabel logo = new JLabel();
        logo.setBounds(20, 20, 180, 100);
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            URL imgUrl = getClass().getResource("/imagens/logo.png");
            if (imgUrl != null) {
                ImageIcon iconOriginal = new ImageIcon(imgUrl);
                Image imgRedimensionada = iconOriginal.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
                logo.setIcon(new ImageIcon(imgRedimensionada));
            } else {
                logo.setText("<html><center>ASSOCIAÇÃO<br>QUILOMBOLA</center></html>");
                logo.setForeground(Color.WHITE);
                logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar a imagem da logo: " + e.getMessage());
        }
        menuLateral.add(logo);

        // Separador estético abaixo da logo
        JSeparator separador = new JSeparator();
        separador.setBounds(20, 125, 180, 1);
        separador.setForeground(new Color(205, 145, 55));
        menuLateral.add(separador);

        // Inicialização dos botões posicionados por coordenadas absolutas estáveis
        btInicio = criarBotaoMenu("Início", 140, true);
        btAssociados = criarBotaoMenu("Associados", 200, false);
        btFinanceiro = criarBotaoMenu("Financeiro", 260, false);
        btRelatorios = criarBotaoMenu("Relatórios", 320, false);

        // Botão de deslogar posicionado na parte inferior ou na sequência do menu absoluto
        btSair = criarBotaoMenu("Sair", 380, false);
        btSair.setForeground(new Color(240, 150, 150));

        todosBotoes = new JButton[]{btInicio, btAssociados, btFinanceiro, btRelatorios, btSair};

        for (JButton btn : todosBotoes) {
            menuLateral.add(btn);
        }

        add(menuLateral, BorderLayout.WEST);
    }

    private JButton criarBotaoMenu(String texto, int y, boolean ativo) {
        JButton btn = new JButton(texto);
        btn.setBounds(20, y, 170, 50);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (ativo) {
            btn.setBackground(COR_ATIVO);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(COR_MENU);
            btn.setForeground(new Color(220, 210, 200));
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.getBackground().equals(COR_MENU)) {
                    btn.setBackground(new Color(60, 35, 15));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(COR_ATIVO)) {
                    btn.setBackground(COR_MENU);
                }
            }
        });

        return btn;
    }

    private void configurarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setPreferredSize(new Dimension(0, 80));
        topo.setBackground(COR_MENU);

        JLabel titulo = new JLabel("Sistema de Gestão da Comunidade Quilombola");
        titulo.setForeground(new Color(255, 248, 235));
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitulo = new JLabel("Painel Administrativo");
        subtitulo.setForeground(new Color(200, 170, 110));
        subtitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel painelTitulo = new JPanel();
        painelTitulo.setOpaque(false);
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 0));

        painelTitulo.add(titulo);
        painelTitulo.add(Box.createVerticalStrut(3));
        painelTitulo.add(subtitulo);

        topo.add(painelTitulo, BorderLayout.WEST);
        add(topo, BorderLayout.NORTH);
    }

    private void criarConteudo() {
        card = new CardLayout();
        painelConteudo = new JPanel(card);
        painelConteudo.setBackground(Color.WHITE);

        // Painéis com referências cruzadas
        painelCadastro = new PainelCadastroAssociado(this);
        painelSenha = new PainelCriarUsuarioSenha(this);

        // Instanciação dos sub-painéis internos
        painelAssociados = new PainelAssociados();
        painelEditar = new PainelEditarAssociado();
        painelFinanceiro = new PainelFinanceiro();
        painelNovaMovimentacao = new PainelNovaMovimentacao();
        painelResumoFinanceiro = new PainelResumoFinanceiro();

        // Envelopamento em JScrollPanes customizados e limpos (Borda null, ScrollBarCustomUI)
        JScrollPane scrollInicio = customizarScrollPane(new PainelInicio(this));
        JScrollPane scrollAssociados = customizarScrollPane(painelAssociados);
        JScrollPane scrollCadastro = customizarScrollPane(painelCadastro);
        JScrollPane scrollSenha = customizarScrollPane(painelSenha);
        JScrollPane scrollEditar = customizarScrollPane(painelEditar);
        JScrollPane scrollFinanceiro = customizarScrollPane(painelFinanceiro);
        JScrollPane scrollNovaMov = customizarScrollPane(painelNovaMovimentacao);
        JScrollPane scrollResumo = customizarScrollPane(painelResumoFinanceiro);

        // Vinculação das chaves textuais do CardLayout
        painelConteudo.add(scrollInicio, "inicio");
        painelConteudo.add(scrollCadastro, "cadastroAssociado");
        painelConteudo.add(scrollSenha, "criarUsuarioSenha");
        painelConteudo.add(scrollAssociados, "listarAssociados");
        painelConteudo.add(scrollEditar, "editarAssociado");
        painelConteudo.add(scrollFinanceiro, "painelFinanceiro");
        painelConteudo.add(scrollNovaMov, "novaMovimentacao");
        painelConteudo.add(scrollResumo, "resumoFinanceiro");

        add(painelConteudo, BorderLayout.CENTER);
    }

    // Auxiliar para centralizar a aplicação repetitiva da UI customizada nas barras de rolagem
    private JScrollPane customizarScrollPane(JPanel painel) {
        JScrollPane scroll = new JScrollPane(painel);
        scroll.setBorder(null);

        scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
        scroll.getHorizontalScrollBar().setUI(new ScrollBarCustomUI());

        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));

        return scroll;
    }

    private void ConfigurarAcoesMenu() {
        btInicio.addActionListener(e -> {
            alternarCorBotao(btInicio);
            card.show(painelConteudo, "inicio");

            Component[] componentes = painelConteudo.getComponents();
            for (Component comp : componentes) {
                if (comp instanceof JScrollPane) {
                    Component view = ((JScrollPane) comp).getViewport().getView();
                    if (view instanceof PainelInicio) {
                        ((PainelInicio) view).recarregarDadosBanco();
                    }
                }
            }
        });

        btAssociados.addActionListener(e -> {
            alternarCorBotao(btAssociados);
            card.show(painelConteudo, "listarAssociados");
        });

        btFinanceiro.addActionListener(e -> {
            alternarCorBotao(btFinanceiro);
            card.show(painelConteudo, "painelFinanceiro");
        });

        btRelatorios.addActionListener(e -> {
            alternarCorBotao(btRelatorios);
            card.show(painelConteudo, "resumoFinanceiro");
        });
    }

    public void alternarCorBotao(JButton botaoSelecionado) {
        if (todosBotoes == null) return;
        for (JButton btn : todosBotoes) {
            if (btn != null) {
                btn.setBackground(COR_MENU);
                btn.setForeground(new Color(220, 210, 200));
            }
        }
        if (botaoSelecionado != null) {
            botaoSelecionado.setBackground(COR_ATIVO);
            botaoSelecionado.setForeground(Color.WHITE);
        }
    }

    public void selecionarBotao(JButton botaoSelecionado) {
        alternarCorBotao(botaoSelecionado);
    }

    // ================= GETTERS DOS BOTÕES =================
    public JButton getBtInicio() { return btInicio; }
    public JButton getBtAssociados() { return btAssociados; }
    public JButton getBtFinanceiro() { return btFinanceiro; }
    public JButton getBtRelatorios() { return btRelatorios; }
    public JButton getBtSair() { return btSair; }

    // ================= GETTERS DOS PAINÉIS =================
    public CardLayout getCard() { return card; }
    public JPanel getPainelConteudo() { return painelConteudo; }
    public PainelCadastroAssociado getPainelCadastro() { return painelCadastro; }
    public PainelCriarUsuarioSenha getPainelSenha() { return painelSenha; }
    public PainelAssociados getPainelAssociados() { return painelAssociados; }
    public PainelEditarAssociado getPainelEditar() { return painelEditar; }
    public PainelFinanceiro getPainelFinanceiro() { return painelFinanceiro; }
    public PainelNovaMovimentacao getPainelNovaMovimentacao() { return painelNovaMovimentacao; }
    public PainelResumoFinanceiro getPainelResumoFinanceiro() { return painelResumoFinanceiro; }
    public ControllerListarAssociado getControllerListar() { return controllerListar; }
}