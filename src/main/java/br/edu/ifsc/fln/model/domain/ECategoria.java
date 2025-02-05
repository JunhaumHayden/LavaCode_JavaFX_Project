package br.edu.ifsc.fln.model.domain;
// É um tipo especial de classe onde os objetos são previamente criados, imutaveis e disponiveis por toda aplicacao
public enum ECategoria
{
    //Declarando o conjunto de objetos pre-definidos
    PEQUENO("Pequeno"),
    MEDIO("Medio"),
    GRANDE("Grande"),
    MOTO("Moto"),
    PADRAO("Padrão");

    //Declarando os atributos
    private String descricao;

    //Declarando o metodo construtor - PRIVADO pq não é permitido que seja criado novos objetos pela aplicaçao
    private ECategoria(String descricao) {
     this.descricao = descricao;
    }

    //Declaracao dos metodos manipulados pela aplicacao
    public String getDescricao()
    {
        return descricao;
    }

}
