package controller;

import model.Atividade;
import model.AtividadeDAO;
import java.util.List;

public class AtividadeController {
    private AtividadeDAO dao;

    public AtividadeController() {
        this.dao = new AtividadeDAO();
    }

    public List<Atividade> obterCronogramaAtividades() {
        return dao.buscarHistoricoRecente();
    }
}