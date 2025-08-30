package SoftPet.backend.service;

import SoftPet.backend.DAO.EmpresaDAO;
import SoftPet.backend.dto.EmpresaDTO;
import SoftPet.backend.dto.EmpresaProfileResponseDTO;
import SoftPet.backend.model.EmpresaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaDAO empresaDAO;

    public List<EmpresaDTO> listarEmpresas() {
        return empresaDAO.getAllEmpresas();
    }

    public EmpresaDTO buscarPorId(Long id) {
        return empresaDAO.findByEmpresa(id);
    }

    public void cadastrarEmpresa(EmpresaDTO dto) {
        EmpresaModel empresa = new EmpresaModel(
                dto.getNome(),
                dto.getRazaoSocial(),
                dto.getCnpj(),
                dto.getLogoPequena(),
                dto.getEndereco(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getUf(),
                dto.getDiretor(),
                dto.getSite(),
                dto.getTelefone()
        );
        empresaDAO.addEmpresa(empresa);
    }

    public void atualizarEmpresa(Long id, EmpresaDTO dto) {
        EmpresaModel empresa = new EmpresaModel(
                id,
                dto.getNome(),
                dto.getRazaoSocial(),
                dto.getCnpj(),
                dto.getLogoPequena(),
                dto.getEndereco(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getUf(),
                dto.getDiretor(),
                dto.getSite(),
                dto.getTelefone()
        );
        empresaDAO.updateEmpresa(empresa);
    }

    public class InvalidCnpjException extends RuntimeException{
        public InvalidCnpjException(String message) {
            super(message);
        }
    }

    public static class EmpresaNotFoundException extends RuntimeException{

        public EmpresaNotFoundException(String message) {
            super(message);
        }
    }

    public EmpresaDTO atualizarEmpresaPorCnpj(EmpresaDTO dto) {
        if (!EmpresaModel.isCNPJ(dto.getCnpj())) {
            throw new InvalidCnpjException("CNPJ inválido");
        }

        List<EmpresaDTO> empresas = empresaDAO.getAllEmpresas();
        EmpresaDTO existente = empresas.stream()
                .filter(e -> e.getCnpj().equals(dto.getCnpj()))
                .findFirst()
                .orElseThrow(() -> new EmpresaNotFoundException("Empresa não existe"));

        EmpresaModel empresaAtualizada = new EmpresaModel(
                existente.getId(),  // mantém o ID da empresa existente
                dto.getNome(),
                dto.getRazaoSocial(),
                dto.getCnpj(),
                dto.getLogoPequena(),
                dto.getEndereco(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getUf(),
                dto.getDiretor(),
                dto.getSite(),
                dto.getTelefone()
        );

        boolean sucesso = empresaDAO.updateEmpresa(empresaAtualizada);
        if (!sucesso) {
            throw new RuntimeException("Falha ao atualizar a empresa no banco de dados.");
        }

        return new EmpresaDTO(empresaAtualizada);
    }




    public void excluirEmpresa(Long id) {
        empresaDAO.deleteByEmpresa(id);
    }

    public EmpresaProfileResponseDTO getFirstEmpresa() {
        List<EmpresaDTO> empresas = empresaDAO.getAllEmpresas();

        if (empresas.isEmpty()) {
            throw new RuntimeException("Não existe empresa.");
        }

        EmpresaDTO dto = empresas.get(0); // ou pegar a última, depende da lógica que deseja

        return new EmpresaProfileResponseDTO(
                dto.getId(),
                dto.getNome(),
                dto.getRazaoSocial(),
                dto.getCnpj(),
                dto.getLogoPequena(),
                dto.getEndereco(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getUf(),
                dto.getDiretor(),
                dto.getSite(),
                dto.getTelefone()
        );
    }

    public boolean existeEmpresa() {
        return empresaDAO.getAllEmpresas().size() > 0;
    }

    public EmpresaDTO saveEmpresa(EmpresaDTO dto) {
        if (this.existeEmpresa()) {
            throw new RuntimeException("Parametrização já foi feita!");
        }

        if (!EmpresaModel.isCNPJ(dto.getCnpj())) {
            throw new RuntimeException("CNPJ inválido");
        }

        List<EmpresaDTO> existentes = empresaDAO.getAllEmpresas()
                .stream()
                .filter(e -> e.getCnpj().equals(dto.getCnpj()))
                .toList();

        if (!existentes.isEmpty()) {
            throw new RuntimeException("Empresa já existe com o CNPJ: " + EmpresaModel.imprimeCNPJ(dto.getCnpj()));
        }

        EmpresaModel empresa = new EmpresaModel(
                dto.getNome(),
                dto.getRazaoSocial(),
                dto.getCnpj(),
                dto.getLogoPequena(),
                dto.getEndereco(),
                dto.getBairro(),
                dto.getCidade(),
                dto.getUf(),
                dto.getDiretor(),
                dto.getSite(),
                dto.getTelefone()
        );

        empresaDAO.addEmpresa(empresa);

        return empresaDAO.getAllEmpresas().getLast(); // ou outra forma de pegar a recém-criada
    }
    public boolean shutdownEmpresa(String cnpj) {
        List<EmpresaDTO> empresas = empresaDAO.getAllEmpresas();
        for (EmpresaDTO empresa : empresas) {
            if (empresa.getCnpj().equals(cnpj)) {
                try {
                    empresaDAO.deleteByEmpresa(empresa.getId());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        throw new RuntimeException("Empresa não existe com o CNPJ: " + cnpj);
    }


}
