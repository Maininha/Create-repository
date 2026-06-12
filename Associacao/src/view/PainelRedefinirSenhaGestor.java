package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PainelRedefinirSenhaGestor extends JPanel {

    private JTextField txtCpf;
    private JPasswordField txtNovaSenha;
    private JPasswordField txtConfirmarNovaSenha;
    private JButton btnAtualizar;
    private JLabel lbTitulo;
    private JLabel lbAviso;
    private JLabel lblVoltar;

    public PainelRedefinirSenhaGestor() {
        // 🔥 CORREÇÃO: GridBagLayout centraliza qualquer componente filho automaticamente
        setLayout(new GridBagLayout());
        setBackground(new Color(233, 226, 218));

        // Mantemos o tamanho fixo interno do seu card (480x480)
        JPanel boxFormulario = new JPanel();
        boxFormulario.setLayout(null);
        boxFormulario.setBackground(Color.WHITE);
        boxFormulario.setPreferredSize(new Dimension(480, 480)); // Mudado para PreferredSize para o Layout respeitar
        boxFormulario.setBorder(new LineBorder(new Color(215, 205, 190), 1, true));

        // Título
        lbTitulo = new JLabel("Redefinir Senha do Gestor", SwingConstants.CENTER);
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbTitulo.setForeground(new Color(35, 18, 4));
        lbTitulo.setBounds(0, 25, 480, 35);
        boxFormulario.add(lbTitulo);

        // Subtítulo descritivo
        lbAviso = new JLabel("Insira o CPF do gestor para validar a troca de senha.", SwingConstants.CENTER);
        lbAviso.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lbAviso.setForeground(new Color(166, 104, 24));
        lbAviso.setBounds(0, 65, 480, 20);
        boxFormulario.add(lbAviso);

        // Campo CPF
        JLabel lbCpf = new JLabel("CPF do Gestor:");
        lbCpf.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbCpf.setForeground(Color.GRAY);
        lbCpf.setBounds(40, 105, 400, 20);
        boxFormulario.add(lbCpf);

        txtCpf = new JTextField();
        txtCpf.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtCpf.setBounds(40, 130, 400, 45);
        txtCpf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        boxFormulario.add(txtCpf);

        // Campo Nova Senha
        JLabel lbSenha = new JLabel("Nova Senha:");
        lbSenha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbSenha.setForeground(Color.GRAY);
        lbSenha.setBounds(40, 200, 400, 20);
        boxFormulario.add(lbSenha);

        txtNovaSenha = new JPasswordField();
        txtNovaSenha.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtNovaSenha.setBounds(40, 225, 400, 45);
        txtNovaSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        boxFormulario.add(txtNovaSenha);

        // Campo Confirmar Nova Senha
        JLabel lbConfirmar = new JLabel("Confirme a Nova Senha:");
        lbConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbConfirmar.setForeground(Color.GRAY);
        lbConfirmar.setBounds(40, 295, 400, 20);
        boxFormulario.add(lbConfirmar);

        txtConfirmarNovaSenha = new JPasswordField();
        txtConfirmarNovaSenha.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtConfirmarNovaSenha.setBounds(40, 320, 400, 45);
        txtConfirmarNovaSenha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        boxFormulario.add(txtConfirmarNovaSenha);

        // Botão de Atualização
        btnAtualizar = new JButton("Atualizar Senha");
        btnAtualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setBackground(new Color(185, 120, 30));
        btnAtualizar.setBorderPainted(false);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtualizar.setBounds(40, 385, 400, 45);

        btnAtualizar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnAtualizar.setBackground(new Color(205, 145, 55));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnAtualizar.setBackground(new Color(185, 120, 30));
            }
        });
        boxFormulario.add(btnAtualizar);

        // LINK: Voltar para o Login
        lblVoltar = new JLabel("<html><u>Voltar para o Login</u></html>");
        lblVoltar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblVoltar.setForeground(new Color(110, 70, 20));
        lblVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblVoltar.setBounds(0, 442, 480, 25);
        lblVoltar.setHorizontalAlignment(SwingConstants.CENTER);

        lblVoltar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lblVoltar.setForeground(new Color(185, 120, 30));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lblVoltar.setForeground(new Color(110, 70, 20));
            }
        });
        boxFormulario.add(lblVoltar);

        // 🔥 O truque do GridBagLayout injeta o card exatamente no meio da tela limpa
        add(boxFormulario, new GridBagConstraints());
    }

    // Getters
    public JTextField getTxtCpf() { return txtCpf; }
    public JPasswordField getTxtNovaSenha() { return txtNovaSenha; }
    public JPasswordField getTxtConfirmarNovaSenha() { return txtConfirmarNovaSenha; }
    public JButton getBtnAtualizar() { return btnAtualizar; }
    public JLabel getLblVoltar() { return lblVoltar; }
}