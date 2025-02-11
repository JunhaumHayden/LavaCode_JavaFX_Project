package br.edu.ifsc.fln.model.dao.ordemservicos;

import br.edu.ifsc.fln.model.dao.ordenservicos.ItemDeOrdemDeServicoDAO;
import br.edu.ifsc.fln.model.dao.ordenservicos.OrdemDeServicoDAO;
import br.edu.ifsc.fln.model.dao.ordenservicos.ServicoDAO;
import br.edu.ifsc.fln.model.domain.ordemServicos.ItemDeOrdemDeServico;
import br.edu.ifsc.fln.model.domain.ordemServicos.OrdemDeServico;
import br.edu.ifsc.fln.model.domain.ordemServicos.Servico;
import br.edu.ifsc.fln.service.OrdemDeServicoService;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemDeOrdemDeServicoDAOTest {

    private ItemDeOrdemDeServicoDAO itemOsDAO;
    private ServicoDAO servicoDAO;
    private OrdemDeServicoDAO ordemDeServicoDAO;
    private Connection connection;

    @BeforeAll
    void setupDatabase() throws SQLException {
        // Configuração da conexão com o banco de dados
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "java", "java");
        itemOsDAO = new ItemDeOrdemDeServicoDAO(connection);
        servicoDAO = new ServicoDAO(connection);
        ordemDeServicoDAO = new OrdemDeServicoDAO(connection);
        // Criar tabelas cliente e veiculo e inserir dados de teste
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS cliente (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(255), telefone VARCHAR(255), email VARCHAR(255), data_cadastro DATE)");
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS pessoa_fisica ( id_cliente INT NOT NULL,
                                        data_nascimento DATE NOT NULL,
                                        cpf VARCHAR(14) NOT NULL,
                                        CONSTRAINT pk_pessoa_fisica PRIMARY KEY (id_cliente),
                                        CONSTRAINT fk_pessoa_fisica_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
                                        ON DELETE CASCADE
                                        ON UPDATE CASCADE
                                ) ENGINE=InnoDB""");

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS pessoa_juridica (id_cliente INT NOT NULL,
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

            stmt.execute("""
                    INSERT INTO veiculo (placa, observacoes, id_cor, id_modelo, id_cliente)
                    VALUES
                        ('AAA1111', 'Sem observações.', 1, 2, 1),
                        ('BBB2222', 'Cliente solicita cuidado extra ao estacionar.', 2, 4, 2),
                        ('ZZZ1010', 'Veículo novo, ainda em garantia.', 3, 3, 2)""");
        }
        // Criar tabela servico e inserir dados de teste
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS servico (
                id INT AUTO_INCREMENT PRIMARY KEY,
                descricao VARCHAR(255) NOT NULL,
                valor DECIMAL(10, 2) NOT NULL,
                categoria ENUM('PEQUENO', 'MEDIO', 'GRANDE', 'MOTO', 'PADRAO') NOT NULL,
                pontos INT
            ) ENGINE=InnoDB""");



            stmt.execute("""
                    INSERT INTO servico (descricao, valor, categoria, pontos)
                    VALUES
                        ('Lavagem simples', 50.00, 'PEQUENO', 10),
                        ('Lavagem completa', 80.00, 'MEDIO', 20),
                        ('Polimento externo', 150.00, 'GRANDE', 30),
                        ('Lavagem rápida', 30.00, 'MOTO', 5),
                        ('Higienização interna', 100.00, 'PADRAO', 15),
                        ('Lavagem completa premium', 120.00, 'GRANDE', 25),
                        ('Enceramento', 70.00, 'PEQUENO', 15),
                        ('Lavagem detalhada', 90.00, 'MEDIO', 20),
                        ('Aspiração',50.00,'PEQUENO',30),
                        ('Lavagem Externa', 40.00,'PEQUENO',30),
                        ('Lavagem Motor',40.00,'PEQUENO',40),
                        ('Cera Liquida', 25.00,'PEQUENO',10),
                        ('Cera Pastosa',40.00, 'PEQUENO',30),
                        ('Pretinho Pneu',50.00,'PEQUENO',10),
                        ('Secagem',15.00,'PEQUENO',25)""");
        }
        // Criar tabela ordem de servico e inserir dados de teste
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                
                    CREATE TABLE ordem_servico (
                                                id INT AUTO_INCREMENT,
                                                numero VARCHAR(255) NOT NULL UNIQUE,
                                                total DECIMAL(10, 2),
                                                desconto DECIMAL(10, 2) NOT NULL,
                                                agenda DATE,
                                                id_veiculo INT NOT NULL,
                                                status ENUM('ABERTA', 'FECHADA', 'CANCELADA') NOT NULL DEFAULT 'ABERTA',
                                                CONSTRAINT pk_os PRIMARY KEY(id),
                                                CONSTRAINT fk_os_veiculo FOREIGN KEY (id_veiculo) REFERENCES veiculo(id)
                ) ENGINE=InnoDB""");



            stmt.execute("""
                    INSERT INTO ordem_servico (numero, total, desconto, agenda, id_veiculo, status)
                    VALUES
                        (10001, 250.00, 0.0, '2025-01-22', 1, 'ABERTA'),
                        (10002, 500.00, 15.0, '2025-01-23', 2, 'FECHADA'),
                        (10003, 150.00, 5.0, '2025-01-24', 3, 'CANCELADA');""");
        }

        // Criação da tabela para testes
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS item_os (
            id INT AUTO_INCREMENT PRIMARY KEY,
            observacao TEXT,
            valor_servico DECIMAL(10, 2) NOT NULL,
            id_servico INT NOT NULL,
            id_ordemServico INT NOT NULL,
            FOREIGN KEY (id_servico) REFERENCES Servico(id),
            FOREIGN KEY (id_ordemServico) REFERENCES ordem_servico(id)
                ON DELETE CASCADE
                            ) ENGINE=InnoDB
        """;

        connection.createStatement().execute(createTableSQL);
    }

    @AfterAll
    void teardownDatabase() throws SQLException {
        // Limpa a tabela após os testes
        connection.createStatement().execute("DROP TABLE IF EXISTS item_os");
        connection.createStatement().execute("DROP TABLE IF EXISTS ordem_servico");
        connection.createStatement().execute("DROP TABLE IF EXISTS veiculo");
        connection.createStatement().execute("DROP TABLE IF EXISTS pessoa_fisica");
        connection.createStatement().execute("DROP TABLE IF EXISTS pessoa_juridica");
        connection.createStatement().execute("DROP TABLE IF EXISTS cliente");
        connection.createStatement().execute("DROP TABLE IF EXISTS servico");

        connection.close();
    }

    @Test
    @Order(1)
    void testInserir() throws SQLException{
        Servico servico01 = servicoDAO.buscarPorId(1);
        OrdemDeServico os = ordemDeServicoDAO.buscarPorId(1);

        ItemDeOrdemDeServico itemDeOrdemDeServico = new ItemDeOrdemDeServico("Troca de óleo",  servico01);
        itemDeOrdemDeServico.setOrdemDeServico(os);
        // Executando o método
        ItemDeOrdemDeServico resultado = itemOsDAO.inserir(itemDeOrdemDeServico);
        // Verificando os resultados
        assertNotNull(resultado);
        assertTrue(itemDeOrdemDeServico.getId() > 0, "O ID do item de OS deveria ter sido gerado.");
        assertTrue(itemDeOrdemDeServico.getValorServico().compareTo(BigDecimal.ZERO) > 0, "O Valor do item de OS deveria ter sido gerado.");
        assertTrue(itemDeOrdemDeServico.getValorServico().compareTo(servico01.getValor()) == 0, "O valor do item de OS deve ser igual ao valor do serviço.");


    }

    @Test
    @Order(2)
    void testBuscarPorId() throws SQLException {
        ItemDeOrdemDeServico itemDeOrdemDeServico = itemOsDAO.buscarPorId(1);

        assertNotNull(itemDeOrdemDeServico, "O item de OS com ID 1 deve existir.");
        assertEquals("Troca de óleo", itemDeOrdemDeServico.getObservacao(), "A descrição do item de OS deve ser 'Troca de óleo'.");
    }

    @Test
    @Order(3)
    void testAtualizar() throws SQLException {
        Servico servico02 = servicoDAO.buscarPorId(2);
        ItemDeOrdemDeServico itemDeOrdemDeServico = itemOsDAO.buscarPorId(1);
        assertNotNull(itemDeOrdemDeServico, "O item de OS com ID 1 deve existir para ser atualizado.");

        itemDeOrdemDeServico.setObservacao("Troca de filtro de ar");
        itemDeOrdemDeServico.setServico(servico02);
        itemOsDAO.alterar(itemDeOrdemDeServico);

        ItemDeOrdemDeServico itemDeOrdemDeServicoAtualizado = itemOsDAO.buscarPorId(1);
        assertEquals("Troca de filtro de ar", itemDeOrdemDeServicoAtualizado.getObservacao(), "A descrição deve ser 'Troca de filtro de ar'.");
        assertNotEquals(itemDeOrdemDeServicoAtualizado.getValorServico().compareTo(servico02.getValor()) == 0, "O valor do item de OS não deve ser alterado.");
        assertNotEquals(servico02, itemDeOrdemDeServicoAtualizado.getServico(), "O serviço do item de OS não deve ser alterado.");
    }

    @Test
    @Order(4)
    void testListarTodos() throws SQLException {
        Servico servico01 = servicoDAO.buscarPorId(3);
        OrdemDeServico os = new OrdemDeServico();
        os.setId(2);
        os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());

        ItemDeOrdemDeServico itemDeOrdemDeServico = new ItemDeOrdemDeServico("Lavar geral",  servico01);
        itemDeOrdemDeServico.setOrdemDeServico(os);
        // Executando o método
        itemOsDAO.inserir(itemDeOrdemDeServico);

        List<ItemDeOrdemDeServico> itensOs = itemOsDAO.listar();
        assertFalse(itensOs.isEmpty(), "A lista de itens de OS não deve estar vazia.");
        assertEquals(2, itensOs.size(), "A lista de itens de OS deve conter exatamente 2 item.");
    }

    @Test
    @Order(5)
    void testRemover() throws SQLException {
        ItemDeOrdemDeServico itemDeOrdemDeServico = itemOsDAO.buscarPorId(1);
        itemOsDAO.remover(itemDeOrdemDeServico);
        itemDeOrdemDeServico = itemOsDAO.buscarPorId(1);
        assertNull(itemDeOrdemDeServico, "O item de OS com ID 1 deve ser nulo após ser removido.");
    }
}
