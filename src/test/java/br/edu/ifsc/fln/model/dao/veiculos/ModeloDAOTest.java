package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.model.domain.ECategoria;
import br.edu.ifsc.fln.model.domain.veiculos.Marca;
import br.edu.ifsc.fln.model.domain.veiculos.Modelo;
import br.edu.ifsc.fln.model.domain.veiculos.ETipoCombustivel;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para VeiculoDAO.
 *
 * @author
 * @version 1.0
 * @since 27/11/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModeloDAOTest {

    private Connection connection;
    private ModeloDAO modeloDAO;
    private MarcaDAO marcaDAO;

    @BeforeAll
    void setup() throws SQLException {
        // Conexão com o banco de dados de teste
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "java", "java");
        connection.setAutoCommit(false);

        // Criação das tabelas para testes
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS marca (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "nome VARCHAR(50) NOT NULL UNIQUE" +
                    ")ENGINE=InnoDB");
            stmt.execute("CREATE TABLE IF NOT EXISTS motor (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "potencia INT NOT NULL," +
                    "tipo_combustivel ENUM('GASOLINA', 'ETANOL', 'FLEX', 'DIESEL', 'GNV', 'ELETRICO', 'OUTRO') NOT NULL" +
                    ")ENGINE=InnoDB");
            stmt.execute("CREATE TABLE IF NOT EXISTS modelo (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "descricao VARCHAR(50) NOT NULL UNIQUE," +
                    "id_marca INT NOT NULL," +
                    "id_motor INT NOT NULL," +
                    "categoria ENUM('PEQUENO', 'MEDIO', 'GRANDE', 'MOTO', 'PADRAO') NOT NULL," +
                    "FOREIGN KEY (id_marca) REFERENCES marcas(id)," +
                    "FOREIGN KEY (id_motor) REFERENCES motor(id) ON DELETE CASCADE" +
                    ")ENGINE=InnoDB");
            stmt.execute("INSERT INTO marca (nome) VALUES ('Toyota')");
            stmt.execute("INSERT INTO marca (nome) VALUES ('Hyundai')");
        }

        modeloDAO = new ModeloDAO(connection);
        marcaDAO = new MarcaDAO(connection);
    }


//    @BeforeEach
//    void cleanUpTable() throws SQLException {
////        connection.createStatement().executeUpdate("DELETE FROM marcas");
////        connection.createStatement().executeUpdate("DELETE FROM modelos");
////        connection.createStatement().executeUpdate("DELETE FROM motor");
//        connection.createStatement().executeUpdate("INSERT INTO marcas (nome) VALUES ('Toyota')");
//    }

    @AfterAll
    void tearDownClass() throws SQLException {
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
    void testInserirModelo() {
        Marca marca = marcaDAO.buscar(1); // Marca previamente inserida

        Modelo modelo = new Modelo();
        modelo.setDescricao("Corolla test");
        modelo.setMarca(marca);
        modelo.getMotor().setPotencia(100);
        modelo.getMotor().setTipoCombustivel(ETipoCombustivel.GASOLINA);
        modelo.setCategoria(ECategoria.MEDIO);

        boolean resultado = modeloDAO.inserir(modelo);

        assertTrue(resultado, "A inserção do modelo deveria retornar true.");
        assertNotNull(modelo.getId(), "O ID do modelo deveria ser gerado após a inserção.");
    }

    @Test
    @Order(2)
    void testListarModelos() {
        List<Modelo> modelos = modeloDAO.listar();
//        assertEquals(0, modelos.size(), "Deve iniciar com a tabela vazia.");
//
//        testInserirModelo(); // Insere um modelo

        modelos = modeloDAO.listar();
        assertEquals(1, modelos.size(), "Deveria haver um modelo na tabela.");
        assertEquals("Corolla", modelos.get(0).getDescricao(), "O nome do modelo deveria ser 'Corolla'.");
    }

    @Test
    @Order(3)
    void testBuscarModelo() {
//        testInserirModelo(); // Insere um modelo

        Modelo modelo = new Modelo();
        modelo.setId(1); // ID do modelo inserido

        Modelo encontrado = modeloDAO.buscar(modelo);
        assertNotNull(encontrado, "O modelo deveria ser encontrado.");
        assertEquals("Corolla", encontrado.getDescricao(), "O nome do modelo deveria ser 'Corolla'.");
    }

    @Test
    @Order(4)
    void testAlterarModelo() {
//        testInserirModelo(); // Insere um modelo

        Modelo modelo = new Modelo();
        modelo.setId(1); // ID do modelo inserido
        modelo = modeloDAO.buscar(modelo);
        assertNotNull(modelo, "O modelo deveria existir para ser alterado.");

        modelo.setDescricao("Corolla Altis");
        boolean resultado = modeloDAO.alterar(modelo);

        assertTrue(resultado, "A alteração do modelo deveria retornar true.");
        assertEquals("Corolla Altis", modeloDAO.buscar(modelo).getDescricao(), "O nome do modelo deveria ser atualizado.");
    }

    @Test
    @Order(5)
    void testRemoverModelo() {
        testInserirModelo(); // Insere um modelo

        Modelo modelo = new Modelo();
        modelo.setId(1); // ID do modelo inserido

        boolean resultado = modeloDAO.remover(modelo);
        assertTrue(resultado, "A remoção do modelo deveria retornar true.");
        assertNull(modeloDAO.buscar(modelo), "O modelo removido não deveria mais existir no banco de dados.");
    }

    @AfterEach
    void rollback() throws SQLException {
        connection.rollback(); // Desfaz alterações após cada teste
    }

    @AfterAll
    void tearDown() throws SQLException {
        connection.close(); // Fecha a conexão ao final dos testes
    }
}

