package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.clientes.ClienteDAO;
import br.edu.ifsc.fln.model.domain.veiculos.*;
import br.edu.ifsc.fln.model.domain.clientes.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável pela persistência de dados da entidade Veiculo.
 *
 *  @author  Carlos
 *  @version 1.2
 *  @since   23/11/2024
 */
public class VeiculoDAO {

    private final Connection connection;
    private final ModeloDAO modeloDAO;
    private final CorDAO corDAO;
    private final ClienteDAO clienteDAO;

    public VeiculoDAO(Connection connection) {
        this.connection = connection;
        this.modeloDAO = new ModeloDAO(connection);
        this.corDAO = new CorDAO(connection);
        this.clienteDAO = new ClienteDAO(connection);
    }

    public Veiculo inserir(Veiculo veiculo) {
        String sql = "INSERT INTO veiculo (placa, observacoes, id_cliente, id_cor, id_modelo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getObservacoes());
            stmt.setInt(3, veiculo.getCliente().getId());
            stmt.setObject(4, veiculo.getCor() != null ? veiculo.getCor().getId() : null, Types.INTEGER);
            stmt.setObject(5, veiculo.getModelo() != null ? veiculo.getModelo().getId() : null, Types.INTEGER);

            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        veiculo.setId(rs.getInt(1));
                        return veiculo;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, "Erro ao inserir Veículo.", ex);
        }
        return null;
    }

    public boolean alterar(Veiculo veiculo) {
        String sql = "UPDATE veiculo SET placa = ?, observacoes = ?, id_cliente = ?, id_cor = ?, id_modelo = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getObservacoes());
            stmt.setObject(3, veiculo.getCliente() != null ? veiculo.getCliente().getId() : null, Types.INTEGER);
            stmt.setObject(4, veiculo.getCor() != null ? veiculo.getCor().getId() : null, Types.INTEGER);
            stmt.setObject(5, veiculo.getModelo() != null ? veiculo.getModelo().getId() : null, Types.INTEGER);
            stmt.setInt(6, veiculo.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, "Erro ao alterar Veículo.", ex);
            return false;
        }
    }

    public boolean remover(Veiculo veiculo) {
        String sql = "DELETE FROM veiculo WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, veiculo.getId());
            veiculo.getCliente().removeVeiculos(veiculo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, "Erro ao remover Veículo.", ex);
        }
        return false;
    }

    public List<Veiculo> listar() {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM veiculo";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                veiculos.add(construirVeiculo(rs));
            }
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, "Erro ao listar Veículos.", ex);
        }
        return veiculos;
    }

    public Veiculo buscarPorId(int id) {
        String sql = "SELECT * FROM veiculo WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirVeiculo(rs);
                }
            }
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, "Erro ao buscar Veículo.", ex);
        }
        return null;
    }

    public Veiculo buscar(Veiculo veiculo) {
        String sql = "SELECT * FROM veiculo WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, veiculo.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirVeiculo(rs);
                }
            }
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(VeiculoDAO.class.getName()).log(Level.SEVERE, "Erro ao buscar Veículo.", ex);
        }
        return null;
    }

    private Veiculo construirVeiculo(ResultSet rs) throws SQLException, ExceptionLavacao {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(rs.getInt("id"));
        veiculo.setPlaca(rs.getString("placa"));
        veiculo.setObservacoes(rs.getString("observacoes"));

        int idModelo = rs.getInt("id_modelo");
        if (!rs.wasNull()) {
            veiculo.setModelo(modeloDAO.buscar(new Modelo(idModelo)));
        }

        int idCor = rs.getInt("id_cor");
        if (!rs.wasNull()) {
            veiculo.setCor(corDAO.buscar(new Cor(idCor)));
        }

        int idCliente = rs.getInt("id_cliente");
        if (!rs.wasNull()) {
            veiculo.setCliente(clienteDAO.buscarPorId(idCliente));
        }

        return veiculo;
    }
}
