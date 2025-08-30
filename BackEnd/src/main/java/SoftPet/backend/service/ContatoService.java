package SoftPet.backend.service;

import SoftPet.backend.DAO.ContatoDAO;
import SoftPet.backend.model.ContatoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContatoService {

    @Autowired
    private ContatoDAO contatoDAO;

    public ContatoModel criarContato(ContatoModel contato) {
        return contatoDAO.addContato(contato);
    }

    public ContatoModel buscarPorId(Long id) {
        return contatoDAO.FindById(id);
    }

    public boolean atualizarContato(ContatoModel contato) {
        return contatoDAO.updateContato(contato);
    }

    public boolean deletarContato(Long id) {
        return contatoDAO.deleteByContato(id);
    }
}
