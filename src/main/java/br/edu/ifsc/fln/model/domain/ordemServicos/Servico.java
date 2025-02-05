package br.edu.ifsc.fln.model.domain.ordemServicos;

import br.edu.ifsc.fln.model.domain.ECategoria;
import java.math.BigDecimal;

/**
* Classe para tratar dos servicos existentes.<br>
* @author  Junhaum Hayden
* @version 1.2
* @since   07/08/2024
* 
*/
public class Servico {
    private int id;
    private String descricao;
    private BigDecimal valor;
    private int pontos;
    private ECategoria categoria;

    //construtor padrao
    public Servico() {
    }
    /**
     * /**
     * Construtor completo.
     *
     * @param id Identificador único do serviço.
     * @param descricao Descrição do serviço.
     * @param valor Valor do serviço.
     * @param pontos Pontuação associada.
     * @param categoria Categoria do serviço.
     *      */

    public Servico(int id, String descricao, BigDecimal valor, int pontos, ECategoria categoria) {
        this.id = id;
        this.descricao = descricao;
        setValor(valor);
        setPontos(pontos);
        this.categoria = categoria;
    }

    public Servico(String descricao, BigDecimal valor, int pontos, ECategoria categoria) {
        this.descricao = descricao;
        setValor(valor);
        setPontos(pontos);
        this.categoria = categoria;
    }


    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor do serviço não pode ser negativo.");
        }
        this.valor = valor;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        if (pontos < 0) {
            throw new IllegalArgumentException("A pontuação não pode ser negativa.");
        }
        this.pontos = pontos;
    }

    public ECategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(ECategoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Servico servico = (Servico) obj;
        return id == servico.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

   
    
}

