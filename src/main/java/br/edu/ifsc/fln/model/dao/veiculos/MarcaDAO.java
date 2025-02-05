package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.model.domain.veiculos.Marca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável por interagir com o banco de dados, realizando operações CRUD para a entidade Marca.
 * Possui um atributo de conexão que utiliza o padrão Factory, permitindo a troca fácil de implementações de banco de dados.
 *
 * @author  Carlos Hayden
 * @version 1.1
 * @since   16/11/2024
 */
public class MarcaDAO {

    private static final Logger LOGGER = Logger.getLogger(MarcaDAO.class.getName());

    private Connection connection;

    // Construtor padrão
    public MarcaDAO() {}
    // Construtor com injeção de conexão
    public MarcaDAO(Connection connection) {
        this.connection = connection;
    }

    // Getters e Setters
    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere uma nova marca no banco de dados.
     * @param marca Marca a ser inserida.
     * @return true se a operação for bem-sucedida, false caso contrário.
     */
    public boolean inserir(Marca marca) {
        String sql = "INSERT INTO marca (nome) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, marca.getNome());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir marca: " + marca.getNome(), ex);
            return false;
        }

    }

    /**
     * Lista todas as marca no banco de dados.
     * @return Lista de marca.
     */
    public List<Marca> listar() {
        String sql = "SELECT * FROM marca";
        List<Marca> retorno = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                Marca marca = new Marca();
                marca.setId(resultado.getInt("id"));
                marca.setNome(resultado.getString("nome"));
                retorno.add(marca);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao listar marca", ex);
        }
        return retorno;
    }

    /**
     * Busca uma marca pelo ID.
     * @param id ID da marca a ser buscada.
     * @return Objeto Marca se encontrado, null caso contrário.
     */
    public Marca buscar(int id) {
        String sql = "SELECT * FROM marca WHERE id=?";
        Marca marca = new Marca();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    marca.setId(resultado.getInt("id"));
                    marca.setNome(resultado.getString("nome"));
                    return marca;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar marca com ID: " + id, ex);
        }
        return marca;
    }

    public Marca buscarMarca(Marca marca) {
        String sql = "SELECT * FROM marca WHERE id=?";
        Marca retorno = new Marca();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, marca.getId());
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    retorno.setId(resultado.getInt("id"));
                    retorno.setNome(resultado.getString("nome"));
                    return retorno;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar marca: ", ex);
        }
        return retorno;
    }

    /**
     * Atualiza os dados de uma marca no banco de dados.
     * @param marca Marca com dados atualizados.
     * @return true se a operação for bem-sucedida, false caso contrário.
     */
    public boolean alterar(Marca marca) {
        String sql = "UPDATE marca SET nome=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, marca.getNome());
            stmt.setInt(2, marca.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar marca com ID: " + marca.getId(), ex);
        }
        return false;
    }

    /**
     * Remove uma marca do banco de dados.
     * @param id ID da marca a ser removida.
     * @return true se a operação for bem-sucedida, false caso contrário.
     */
    public boolean remover(int id) {
        String sql = "DELETE FROM marca WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao remover marca com ID: " + id, ex);
        }
        return false;
    }
}
