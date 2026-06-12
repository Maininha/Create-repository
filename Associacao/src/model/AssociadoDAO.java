package model;

import java.sql.*;
import java.util.ArrayList;

public class AssociadoDAO {

    // ================= MÉTODO DE VALIDAÇÃO DE DUPLICIDADE =================
    public boolean existeCpf(String cpf) {
        String sql = "SELECT COUNT(*) FROM associados WHERE cpf = ?";

        // Remove pontos e traços para garantir consistência na busca
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
    public boolean inserir(Usuario usuario) {

        String sqlEndereco =
                "INSERT INTO enderecos (cidade, estado, referencia, logradouro) VALUES (?, ?, ?, ?)";

        String sqlUsuario =
                "INSERT INTO usuario (cpf, senha) VALUES (?, ?)";

        String sqlAssociado =
                "INSERT INTO associados (cpf, nome, data_cadastro, tipo_perfil, id_endereco, id_usuario) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;

        try {
            conn = Conexao.getConnection();
            conn.setAutoCommit(false);

            int idEndereco = -1;

            // Inserir endereço
            try (PreparedStatement stmt =
                         conn.prepareStatement(sqlEndereco, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, usuario.getEndereco().getCidade());
                stmt.setString(2, usuario.getEndereco().getEstado());
                stmt.setString(3, usuario.getEndereco().getReferencia());
                stmt.setString(4, usuario.getEndereco().getLogradouro());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        idEndereco = rs.getInt(1);
                    }
                }
            }

            if (idEndereco <= 0) {
                throw new SQLException("Não foi possível obter o ID do endereço.");
            }

            Integer idUsuario = null;

            String cpfTratado =
                    usuario.getCpf()
                            .replaceAll("[^0-9]", "")
                            .trim();

            // Inserir usuário (somente gestor)
            if (usuario.getSenha() != null &&
                    !usuario.getSenha().trim().isEmpty()) {

                try (PreparedStatement stmt =
                             conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setString(1, cpfTratado);
                    stmt.setString(2, usuario.getSenha());

                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            idUsuario = rs.getInt(1);
                        }
                    }
                }

                if (idUsuario == null) {
                    throw new SQLException("Não foi possível obter o ID do usuário.");
                }
            }

            // Inserir associado
            try (PreparedStatement stmt =
                         conn.prepareStatement(sqlAssociado)) {

                stmt.setString(1, cpfTratado);
                stmt.setString(2, usuario.getNome());
                stmt.setTimestamp(3,
                        new Timestamp(System.currentTimeMillis()));
                stmt.setString(4, usuario.getTipoPerfil());
                stmt.setInt(5, idEndereco);

                if (idUsuario != null) {
                    stmt.setInt(6, idUsuario);
                } else {
                    stmt.setNull(6, Types.INTEGER);
                }

                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {

            System.err.println("ERRO AO CADASTRAR:");
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return false;

        } finally {

            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ================= LISTAR =================
    public ArrayList<Associado> listar() {
        ArrayList<Associado> lista = new ArrayList<>();
        String sql = """
            SELECT a.cpf, a.nome, a.data_cadastro, a.tipo_perfil,
                   e.cidade, e.estado, e.referencia, e.logradouro
            FROM associados a
            JOIN enderecos e ON a.id_endereco = e.id
        """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Endereco end = new Endereco(
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("referencia"),
                        rs.getString("logradouro")
                );

                Associado a = new Associado();
                a.setCpf(rs.getString("cpf"));
                a.setNome(rs.getString("nome"));
                a.setTipoAssociado(rs.getString("tipo_perfil"));
                a.setDataCadastro(rs.getTimestamp("data_cadastro"));
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
        String sql = """
            SELECT a.cpf, a.nome, a.data_cadastro, a.tipo_perfil,
                   e.cidade, e.estado, e.referencia, e.logradouro
            FROM associados a
            JOIN enderecos e ON a.id_endereco = e.id
            WHERE a.cpf = ?
        """;

        String cpfTratado = cpf.replaceAll("[^0-9]", "").trim();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfTratado);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Endereco end = new Endereco(
                            rs.getString("cidade"),
                            rs.getString("estado"),
                            rs.getString("referencia"),
                            rs.getString("logradouro")
                    );

                    Associado a = new Associado();
                    a.setCpf(rs.getString("cpf"));
                    a.setNome(rs.getString("nome"));
                    a.setTipoAssociado(rs.getString("tipo_perfil"));
                    a.setDataCadastro(rs.getTimestamp("data_cadastro"));
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

    // ================= UPDATE COMPLETO =================
    public boolean atualizarCompleto(Associado a) {
        String sqlId = "SELECT id_endereco FROM associados WHERE cpf = ?";
        String sqlEnd = "UPDATE enderecos SET cidade=?, estado=?, referencia=?, logradouro=? WHERE id=?";
        String sqlAss = "UPDATE associados SET nome=?, tipo_perfil=? WHERE cpf=?";

        String cpfTratado = a.getCpf().replaceAll("[^0-9]", "").trim();

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            int idEndereco = 0;

            try (PreparedStatement stmt = conn.prepareStatement(sqlId)) {
                stmt.setString(1, cpfTratado);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idEndereco = rs.getInt("id_endereco");
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlEnd)) {
                stmt.setString(1, a.getEndereco().getCidade());
                stmt.setString(2, a.getEndereco().getEstado());
                stmt.setString(3, a.getEndereco().getReferencia());
                stmt.setString(4, a.getEndereco().getLogradouro());
                stmt.setInt(5, idEndereco);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlAss)) {
                stmt.setString(1, a.getNome());
                stmt.setString(2, a.getTipoAssociado());
                stmt.setString(3, cpfTratado);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar associado:");
            e.printStackTrace();
            return false;
        }
    }

    // ================= DELETE =================
    public boolean excluir(String cpf) {
        String sqlBusca = "SELECT id_usuario, id_endereco FROM associados WHERE cpf=?";
        String sqlA = "DELETE FROM associados WHERE cpf=?";
        String sqlU = "DELETE FROM usuarios WHERE id=?";
        String sqlE = "DELETE FROM enderecos WHERE id=?";

        String cpfTratado = cpf.replaceAll("[^0-9]", "").trim();

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(false);
            int idU = 0;
            int idE = 0;

            try (PreparedStatement stmt = conn.prepareStatement(sqlBusca)) {
                stmt.setString(1, cpfTratado);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idU = rs.getInt("id_usuario");
                    idE = rs.getInt("id_endereco");
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlA)) {
                stmt.setString(1, cpfTratado);
                stmt.executeUpdate();
            }

            if (idU > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlU)) {
                    stmt.setInt(1, idU);
                    stmt.executeUpdate();
                }
            }

            if (idE > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlE)) {
                    stmt.setInt(1, idE);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erro ao excluir associado:");
            e.printStackTrace();
            return false;
        }
    }
}