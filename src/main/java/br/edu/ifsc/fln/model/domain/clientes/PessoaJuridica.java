package br.edu.ifsc.fln.model.domain.clientes;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.domain.veiculos.Veiculo;

/**
* 
* Classe para tratar de clientes pessoa juridica.
* E uma das especializações da classe Cliente e deve implementar os Métodos abstratos das superclasses.
* @author  Junhaum Hayden
* @version 1.1
* @since   07/08/2024
*/
public class PessoaJuridica extends Cliente {
    private String cnpj;
    private String inscricaoEstadual;

    //construtor sobrecarregado
    /**
     * Cria um cliente com as informaçoes obrigatorias e garante que nao seja criado cliente sem informacoes.
     * @param cnpj O numero do cnpj valido do cliente.
     * @param inscricaoEstadual O numero da Inscricao Estadual valida do cliente.
     */
    public PessoaJuridica(String nome, String celular, String email, String cnpj, String inscricaoEstadual) {
        super(nome, celular, email);
        this.cnpj = cnpj;
        this.inscricaoEstadual = inscricaoEstadual;
    }

    //construtor vazio
    public PessoaJuridica() {

    }

    // Métodos getters e setters para os atributos específicos de Pessoa Jurídica
    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) throws ExceptionLavacao {
        if (cnpj == null || cnpj.isEmpty()) {
            throw new ExceptionLavacao("O CNPJ não pode ser vazio.");
        }
        this.cnpj = cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) throws ExceptionLavacao {
        if (inscricaoEstadual == null || inscricaoEstadual.isEmpty()) {
            throw new ExceptionLavacao("O CNPJ não pode ser vazio.");
        }
        this.inscricaoEstadual = inscricaoEstadual;
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
     * A classe PessoaJuridica herda de Cliente e fornece sua própria implementação do método getDados(String observacao).
     *
     * @param observacao A Observacao que se dejesa inserir no retorno dos dados
     * @return As informaçoes referentes ao cliente PJ.
     */
    @Override
    public String getDados(String observacao) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDados()).append("\n");
        sb.append("CNPJ........: ").append(cnpj).append("\n");
        sb.append("Inscrição Estadual: ").append(inscricaoEstadual).append("\n");
        sb.append("Observação.....:").append(observacao);
        return sb.toString();
    }

    // Sobrescrita do método toString para incluir os atributos adicionais
    @Override
    public String getDados() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getDados()).append("\n");
        sb.append("CNPJ........: ").append(cnpj).append("\n");
        sb.append("Inscrição Estadual: ").append(inscricaoEstadual).append("\n");
        return sb.toString();
    }
}

