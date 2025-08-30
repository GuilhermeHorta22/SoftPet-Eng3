package SoftPet.backend.service;

import SoftPet.backend.DAO.VoluntarioDAO;
import SoftPet.backend.model.VoluntarioModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoluntarioService {

    @Autowired
    private VoluntarioDAO voluntarioDAO;

    public VoluntarioModel cadastrarVoluntario(VoluntarioModel voluntario) throws InstantiationException, IllegalAccessException {
        // 1. Validações importantes ANTES de tentar criar:
        if (voluntario == null) {
            throw new IllegalArgumentException("Dados do voluntário não podem ser nulos.");
        }
        if (voluntario.getCpf() == null || voluntario.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do voluntário é obrigatório.");
        }
        // Adicione outras validações de campos obrigatórios aqui (nome, etc.)

        // 2. [RECOMENDADO] Verificar se já existe um voluntário com o mesmo CPF
        //    Isso previne duplicidade baseada em um identificador de negócio.
        //    Assumindo que seu VoluntarioDAO tem um método findByCPF.
        VoluntarioModel existentePorCPF = voluntarioDAO.findByCPF(voluntario.getCpf());
        if (existentePorCPF != null) {
            throw new IllegalArgumentException("Já existe um voluntário cadastrado com o CPF: " + voluntario.getCpf());
        }

        // A verificação por ID que causava o erro foi REMOVIDA.
        // Não faz sentido buscar um novo voluntário por um ID que ainda não foi gerado.
        // O ID será gerado pelo banco de dados durante a operação de 'create'.

        // 3. Se todas as validações passarem, prossiga com a criação
        return voluntarioDAO.create(voluntario); // O DAL.create() irá inserir e retornar o voluntário com o ID gerado.
    }

    public List<VoluntarioModel> listarTodos() {
        return voluntarioDAO.getAll();
    }

    public VoluntarioModel buscarPorCPF(String cpf) {
        return voluntarioDAO.findByCPF(cpf);
    }

    public boolean removerPorCPF(String cpf) {
        return voluntarioDAO.deleteByCPF(cpf);
    }

    public boolean atualizarVoluntario(VoluntarioModel voluntario) {
        return voluntarioDAO.atualizar(voluntario);
    }

    public VoluntarioModel buscarPorId(Long id) {
        return voluntarioDAO.buscarPorId(id);
    }

}
