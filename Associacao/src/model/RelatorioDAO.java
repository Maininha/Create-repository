package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Import adicionado
import java.sql.Timestamp;
import java.util.ArrayList; // Import adicionado
import java.util.Calendar;
import java.util.List; // Import adicionado

public class RelatorioDAO {

    public Relatorio preencherDadosFinanceiros(Relatorio relatorio) {
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
            Calendar calInicio = Calendar.getInstance();
            calInicio.setTime(relatorio.getPeriodoInicial());
            calInicio.set(Calendar.HOUR_OF_DAY, 0);
            calInicio.set(Calendar.MINUTE, 0);
            calInicio.set(Calendar.SECOND, 0);
            calInicio.set(Calendar.MILLISECOND, 0);

            Calendar calFim = Calendar.getInstance();
            calFim.setTime(relatorio.getPeriodoFinal());
            calFim.set(Calendar.HOUR_OF_DAY, 23);
            calFim.set(Calendar.MINUTE, 59);
            calFim.set(Calendar.SECOND, 59);
            calFim.set(Calendar.MILLISECOND, 999);

            stmt.setTimestamp(1, new Timestamp(calInicio.getTimeInMillis()));
            stmt.setTimestamp(2, new Timestamp(calFim.getTimeInMillis()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double entradas = rs.getDouble("total_entradas");
                    double saidas = rs.getDouble("total_saidas");

                    relatorio.setTotalEntradas(entradas);
                    relatorio.setTotalSaidas(saidas);
                    relatorio.setSaldoFinal(entradas - saidas);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao calcular dados financeiros no RelatorioDAO: " + e.getMessage());
        }

        return relatorio;
    }

    // 🛠️ MÉTODO MODIFICADO: Agora salva o relatório e vincula todas as movimentações do período automaticamente
    public boolean salvarNoBanco(Relatorio relatorio) {
        String sqlRelatorio = "INSERT INTO relatorio (periodo_inicial, periodo_final, total_entradas, total_saidas, saldo_final) VALUES (?, ?, ?, ?, ?)";
        String sqlIntermediaria = "INSERT INTO relatorio_movimentacoes (id_relatorio, id_mov) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stmtRelatorio = null;
        PreparedStatement stmtIntermediaria = null;
        ResultSet rsKeys = null;

        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false); // Desativa commit automático para segurança da transação (Tudo ou Nada)

            // 1. Salva na tabela pai 'relatorio' capturando o ID gerado por autoincremento
            stmtRelatorio = conn.prepareStatement(sqlRelatorio, Statement.RETURN_GENERATED_KEYS);
            stmtRelatorio.setTimestamp(1, new Timestamp(relatorio.getPeriodoInicial().getTime()));
            stmtRelatorio.setTimestamp(2, new Timestamp(relatorio.getPeriodoFinal().getTime()));
            stmtRelatorio.setDouble(3, relatorio.getTotalEntradas());
            stmtRelatorio.setDouble(4, relatorio.getTotalSaidas());
            stmtRelatorio.setDouble(5, relatorio.getSaldoFinal());

            int linhasAfetadas = stmtRelatorio.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao inserir o registro de relatório.");
            }

            // Recupera o ID gerado da tabela relatorio
            int idRelatorioGerado = -1;
            rsKeys = stmtRelatorio.getGeneratedKeys();
            if (rsKeys.next()) {
                idRelatorioGerado = rsKeys.getInt(1);
            }

            // 2. Busca dinamicamente os IDs das movimentações que pertencem a este intervalo de datas
            if (idRelatorioGerado != -1) {
                List<Integer> idsMovimentacoes = buscarIdsMovimentacoesNoPeriodo(conn, relatorio.getPeriodoInicial(), relatorio.getPeriodoFinal());

                // 3. Alimenta a tabela relatorio_movimentacoes usando processamento em lote (Batch)
                if (!idsMovimentacoes.isEmpty()) {
                    stmtIntermediaria = conn.prepareStatement(sqlIntermediaria);
                    for (int idMov : idsMovimentacoes) {
                        stmtIntermediaria.setInt(1, idRelatorioGerado);
                        stmtIntermediaria.setInt(2, idMov);
                        stmtIntermediaria.addBatch(); // Empilha para execução em lote
                    }
                    stmtIntermediaria.executeBatch(); // Executa todas as inserções de uma única vez
                }
            }

            conn.commit(); // Grava de forma definitiva em ambas as tabelas
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar histórico de relatório e movimentações no banco: " + e.getMessage());
            if (conn != null) {
                try {
                    System.err.println("Executando Rollback da transação...");
                    conn.rollback(); // Cancela tudo se houver qualquer erro no processo
                } catch (SQLException ex) {
                    System.err.println("Erro ao tentar executar rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            // Garante o fechamento limpo de todos os recursos abertos manualmente
            try { if (rsKeys != null) rsKeys.close(); } catch (Exception e) {}
            try { if (stmtRelatorio != null) stmtRelatorio.close(); } catch (Exception e) {}
            try { if (stmtIntermediaria != null) stmtIntermediaria.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    // 🛠️ MÉTODO AUXILIAR PRIVADO: Varre a tabela 'financeiro' trazendo os IDs que se encaixam no filtro
    private List<Integer> buscarIdsMovimentacoesNoPeriodo(Connection conn, java.util.Date inicio, java.util.Date fim) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sqlBusca = "SELECT id_mov FROM financeiro WHERE data_mov >= ? AND data_mov <= ?";

        Calendar calInicio = Calendar.getInstance();
        calInicio.setTime(inicio);
        calInicio.set(Calendar.HOUR_OF_DAY, 0);
        calInicio.set(Calendar.MINUTE, 0);
        calInicio.set(Calendar.SECOND, 0);
        calInicio.set(Calendar.MILLISECOND, 0);

        Calendar calFim = Calendar.getInstance();
        calFim.setTime(fim);
        calFim.set(Calendar.HOUR_OF_DAY, 23);
        calFim.set(Calendar.MINUTE, 59);
        calFim.set(Calendar.SECOND, 59);
        calFim.set(Calendar.MILLISECOND, 999);

        try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca)) {
            stmtBusca.setTimestamp(1, new Timestamp(calInicio.getTimeInMillis()));
            stmtBusca.setTimestamp(2, new Timestamp(calFim.getTimeInMillis()));

            try (ResultSet rs = stmtBusca.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_mov"));
                }
            }
        }
        return ids;
    }
}