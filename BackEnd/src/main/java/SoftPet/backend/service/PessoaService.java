package SoftPet.backend.service;

import SoftPet.backend.DAO.ContatoDAO;
import SoftPet.backend.DAO.PessoaDAO;
import SoftPet.backend.DAO.EnderecoDAO;
import SoftPet.backend.dto.PessoaCompletoDTO;
import SoftPet.backend.model.ContatoModel;
import SoftPet.backend.model.PessoaModel;
import SoftPet.backend.model.EnderecoModel;
import SoftPet.backend.model.ProdutoModel;
import SoftPet.backend.observer.Observer;
import SoftPet.backend.util.cpfValidator;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import SoftPet.backend.util.Validation;
import SoftPet.backend.observer.Observer;

import java.util.List;
import java.util.Properties;

@Service
public class PessoaService implements Observer
{
    @Autowired
    private PessoaDAO pessoaDAO;
    @Autowired
    private ContatoDAO contatoDAO;
    @Autowired
    private EnderecoDAO enderecoDAO;

    @Autowired
    private ProdutoService produtoService;

    @PostConstruct
    public void init()
    {
        produtoService.adicionarObserver(this); //registrando PessoaService como observer
    }

    public PessoaModel addPessoa(PessoaCompletoDTO pessoa) throws Exception
    {
        if(!cpfValidator.isCpfValido(pessoa.getPessoa().getCpf()))
            throw new Exception("CPF inválido!");

        if(pessoaDAO.findByPessoa(pessoa.getPessoa().getCpf()) != null)
            throw new Exception("Usuário já cadastrado!");

        if(!Validation.validarNomeCompleto(pessoa.getPessoa().getNome()))
            throw new Exception("Digite o nome completo corretamente!");

        if(!Validation.validarTelefone(pessoa.getContato().getTelefone()))
            throw new Exception("Telefone inválido! Ex: (11) 91234-5678");

        if(!Validation.validarRG(pessoa.getPessoa().getRg()))
            throw new Exception("RG inválido!");

        if(!Validation.validarCEP(pessoa.getEndereco().getCep()))
            throw new Exception("CEP inválido! Ex: 12345-678");

        ContatoModel novoContato = contatoDAO.addContato(pessoa.getContato());
        EnderecoModel novoEndereco = enderecoDAO.addEndereco(pessoa.getEndereco());

        PessoaModel novaPessoa = pessoa.getPessoa();
        novaPessoa.setId_contato(novoContato.getId());
        novaPessoa.setId_endereco(novoEndereco.getId());

        PessoaModel pessoaFinal = pessoaDAO.addPessoa(novaPessoa);

        return pessoaFinal;
    }

    public PessoaCompletoDTO getPessoaCpf(String cpf)
    {
        if(pessoaDAO.findByPessoa(cpf) != null)
            return pessoaDAO.findByPessoa(cpf);
        return null;
    }

    public void updatePessoa(String cpf, PessoaModel pessoa, ContatoModel contato, EnderecoModel endereco) throws Exception
    {
        if(!cpfValidator.isCpfValido(cpf))
            throw new Exception("CPF inválido!");

        if(!Validation.validarNomeCompleto(pessoa.getNome()))
            throw new Exception("Digite o nome completo corretamente!");

        if(!Validation.validarTelefone(contato.getTelefone()))
            throw new Exception("Telefone inválido! Ex: (11) 91234-5678");

        if(!Validation.validarRG(pessoa.getRg()))
            throw new Exception("RG inválido!");

        if(!Validation.validarCEP(endereco.getCep()))
            throw new Exception("CEP inválido! Ex: 12345-678");

        PessoaCompletoDTO pessoaExistente = pessoaDAO.findByPessoa(cpf);
        if(pessoaExistente == null)
            throw new Exception("Não existe esse usuário!");
        pessoa.setStatus(pessoaExistente.getPessoa().getStatus());

        pessoa.setId_contato(pessoaExistente.getContato().getId());
        pessoa.setId_endereco(pessoaExistente.getEndereco().getId());

        pessoaDAO.updatePessoa(cpf, pessoa);
        ContatoModel contatoAtualizado = new ContatoModel();
        contatoAtualizado.setId(pessoa.getId_contato());
        contatoAtualizado.setTelefone(contato.getTelefone());
        contatoAtualizado.setEmail(contato.getEmail());

        contatoDAO.updateContato(contatoAtualizado);
        enderecoDAO.updateEndereco(pessoa.getId_endereco(),endereco);
    }

    public void deletePessoa(String cpf) throws Exception
    {
        if(!cpfValidator.isCpfValido(cpf))
            throw new Exception("CPF inválido!");

        PessoaCompletoDTO pessoaDelete = pessoaDAO.findByPessoa(cpf);

        if(pessoaDelete == null)
            throw new Exception("Não existe esse usuário!");

        if(!pessoaDAO.deleteFisicoByPessoa(cpf))
            throw new Exception("Erro ao deletar uma pessoa!");

        contatoDAO.deleteByContato(pessoaDelete.getContato().getId());
        enderecoDAO.deleteByEndereco(pessoaDelete.getEndereco().getId());
    }

    public void excluirLogicamente(String cpf) throws Exception
    {
        PessoaCompletoDTO pessoaDTO = pessoaDAO.findByPessoa(cpf);
        if(pessoaDTO == null)
            throw new Exception("Pessoa não encontrado!");

        PessoaModel pessoa = pessoaDTO.getPessoa();
        pessoa.setStatus(false);
        pessoaDAO.updatePessoa(cpf, pessoa);
    }

    public void reativarPessoa(String cpf) throws Exception
    {
        PessoaCompletoDTO pessoaDTO = pessoaDAO.findByPessoa(cpf);
        if(pessoaDTO == null)
            throw new Exception("Pessoa não encontrado!");

        PessoaModel pessoa = pessoaDTO.getPessoa();
        pessoa.setStatus(true);
        pessoaDAO.updatePessoa(cpf, pessoa);
    }

    public List<PessoaCompletoDTO> getAllPessoa()
    {
        return pessoaDAO.getAll();
    }

    @Override
    public void update(ProdutoModel produto)
    {
        List<PessoaCompletoDTO> pessoas = pessoaDAO.findAllByNotificarTrue();
        for(PessoaCompletoDTO pessoa : pessoas)
        {
            System.out.println("Produto: " + produto.getDescricao() +
                    " está em 0 unidades. Notificando: " + pessoa.getPessoa().getNome());
        }
    }
}