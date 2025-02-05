package br.edu.ifsc.fln.model.dao.ordenservicos;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.domain.ordemServicos.ItemOs;
import br.edu.ifsc.fln.model.domain.ordemServicos.OrdemDeServico;
import br.edu.ifsc.fln.model.domain.ordemServicos.Servico;
import br.edu.ifsc.fln.service.OrdemDeServicoService;
import br.edu.ifsc.fln.utils.AlertDialog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemOsDAO {
    private Connection connection;

    public ItemOsDAO(Connection connection) {
        this.connection = connection;
    }

    public ItemOs inserir(ItemOs itemOs) throws SQLException {
        String sql = "INSERT INTO item_os (observacao, valor_servico, id_servico, id_ordemServico) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, itemOs.getObservacao());
            stmt.setBigDecimal(2, itemOs.getServico().getValor());
            stmt.setInt(3, itemOs.getServico().getId());
            stmt.setInt(4, itemOs.getOrdemDeServico().getId());
            if (stmt.executeUpdate() > 0) { //verifica se o número de linhas afetadas é maior que zero.
                try (ResultSet rs = stmt.getGeneratedKeys()) { //retorna um objeto ResultSet contendo as chaves primárias geradas durante a última execução de um INSERT
                    if (rs.next()) {
                        itemOs.setId(rs.getInt(1));
                        itemOs.setValorServico(itemOs.getServico().getValor());
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ItemOsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return itemOs;
    }

    public boolean alterar(ItemOs itemOs) throws SQLException {
        String sql = "UPDATE item_os SET observacao = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, itemOs.getObservacao());
            stmt.setInt(2, itemOs.getId());
            return (stmt.executeUpdate() > 0);
        }catch (SQLException ex) {
            Logger.getLogger(ItemOsDAO.class.getName()).log(Level.SEVERE, "Erro ao alterar o item de OS.", ex);
            AlertDialog.exceptionMessage(ex);
        }
        return false;
    }

    public boolean remover(ItemOs itemOs) throws SQLException {
        String sql = "DELETE FROM item_os WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemOs.getId());
            if (!OrdemDeServicoService.podeAlterar(itemOs.getOrdemDeServico())) {
                throw new ExceptionLavacao("OS Fechada não pode ser alterada.");
            }
            return (stmt.executeUpdate() > 0);
        }catch (SQLException ex) {
            Logger.getLogger(ItemOsDAO.class.getName()).log(Level.SEVERE, "Erro ao alterar o item de OS.", ex);
            AlertDialog.exceptionMessage(ex);
        } catch (ExceptionLavacao e) {
            AlertDialog.exceptionMessage(e);
            throw new RuntimeException(e);
        }
        return false;
    }

    public List<ItemOs> listar() throws SQLException {
        List<ItemOs> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_os";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ItemOs item = new ItemOs();
                item = populateItemOs(rs, connection, item);
                itens.add(item);
            }
        }catch (SQLException ex) {
            Logger.getLogger(ItemOsDAO.class.getName()).log(Level.SEVERE, null, ex);
            AlertDialog.exceptionMessage(ex);
        }
        return itens;
    }

    public ItemOs buscar(ItemOs itemOs) throws SQLException {
        String sql = "SELECT * FROM item_os WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemOs.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    itemOs = populateItemOs(rs, connection, itemOs);
                }
            }
        }catch (SQLException ex) {
            Logger.getLogger(ItemOsDAO.class.getName()).log(Level.SEVERE, null, ex);
            AlertDialog.exceptionMessage(ex);
        }
        return itemOs;
    }

    public ItemOs buscarPorId(int id) throws SQLException {
        ItemOs itemOs = null;
        String sql = "SELECT * FROM item_os WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    itemOs = new ItemOs();
                    itemOs = populateItemOs(rs, connection, itemOs);
                }
            }
        }catch (SQLException ex) {
            Logger.getLogger(ItemOsDAO.class.getName()).log(Level.SEVERE, null, ex);
            AlertDialog.exceptionMessage(ex);
        }
        return itemOs;
    }

    public List<ItemOs> listarPorOs(OrdemDeServico os) throws SQLException {
        List<ItemOs> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_os WHERE id_ordemServico = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, os.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemOs item = new ItemOs();
                    item = populateItemOs(rs, connection, item);
                    item.setOrdemDeServico(os);
                    itens.add(item);
                }
            }
        }
        return itens;
    }

    public static ItemOs populateItemOs(ResultSet rs, Connection connection, ItemOs item) throws SQLException {
        Servico servico = new Servico();
        OrdemDeServico os = new OrdemDeServico();

        item.setId(rs.getInt("id"));
        item.setObservacao(rs.getString("observacao"));
        item.setValorServico(rs.getBigDecimal("valor_servico"));
        servico.setId(rs.getInt("id_servico"));
        os.setId(rs.getInt("id_ordemServico"));

        // Obtendo os dados completos do Serviço associado ao ItemOs
        ServicoDAO servicoDAO = new ServicoDAO(connection);
        servico = servicoDAO.buscar(servico);
        item.setServico(servico);
        item.setOrdemDeServico(os);

        return item;
    }
}
