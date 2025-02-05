package br.edu.ifsc.fln.model.dao.clientes;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.domain.clientes.Cliente;
import br.edu.ifsc.fln.model.domain.clientes.PessoaFisica;
import br.edu.ifsc.fln.model.domain.clientes.PessoaJuridica;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteDAOTest {

    private Connection connection;
    private ClienteDAO clienteDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        // Configurar a conexão com o banco de dados de teste
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "java", "java");
        clienteDAO = new ClienteDAO(connection);
        // Criar tabelas e inserir dados de teste
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE cliente (\n" +
                    "                    id INT NOT NULL AUTO_INCREMENT,\n" +
                    "                    nome VARCHAR(100) NOT NULL,\n" +
                    "                    telefone VARCHAR(20),\n" +
                    "                    email VARCHAR(100),\n" +
                    "                    data_cadastro DATE NOT NULL,\n" +
                    "                    CONSTRAINT pk_cliente PRIMARY KEY (id)\n" +
                    "            ) ENGINE=InnoDB");

            stmt.execute("CREATE TABLE pessoa_fisica ( id_cliente INT NOT NULL,\n" +
                    "                    data_nascimento DATE NOT NULL,\n" +
                    "                    cpf VARCHAR(14) NOT NULL,\n" +
                    "                    CONSTRAINT pk_pessoa_fisica PRIMARY KEY (id_cliente),\n" +
                    "                    CONSTRAINT fk_pessoa_fisica_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)\n" +
                    "                    ON DELETE CASCADE\n" +
                    "                    ON UPDATE CASCADE\n" +
                    "            ) ENGINE=InnoDB");

            stmt.execute("CREATE TABLE pessoa_juridica (id_cliente INT NOT NULL,\n" +
                    "                    inscricao_estadual VARCHAR(20),\n" +
                    "                    cpf_cnpj VARCHAR(18) NOT NULL,\n" +
                    "                    CONSTRAINT pk_pessoa_juridica PRIMARY KEY (id_cliente),\n" +
                    "                    CONSTRAINT fk_pessoa_juridica_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)\n" +
                    "                    ON DELETE CASCADE\n" +
                    "                    ON UPDATE CASCADE\n" +
                    "            ) ENGINE=InnoDB");

            stmt.execute("CREATE TABLE  veiculo (id INT AUTO_INCREMENT PRIMARY KEY, placa VARCHAR(20) NOT NULL, observacoes TEXT,\n" +
                    "            id_cor INT NOT NULL,\n" +
                    "            id_modelo INT NOT NULL,\n" +
                    "            id_cliente INT NOT NULL, CONSTRAINT fk_veiculo_cor FOREIGN KEY (id_cor) REFERENCES cor(id),\n" +
                    "                    CONSTRAINT fk_veiculo_modelo FOREIGN KEY (id_modelo) REFERENCES modelo(id),\n" +
                    "                    CONSTRAINT fk_veiculo_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id)\n" +
                    "                    ON DELETE CASCADE\n" +
                    "            ON UPDATE CASCADE\n" +
                    "    ) ENGINE=InnoDB");

            stmt.execute("INSERT INTO cliente (nome, telefone, email, data_cadastro)\n" +
                    "            VALUES\n" +
                    "                    ('Ana Amalia', '99999-1111', 'ana@email.com', '2024-12-01')");

            stmt.execute(" INSERT INTO pessoa_fisica (id_cliente, data_nascimento, cpf)\n" +
                    "            VALUES\n" +
                    "                    ((SELECT max(id) FROM cliente), '1990-03-15', '123.456.789-00')");

            stmt.execute("INSERT INTO cliente (nome, telefone, email, data_cadastro)\n" +
                    "            VALUES\n" +
                    "                    ('Zaza Enterprise', '77777-3333', 'contato@zaza.com', '2024-12-10')");

            stmt.execute("INSERT INTO pessoa_juridica (id_cliente, inscricao_estadual, cpf_cnpj)\n" +
                    "            VALUES\n" +
                    "                    ((SELECT max(id) FROM cliente), '123456789', '12.345.678/0001-00')");
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Limpar o banco de dados de teste
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE veiculo");
            stmt.execute("DROP TABLE pessoa_juridica");
            stmt.execute("DROP TABLE pessoa_fisica");
            stmt.execute("DROP TABLE cliente");
        }
        connection.close();
    }

    @Test
    @Order(1)
    public void testInserir() throws SQLException, ExceptionLavacao {
        Cliente cliente = new PessoaFisica();
        cliente.setNome("João");
        cliente.setCelular("123456789");
        cliente.setEmail("joao@example.com");
        cliente.setDataCadastro(new Date(System.currentTimeMillis()));
        ((PessoaFisica) cliente).setCpf("123.456.789-00");
        ((PessoaFisica) cliente).setDataNascimento(new Date(System.currentTimeMillis()));

        Cliente inserido = clienteDAO.inserir(cliente);
        assertNotNull(inserido);
        assertEquals("João", inserido.getNome());
    }

    @Test
    @Order(2)
    public void testAlterar() throws SQLException, ExceptionLavacao {
        Cliente cliente = new PessoaFisica();
        cliente.setNome("João");
        cliente.setCelular("123456789");
        cliente.setEmail("joao@example.com");
        cliente.setDataCadastro(new Date(System.currentTimeMillis()));
        ((PessoaFisica) cliente).setCpf("123.456.789-00");
        ((PessoaFisica) cliente).setDataNascimento(new Date(System.currentTimeMillis()));

        Cliente inserido = clienteDAO.inserir(cliente);
        inserido.setNome("João Alterado");
        boolean atualizado = clienteDAO.alterar(inserido);
        assertTrue(atualizado);

        Cliente buscado = clienteDAO.buscarPorId(inserido.getId());
        assertEquals("João Alterado", buscado.getNome());
    }

    @Test
    @Order(4)
    public void testRemover() throws SQLException, ExceptionLavacao {
        Cliente cliente = new PessoaFisica();
        cliente.setNome("João");
        cliente.setCelular("123456789");
        cliente.setEmail("joao@example.com");
        cliente.setDataCadastro(new Date(System.currentTimeMillis()));
        ((PessoaFisica) cliente).setCpf("123.456.789-00");
        ((PessoaFisica) cliente).setDataNascimento(new Date(System.currentTimeMillis()));

        Cliente inserido = clienteDAO.inserir(cliente);
        boolean removido = clienteDAO.remover(inserido);
        assertTrue(removido);

        Cliente buscado = clienteDAO.buscarPorId(inserido.getId());
        assertNull(buscado);
    }

    @Test
    @Order(3)
    public void testListar() throws SQLException, ExceptionLavacao {
        Cliente cliente1 = new PessoaFisica();
        cliente1.setNome("João");
        cliente1.setCelular("123456789");
        cliente1.setEmail("joao@example.com");
        cliente1.setDataCadastro(new Date(System.currentTimeMillis()));
        ((PessoaFisica) cliente1).setCpf("123.456.789-00");
        ((PessoaFisica) cliente1).setDataNascimento(new Date(System.currentTimeMillis()));

        Cliente cliente2 = new PessoaJuridica();
        cliente2.setNome("Empresa X");
        cliente2.setCelular("987654321");
        cliente2.setEmail("empresa@example.com");
        cliente2.setDataCadastro(new Date(System.currentTimeMillis()));
        ((PessoaJuridica) cliente2).setCnpj("12.345.678/0001-00");
        ((PessoaJuridica) cliente2).setInscricaoEstadual("123456789");

        clienteDAO.inserir(cliente1);
        clienteDAO.inserir(cliente2);

        List<Cliente> clientes = clienteDAO.listar();
        assertEquals(4, clientes.size());
    }
}

