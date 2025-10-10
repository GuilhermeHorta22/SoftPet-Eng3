package SoftPet.backend.service;

import SoftPet.backend.DAO.ProdutoDAO;
import SoftPet.backend.dto.ProdutoDTO;
import SoftPet.backend.model.ProdutoModel;
import SoftPet.backend.observer.Observer;
import SoftPet.backend.observer.Subject;
import SoftPet.backend.service.template.AtualizaEstoqueProduto;
import SoftPet.backend.util.Validation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdutoService implements Subject {

    private final ProdutoDAO produtoDAO;
    private final AtualizaEstoqueProduto estoqueProduto;

    // vou usar para ter o controle de quem eu vou mandar o email quando o estoque chegar em 0
    private final List<Observer> observers = new ArrayList<>();

    public ProdutoService(ProdutoDAO produtoDAO, AtualizaEstoqueProduto estoqueProduto) {
        this.produtoDAO = produtoDAO;
        this.estoqueProduto = estoqueProduto;
    }

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

    // --------- Observer ---------

    @Override
    public void adicionarObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removerObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notificarObserver(ProdutoModel produto) {
        for (Observer o : observers) o.update(produto);
    }

    // --------- Movimentação de estoque (Template Method) ---------

    public void consumirProduto(Long id, int qtde) {
        if (id == null || id <= 0) throw new IllegalArgumentException("ID do produto inválido!");
        if (!Validation.numNegativo(qtde)) throw new IllegalArgumentException("Quantidade do produto inválida!");

        // usa o açúcar do Template Method concreto
        estoqueProduto.saida(id, Math.abs(qtde));

        // após a operação, rele o produto e notifica se zerou
        var dto = produtoDAO.findByProduto(id);
        if (dto != null && dto.getProduto() != null && dto.getProduto().getQuantidadeEstoque() == 0) {
            notificarObserver(dto.getProduto());
        }
    }

    public void reporProduto(Long id, int qtde) {
        if (id == null || id <= 0) throw new IllegalArgumentException("ID do produto inválido!");
        if (!Validation.numNegativo(qtde)) throw new IllegalArgumentException("Quantidade do produto inválida!");

        estoqueProduto.entrada(id, Math.abs(qtde));
        // aqui não há notificação de “zerado”, pois estamos repondo
    }
}
