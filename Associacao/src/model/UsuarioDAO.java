package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    // Seu método autenticar (mantido intacto)
    public Usuario autenticar(String cpf, String senha) {
        String sql = "SELECT * FROM usuario WHERE cpf = ? AND senha = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getString("cpf"), rs.getString("senha"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Versão corrigida e monitorada do método de redefinição
     */
    public int redefinirSenha(String cpf, String novaSenha) {
        // 🔥 ATENÇÃO: Verifique se na sua tabela o nome é 'usuario', 'senha' e 'cpf' mesmo!
        String sqlVerificar = "SELECT cpf FROM usuario WHERE cpf = ?";
        String sqlAtualizar = "UPDATE usuario SET senha = ? WHERE cpf = ?";

        System.out.println("[DAO] Iniciando redefinição para o CPF: " + cpf);

        try (Connection conn = Conexao.getConnection()) {

            // Garantir que as alterações sejam salvas imediatamente no banco
            conn.setAutoCommit(true);

            // 1. Validar se o CPF existe
            try (PreparedStatement stmtCheck = conn.prepareStatement(sqlVerificar)) {
                stmtCheck.setString(1, cpf);
                try (ResultSet rs = stmtCheck.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("[DAO] Erro: O CPF " + cpf + " não existe na tabela.");
                        return 0; // CPF não encontrado
                    }
                }
            }

            System.out.println("[DAO] CPF confirmado. Atualizando senha...");

            // 2. Executar a alteração da senha
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlAtualizar)) {
                stmtUpdate.setString(1, novaSenha);
                stmtUpdate.setString(2, cpf);

                int linhasAfetadas = stmtUpdate.executeUpdate();
                System.out.println("[DAO] Linhas afetadas pelo UPDATE: " + linhasAfetadas);

                if (linhasAfetadas > 0) {
                    return 1; // Sucesso!
                }
            }

        } catch (Exception e) {
            System.err.println("[DAO] Exceção disparada no banco de dados:");
            e.printStackTrace();
            return -1; // Erro de SQL/Conexão
        }

        return 0;
    }
}