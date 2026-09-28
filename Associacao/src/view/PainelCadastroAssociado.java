package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;

public class PainelCadastroAssociado extends JPanel {

    private TelaPrincipal telaPrincipal;

    private JTextField txtNome;
    private JFormattedTextField txtCpf;
    private JTextField txtLogradouro;
    private JTextField txtCidade;
    private JComboBox<String> cbEstado;
    private JTextField txtReferencia;
    private JRadioButton rbGestor;
    private JRadioButton rbAssociado;
    private ButtonGroup grupoTipo;
    private JButton btnCadastrar;
    private JButton btnVoltarLink;

    public PainelCadastroAssociado(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;

        setLayout(new GridBagLayout());
        setBackground(new Color(248, 245, 240));

        // Box Central (Card do Formulário)
        JPanel boxFormulario = new JPanel(new GridBagLayout());
        boxFormulario.setBackground(Color.WHITE);
        boxFormulario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 225, 215), 1, true),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lbTitulo = new JLabel("Cadastrar Associado");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbTitulo.setForeground(new Color(35, 18, 4));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 20, 10);
        boxFormulario.add(lbTitulo, gbc);

        gbc.insets = new Insets(4, 10, 4, 10);
        gbc.gridwidth = 1;

        // ================= COLUNA 1: DADOS PESSOAIS =================
        // Nome
        gbc.gridx = 0; gbc.gridy = 1;
        boxFormulario.add(criarRotulo("Nome *"), gbc);

        txtNome = new JTextField();
        estilizarCampo(txtNome);
        gbc.gridy = 2;
        boxFormulario.add(txtNome, gbc);

        // CPF com Máscara Estável
        gbc.gridy = 3;
        boxFormulario.add(criarRotulo("CPF *"), gbc);

        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter('_');
            txtCpf = new JFormattedTextField(mascaraCpf);
            txtCpf.setFocusLostBehavior(JFormattedTextField.PERSIST);
        } catch (ParseException e) {
            txtCpf = new JFormattedTextField();
        }
        estilizarCampo(txtCpf);
        gbc.gridy = 4;
        boxFormulario.add(txtCpf, gbc);

        // Tipo de Perfil
        gbc.gridy = 5;
        boxFormulario.add(criarRotulo("Tipo de Perfil"), gbc);

        JPanel painelRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        painelRadio.setOpaque(false);

        rbGestor = new JRadioButton("Gestor");
        rbGestor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbGestor.setOpaque(false);
        rbGestor.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rbAssociado = new JRadioButton("Associado", true);
        rbAssociado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rbAssociado.setOpaque(false);
        rbAssociado.setCursor(new Cursor(Cursor.HAND_CURSOR));

        grupoTipo = new ButtonGroup();
        grupoTipo.add(rbGestor);
        grupoTipo.add(rbAssociado);

        painelRadio.add(rbGestor);
        painelRadio.add(rbAssociado);

        gbc.gridy = 6;
        boxFormulario.add(painelRadio, gbc);

        // ================= COLUNA 2: ENDEREÇO =================
        // Logradouro
        gbc.gridx = 1; gbc.gridy = 1;
        boxFormulario.add(criarRotulo("Logradouro"), gbc);

        txtLogradouro = new JTextField();
        estilizarCampo(txtLogradouro);
        gbc.gridy = 2;
        boxFormulario.add(txtLogradouro, gbc);

        // Cidade e Estado (Sub-painel)
        JPanel painelCidadeEstado = new JPanel(new GridBagLayout());
        painelCidadeEstado.setOpaque(false);
        GridBagConstraints gbcCE = new GridBagConstraints();
        gbcCE.fill = GridBagConstraints.HORIZONTAL;

        gbcCE.gridx = 0; gbcCE.gridy = 0; gbcCE.weightx = 0.75; gbcCE.insets = new Insets(0, 0, 2, 5);
        painelCidadeEstado.add(criarRotulo("Cidade"), gbcCE);

        gbcCE.gridx = 1; gbcCE.weightx = 0.25; gbcCE.insets = new Insets(0, 5, 2, 0);
        painelCidadeEstado.add(criarRotulo("UF"), gbcCE);

        txtCidade = new JTextField();
        estilizarCampo(txtCidade);
        gbcCE.gridx = 0; gbcCE.gridy = 1; gbcCE.weightx = 0.75; gbcCE.insets = new Insets(0, 0, 0, 5);
        painelCidadeEstado.add(txtCidade, gbcCE);

        // JComboBox para UF
        String[] ufs = {"", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};
        cbEstado = new JComboBox<>(ufs);
        cbEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbEstado.setPreferredSize(new Dimension(80, 35));
        cbEstado.setBackground(Color.WHITE);
        cbEstado.setBorder(new LineBorder(new Color(210, 210, 210)));

        gbcCE.gridx = 1; gbcCE.weightx = 0.25; gbcCE.insets = new Insets(0, 5, 0, 0);
        painelCidadeEstado.add(cbEstado, gbcCE);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridheight = 2;
        boxFormulario.add(painelCidadeEstado, gbc);

        // Referência
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.gridheight = 1;
        boxFormulario.add(criarRotulo("Referência"), gbc);

        txtReferencia = new JTextField();
        estilizarCampo(txtReferencia);
        gbc.gridy = 6;
        boxFormulario.add(txtReferencia, gbc);

        // ================= AÇÕES E BOTÕES =================
        // Botão Cadastrar
        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setBackground(new Color(185, 120, 30));
        btnCadastrar.setBorderPainted(false);
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCadastrar.setPreferredSize(new Dimension(0, 45));

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

        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 5, 10);
        boxFormulario.add(btnCadastrar, gbc);

        // Botão Voltar Link
        btnVoltarLink = new JButton("← Cancelar e voltar para a listagem");
        btnVoltarLink.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVoltarLink.setForeground(new Color(185, 120, 30));
        btnVoltarLink.setContentAreaFilled(false);
        btnVoltarLink.setBorderPainted(false);
        btnVoltarLink.setFocusPainted(false);
        btnVoltarLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltarLink.setHorizontalAlignment(SwingConstants.CENTER);

        btnVoltarLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnVoltarLink.setForeground(new Color(43, 22, 7));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnVoltarLink.setForeground(new Color(185, 120, 30));
            }
        });

        gbc.gridy = 8;
        gbc.insets = new Insets(0, 10, 0, 10);
        boxFormulario.add(btnVoltarLink, gbc);

        // Atalho Tecla Enter nos campos para Submissão
        ActionListener acaoEnter = e -> btnCadastrar.doClick();
        txtNome.addActionListener(acaoEnter);
        txtCpf.addActionListener(acaoEnter);
        txtLogradouro.addActionListener(acaoEnter);
        txtCidade.addActionListener(acaoEnter);
        txtReferencia.addActionListener(acaoEnter);

        add(boxFormulario);
    }

    private JLabel criarRotulo(String texto) {
        JLabel lb = new JLabel(texto);
        lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lb.setForeground(new Color(70, 70, 70));
        return lb;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(320, 35));
        campo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
    }

    public void limparCampos() {
        txtNome.setText("");
        txtCpf.setValue(null);
        txtLogradouro.setText("");
        txtCidade.setText("");
        if (cbEstado.getItemCount() > 0) cbEstado.setSelectedIndex(0);
        txtReferencia.setText("");
        rbAssociado.setSelected(true);
    }

    public boolean isGestorSelecionado() {
        return rbGestor.isSelected();
    }

    public TelaPrincipal getTelaPrincipal() { return telaPrincipal; }
    public JTextField getTxtNome() { return txtNome; }
    public JFormattedTextField getTxtCpf() { return txtCpf; }
    public JTextField getTxtLogradouro() { return txtLogradouro; }
    public JTextField getTxtCidade() { return txtCidade; }
    public JComboBox<String> getCbEstado() { return cbEstado; }
    public JTextField getTxtReferencia() { return txtReferencia; }
    public JRadioButton getRbGestor() { return rbGestor; }
    public JRadioButton getRbAssociado() { return rbAssociado; }
    public JButton getBtnCadastrar() { return btnCadastrar; }
    public JButton getBtnVoltarLink() { return btnVoltarLink; }
}