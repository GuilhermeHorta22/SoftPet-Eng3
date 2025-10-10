package SoftPet.backend.service.template;

public abstract class AtualizaEstoqueTemplate<RQ, ENT> {

    public final void executar(RQ req) {
        validar(req);                  // pode ficar vazio
        ENT ent = carregar(req);       // busca entidade
        int atual = obterEstoque(ent); // estoque atual
        int novo  = atual + delta(req);// aplica variação

        if (novo < 0) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }

        setEstoque(ent, novo);
        salvar(ent);
    }

    protected void validar(RQ req) { }
    protected abstract ENT carregar(RQ req);
    protected abstract int obterEstoque(ENT ent);
    protected abstract void setEstoque(ENT ent, int novo);
    protected abstract int delta(RQ req);
    protected abstract void salvar(ENT ent);
}
