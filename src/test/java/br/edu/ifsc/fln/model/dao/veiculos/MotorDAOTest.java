package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.model.domain.veiculos.ETipoCombustivel;
import br.edu.ifsc.fln.model.domain.veiculos.Motor;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Para compartilhar o setup entre os testes
class MotorDAOTest {

    private Connection connection;
    private MotorDAO motorDAO;

    @BeforeAll
    void setUpClass() throws SQLException {
        // Conexão com o banco de dados para testes (configure conforme necessário
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "java", "java");
        motorDAO = new MotorDAO(connection);

        // Criação da tabela para testes
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS motor (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "potencia INT NOT NULL, "+
                "tipo_combustivel ENUM('GASOLINA', 'ETANOL', 'FLEX', 'DIESEL', 'GNV', 'OUTRO') NOT NULL)");
    }

    @AfterAll
    void tearDownClass() throws SQLException {
        connection.createStatement().executeUpdate("DELETE FROM motor");
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            fail("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
        }
    }

    @Test
    @Order(1)
    void testInserirMotor() {
        Motor motor = new Motor();
        motor.setPotencia(150);
        motor.setTipoCombustivel(ETipoCombustivel.FLEX);

        Motor motorInserido = motorDAO.inserir(motor);

        assertNotNull(motorInserido);
        assertNotNull(motorInserido.getId());
        assertEquals(150, motorInserido.getPotencia());
        assertEquals(ETipoCombustivel.FLEX, motorInserido.getTipoCombustivel());
    }

    @Test
    @Order(2)
    void testBuscarMotor() {
        Motor motor = motorDAO.buscar(1); // Supondo que o ID do motor inserido anteriormente seja 1
        assertNotNull(motor);
        assertEquals(150, motor.getPotencia());
        assertEquals(ETipoCombustivel.FLEX, motor.getTipoCombustivel());
    }

    @Test
    @Order(3)
    void testListarMotores() {
        List<Motor> motores = motorDAO.listar();
        assertNotNull(motores);
        assertFalse(motores.isEmpty());
    }

    @Test
    @Order(4)
    void testAlterarMotor() {
        Motor motor = motorDAO.buscar(1);
        assertNotNull(motor);

        motor.setPotencia(200);
        motor.setTipoCombustivel(ETipoCombustivel.GASOLINA);

        boolean atualizado = motorDAO.alterar(motor);
        assertTrue(atualizado);

        Motor motorAtualizado = motorDAO.buscar(1);
        assertNotNull(motorAtualizado);
        assertEquals(200, motorAtualizado.getPotencia());
        assertEquals(ETipoCombustivel.GASOLINA, motorAtualizado.getTipoCombustivel());
    }

    @Test
    @Order(5)
    void testRemoverMotor() {
        Motor motor = motorDAO.buscar(1);
        assertNotNull(motor);

        boolean removido = motorDAO.remover(motor.getId());
        assertTrue(removido);

        Motor motorRemovido = motorDAO.buscar(1);
        assertNull(motorRemovido);
    }
}
