package br.edu.ifsc.fln.model.dao.clientes;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.veiculos.CorDAO;
import br.edu.ifsc.fln.model.domain.clientes.Cliente;
import br.edu.ifsc.fln.model.domain.clientes.PessoaFisica;
import br.edu.ifsc.fln.model.domain.clientes.PessoaJuridica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe ClienteDAO para manipulação de dados da tabela Cliente no banco de dados.
 *
 * @author Junhaum
 * @version 1.0
 * @since 27/11/2024
 */
public class ClienteDAO {

    private final Connection connection;

    // Construtor que recebe a conexão com o banco de dados
    public ClienteDAO(Connection connection) {
        this.connection = connection;
    }

    // Método para adicionar um cliente no banco de dados
    public Cliente inserir(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente(nome, telefone, email, data_cadastro) VALUES(?, ?, ?,?)";
        String sqlPF = "INSERT INTO pessoa_fisica (id_cliente, data_nascimento, cpf) VALUES((SELECT max(id) FROM cliente), ?, ?)";
        String sqlPJ = "INSERT INTO pessoa_juridica (id_cliente, inscricao_estadual, cpf_cnpj) VALUES((SELECT max(id) FROM cliente), ?, ?)";
        try {
            connection.setAutoCommit(false); // Inicia a transação

            // Armazena os dados da superclasse
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCelular());
            stmt.setString(3, cliente.getEmail());
            stmt.setDate(4, new java.sql.Date(cliente.getDataCadastro().getTime()));
            stmt.executeUpdate();

            // Recupera o ID gerado
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                cliente.setId(generatedKeys.getInt(1));
            }

            // Armazena os dados da subclasse
            if (cliente instanceof PessoaFisica) {
                stmt = connection.prepareStatement(sqlPF);
                stmt.setDate(1, new java.sql.Date(((PessoaFisica) cliente).getDataNascimento().getTime()));
                stmt.setString(2, ((PessoaFisica) cliente).getCpf());
            } else {
                stmt = connection.prepareStatement(sqlPJ);
                stmt.setString(1, ((PessoaJuridica) cliente).getInscricaoEstadual());
                stmt.setString(2, ((PessoaJuridica) cliente).getCnpj());
            }

            PontuacaoDAO pontuacaoDAO = new PontuacaoDAO(connection);
            pontuacaoDAO.inserir(cliente);

            stmt.executeUpdate();

            connection.commit(); // Confirma a transação
            return cliente;
        } catch (SQLException ex) {
            connection.rollback(); // Desfaz a transação em caso de erro
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            connection.setAutoCommit(true); // Restaura o modo de auto commit
        }
    }

    // Método para atualizar um cliente existente no banco de dados
    public boolean alterar (Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET nome=?, telefone=?, email=?, data_cadastro=? WHERE id=?";
        String sqlPF = "UPDATE  pessoa_fisica SET data_nascimento=?, cpf=? WHERE id_cliente = ?";
        String sqlPJ = "UPDATE pessoa_juridica SET inscricao_estadual, cpf_cnpj=? WHERE id_cliente = ?";
        try {
            connection.setAutoCommit(false); // Inicia a transação. Alterando para false para poder fazer um rollback e garantir a integridade dos dados e a atomicidade da transação

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCelular());
            stmt.setString(3, cliente.getEmail());
            stmt.setDate(4, new java.sql.Date(cliente.getDataCadastro().getTime()));
            stmt.setInt(5, cliente.getId());

            stmt.executeUpdate();

            if (cliente instanceof PessoaFisica) {
                stmt = connection.prepareStatement(sqlPF);
                stmt.setDate(1, new java.sql.Date(((PessoaFisica) cliente).getDataNascimento().getTime()));
                stmt.setString(2, ((PessoaFisica) cliente).getCpf());
            } else {
                stmt = connection.prepareStatement(sqlPJ);
                stmt.setString(1, ((PessoaJuridica) cliente).getInscricaoEstadual());
                stmt.setString(2, ((PessoaJuridica) cliente).getCnpj());
            }
            stmt.setInt(3, cliente.getId());
            stmt.executeUpdate();

            connection.commit(); // Confirma a transação
            PontuacaoDAO pontuacaoDAO = new PontuacaoDAO(connection);
            pontuacaoDAO.alterar(cliente);
            return true;
        } catch (SQLException ex) {
            connection.rollback(); // Desfaz a transação em caso de erro
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            connection.setAutoCommit(true); // Restaura o modo de auto commit
        }
    }

    // Método para excluir um cliente do banco de dados
    public boolean remover(Cliente cliente){
        String sql = "DELETE FROM Cliente WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cliente.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(CorDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // Método para buscar um cliente por ID
    public Cliente buscar(Cliente cliente) {
        String sql = "SELECT * FROM cliente c LEFT JOIN pessoa_fisica pf on pf.id_cliente = c.id LEFT JOIN pessoa_juridica pj on pj.id_cliente = c.id WHERE id = ?";
        Cliente retorno = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cliente.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                retorno = populateVO(rs);
            }
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(CorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    // Método para buscar um cliente por ID
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente c LEFT JOIN pessoa_fisica pf on pf.id_cliente = c.id LEFT JOIN pessoa_juridica pj on pj.id_cliente = c.id WHERE id = ?";
        Cliente retorno = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                retorno = populateVO(rs);
            }
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }

    // Método para listar todos os clientes
    public List<Cliente> listar() throws SQLException {
        List<Cliente> retorno = new ArrayList<>();
        String sql = "SELECT * FROM cliente c LEFT JOIN pessoa_fisica pf on pf.id_cliente = c.id LEFT JOIN pessoa_juridica pj on pj.id_cliente = c.id";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                retorno.add(populateVO(rs));
            }
        } catch (SQLException | ExceptionLavacao ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
    private Cliente populateVO(ResultSet rs) throws SQLException, ExceptionLavacao {
        Cliente cliente;
        if (rs.getString("cpf_cnpj") == null || rs.getString("cpf_cnpj").length() <= 0) {
            //é um cliente  pf
            cliente = new PessoaFisica();
            ((PessoaFisica)cliente).setCpf(rs.getString("cpf"));
            ((PessoaFisica)cliente).setDataNascimento(rs.getDate("data_nascimento"));
        } else {
            //é um cliente pj
            cliente = new PessoaJuridica();
            ((PessoaJuridica)cliente).setCnpj(rs.getString("cpf_cnpj"));
            ((PessoaJuridica)cliente).setInscricaoEstadual(rs.getString("inscricao_estadual"));
        }
        cliente.setId(rs.getInt("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setEmail(rs.getString("email"));
        cliente.setCelular(rs.getString("telefone"));
        cliente.setDataCadastro(rs.getDate("data_cadastro"));

        PontuacaoDAO pontuacaoDAO = new PontuacaoDAO(connection);
        cliente.setPontuacao(pontuacaoDAO.buscar(cliente));
        return cliente ;
    }
}

