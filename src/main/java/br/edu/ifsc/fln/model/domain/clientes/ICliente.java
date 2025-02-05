package br.edu.ifsc.fln.model.domain.clientes;

/**
* Esta é uma Interface de Cliente.<br>
* Uma Interface é um contrato que define um conjunto de métodos que uma classe deve implementar. As interfaces são usadas para definir comportamentos que podem ser compartilhados por classes diferentes, sem forçar uma relação de herança entre elas. Elas permitem a criação de código mais flexível e desacoplado. 
*
* Benefícios de Usar Interfaces
Desacoplamento: Interfaces permitem que diferentes partes de um programa sejam desenvolvidas e modificadas independentemente.
Flexibilidade: Uma classe pode implementar múltiplas interfaces, permitindo a combinação de comportamentos de maneiras diferentes.
Testabilidade: Interfaces facilitam a criação de mocks e stubs para testes unitários, pois permitem a substituição de implementações reais por implementações simuladas.
Em resumo, interfaces são uma ferramenta poderosa em Java para definir contratos de comportamento que classes podem implementar, promovendo a reutilização de código e o design orientado a objetos.
*
* @author  Junhaum Hayden
* @version 1.0
* @since   07/08/2024
* @see String #Observacao
*/

public interface ICliente {

    /**
     * Este metodo retorna uma string que representa os dados do cliente
     *
     */

    public String getDados();

     /**
     * Este metodo retorna uma string que representa os dados do cliente, possivelmente incluindo alguma observação específica passada como argumento.
     * @param Observacao A Observacao que se deseja inserir no retorno dos dados
     */

     public String getDados(String Observacao);
}
