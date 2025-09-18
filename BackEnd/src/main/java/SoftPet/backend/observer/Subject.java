package SoftPet.backend.observer;

import SoftPet.backend.dto.PessoaCompletoDTO;
import SoftPet.backend.model.PessoaModel;
import SoftPet.backend.model.ProdutoModel;

public interface Subject
{
    void adicionarObserver(PessoaCompletoDTO pessoa);
    void removerObserver(PessoaCompletoDTO pessoa);
    void notificarObserver(ProdutoModel produto);
}
