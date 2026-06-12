package controller;

import model.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date; // Import necessário para manipular as datas do banco

public class InicioController {

    /**
     * Retorna a quantidade total de associados cadastrados.
     * Tabela: 'associados' (Plural)
     */
    public int obterTotalAssociados() {
        String sql = "SELECT COUNT(*) FROM associados";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao contar associados: " + e.getMessage());
        }
        return 0;
    }

    /**
     * CORREÇÃO: Nome alterado para bater com a chamada do PainelInicio
     * Retorna a quantidade de novos cadastros realizados no mês atual.
     */
    public int obterCadastrosMes() {
        String sql = "SELECT COUNT(*) FROM associados WHERE MONTH(data_cadastro) = MONTH(CURRENT_DATE) AND YEAR(data_cadastro) = YEAR(CURRENT_DATE)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao contar cadastros do mês: " + e.getMessage());
        }
        return 0;
    }

    /**
     * CORREÇÃO: Nome alterado para bater com a chamada do PainelInicio
     * Retorna a quantidade de relatórios oficiais salvos no mês atual.
     */
    public int obterRelatoriosMes() {
        String sql = "SELECT COUNT(*) FROM relatorio WHERE MONTH(data_geracao) = MONTH(CURRENT_DATE) AND YEAR(data_geracao) = YEAR(CURRENT_DATE)";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao contar relatórios do mês: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Faz o INSERT real dos dados calculados na tabela 'relatorio' do MySQL.
     */
    public boolean cadastrarRelatorio(String dataInicial, String dataFinal, double totalEntradas, double totalSaidas, double saldoFinal) {
        String sql = "INSERT INTO relatorio (periodo_inicial, periodo_final, total_entradas, total_saidas, saldo_final) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Converte as Strings "AAAA-MM-DD" para o tipo Date do JDBC/MySQL
            stmt.setDate(1, Date.valueOf(dataInicial));
            stmt.setDate(2, Date.valueOf(dataFinal));
            stmt.setDouble(3, totalEntradas);
            stmt.setDouble(4, totalSaidas);
            stmt.setDouble(5, saldoFinal);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // Retorna true se salvou com sucesso

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar relatório no banco: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Erro no formato das datas enviado pelo formulário: " + e.getMessage());
        }
        return false;
    }
}