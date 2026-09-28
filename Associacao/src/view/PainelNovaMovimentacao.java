package view;

import controller.FinanceiroController;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PainelNovaMovimentacao extends JPanel {

    private JComboBox<String> combo;
    private JComboBox<String> comboClassificacao;
    private JTextArea area;
    private JTextField txtValor;
    private JFormattedTextField txtData;
    private JLabel titulo;

    private int idMovEdicao = -1;
    private String dataOriginalEdicao = "";

    private final String PLACEHOLDER_DESC = "Ex: Doação de insumos ou pagamento de taxa de associado...";
    private final Color COR_OBRIGATORIO = new Color(211, 47, 47);
    private final Color COR_TEXTO_PADRAO = new Color(35, 18, 4);

    public PainelNovaMovimentacao() {

        setLayout(null);
        setBackground(new Color(248, 245, 240));

        titulo = new JLabel("Nova movimentação");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(70, 40, 15));
        titulo.setBounds(40, 20, 400, 40);
        add(titulo);

        // ------------------ TIPO MOVIMENTAÇÃO ------------------
        JLabel lblTipo = criarLabelComObrigatorio("Tipo movimentação", 40, 90, 200, 20);
        add(lblTipo);

        combo = new JComboBox<>(new String[]{"Entrada", "Saída"});
        combo.setBounds(40, 115, 300, 40);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setForeground(COR_TEXTO_PADRAO);
        combo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(combo);

        // ------------------ CLASSIFICAÇÃO ------------------
        JLabel classificacao = criarLabelComObrigatorio("Classificação", 380, 90, 200, 20);
        add(classificacao);

        comboClassificacao = new JComboBox<>(new String[]{
                "Doação", "Arrecadação", "Mensalidade", "Patrocínio", "Evento Beneficente", "Outros"
        });
        comboClassificacao.setBounds(380, 115, 300, 40);
        comboClassificacao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboClassificacao.setBackground(Color.WHITE);
        comboClassificacao.setForeground(COR_TEXTO_PADRAO);
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

        // ------------------ DESCRIÇÃO ------------------
        JLabel descricao = criarLabelComObrigatorio("Descrição", 40, 175, 200, 20);
        add(descricao);

        area = new JTextArea(PLACEHOLDER_DESC);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setForeground(Color.GRAY);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (area.getText().equals(PLACEHOLDER_DESC)) {
                    area.setText("");
                    area.setForeground(COR_TEXTO_PADRAO);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (area.getText().trim().isEmpty()) {
                    area.setText(PLACEHOLDER_DESC);
                    area.setForeground(Color.GRAY);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBounds(40, 200, 640, 100);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(scroll);

        // ------------------ VALOR ------------------
        JLabel valor = criarLabelComObrigatorio("Valor", 40, 320, 100, 20);
        add(valor);

        JPanel painelValor = new JPanel(new BorderLayout());
        painelValor.setBounds(40, 345, 300, 40);
        painelValor.setBackground(Color.WHITE);
        painelValor.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel lblSifrao = new JLabel(" R$ ");
        lblSifrao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSifrao.setForeground(Color.GRAY);
        painelValor.add(lblSifrao, BorderLayout.WEST);

        txtValor = new JTextField();
        txtValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtValor.setBorder(null);

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

        painelValor.add(txtValor, BorderLayout.CENTER);
        add(painelValor);

        // ------------------ DATA DA MOVIMENTAÇÃO ------------------
        JLabel lblData = criarLabelComObrigatorio("Data da Movimentação", 380, 320, 200, 20);
        add(lblData);

        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(mascaraData);
        } catch (ParseException e) {
            txtData = new JFormattedTextField();
        }

        txtData.setBounds(380, 345, 300, 40);
        txtData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtData.setBackground(Color.WHITE);
        txtData.setForeground(COR_TEXTO_PADRAO);
        txtData.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        txtData.setText(getDataAtualFormatada());
        add(txtData);

        // ------------------ BOTÕES DE AÇÃO ------------------
        JButton salvar = new JButton("Salvar");
        salvar.setBounds(40, 430, 140, 45);
        salvar.setBackground(new Color(185, 120, 30));
        salvar.setForeground(Color.WHITE);
        salvar.setFocusPainted(false);
        salvar.setBorderPainted(false);
        salvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        salvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(salvar);

        JButton cancelar = new JButton("Cancelar");
        cancelar.setBounds(200, 430, 140, 45);
        cancelar.setBackground(Color.WHITE);
        cancelar.setForeground(Color.DARK_GRAY);
        cancelar.setFocusPainted(false);
        cancelar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        cancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(cancelar);

        FinanceiroController financeiroController = new FinanceiroController();

        salvar.addActionListener(e -> {
            String tipoSelecionado = (String) combo.getSelectedItem();
            String categoriaSelecionada = (String) comboClassificacao.getSelectedItem();
            String descTexto = area.getText().trim();
            String valorTexto = txtValor.getText().trim();
            String dataTexto = txtData.getText().trim();

            if (descTexto.isEmpty() || descTexto.equals(PLACEHOLDER_DESC) || valorTexto.isEmpty() || dataTexto.contains("_")) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, preencha todos os campos obrigatórios (*) com dados válidos.",
                        "Campos Obrigatórios",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean sucesso;

            if (idMovEdicao == -1) {
                String cpfLogado = model.Usuario.getUsuarioLogado() != null ? model.Usuario.getUsuarioLogado().getCpf() : "";
                sucesso = financeiroController.salvarMovimentacao(
                        tipoSelecionado, categoriaSelecionada, descTexto, valorTexto, cpfLogado
                );
            } else {
                sucesso = financeiroController.editarMovimentacao(
                        idMovEdicao, tipoSelecionado, categoriaSelecionada, descTexto, valorTexto
                );
            }

            if (sucesso) {
                JOptionPane.showMessageDialog(this, idMovEdicao == -1 ? "Movimentação registrada!" : "Movimentação atualizada!");
                limparCamposERetornar();
            }
        });

        cancelar.addActionListener(e -> {
            // Opções personalizadas em Português
            Object[] opcoes = {"Sim", "Não"};

            int resposta = JOptionPane.showOptionDialog(
                    this,
                    "Deseja realmente cancelar? Os dados não salvos serão perdidos.",
                    "Confirmar Cancelamento",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[1] // Mantém o "Não" focado por padrão
            );

            if (resposta == 0) { // 0 corresponde à opção "Sim"
                limparCamposERetornar();
            }
        });
    }

    private JLabel criarLabelComObrigatorio(String texto, int x, int y, int width, int height) {
        JLabel label = new JLabel("<html>" + texto + " <font color='#D32F2F'>*</font></html>");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(70, 40, 15));
        label.setBounds(x, y, width, height);
        return label;
    }

    private String getDataAtualFormatada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
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
        this.area.setForeground(COR_TEXTO_PADRAO);

        this.txtValor.setText(valor.trim());
        this.txtData.setText(data != null && !data.isEmpty() ? data : getDataAtualFormatada());
    }

    private void limparCamposERetornar() {
        // 1. Limpa os campos do formulário
        this.idMovEdicao = -1;
        this.dataOriginalEdicao = "";
        this.titulo.setText("Nova movimentação");
        this.combo.setSelectedIndex(0);

        this.area.setText(PLACEHOLDER_DESC);
        this.area.setForeground(Color.GRAY);

        this.txtValor.setText("");
        this.txtData.setText(getDataAtualFormatada());

        // 2. Localiza a instância da TelaPrincipal na árvore de componentes
        Window janelaAncestral = SwingUtilities.getWindowAncestor(this);

        if (janelaAncestral instanceof TelaPrincipal) {
            TelaPrincipal tela = (TelaPrincipal) janelaAncestral;

            // 3. Recarrega os dados do painel financeiro se necessário
            if (tela.getPainelFinanceiro() != null) {
                tela.getPainelFinanceiro().executarConsultaAtual();
            }

            // 4. Exibe a tela usando a chave registrada na TelaPrincipal
            tela.getCard().show(tela.getPainelConteudo(), "painelFinanceiro");

            // 5. Atualiza o realce visual do botão do menu lateral
            if (tela.getBtFinanceiro() != null) {
                tela.selecionarBotao(tela.getBtFinanceiro());
            }
        }
    }
}