package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import controller.RedefinirSenhaController;

public class TelaLogin extends JFrame {

    private JLabel lbTitulo;
    private JLabel lbSubtitulo;
    private JLabel lbUsuario;
    private JLabel lbSenha;
    private JLabel lblEsqueciSenha;

    private JTextField txUsuario;
    private JPasswordField txSenha;

    private JButton btEntrar;
    private JButton btLimpar;

    private JPanel painelDireito;
    private JPanel cardLogin;

    public TelaLogin() {

        // =========================
        // CONFIGURAÇÃO DA JANELA
        // =========================

        setTitle("Sistema Quilombola");
        setSize(1000, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicia a tela maximizada
        setExtendedState(JFrame.MAXIMIZED_BOTH);


        // =========================
        // PAINEL PRINCIPAL
        // =========================

        JPanel painelPrincipal = new JPanel(new BorderLayout());


        // =========================
        // MENU LATERAL
        // =========================

        JPanel menuLateral = new JPanel();
        menuLateral.setPreferredSize(new Dimension(280, 550));
        menuLateral.setBackground(new Color(35, 18, 4));

        // Borda lateral decorativa
        menuLateral.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 0, 2,
                        new Color(185, 120, 30)
                )
        );

        menuLateral.setLayout(null);


        // =========================
        // LOGO
        // =========================

        try {

            ImageIcon logoOriginal =
                    new ImageIcon(
                            getClass().getResource("/imagens/logo.png")
                    );

            Image img =
                    logoOriginal.getImage()
                            .getScaledInstance(
                                    160,
                                    160,
                                    Image.SCALE_SMOOTH
                            );

            JLabel logo = new JLabel(new ImageIcon(img));

            logo.setBounds(60, 30, 160, 160);

            menuLateral.add(logo);

        } catch (Exception e) {

            JLabel erro = new JLabel("Logo não encontrada");

            erro.setForeground(Color.WHITE);
            erro.setBounds(50, 50, 180, 30);

            menuLateral.add(erro);
        }


        // =========================
        // TÍTULO DO MENU
        // =========================

        JLabel tituloMenu = new JLabel(
                "<html><center>" +
                        "<font color='#FFFFFF'>ASSOCIAÇÃO</font><br>" +
                        "<font color='#DCAA5A'>QUILOMBOLA</font>" +
                        "</center></html>"
        );

        tituloMenu.setHorizontalAlignment(SwingConstants.CENTER);
        tituloMenu.setFont(new Font("Segoe UI", Font.BOLD, 23));
        tituloMenu.setBounds(20, 210, 240, 70);


        // =========================
        // LINHA DECORATIVA 1
        // =========================

        JPanel linha = new JPanel();

        linha.setBackground(new Color(185, 120, 30));
        linha.setBounds(40, 200, 200, 2);


        // =========================
        // LINHA DECORATIVA 2
        // =========================

        JPanel linha2 = new JPanel();

        linha2.setBackground(new Color(110, 70, 20));
        linha2.setBounds(75, 308, 130, 1);

        menuLateral.add(linha2);


        // =========================
        // DESCRIÇÃO
        // =========================

        JLabel descricao = new JLabel(
                "<html><center>" +
                        "Gestão Organizada<br>" +
                        "e Transparente<br>" +
                        "</center></html>"
        );

        descricao.setHorizontalAlignment(SwingConstants.CENTER);
        descricao.setForeground(new Color(220, 220, 220));
        descricao.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        descricao.setBounds(35, 330, 200, 50);


        menuLateral.add(tituloMenu);
        menuLateral.add(linha);
        menuLateral.add(descricao);


        // =========================
        // PAINEL DIREITO
        // =========================

        painelDireito = new JPanel(new GridBagLayout());

        painelDireito.setBackground(
                new Color(233, 226, 218)
        );


        // =========================
        // CARD DE LOGIN
        // =========================

        cardLogin = new JPanel();

        cardLogin.setLayout(null);
        cardLogin.setPreferredSize(new Dimension(500, 420));
        cardLogin.setBackground(Color.WHITE);


