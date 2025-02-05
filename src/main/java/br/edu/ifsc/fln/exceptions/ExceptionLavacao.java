package br.edu.ifsc.fln.exceptions;
/**
* <h1>ExceptionLavacao</h1>
* Classe para representar exceções personalizadas no sistema de lavação.
* 
* @author Junhaum Hayden
* @version 1.0
* @since 07/08/2024
*/

public class ExceptionLavacao extends Exception{

    public ExceptionLavacao(String mensagem) {
        super(mensagem);
    }

    public ExceptionLavacao(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
