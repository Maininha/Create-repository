package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FinanceiroDAO {

    public boolean salvar(Financeiro financeiro) {
        String sql = "INSERT INTO financeiro (data_mov, valor, descricao, categoria, tipo, cpf_associado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(financeiro.getData().getTime()));
            stmt.setDouble(2, Math.abs(financeiro.getValor()));
            stmt.setString(3, financeiro.getDesc());
            stmt.setString(4, financeiro.getCat());
            stmt.setString(5, financeiro.getTipo());

            // 1. Limpa o CPF mantendo apenas dígitos numéricos
            String cpfLimpo = null;
            if (financeiro.getCpfAssociado() != null && !financeiro.getCpfAssociado().trim().isEmpty()) {
                cpfLimpo = financeiro.getCpfAssociado().replaceAll("[^0-9]", "");
            }

            // 2. Valida se o CPF limpo existe na tabela associados antes de atribuir à Foreign Key
            if (cpfLimpo != null && !cpfLimpo.isEmpty() && existeAssociado(conn, cpfLimpo)) {
                stmt.setString(6, cpfLimpo);
            } else {
                stmt.setNull(6, java.sql.Types.VARCHAR);
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar movimentação financeira: " + e.getMessage());
            return false;
        }
    }

    // Método auxiliar para verificar se o CPF existe na tabela 'associados'
    private boolean existeAssociado(Connection conn, String cpf) {
        String sql = "SELECT 1 FROM associados WHERE REPLACE(REPLACE(cpf, '.', ''), '-', '') = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Aviso ao verificar existência de associado: " + e.getMessage());
            return false;
        }
    }

    public List<Financeiro> listar(String tipo, String dataInicio, String dataFim) {
        List<Financeiro> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id_mov, data_mov, valor, descricao, categoria, tipo, cpf_associado FROM financeiro WHERE 1=1 ");

        if (tipo != null && !tipo.equalsIgnoreCase("Todos")) {
            sql.append("AND tipo = ? ");
        }

        boolean filtrarDatas = (dataInicio != null && !dataInicio.trim().isEmpty() && dataFim != null && !dataFim.trim().isEmpty());
        if (filtrarDatas) {
            sql.append("AND STR_TO_DATE(data_mov, '%Y-%m-%d') BETWEEN STR_TO_DATE(?, '%d/%m/%Y') AND STR_TO_DATE(?, '%d/%m/%Y') ");
        }

        sql.append("ORDER BY data_mov DESC");

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (tipo != null && !tipo.equalsIgnoreCase("Todos")) {
                stmt.setString(index++, tipo);
            }
            if (filtrarDatas) {
                stmt.setString(index++, dataInicio);
                stmt.setString(index++, dataFim);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Financeiro f = new Financeiro();
                    f.setIdMov(rs.getInt("id_mov"));
                    f.setData(rs.getTimestamp("data_mov"));
                    f.setValor(rs.getDouble("valor"));
                    f.setDesc(rs.getString("descricao"));
                    f.setCat(rs.getString("categoria"));
                    f.setTipo(rs.getString("tipo"));
                    f.setCpfAssociado(rs.getString("cpf_associado"));
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao filtrar movimentações gerais por texto: " + e.getMessage());
        }
        return lista;
    }

    public List<Financeiro> listarPorPeriodo(String tipo, java.util.Date dataInicio, java.util.Date dataFim) {
        List<Financeiro> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String sql = "SELECT id_mov, data_mov, valor, descricao, categoria, tipo, cpf_associado FROM financeiro ";
        if (dataInicio != null && dataFim != null) {
            sql += "WHERE data_mov BETWEEN ? AND ? ";
        }
        sql += "ORDER BY data_mov DESC";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (dataInicio != null && dataFim != null) {
                stmt.setString(1, sdf.format(dataInicio));
                stmt.setString(2, sdf.format(dataFim));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Financeiro f = new Financeiro();
                    f.setIdMov(rs.getInt("id_mov"));
                    f.setData(rs.getTimestamp("data_mov"));
                    f.setValor(rs.getDouble("valor"));
                    f.setDesc(rs.getString("descricao"));
                    f.setCat(rs.getString("categoria"));
                    f.setTipo(rs.getString("tipo"));
                    f.setCpfAssociado(rs.getString("cpf_associado"));
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar movimentações filtradas por período: " + e.getMessage());
        }
        return lista;
    }

    public boolean editar(Financeiro financeiro) {
        String sql = "UPDATE financeiro SET tipo = ?, categoria = ?, descricao = ?, valor = ? WHERE id_mov = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, financeiro.getTipo());
            stmt.setString(2, financeiro.getCat());
            stmt.setString(3, financeiro.getDesc());
            stmt.setDouble(4, Math.abs(financeiro.getValor()));
            stmt.setInt(5, financeiro.getIdMov());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erro ao editar movimentação no banco: " + e.getMessage());
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
            System.err.println("❌ Erro ao deletar registro financeiro no banco: " + e.getMessage());
            return false;
        }
    }
}