package br.edu.ifsc.fln.model.dao.clientes;

import br.edu.ifsc.fln.model.domain.Pontuacao;
import br.edu.ifsc.fln.model.domain.clientes.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PontuacaoDAO {
    private Connection connection;

    public PontuacaoDAO(Connection connection) {
        this.connection = connection;
    }
    // Getters e Setters
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO pontuacao_fidelidade (id_cliente, pontos) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cliente.getId());
            stmt.setInt(2, cliente.getPontuacao().getSaldo());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PontuacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean alterar(Cliente cliente) throws SQLException {
        String sql = "UPDATE pontuacao_fidelidade SET pontos = ? WHERE id_cliente = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cliente.getPontuacao().getSaldo());
            stmt.setInt(2, cliente.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PontuacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean remover(int idCliente) throws SQLException {
        String sql = "DELETE FROM pontuacao_fidelidade WHERE id_cliente = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(PontuacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public Pontuacao buscar(Cliente cliente) throws SQLException {
        String sql = "SELECT * FROM pontuacao_fidelidade WHERE id_cliente = ?";
        Pontuacao pontuacao = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cliente.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pontuacao = new Pontuacao(rs.getInt("id_cliente"), rs.getInt("pontos"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PontuacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pontuacao;
    }

    public Pontuacao buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM pontuacao_fidelidade WHERE id_cliente = ?";
        Pontuacao pontuacao = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                pontuacao = new Pontuacao(rs.getInt("id_cliente"), rs.getInt("pontos"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PontuacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pontuacao;
    }

    public List<Pontuacao> listar() throws SQLException {
        List<Pontuacao> pontuacoes = new ArrayList<>();
        String sql = "SELECT * FROM pontuacao_fidelidade";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                pontuacoes.add(new Pontuacao(rs.getInt("id_cliente"), rs.getInt("pontos")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PontuacaoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pontuacoes;
    }
}
