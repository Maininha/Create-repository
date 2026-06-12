package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PainelCriarUsuarioSenha extends JPanel {

    private TelaPrincipal telaPrincipal;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;
    private JButton btnFinalizar;

    public PainelCriarUsuarioSenha(TelaPrincipal telaPrincipal) {
        this.telaPrincipal = telaPrincipal;

        setLayout(null);
        setBackground(new Color(248, 245, 240));

        JPanel boxFormulario = new JPanel();
        boxFormulario.setLayout(null);
        boxFormulario.setBackground(Color.WHITE);
        boxFormulario.setBounds(460, 130, 440, 320);
        boxFormulario.setBorder(new LineBorder(new Color(230, 225, 215), 1, true));

        JLabel lbTitulo = new JLabel("Criar Senha do Diretor");
        lbTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbTitulo.setForeground(new Color(35, 18, 4));
        lbTitulo.setBounds(40, 30, 360, 30);
        boxFormulario.add(lbTitulo);

        JLabel lbAviso = new JLabel("Nota: O CPF digitado será usado como login de acesso.");
        lbAviso.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lbAviso.setForeground(Color.GRAY);
        lbAviso.setBounds(40, 65, 360, 20);
        boxFormulario.add(lbAviso);

        JLabel lbSenha = new JLabel("Digite a Senha:");
        lbSenha.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbSenha.setForeground(Color.GRAY);
        lbSenha.setBounds(40, 105, 360, 20);
        boxFormulario.add(lbSenha);

        txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSenha.setBounds(40, 130, 360, 35);
        boxFormulario.add(txtSenha);

        JLabel lbConfirmar = new JLabel("Confirme a Senha:");
        lbConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbConfirmar.setForeground(Color.GRAY);
        lbConfirmar.setBounds(40, 180, 360, 20);
        boxFormulario.add(lbConfirmar);

        txtConfirmarSenha = new JPasswordField();
        txtConfirmarSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConfirmarSenha.setBounds(40, 205, 360, 35);
        boxFormulario.add(txtConfirmarSenha);

        btnFinalizar = new JButton("Finalizar Cadastro");
        btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnFinalizar.setForeground(Color.WHITE);
        btnFinalizar.setBackground(new Color(185, 120, 30));
        btnFinalizar.setBorderPainted(false);
        btnFinalizar.setFocusPainted(false);
        btnFinalizar.setBounds(40, 260, 360, 40);
        boxFormulario.add(btnFinalizar);

        add(boxFormulario);
    }

    public TelaPrincipal getTelaPrincipal() { return telaPrincipal; }
    public JPasswordField getTxtSenha() { return txtSenha; }
    public JPasswordField getTxtConfirmarSenha() { return txtConfirmarSenha; }
    public JButton getBtnFinalizar() { return btnFinalizar; }
}