package SoftPet.backend.observer;

import SoftPet.backend.dto.PessoaCompletoDTO;
import SoftPet.backend.model.PessoaModel;
import SoftPet.backend.model.ProdutoModel;

public interface Subject
{
    void adicionarObserver(Observer observer);
    void removerObserver(Observer observer);
    void notificarObserver(ProdutoModel produto);
}
