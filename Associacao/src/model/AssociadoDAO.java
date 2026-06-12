package model;

import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AssociadoDAO {

    // ================= MÉTODO DE VALIDAÇÃO DE DUPLICIDADE =================
    public boolean existeCpf(String cpf) {
        String sql = "SELECT COUNT(*) FROM associados WHERE cpf = ?";
        String cpfTratado = cpf.replaceAll("[^0-9]", "").trim();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfTratado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao verificar duplicidade de CPF:");
            e.printStackTrace();
        }
        return false;
    }

    // ================= CRIPTOGRAFIA SHA-256 =================
    private String criptografarSenha(String senhaOriginal) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senhaOriginal.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            System.err.println("❌ Erro ao criptografar senha.");
            return senhaOriginal;
        }
    }

    // ================= LISTAR ASSOCIADOS (ON a.id_endereco = e.id) =================
    public ArrayList<Associado> listar() {
        ArrayList<Associado> lista = new ArrayList<>();
        String sql = "SELECT a.cpf, a.nome, a.data_cadastro, a.tipo_perfil, " +
                "e.logradouro, e.cidade, e.estado, e.referencia " +
                "FROM associados a " +
                "LEFT JOIN enderecos e ON a.id_endereco = e.id";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Associado a = new Associado();
                a.setCpf(rs.getString("cpf"));
                a.setNome(rs.getString("nome"));
                a.setDataCadastro(rs.getTimestamp("data_cadastro"));
                a.setTipoAssociado(rs.getString("tipo_perfil"));

                Endereco end = new Endereco(
                        rs.getString("cidade") != null ? rs.getString("cidade") : "",
                        rs.getString("estado") != null ? rs.getString("estado") : "",
                        rs.getString("referencia") != null ? rs.getString("referencia") : "",
                        rs.getString("logradouro") != null ? rs.getString("logradouro") : ""
                );
                a.setEndereco(end);

                lista.add(a);
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar associados:");
            e.printStackTrace();
        }
        return lista;
    }

    // ================= BUSCAR POR CPF =================
    public Associado buscarPorCpf(String cpf) {
        String sql = "SELECT a.cpf, a.nome, a.data_cadastro, a.tipo_perfil, " +
                "e.logradouro, e.cidade, e.estado, e.referencia " +
                "FROM associados a " +
                "LEFT JOIN enderecos e ON a.id_endereco = e.id " +
                "WHERE a.cpf = ?";

        String cpfTratado = cpf.replaceAll("[^0-9]", "").trim();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfTratado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Associado a = new Associado();
                    a.setCpf(rs.getString("cpf"));
                    a.setNome(rs.getString("nome"));
                    a.setDataCadastro(rs.getTimestamp("data_cadastro"));
                    a.setTipoAssociado(rs.getString("tipo_perfil"));

                    Endereco end = new Endereco(
                            rs.getString("cidade"),
                            rs.getString("estado"),
                            rs.getString("referencia"),
                            rs.getString("logradouro")
                    );
                    a.setEndereco(end);
                    return a;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar associado por CPF:");
            e.printStackTrace();
        }
        return null;
    }

    // ================= EXCLUIR REGISTRO =================
    public boolean excluir(String cpf) {
        String sqlDeleteAssociado = "DELETE FROM associados WHERE cpf = ?";
        String sqlDeleteEndereco = "DELETE FROM enderecos WHERE id = ?";
        String sqlDeleteUsuario = "DELETE FROM usuario WHERE id = ?";

        String cpfTratado = cpf.replaceAll("[^0-9]", "").trim();
        Connection conn = null;

        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false);

            int idEndereco = -1;
            int idUsuario = -1;

            try (PreparedStatement stmt = conn.prepareStatement("SELECT id_endereco, id_usuario FROM associados WHERE cpf = ?")) {
                stmt.setString(1, cpfTratado);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idEndereco = rs.getInt("id_endereco");
                        idUsuario = rs.getInt("id_usuario");
                        if (rs.wasNull()) idUsuario = -1;
                    }
                }
            }

            // Remove o associado primeiro (por causa do vínculo da FK)
            try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteAssociado)) {
                stmt.setString(1, cpfTratado);
                stmt.executeUpdate();
            }

            // Remove o endereço vinculado
            if (idEndereco > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteEndereco)) {
                    stmt.setInt(1, idEndereco);
                    stmt.executeUpdate();
                }
            }

            // Remove o usuário associado (se houver credencial de login)
            if (idUsuario > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteUsuario)) {
                    stmt.setInt(1, idUsuario);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erro ao excluir associado:");
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // ================= ATUALIZAR DADOS COMPLETOS =================
    public boolean atualizarCompleto(Associado associado) {
        String sqlBuscarEndereco = "SELECT id_endereco FROM associados WHERE cpf = ?";
        String sqlUpdateAssociado = "UPDATE associados SET nome = ? WHERE cpf = ?";
        String sqlUpdateEndereco = "UPDATE enderecos SET cidade = ?, estado = ?, referencia = ?, logradouro = ? WHERE id = ?";

        String cpfTratado = associado.getCpf().replaceAll("[^0-9]", "").trim();
        Connection conn = null;

        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false);

            int idEndereco = -1;

            try (PreparedStatement stmt = conn.prepareStatement(sqlBuscarEndereco)) {
                stmt.setString(1, cpfTratado);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        idEndereco = rs.getInt("id_endereco");
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateAssociado)) {
                stmt.setString(1, associado.getNome());
                stmt.setString(2, cpfTratado);
                stmt.executeUpdate();
            }

            if (idEndereco > 0 && associado.getEndereco() != null) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateEndereco)) {
                    stmt.setString(1, associado.getEndereco().getCidade());
                    stmt.setString(2, associado.getEndereco().getEstado());
                    stmt.setString(3, associado.getEndereco().getReferencia());
                    stmt.setString(4, associado.getEndereco().getLogradouro());
                    stmt.setInt(5, idEndereco);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar dados do associado:");
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // ================= INSERIR NOVO ASSOCIADO =================
    public boolean inserir(Usuario usuario) {
        String sqlEndereco = "INSERT INTO enderecos (cidade, estado, referencia, logradouro) VALUES (?, ?, ?, ?)";
        String sqlUsuario = "INSERT INTO usuario (cpf, senha) VALUES (?, ?)";
        String sqlAssociado = "INSERT INTO associados (cpf, nome, data_cadastro, tipo_perfil, id_endereco, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false);
            int idEndereco = -1;

            try (PreparedStatement stmt = conn.prepareStatement(sqlEndereco, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, usuario.getEndereco().getCidade());
                stmt.setString(2, usuario.getEndereco().getEstado());
                stmt.setString(3, usuario.getEndereco().getReferencia());
                stmt.setString(4, usuario.getEndereco().getLogradouro());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) idEndereco = rs.getInt(1);
                }
            }

            if (idEndereco <= 0) throw new SQLException("Erro ao obter ID do endereço.");

            Integer idUsuario = null;
            String cpfTratado = usuario.getCpf().replaceAll("[^0-9]", "").trim();

            if (usuario.getSenha() != null && !usuario.getSenha().trim().isEmpty()) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, cpfTratado);
                    stmt.setString(2, criptografarSenha(usuario.getSenha()));
                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) idUsuario = rs.getInt(1);
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlAssociado)) {
                stmt.setString(1, cpfTratado);
                stmt.setString(2, usuario.getNome());
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                stmt.setString(4, usuario.getTipoPerfil());
                stmt.setInt(5, idEndereco);

                if (idUsuario != null) stmt.setInt(6, idUsuario);
                else stmt.setNull(6, Types.INTEGER);

                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            System.err.println("❌ ERRO AO CADASTRAR:");
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}