package br.edu.ifsc.fln.model.domain.veiculos;

public class Cor {
    private int id;
    private String nome;

    // Construtor
    public Cor(){
    }

    public Cor(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Cor(int id) {
        this.id = id;
    }

    public Cor(String nome) {
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

    //O método toString() para fornecer uma representação de string da instância da classe Marca.
    @Override
    public String toString()
    {
        return nome;
    }
}
