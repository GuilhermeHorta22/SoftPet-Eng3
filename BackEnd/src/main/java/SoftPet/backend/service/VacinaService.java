package SoftPet.backend.service;

import SoftPet.backend.DAO.AnimalDAO;
import SoftPet.backend.DAO.AnimalXVacinacaoDAO;
import SoftPet.backend.DAO.VacinaDAO;
import SoftPet.backend.DAO.VoluntarioDAO;
import SoftPet.backend.dto.VacinaRequestDTO;
import SoftPet.backend.dto.VacinacaoRequestDTO;
import SoftPet.backend.model.AnimalModel;
import SoftPet.backend.model.AnimalXVacinacaoModel;
import SoftPet.backend.model.VacinaModel;
import SoftPet.backend.model.VoluntarioModel;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class VacinaService {

    private final VacinaDAO vacinaDAO;
    private final AnimalDAO animalDAL;
    private final VoluntarioDAO voluntarioDAO;
    private final AnimalXVacinacaoDAO animalXVacinacaoDAO;
    private final AtualizaEstoqueVacina estoqueVacina;

    public VacinaService(
            VacinaDAO vacinaDAO,
            AnimalDAO animalDAL,
            VoluntarioDAO voluntarioDAO,
            AnimalXVacinacaoDAO animalXVacinacaoDAO,
            AtualizaEstoqueVacina estoqueVacina
    ) {
        this.vacinaDAO = vacinaDAO;
        this.animalDAL = animalDAL;
        this.voluntarioDAO = voluntarioDAO;
        this.animalXVacinacaoDAO = animalXVacinacaoDAO;
        this.estoqueVacina = estoqueVacina;
    }

    public VacinaModel adicionarVacina(VacinaRequestDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty())
            throw new IllegalArgumentException("O nome da vacina é obrigatório.");
        if (dto.getDataReferenciaLote() == null)
            throw new IllegalArgumentException("A data de referência do lote é obrigatória.");
        if (dto.getTipoDosePadrao() == null || dto.getTipoDosePadrao().trim().isEmpty())
            throw new IllegalArgumentException("O tipo/dose padrão da vacina é obrigatório.");

        VacinaModel nova = new VacinaModel();
        nova.setNome(dto.getNome());
        nova.setDescricao(dto.getDescricao());
        nova.setDataReferenciaLote(dto.getDataReferenciaLote());
        nova.setTipoDosePadrao(dto.getTipoDosePadrao());

        return vacinaDAO.criar(nova);
    }

    public VacinaModel buscarVacinaPorId(Long id) {
        if (id == null) throw new IllegalArgumentException("ID não pode ser nulo.");
        VacinaModel v = vacinaDAO.buscarPorCod(id);
        if (v == null) throw new RuntimeException("Vacina com ID " + id + " não encontrada.");
        return v;
    }

    public List<VacinaModel> listarTodasVacinas() {
        return vacinaDAO.listarTodas();
    }

    public VacinaModel atualizarVacina(Long id, VacinaRequestDTO dto) {
        if (id == null) throw new IllegalArgumentException("O ID da vacina para atualização não pode ser nulo.");
        VacinaModel v = buscarVacinaPorId(id);

        if (dto.getNome() == null || dto.getNome().trim().isEmpty())
            throw new IllegalArgumentException("O nome da vacina é obrigatório para atualização.");
        if (dto.getDataReferenciaLote() == null)
            throw new IllegalArgumentException("A data de referência do lote é obrigatória para atualização.");
        if (dto.getTipoDosePadrao() == null || dto.getTipoDosePadrao().trim().isEmpty())
            throw new IllegalArgumentException("O tipo/dose padrão é obrigatório para atualização.");

        v.setNome(dto.getNome());
        v.setDescricao(dto.getDescricao());
        v.setDataReferenciaLote(dto.getDataReferenciaLote());
        v.setTipoDosePadrao(dto.getTipoDosePadrao());


        return vacinaDAO.atualizar(v);
    }

    public void deletarVacina(Long id) {
        if (id == null) throw new IllegalArgumentException("O ID da vacina para deleção não pode ser nulo.");
        buscarVacinaPorId(id);
        boolean ok = vacinaDAO.deletar(id);
        if (!ok) {
            System.err.println("Vacina " + id + " não foi deletada (verificar dependências).");
        }
    }

    public AnimalXVacinacaoModel efetuarVacinacao(VacinacaoRequestDTO req) throws Exception {
        if (req.getVoluntarioRegistrandoCod() == null)
            throw new IllegalArgumentException("Código do voluntário registrando não pode ser nulo.");
        if (req.getAnimalCod() == null)
            throw new IllegalArgumentException("Código do animal não pode ser nulo.");
        if (req.getVacinaCod() == null)
            throw new IllegalArgumentException("Código da vacina não pode ser nulo.");
        if (req.getVoluntarioAplicadorCod() == null)
            throw new IllegalArgumentException("Código do voluntário aplicador não pode ser nulo.");
        if (req.getDataAplicacaoAnimal() == null)
            throw new Exception("A data de aplicação da vacina no animal é obrigatória.");

        VoluntarioModel registrando = voluntarioDAO.findById(req.getVoluntarioRegistrandoCod());
        if (registrando == null)
            throw new Exception("Voluntário (registrando) " + req.getVoluntarioRegistrandoCod() + " não encontrado.");

        AnimalModel animal = animalDAL.buscarPorCod(req.getAnimalCod());
        if (animal == null)
            throw new Exception("Animal " + req.getAnimalCod() + " não encontrado.");

        VacinaModel vacina = buscarVacinaPorId(req.getVacinaCod());

        VoluntarioModel aplicador = voluntarioDAO.findById(req.getVoluntarioAplicadorCod());
        if (aplicador == null)
            throw new Exception("Voluntário (aplicador) " + req.getVoluntarioAplicadorCod() + " não encontrado.");

        AnimalXVacinacaoModel aplicacao = new AnimalXVacinacaoModel();
        aplicacao.setAnimalCod(animal.getCod());
        aplicacao.setVacinaCod(vacina.getCod());
        aplicacao.setDoseNumerica(req.getDoseNumericaAplicada());
        aplicacao.setDataAplicacao(req.getDataAplicacaoAnimal());
        aplicacao.setVoluntarioAplicadorCod(aplicador.getId());
        aplicacao.setObservacao(construirObservacaoRelatorioParaAplicacao(animal, vacina, aplicador, registrando, req));

        return animalXVacinacaoDAO.registrarAplicacaoVacina(aplicacao);
    }

    private String construirObservacaoRelatorioParaAplicacao(
            AnimalModel animal, VacinaModel vacina,
            VoluntarioModel aplicador, VoluntarioModel registrador,
            VacinacaoRequestDTO req
    ) {
        StringBuilder sb = new StringBuilder();

        if (req.getObservacaoDaAplicacao() != null && !req.getObservacaoDaAplicacao().trim().isEmpty()) {
            sb.append("Observação da Aplicação: ").append(req.getObservacaoDaAplicacao()).append(". ");
        }

        String dataFmt = new SimpleDateFormat("yyyy-MM-dd").format(req.getDataAplicacaoAnimal());

        sb.append(String.format(
                "Relatório: Vacina '%s' (Cod: %s) aplicada no animal '%s' (Cod: %s) em %s. ",
                vacina.getNome(), String.valueOf(vacina.getCod()),
                animal.getNome(), String.valueOf(animal.getCod()),
                dataFmt
        ));

        if (req.getDoseNumericaAplicada() != null) {
            sb.append("Dose Aplicada: ").append(req.getDoseNumericaAplicada()).append(". ");
        }

        sb.append(String.format(
                "Tipo Dose Padrão da Vacina: '%s'. Aplicada por Vol/Vet: '%s' (Cod: %s). Registrado por Vol: '%s' (Cod: %s).",
                vacina.getTipoDosePadrao(),
                aplicador.getNome(), String.valueOf(aplicador.getId()),
                registrador.getNome(), String.valueOf(registrador.getId())
        ));

        return sb.toString();
    }

    public void consumirVacina(Long id, int qtde) {
        if (id == null || qtde <= 0) throw new IllegalArgumentException("ID e quantidade devem ser válidos.");
        estoqueVacina.saida(id, qtde);   // subtrai
    }

    public void reporVacina(Long id, int qtde) {
        if (id == null || qtde <= 0) throw new IllegalArgumentException("ID e quantidade devem ser válidos.");
        estoqueVacina.entrada(id, qtde); // adiciona
    }
}
