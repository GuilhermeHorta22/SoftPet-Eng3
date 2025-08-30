package SoftPet.backend.service;

import SoftPet.backend.DAO.CargoDAO;
import SoftPet.backend.model.CargoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoService {

    @Autowired
    private CargoDAO cargoDAO;

    public CargoModel findById(Long id) {
        return cargoDAO.buscarPorId(id);
    }

    public CargoModel findByNome(String nome) {
        return cargoDAO.buscarPorNome(nome);
    }

    public List<CargoModel> getAll() {
        return cargoDAO.getAll();
    }

    public CargoModel create(CargoModel cargo) {
        Long id = cargoDAO.criar(cargo).getId();
        if (id != -1) {
            cargo.setId(id);
            return cargo;
        }
        return null;
    }

    public boolean update(CargoModel cargo) {
        return cargoDAO.update(cargo);
    }

    public boolean delete(Long id) {
        return cargoDAO.delete(id);
    }

    /**
     * Busca um cargo pelo nome e cria se não existir.
     * Retorna o objeto cargo com id preenchido.
     */
    public CargoModel buscarOuCriar(CargoModel cargo) {
        Long id = cargoDAO.buscarOuCriar(cargo);
        cargo.setId(id);
        return cargo;
    }
}
