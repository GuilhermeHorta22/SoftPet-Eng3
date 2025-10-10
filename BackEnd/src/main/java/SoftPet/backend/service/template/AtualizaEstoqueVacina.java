package SoftPet.backend.service.template;

import SoftPet.backend.DAO.VacinaDAO;
import SoftPet.backend.model.VacinaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// pacote: service.template
@Component
public class AtualizaEstoqueVacina extends AtualizaEstoqueTemplate<AtualizaEstoqueVacina.Req, VacinaModel> {
    public static record Req(Long vacinaId, int quantidade) {}
    private final VacinaDAO vacinaDAO;
    @Autowired
    public AtualizaEstoqueVacina(VacinaDAO dao){ this.vacinaDAO=dao; }
    protected void validar(Req r){ if(r==null||r.vacinaId()==null||r.vacinaId()<=0||r.quantidade()==0) throw new IllegalArgumentException(); }
    protected VacinaModel carregarEntidade(Req r) throws Exception { var v=vacinaDAO.buscarPorCod(r.vacinaId()); if(v==null) throw new Exception("Vacina não encontrada"); return v; }
    protected int obterEstoque(VacinaModel v){ return v.getQuantidadeEstoque(); }        // precisa ter esse campo no model
    protected int calcularDelta(Req r){ return r.quantidade(); }
    protected void persistir(VacinaModel v,int novo) throws Exception { v.setQuantidadeEstoque(novo); vacinaDAO.atualizar(v); }
}
