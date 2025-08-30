package SoftPet.backend.controll;

import SoftPet.backend.DAO.CargoDAO;
import SoftPet.backend.model.CargoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/cargos")
public class CargoControl {

    @Autowired
    private CargoDAO cargoDAO;

    // Listar todos os cargos
    @GetMapping
    public List<CargoModel> listarTodos() {
        return cargoDAO.getAll();
    }

    // Criar um novo cargo (se não existir, caso contrário retorna o existente)
    @PostMapping
    public ResponseEntity<CargoModel> criarOuRetornarCargo(@RequestBody CargoModel cargo) {
        CargoModel existente = cargoDAO.buscarPorNome(cargo.getNome());
        if (existente != null) {
            // Já existe - retorna com status 200 OK
            return ResponseEntity.ok(existente);
        }

        // Não existe - cria um novo cargo usando o método criar
        Long idGerado = cargoDAO.criar(cargo).getId();
        cargo.setId(idGerado);
        return ResponseEntity.status(201).body(cargo);
    }

    // Buscar cargo por ID
    @GetMapping("/{id}")
    public ResponseEntity<CargoModel> buscarPorId(@PathVariable Long id) {
        CargoModel cargo = cargoDAO.buscarPorId(id);
        if (cargo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cargo);
    }

    // Atualizar cargo
    @PutMapping("/{id}")
    public ResponseEntity<CargoModel> atualizar(@PathVariable Long id, @RequestBody CargoModel cargo) {
        cargo.setId(id);
        boolean atualizado = cargoDAO.update(cargo);
        if (!atualizado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cargo);
    }

    // Deletar cargo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean deletado = cargoDAO.delete(id);
        if (!deletado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
