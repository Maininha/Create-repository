package view;

import controller.FinanceiroController;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class PainelNovaMovimentacao extends JPanel {

    private JComboBox<String> combo;
    private JComboBox<String> comboClassificacao;
    private JTextArea area;
    private JTextField txtValor;
    private JLabel titulo;

    private int idMovEdicao = -1;
    private String dataOriginalEdicao = "";

    public PainelNovaMovimentacao() {

        setLayout(null);
        setBackground(new Color(248, 245, 240));

        titulo = new JLabel("Nova movimentação");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(70, 40, 15));
        titulo.setBounds(40, 20, 400, 40);
        add(titulo);

        JLabel lblTipo = new JLabel("Tipo movimentação");
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTipo.setBounds(40, 100, 200, 20);
        add(lblTipo);

        combo = new JComboBox<>(new String[]{"Entrada", "Saída"});
        combo.setBounds(40, 130, 300, 40);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(35, 18, 4));
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(combo);

        JLabel classificacao = new JLabel("Classificação");
        classificacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        classificacao.setBounds(380, 100, 200, 20);
        add(classificacao);

        comboClassificacao = new JComboBox<>(new String[]{
                "Doação", "Arrecadação", "Mensalidade", "Patrocínio", "Outros"
        });
        comboClassificacao.setBounds(380, 130, 300, 40);
        comboClassificacao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboClassificacao.setBackground(Color.WHITE);
        comboClassificacao.setForeground(new Color(35, 18, 4));
        comboClassificacao.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(comboClassificacao);

        combo.addActionListener(e -> {
            String selecionado = (String) combo.getSelectedItem();
            if (selecionado == null) return;

            comboClassificacao.removeAllItems();
            if (selecionado.equals("Entrada")) {
                comboClassificacao.addItem("Doação");
                comboClassificacao.addItem("Arrecadação");
                comboClassificacao.addItem("Mensalidade");
                comboClassificacao.addItem("Patrocínio");
                comboClassificacao.addItem("Evento Beneficente");
                comboClassificacao.addItem("Outros");
            } else {
                comboClassificacao.addItem("Material de Consumo");
                comboClassificacao.addItem("Despesas Fixas");
                comboClassificacao.addItem("Energia");
                comboClassificacao.addItem("Água");
                comboClassificacao.addItem("Internet");
                comboClassificacao.addItem("Limpeza");
                comboClassificacao.addItem("Manutenção");
                comboClassificacao.addItem("Evento");
                comboClassificacao.addItem("Transporte");
                comboClassificacao.addItem("Outros");
            }
        });

        JLabel descricao = new JLabel("Descrição");
        descricao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descricao.setBounds(40, 200, 150, 20);
        add(descricao);

        area = new JTextArea();
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBounds(40, 230, 640, 120);
        add(scroll);

        JLabel valor = new JLabel("Valor");
        valor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valor.setBounds(40, 380, 100, 20);
        add(valor);

        txtValor = new JTextField();
        txtValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtValor.setBounds(40, 410, 300, 40);

        ((AbstractDocument) txtValor.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (string.matches("[0-9.,\\-]+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (text.matches("[0-9.,\\-]+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        add(txtValor);

        JButton salvar = new JButton("Salvar");
        salvar.setBounds(40, 500, 140, 45);
        salvar.setBackground(new Color(185, 120, 30));
        salvar.setForeground(Color.WHITE);
        salvar.setFocusPainted(false);
        salvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        salvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(salvar);

        JButton cancelar = new JButton("Cancelar");
        cancelar.setBounds(200, 500, 140, 45);
        cancelar.setBackground(Color.WHITE);
        cancelar.setForeground(Color.DARK_GRAY);
        cancelar.setFocusPainted(false);
        cancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(cancelar);

        FinanceiroController financeiroController = new FinanceiroController();

        salvar.addActionListener(e -> {
            String tipoSelecionado = (String) combo.getSelectedItem();
            String categoriaSelecionada = (String) comboClassificacao.getSelectedItem();
            String descTexto = area.getText().trim();
            String valorTexto = txtValor.getText().trim();

            if (descTexto.isEmpty() || valorTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha a Descrição e o Valor antes de salvar.", "Campos Obrigatórios", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean sucesso;

            if (idMovEdicao == -1) {
                String cpfLogado = model.Usuario.getUsuarioLogado().getCpf();
                sucesso = financeiroController.salvarMovimentacao(
                        tipoSelecionado, categoriaSelecionada, descTexto, valorTexto, cpfLogado
                );
            } else {
                sucesso = financeiroController.editarMovimentacao(
                        idMovEdicao, tipoSelecionado, categoriaSelecionada, descTexto, valorTexto
                );
            }

            if (sucesso) {
                limparCamposERetornar();
            }
        });

        cancelar.addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja cancelar a operação e retornar para a consulta?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (resposta == JOptionPane.YES_OPTION) {
                limparCamposERetornar();
            }
        });
    }

    public void preencherCamposParaEdicao(int idMov, String data, String tipo, String categoria, String descricao, String valor) {
        this.idMovEdicao = idMov;
        this.dataOriginalEdicao = data;

        this.titulo.setText("Editar Movimentação");
        this.combo.setSelectedItem(tipo);

        this.comboClassificacao.removeAllItems();
        if ("Entrada".equals(tipo)) {
            this.comboClassificacao.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Doação", "Arrecadação", "Mensalidade", "Patrocínio", "Evento Beneficente", "Outros"
            }));
        } else {
            this.comboClassificacao.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Material de Consumo", "Despesas Fixas", "Energia", "Água", "Internet",
                    "Limpeza", "Manutenção", "Evento", "Transporte", "Outros"
            }));
        }

        this.comboClassificacao.setSelectedItem(categoria);
        this.area.setText(descricao);
        this.txtValor.setText(valor.trim());
    }

    private void limparCamposERetornar() {
        this.idMovEdicao = -1;
        this.dataOriginalEdicao = "";
        this.titulo.setText("Nova movimentação");
        this.combo.setSelectedIndex(0);
        this.area.setText("");
        this.txtValor.setText("");

        SwingUtilities.invokeLater(() -> {
            Container ancestral = this.getParent();
            while (ancestral != null && !(ancestral instanceof TelaPrincipal)) {
                ancestral = ancestral.getParent();
            }

            if (ancestral != null) {
                TelaPrincipal tela = (TelaPrincipal) ancestral;
                tela.getCard().show(tela.getPainelConteudo(), "painelFinanceiro");

                if (tela.getPainelFinanceiro() != null) {
                    tela.getPainelFinanceiro().executarConsultaAtual();
                }
            }
        });
    }
}