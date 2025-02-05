package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.model.domain.veiculos.ETipoCombustivel;
import br.edu.ifsc.fln.model.domain.veiculos.Motor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe DAO para gerenciar as operações no banco de dados relacionadas à entidade Motor.
 * Utiliza o padrão Delegation para ser usada por outras classes DAO, como ModeloDAO.
 * @Attribute connection é um objeto do tipo Connection que usa o padrão Factory para obter a conexão com o banco de dados permitindo que a implementação de diferentes bancos de dados (como MySQL, PostgreSQL, etc.) seja alterada facilmente, sem modificar o código do DAO.
 *
 * @author  Carlos
 * @version 1.2
 * @since   23/11/2024
 */
public class MotorDAO {

    private Connection connection;

    // Construtor padrão
    public MotorDAO() {}

    // Construtor com injeção de conexão
    public MotorDAO(Connection connection) {
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
     * Insere um novo motor no banco de dados.
     * @param motor Objeto Motor a ser inserido.
     * @return O objeto Motor com o ID gerado preenchido, ou null em caso de falha.
     */
    public Motor inserir(Motor motor) {
        String sql = "INSERT INTO motor(potencia, tipo_combustivel) VALUES(?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, motor.getPotencia());
            stmt.setString(2, motor.getTipoCombustivel().name());

            if (stmt.executeUpdate() > 0) { //Este metodo retorna um número inteiro que representa a quantidade de linhas afetadas pela execução do comando SQL. verifica se o número de linhas afetadas é maior que zero.//Se o comando afetar pelo menos uma linha, o metodo retorna true, indicando que a operação foi bem-sucedida.
                try (ResultSet rs = stmt.getGeneratedKeys()) { //retorna um objeto ResultSet contendo as chaves primárias geradas durante a última execução de um INSERT
                    if (rs.next()) {
                        motor.setId(rs.getInt(1));
                        return motor; // Retorna o motor com o ID atualizado
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAO.class.getName()).log(Level.SEVERE, "Erro ao inserir Motor.", ex);
        }
        return null; // Retorna null em caso de falha
    }

    /**
     * Busca um motor no banco de dados pelo ID.
     * @param id O ID do motor.
     * @return O objeto Motor encontrado, ou null se não existir.
     */
    public Motor buscar(int id) {
        String sql = "SELECT * FROM motor WHERE id=?";
        Motor motor = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    motor = new Motor();
                    motor.setId(rs.getInt("id"));
                    motor.setPotencia(rs.getInt("potencia"));
                    motor.setTipoCombustivel(ETipoCombustivel.valueOf(rs.getString("tipo_combustivel")));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAO.class.getName()).log(Level.SEVERE, "Erro ao buscar Motor.", ex);
        }
        return motor;
    }

    /**
     * Lista todos os motores no banco de dados.
     * @return Uma lista de objetos Motor.
     */
    public List<Motor> listar() {
        String sql = "SELECT * FROM motor";
        List<Motor> motores = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Motor motor = new Motor();
                motor.setId(rs.getInt("id"));
                motor.setPotencia(rs.getInt("potencia"));
                motor.setTipoCombustivel(ETipoCombustivel.valueOf(rs.getString("tipo_combustivel")));
                motores.add(motor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAO.class.getName()).log(Level.SEVERE, "Erro ao listar Motores.", ex);
        }
        return motores;
    }

    /**
     * Atualiza um motor no banco de dados.
     * @param motor O objeto Motor com os dados atualizados.
     * @return True se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean alterar(Motor motor) {
        String sql = "UPDATE motor SET potencia=?, tipo_combustivel=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, motor.getPotencia());
            stmt.setString(2, motor.getTipoCombustivel().name());
            stmt.setInt(3, motor.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAO.class.getName()).log(Level.SEVERE, "Erro ao alterar Motor.", ex);
        }
        return false;
    }

    /**
     * Remove um motor do banco de dados pelo ID.
     * @param id O ID do motor a ser removido.
     * @return True se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean remover(int id) {
        String sql = "DELETE FROM motor WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAO.class.getName()).log(Level.SEVERE, "Erro ao remover Motor.", ex);
        }
        return false;
    }
}
