package SoftPet.backend.service.template;

import SoftPet.backend.DAO.ProdutoDAO;
import SoftPet.backend.model.ProdutoModel;
import org.springframework.stereotype.Component;

@Component
public class AtualizaEstoqueProduto
        extends AtualizaEstoqueTemplate<AtualizaEstoqueProduto.Req, ProdutoModel> {

    private final ProdutoDAO produtoDAO;

    public static class Req {
        public Long id;
        public int delta;
        public Req(Long id, int delta) { this.id = id; this.delta = delta; }
    }

    public AtualizaEstoqueProduto(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    @Override
    protected void validar(Req r) {
        if (r == null || r.id == null || r.id <= 0 || r.delta == 0)
            throw new IllegalArgumentException("Dados inválidos");
    }

    @Override
    protected ProdutoModel carregar(Req r) {
        var dto = produtoDAO.findByProduto(r.id);
        if (dto == null || dto.getProduto() == null)
            throw new IllegalArgumentException("Produto não encontrado");
        return dto.getProduto();
    }

    @Override
    protected int obterEstoque(ProdutoModel p) { return p.getQuantidadeEstoque(); }

    @Override
    protected void setEstoque(ProdutoModel p, int novo) { p.setQuantidadeEstoque(novo); }

    @Override
    protected int delta(Req r) { return r.delta; }

    @Override
    protected void salvar(ProdutoModel p) { produtoDAO.updateProduto(p); }

    public void entrada(Long id, int qtd) { executar(new Req(id,  Math.max(0, qtd))); }
    public void saida  (Long id, int qtd) { executar(new Req(id, -Math.max(0, qtd))); }
}
