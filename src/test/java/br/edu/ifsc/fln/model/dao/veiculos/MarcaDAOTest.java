package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.model.domain.veiculos.Marca;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes para MarcaDAO.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Para compartilhar o setup entre os testes
class MarcaDAOTest {

    private Connection connection;
    private MarcaDAO marcaDAO;

    @BeforeAll
    void setUp() throws SQLException {
        // Conexão com banco de dados de teste
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "java", "java");
        marcaDAO = new MarcaDAO(connection);

        // Criação da tabela para testes
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS marca (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nome VARCHAR(255) NOT NULL UNIQUE) ENGINE=InnoDB");
    }


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
    void testInserirMarca() {
        Marca marca = new Marca();
        marca.setNome("Toyota test");

        boolean resultado = marcaDAO.inserir(marca);

        assertTrue(resultado, "A marca deveria ter sido inserida com sucesso.");
        assertNotNull(marca.getId(), "O ID da marca deveria ter sido gerado.");
    }

    @Test
    void testListarMarcas() {
        Marca marca1 = new Marca();
        marca1.setNome("Honda");
        marcaDAO.inserir(marca1);

        Marca marca2 = new Marca();
        marca2.setNome("Ford");
        marcaDAO.inserir(marca2);

        List<Marca> marcas = marcaDAO.listar();

        assertEquals(2, marcas.size(), "O número de marcas listadas deveria ser 2.");
    }

    @Test
    void testBuscarMarca() {
        Marca marca = new Marca();
        marca.setNome("Chevrolet");
        marcaDAO.inserir(marca);

        Marca encontrada = marcaDAO.buscar(marca.getId());

        assertNotNull(encontrada, "A marca deveria ter sido encontrada.");
        assertEquals(marca.getNome(), encontrada.getNome(), "O nome da marca deveria corresponder.");
    }

    @Test
    void testAlterarMarca() {
        Marca marca = new Marca();
        marca.setNome("Nissan");
        marcaDAO.inserir(marca);

        marca.setNome("Nissan Updated");
        boolean resultado = marcaDAO.alterar(marca);

        assertTrue(resultado, "A marca deveria ter sido atualizada com sucesso.");
        Marca atualizada = marcaDAO.buscar(marca.getId());
        assertEquals("Nissan Updated", atualizada.getNome(), "O nome da marca deveria ter sido atualizado.");
    }

    @Test
    void testRemoverMarca() {
        Marca marca = new Marca();
        marca.setNome("Hyundai");
        marcaDAO.inserir(marca);

        boolean resultado = marcaDAO.remover(marca.getId());

        assertTrue(resultado, "A marca deveria ter sido removida com sucesso.");
        Marca removida = marcaDAO.buscar(marca.getId());
        assertNull(removida, "A marca não deveria ser encontrada após a remoção.");
    }

    @AfterAll
    void tearDown() throws SQLException {
        // Limpeza do banco após os testes
        connection.createStatement().executeUpdate("DROP TABLE IF EXISTS marcas");
        connection.close();
    }
}

