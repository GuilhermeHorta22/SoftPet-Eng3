package SoftPet.backend.service;

import SoftPet.backend.DAO.ProdutoDAO;

import SoftPet.backend.dto.ProdutoDTO;
import SoftPet.backend.model.ProdutoModel;
import SoftPet.backend.util.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoDAO produtoDAO;

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



}
