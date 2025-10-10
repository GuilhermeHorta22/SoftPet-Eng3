package SoftPet.backend.service;

import SoftPet.backend.DAO.VacinaDAO;
import SoftPet.backend.model.VacinaModel;
import SoftPet.backend.service.template.AtualizaEstoqueTemplate;
import org.springframework.stereotype.Component;

@Component
public class AtualizaEstoqueVacina extends AtualizaEstoqueTemplate<AtualizaEstoqueVacina.Req, VacinaModel> {

    private final VacinaDAO vacinaDAO;

    public static class Req {
        public Long vacinaId;
        public int quantidade; // + repõe / - consome
        public Req(Long vacinaId, int quantidade) {
            this.vacinaId = vacinaId;
            this.quantidade = quantidade;
        }
    }

    public AtualizaEstoqueVacina(VacinaDAO dao) {
        this.vacinaDAO = dao;
    }

    @Override
    protected void validar(Req r) {
        if (r == null || r.vacinaId == null || r.vacinaId <= 0 || r.quantidade == 0) {
            throw new IllegalArgumentException("Dados inválidos");
        }
    }

    @Override
    protected VacinaModel carregar(Req r) {
        var v = vacinaDAO.buscarPorCod(r.vacinaId);
        if (v == null) throw new IllegalArgumentException("Vacina não encontrada");
        return v;
    }

    @Override
    protected int obterEstoque(VacinaModel v) {
        return v.getQuantidadeEstoque();
    }

    @Override
    protected void setEstoque(VacinaModel v, int novo) {
        v.setQuantidadeEstoque(novo);
    }

    @Override
    protected int delta(Req r) {
        return r.quantidade; // já vem com sinal
    }

    @Override
    protected void salvar(VacinaModel v) {
        vacinaDAO.atualizar(v);
    }

    public void entrada(Long id, int qtd) { executar(new Req(id,  Math.max(0, qtd))); }
    public void saida  (Long id, int qtd) { executar(new Req(id, -Math.max(0, qtd))); }
}
