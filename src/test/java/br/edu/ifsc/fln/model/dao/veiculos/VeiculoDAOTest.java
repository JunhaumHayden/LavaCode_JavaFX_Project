package br.edu.ifsc.fln.model.dao.veiculos;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.clientes.ClienteDAO;
import br.edu.ifsc.fln.model.domain.veiculos.Cor;
import br.edu.ifsc.fln.model.domain.veiculos.Modelo;
import br.edu.ifsc.fln.model.domain.veiculos.Veiculo;
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
 * @author Junhaum Hayden
 * @version 1.0
 * @since 27/11/2024
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VeiculoDAOTest {

    private Connection connection;
    private VeiculoDAO veiculoDAO;
    private ClienteDAO clienteDAO;

    @BeforeAll
    void setup() throws SQLException {
        // Configuração da conexão com o banco de dados
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "java", "java");
        veiculoDAO = new VeiculoDAO(connection);
        clienteDAO = new ClienteDAO(connection);
        // Criar tabelas e inserir dados de teste
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE cliente (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(255), telefone VARCHAR(255), email VARCHAR(255), data_cadastro DATE)");
            stmt.execute("""
                    CREATE TABLE pessoa_fisica ( id_cliente INT NOT NULL,
                                        data_nascimento DATE NOT NULL,
                                        cpf VARCHAR(14) NOT NULL,
                                        CONSTRAINT pk_pessoa_fisica PRIMARY KEY (id_cliente),
                                        CONSTRAINT fk_pessoa_fisica_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE
                                ) ENGINE=InnoDB""");

            stmt.execute("""
                    CREATE TABLE pessoa_juridica (id_cliente INT NOT NULL,
                                        inscricao_estadual VARCHAR(20),
                                        cpf_cnpj VARCHAR(18) NOT NULL,
                                        CONSTRAINT pk_pessoa_juridica PRIMARY KEY (id_cliente),
                                        CONSTRAINT fk_pessoa_juridica_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE
                                ) ENGINE=InnoDB""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS veiculo (
                                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                                        placa VARCHAR(20) NOT NULL UNIQUE,
                                                        observacoes TEXT,
                                                        id_cor INT NOT NULL,
                                                        id_modelo INT NOT NULL,
                                                        id_cliente INT NOT NULL,
                                                        CONSTRAINT fk_veiculo_cor FOREIGN KEY (id_cor) REFERENCES cor(id),
                                                        CONSTRAINT fk_veiculo_modelo FOREIGN KEY (id_modelo) REFERENCES modelo(id),
                                                        CONSTRAINT fk_veiculo_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id)
                                                            ON DELETE CASCADE
                                                            ON UPDATE CASCADE
                        ) ENGINE=InnoDB""");

            stmt.execute("""
                    INSERT INTO cliente (nome, telefone, email, data_cadastro)
                                VALUES
                                        ('Ana Amalia', '99999-1111', 'ana@email.com', '2024-12-01')""");

            stmt.execute("""
                     INSERT INTO pessoa_fisica (id_cliente, data_nascimento, cpf)
                                VALUES
                                        ((SELECT max(id) FROM cliente), '1990-03-15', '123.456.789-00')\
                    """);

            stmt.execute("""
                    INSERT INTO cliente (nome, telefone, email, data_cadastro)
                                VALUES
                                        ('Zaza Enterprise', '77777-3333', 'contato@zaza.com', '2024-12-10')""");

            stmt.execute("""
                    INSERT INTO pessoa_juridica (id_cliente, inscricao_estadual, cpf_cnpj)
                                VALUES
                                        ((SELECT max(id) FROM cliente), '123456789', '12.345.678/0001-00')""");
        }
    }

    @Test
    @Order(1)
    void testInserirVeiculo() throws ExceptionLavacao {
        // Configurando um Veículo para inserção
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC-1234");
        veiculo.setObservacoes("Carro do cliente");
        veiculo.setCliente(clienteDAO.buscarPorId(1));
        veiculo.setModelo(new Modelo(2));
        veiculo.setCor(new Cor(1));

        // Executando o método
        Veiculo resultado = veiculoDAO.inserir(veiculo);

        // Verificando os resultados
        assertNotNull(resultado);
        assertTrue(veiculo.getId() > 0, "ID do veículo deveria ter sido gerado.");
    }

    @Test
    @Order(2)
    void testBuscarVeiculoPorId() {
        int idTest = 1; // Assumindo que um veículo com ID 1 foi adicionado no banco
        Veiculo veiculo = veiculoDAO.buscarPorId(idTest);

        assertNotNull(veiculo, "O veículo não deveria ser nulo.");
        assertEquals("ABC-1234", veiculo.getPlaca(), "Placa deveria ser 'ABC-1234'.");
    }

    @Test
    @Order(3)
    void testAtualizarVeiculo() throws ExceptionLavacao {
        // Configurando um Veículo para inserção
        Veiculo veiculo = new Veiculo();
        veiculo.setId(1);
        veiculo.setPlaca("ABC-4321");
        veiculo.setObservacoes("Atualizado para teste");
        veiculo.setCliente(clienteDAO.buscarPorId(2));
        veiculo.setModelo(new Modelo(4));
        veiculo.setCor(new Cor(2));

        // Executando o método
        boolean sucesso = veiculoDAO.alterar(veiculo);

        // Verificando os resultados
        assertTrue(sucesso);

        Veiculo veiculoAtualizado = veiculoDAO.buscarPorId(1);
        assertEquals("Atualizado para teste", veiculoAtualizado.getObservacoes(), "Observação deveria ter sido atualizada.");
    }

    @Test
    @Order(4)
    void testListarVeiculo() {
        List<Veiculo> veiculo = veiculoDAO.listar();
        assertFalse(veiculo.isEmpty(), "A lista de veículos não deveria estar vazia.");
    }

    @Test
    @Order(5)
    void testRemoverVeiculo() {
        Veiculo veiculo = veiculoDAO.buscarPorId(1);
        assertNotNull(veiculo, "O veículo deveria existir para ser removido.");

        veiculoDAO.remover(veiculo);

        Veiculo veiculoRemovido = veiculoDAO.buscarPorId(1);
        assertNull(veiculoRemovido, "O veículo deveria ter sido removido.");
    }

    @AfterAll
    void closeDatabaseConnection() {
        // Limpar o banco de dados de teste
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE veiculo");
            stmt.execute("DROP TABLE pessoa_juridica");
            stmt.execute("DROP TABLE pessoa_fisica");
            stmt.execute("DROP TABLE cliente");
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            fail("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
        }
    }
}

