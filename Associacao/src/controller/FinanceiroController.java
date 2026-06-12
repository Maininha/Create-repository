package controller;

import model.FinanceiroDAO;
import model.Financeiro;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FinanceiroController {

    private FinanceiroDAO financeiroDAO;
    private SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

    public FinanceiroController() {
        this.financeiroDAO = new FinanceiroDAO();
        this.formatoData.setLenient(false);
    }

    public boolean salvarMovimentacao(String tipo, String categoria, String descricao, String valorStr) {
        if (valorStr == null || valorStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O campo 'Valor' é obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        double valor;
        try {
            valorStr = valorStr.replace(",", ".");
            valor = Double.parseDouble(valorStr);

            if (valor <= 0) {
                JOptionPane.showMessageDialog(null, "O valor deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Digite um formato de valor numérico válido (Ex: 150.50).", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Financeiro financeiro = new Financeiro();
        financeiro.setTipo(tipo);
        financeiro.setCat(categoria);
        financeiro.setDesc(descricao);
        financeiro.setValor(valor);
        financeiro.setData(new Date());

        boolean sucesso = financeiroDAO.salvar(financeiro);

        if (sucesso) {
            JOptionPane.showMessageDialog(null, "Movimentação registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Falha ao salvar no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        return sucesso;
    }

    // ========================================================================
    // REGRA DE NEGÓCIO: Executa as validações para a Alteração/Edição
    // ========================================================================
    public boolean editarMovimentacao(int idMov, String tipo, String categoria, String descricao, String valorStr, String dataStr) {
        if (valorStr == null || valorStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O campo 'Valor' é obrigatório para edição.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        double valor;
        Date dataMov;
        try {
            valorStr = valorStr.replace("R$", "").replace(".", "").replace(",", ".").trim();
            valor = Double.parseDouble(valorStr);
            if (valor <= 0) {
                JOptionPane.showMessageDialog(null, "O valor deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            dataMov = formatoData.parse(dataStr.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Verifique os valores e o formato da data (dd/MM/yyyy).", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Financeiro f = new Financeiro();
        f.setIdMov(idMov);
        f.setTipo(tipo);
        f.setCat(categoria);
        f.setDesc(descricao);
        f.setValor(valor);
        f.setData(dataMov);

        boolean sucesso = financeiroDAO.atualizar(f);
        if (sucesso) {
            JOptionPane.showMessageDialog(null, "Movimentação alterada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return sucesso;
    }

    public boolean excluirMovimentacao(int idMov) {
        boolean sucesso = financeiroDAO.excluir(idMov);
        if (sucesso) {
            JOptionPane.showMessageDialog(null, "Movimentação excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Falha ao excluir a movimentação.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return sucesso;
    }

    public void atualizarTabela(DefaultTableModel modelo, String tipoFiltro, String dataInicioStr, String dataFimStr) {
        modelo.setRowCount(0);

        Date dataInicio = null;
        Date dataFim = null;

        try {
            if (dataInicioStr != null && !dataInicioStr.trim().isEmpty()) {
                dataInicio = formatoData.parse(dataInicioStr.trim());
            }
            if (dataFimStr != null && !dataFimStr.trim().isEmpty()) {
                dataFim = formatoData.parse(dataFimStr.trim());
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "As datas devem estar no formato correto: dd/MM/yyyy", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dataInicio != null && dataFim != null && dataInicio.after(dataFim)) {
            JOptionPane.showMessageDialog(null, "A data de início não pode ser posterior à data final.", "Período Incorreto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Financeiro> movimentacoes = financeiroDAO.listar(tipoFiltro, dataInicio, dataFim);

        if (movimentacoes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Não foram encontradas movimentações financeiras para o período ou tipo selecionado.", "Sem Resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Financeiro f : movimentacoes) {
            String dataStr = (f.getData() != null) ? formatoData.format(f.getData()) : "";
            String valorFormatado = String.format("R$ %.2f", f.getValor());

            modelo.addRow(new Object[]{
                    f.getIdMov(),
                    dataStr,
                    f.getTipo(),
                    f.getCat(),
                    f.getDesc(),
                    valorFormatado
            });
        }
    }
}