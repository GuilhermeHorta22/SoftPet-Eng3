package SoftPet.backend.controll;

import SoftPet.backend.dto.AlertaDTO;
import SoftPet.backend.model.PessoaModel;
import SoftPet.backend.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import SoftPet.backend.dto.PessoaCompletoDTO;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pessoa")
public class PessoaControll
{

    @Autowired
    PessoaService pessoaService;

    @GetMapping("/listar")
    public ResponseEntity<Object> getAll()
    {
        List<PessoaCompletoDTO> listPessoa = pessoaService.getAllPessoa();
        if (!listPessoa.isEmpty())
            return ResponseEntity.ok(listPessoa);
        return ResponseEntity.badRequest().body("Erro ao listar todos os pessoas!");
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Object> getPessoaCpf(@PathVariable String cpf)
    {
        PessoaCompletoDTO pessoa = pessoaService.getPessoaCpf(cpf);
        if (pessoa != null)
            return ResponseEntity.ok(pessoa);
        return ResponseEntity.badRequest().body("Pessoa não encontrada!");
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Object> addPessoa(@RequestBody PessoaCompletoDTO pessoa)
    {
        try
        {
            PessoaModel novaPessoa = pessoaService.addPessoa(pessoa);
            return ResponseEntity.ok(novaPessoa);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao adicionar pessoa!");
        }
    }

    @PutMapping("/alterar")
    public ResponseEntity<Object> updatePessoa(@RequestBody PessoaCompletoDTO pessoa)
    {
        try
        {
            pessoaService.updatePessoa(pessoa.getPessoa().getCpf(), pessoa.getPessoa(), pessoa.getContato(), pessoa.getEndereco());
            return ResponseEntity.ok("Pessoa alterada com sucesso!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao atualizar pessoa: " + e.getMessage());
        }
    }

    @DeleteMapping("/logico/{cpf}")
    public ResponseEntity<Object> excluirLogico(@PathVariable String cpf)
    {
        try
        {
            pessoaService.excluirLogicamente(cpf);
            return ResponseEntity.ok("Pessoa desativado com sucesso!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao desativar Pessoa: " + e.getMessage());
        }
    }

    @PutMapping("/reativar/{cpf}")
    public ResponseEntity<Object> reativar(@PathVariable String cpf)
    {
        try
        {
            pessoaService.reativarPessoa(cpf);
            return ResponseEntity.ok("Pessoa reativado com sucesso!");
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao reativar Pessoa: " + e.getMessage());
        }
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Object> deletePessoa(@PathVariable String cpf)
    {
        try
        {
            pessoaService.deletePessoa(cpf);
            return ResponseEntity.ok("Pessoa deletado com sucesso!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao deletar pessoa!" + e.getMessage());
        }
    }

    @PostMapping("/alerta")
    public ResponseEntity<Object> enviarAlerta(@RequestBody AlertaDTO alerta)
    {
        try
        {
            // Aqui você poderia enviar e-mail, SMS ou notificação real
            System.out.println("🔔 ALERTA ENVIADO:");
            System.out.println("CPF: " + alerta.getCpf());
            System.out.println("Mensagem: " + alerta.getMensagem());

            return ResponseEntity.ok("Alerta enviado com sucesso!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar alerta.");
        }
    }
}