        // Borda do card
        cardLogin.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                new Color(215, 205, 190),
                                1,
                                true
                        ),

                        new EmptyBorder(
                                30,
                                30,
                                30,
                                30
                        )
                )
        );


        // =========================
        // LINHA DO TÍTULO
        // =========================

        JPanel linhaTitulo = new JPanel();

        linhaTitulo.setBackground(
                new Color(185, 120, 30)
        );

        linhaTitulo.setBounds(
                175,
                70,
                150,
                3
        );


        // =========================
        // TÍTULO
        // =========================

        lbTitulo = new JLabel(
                "Bem-vindo",
                SwingConstants.CENTER
        );

        lbTitulo.setBounds(
                0,
                25,
                500,
                35
        );

        lbTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        lbTitulo.setForeground(
                new Color(35, 18, 4)
        );


        // =========================
        // SUBTÍTULO
        // =========================

        lbSubtitulo = new JLabel(
                "Acesse sua conta",
                SwingConstants.CENTER
        );

        lbSubtitulo.setBounds(
                0,
                85,
                500,
                20
        );

        lbSubtitulo.setForeground(
                new Color(166, 104, 24)
        );


        // =========================
        // CAMPO USUÁRIO
        // =========================

        lbUsuario = new JLabel("Usuário");

        lbUsuario.setBounds(
                50,
                120,
                100,
                20
        );

        txUsuario = new JTextField();
        txUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JPanel containerUsuario = criarCampoComIcone(txUsuario, "/imagens/images.png");

        containerUsuario.setBounds(
                50,
                145,
                400,
                45
        );


        // =========================
        // CAMPO SENHA
        // =========================

        lbSenha = new JLabel("Senha");

        lbSenha.setBounds(
                50,
                220,
                100,
                20
        );

        txSenha = new JPasswordField();
        txSenha.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JPanel containerSenha = criarCampoComIcone(txSenha, "/imagens/senha.png");

        containerSenha.setBounds(
                50,
                245,
                400,
                45
        );


        // =========================
        // BOTÃO ENTRAR
        // =========================

        btEntrar = new JButton("Entrar");

        btEntrar.setBounds(
                30,
                320,
                200,
                45
        );

        btEntrar.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        btEntrar.setBackground(
                new Color(185, 120, 30)
        );

        btEntrar.setForeground(Color.WHITE);

        btEntrar.setFocusPainted(false);
        btEntrar.setBorderPainted(false);

        btEntrar.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );


        // =========================
        // HOVER BOTÃO ENTRAR
        // =========================

        btEntrar.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {

                        btEntrar.setBackground(
                                new Color(205, 145, 55)
                        );
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                        btEntrar.setBackground(
                                new Color(185, 120, 30)
                        );
                    }
                }
        );


        // =========================
        // BOTÃO LIMPAR
        // =========================

        btLimpar = new JButton("Limpar");

        btLimpar.setBounds(
                270,
                320,
                200,
                45
        );

        btLimpar.setBackground(Color.WHITE);

        btLimpar.setForeground(
                new Color(35, 18, 4)
        );

        btLimpar.setBorder(
                BorderFactory.createLineBorder(
                        new Color(185, 120, 30),
                        1,
                        true
                )
        );

        btLimpar.setFocusPainted(false);

        btLimpar.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );


        // =========================
        // AÇÃO BOTÃO LIMPAR
        // =========================

        btLimpar.addActionListener(e -> limparCampos());


        // =========================
        // HOVER BOTÃO LIMPAR
        // =========================

        btLimpar.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {

                        btLimpar.setBackground(
                                new Color(248, 245, 240)
                        );
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                        btLimpar.setBackground(
                                Color.WHITE
                        );
                    }
                }
        );


        // =========================
        // LINK ESQUECI SENHA
        // =========================

        lblEsqueciSenha = new JLabel(
                "<html><u>Esqueci minha senha?</u></html>"
        );

        lblEsqueciSenha.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        lblEsqueciSenha.setForeground(
                new Color(110, 70, 20)
        );

        lblEsqueciSenha.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        lblEsqueciSenha.setBounds(
                0,
                380,
                500,
                25
        );

        lblEsqueciSenha.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        // =========================
        // AÇÃO ESQUECI SENHA
        // =========================

        lblEsqueciSenha.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        PainelRedefinirSenhaGestor painelRedefinir =
                                new PainelRedefinirSenhaGestor();

                        new RedefinirSenhaController(
                                painelRedefinir
                        );

                        painelRedefinir
                                .getLblVoltar()
                                .addMouseListener(
                                        new MouseAdapter() {

                                            @Override
                                            public void mouseClicked(
                                                    MouseEvent ev
                                            ) {

                                                painelDireito.removeAll();

                                                painelDireito.setLayout(
                                                        new GridBagLayout()
                                                );

                                                painelDireito.add(
                                                        cardLogin
                                                );

                                                painelDireito.revalidate();

                                                painelDireito.repaint();
                                            }
                                        }
                                );

                        painelDireito.removeAll();

                        painelDireito.setLayout(
                                new BorderLayout()
                        );

                        painelRedefinir.setBounds(
                                0,
                                0,
                                720,
                                550
                        );

                        painelDireito.add(
                                painelRedefinir,
                                BorderLayout.CENTER
                        );

                        painelDireito.revalidate();

                        painelDireito.repaint();
                    }


                    @Override
                    public void mouseEntered(MouseEvent e) {

                        lblEsqueciSenha.setForeground(
                                new Color(185, 120, 30)
                        );
                    }


                    @Override
                    public void mouseExited(MouseEvent e) {

                        lblEsqueciSenha.setForeground(
                                new Color(110, 70, 20)
                        );
                    }
                }
        );


        // =========================
        // ADICIONANDO COMPONENTES
        // =========================

        cardLogin.add(lbTitulo);
        cardLogin.add(lbSubtitulo);
        cardLogin.add(linhaTitulo);

        cardLogin.add(lbUsuario);
        cardLogin.add(containerUsuario);

        cardLogin.add(lbSenha);
        cardLogin.add(containerSenha);

        cardLogin.add(btEntrar);
        cardLogin.add(btLimpar);
        cardLogin.add(lblEsqueciSenha);


        // =========================
        // ADICIONA CARD AO PAINEL
        // =========================

        painelDireito.add(cardLogin);


        // =========================
        // MONTA TELA PRINCIPAL
        // =========================

        painelPrincipal.add(
                menuLateral,
                BorderLayout.WEST
        );

        painelPrincipal.add(
                painelDireito,
                BorderLayout.CENTER
        );


        add(painelPrincipal);


        // =========================
        // EXIBE A TELA
        // =========================

        setVisible(true);
    }


    // ==================================================
    // MÉTODOS AUXILIARES DE IMAGEM E CONTAINER
    // ==================================================

    private ImageIcon carregarIcone(String caminho, int largura, int altura) {
        try {
            java.net.URL url = getClass().getResource(caminho);
            if (url == null) {
                url = getClass().getClassLoader().getResource(caminho.startsWith("/") ? caminho.substring(1) : caminho);
            }
            if (url != null) {
                ImageIcon iconOriginal = new ImageIcon(url);
                Image img = iconOriginal.getImage().getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JPanel criarCampoComIcone(JTextField campo, String caminhoIcone) {
        JPanel painelCampo = new JPanel(new BorderLayout(10, 0));
        painelCampo.setBackground(Color.WHITE);

        painelCampo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                )
        );

        ImageIcon icone = carregarIcone(caminhoIcone, 20, 20);
        if (icone != null) {
            JLabel lbIcone = new JLabel(icone);
            painelCampo.add(lbIcone, BorderLayout.WEST);
        }

        campo.setBorder(null);
        campo.setOpaque(false);
        painelCampo.add(campo, BorderLayout.CENTER);

        return painelCampo;
    }


    // ==================================================
    // MÉTODOS DE MENSAGEM E LIMPEZA
    // ==================================================

    public void exibirMensagem(String msg) {

        UIManager.put(
                "OptionPane.background",
                Color.WHITE
        );

        UIManager.put(
                "Panel.background",
                Color.WHITE
        );

        UIManager.put(
                "OptionPane.messageFont",
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        UIManager.put(
                "Button.font",
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );


        JOptionPane.showMessageDialog(
                this,
                msg,
                "Sistema Quilombola",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    public void limparCampos() {

        txUsuario.setText("");
        txSenha.setText("");
    }


    // ==================================================
    // GETTERS
    // ==================================================

    public JTextField getTxUsuario() {

        return txUsuario;
    }


    public JPasswordField getTxSenha() {

        return txSenha;
    }


    public JButton getBtEntrar() {

        return btEntrar;
    }


    public JButton getBtLimpar() {

        return btLimpar;
    }
}