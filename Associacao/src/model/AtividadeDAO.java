package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
// Se a sua classe de conexão estiver em outro pacote (ex: factory.Conexao), importe-a aqui

public class AtividadeDAO {

    public List<Atividade> buscarHistoricoRecente() {
        List<Atividade> lista = new ArrayList<>();

        // CORRIGIDO: Alterado 'FROM associado' para 'FROM associados' (no plural)
        String sql = "SELECT data_cadastro AS data_hora, 'Associados' AS categoria, "
                + "CONCAT('Novo associado cadastrado: ', nome) AS descricao FROM associados " // <-- CORREÇÃO AQUI
                + "UNION "
                + "SELECT data_mov AS data_hora, 'Financeiro' AS categoria, "
                + "CONCAT('Movimentação de ', tipo, ' - R$ ', valor) AS descricao FROM financeiro "
                + "ORDER BY data_hora DESC LIMIT 50";

        // NOTA: Se der erro na palavra 'Conexao', mude para o nome da sua classe de banco (ex: ConnectionFactory)
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Atividade(
                        rs.getTimestamp("data_hora"),
                        rs.getString("categoria"),
                        rs.getString("descricao")
                ));
            }
            System.out.println("[DAO] Histórico carregado com sucesso. Total de linhas: " + lista.size());

        } catch (SQLException e) {
            System.err.println("Erro ao buscar histórico no DAO: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}