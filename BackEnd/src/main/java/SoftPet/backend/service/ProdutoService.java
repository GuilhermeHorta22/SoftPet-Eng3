package SoftPet.backend.service;

import SoftPet.backend.DAO.ProdutoDAO;

import SoftPet.backend.dto.DoacaoDTO;
import SoftPet.backend.dto.PessoaCompletoDTO;
import SoftPet.backend.dto.ProdutoDTO;
import SoftPet.backend.model.DoacaoModel;
import SoftPet.backend.model.PessoaModel;
import SoftPet.backend.model.ProdutoModel;
import SoftPet.backend.observer.Subject;
import SoftPet.backend.util.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService implements Subject
{

    @Autowired
    private ProdutoDAO produtoDAO;

    //vou usar para observar a quantidade do produto
    private PessoaService observer;

    //vou usar para ter o controle de quem eu vou mandar o email quando o estoque chegar em 0
    private List<PessoaCompletoDTO> listaNotify = new ArrayList<>();

    public ProdutoModel addProduto(ProdutoDTO produtoDTO) throws Exception {
        ProdutoModel produto = produtoDTO.getProduto();

        if (!Validation.validarTextoProduto(produto.getTipo()))
            throw new IllegalArgumentException("Tipo do produto inválido!");

        if (!Validation.validarTextoProduto(produto.getDescricao()))
            throw new IllegalArgumentException("Descrição do produto inválida!");

        if (!Validation.validarUnidadeMedida(produto.getUnidadeMedida()))
            throw new IllegalArgumentException("Unidade de medida inválida!");

        if (!Validation.validarQuantidadeEstoque(produto.getQuantidadeEstoque()))
            throw new IllegalArgumentException("Quantidade em estoque inválida!");

        if (!Validation.validarDataValidade(produto.getDataValidade()))
            throw new IllegalArgumentException("Data de validade inválida!");

        return produtoDAO.addProduto(produto);
    }

    public ProdutoDTO getProduto(Long id) {
        return produtoDAO.findByProduto(id);
    }

    public void updateProduto(Long id, ProdutoModel produto) throws Exception {
        if (id == null || id <= 0)
            throw new IllegalArgumentException("ID do produto inválido!");

        if (!Validation.validarTextoProduto(produto.getTipo()))
            throw new IllegalArgumentException("Tipo do produto inválido!");

        if (!Validation.validarTextoProduto(produto.getDescricao()))
            throw new IllegalArgumentException("Descrição do produto inválida!");

        if (!Validation.validarUnidadeMedida(produto.getUnidadeMedida()))
            throw new IllegalArgumentException("Unidade de medida inválida!");

        if (!Validation.validarQuantidadeEstoque(produto.getQuantidadeEstoque()))
            throw new IllegalArgumentException("Quantidade em estoque inválida!");

        if (!Validation.validarDataValidade(produto.getDataValidade()))
            throw new IllegalArgumentException("Data de validade inválida!");

        ProdutoDTO produtoExistente = produtoDAO.findByProduto(id);
        if (produtoExistente == null)
            throw new Exception("Produto com esse ID não foi encontrado!");

        produto.setId(id);
        produtoDAO.updateProduto(produto);
    }

    public void deleteProduto(Long id) throws Exception {
        if (id == null || id <= 0)
            throw new IllegalArgumentException("ID do produto inválido!");

        ProdutoDTO produtoDelete = produtoDAO.findByProduto(id);

        if (produtoDelete == null)
            throw new Exception("Produto com esse ID não existe!");

        if (!produtoDAO.deleteByProduto(id))
            throw new Exception("Erro ao deletar o produto!");
    }

    public List<ProdutoDTO> getAllProdutos() {
        return produtoDAO.getAllProdutos();
    }
    public List<ProdutoDTO> buscarProdutosPorTipo(String tipo) {
        return produtoDAO.getProdutosPorTipo(tipo);
    }

    @Override
    public void adicionarObserver(PessoaCompletoDTO pessoa)
    {
        if(pessoa.getPessoa().getNotificar() == true && !listaNotify.contains(pessoa))
            listaNotify.add(pessoa);
    }

    @Override
    public void removerObserver(PessoaCompletoDTO pessoa)
    {
        if(listaNotify.contains(pessoa))
            listaNotify.remove(pessoa);
    }

    @Override
    public void notificarObserver(ProdutoModel produto)
    {
        observer = new PessoaService();
        for(PessoaCompletoDTO pessoa : listaNotify)
            observer.update(produto, pessoa);
    }

    public void consumirProduto(Long id, int qtdeConsumida) throws Exception
    {
        if(id == null || id <= 0)
            throw new IllegalArgumentException("ID do produto inválido!");

        if(!Validation.numNegativo(qtdeConsumida))
            throw new IllegalArgumentException("Quantidade do produto inválida!");

        ProdutoDTO produtoExistente = produtoDAO.findByProduto(id);
        if(produtoExistente == null)
            throw new Exception("Não existe uma doação com esse ID!");

        int estoqueAtual = produtoExistente.getProduto().getQuantidadeEstoque();

        if(qtdeConsumida > estoqueAtual)
            throw new IllegalArgumentException("Quantidade a consumir maior que o estoque atual!");

        int novoEstoque = estoqueAtual - qtdeConsumida;
        ProdutoModel produtoAtualizado = produtoExistente.getProduto();
        produtoAtualizado.setQuantidadeEstoque(novoEstoque);

        produtoDAO.updateProduto(produtoAtualizado);

        //chamada do notificar do observer
        if(novoEstoque == 0)
            notificarObserver(produtoAtualizado);
    }
}
