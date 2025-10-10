package SoftPet.backend.service.template;

public abstract class AtualizaEstoqueTemplate<RQ, ENT> {
    public final void executar(RQ req) throws Exception {
        validar(req);
        ENT ent = carregarEntidade(req);
        int novo = aplicarRegras(obterEstoque(ent), calcularDelta(req));
        persistir(ent, novo);
        posPersistencia(ent, req);
    }
    protected abstract void validar(RQ req) throws Exception;
    protected abstract ENT carregarEntidade(RQ req) throws Exception;
    protected abstract int obterEstoque(ENT ent);
    protected abstract int calcularDelta(RQ req);
    protected abstract void persistir(ENT ent, int novo) throws Exception;
    protected void posPersistencia(ENT ent, RQ req) throws Exception {}
    protected int aplicarRegras(int atual, int delta){
        int novo = atual + delta; if(novo<0) throw new IllegalArgumentException("Estoque insuficiente"); return novo;
    }
}

