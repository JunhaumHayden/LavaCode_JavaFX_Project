package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.model.domain.veiculos.Cor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável por interagir com o banco de dados, ele vai realizar as operações de inserção, consulta, atualização e remoção (CRUD) dos dados no banco de dados.
 * Possui um atributo que é um objeto do tipo Connection que usa o padrão Factory para obter a conexão com o banco de dados permitindo que a implementação de diferentes bancos de dados (como MySQL, PostgreSQL, etc.) seja alterada facilmente, sem modificar o código do DAO.
 *
 * @author  Carlos Hayden
 * @version 1.0
 * @since   21/11/2024
 *
 */
public class CorDAO {

    private Connection connection;

    // Construtor padrão
    public CorDAO() {}
    // Construtor com injeção de conexão
    public CorDAO(Connection connection) {
        this.connection = connection;
    }

    // Getters e Setters
    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    // Crud - Metodo para inserir uma nova cor no banco de dados
    public boolean inserir(Cor cor) {
        String sql = "INSERT INTO cor (nome) VALUES(?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cor.getNome());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(CorDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // cRud - Metodo para buscar todas as cors no banco de dados
    public List<Cor> listar() {
        String sql = "SELECT * FROM cor";
        List<Cor> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Cor cor = new Cor();
                cor.setId(resultado.getInt("id"));
                cor.setNome(resultado.getString("nome"));
                retorno.add(cor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
    // Metodo para buscar cor
    public Cor buscar(Cor cor) {
        String sql = "SELECT * FROM cor WHERE id=?";
        Cor retorno = new Cor();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, cor.getId());
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                cor.setNome(resultado.getString("nome"));
                retorno = cor;
            }
        } catch (SQLException ex) {
            Logger.getLogger(CorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
    // crUd- Metodo para alterar dados de uma cor no banco de dados
    public boolean alterar(Cor cor) {
        String sql = "UPDATE cor SET nome=? WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cor.getNome());
            stmt.setInt(2, cor.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(CorDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // cruD - Metodo para excluir uma cor
    public boolean remover(Cor cor) {
        String sql = "DELETE FROM cor WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, cor.getId());
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(CorDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
