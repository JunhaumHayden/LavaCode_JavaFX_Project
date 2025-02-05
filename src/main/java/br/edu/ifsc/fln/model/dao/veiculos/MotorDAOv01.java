package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.model.domain.veiculos.ETipoCombustivel;
import br.edu.ifsc.fln.model.domain.veiculos.Motor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe gerenciará as operações de acesso ao banco de dados, como inserção, leitura, atualização e exclusão.
 * @Attribute um objeto do tipo Connection que usa o padrão Factory para obter a conexão com o banco de dados permitindo que a implementação de diferentes bancos de dados (como MySQL, PostgreSQL, etc.) seja alterada facilmente, sem modificar o código do DAO.
 * @author  Carlos Hayden
 * @version 1.0
 * @since   16/11/2024
 *
 */

public class MotorDAOv01 {

    private Connection connection;
    // Getters e Setters
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    // Inserir novo motor
    public Motor inserir(Motor motor) {
        String sql = "INSERT INTO motor(potencia, tipo_combustivel) VALUES(?,?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, motor.getPotencia());
            stmt.setString(2, motor.getTipoCombustivel().name());
          //stmt.execute(); // Metodo ignora o número de linhas afetadas e sempre retorna true após a execução do comando, mesmo que ele não tenha feito alterações no banco.
          //return true;   //Isso pode ser problemático em situações onde você precisa validar se o comando foi realmente eficaz (por exemplo, se um DELETE encontrou linhas para excluir).
            if (stmt.executeUpdate() > 0) { //Este metodo retorna um número inteiro que representa a quantidade de linhas afetadas pela execução do comando SQL. verifica se o número de linhas afetadas é maior que zero.
                //Se o comando afetar pelo menos uma linha, o metodo retorna true, indicando que a operação foi bem-sucedida.
                try (ResultSet rs = stmt.getGeneratedKeys()) { //retorna um objeto ResultSet contendo as chaves primárias geradas durante a última execução de um INSERT
                    if (rs.next()) {
                        motor.setId(rs.getInt(1)); // Define o ID gerado no objeto Motor
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAOv01.class.getName()).log(Level.SEVERE, null, ex);
        }
        return motor;
    }
    // Buscar motor
    public Motor buscar(Motor motor) {
        String sql = "SELECT * FROM motor WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, motor.getId());
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                motor.setPotencia(resultado.getInt("potencia"));
                motor.setTipoCombustivel(ETipoCombustivel.valueOf(resultado.getString("tipo_combustivel")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAOv01.class.getName()).log(Level.SEVERE, null, ex);
        }
        return motor;
    }
    // Buscar todos os motores
    public List<Motor> listar() {
        String sql = "SELECT * FROM motor";
        List<Motor> motores = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Motor motor = new Motor();
                motor.setId(resultado.getInt("id"));
                motor.setPotencia(resultado.getInt("potencia"));
                motor.setTipoCombustivel(ETipoCombustivel.valueOf(resultado.getString("tipo_combustivel")));
                motores.add(motor);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAOv01.class.getName()).log(Level.SEVERE, null, ex);
        }
        return motores;
    }
    // Atualizar motor
    public boolean alterar(Motor motor) {
        String sql = "UPDATE motor SET potencia=?, tipo_combustivel=? WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, motor.getPotencia());
            stmt.setString(2, motor.getTipoCombustivel().name());
            stmt.setInt(3, motor.getId());
//            stmt.execute(); // Usar stmt.execute() seguido de return true ignora o número de linhas afetadas e sempre retorna true após a execução do comando, mesmo que ele não tenha feito alterações no banco.
//Isso pode ser problemático em situações onde você precisa validar se o comando foi realmente eficaz (por exemplo, se um DELETE encontrou linhas para excluir).
//            return true;
            return stmt.executeUpdate() > 0; //Este metodo retorna um número inteiro que representa a quantidade de linhas afetadas pela execução do comando SQL. verifica se o número de linhas afetadas é maior que zero.
            //Se o comando afetar pelo menos uma linha, o metodo retorna true, indicando que a operação foi bem-sucedida.
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAOv01.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    // Excluir motor
    public boolean remover(Motor motor) {
        String sql = "DELETE FROM motor WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, motor.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(MotorDAOv01.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
