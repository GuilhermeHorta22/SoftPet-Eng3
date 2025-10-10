package SoftPet.backend.service.template;

import SoftPet.backend.DAO.ProdutoDAO;
import SoftPet.backend.model.ProdutoModel;
import SoftPet.backend.observer.Observer;

import java.util.List;

public class AtualizaEstoqueProduto extends AtualizaEstoqueTemplate<AtualizaEstoqueProduto.Req, ProdutoModel> {
    public static record Req(Long produtoId, int quantidade) {}
    private final ProdutoDAO produtoDAO;
    private final List<Observer> observers;


    public AtualizaEstoqueProduto(ProdutoDAO dao, List<Observer> observers){
        this.produtoDAO=dao; this.observers=observers;
    }
    protected void validar(Req r){ if(r==null||r.produtoId()==null||r.produtoId()<=0||r.quantidade()==0) throw new IllegalArgumentException(); }
    protected ProdutoModel carregarEntidade(Req r) throws Exception { var dto=produtoDAO.findByProduto(r.produtoId()); if(dto==null) throw new Exception("Produto não encontrado"); return dto.getProduto(); }
    protected int obterEstoque(ProdutoModel p){ return p.getQuantidadeEstoque(); }
    protected int calcularDelta(Req r){ return r.quantidade(); } // + repõe / - consome
    protected void persistir(ProdutoModel p,int novo) throws Exception { p.setQuantidadeEstoque(novo); produtoDAO.updateProduto(p); }
    protected void posPersistencia(ProdutoModel p, Req r) { if(p.getQuantidadeEstoque()==0) observers.forEach(o -> o.update(p)); }
}

