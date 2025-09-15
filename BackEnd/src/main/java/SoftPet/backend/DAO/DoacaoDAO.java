package SoftPet.backend.DAO;

import SoftPet.backend.config.SingletonDB;
import SoftPet.backend.dto.DoacaoDTO;
import SoftPet.backend.model.DoacaoModel;
import SoftPet.backend.model.PessoaModel;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DoacaoDAO
{
    public DoacaoDTO findByDoacao(Long id)
    {
        DoacaoDTO doacaoDTO = null;
        String sql = "SELECT d.doa_cod, d.doa_tipo, d.doa_qtde, d.doa_nome, d.doa_data, d.doa_validade, d.doa_unidade, d.pe_cod, " +
                "p.pe_cpf, p.pe_nome, p.pe_status, p.pe_profissao, p.con_cod, p.en_id, p.pe_rg, p.notificar " +
                "FROM doacoes d " +
                "INNER JOIN pessoa p ON d.pe_cod = p.pe_cod " +
                "WHERE d.doa_cod = ?";

        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
            {
                DoacaoModel doacao = new DoacaoModel(
                        rs.getLong("doa_cod"),
                        rs.getString("doa_tipo"),
                        rs.getInt("doa_qtde"),
                        rs.getString("doa_nome"),
                        rs.getDate("doa_data").toLocalDate(),
                        rs.getDate("doa_validade").toLocalDate(),
                        rs.getString("doa_unidade"),
                        rs.getLong("pe_cod")
                );
                PessoaModel doador = new PessoaModel(
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

                doacaoDTO = new DoacaoDTO(doacao,doador);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return doacaoDTO;
    }

    public DoacaoModel addDoacao(DoacaoModel doacao)
    {
        String sql = "INSERT INTO doacoes (doa_tipo, doa_qtde, doa_nome, doa_data, doa_validade, doa_unidade, pe_cod) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, doacao.getTipo());
            stmt.setInt(2, doacao.getQtde());
            stmt.setString(3, doacao.getNome());
            stmt.setDate(4, Date.valueOf(doacao.getData()));
            stmt.setDate(5, Date.valueOf(doacao.getDataValidade()));
            stmt.setString(6, doacao.getUniMedida());
            stmt.setLong(7, doacao.getId_doador());

            int linhasMod = stmt.executeUpdate();
            if(linhasMod > 0)
            {
                ResultSet rs = stmt.getGeneratedKeys();
                if(rs.next())
                    doacao.setId(rs.getLong(1));
            }

            // 2. Atualiza a quantidade do produto
            String sqlUpdate = "UPDATE produtos SET p_qntd_estoque = p_qntd_estoque + ? WHERE p_tipo = ?";
            try(PreparedStatement stmt2 = SingletonDB.getConexao().getPreparedStatement(sqlUpdate)) {
                stmt2.setInt(1, doacao.getQtde());
                stmt2.setString(2, doacao.getTipo());
                stmt2.executeUpdate();
            }
        }
        catch(SQLException e)
        {
            throw new RuntimeException("Erro ao adicionar doação: " + e.getMessage(), e);
        }
        return doacao;
    }

    public Boolean updateDoacao(DoacaoModel doacao)
    {
        String sql = "UPDATE doacoes SET doa_tipo = ?, doa_qtde = ?, doa_nome = ?, doa_data = ?, doa_validade = ?, doa_unidade = ?, pe_cod = ? WHERE doa_cod = ?";

        try(PreparedStatement stmt = SingletonDB.getConexao().getPreparedStatement(sql))
        {
            stmt.setString(1, doacao.getTipo());
            stmt.setInt(2, doacao.getQtde());
            stmt.setString(3, doacao.getNome());
            stmt.setDate(4, Date.valueOf(doacao.getData()));
            stmt.setDate(5, Date.valueOf(doacao.getDataValidade()));
            stmt.setString(6, doacao.getUniMedida());
            stmt.setLong(7, doacao.getId_doador());
            stmt.setLong(8, doacao.getId());

            return stmt.executeUpdate() > 0;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean deleteByDoacao(Long codigo)
    {
        try
        {
            // 1. Atualiza o estoque do produto com base no tipo da doação
            String sqlUpdate = "UPDATE produtos p " +
                    "SET p_qntd_estoque = p_qntd_estoque - (" +
                    "  SELECT d.doa_qtde FROM doacoes d WHERE d.doa_cod = ?" +
                    ") " +
                    "WHERE p.p_tipo = (" +
                    "  SELECT d.doa_tipo FROM doacoes d WHERE d.doa_cod = ?" +
                    ")";
            try(PreparedStatement stmtUpdate = SingletonDB.getConexao().getPreparedStatement(sqlUpdate))
            {
                stmtUpdate.setLong(1, codigo);
                stmtUpdate.setLong(2, codigo);
                stmtUpdate.executeUpdate();
            }

            // 2. Deleta a doação
            String sqlDelete = "DELETE FROM doacoes WHERE doa_cod = ?";
            try(PreparedStatement stmtDelete = SingletonDB.getConexao().getPreparedStatement(sqlDelete))
            {
                stmtDelete.setLong(1, codigo);
                return stmtDelete.executeUpdate() > 0;
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public List<DoacaoDTO> getAllDoacoes()
    {
        List<DoacaoDTO> list = new ArrayList<>();

        String sql = "SELECT d.doa_cod, d.doa_tipo, d.doa_qtde, d.doa_nome, d.doa_data, d.doa_validade, d.doa_unidade, d.pe_cod, " +
                "p.pe_cpf, p.pe_nome, p.pe_status, p.pe_profissao, p.con_cod, p.en_id, p.pe_rg, p.notificar " +
                "FROM doacoes d " +
                "JOIN pessoa p ON d.pe_cod = p.pe_cod";

        try(ResultSet rs = SingletonDB.getConexao().consultar(sql))
        {
            while(rs.next())
            {
                DoacaoModel doacao = new DoacaoModel(
                        rs.getLong("doa_cod"),
                        rs.getString("doa_tipo"),
                        rs.getInt("doa_qtde"),
                        rs.getString("doa_nome"),
                        rs.getDate("doa_data").toLocalDate(),
                        rs.getDate("doa_validade").toLocalDate(),
                        rs.getString("doa_unidade"),
                        rs.getLong("pe_cod")
                );

                PessoaModel doador = new PessoaModel(
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

                DoacaoDTO dto = new DoacaoDTO(doacao, doador);
                list.add(dto);
            }
        }
        catch(SQLException e)
        {
            System.err.println("Erro ao listar doações:");
            e.printStackTrace();
        }
        return list;
    }

}
