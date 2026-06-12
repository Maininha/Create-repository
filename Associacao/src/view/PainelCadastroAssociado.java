package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PainelCadastroAssociado extends JPanel {

    // Componentes de Dados Pessoais
    private JTextField txtNome;
    private JTextField txtCpf;

    // Componentes de Endereço
    private JTextField txtLogradouro;
    private JTextField txtCidade;
    private JTextField txtEstado;
    private JTextField txtReferencia;

    // Componentes de Seleção de Perfil
    private JRadioButton rbGestor;
    private JRadioButton rbAssociado;
    private ButtonGroup grupoTipo;

    private JButton btnCadastrar;

    public PainelCadastroAssociado() {
        setLayout(null);
        setBackground(new Color(248, 245, 240));

        // Container centralizado (Card do Formulário)
        JPanel boxFormulario = new JPanel();
        boxFormulario.setLayout(null);
        boxFormulario.setBackground(Color.WHITE);
        boxFormulario.setBounds(180, 40, 760, 480);
        boxFormulario.setBorder(new LineBorder(new Color(230, 225, 215), 1, true));

        // Título interno do painel
        JLabel lbTitulo = new JLabel("Cadastrar Associado");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbTitulo.setForeground(new Color(35, 18, 4));
        lbTitulo.setBounds(40, 25, 400, 30);
        boxFormulario.add(lbTitulo);

        // ================= COLUNA 1: DADOS PESSOAIS =================
        JLabel lbNome = new JLabel("Nome:");
        lbNome.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbNome.setForeground(Color.GRAY);
        lbNome.setBounds(40, 80, 320, 20);
        boxFormulario.add(lbNome);

        txtNome = new JTextField();
        estilizarCampo(txtNome);
        txtNome.setBounds(40, 105, 320, 35);
        boxFormulario.add(txtNome);

        JLabel lbCpf = new JLabel("CPF:");
        lbCpf.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbCpf.setForeground(Color.GRAY);
        lbCpf.setBounds(40, 155, 320, 20);
        boxFormulario.add(lbCpf);

        txtCpf = new JTextField();
        estilizarCampo(txtCpf);
        txtCpf.setBounds(40, 180, 320, 35);
        boxFormulario.add(txtCpf);

        JLabel lbTipo = new JLabel("Tipo de Perfil:");
        lbTipo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbTipo.setForeground(Color.GRAY);
        lbTipo.setBounds(40, 235, 320, 20);
        boxFormulario.add(lbTipo);

        rbGestor = new JRadioButton("Gestor");
        rbGestor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbGestor.setOpaque(false);
        rbGestor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rbGestor.setBounds(40, 260, 100, 30);

        rbAssociado = new JRadioButton("Associado", true);
        rbAssociado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbAssociado.setOpaque(false);
        rbAssociado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rbAssociado.setBounds(150, 260, 120, 30);

        grupoTipo = new ButtonGroup();
        grupoTipo.add(rbGestor);
        grupoTipo.add(rbAssociado);

        boxFormulario.add(rbGestor);
        boxFormulario.add(rbAssociado);

        // ================= COLUNA 2: ENDEREÇO =================
        JLabel lbLogradouro = new JLabel("Logradouro (Rua, Número...):");
        lbLogradouro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbLogradouro.setForeground(Color.GRAY);
        lbLogradouro.setBounds(400, 80, 320, 20);
        boxFormulario.add(lbLogradouro);

        txtLogradouro = new JTextField();
        estilizarCampo(txtLogradouro);
        txtLogradouro.setBounds(400, 105, 320, 35);
        boxFormulario.add(txtLogradouro);

        JLabel lbCidade = new JLabel("Cidade:");
        lbCidade.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbCidade.setForeground(Color.GRAY);
        lbCidade.setBounds(400, 155, 200, 20);
        boxFormulario.add(lbCidade);

        txtCidade = new JTextField();
        estilizarCampo(txtCidade);
        txtCidade.setBounds(400, 180, 200, 35);
        boxFormulario.add(txtCidade);

        JLabel lbEstado = new JLabel("Estado (UF):");
        lbEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbEstado.setForeground(Color.GRAY);
        lbEstado.setBounds(620, 155, 100, 20);
        boxFormulario.add(lbEstado);

        txtEstado = new JTextField();
        estilizarCampo(txtEstado);
        txtEstado.setBounds(620, 180, 100, 35);
        boxFormulario.add(txtEstado);

        JLabel lbReferencia = new JLabel("Referência:");
        lbReferencia.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbReferencia.setForeground(Color.GRAY);
        lbReferencia.setBounds(400, 230, 320, 20);
        boxFormulario.add(lbReferencia);

        txtReferencia = new JTextField();
        estilizarCampo(txtReferencia);
        txtReferencia.setBounds(400, 255, 320, 35);
        boxFormulario.add(txtReferencia);

        // ================= BOTÃO DE SUBMISSÃO =================
        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setBackground(new Color(185, 120, 30));
        btnCadastrar.setBorderPainted(false);
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCadastrar.setBounds(40, 380, 680, 45);

        // Efeito Hover para seguir a identidade visual premium dos botões
        btnCadastrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCadastrar.setBackground(new Color(205, 145, 55));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCadastrar.setBackground(new Color(185, 120, 30));
            }
        });
        boxFormulario.add(btnCadastrar);

        add(boxFormulario);
    }

    // Método auxiliar para dar o acabamento premium às caixas de texto
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(0, 10, 0, 0)
        ));
    }

    // Getters
    public JTextField getTxtNome() { return txtNome; }
    public JTextField getTxtCpf() { return txtCpf; }
    public JTextField getTxtLogradouro() { return txtLogradouro; }
    public JTextField getTxtCidade() { return txtCidade; }
    public JTextField getTxtEstado() { return txtEstado; }
    public JTextField getTxtReferencia() { return txtReferencia; }
    public JRadioButton getRbGestor() { return rbGestor; }
    public JRadioButton getRbAssociado() { return rbAssociado; }
    public JButton getBtnCadastrar() { return btnCadastrar; }
}