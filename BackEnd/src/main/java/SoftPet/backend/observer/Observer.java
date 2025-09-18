package SoftPet.backend.observer;

import SoftPet.backend.dto.PessoaCompletoDTO;
import SoftPet.backend.model.PessoaModel;
import SoftPet.backend.model.ProdutoModel;

public interface Observer
{
    void update(ProdutoModel produto, PessoaCompletoDTO pessoa);
}
