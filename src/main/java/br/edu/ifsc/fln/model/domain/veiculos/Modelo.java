package br.edu.ifsc.fln.model.domain.veiculos;

import br.edu.ifsc.fln.model.domain.ECategoria;

public class Modelo {
    private int id;
    private String descricao;
    private Marca marca; // Adicionando referência para a classe Marca
    private ECategoria categoria;
    private Motor motor = new Motor(); //Para configurar uma composicao precisa instanciar no momento da criacao

    // Construtor padrão
    public Modelo() {
    }

    public Modelo(int id) {
        this.id = id;
    }
    /**
     * Construtor sobre carregado chama o construtor padrão (this()) para gerar o id e, em seguida, o nome da marca e uma descricao do Modelo.
     *
     * @param id
     * @param descricao Adiciona uma descriçao do modelo do veiculo
     * @param marca     Vincula a marca.
     */
    public Modelo(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }
    public Modelo(int id, String descricao, Marca marca) {
        this.id = id;
        this.descricao = descricao;
        this.marca = marca;
    }
    /**
     * Construtor sobregarregado 2 para exercitar uma chamada a outro construtor sobrecarregado.
     *
     * @param marca Nome completo do cliente.
     * @param descricao Adiciona uma descriçao caso necessário.
     * @param categoria Selecao de categotia referenteao modelo.
     * @param potencia Adiciona a pontencia do Moto.
     * @param EtipoCombustivel Selecao do tipo de Combistivel.
     * 
     */
    public Modelo(int id, String descricao, Marca marca, ECategoria categoria, int potencia, ETipoCombustivel tipoCombustivel)
    {
        this(id, descricao, marca);
        this.categoria = categoria;
        this.motor.setPotencia(potencia);
        this.motor.setTipoCombustivel(tipoCombustivel);
    }

    public Modelo(int id, String descricao, Marca marca, Motor motor, ECategoria categoria) {
        this(id, descricao, marca);
        this.categoria = categoria;
        this.motor = motor;
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

    public Marca getMarca(){
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public ECategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(ECategoria categoria) {
        this.categoria = categoria;
    }

    public Motor getMotor(){
        return motor;
    }
    //Para configurar uma composicao precisa instanciar no momento da criacao de maneira que nao e aceitavel realizar alteracao posterior
   // public void setMotor(Motor motor) {
   //     this.motor = motor;
   // }

    public String getNomeMarca() {
        return marca != null ? marca.getNome() : "";
    }

    @Override
    public String toString() {
        return  descricao;
    }
}

