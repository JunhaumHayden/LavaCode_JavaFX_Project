package br.edu.ifsc.fln;

import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseMySQLTest {

    @Test
    public void testConexaoMySQL() {
        // Obter a implementação do banco de dados (MySQL)
        Database database = DatabaseFactory.getDatabase("mysql");

        // Verificar se o objeto Database não é nulo
        assertNotNull(database, "A instância do banco de dados é nula!");

        // Conectar ao banco de dados
        Connection connection = database.conectar();

        // Verificar se a conexão foi bem-sucedida
        assertNotNull(connection, "Falha ao estabelecer a conexão com o banco de dados!");

        // Desconectar e verificar se não há exceções
        try {
            database.desconectar(connection);
            System.out.println("Conexão desconectada com sucesso.");
        } catch (Exception e) {
            fail("Erro ao desconectar do banco de dados: " + e.getMessage());
        }
    }
}

