package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FinanceiroDAO {

    public boolean salvar(Financeiro financeiro) {
        String sql = "INSERT INTO financeiro (data_mov, valor, descricao, categoria, tipo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(financeiro.getData().getTime()));


            stmt.setDouble(2, Math.abs(financeiro.getValor()));

            stmt.setString(3, financeiro.getDesc());
            stmt.setString(4, financeiro.getCat());
            stmt.setString(5, financeiro.getTipo());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar movimentação financeira: " + e.getMessage());
            return false;
        }
    }

    public List<Financeiro> listar(String tipoFiltro, java.util.Date dataInicio, java.util.Date dataFim) {

        StringBuilder sql = new StringBuilder(
                "SELECT id_mov, data_mov, ABS(valor) AS valor_limpo, descricao, categoria, tipo FROM financeiro WHERE 1=1"
        );

        if (tipoFiltro != null && !tipoFiltro.equals("Todos")) {
            sql.append(" AND tipo = ?");
        }
        if (dataInicio != null) {
            sql.append(" AND data_mov >= ?");
        }
        if (dataFim != null) {
            sql.append(" AND data_mov <= ?");
        }

        sql.append(" ORDER BY data_mov DESC");
        List<Financeiro> lista = new ArrayList<>();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (tipoFiltro != null && !tipoFiltro.equals("Todos")) {
                stmt.setString(paramIndex++, tipoFiltro);
            }
            if (dataInicio != null) {
                Calendar calInic = Calendar.getInstance();
                calInic.setTime(dataInicio);
                calInic.set(Calendar.HOUR_OF_DAY, 0);
                calInic.set(Calendar.MINUTE, 0);
                calInic.set(Calendar.SECOND, 0);
                calInic.set(Calendar.MILLISECOND, 0);
                stmt.setTimestamp(paramIndex++, new Timestamp(calInic.getTimeInMillis()));
            }
            if (dataFim != null) {
                Calendar calFim = Calendar.getInstance();
                calFim.setTime(dataFim);
                calFim.set(Calendar.HOUR_OF_DAY, 23);
                calFim.set(Calendar.MINUTE, 59);
                calFim.set(Calendar.SECOND, 59);
                calFim.set(Calendar.MILLISECOND, 999);
                stmt.setTimestamp(paramIndex++, new Timestamp(calFim.getTimeInMillis()));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Financeiro f = new Financeiro();
                    f.setIdMov(rs.getInt("id_mov"));
                    f.setData(rs.getTimestamp("data_mov"));


                    f.setValor(rs.getDouble("valor_limpo"));

                    f.setDesc(rs.getString("descricao"));
                    f.setCat(rs.getString("categoria"));
                    f.setTipo(rs.getString("tipo"));
                    lista.add(f);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao filtrar movimentações financeiras: " + e.getMessage());
        }

        return lista;
    }

    public boolean atualizar(Financeiro financeiro) {
        String sql = "UPDATE financeiro SET data_mov = ?, valor = ?, descricao = ?, categoria = ?, tipo = ? WHERE id_mov = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(financeiro.getData().getTime()));


            stmt.setDouble(2, Math.abs(financeiro.getValor()));

            stmt.setString(3, financeiro.getDesc());
            stmt.setString(4, financeiro.getCat());
            stmt.setString(5, financeiro.getTipo());
            stmt.setInt(6, financeiro.getIdMov());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar movimentação: " + e.getMessage());
            return false;
        }
    }

    public boolean excluir(int idMov) {
        String sql = "DELETE FROM financeiro WHERE id_mov = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMov);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao excluir movimentação: " + e.getMessage());
            return false;
        }
    }


}