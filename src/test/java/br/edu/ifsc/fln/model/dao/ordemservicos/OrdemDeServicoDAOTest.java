package br.edu.ifsc.fln.model.dao.ordemservicos;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.ordenservicos.ItemDeOrdemDeServicoDAO;
import br.edu.ifsc.fln.model.dao.ordenservicos.OrdemDeServicoDAO;
import br.edu.ifsc.fln.model.dao.ordenservicos.ServicoDAO;
import br.edu.ifsc.fln.model.dao.veiculos.VeiculoDAO;
import br.edu.ifsc.fln.model.domain.ordemServicos.ItemDeOrdemDeServico;
import br.edu.ifsc.fln.model.domain.ordemServicos.OrdemDeServico;
import br.edu.ifsc.fln.model.domain.ordemServicos.Servico;
import br.edu.ifsc.fln.model.domain.ordemServicos.EStatus;
import br.edu.ifsc.fln.service.OrdemDeServicoService;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrdemDeServicoDAOTest {

    private ServicoDAO servicoDAO;
    private VeiculoDAO veiculoDAO;
    private OrdemDeServicoDAO ordemDeServicoDAO;
    private ItemDeOrdemDeServicoDAO itemDeOrdemDeServicoDAO;
    private Connection connection;

    @BeforeAll
    void setupDatabase() throws SQLException {
        // Configuração da conexão com o banco de dados
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "java", "java");
        servicoDAO = new ServicoDAO(connection);
        veiculoDAO = new VeiculoDAO(connection);
        ordemDeServicoDAO = new OrdemDeServicoDAO(connection);
        itemDeOrdemDeServicoDAO = new ItemDeOrdemDeServicoDAO(connection);
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
        }
        // Criação da tabela para testes
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
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
            """);
        }
        // Criação da tabela Pontuacao para testes
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pontuacao_fidelidade (
                                id_cliente INT NOT NULL,
                                pontos INT NOT NULL DEFAULT 0,
                                CONSTRAINT pk_pontuacao_fidelidade PRIMARY KEY (id_cliente),
                                CONSTRAINT fk_pontuacao_fidelidade_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
                                ON DELETE CASCADE
                                ON UPDATE CASCADE
                        ) ENGINE=InnoDB
            """);

            stmt.execute("""
                    INSERT INTO pontuacao_fidelidade (id_cliente, pontos)
                    VALUES
                        (1, 20),
                        (2, 40)""");
        }

    }

    @AfterAll
    void teardownDatabase() throws SQLException {
        // Limpa a tabela após os testes
        connection.createStatement().execute("DROP TABLE IF EXISTS item_os");
        connection.createStatement().execute("DROP TABLE IF EXISTS ordem_servico");
        connection.createStatement().execute("DROP TABLE IF EXISTS veiculo");
        connection.createStatement().execute("DROP TABLE IF EXISTS pessoa_fisica");
        connection.createStatement().execute("DROP TABLE IF EXISTS pessoa_juridica");
        connection.createStatement().execute("DROP TABLE IF EXISTS pontuacao_fidelidade");
        connection.createStatement().execute("DROP TABLE IF EXISTS cliente");
        connection.createStatement().execute("DROP TABLE IF EXISTS servico");

        connection.close();
    }


    @Test
    @Order(1)
    public void testInserirSemItemOs() throws SQLException, ExceptionLavacao {
        OrdemDeServico os = new OrdemDeServico();
        os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());
        os.setDesconto(BigDecimal.ZERO);
        os.setAgenda(new Date(System.currentTimeMillis()));
        os.setVeiculo(veiculoDAO.buscarPorId(1));
        os.setStatus(EStatus.ABERTA);

        OrdemDeServico insertedOs = ordemDeServicoDAO.inserir(os);
        assertNotNull(insertedOs);
        assertNotNull(insertedOs.getId());
        BigDecimal zero = BigDecimal.ZERO;
        assertEquals(zero, insertedOs.getTotal(), "O valor total da OS deve ter sido alterado.");


    }

    @Test
    @Order(2)
    public void testInserirComItemOs() throws SQLException, ExceptionLavacao {
        OrdemDeServico os = new OrdemDeServico();
        os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());
        os.setDesconto(new BigDecimal("20.00"));
        os.setAgenda(new Date(System.currentTimeMillis()));
        os.setVeiculo(veiculoDAO.buscarPorId(1));
        os.setStatus(EStatus.ABERTA);

        Servico servico01 = servicoDAO.buscarPorId(1);
        ItemDeOrdemDeServico item = new ItemDeOrdemDeServico();
        item.setObservacao("Observação do teste com item de OS");
        item.setServico(servico01);
        os.addItemOS(item);

        OrdemDeServico insertedOs = ordemDeServicoDAO.inserir(os);
        assertNotNull(insertedOs);
        assertNotNull(insertedOs.getId());
    }

    @Test
    @Order(3)
    public void testAlterarOs() throws SQLException, ExceptionLavacao {
        OrdemDeServico os = new OrdemDeServico();
        os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());
        os.setDesconto(new BigDecimal("30.00"));
        os.setAgenda(new Date(System.currentTimeMillis()));
        os.setVeiculo(veiculoDAO.buscarPorId(2));
        os.setStatus(EStatus.ABERTA);
        OrdemDeServico insertedOs = ordemDeServicoDAO.inserir(os);

        insertedOs.setAgenda(new Date("2025/02/04"));
        insertedOs.setStatus(EStatus.CANCELADA);
