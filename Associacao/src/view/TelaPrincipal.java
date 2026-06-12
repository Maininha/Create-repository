package view;

import controller.ControllerListarAssociado;
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

    // Sub-painéis (Lincados com o Banco de Dados e Controllers)
    private PainelCadastroAssociado painelCadastro;
    private PainelCriarUsuarioSenha painelSenha;
    private PainelAssociados painelAssociados;
    private PainelEditarAssociado painelEditar;
    private PainelFinanceiro painelFinanceiro;
    private PainelNovaMovimentacao painelNovaMovimentacao;
    private PainelResumoFinanceiro painelResumoFinanceiro;

    // Controller
    private ControllerListarAssociado controllerListar;

    // Cores Customizadas da Identidade Visual Premium
    private final Color COR_MENU = new Color(43, 22, 7);
    private final Color COR_ATIVO = new Color(185, 120, 30);

    public TelaPrincipal() {

        // 1. Configurações estruturais imediatas do JFrame
        setTitle("Sistema de Gestão Comunidade Quilombola");
        setSize(1366, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Força a janela a permitir maximização e iniciar em tela cheia
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // 2. 🔥 Inicializa apenas o esqueleto visual básico (Menu e Topo) que são instantâneos
        criarMenu();
        criarTopo();

        // 3. 🔥 ABRE A TELA NA HORA: Remove o delay visual e esconde a tela de login imediatamente
        setVisible(true);

        // 4. 🔥 PROCESSO EM SEGUNDO PLANO: Toda a carga pesada de criação de painéis e tabelas
        // que buscam dados no banco agora roda numa Thread separada para nunca travar o clique do Login
        new Thread(() -> {
            try {
                // Instancia e organiza as abas do CardLayout por trás dos panos
                criarConteudo();
                ConfigurarAcoesMenu();

                // Conecta o banco de dados e inicializa os controladores
                AssociadoDAO dao = new AssociadoDAO();
                controllerListar = new ControllerListarAssociado(
                        painelAssociados,
                        painelEditar,
                        TelaPrincipal.this,
                        dao
                );

                // 5. 🔥 Sincroniza o fim do processamento de volta com a interface gráfica
                SwingUtilities.invokeLater(() -> {
                    // Atualiza a interface gráfica para acoplar os componentes criados em background
                    painelConteudo.revalidate();
                    painelConteudo.repaint();

                    // Dispara a contagem dos dados do banco para os cards do painel de início
                    PainelInicio.dispararAtualizacaoAutomatica();
                });

            } catch (Exception e) {
                System.out.println("Erro no carregamento assíncrono da TelaPrincipal: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    // ================= MENU LATERAL CONFIGURADO =================
    private void criarMenu() {

        JPanel menu = new JPanel();
        menu.setLayout(null);

        // Altura dinâmica usando o getHeight() para acompanhar o monitor expandido
        menu.setPreferredSize(new Dimension(220, getHeight()));
        menu.setBackground(COR_MENU);
        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(70, 40, 15)));

        // Faixa decorativa no topo do menu lateral
        JPanel faixa = new JPanel();
        faixa.setBackground(new Color(205, 145, 55));
        faixa.setBounds(0, 0, 220, 5);
        menu.add(faixa);

        // Componente de Logotipo ou Fallback de Texto
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
        menu.add(logo);

        // Separador visual abaixo da logo
        JSeparator separador = new JSeparator();
        separador.setBounds(20, 125, 180, 1);
        separador.setForeground(new Color(205, 145, 55));
        menu.add(separador);

        // Criando botões com as alturas (Y) reajustadas de forma linear
        btInicio = criarBotao("Início", 140, true);
        btAssociados = criarBotao("Associados", 200, false);
        btFinanceiro = criarBotao("Financeiro", 260, false);
        btRelatorios = criarBotao("Relatórios", 320, false);

        // Vetor de botões atualizado para gerenciar a troca de estados (Ativo/Inativo)
        todosBotoes = new JButton[]{btInicio, btAssociados, btFinanceiro, btRelatorios};

        for (JButton bt : todosBotoes) {
            menu.add(bt);
        }

        add(menu, BorderLayout.WEST);
    }

    // Fábrica de botões com efeito dinâmico de Hover (Mouse Entered/Exited)
    private JButton criarBotao(String texto, int y, boolean ativo) {
        JButton bt = new JButton(texto);
        bt.setBounds(20, y, 170, 45);
        bt.setHorizontalAlignment(SwingConstants.LEFT);
        bt.setFocusPainted(false);
        bt.setBorderPainted(false);
        bt.setForeground(Color.WHITE);
        bt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bt.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (ativo) {
            bt.setBackground(COR_ATIVO);
        } else {
            bt.setBackground(COR_MENU);
        }

        bt.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (bt.getBackground().equals(COR_MENU)) {
                    bt.setBackground(new Color(60, 35, 15));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!bt.getBackground().equals(COR_ATIVO)) {
                    bt.setBackground(COR_MENU);
                }
            }
        });

        return bt;
    }

    // ================= CONFIGURAÇÃO DE NAVEGAÇÃO INTERNA DO MENU =================
    private void ConfigurarAcoesMenu() {
        btInicio.addActionListener(e -> {
            selecionarBotao(btInicio);
            card.show(painelConteudo, "inicio");
        });

        btAssociados.addActionListener(e -> {
            selecionarBotao(btAssociados);
            card.show(painelConteudo, "associados");
        });

        btFinanceiro.addActionListener(e -> {
            selecionarBotao(btFinanceiro);
            card.show(painelConteudo, "financeiro");
        });

        btRelatorios.addActionListener(e -> {
            selecionarBotao(btRelatorios);
            if (painelResumoFinanceiro != null) {
                painelResumoFinanceiro.focarAba(0);
            }
            card.show(painelConteudo, "relatorios");
        });
    }

    // ================= TOPO PREMIUM CONFIGURADO =================
    private void criarTopo() {

        JPanel topo = new JPanel(new BorderLayout());
        topo.setPreferredSize(new Dimension(0, 80));
        topo.setBackground(COR_MENU);

        JLabel titulo = new JLabel("Sistema de Gestão da Comunidade Quilombola");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel subtitulo = new JLabel("Painel Administrativo");
        subtitulo.setForeground(new Color(210, 190, 170));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel painelTitulo = new JPanel();
        painelTitulo.setOpaque(false);
        painelTitulo.setLayout(new BoxLayout(painelTitulo, BoxLayout.Y_AXIS));
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 0));

        painelTitulo.add(titulo);
        painelTitulo.add(Box.createVerticalStrut(3));
        painelTitulo.add(subtitulo);

        JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        painelDireito.setOpaque(false);

        // ================= BOTÃO SAIR ULTRA MODERNO E ARREDONDADO =================
        btSair = new JButton("Sair") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2.setColor(new Color(210, 140, 45));
                } else {
                    g2.setColor(COR_ATIVO);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };

        btSair.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btSair.setForeground(Color.WHITE);
        btSair.setFocusPainted(false);
        btSair.setContentAreaFilled(false);
        btSair.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        btSair.setCursor(new Cursor(Cursor.HAND_CURSOR));

        painelDireito.add(btSair);

        topo.add(painelTitulo, BorderLayout.WEST);
        topo.add(painelDireito, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);
    }

    // ================= ÁREA DE CONTEÚDO COM SCROLL PERSONALIZADO =================
    private void criarConteudo() {

        card = new CardLayout();
        painelConteudo = new JPanel(card);

        // Instancia os painéis ativos
        painelCadastro = new PainelCadastroAssociado();
        painelSenha = new PainelCriarUsuarioSenha();
        painelAssociados = new PainelAssociados();
        painelEditar = new PainelEditarAssociado();
        painelFinanceiro = new PainelFinanceiro();
        painelNovaMovimentacao = new PainelNovaMovimentacao();
        painelResumoFinanceiro = new PainelResumoFinanceiro();

        // Envolve em JScrollPanes os painéis mantidos
        JScrollPane scrollInicio = new JScrollPane(new PainelInicio(this));
        JScrollPane scrollAssociados = new JScrollPane(painelAssociados);
        JScrollPane scrollCadastro = new JScrollPane(painelCadastro);
        JScrollPane scrollSenha = new JScrollPane(painelSenha);
        JScrollPane scrollEditar = new JScrollPane(painelEditar);
        JScrollPane scrollFinanceiro = new JScrollPane(painelFinanceiro);
        JScrollPane scrollRelatorios = new JScrollPane(painelResumoFinanceiro);
        JScrollPane scrollNovaMovimentacao = new JScrollPane(painelNovaMovimentacao);

        JScrollPane[] todosScrolls = {
                scrollInicio, scrollAssociados, scrollCadastro, scrollSenha,
                scrollEditar, scrollFinanceiro, scrollRelatorios, scrollNovaMovimentacao
        };

        for (JScrollPane scroll : todosScrolls) {
            scroll.setBorder(null);
            scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
            scroll.getHorizontalScrollBar().setUI(new ScrollBarCustomUI());
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
            scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
        }

        // Mapeia as chaves de navegação do CardLayout
        painelConteudo.add(scrollInicio, "inicio");
        painelConteudo.add(scrollAssociados, "associados");
        painelConteudo.add(scrollCadastro, "cadastros");
        painelConteudo.add(scrollSenha, "criarSenha");
        painelConteudo.add(scrollEditar, "editar");
        painelConteudo.add(scrollFinanceiro, "financeiro");
        painelConteudo.add(scrollRelatorios, "relatorios");
        painelConteudo.add(scrollNovaMovimentacao, "novaMovimentacao");

        add(painelConteudo, BorderLayout.CENTER);
    }

    public void selecionarBotao(JButton botaoSelecionado) {
        for (JButton bt : todosBotoes) {
            if (bt != null) {
                bt.setBackground(COR_MENU);
            }
        }
        if (botaoSelecionado != null) {
            botaoSelecionado.setBackground(COR_ATIVO);
        }
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