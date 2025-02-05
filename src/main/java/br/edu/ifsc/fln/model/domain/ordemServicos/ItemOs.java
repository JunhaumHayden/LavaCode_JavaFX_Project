package br.edu.ifsc.fln.model.domain.ordemServicos;

import java.math.BigDecimal;

/**
* Classe para vincular um item de servico com a ordem de servico.<br>
* 
* @author  Junhaum Hayden
* @version 1.0
* @since   04/02/2025
* 
*/

public class ItemOs {
    private int id;
    private String observacao;
    private BigDecimal valorServico = BigDecimal.ZERO;
    private Servico servico;
    private OrdemDeServico ordemDeServico;

    // Construtores
    public ItemOs() {}
    //construtor sobrecarregado
    /**
     * Cria um item de OS vinculando um servico a uma ordem de servico e armazena o valor do servico para ser usado na Ordem de Servico.
     *
     * @param observacao String com uma observacao referente a algo pertimente ao serviço.
     * @param servico Um objeto do tipo Servico será vinculado a uma Ordem de Servico.
     * 
     */
    public ItemOs(String observacao, Servico servico) {
        this.observacao = observacao;
        this.servico = servico;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public OrdemDeServico getOrdemDeServico() {
        return ordemDeServico;
    }

    public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
        this.ordemDeServico = ordemDeServico;
    }

    @Override
    public String toString() {
        return  "\n       Servico.........:" + servico +
                "\n       Valor do Servico: R$" + valorServico +
                "\n       Observacao......:" + observacao + "\n"
                ;
    }

    
}