//        os.setAgenda(LocalDate.of(2025, 2, 4));
        boolean updated = ordemDeServicoDAO.alterar(insertedOs);
        assertTrue(updated);

        OrdemDeServico updatedOs = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        assertEquals(insertedOs.getAgenda(), updatedOs.getAgenda(), "A data deve deve ter sido alterada.");
        assertEquals(insertedOs.getStatus().name(), updatedOs.getStatus().name(), "A data deve deve ter sido alterada.");
    }
    @Test
    @Order(4)
    public void testAlterarItensOs() throws SQLException, ExceptionLavacao {
        OrdemDeServico os = new OrdemDeServico();
        os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());
        os.setDesconto(new BigDecimal("00.00"));
        os.setAgenda(new Date(System.currentTimeMillis()));
        os.setVeiculo(veiculoDAO.buscarPorId(2));
        os.setStatus(EStatus.ABERTA);
        OrdemDeServico insertedOs = ordemDeServicoDAO.inserir(os);

        Servico servico02 = servicoDAO.buscarPorId(2);
        ItemDeOrdemDeServico item02 = new ItemDeOrdemDeServico();
        item02.setObservacao("Observação do teste alterar os itens da OS");
        item02.setServico(servico02);
        insertedOs.addItemOS(item02);

        Servico servico03 = servicoDAO.buscarPorId(3);
        ItemDeOrdemDeServico item03 = new ItemDeOrdemDeServico();
        item03.setObservacao("Observação 02 do teste alterar os itens da OS");
        item03.setServico(servico03);
        insertedOs.addItemOS(item03);

        boolean updated = ordemDeServicoDAO.alterar(insertedOs);
        assertTrue(updated);

        BigDecimal TotalOs = servico02.getValor().add(servico03.getValor());
        OrdemDeServico updatedOs = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        assertEquals(TotalOs, updatedOs.getTotal(), "O valor total da OS deve ter sido alterado.");
        assertEquals(2, updatedOs.getItens().size(), "A lista de itens de OS deve conter exatamente 2 item.");
    }

    @Test
    @Order(5)
    public void testRemover() throws SQLException, ExceptionLavacao {
        OrdemDeServico os = new OrdemDeServico();
        os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());
        os.setDesconto(new BigDecimal("00.00"));
        os.setAgenda(new Date(System.currentTimeMillis()));
        os.setVeiculo(veiculoDAO.buscarPorId(2));
        os.setStatus(EStatus.ABERTA);
        OrdemDeServico insertedOs = ordemDeServicoDAO.inserir(os);
        boolean removed = ordemDeServicoDAO.remover(insertedOs);
        assertTrue(removed);
    }

    @Test
    @Order(6)
    public void testRemoverItemOs() throws SQLException, ExceptionLavacao {
        OrdemDeServico os = new OrdemDeServico();
        os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());
        os.setDesconto(new BigDecimal("00.00"));
        os.setAgenda(new Date(System.currentTimeMillis()));
        os.setVeiculo(veiculoDAO.buscarPorId(2));
        os.setStatus(EStatus.ABERTA);
        OrdemDeServico insertedOs = ordemDeServicoDAO.inserir(os);

        Servico servico02 = servicoDAO.buscarPorId(2);
        ItemDeOrdemDeServico item02 = new ItemDeOrdemDeServico();
        item02.setObservacao("Observação do teste alterar os itens da OS");
        item02.setServico(servico02);
        insertedOs.addItemOS(item02);

        Servico servico03 = servicoDAO.buscarPorId(3);
        ItemDeOrdemDeServico item03 = new ItemDeOrdemDeServico();
        item03.setObservacao("Observação 02 do teste alterar os itens da OS");
        item03.setServico(servico03);
        insertedOs.addItemOS(item03);

        boolean updated = ordemDeServicoDAO.alterar(insertedOs);
        assertTrue(updated);

        BigDecimal TotalOs = servico02.getValor().add(servico03.getValor());
        OrdemDeServico updatedOs = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        assertEquals(TotalOs, updatedOs.getTotal(), "O valor total da OS deve ter sido alterado.");
        assertEquals(2, updatedOs.getItens().size(), "A lista de itens de OS deve conter exatamente 2 item.");

        boolean removed = itemDeOrdemDeServicoDAO.remover(insertedOs.getItens().get(1));
        assertTrue(removed);

        OrdemDeServico updatedOs02 = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        assertNotEquals(TotalOs, updatedOs02.getTotal(), "O valor total da OS deve ter sido alterado.");
        assertEquals(1, updatedOs02.getItens().size(), "A lista de itens de OS deve conter exatamente 2 item.");
    }

    @Test
    @Order(7)
    public void testListar() throws SQLException {
        List<OrdemDeServico> ordens = ordemDeServicoDAO.listar();
        assertNotNull(ordens);
        assertTrue(ordens.size() > 0);
    }

    @Test
    @Order(8)
    public void testBuscarPorId() throws SQLException, ExceptionLavacao {
        OrdemDeServico os = new OrdemDeServico();
        os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());
        os.setDesconto(new BigDecimal("00.00"));
        os.setAgenda(new Date(System.currentTimeMillis()));
        os.setVeiculo(veiculoDAO.buscarPorId(1));
        os.setStatus(EStatus.ABERTA);
        OrdemDeServico insertedOs = ordemDeServicoDAO.inserir(os);
        OrdemDeServico foundOs = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        assertNotNull(foundOs);
        assertEquals(insertedOs.getId(), foundOs.getId());
    }

    @Test
    @Order(6)
    public void testPontuacao() throws SQLException, ExceptionLavacao {
        OrdemDeServico os = new OrdemDeServico();
        os.setNumero(OrdemDeServicoService.gerarNumeroOrdem());
        os.setDesconto(new BigDecimal("00.00"));
        os.setAgenda(new Date(System.currentTimeMillis()));
        os.setVeiculo(veiculoDAO.buscarPorId(2));
        os.setStatus(EStatus.ABERTA);
        OrdemDeServico insertedOs = ordemDeServicoDAO.inserir(os);

        Servico servico02 = servicoDAO.buscarPorId(2);
        ItemDeOrdemDeServico item02 = new ItemDeOrdemDeServico();
        item02.setObservacao("Observação do teste alterar os itens da OS");
        item02.setServico(servico02);
        insertedOs.addItemOS(item02);

        Servico servico03 = servicoDAO.buscarPorId(3);
        ItemDeOrdemDeServico item03 = new ItemDeOrdemDeServico();
        item03.setObservacao("Observação 02 do teste alterar os itens da OS");
        item03.setServico(servico03);
        insertedOs.addItemOS(item03);

        boolean updated = ordemDeServicoDAO.alterar(insertedOs);
        assertTrue(updated);

        BigDecimal TotalOs = servico02.getValor().add(servico03.getValor());
        OrdemDeServico updatedOs = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        assertEquals(TotalOs, updatedOs.getTotal(), "O valor total da OS deve ter sido alterado.");
        assertEquals(2, updatedOs.getItens().size(), "A lista de itens de OS deve conter exatamente 2 item.");

        OrdemDeServico insertedOs02 = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        insertedOs02.setStatus(EStatus.CANCELADA);
        boolean updated02 = ordemDeServicoDAO.alterar(insertedOs);
        assertTrue(updated02);

        insertedOs02 = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        insertedOs02.setStatus(EStatus.FECHADA);
        updated02 = ordemDeServicoDAO.alterar(insertedOs);
        assertTrue(updated02);

        insertedOs02 = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        assertEquals(EStatus.FECHADA, insertedOs02.getStatus(), "A deve constar como FECHADA.");
        assertEquals(EStatus.FECHADA, insertedOs02.getItens().get(1).getOrdemDeServico().getStatus(), "A deve constar como FECHADA.");

        boolean removed = itemDeOrdemDeServicoDAO.remover(insertedOs02.getItens().get(1));
        assertFalse(removed);
        insertedOs02.setDesconto(new BigDecimal("00.00"));
        insertedOs02.setAgenda(new Date(System.currentTimeMillis()));
        insertedOs02.setVeiculo(veiculoDAO.buscarPorId(1));
        insertedOs02.setStatus(EStatus.ABERTA);
        insertedOs02.setStatus(EStatus.CANCELADA);

        int TotalPontos = servico02.getPontos() + servico03.getPontos();
        OrdemDeServico updatedOs02 = ordemDeServicoDAO.buscarPorId(insertedOs.getId());
        assertEquals(TotalPontos, updatedOs02.getVeiculo().getCliente().getPontuacao().getSaldo(), "O valor da pontuaçao deve ter sido alterado.");
        assertEquals(EStatus.FECHADA, updatedOs02.getStatus(), "A deve constar como FECHADA.");
    }
}
