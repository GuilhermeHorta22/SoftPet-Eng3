package SoftPet.backend.service;

import SoftPet.backend.DAO.AdocaoDAO;
import SoftPet.backend.DAO.AnimalDAO;
import SoftPet.backend.DAO.PessoaDAO;
import SoftPet.backend.dto.AdocaoDTO;
import SoftPet.backend.dto.PessoaCompletoDTO;
import SoftPet.backend.model.AdocaoModel;
import SoftPet.backend.model.AnimalModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import SoftPet.backend.util.Validation;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdocaoService {
    @Autowired
    private AdocaoDAO adocaoDAO;
    @Autowired
    private AnimalDAO animalDAO;
    @Autowired
    private PessoaDAO pessoaDAO;

    public AdocaoModel addAdocao(AdocaoDTO adocaoDTO) {
        // Validação básica do DTO
        System.out.println("Dados recebidos: servise");
        System.out.println("Data: " + adocaoDTO.getAdocao().getAdo_dt());
        System.out.println("ID Pessoa: " + adocaoDTO.getPessoa().getId());
        System.out.println("ID Animal: " + adocaoDTO.getAnimal().getCod());
        if (adocaoDTO == null || adocaoDTO.getPessoa() == null || adocaoDTO.getAnimal() == null) {
            throw new IllegalArgumentException("Dados de adoção inválidos");
        }
        System.out.println(adocaoDTO.getAnimal().getCod());
        // Busca animal - verifica se existe
        AnimalModel animal = animalDAO.buscarPorCod(adocaoDTO.getAnimal().getCod());
        if (animal == null) {
            System.out.println(adocaoDTO.getAnimal().getCod());
            throw new IllegalArgumentException("Animal não encontrado!");
        }
        if (!Validation.ValidarIdade(animal.getIdade())) {

            throw new IllegalArgumentException("Idade do animal negativa!");
        }

        // Busca adotante - usando pe_cod do JSON
        PessoaCompletoDTO pessoa = pessoaDAO.findById(adocaoDTO.getPessoa().getId());
        if (pessoa == null) {
            throw new IllegalArgumentException("Adotante não encontrado!");
        }

        // Cria nova adoção
        AdocaoModel novaAdocao = new AdocaoModel();
        novaAdocao.setAdo_dt(adocaoDTO.getAdocao().getAdo_dt());
        novaAdocao.setAn_cod(animal.getCod());
        novaAdocao.setPe_cod(pessoa.getPessoa().getId());

        return adocaoDAO.NovaAdocao(novaAdocao);
    }

    public List<AdocaoDTO> buscarAdocoes(String cpf, LocalDate dataInicio, LocalDate dataFim) {
        return adocaoDAO.buscarAdocoes(cpf, dataInicio, dataFim);
    }

    public boolean upContrato(Long id, byte[] contrato)
    {
        AdocaoModel adocao = new AdocaoModel();
        adocao=adocaoDAO.buscarAdocaoPorId(id).getAdocao();
        if(adocao!=null)
        {
            return adocaoDAO.atualizarContratoAdocao(id, contrato);
        }
        return false;
    }
    public byte[] buscarContratoPorIdAdocao(Long idAdocao) {
        AdocaoDTO adocao = adocaoDAO.buscarAdocaoPorId(idAdocao);
        if (adocao.getAdocao().getAdo_contrato() == null || adocao.getAdocao().getAdo_contrato().length == 0) {
            throw new RuntimeException("Contrato não encontrado para a adoção ID: " + idAdocao);
        }
        return adocao.getAdocao().getAdo_contrato();
    }
}
