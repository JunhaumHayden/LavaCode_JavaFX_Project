package br.edu.ifsc.fln.model.domain.veiculos;

public class Motor {
    private int id;
    private int potencia;
    private ETipoCombustivel tipoCombustivel;

    // Construtor padrão
    public Motor() {
    }

    // Construtor
    public Motor(int id, int potencia, ETipoCombustivel tipoCombustivel) {
        this.id = id;
        this.potencia = potencia;
        this.tipoCombustivel = tipoCombustivel;
    }

    // Getters e Setters
    public int getPotencia() {
        return potencia;
    }

    public void setPotencia(int potencia) {
        this.potencia = potencia;
    }

    public ETipoCombustivel getTipoCombustivel() {
        return tipoCombustivel;
    }

    public void setTipoCombustivel(ETipoCombustivel tipoCombustivel) {
       this.tipoCombustivel = tipoCombustivel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return tipoCombustivel.toString();
    }
}
