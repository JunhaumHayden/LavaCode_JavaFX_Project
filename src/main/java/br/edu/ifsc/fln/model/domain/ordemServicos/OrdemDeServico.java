package br.edu.ifsc.fln.model.domain.ordemServicos;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.domain.veiculos.Veiculo;
import br.edu.ifsc.fln.service.OrdemDeServicoService;
import br.edu.ifsc.fln.utils.AlertDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* <h1>OrdemDeServico</h1>
* Classe para representar uma ordem de serviço no sistema de lavação.
* 
* @author Junhaum Hayden
* @version 2.0
* @since 03/02/2025
*/
public class OrdemDeServico {
    private int id;
    private String numero;
    private BigDecimal total;
    private BigDecimal desconto;
    private Date agenda;
    private EStatus status;
    private List<ItemOs> itens;
    private Veiculo veiculo;

    //construtor padrao
    /**
     * Instancia a lista de itens que irá compor a OS e o estado para Aberta
     */
    public OrdemDeServico() {
        this.itens = new ArrayList<>();
        this.agenda = new Date();
        this.status = EStatus.ABERTA;
        this.total = BigDecimal.ZERO;
    }

    //construtor sobrecarregado
    /**
     * Cria uma nova OS
     *
     * @param desconto Pode haver um valor de desconto na OS
     * @param veiculo veiculo vinculado a OS
     * 
     */
    public OrdemDeServico(BigDecimal desconto, Veiculo veiculo) {
        this();
        this.desconto = desconto;
        this.veiculo = veiculo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getTotal() {
        return total;
    }

    // Metodo Private para ser usado apenas quando o atributo item for alterado
    private void setTotal() {
        this.total = OrdemDeServicoService.calcularServico(this);
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto){
        try {
            if (!OrdemDeServicoService.podeAlterar(this)) {
                throw new ExceptionLavacao("OS Fechada não pode ser alterada.");
            }
        } catch (ExceptionLavacao ex) {
            AlertDialog.exceptionMessage(ex);
        }
        this.desconto = desconto;
    }

    public Date getAgenda() {
        return agenda;
    }

    public void setAgenda(Date agenda)throws ExceptionLavacao {
        try {
            if (!OrdemDeServicoService.podeAlterar(this)) {
                throw new ExceptionLavacao("OS Fechada não pode ser alterada.");
            }
        } catch (ExceptionLavacao ex) {
            AlertDialog.exceptionMessage(ex);
        }
        this.agenda = agenda;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) throws ExceptionLavacao {
        this.status = OrdemDeServicoService.setStatus(this, status);
        this.setTotal();
    }

    public List<ItemOs> getItens() {
        return itens;
    }

    public void addItemOS(ItemOs itemOS) throws ExceptionLavacao {
        try {
            if (!OrdemDeServicoService.podeAlterar(this)) {
                throw new ExceptionLavacao("OS Fechada não pode ser alterada.");
            }
        } catch (ExceptionLavacao ex) {
            AlertDialog.exceptionMessage(ex);
        }
        itemOS.setOrdemDeServico(this); // Vincula a ordem de serviço ao item
        this.itens.add(itemOS);
        this.setTotal();
    }

    public void removeItemOS(ItemOs itemOS) throws ExceptionLavacao {
        try {
            if (!OrdemDeServicoService.podeAlterar(this)) {
                throw new ExceptionLavacao("OS Fechada não pode ser alterada.");
            }
        } catch (ExceptionLavacao ex) {
            AlertDialog.exceptionMessage(ex);
        }
        this.itens.remove(itemOS);
        this.setTotal();
    }

    public void setItens(List<ItemOs> itens) throws ExceptionLavacao {
        try {
            if (!OrdemDeServicoService.podeAlterar(this)) {
                throw new ExceptionLavacao("OS Fechada não pode ser alterada.");
            }
        } catch (ExceptionLavacao ex) {
            AlertDialog.exceptionMessage(ex);
        }
        this.itens = itens;
        this.setTotal();
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) throws ExceptionLavacao {
        try {
            if (!OrdemDeServicoService.podeAlterar(this)) {
            throw new ExceptionLavacao("OS Fechada não pode ser alterada.");
            }
            if (this.veiculo != null) {
                throw new ExceptionLavacao("OS já esta vinculada ao veiculo " + this.getVeiculo().getPlaca());
            }
        } catch (ExceptionLavacao ex) {
            AlertDialog.exceptionMessage(ex);
        }
        this.veiculo = veiculo;
    }

    //O método toString() para fornecer uma representação de string da instância.
    @Override
    public String toString()
    {
        return numero;
    }

}

