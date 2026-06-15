package controller;

import model.FinanceiroDAO;
import model.Financeiro;
import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FinanceiroController {

    private FinanceiroDAO financeiroDAO;

    public FinanceiroController() {
        this.financeiroDAO = new FinanceiroDAO();
    }

    public boolean salvarMovimentacao(String tipo, String categoria, String descricao, String valorStr, String cpfAssociado) {
        if (valorStr == null || valorStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O campo 'Valor' é obrigatório.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        BigDecimal valor;
        try {
            valorStr = valorStr.replace(",", ".").trim();
            valor = new BigDecimal(valorStr);

            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
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
        financeiro.setValor(valor.doubleValue());
        financeiro.setData(new Date());
        financeiro.setCpfAssociado(cpfAssociado);

        boolean sucesso = financeiroDAO.salvar(financeiro);

        if (sucesso) {
            JOptionPane.showMessageDialog(null, "Movimentação registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Falha ao salvar no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        return sucesso;
    }

    /**
     * 🛠️ RECOLOCADO: Método responsável por processar as atualizações da View no Banco
     */
    public boolean editarMovimentacao(int idMov, String tipo, String categoria, String descricao, String valorStr) {
        if (valorStr == null || valorStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "O campo 'Valor' é obrigatório para edição.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        BigDecimal valor;
        try {
            valorStr = valorStr.replace(",", ".").trim();
            valor = new BigDecimal(valorStr);

            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(null, "O valor editado deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Digite um formato de valor numérico válido para a edição (Ex: 150.50).", "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Financeiro financeiro = new Financeiro();
        financeiro.setIdMov(idMov);
        financeiro.setTipo(tipo);
        financeiro.setCat(categoria);
        financeiro.setDesc(descricao);
        financeiro.setValor(valor.doubleValue());

        if (this.financeiroDAO == null) {
            this.financeiroDAO = new FinanceiroDAO();
        }

        boolean sucesso = financeiroDAO.editar(financeiro);

        if (sucesso) {
            JOptionPane.showMessageDialog(null, "Movimentação atualizada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Falha ao atualizar a movimentação no banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

        return sucesso;
    }

    public boolean excluirMovimentacao(int idMov) {
        if (this.financeiroDAO == null) {
            this.financeiroDAO = new FinanceiroDAO();
        }
        return this.financeiroDAO.excluir(idMov);
    }

    public List<Financeiro> buscarMovimentacoesFiltradas(String tipo, String dataInicio, String dataFim) {
        if (this.financeiroDAO == null) {
            this.financeiroDAO = new FinanceiroDAO();
        }
        return this.financeiroDAO.listar(tipo, dataInicio, dataFim);
    }
}