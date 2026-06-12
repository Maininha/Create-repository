package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class RelatorioDAO {

    public Relatorio preencherDadosFinanceiros(Relatorio relatorio) {

        // 🛠️ CORREÇÃO ABS(): Ignora se o valor foi salvo como positivo ou negativo, somando o valor absoluto.
        String sql =
                "SELECT " +
                        "COALESCE(SUM(CASE WHEN LOWER(tipo)='entrada' THEN ABS(valor) END), 0) AS total_entradas, " +
                        "COALESCE(SUM(CASE WHEN LOWER(tipo) IN ('saída', 'saida') THEN ABS(valor) END), 0) AS total_saidas " +
                        "FROM financeiro " +
                        "WHERE data_mov >= ? AND data_mov <= ?";

        try (
                Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Configura o início do dia para o período inicial (00:00:00)
            Calendar calInicio = Calendar.getInstance();
            calInicio.setTime(relatorio.getPeriodoInicial());
            calInicio.set(Calendar.HOUR_OF_DAY, 0);
            calInicio.set(Calendar.MINUTE, 0);
            calInicio.set(Calendar.SECOND, 0);
            calInicio.set(Calendar.MILLISECOND, 0);

            // Configura o fim do dia para o período final (23:59:59)
            Calendar calFim = Calendar.getInstance();
            calFim.setTime(relatorio.getPeriodoFinal());
            calFim.set(Calendar.HOUR_OF_DAY, 23);
            calFim.set(Calendar.MINUTE, 59);
            calFim.set(Calendar.SECOND, 59);
            calFim.set(Calendar.MILLISECOND, 999);

            // 🛠️ CORREÇÃO TIMESTAMP: Evita que movimentações no último dia do período sejam ignoradas
            stmt.setTimestamp(1, new Timestamp(calInicio.getTimeInMillis()));
            stmt.setTimestamp(2, new Timestamp(calFim.getTimeInMillis()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double entradas = rs.getDouble("total_entradas");
                    double saidas = rs.getDouble("total_saidas");

                    relatorio.setTotalEntradas(entradas);
                    relatorio.setTotalSaidas(saidas);

                    // Com as saídas somadas de forma absoluta e correta, a subtração é perfeitamente segura
                    relatorio.setSaldoFinal(entradas - saidas);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular dados financeiros no RelatorioDAO: " + e.getMessage());
        }

        return relatorio;
    }

    public boolean salvarNoBanco(Relatorio relatorio) {

        String sql =
                "INSERT INTO relatorio " +
                        "(periodo_inicial, periodo_final, total_entradas, total_saidas, saldo_final) " +
                        "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Salva com o timestamp exato do momento da geração
            stmt.setTimestamp(1, new Timestamp(relatorio.getPeriodoInicial().getTime()));
            stmt.setTimestamp(2, new Timestamp(relatorio.getPeriodoFinal().getTime()));
            stmt.setDouble(3, relatorio.getTotalEntradas());
            stmt.setDouble(4, relatorio.getTotalSaidas());
            stmt.setDouble(5, relatorio.getSaldoFinal());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar histórico de relatório no banco: " + e.getMessage());
            return false;
        }
    }
}