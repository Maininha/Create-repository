package view;

import controller.ControllerCadastroAssociado;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaCadastroAssociado extends JFrame {

    private JButton btInicio;
    private JButton btAssociados;
    private JButton btFinanceiro;
    private JButton btRelatorios;
    private JButton btCadastros;
    private JButton btConfiguracoes;
    private JButton btSobre;

    private JPanel painelConteudo;
    private CardLayout card;

    // Transformados em atributos para os 'getters' funcionarem
    private PainelCadastroAssociado cadastro;
    private PainelCriarUsuarioSenha senha;

    public TelaCadastroAssociado() {
        setTitle("Sistema de Gestão da Comunidade Quilombola");
        setSize(1280, 740);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        criarTopo();
        criarMenuLateral();
        criarConteudo();

        card.show(painelConteudo, "inicio");

        setVisible(true);
    }

    private void criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(43, 22, 7));
        topo.setPreferredSize(new Dimension(0, 65));
        topo.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel lbTitulo = new JLabel("Sistema de Gestão da Comunidade Quilombola");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbTitulo.setForeground(Color.WHITE);
        topo.add(lbTitulo, BorderLayout.WEST);

        JButton btnAdmin = new JButton("Administrador");
        btnAdmin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdmin.setForeground(Color.WHITE);
        btnAdmin.setBackground(new Color(68, 36, 12));
        btnAdmin.setFocusPainted(false);
        btnAdmin.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel containerAdmin = new JPanel(new GridBagLayout());
        containerAdmin.setOpaque(false);
        containerAdmin.add(btnAdmin);
        topo.add(containerAdmin, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);
    }

    private void criarMenuLateral() {
        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(240, 0));
        menu.setBackground(new Color(43, 22, 7));
        menu.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
        menu.setBorder(new EmptyBorder(20, 15, 20, 15));

        btInicio = estilizarBotaoMenu("🏠  Início");
        btAssociados = estilizarBotaoMenu("👥  Associados");
        btFinanceiro = estilizarBotaoMenu("💰  Financeiro");
        btRelatorios = estilizarBotaoMenu("📊  Relatórios");
        btCadastros = estilizarBotaoMenu("👤+ Cadastros");
        btConfiguracoes = estilizarBotaoMenu("💰 Movimentações ");
        btSobre = estilizarBotaoMenu("ℹ️  Sobre");

        marcarBotaoAtivo(btInicio);

        menu.add(btInicio);
        menu.add(btAssociados);
        menu.add(btFinanceiro);
        menu.add(btRelatorios);
        menu.add(btCadastros);
        menu.add(btConfiguracoes);
        menu.add(btSobre);

        btInicio.addActionListener(e -> { alternarFocoMenu(btInicio); card.show(painelConteudo, "inicio"); });
        btCadastros.addActionListener(e -> { alternarFocoMenu(btCadastros); card.show(painelConteudo, "criarCadastro"); });
        btFinanceiro.addActionListener(e -> { alternarFocoMenu(btFinanceiro); card.show(painelConteudo, "financeiro"); });

        add(menu, BorderLayout.WEST);
    }

    private void criarConteudo() {
        card = new CardLayout();
        painelConteudo = new JPanel(card);
        painelConteudo.setBackground(new Color(248, 245, 240));

        // CORREÇÃO: Passando 'null' já que essa classe não herda de TelaPrincipal.
        // Nota: As ações do card de atividades que dependem da TelaPrincipal não funcionarão nesta tela secundária.
        PainelInicio inicio = new PainelInicio(null);

        cadastro = new PainelCadastroAssociado();
        senha = new PainelCriarUsuarioSenha();
        PainelResumoFinanceiro financeiro = new PainelResumoFinanceiro();

        painelConteudo.add(inicio, "inicio");
        painelConteudo.add(cadastro, "criarCadastro");
        painelConteudo.add(senha, "criarSenha");
        painelConteudo.add(financeiro, "financeiro");

        add(painelConteudo, BorderLayout.CENTER);
    }

    private JButton estilizarBotaoMenu(String texto) {
        JButton botao = new JButton(texto);
        botao.setPreferredSize(new Dimension(210, 45));
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        botao.setForeground(new Color(210, 200, 190));
        botao.setBackground(new Color(43, 22, 7));
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }

    public void marcarBotaoAtivo(JButton botao) {
        botao.setBackground(new Color(175, 110, 25));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    private void desmarcarBotao(JButton botao) {
        botao.setBackground(new Color(43, 22, 7));
        botao.setForeground(new Color(210, 200, 190));
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    public void alternarFocoMenu(JButton botaoSelecionado) {
        desmarcarBotao(btInicio);
        desmarcarBotao(btAssociados);
        desmarcarBotao(btFinanceiro);
        desmarcarBotao(btRelatorios);
        desmarcarBotao(btCadastros);
        desmarcarBotao(btConfiguracoes);
        desmarcarBotao(btSobre);
        marcarBotaoAtivo(botaoSelecionado);
    }

    public PainelCadastroAssociado getPainelCadastro() { return cadastro; }
    public PainelCriarUsuarioSenha getPainelSenha() { return senha; }
    public CardLayout getCard() { return card; }
    public JPanel getPainelConteudo() { return painelConteudo; }
}