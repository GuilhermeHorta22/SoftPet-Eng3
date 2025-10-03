package SoftPet.backend.DAO;

import SoftPet.backend.config.SingletonDB;
import SoftPet.backend.dto.PessoaCompletoDTO;
import SoftPet.backend.model.ContatoModel;
import SoftPet.backend.model.PessoaModel;
import SoftPet.backend.model.EnderecoModel;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PessoaDAO
{
    public static PessoaCompletoDTO findByPessoa(String cpf)
    {
        PessoaCompletoDTO pessoaDTO = null;
        String sql = "SELECT p.pe_cod, p.pe_cpf, p.pe_nome, p.pe_status, p.pe_profissao, p.con_cod, p.en_id, p.pe_rg, p.notificar," +
                "c.con_telefone, c.con_email, " +
                "e.en_cep, e.en_rua, e.en_numero, e.en_bairro, e.en_cidade, e.en_uf, e.en_complemento " +
                "FROM pessoa p " +
                "JOIN contato c ON p.con_cod = c.con_cod " +
                "JOIN endereco e ON p.en_id = e.en_id " +
                "WHERE p.pe_cpf = ?";


        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                PessoaModel pessoa = new PessoaModel(
                        rs.getLong("pe_cod"),
                        rs.getString("pe_cpf"),
                        rs.getString("pe_nome"),
                        rs.getBoolean("pe_status"),
                        rs.getString("pe_profissao"),
                        rs.getLong("con_cod"),
                        rs.getLong("en_id"),
                        rs.getString("pe_rg"),
                        rs.getBoolean("notificar")
                );

                //relacionando os objetos completos
                ContatoModel contato = new ContatoModel(
                        rs.getLong("con_cod"),
                        rs.getString("con_telefone"),
                        rs.getString("con_email")
                );
                EnderecoModel endereco = new EnderecoModel(
                        rs.getLong("en_id"),
                        rs.getString("en_cep"),
                        rs.getString("en_rua"),
                        rs.getInt("en_numero"),
                        rs.getString("en_bairro"),
                        rs.getString("en_cidade"),
                        rs.getString("en_uf"),
                        rs.getString("en_complemento")
                );

                pessoaDTO = new PessoaCompletoDTO(pessoa, contato, endereco);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return pessoaDTO;
    }

    public PessoaCompletoDTO findById(long id)
    {
        PessoaCompletoDTO pessoaDTO = null;
        String sql = "SELECT p.pe_cod, p.pe_cpf, p.pe_nome, p.pe_status, p.pe_profissao, p.con_cod, p.en_id, p.pe_rg, p.notificar," +
                "c.con_telefone, c.con_email, " +
                "e.en_cep, e.en_rua, e.en_numero, e.en_bairro, e.en_cidade, e.en_uf, e.en_complemento " +
                "FROM pessoa p " +
                "JOIN contato c ON p.con_cod = c.con_cod " +
                "JOIN endereco e ON p.en_id = e.en_id " +
                "WHERE p.pe_cod = ?";

        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                PessoaModel pessoa = new PessoaModel(
                        rs.getLong("pe_cod"),
                        rs.getString("pe_cpf"),
                        rs.getString("pe_nome"),
                        rs.getBoolean("pe_status"),
                        rs.getString("pe_profissao"),
                        rs.getLong("con_cod"),
                        rs.getLong("en_id"),
                        rs.getString("pe_rg"),
                        rs.getBoolean("notificar")
                );

                ContatoModel contato = new ContatoModel(
                        rs.getLong("con_cod"),
                        rs.getString("con_telefone"),
                        rs.getString("con_email")
                );

                EnderecoModel endereco = new EnderecoModel(
                        rs.getLong("en_id"),
                        rs.getString("en_cep"),
                        rs.getString("en_rua"),
                        rs.getInt("en_numero"),
                        rs.getString("en_bairro"),
                        rs.getString("en_cidade"),
                        rs.getString("en_uf"),
                        rs.getString("en_complemento")
                );

                pessoaDTO = new PessoaCompletoDTO(pessoa, contato, endereco);
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Erro ao buscar pessoa por ID: " + e.getMessage(), e);
        }
        return pessoaDTO;
    }

    public List<PessoaCompletoDTO> findAllByNotificarTrue()
    {
        List<PessoaCompletoDTO> pessoas = new ArrayList<>();
        String sql = "SELECT p.pe_cod, p.pe_cpf, p.pe_nome, p.pe_status, p.pe_profissao, p.con_cod, p.en_id, p.pe_rg, p.notificar," +
                "c.con_telefone, c.con_email, " +
                "e.en_cep, e.en_rua, e.en_numero, e.en_bairro, e.en_cidade, e.en_uf, e.en_complemento " +
                "FROM pessoa p " +
                "JOIN contato c ON p.con_cod = c.con_cod " +
                "JOIN endereco e ON p.en_id = e.en_id " +
                "WHERE p.notificar = TRUE";

        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PessoaModel pessoa = new PessoaModel(
                        rs.getLong("pe_cod"),
                        rs.getString("pe_cpf"),
                        rs.getString("pe_nome"),
                        rs.getBoolean("pe_status"),
                        rs.getString("pe_profissao"),
                        rs.getLong("con_cod"),
                        rs.getLong("en_id"),
                        rs.getString("pe_rg"),
                        rs.getBoolean("notificar")
                );

                ContatoModel contato = new ContatoModel(
                        rs.getLong("con_cod"),
                        rs.getString("con_telefone"),
                        rs.getString("con_email")
                );

                EnderecoModel endereco = new EnderecoModel(
                        rs.getLong("en_id"),
                        rs.getString("en_cep"),
                        rs.getString("en_rua"),
                        rs.getInt("en_numero"),
                        rs.getString("en_bairro"),
                        rs.getString("en_cidade"),
                        rs.getString("en_uf"),
                        rs.getString("en_complemento")
                );

                pessoas.add(new PessoaCompletoDTO(pessoa, contato, endereco));
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Erro ao buscar pessoas com notificar = true: " + e.getMessage(), e);
        }

        return pessoas;
    }

    public PessoaModel addPessoa(PessoaModel pessoa) {
        String sql = "INSERT INTO pessoa (pe_cpf, pe_nome, pe_profissao, con_cod, en_id, pe_rg, pe_status, notificar) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, pessoa.getCpf());
            stmt.setString(2, pessoa.getNome());
            stmt.setString(3, pessoa.getProfissao());

            if(pessoa.getId_contato() != null)
                stmt.setLong(4, pessoa.getId_contato());
            else
                stmt.setNull(4, java.sql.Types.INTEGER);

            if(pessoa.getId_endereco() != null)
                stmt.setLong(5, pessoa.getId_endereco());
            else
                stmt.setNull(5, java.sql.Types.INTEGER);

            stmt.setString(6, pessoa.getRg());

            // Aqui está o novo campo: pe_status = true
            stmt.setBoolean(7, true);

            stmt.setBoolean(8,pessoa.getNotificar());

            int linhasMod = stmt.executeUpdate();
            if(linhasMod > 0)
            {
                ResultSet rs = stmt.getGeneratedKeys();
                if(rs.next())
                    pessoa.setId(rs.getLong(1));
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Erro ao adicionar pessoa: " + e.getMessage(), e);
        }
        return pessoa;
    }

    public Boolean updatePessoa(String cpf, PessoaModel pessoa)
    {
        if(!cpf.equals(pessoa.getCpf()))
            throw new IllegalArgumentException("O CPF não pode ser alterado.");

        String sql = "UPDATE pessoa SET pe_nome = ?, pe_status = ?, pe_profissao = ?, con_cod = ?, en_id = ?, pe_rg = ?, notificar = ? WHERE pe_cpf = ?";
        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setString(1, pessoa.getNome());
            stmt.setBoolean(2, pessoa.getStatus());
            stmt.setString(3, pessoa.getProfissao());
            if(pessoa.getId_contato() != null)
                stmt.setLong(4, pessoa.getId_contato());
            else
                stmt.setNull(4, java.sql.Types.BIGINT);
            if(pessoa.getId_endereco() != null)
                stmt.setLong(5, pessoa.getId_endereco());
            else
                stmt.setNull(5, java.sql.Types.BIGINT);
            stmt.setString(6, pessoa.getRg());
            stmt.setBoolean(7,pessoa.getNotificar());
            stmt.setString(8, cpf);

            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean deleteFisicoByPessoa(String cpf)
    {
        String sql = "DELETE FROM pessoa WHERE pe_cpf = ?";
        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setString(1, cpf);
            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean deleteLogicoByPessoa(String cpf) {
        String sql = "UPDATE pessoa SET pe_status = false WHERE pe_cpf = ?";
        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setString(1, cpf);
            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean reativarPessoa(String cpf) {
        String sql = "UPDATE pessoa SET pe_status = true WHERE pe_cpf = ?";
        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setString(1, cpf);
            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public List<PessoaCompletoDTO> getAll()
    {
        List<PessoaCompletoDTO> list = new ArrayList<>();

        String sql = "SELECT p.pe_cod, p.pe_cpf, p.pe_nome, p.pe_status, p.pe_profissao, p.con_cod, p.en_id, p.pe_rg, p.notificar," +
                "c.con_cod, c.con_telefone, c.con_email, " +
                "e.en_id, e.en_cep, e.en_rua, e.en_numero, e.en_bairro, e.en_cidade, e.en_uf, e.en_complemento " +
                "FROM pessoa p " +
                "JOIN contato c ON p.con_cod = c.con_cod " +
                "JOIN endereco e ON p.en_id = e.en_id " +
                "WHERE p.pe_status = true"; // <-- filtro aplicado aqui

        try(ResultSet rs = SingletonDB.getConexao().consultar(sql))
        {
            while(rs.next())
            {
                PessoaModel pessoa = new PessoaModel(
                        rs.getLong("pe_cod"),
                        rs.getString("pe_cpf"),
                        rs.getString("pe_nome"),
                        rs.getBoolean("pe_status"),
                        rs.getString("pe_profissao"),
                        rs.getLong("con_cod"),
                        rs.getLong("en_id"),
                        rs.getString("pe_rg"),
                        rs.getBoolean("notificar")
                );

                ContatoModel contato = new ContatoModel(
                        rs.getLong("con_cod"),
                        rs.getString("con_telefone"),
                        rs.getString("con_email")
                );
                EnderecoModel endereco = new EnderecoModel(
                        rs.getLong("en_id"),
                        rs.getString("en_cep"),
                        rs.getString("en_rua"),
                        rs.getInt("en_numero"),
                        rs.getString("en_bairro"),
                        rs.getString("en_cidade"),
                        rs.getString("en_uf"),
                        rs.getString("en_complemento")
                );

                PessoaCompletoDTO pessoaDTO = new PessoaCompletoDTO(pessoa, contato, endereco);
                list.add(pessoaDTO);
            }
        }
        catch(SQLException e)
        {
            System.err.println("Erro ao listar pessoa:");
            e.printStackTrace();
        }
        return list;
    }
}