package br.edu.ifsc.fln.model.domain.clientes;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.domain.*;
import br.edu.ifsc.fln.model.domain.veiculos.Veiculo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Classe para garantir que todos os clientes tenham informaçoes minimmas no sistema.<br>
* A classe Cliente é uma classe abstrata que implementa a interface ICliente. Isso significa que ela pode fornecer implementações parciais ou completas dos métodos da interface, mas como é abstrata, não precisa fornecer todas as implementações. As subclasses concretas (não abstratas) terão que completar a implementação.
* @author  Junhaum Hayden
* @version 1.2
* @since   07/08/2024
*/
public abstract class Cliente implements ICliente
{
    protected int id; // Será gerado pelo banco de dados
    protected String nome;
    protected String celular;
    protected String email;
    protected Date dataCadastro;
    protected Pontuacao pontuacao = new Pontuacao(); //Para configurar uma composicao precisa instanciar no momento da criacao
    protected List<Veiculo> listaVeiculos;
    
    //construtor padrao
    /**
     * Gera automaticamente um ID de cliente unico a cada nova instancia da classe e atribui a data e inicializa a lista de veiculos. 
     */
    public Cliente()
    {
        this.listaVeiculos = new ArrayList<>();
        this.dataCadastro = new Date(); // Atribui a data e hora atuais
    }

    //construtor sobrecarregado
    /**
     * Cria um cliente com as informaçoes obrigatorias e chama, automaticamente o construtor padrao para gerar o ID de cliente unico.
     *
     * @param nome Nome completo do cliente
     * @param celular O numero do celular do cliente
     * @param email email valido do cliente
     * 
     */
    public Cliente(String nome, String celular, String email) 
    {
        this(); //chama o construtor padrao para gerar id
        this.nome = nome;
        this.celular = celular;
        this.email = email;
        
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro =dataCadastro;
    }

    public void setPontuacao(Pontuacao pontuacao) {
        this.pontuacao = pontuacao;
    }
    public Pontuacao getPontuacao() 
    {
        return pontuacao;
    }
    /**
     * Vincula um veiculo ao cliente adicionando-o a uma lista.
     * Garantindo que não será adicionado um veiculo que já esteja vinculado a outro cliente e vinculando o veiculo ao cliente.
     * @param veiculo Recebe o veiculo a ser adicionado, é um objeto do tipo Veiculo.
     * @return Uma String com uma mensagem se houve sucesso ou se não lança uma excecao. 
     */
    public String addVeiculos(Veiculo veiculo) throws ExceptionLavacao {
        if (veiculo == null) {
            throw new ExceptionLavacao("Veículo não pode ser nulo.");
        }
        if (veiculo.getCliente() == this) {
            listaVeiculos.add(veiculo);
//            veiculo.setCliente(this);
            return "Veículo adicionado ao cliente com sucesso!";
        } else {
            throw new ExceptionLavacao("Este veículo já possui um cliente associado.");
        }
    }
    /**
     * Desvincula um veiculo de um cliente, retirando-o da lista.
     * Desvincula o cliente do veiculo também.
     * @param veiculo Recebe o veiculo a ser removido, é um objeto do tipo Veiculo 
     * 
     */
    public void removeVeiculos(Veiculo veiculo) throws ExceptionLavacao{
        if (veiculo == null) {
            throw new ExceptionLavacao("Veículo não pode ser nulo.");
        }
        if (veiculo.getCliente() != this) {
            throw new ExceptionLavacao("Veículo não pertence a este cliente."); 
        } else {
            listaVeiculos.remove(veiculo);
//            veiculo.setCliente(null);
        }
    }

    //Declaração do método para listar todos os produtos (opção 5)

    /**
     * Retorna a lista de veiculos do cliente.
     *  
     * @return A lista com os veiculos. É uma lista de Objetos 
     */
    public List<Veiculo> getVeiculos()
    {
        return listaVeiculos;
    }

    //O método toString() para fornecer uma representação de string da instância.
    @Override
    public String toString()
    {
        return nome;
    }
    // Sobrescrever metodos obrigatórios
    /**
     * Implementação parcial do método getDados
     *
     * @return As informaçoes referentes ao cliente.
     * 
     */
    @Override
    public String getDados() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dados do Cliente: \n");
        sb.append("Nome...........: ").append(nome).append("\n");
        sb.append("Fone........: ").append(celular).append("\n");
        sb.append("Email..........: ").append(email).append("\n");
        sb.append("Data Cadastro..: ").append(dataCadastro).append("\n");
        sb.append("Pontuação......: ").append(pontuacao).append("\n");
        sb.append("Veiculo........: ").append(this.getVeiculos());
        return sb.toString();
    }
    /**
     * Metodo abstrato que deve ser implementado pelas subclasses
     *
     * @param observacao A Observacao que se dejesa inserir no retorno dos dados
     * 
     */
    @Override
    public abstract String getDados(String observacao);
}
