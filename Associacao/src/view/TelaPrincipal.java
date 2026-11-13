package view;

import controller.ControllerListarAssociado;
import controller.ControllerCadastroAssociado;
import controller.ControllerLogin;
import model.AssociadoDAO;
import model.UsuarioDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class TelaPrincipal extends JFrame {

    private JButton btInicio;
    private JButton btAssociados;
    private JButton btFinanceiro;
    private JButton btRelatorios;
    private JButton btSair;
    private JButton[] todosBotoes;

    private JPanel painelConteudo;
    private CardLayout card;

    private PainelCadastroAssociado painelCadastro;
    private PainelCriarUsuarioSenha painelSenha;
    private PainelAssociados painelAssociados;
    private PainelEditarAssociado painelEditar;
    private PainelFinanceiro painelFinanceiro;
    private PainelNovaMovimentacao painelNovaMovimentacao;
    private PainelResumoFinanceiro painelResumoFinanceiro;

    private ControllerListarAssociado controllerListar;
    private ControllerCadastroAssociado controllerCadastro;

    private final Color COR_MENU = new Color(43, 22, 7);
    private final Color COR_ATIVO = new Color(185, 120, 30);
    private final Color COR_TEXTO_INATIVO = new Color(220, 210, 200);
    private final Color COR_FUNDO_CREME = new Color(250, 246, 240);
    private final Color COR_BOTAO_BEGE = new Color(245, 238, 228);
    private final Color COR_BORDA_SUAVE = new Color(225, 215, 200);

    public TelaPrincipal() {
        setTitle("Sistema de Gestão Quilombola");
        setSize(1366, 768);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        estilizarTooltipsSistema();
        configurarMenuLateral();
        configurarTopo();
        criarConteudo();
        configurarAtalhoAjudaGlobal();

        setVisible(true);

        new Thread(() -> {
            try {
                ConfigurarAcoesMenu();

                AssociadoDAO dao = new AssociadoDAO();
                controllerListar = new ControllerListarAssociado(painelAssociados, painelEditar, this, dao);
                controllerCadastro = new ControllerCadastroAssociado(this, painelCadastro, painelSenha, dao);

                PainelInicio.dispararAtualizacaoAutomatica();
                PainelResumoFinanceiro.dispararAtualizacaoAutomatica();

            } catch (Exception e) {
                System.err.println("Erro no carregamento assíncrono de dados: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void estilizarTooltipsSistema() {
        UIManager.put("ToolTip.background", COR_MENU);
        UIManager.put("ToolTip.foreground", new Color(250, 245, 238));
        UIManager.put("ToolTip.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("ToolTip.border", BorderFactory.createCompoundBorder(
                new LineBorder(COR_ATIVO, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        ToolTipManager.sharedInstance().setInitialDelay(250);
        ToolTipManager.sharedInstance().setDismissDelay(10000);
    }

    private void configurarMenuLateral() {
        JPanel menuLateral = new JPanel();
        menuLateral.setLayout(null);
        menuLateral.setPreferredSize(new Dimension(220, 0));
        menuLateral.setBackground(COR_MENU);
        menuLateral.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(70, 40, 15)));

        JPanel faixa = new JPanel();
        faixa.setBackground(new Color(205, 145, 55));
        faixa.setBounds(0, 0, 220, 5);
        menuLateral.add(faixa);

        JLabel logo = new JLabel();
        logo.setBounds(20, 20, 180, 100);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logo.setToolTipText("Voltar para a Tela Inicial");

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

        // Evento de clique na logo para voltar à tela inicial
        logo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                alternarCorBotao(btInicio);
                card.show(painelConteudo, "inicio");
                PainelInicio.dispararAtualizacaoAutomatica();
            }
        });

        menuLateral.add(logo);

        JSeparator separador = new JSeparator();
        separador.setBounds(20, 125, 180, 1);
        separador.setForeground(new Color(205, 145, 55));
        menuLateral.add(separador);

        btInicio = criarBotaoMenu("Início", "inicio", 140, true);
        btAssociados = criarBotaoMenu("Associados", "associados", 200, false);
        btFinanceiro = criarBotaoMenu("Financeiro", "financeiro", 260, false);
        btRelatorios = criarBotaoMenu("Relatórios", "relatorios", 320, false);

        btSair = criarBotaoMenu("Sair", "sair", 380, false);
        btSair.setForeground(COR_TEXTO_INATIVO);

        todosBotoes = new JButton[]{btInicio, btAssociados, btFinanceiro, btRelatorios, btSair};

        for (JButton btn : todosBotoes) {
            menuLateral.add(btn);
        }

        add(menuLateral, BorderLayout.WEST);
    }

    private JButton criarBotaoMenu(String texto, String tipoIcone, int y, boolean ativo) {
        JButton btn = new JButton(texto);
        btn.setBounds(20, y, 175, 50);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(12);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setIcon(criarIconeMenu(tipoIcone, btn));

        if (ativo) {
            btn.setBackground(COR_ATIVO);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(COR_MENU);
            btn.setForeground(COR_TEXTO_INATIVO);
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

    // ==========================================
    // ÍCONES VETORIAIS DO MENU LATERAL
    // ==========================================
    private Icon criarIconeMenu(String tipo, JButton botaoRef) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                // Usa a mesma cor do texto do botão (branco quando ativo, bege claro quando inativo)
                g2.setColor(botaoRef.getForeground());
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                switch (tipo) {
                    case "inicio":
                        // Telhado
                        g2.drawLine(x + 2, y + 9, x + 9, y + 3);
                        g2.drawLine(x + 9, y + 3, x + 16, y + 9);
                        // Corpo da casa
                        g2.drawPolyline(new int[]{x + 4, x + 4, x + 14, x + 14},
                                new int[]{y + 8, y + 16, y + 16, y + 8}, 4);
                        // Porta
                        g2.drawRect(x + 7, y + 11, 4, 5);
                        break;

                    case "associados":
                        // Pessoa principal (cabeça e ombros)
                        g2.drawOval(x + 4, y + 3, 6, 6);
                        g2.drawArc(x + 1, y + 11, 12, 8, 0, 180);
                        // Segunda pessoa ao fundo
                        g2.drawArc(x + 10, y + 4, 5, 5, -60, 240);
                        g2.drawArc(x + 11, y + 11, 6, 6, 0, 110);
                        break;

                    case "financeiro":
                        // Símbolo de cifrão ($)
                        g2.drawLine(x + 9, y + 2, x + 9, y + 16);
                        g2.drawArc(x + 5, y + 4, 8, 5, 45, 225);
                        g2.drawArc(x + 5, y + 9, 8, 5, 45, -225);
                        break;

                    case "relatorios":
                        // Documento com linhas de gráfico/texto
                        g2.drawRoundRect(x + 3, y + 2, 12, 14, 2, 2);
                        g2.drawLine(x + 6, y + 6, x + 12, y + 6);
                        g2.drawLine(x + 6, y + 9, x + 12, y + 9);
                        g2.drawLine(x + 6, y + 12, x + 10, y + 12);
                        break;

                    case "sair":
                        // Porta aberta e seta saindo para a direita
                        g2.drawPolyline(new int[]{x + 9, x + 3, x + 3, x + 9},
                                new int[]{y + 3, y + 3, y + 15, y + 15}, 4);
                        g2.drawLine(x + 7, y + 9, x + 15, y + 9);
                        g2.drawLine(x + 12, y + 6, x + 15, y + 9);
                        g2.drawLine(x + 12, y + 12, x + 15, y + 9);
                        break;
                }

                g2.dispose();
            }

            @Override
            public int getIconWidth() { return 18; }

            @Override
            public int getIconHeight() { return 18; }
        };
    }

    private void configurarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setPreferredSize(new Dimension(0, 80));
        topo.setBackground(COR_MENU);
        topo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 40, 15)));

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

        // Botão de interrogação (?) no canto direito do topo
        JPanel painelDireito = new JPanel(new GridBagLayout());
        painelDireito.setOpaque(false);
        painelDireito.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));

        JButton btnAjuda = criarBotaoAjudaTopo();
        painelDireito.add(btnAjuda);

        topo.add(painelTitulo, BorderLayout.WEST);
        topo.add(painelDireito, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);
    }

    private JButton criarBotaoAjudaTopo() {
        JButton btn = new JButton() {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                g2.setColor(hover ? COR_ATIVO : new Color(65, 38, 18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.setColor(hover ? Color.WHITE : new Color(205, 145, 55, 160));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                int size = 18;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(x + 1, y + 1, size - 2, size - 2);

                Font fonteAjuda = new Font("Segoe UI", Font.BOLD, 12);
                g2.setFont(fonteAjuda);
                FontMetrics fm = g2.getFontMetrics(fonteAjuda);
                int textX = x + (size - fm.stringWidth("?")) / 2;
                int textY = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString("?", textX, textY);

                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("<html><b>Central de Autoajuda (F1)</b><br>Abre o passo a passo das tarefas e a lista de atalhos.</html>");
        btn.addActionListener(e -> exibirGuiaPassoAPasso());
        return btn;
    }

    private void configurarAtalhoAjudaGlobal() {
        JRootPane root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "ajudaGlobal");
        am.put("ajudaGlobal", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exibirGuiaPassoAPasso();
            }
        });
    }

    public void exibirGuiaPassoAPasso() {
        JDialog dialog = new JDialog(this, "Central de Autoajuda", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(780, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(COR_MENU);
        topo.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblTituloModal = new JLabel("Central de Autoajuda");
        lblTituloModal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloModal.setForeground(Color.WHITE);

        JLabel lblSubModal = new JLabel("Consulte o fluxo operacional de cada módulo e os atalhos de teclado.");
        lblSubModal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubModal.setForeground(new Color(220, 200, 175));

        JPanel textosTopo = new JPanel(new GridLayout(2, 1, 0, 2));
        textosTopo.setOpaque(false);
        textosTopo.add(lblTituloModal);
        textosTopo.add(lblSubModal);
        topo.add(textosTopo, BorderLayout.WEST);

        dialog.add(topo, BorderLayout.NORTH);

        JPanel corpo = new JPanel(new CardLayout());
        corpo.setBackground(COR_FUNDO_CREME);

        JScrollPane scroll1 = criarScrollGuia(criarCartaoEtapas("1. Gestão de Associados",
                "Como cadastrar, editar e consultar membros da comunidade:",
                new String[][]{
                        {"1", "Acesse o Menu Lateral", "Clique em 'Associados' na barra marrom à esquerda."},
                        {"2", "Preencha a Ficha", "Clique em 'Novo Cadastro' e informe nome completo, CPF, telefone e endereço."},
                        {"3", "Salve e Valide", "Confirme o cadastro. O indicador 'Quantidade de Clientes' será atualizado na hora."}
                }));

        JScrollPane scroll2 = criarScrollGuia(criarCartaoEtapas("2. Controle Financeiro",
                "Como lançar entradas, despesas e acompanhar o saldo:",
                new String[][]{
                        {"1", "Abra o Módulo Financeiro", "Selecione 'Financeiro' no menu lateral esquerdo."},
                        {"2", "Escolha o Tipo de Lançamento", "Defina se a movimentação é uma Entrada (Receita) ou Saída (Despesa)."},
                        {"3", "Registre os Valores", "Informe valor, data e categoria. Os dados alimentam o Relatório Financeiro."}
                }));

        JScrollPane scroll3 = criarScrollGuia(criarCartaoEtapas("3. Emissão de Relatórios PDF",
                "Como exportar documentos oficiais da comunidade:",
                new String[][]{
                        {"1", "Escolha o Relatório", "Na tela Início, localize 'Resumo de Relatórios' ou acesse a aba 'Relatórios'."},
                        {"2", "Exporte com 1 Clique ou Atalho", "Clique em 'Visualizar relatório' ou pressione Ctrl+1, Ctrl+2 ou Ctrl+3."},
                        {"3", "Localize o Arquivo", "O PDF formatado é salvo automaticamente na pasta 'Downloads' do computador."}
                }));

        JScrollPane scroll4 = criarScrollGuia(criarPainelAtalhos());

        corpo.add(scroll1, "TAREFA_1");
        corpo.add(scroll2, "TAREFA_2");
        corpo.add(scroll3, "TAREFA_3");
        corpo.add(scroll4, "ATALHOS");

        JScrollPane[] scrolls = {scroll1, scroll2, scroll3, scroll4};

        JPanel menuEsquerdo = new JPanel();
        menuEsquerdo.setLayout(new BoxLayout(menuEsquerdo, BoxLayout.Y_AXIS));
        menuEsquerdo.setBackground(new Color(240, 234, 224));
        menuEsquerdo.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COR_BORDA_SUAVE));
        menuEsquerdo.setPreferredSize(new Dimension(215, 0));

        CardLayout cl = (CardLayout) corpo.getLayout();
        JButton[] botoesMenu = new JButton[4];
        String[] labelsMenu = {"1. Associados", "2. Financeiro", "3. Relatórios PDF", "Atalhos de Teclado"};
        String[] chavesMenu = {"TAREFA_1", "TAREFA_2", "TAREFA_3", "ATALHOS"};

        for (int i = 0; i < labelsMenu.length; i++) {
            final int idx = i;
            JButton item = new JButton(labelsMenu[i]);
            item.setMaximumSize(new Dimension(215, 44));
            item.setHorizontalAlignment(SwingConstants.LEFT);
            item.setBorder(new EmptyBorder(0, 20, 0, 10));
            item.setFont(new Font("Segoe UI", Font.BOLD, 13));
            item.setFocusPainted(false);
            item.setCursor(new Cursor(Cursor.HAND_CURSOR));
            item.setBackground(i == 0 ? COR_ATIVO : new Color(240, 234, 224));
            item.setForeground(i == 0 ? Color.WHITE : COR_MENU);

            item.addActionListener(e -> {
                cl.show(corpo, chavesMenu[idx]);
                SwingUtilities.invokeLater(() -> scrolls[idx].getVerticalScrollBar().setValue(0));

                for (int j = 0; j < botoesMenu.length; j++) {
                    botoesMenu[j].setBackground(j == idx ? COR_ATIVO : new Color(240, 234, 224));
                    botoesMenu[j].setForeground(j == idx ? Color.WHITE : COR_MENU);
                }
            });
            botoesMenu[i] = item;
            menuEsquerdo.add(item);
        }

        dialog.add(menuEsquerdo, BorderLayout.WEST);
        dialog.add(corpo, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        rodape.setBackground(Color.WHITE);
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COR_BORDA_SUAVE));

        JButton btnFechar = new JButton("Fechar Guia");
        btnFechar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnFechar.setBackground(COR_MENU);
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFocusPainted(false);
        btnFechar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFechar.setBorder(new EmptyBorder(8, 18, 8, 18));
        btnFechar.addActionListener(e -> dialog.dispose());
        rodape.add(btnFechar);

        dialog.add(rodape, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JScrollPane criarScrollGuia(JPanel conteudo) {
        conteudo.setOpaque(true);
        conteudo.setBackground(COR_FUNDO_CREME);
        conteudo.setBorder(new EmptyBorder(20, 24, 24, 24));

        JScrollPane scroll = new JScrollPane(conteudo);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUI(new ScrollBarCustomUI());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        return scroll;
    }

    private JPanel criarCartaoEtapas(String titulo, String subtitulo, String[][] etapas) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);

        JLabel lblT = new JLabel(titulo);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblT.setForeground(COR_MENU);
        lblT.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblS = new JLabel(subtitulo);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblS.setForeground(Color.GRAY);
        lblS.setAlignmentX(Component.LEFT_ALIGNMENT);

        painel.add(lblT);
        painel.add(Box.createRigidArea(new Dimension(0, 4)));
        painel.add(lblS);
        painel.add(Box.createRigidArea(new Dimension(0, 16)));

        for (String[] etapa : etapas) {
            JPanel cardPasso = new JPanel(new BorderLayout(14, 0));
            cardPasso.setBackground(Color.WHITE);
            cardPasso.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COR_BORDA_SUAVE, 1, true),
                    new EmptyBorder(12, 14, 12, 14)
            ));
            cardPasso.setMaximumSize(new Dimension(500, 74));
            cardPasso.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel num = new JLabel(etapa[0], SwingConstants.CENTER);
            num.setPreferredSize(new Dimension(30, 30));
            num.setOpaque(true);
            num.setBackground(COR_BOTAO_BEGE);
            num.setForeground(COR_ATIVO);
            num.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JPanel textoPasso = new JPanel(new GridLayout(2, 1, 0, 2));
            textoPasso.setOpaque(false);
            JLabel t = new JLabel(etapa[1]);
            t.setFont(new Font("Segoe UI", Font.BOLD, 13));
            t.setForeground(COR_MENU);
            JLabel d = new JLabel("<html>" + etapa[2] + "</html>");
            d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            d.setForeground(new Color(90, 90, 90));

            textoPasso.add(t);
            textoPasso.add(d);

            cardPasso.add(num, BorderLayout.WEST);
            cardPasso.add(textoPasso, BorderLayout.CENTER);

            painel.add(cardPasso);
            painel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        return painel;
    }

    private JPanel criarPainelAtalhos() {
        String[][] atalhos = {
                {"F1", "Abre esta Central de Autoajuda com o passo a passo das tarefas."},
                {"Ctrl + 1", "Gera e baixa o Relatório de Associados em formato PDF (na tela Início)."},
                {"Ctrl + 2", "Gera e baixa o Relatório Financeiro em formato PDF (na tela Início)."},
                {"Ctrl + 3", "Gera e baixa o Relatório de Atividades em formato PDF (na tela Início)."}
        };

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);

        JLabel lblT = new JLabel("Atalhos de Teclado do Sistema");
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblT.setForeground(COR_MENU);
        lblT.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(lblT);
        painel.add(Box.createRigidArea(new Dimension(0, 14)));

        for (String[] item : atalhos) {
            JPanel linha = new JPanel(new BorderLayout(15, 0));
            linha.setBackground(Color.WHITE);
            linha.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COR_BORDA_SUAVE, 1, true),
                    new EmptyBorder(10, 14, 10, 14)
            ));
            linha.setMaximumSize(new Dimension(500, 48));
            linha.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel tecla = new JLabel(item[0], SwingConstants.CENTER);
            tecla.setPreferredSize(new Dimension(75, 26));
            tecla.setOpaque(true);
            tecla.setBackground(COR_MENU);
            tecla.setForeground(Color.WHITE);
            tecla.setFont(new Font("Segoe UI", Font.BOLD, 12));

            JLabel desc = new JLabel(item[1]);
            desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            desc.setForeground(new Color(60, 60, 60));

            linha.add(tecla, BorderLayout.WEST);
            linha.add(desc, BorderLayout.CENTER);
            painel.add(linha);
            painel.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        return painel;
    }

    private void criarConteudo() {
        card = new CardLayout();
        painelConteudo = new JPanel(card);
        painelConteudo.setBackground(Color.WHITE);

        painelCadastro = new PainelCadastroAssociado(this);
        painelSenha = new PainelCriarUsuarioSenha(this);
        painelAssociados = new PainelAssociados();
        painelEditar = new PainelEditarAssociado();
        painelFinanceiro = new PainelFinanceiro();
        painelNovaMovimentacao = new PainelNovaMovimentacao();
        painelResumoFinanceiro = new PainelResumoFinanceiro();

        JScrollPane scrollInicio = customizarScrollPane(new PainelInicio(this));
        JScrollPane scrollAssociados = customizarScrollPane(painelAssociados);
        JScrollPane scrollCadastro = customizarScrollPane(painelCadastro);
        JScrollPane scrollSenha = customizarScrollPane(painelSenha);
        JScrollPane scrollEditar = customizarScrollPane(painelEditar);
        JScrollPane scrollFinanceiro = customizarScrollPane(painelFinanceiro);
        JScrollPane scrollNovaMov = customizarScrollPane(painelNovaMovimentacao);
        JScrollPane scrollResumo = customizarScrollPane(painelResumoFinanceiro);

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
            PainelInicio.dispararAtualizacaoAutomatica();
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
            PainelResumoFinanceiro.dispararAtualizacaoAutomatica();
        });

        for (java.awt.event.ActionListener al : btSair.getActionListeners()) {
            btSair.removeActionListener(al);
        }

        btSair.addActionListener(e -> {
            Object[] opcoes = {"Sim", "Não"};

            int resposta = JOptionPane.showOptionDialog(
                    this,
                    "Deseja realmente sair?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[1]
            );

            if (resposta == 0) {
                for (java.awt.event.WindowListener wl : this.getWindowListeners()) {
                    this.removeWindowListener(wl);
                }

                SwingUtilities.invokeLater(() -> {
                    this.setVisible(false);
                    this.dispose();

                    TelaLogin login = new TelaLogin();
                    UsuarioDAO usuarioDao = new UsuarioDAO();

                    new ControllerLogin(login, usuarioDao);

                    login.setVisible(true);
                });
            }
        });
    }

    public void navegarPara(String nomeCard, JButton botaoMenu) {
        alternarCorBotao(botaoMenu);
        card.show(painelConteudo, nomeCard);
    }

    public void alternarCorBotao(JButton botaoSelecionado) {
        if (todosBotoes == null) return;
        for (JButton btn : todosBotoes) {
            if (btn != null) {
                btn.setBackground(COR_MENU);
                btn.setForeground(COR_TEXTO_INATIVO);
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

    public JButton getBtInicio() { return btInicio; }
    public JButton getBtAssociados() { return btAssociados; }
    public JButton getBtFinanceiro() { return btFinanceiro; }
    public JButton getBtRelatorios() { return btRelatorios; }
    public JButton getBtSair() { return btSair; }

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
    public ControllerCadastroAssociado getControllerCadastro() { return controllerCadastro; }
}