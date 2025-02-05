package br.edu.ifsc.fln.model.dao.clientes;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.domain.Pontuacao;
import br.edu.ifsc.fln.model.domain.clientes.Cliente;
import br.edu.ifsc.fln.model.domain.clientes.PessoaFisica;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

public class PontuacaoDAOTest {
    private PontuacaoDAO pontuacaoDAO;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException, ExceptionLavacao {
        // Configurar a conexão com o banco de dados de teste
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "java", "java");
        pontuacaoDAO = new PontuacaoDAO(connection);
        connection.createStatement().execute("CREATE TABLE pontuacao_fidelidade (\n" +
                "                                      id_cliente INT NOT NULL,\n" +
                "                                      pontos INT NOT NULL DEFAULT 0,\n" +
                "                                      CONSTRAINT pk_pontuacao_fidelidade PRIMARY KEY (id_cliente)\n" +
                "            ) ENGINE=InnoDB");
    }

    @AfterEach
    public void tearDown() throws SQLException {
        connection.createStatement().execute("DROP TABLE pontuacao_fidelidade");
        connection.close();
    }

    @Test
    public void testInserir() throws SQLException, ExceptionLavacao {
        Cliente cliente = new PessoaFisica();
        cliente.setId(1);
        cliente.getPontuacao().adicionarPontos(100);
        assertTrue(pontuacaoDAO.inserir(cliente));
    }

    @Test
    public void testAlterar() throws SQLException, ExceptionLavacao {
        Cliente cliente = new PessoaFisica();
        cliente.setId(1);
        cliente.getPontuacao().adicionarPontos( 100);
        pontuacaoDAO.inserir(cliente);
        cliente.getPontuacao().adicionarPontos(50);
        assertTrue(pontuacaoDAO.alterar(cliente));
        assertEquals(150, pontuacaoDAO.buscarPorId(1).getSaldo(), "A pontuação não foi alterada corretamente");
        cliente.getPontuacao().subtrairPontos(50);
        assertTrue(pontuacaoDAO.alterar(cliente));
        assertEquals(100, pontuacaoDAO.buscarPorId(1).getSaldo(), "A pontuação não foi alterada corretamente");
    }

    @Test
    public void testAdicionarPontosNegativos() {
        Cliente cliente = new PessoaFisica();
        cliente.setId(1);
        Exception exception = assertThrows(ExceptionLavacao.class, () -> {
            cliente.getPontuacao().adicionarPontos(-10);
        });
        assertEquals("Quantidade de pontos a adicionar não pode ser negativa.", exception.getMessage());
    }

    @Test
    public void testSubtrairPontosNegativos() {
        Cliente cliente = new PessoaFisica();
        cliente.setId(1);
        Exception exception = assertThrows(ExceptionLavacao.class, () -> {
            cliente.getPontuacao().subtrairPontos(-10);
        });
        assertEquals("Quantidade de pontos a subtrair não pode ser negativa.", exception.getMessage());
    }

    @Test
    public void testSubtrairPontosInsuficientes() throws ExceptionLavacao {
        Cliente cliente = new PessoaFisica();
        cliente.setId(1);
        cliente.getPontuacao().adicionarPontos(50);
        Exception exception = assertThrows(ExceptionLavacao.class, () -> {
            cliente.getPontuacao().subtrairPontos(100);
        });
        assertEquals("Saldo de pontos insuficiente.", exception.getMessage());
    }

    @Test
    public void testRemover() throws SQLException, ExceptionLavacao {
        Cliente cliente = new PessoaFisica();
        cliente.setId(1);
        cliente.getPontuacao().adicionarPontos(100);
        pontuacaoDAO.inserir(cliente);
        assertTrue(pontuacaoDAO.remover(1));
    }

    @Test
    public void testBuscar() throws SQLException, ExceptionLavacao {
        Cliente cliente = new PessoaFisica();
        cliente.setId(1);
        cliente.getPontuacao().adicionarPontos(100);
        pontuacaoDAO.inserir(cliente);
        Pontuacao pontuacao = pontuacaoDAO.buscar(cliente);
        assertNotNull(pontuacao);
        assertEquals(100, pontuacao.getSaldo());
    }

    @Test
    public void testBuscarPorId() throws SQLException, ExceptionLavacao {
        Cliente cliente = new PessoaFisica();
        cliente.setId(1);
        cliente.getPontuacao().adicionarPontos(100);
        pontuacaoDAO.inserir(cliente);
        Pontuacao pontuacao = pontuacaoDAO.buscarPorId(1);
        assertNotNull(pontuacao);
        assertEquals(100, pontuacao.getSaldo());
    }

    @Test
    public void testListar() throws SQLException, ExceptionLavacao {
        Cliente cliente1 = new PessoaFisica();
        cliente1.setId(1);
        cliente1.getPontuacao().adicionarPontos(100);
        pontuacaoDAO.inserir(cliente1);

        Cliente cliente2 = new PessoaFisica();
        cliente2.setId(2);
        cliente2.getPontuacao().adicionarPontos(200);
        pontuacaoDAO.inserir(cliente2);

        assertEquals(2, pontuacaoDAO.listar().size());
    }
}
