package br.edu.ifsc.fln.model.domain.clientes;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.domain.veiculos.Veiculo;

import java.util.Date;

/**
* Classe para tratar de cliente pessoa fisica.<br>
* E uma das especializações da classe Cliente e deve implementar os Métodos abstratos das superclasses.
* @author  Junhaum Hayden
* @version 1.1
* @since   07/08/2024
*/
public class PessoaFisica extends Cliente {
    private String cpf;
    private Date dataNascimento;

    //construtor sobrecarregado
    /**
     * Cria um cliente com as informaçoes obrigatorias e garante que nao seja criado cliente sem informacoes.
     * @param cpf O numero do cpf valido do cliente
     * @param dataNascimento A data de nascimento do cliente.
     */
    public PessoaFisica(String nome, String celular, String email, String cpf, Date dataNascimento) {
        super(nome, celular, email);
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
    }

    //construtor vazio
    public PessoaFisica() {

    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf)throws ExceptionLavacao {
        if (cpf == null || cpf.isEmpty()) {
            throw new ExceptionLavacao("O CPF não pode ser vazio.");
        }
        this.cpf = cpf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String addVeiculos(Veiculo veiculo) throws ExceptionLavacao {
        return super.addVeiculos(veiculo);
    }

    @Override
    public void removeVeiculos(Veiculo veiculo) throws ExceptionLavacao {
        super.removeVeiculos(veiculo);
    }

    /**
     * A classe PessoaFisica herda de Cliente e deve fornecer a implementação do método getDados(String observacao).
     *
     * @param observacao A Observacao que se dejesa inserir no retorno dos dados
     * @return As informaçoes referentes ao cliente.
     */
    @Override
    public String getDados(String observacao) {
        StringBuilder sb = new StringBuilder();
//        sb.append(super.getDados()).append("\n");
        sb.append("CPF........: ").append(cpf).append("\n");
        sb.append("Data de nascimento........: ").append(dataNascimento).append("\n");
        sb.append("Observação.....:").append(observacao);
        return sb.toString();
    }

    // Sobrescrita do método toString para incluir os atributos adicionais
    @Override
    public String getDados() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDados()).append("\n");
        sb.append("CPF........: ").append(cpf).append("\n");
        sb.append("Data de nascimento........: ").append(dataNascimento).append("\n");
        return sb.toString();
    }
}

