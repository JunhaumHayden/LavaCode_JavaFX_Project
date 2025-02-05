
package br.edu.ifsc.fln.model.domain.veiculos;

/**
 * Classe que representa uma marca de veiculos.
 * Ela contém informações sobre a identificação e o nome da marca.
 */

public class Marca {
    private int id;
    private String nome;

    // Construtores
    public Marca() {}

    public Marca(String nome) {
        this.nome = nome;
    }

    public Marca(int id, String nome) {
        this.id = id;
        this.nome = nome;
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
    @Override
    public String toString() {
        return nome;
    }
}

