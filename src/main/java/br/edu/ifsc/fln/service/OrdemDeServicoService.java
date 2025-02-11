package br.edu.ifsc.fln.service;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.domain.ordemServicos.OrdemDeServico;
import br.edu.ifsc.fln.model.domain.ordemServicos.EStatus;
import br.edu.ifsc.fln.model.domain.ordemServicos.ItemDeOrdemDeServico;
import br.edu.ifsc.fln.utils.AlertDialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h1>OrdemDeServicoService</h1>
 * Classe para representar as regras de negocio de uma ordem de serviço no sistema de lavação.
 *
 * @author Junhaum Hayden
 * @version 1.0
 * @since 03/02/2025
 */
public class OrdemDeServicoService {
    private static final Random random = new Random();

    /**
     * Gerador de numero de OS
     * Gera a parte da data e hora no formato yyyy/MM/dd-H:mm.
     * Gera um número único de 5 dígitos usando um Random.
     * Combina a data/hora com o número único para formar o número completo da ordem de serviço.
     * @return Retorna uma String formando um numero unico de cada OS.
     *
     */
    public static String gerarNumeroOrdem() {
        String dataHora = new SimpleDateFormat("yyyy.MMdd-HHmmss").format(new Date());
        String numeroUnico = String.format("%05d", random.nextInt(100000));
        return dataHora + "/" + numeroUnico;
    }

    public static BigDecimal calcularServico(OrdemDeServico os) {
        BigDecimal totalServico = BigDecimal.ZERO;
        for (ItemDeOrdemDeServico item : os.getItens()) {
            totalServico = totalServico.add(item.getValorServico());
        }
        return totalServico.subtract(os.getDesconto());
    }

    public static int calcularPontos(OrdemDeServico os) {
        int totalPontos = 0;
        for (ItemDeOrdemDeServico item : os.getItens()) {
            totalPontos += item.getServico().getPontos();
        }
        return totalPontos;
    }

    public static boolean podeAlterar(OrdemDeServico os) {
        return os.getStatus() != EStatus.FECHADA;
    }

    public static EStatus setStatus(OrdemDeServico os, EStatus status) throws ExceptionLavacao {
        try {
            if (!podeAlterar(os)) {
                throw new ExceptionLavacao("OS Fechada não pode ser alterada.");
            }
        } catch (ExceptionLavacao ex) {
            Logger.getLogger(OrdemDeServicoService.class.getName()).log(Level.SEVERE, "OS Fechada não pode ser alterada.", ex);
            AlertDialog.exceptionMessage(ex);
        return os.getStatus();
        }
        if (status == EStatus.FECHADA) {
            os.getVeiculo().getCliente().getPontuacao().adicionarPontos(calcularPontos(os));
        }
        return status;
    }
}
