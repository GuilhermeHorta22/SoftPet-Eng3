package SoftPet.backend.service;

import SoftPet.backend.DAO.ContatoDAO;
import SoftPet.backend.DAO.PessoaDAO;
import SoftPet.backend.DAO.EnderecoDAO;
import SoftPet.backend.dto.PessoaCompletoDTO;
import SoftPet.backend.model.ContatoModel;
import SoftPet.backend.model.PessoaModel;
import SoftPet.backend.model.EnderecoModel;
import SoftPet.backend.util.cpfValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import SoftPet.backend.util.Validation;

import java.util.List;

@Service
public class PessoaService
{
    @Autowired
    private PessoaDAO pessoaDAO;
    @Autowired
    private ContatoDAO contatoDAO;
    @Autowired
    private EnderecoDAO enderecoDAO;

    public PessoaModel addDoador(PessoaCompletoDTO doador) throws Exception
    {
        if(!cpfValidator.isCpfValido(doador.getPessoa().getCpf()))
            throw new Exception("CPF inválido!");

        if(pessoaDAO.findByDoador(doador.getPessoa().getCpf()) != null)
            throw new Exception("Usuário já cadastrado!");

        if(!Validation.validarNomeCompleto(doador.getPessoa().getNome()))
            throw new Exception("Digite o nome completo corretamente!");

        if(!Validation.validarTelefone(doador.getContato().getTelefone()))
            throw new Exception("Telefone inválido! Ex: (11) 91234-5678");

        if(!Validation.validarRG(doador.getPessoa().getRg()))
            throw new Exception("RG inválido!");

        if(!Validation.validarCEP(doador.getEndereco().getCep()))
            throw new Exception("CEP inválido! Ex: 12345-678");

        ContatoModel novoContato = contatoDAO.addContato(doador.getContato());
        EnderecoModel novoEndereco = enderecoDAO.addEndereco(doador.getEndereco());

        PessoaModel novoDoador = doador.getPessoa();
        novoDoador.setId_contato(novoContato.getId());
        novoDoador.setId_endereco(novoEndereco.getId());

        PessoaModel doadorFinal = pessoaDAO.addDoador(novoDoador);
        return doadorFinal;
    }


    public PessoaCompletoDTO getDoadorCpf(String cpf)
    {
        if(pessoaDAO.findByDoador(cpf) != null)
            return pessoaDAO.findByDoador(cpf);
        return null;
    }

    public void updateDoador(String cpf, PessoaModel doador, ContatoModel contato, EnderecoModel endereco) throws Exception
    {
        if(!cpfValidator.isCpfValido(cpf))
            throw new Exception("CPF inválido!");

        if(!Validation.validarNomeCompleto(doador.getNome()))
            throw new Exception("Digite o nome completo corretamente!");

        if(!Validation.validarTelefone(contato.getTelefone()))
            throw new Exception("Telefone inválido! Ex: (11) 91234-5678");

        if(!Validation.validarRG(doador.getRg()))
            throw new Exception("RG inválido!");

        if(!Validation.validarCEP(endereco.getCep()))
            throw new Exception("CEP inválido! Ex: 12345-678");

        PessoaCompletoDTO doadorExistente = pessoaDAO.findByDoador(cpf);
        if(doadorExistente == null)
            throw new Exception("Não existe esse usuário!");

        doador.setId_contato(doadorExistente.getContato().getId());
        doador.setId_endereco(doadorExistente.getEndereco().getId());

        pessoaDAO.updateDoador(cpf,doador);
        ContatoModel contatoAtualizado = new ContatoModel();
        contatoAtualizado.setId(doador.getId_contato());
        contatoAtualizado.setTelefone(contato.getTelefone());
        contatoAtualizado.setEmail(contato.getEmail());

        contatoDAO.updateContato(contatoAtualizado);
        enderecoDAO.updateEndereco(doador.getId_endereco(),endereco);
    }

    public void deleteDoador(String cpf) throws Exception
    {
        if(!cpfValidator.isCpfValido(cpf))
            throw new Exception("CPF inválido!");

        PessoaCompletoDTO doadorDelete = pessoaDAO.findByDoador(cpf);

        if(doadorDelete == null)
            throw new Exception("Não existe esse usuário!");

        if(!pessoaDAO.deleteFisicoByDoador(cpf))
            throw new Exception("Erro ao deletar um doador!");

        contatoDAO.deleteByContato(doadorDelete.getContato().getId());
        enderecoDAO.deleteByEndereco(doadorDelete.getEndereco().getId());
    }

    public void excluirLogicamente(String cpf) throws Exception {
        PessoaCompletoDTO doador = pessoaDAO.findByDoador(cpf);
        if (doador == null)
            throw new Exception("Doador não encontrado!");

        PessoaModel pessoa = doador.getPessoa();
        pessoa.setStatus(false);
        pessoaDAO.updateDoador(cpf, pessoa);
    }

    public void reativarDoador(String cpf) throws Exception {
        PessoaCompletoDTO doador = pessoaDAO.findByDoador(cpf);
        if (doador == null)
            throw new Exception("Doador não encontrado!");

        PessoaModel pessoa = doador.getPessoa();
        pessoa.setStatus(true);
        pessoaDAO.updateDoador(cpf, pessoa);
    }

    public List<PessoaCompletoDTO> getAllDoador()
    {
        return pessoaDAO.getAll();
    }
}