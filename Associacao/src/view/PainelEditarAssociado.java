package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PainelEditarAssociado extends JPanel {

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtLogradouro;
    private JTextField txtCidade;
    private JTextField txtEstado;
    private JTextField txtReferencia;
    private JButton btnSalvar;
    private JButton btnCancelar;

    public PainelEditarAssociado() {

        setLayout(null);
        setBackground(new Color(248, 245, 240));

        // Título principal em Marrom Escuro
        JLabel titulo = new JLabel("Editar Associado");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(70, 40, 15));
        titulo.setBounds(50, 30, 300, 35);
        add(titulo);

        txtNome = new JTextField();
        txtCpf = new JTextField();
        txtLogradouro = new JTextField();
        txtCidade = new JTextField();
        txtEstado = new JTextField();
        txtReferencia = new JTextField();

        txtCpf.setEditable(false);

        int y = 80;
        addField("Nome", txtNome, y); y += 65;
        addField("CPF", txtCpf, y); y += 65;
        addField("Logradouro", txtLogradouro, y); y += 65;
        addField("Cidade", txtCidade, y); y += 65;
        addField("Estado", txtEstado, y); y += 65;
        addField("Referência", txtReferencia, y);

        // ================= BOTÃO SALVAR (Dourado/Terroso) =================
        btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(50, y + 70, 130, 40);
        btnSalvar.setBackground(new Color(185, 120, 30));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito Hover para o botão Salvar
        btnSalvar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSalvar.setBackground(new Color(205, 145, 55));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSalvar.setBackground(new Color(185, 120, 30));
            }
        });
        add(btnSalvar);

        // ================= BOTÃO CANCELAR (Estilo Outline Terroso) =================
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(195, y + 70, 130, 40);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Cores padrão: Texto marrom e uma borda fina combinando
        btnCancelar.setForeground(new Color(70, 40, 15));
        btnCancelar.setBorder(BorderFactory.createLineBorder(new Color(70, 40, 15), 1));

        btnCancelar.setContentAreaFilled(false); // Mantém o fundo transparente por padrão
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito Hover sutil para o botão Cancelar
        btnCancelar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Ao passar o mouse, ele ganha um fundo terroso bem clarinho
                btnCancelar.setContentAreaFilled(true);
                btnCancelar.setBackground(new Color(235, 225, 215));
                btnCancelar.setForeground(new Color(185, 120, 30)); // Texto muda sutilmente para o dourado
                btnCancelar.setBorder(BorderFactory.createLineBorder(new Color(185, 120, 30), 1));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Volta ao estado original transparente e discreto
                btnCancelar.setContentAreaFilled(false);
                btnCancelar.setForeground(new Color(70, 40, 15));
                btnCancelar.setBorder(BorderFactory.createLineBorder(new Color(70, 40, 15), 1));
            }
        });
        add(btnCancelar);
    }

    private void addField(String label, JTextField field, int y) {
        JLabel lb = new JLabel(label);
        lb.setBounds(50, y, 200, 20);
        lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lb.setForeground(Color.GRAY);
        add(lb);

        // Estilização moderna para as caixas de texto com padding interno de 10px à esquerda
        field.setBounds(50, y + 22, 400, 38);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(0, 10, 0, 0)
        ));

        // Ajuste de cor de fundo especial se o campo for bloqueado (como o CPF)
        if (!field.isEditable()) {
            field.setBackground(new Color(240, 238, 235));
            field.setForeground(Color.DARK_GRAY);
        } else {
            field.setBackground(Color.WHITE);
        }

        add(field);
    }

    // GETTERS EXIGIDOS PELO CONTROLLER
    public JTextField getTxtNome() { return txtNome; }
    public JTextField getTxtCpf() { return txtCpf; }
    public JTextField getTxtLogradouro() { return txtLogradouro; }
    public JTextField getTxtCidade() { return txtCidade; }
    public JTextField getTxtEstado() { return txtEstado; }
    public JTextField getTxtReferencia() { return txtReferencia; }
    public JButton getBtnSalvar() { return btnSalvar; }
    public JButton getBtnCancelar() { return btnCancelar; }
}