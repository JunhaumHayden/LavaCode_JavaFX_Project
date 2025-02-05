package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.model.domain.ECategoria;
import br.edu.ifsc.fln.model.domain.veiculos.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe para realizar operações CRUD com a entidade Modelo.
 * Gerencia a persistência e recuperação de objetos Modelo do banco de dados.
 * Utiliza o padrão Delegation para ser usar outras classes DAO, como MootorDAO e MarcaDAO.
 * Utiliza composição com MotorDAO para gerenciar a relação entre Modelo e Motor.
 *
 * @author  Carlos Hayden
 * @version 1.1
 * @since   23/11/2024
 */

public class ModeloDAO {

    private Connection connection;
    private MotorDAO motorDAO;
    private MarcaDAO marcaDAO;

    // Construtor padrão
    public ModeloDAO() {}

    // Construtor para injetar dependências
    public ModeloDAO(Connection connection) {
        this.connection = connection;
        this.motorDAO = new MotorDAO(connection);
        this.marcaDAO = new MarcaDAO(connection);
    }

    // Getters e Setters
    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
        this.motorDAO = new MotorDAO(connection);
        this.marcaDAO = new MarcaDAO(connection);
    }

    /**
     * Insere um novo modelo no banco de dados.
     * @param modelo Objeto Modelo a ser inserido.
     * @return O objeto Modelo com o ID gerado preenchido, ou null em caso de falha.
     */
    public boolean inserir(Modelo modelo) {
        String sqlModelo = "INSERT INTO modelo (descricao, id_marca, id_motor, categoria) VALUES (?, ?, ?, ?)";
        // Insere o motor
        try {
            // Inserir motor associado primeiro
            Motor motor = motorDAO.inserir(modelo.getMotor());
            modelo.getMotor().setId(motor.getId());
            // Insere o modelo
            try (PreparedStatement stmtModelo = connection.prepareStatement(sqlModelo, Statement.RETURN_GENERATED_KEYS)){
                stmtModelo.setString(1, modelo.getDescricao());
                stmtModelo.setInt(2, modelo.getMarca().getId());
                stmtModelo.setInt(3, modelo.getMotor().getId());
                stmtModelo.setString(4, modelo.getCategoria().name());
                if (stmtModelo.executeUpdate() > 0) {
                    try (ResultSet rs = stmtModelo.getGeneratedKeys()) {
                        if (rs.next()) {
                            modelo.setId(rs.getInt(1)); // Define o ID gerado no objeto Modelo
                        }
                    }
                }
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    /**
     * Lista todos os modelo no banco de dados.
     * @return Uma lista de objetos Modelo.
     */
    public List<Modelo> listar() {
        String sql = "SELECT * FROM modelo";
        List<Modelo> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                retorno.add(construirModelo(resultado, new Modelo()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
    /**
     * Busca um motor no banco de dados pelo ID.
     * @param modelo Um objeto Modelo a ser removido.
     * @return O objeto Motor encontrado, ou null se não existir.
     */
    public Modelo buscar(Modelo modelo) {
        String sql = "SELECT * FROM modelo WHERE id = ?";
        Modelo retorno = new Modelo(); // Inicializa um objeto vazio
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, modelo.getId());
            try (ResultSet resultado = stmt.executeQuery()){
                if (resultado.next()) {
                    retorno = construirModelo(resultado, retorno); // Popula o objeto se houver resultado
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno; // Retorna o objeto vazio ou o populado
    }
    /**
     * Atualiza um modelo no banco de dados.
     * @param modelo O objeto Modelo com os dados atualizados.
     * @return True se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean alterar(Modelo modelo) {
        String sql = "UPDATE modelo SET descricao = ?, id_marca = ?, id_motor = ?, categoria = ? WHERE id = ?";
        try {
            // Atualizar motor associado primeiro
            motorDAO.alterar(modelo.getMotor());
            // Atualiza o modelo
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, modelo.getDescricao());
            stmt.setInt(2, modelo.getMarca().getId());
            stmt.setInt(3, modelo.getMotor().getId());
            stmt.setString(4, modelo.getCategoria().name());
            stmt.setInt(5, modelo.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    /**
     * Remove um modelo do banco de dados.
     * Tabela Modelo no banco de dados setado com "ON DELETE CASCADE"
     * @param modelo O objeto Modelo a ser removido.
     * @return True se a operação foi bem-sucedida, false caso contrário.
     */
    public boolean remover(Modelo modelo) {
        String sql = "DELETE FROM modelo WHERE id=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, modelo.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ModeloDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Metodo auxiliar para construcao de um Objeto Modelo apartir de um retorno do Banco de dados.
     * @param resultado Um objeto do tipo Resultset com os dados para a construçao do objeto.
     * @return Um objeto do tipo Modelo
     */
    private Modelo construirModelo(ResultSet resultado, Modelo modelo) throws SQLException {
        modelo.setId(resultado.getInt("id"));
        modelo.setDescricao(resultado.getString("descricao"));
        modelo.setCategoria(ECategoria.valueOf(resultado.getString("categoria")));
        // Buscar marca associado
        Marca marca = marcaDAO.buscar(resultado.getInt("id_marca"));
        modelo.setMarca(marca);
        // Buscar motor associado
        Motor motor = motorDAO.buscar(resultado.getInt("id_motor"));
        modelo.getMotor().setId(motor.getId());
        modelo.getMotor().setPotencia(motor.getPotencia());
        modelo.getMotor().setTipoCombustivel(ETipoCombustivel.valueOf(motor.getTipoCombustivel().name()));

        return modelo;
    }

}
