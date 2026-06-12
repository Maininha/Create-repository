package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class RelatorioDAO {

    public Relatorio preencherDadosFinanceiros(Relatorio relatorio) {

        String sql =
                "SELECT " +
                        "COALESCE(SUM(CASE WHEN LOWER(tipo)='entrada' THEN valor END),0) AS total_entradas," +
                        "COALESCE(SUM(CASE WHEN LOWER(tipo)='saída' OR LOWER(tipo)='saida' THEN valor END),0) AS total_saidas " +
                        "FROM financeiro " +
                        "WHERE data_mov BETWEEN ? AND ?";

        try (
                Connection conn = Conexao.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setDate(
                    1,
                    new Date(relatorio.getPeriodoInicial().getTime())
            );

            stmt.setDate(
                    2,
                    new Date(relatorio.getPeriodoFinal().getTime())
            );

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                double entradas =
                        rs.getDouble("total_entradas");

                double saidas =
                        rs.getDouble("total_saidas");

                relatorio.setTotalEntradas(entradas);
                relatorio.setTotalSaidas(saidas);
                relatorio.setSaldoFinal(entradas - saidas);
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao gerar relatório: "
                            + e.getMessage()
            );
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

            stmt.setDate(
                    1,
                    new Date(relatorio.getPeriodoInicial().getTime())
            );

            stmt.setDate(
                    2,
                    new Date(relatorio.getPeriodoFinal().getTime())
            );

            stmt.setDouble(
                    3,
                    relatorio.getTotalEntradas()
            );

            stmt.setDouble(
                    4,
                    relatorio.getTotalSaidas()
            );

            stmt.setDouble(
                    5,
                    relatorio.getSaldoFinal()
            );

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao salvar relatório: "
                            + e.getMessage()
            );

            return false;
        }
    }
}