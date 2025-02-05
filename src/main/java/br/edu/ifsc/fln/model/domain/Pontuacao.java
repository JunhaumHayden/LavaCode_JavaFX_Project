package br.edu.ifsc.fln.model.domain;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;

/**
* <h1>Pontuacao</h1>
* Classe para gerenciar a pontuação de fidelidade dos clientes.
* 
* @author Junhaum Hayden
* @version 1.2
* @since 04/02/2025
*/
public class Pontuacao {
    private int idCliente;
    private int quantidade;

    // Construtor sempre inicializa com zero
    public Pontuacao() {
        this.quantidade = 0;
    }

    public Pontuacao(int idCliente, int pontos) {
    }

    //para poder passar uma quantidade de pontos na instanciacao do objeto é necessario ter um construtor que aceite a instanciaçao com um valor como abaixo
    // public Pontuacao(int quantidade) {
    //     this.quantidade = quantidade;
    // }

    // Método para adicionar quantidade
    public void adicionarPontos(int pontos) throws ExceptionLavacao {
        if (pontos < 0) {
            throw new ExceptionLavacao("Quantidade de pontos a adicionar não pode ser negativa.");
        }
        this.quantidade += pontos;
    }

    // Método para subtrair quantidade
    public void subtrairPontos(int pontos) throws ExceptionLavacao {
        if (pontos < 0) {
            throw new ExceptionLavacao("Quantidade de pontos a subtrair não pode ser negativa.");
        }
        if (pontos > quantidade) {
            throw new ExceptionLavacao("Saldo de pontos insuficiente.");
        }
        this.quantidade -= pontos;
    }

    public int setSaldo(int quantidade) {
        return this.quantidade = quantidade;
    }

    // Método para obter saldo
    public int getSaldo() {
        return quantidade;
    }

    @Override
    public String toString() {
        return "Pontuacao [quantidade=" + quantidade + "]";
    }
    


}

