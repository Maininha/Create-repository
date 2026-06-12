package view;

import controller.FinanceiroController;
import javax.swing.*;
import java.awt.*;

public class PainelNovaMovimentacao extends JPanel {

    // Componentes promovidos a atributos para permitir o preenchimento externo
    private JComboBox<String> combo;
    private JComboBox<String> comboClassificacao;
    private JTextArea area;
    private JTextField txtValor;
    private JLabel titulo;

    // Variável de controle: se for -1 é inserção, se for maior que 0 é edição
    private int idMovEdicao = -1;
    private String dataOriginalEdicao = "";

    public PainelNovaMovimentacao() {

        setLayout(null);
        setBackground(new Color(248, 245, 240));

        // TÍTULO
        titulo = new JLabel("Nova movimentação");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(70, 40, 15));
        titulo.setBounds(40, 20, 400, 40);
        add(titulo);

        // TIPO MOVIMENTAÇÃO
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

        // CLASSIFICAÇÃO
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

        // Listener para alternar os itens da classificação dinamicamente (Uso Padrão do Usuário)
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

        // DESCRIÇÃO
        JLabel descricao = new JLabel("Descrição");
        descricao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descricao.setBounds(40, 200, 150, 20);
        add(descricao);

        area = new JTextArea();
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBounds(40, 230, 640, 120);
        add(scroll);

        // VALOR
        JLabel valor = new JLabel("Valor");
        valor.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valor.setBounds(40, 380, 100, 20);
        add(valor);

        txtValor = new JTextField();
        txtValor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtValor.setBounds(40, 410, 300, 40);
        add(txtValor);

        // BOTÃO SALVAR
        JButton salvar = new JButton("Salvar");
        salvar.setBounds(40, 500, 140, 45);
        salvar.setBackground(new Color(185, 120, 30));
        salvar.setForeground(Color.WHITE);
        salvar.setFocusPainted(false);
        salvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        salvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(salvar);

        // BOTÃO CANCELAR
        JButton cancelar = new JButton("Cancelar");
        cancelar.setBounds(200, 500, 140, 45);
        cancelar.setBackground(Color.WHITE);
        cancelar.setForeground(Color.DARK_GRAY);
        cancelar.setFocusPainted(false);
        cancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(cancelar);

        FinanceiroController financeiroController = new FinanceiroController();

        // Ação Dinâmica do Botão Salvar (Detecta se é INSERT ou UPDATE)
        salvar.addActionListener(e -> {
            String tipoSelecionado = (String) combo.getSelectedItem();
            String categoriaSelecionada = (String) comboClassificacao.getSelectedItem();
            String descTexto = area.getText();
            String valorTexto = txtValor.getText();

            boolean sucesso;

            if (idMovEdicao == -1) {
                // MODO: NOVO CADASTRO
                sucesso = financeiroController.salvarMovimentacao(
                        tipoSelecionado, categoriaSelecionada, descTexto, valorTexto
                );
            } else {
                // MODO: EDIÇÃO DE REGISTRO EXISTENTE
                sucesso = financeiroController.editarMovimentacao(
                        idMovEdicao, tipoSelecionado, categoriaSelecionada, descTexto, valorTexto, dataOriginalEdicao
                );
            }

            if (sucesso) {
                limparCamposERetornar();
            }
        });

        // Ação do Botão Cancelar
        cancelar.addActionListener(e -> {
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja cancelar a operação e retornar para a consulta?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );

            if (resposta == JOptionPane.YES_OPTION) {
                limparCamposERetornar();
            }
        });
    }

    // ========================================================================
    // MÉTODO ESTRUTURAL: Recebe os dados carregados do clique da tabela
    // ========================================================================
    public void preencherCamposParaEdicao(int idMov, String data, String tipo, String categoria, String descricao, String valor) {
        this.idMovEdicao = idMov;
        this.dataOriginalEdicao = data; // Preserva a data original do registro

        this.titulo.setText("Editar Movimentação");

        // 1. Define o Tipo de forma segura (Entrada ou Saída)
        this.combo.setSelectedItem(tipo);

        // 2. Reconstrói o modelo de dados imediatamente para evitar falhas assíncronas do listener
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

        // 3. Define a categoria correta pós-sincronização
        this.comboClassificacao.setSelectedItem(categoria);

        // 4. Popula as informações complementares
        this.area.setText(descricao);

        // Remove símbolos de cifrão e pontos de milhar, padronizando o valor numérico puro
        String valorLimpo = valor.replace("R$", "").replace(".", "").replace(",", ".").trim();
        this.txtValor.setText(valorLimpo);
    }

    // Restaura a interface ao estado padrão de inserção e retorna à tela de listagem
    private void limparCamposERetornar() {
        this.idMovEdicao = -1;
        this.dataOriginalEdicao = "";
        this.titulo.setText("Nova movimentação");
        this.combo.setSelectedIndex(0);
        this.area.setText("");
        this.txtValor.setText("");

        // Executa a transição de volta para o Painel de listagem no CardLayout
        TelaPrincipal tela = (TelaPrincipal) SwingUtilities.getWindowAncestor(this);
        if (tela != null) {
            tela.getCard().show(tela.getPainelConteudo(), "financeiro");

            // CORREÇÃO: Método chamado com "x" (executarConsultaAtual) combinado com o PainelFinanceiro
            for (Component comp : tela.getPainelConteudo().getComponents()) {
                if (comp instanceof PainelFinanceiro) {
                    ((PainelFinanceiro) comp).executarConsultaAtual();
                    break;
                }
            }
        }
    }
}