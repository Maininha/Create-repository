package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class UsuarioDAO {


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
            return hexString.toString(); // Retorna sempre uma string de 64 caracteres
        } catch (Exception e) {
            System.err.println("[DAO] Erro ao criptografar senha.");
            return senhaOriginal;
        }
    }


    public Usuario autenticar(String cpf, String senhaDigitada) {
        // Criptografa a senha que o usuário digitou na tela para comparar com o hash do banco
        String senhaCriptografada = criptografarSenha(senhaDigitada);
        String sql = "SELECT * FROM usuario WHERE cpf = ? AND senha = ?";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.setString(2, senhaCriptografada);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // 🛠️ CORREÇÃO AQUI: Instancia com o construtor vazio e alimenta com os setters
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setCpf(rs.getString("cpf"));
                    usuario.setSenha(rs.getString("senha"));
                    return usuario;
                }
            }
        } catch (Exception e) {
            System.err.println("[DAO] Erro ao autenticar usuário com SHA-256:");
            e.printStackTrace();
        }
        return null;
    }


    public int redefinirSenha(String cpf, String novaSenha) {
        String sqlVerificar = "SELECT cpf FROM usuario WHERE cpf = ?";
        String sqlAtualizar = "UPDATE usuario SET senha = ? WHERE cpf = ?";

        System.out.println("[DAO] Iniciando redefinição criptografada para o CPF: " + cpf);

        try (Connection conn = Conexao.getConnection()) {
            conn.setAutoCommit(true);


            try (PreparedStatement stmtCheck = conn.prepareStatement(sqlVerificar)) {
                stmtCheck.setString(1, cpf);
                try (ResultSet rs = stmtCheck.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("[DAO] Erro: O CPF " + cpf + " não existe na tabela.");
                        return 0;
                    }
                }
            }


            String novaSenhaCriptografada = criptografarSenha(novaSenha);
            System.out.println("[DAO] Senha convertida para SHA-256: " + novaSenhaCriptografada);


            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlAtualizar)) {
                stmtUpdate.setString(1, novaSenhaCriptografada);
                stmtUpdate.setString(2, cpf);

                int linhasAfetadas = stmtUpdate.executeUpdate();
                System.out.println("[DAO] Linhas afetadas pelo UPDATE: " + linhasAfetadas);

                if (linhasAfetadas > 0) {
                    return 1; // Sucesso!
                }
            }

        } catch (Exception e) {
            System.err.println("[DAO] Exceção no banco de dados durante redefinição:");
            e.printStackTrace();
            return -1;
        }

        return 0;
    }
}