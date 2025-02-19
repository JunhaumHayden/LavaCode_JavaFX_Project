package br.edu.ifsc.fln.model.dao.ordenservicos;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.clientes.PontuacaoDAO;
import br.edu.ifsc.fln.model.dao.veiculos.VeiculoDAO;
import br.edu.ifsc.fln.model.domain.ordemServicos.EStatus;
import br.edu.ifsc.fln.model.domain.ordemServicos.ItemDeOrdemDeServico;
import br.edu.ifsc.fln.model.domain.ordemServicos.OrdemDeServico;
import br.edu.ifsc.fln.model.domain.veiculos.Veiculo;
import br.edu.ifsc.fln.service.OrdemDeServicoService;
import br.edu.ifsc.fln.utils.AlertDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrdemDeServicoDAO {
    private Connection connection;

    public OrdemDeServicoDAO(Connection connection) {
        this.connection = connection;
    }

    public OrdemDeServico inserir(OrdemDeServico os) throws SQLException {
        String sql = "INSERT INTO ordem_servico (numero, total, desconto, agenda, id_veiculo, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());
            stmt.setString(1, os.getNumero());
            stmt.setBigDecimal(2, os.getTotal());
            stmt.setBigDecimal(3, os.getDesconto());
            stmt.setDate(4, Date.valueOf(os.getAgenda()));
            stmt.setInt(5, os.getVeiculo().getId());
            stmt.setString(6, EStatus.ABERTA.name());
            if (stmt.executeUpdate() > 0) { //verifica se o número de linhas afetadas é maior que zero.
                try (ResultSet rs = stmt.getGeneratedKeys()) { //retorna um objeto ResultSet contendo as chaves primárias geradas durante a última execução de um INSERT
                    if (rs.next()) {
                        os.setId(rs.getInt(1)); // Define o ID gerado
                    }
                }
            }
            ItemDeOrdemDeServicoDAO itemDeOrdemDeServicoDAO = new ItemDeOrdemDeServicoDAO(connection);
            for (ItemDeOrdemDeServico item : os.getItens()) {
                item.setOrdemDeServico(os);
                ItemDeOrdemDeServico resultado = itemDeOrdemDeServicoDAO.inserir(item);
                if (resultado != null) {
                    item.setId(resultado.getId());
                    item.setValorServico(resultado.getValorServico());
                }
            }
            connection.commit();
            return os;
        } catch (SQLException ex) {
            connection.rollback();
            Logger.getLogger(OrdemDeServicoDAO.class.getName()).log(Level.SEVERE, "Erro ao inserir a Ordem de Serviço.", ex);
            AlertDialog.exceptionMessage(ex);
            return null;
        } finally { //garante que setAutoCommit(true) seja sempre chamado, independentemente de a transação ter sido bem-sucedida ou não
            connection.setAutoCommit(true);
        }
    }


    public boolean alterar(OrdemDeServico os) throws SQLException {
    String sql = "UPDATE ordem_servico SET total=?, desconto=?, agenda=?, status=? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        connection.setAutoCommit(false);
        stmt.setBigDecimal(1, os.getTotal());
        stmt.setBigDecimal(2, os.getDesconto());
        stmt.setDate(3, Date.valueOf(os.getAgenda()));
        stmt.setString(4, os.getStatus().name());
        stmt.setInt(5, os.getId());

        ItemDeOrdemDeServicoDAO itemDeOrdemDeServicoDAO = new ItemDeOrdemDeServicoDAO(connection);
        for (ItemDeOrdemDeServico item : os.getItens()) {
            if (item.getId() > 0) {
                itemDeOrdemDeServicoDAO.alterar(item);
            } else {
                itemDeOrdemDeServicoDAO.inserir(item);
            }
        }
        if (os.getStatus() == EStatus.FECHADA){
            PontuacaoDAO pontuacaoDAO = new PontuacaoDAO(connection);
            pontuacaoDAO.alterar(os.getVeiculo().getCliente());
        }
        stmt.executeUpdate();
        connection.commit();
        return true;
    } catch (SQLException ex) {
        connection.rollback();
        Logger.getLogger(OrdemDeServicoDAO.class.getName()).log(Level.SEVERE, "Erro ao alterar a Ordem de Serviço.", ex);
        AlertDialog.exceptionMessage(ex);
        return false;
    } finally {
        connection.setAutoCommit(true);
    }
}

    public boolean remover(OrdemDeServico os) throws SQLException {
        String sql = "DELETE FROM ordem_servico WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, os.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(OrdemDeServicoDAO.class.getName()).log(Level.SEVERE, "Erro ao remover a Ordem de Serviço.", ex);
        }
        return false;
    }

    public List<OrdemDeServico> listar() throws SQLException {
        List<OrdemDeServico> ordens = new ArrayList<>();
        String sql = "SELECT * FROM ordem_servico";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                OrdemDeServico os = new OrdemDeServico();
                os = populateOrdemDeServico(rs, os);
                ordens.add(os);
            }
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(OrdemDeServicoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ordens;
    }
    public List<OrdemDeServico> listarOrdensAbertas() throws SQLException {
        List<OrdemDeServico> ordens = new ArrayList<>();
        String sql = "SELECT *" +
                     "FROM ordem_servico os " +
                     "WHERE os.status = 'ABERTA' " +
                     "ORDER BY os.agenda ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                OrdemDeServico os = new OrdemDeServico();
                os = populateOrdemDeServico(rs, os);
//                os.setNumero(rs.getString("numero"));
//                os.setAgenda(rs.getDate("agenda").toLocalDate());
//                os.setStatus(EStatus.valueOf(rs.getString("status")));
//                os.setTotal(rs.getBigDecimal("total"));
//                Veiculo veiculo = new Veiculo();
//                veiculo.setPlaca(rs.getString("placa"));
//                os.setVeiculo(veiculo);
                ordens.add(os);
            }
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(OrdemDeServicoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ordens;
    }

    public OrdemDeServico buscar(OrdemDeServico os) throws SQLException {
        String sql = "SELECT * FROM ordem_servico WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, os.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    os = populateOrdemDeServico(rs, os);
                }
            } catch (ExceptionLavacao e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrdemDeServicoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return os;
    }

    public OrdemDeServico buscarPorId(int id) throws SQLException {
        OrdemDeServico os = null;
        String sql = "SELECT * FROM ordem_servico WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    os = new OrdemDeServico();
                    os = populateOrdemDeServico(rs, os);
                }
            }
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(OrdemDeServicoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return os;
    }

    private OrdemDeServico populateOrdemDeServico(ResultSet rs, OrdemDeServico os) throws SQLException, ExceptionLavacao {
        Veiculo veiculo = new Veiculo();
        os.setId(rs.getInt("id"));
        os.setNumero(rs.getString("numero"));
        os.setDesconto(rs.getBigDecimal("desconto"));
        os.setAgenda(rs.getDate("agenda").toLocalDate());
        veiculo.setId(rs.getInt("id_veiculo"));
        VeiculoDAO veiculoDAO = new VeiculoDAO(connection);
        veiculo = veiculoDAO.buscar(veiculo);
        os.setVeiculo(veiculo);
        // Obtendo os itens da Ordem de Serviço
        ItemDeOrdemDeServicoDAO itemDeOrdemDeServicoDAO = new ItemDeOrdemDeServicoDAO(connection);
        os.setItens(itemDeOrdemDeServicoDAO.listarPorOs(os));
        os.setStatus(EStatus.valueOf(rs.getString("status")));
        return os;
    }
}
