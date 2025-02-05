/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.clientes.ClienteDAO;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import br.edu.ifsc.fln.model.domain.clientes.Cliente;
import br.edu.ifsc.fln.model.domain.clientes.PessoaFisica;
import br.edu.ifsc.fln.model.domain.clientes.PessoaJuridica;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mpisc
 */
public class FXMLAnchorPaneCadastroClienteDialogController implements Initializable {

    @FXML
    private Button btCancelar;

    @FXML
    private Button btConfirmar;

    @FXML
    private RadioButton rbPessoaJuridica;

    @FXML
    private RadioButton rbPessoaFisica;

    @FXML
    private Group gbTipo;

    @FXML
    private ToggleGroup tgTipo;

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfFone;

    @FXML
    private TextField tfNome;

    @FXML
    private TextField tfNumFiscal;

    @FXML
    private TextField tfTipo;

    @FXML
    private Label lbClienteEtiquetaDado;

    @FXML
    private TextField tfDado;

    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Cliente cliente;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public boolean isBtConfirmarClicked() {
        return btConfirmarClicked;
    }

    public void setBtConfirmarClicked(boolean btConfirmarClicked) {
        this.btConfirmarClicked = btConfirmarClicked;
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;

        this.tfNome.setText(this.cliente.getNome());
        this.tfEmail.setText(this.cliente.getEmail());
        this.tfFone.setText(this.cliente.getCelular());
        if (cliente instanceof PessoaFisica) {
//            rbPessoaFisica.setSelected(true);
            tfNumFiscal.setText(((PessoaFisica) this.cliente).getCpf());
            tfTipo.setText("Pessoa Física");
            lbClienteEtiquetaDado.setText("Data Nascimento:");
            tfDado.setText(((PessoaFisica) this.cliente).getDataNascimento() != null ? ((PessoaFisica) this.cliente).getDataNascimento().toString() : "");
        } else {
//            rbPessoaJuridica.setSelected(true);
            tfNumFiscal.setText(((PessoaJuridica) this.cliente).getCnpj());
            tfTipo.setText("Pessoa Jurídica");
            lbClienteEtiquetaDado.setText("Inscrição Estadual:");
            tfDado.setText(((PessoaJuridica) this.cliente).getInscricaoEstadual());
        }
        this.tfNome.requestFocus();
    }

    @FXML
    public void handleBtConfirmar() throws ExceptionLavacao, ParseException {
        if (validarEntradaDeDados()) {
            cliente.setNome(tfNome.getText());
            cliente.setEmail(tfEmail.getText());
            cliente.setCelular(tfFone.getText());
            if (cliente instanceof PessoaFisica) {
                ((PessoaFisica) cliente).setCpf(tfNumFiscal.getText());
                ((PessoaFisica) cliente).setDataNascimento(new SimpleDateFormat("dd/MM/yyyy").parse(tfDado.getText()));

            } else {
                ((PessoaJuridica) cliente).setCnpj(tfNumFiscal.getText());
                ((PessoaJuridica) cliente).setInscricaoEstadual(tfDado.getText());
            }
            btConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    public void handleBtCancelar() {
        dialogStage.close();
    }

//    @FXML
//    public void handleRbPessoaFisica() {
//        this.tfPais.setText("BRASIL");
//        this.tfPais.setDisable(true);
//    }
//
//    @FXML
//    public void handleRbPessoaJuridica() {
//        this.tfPais.setText("");
//        this.tfPais.setDisable(false);
//    }

    //método para validar a entrada de dados
    private boolean validarEntradaDeDados() {
        String errorMessage = "";
        if (this.tfNome.getText() == null || this.tfNome.getText().length() == 0) {
            errorMessage += "Nome inválido.\n";
        }

        if (this.tfFone.getText() == null || this.tfFone.getText().length() == 0) {
            errorMessage += "Telefone inválido.\n";
        }

        if (this.tfEmail.getText() == null || this.tfEmail.getText().length() == 0 || !this.tfEmail.getText().contains("@")) {
            errorMessage += "Email inválido.\n";
        }

        if (cliente instanceof PessoaFisica) {
            if (this.tfNumFiscal.getText() == null || this.tfNumFiscal.getText().length() == 0) {
                errorMessage += "CPF inválido.\n";
            }
            if (this.tfDado.getText() == null || this.tfDado.getText().length() == 0) {
                errorMessage += "Data de nascimento inválida.\n";
            }
        } else {
            if (this.tfNumFiscal.getText() == null || this.tfNumFiscal.getText().length() == 0) {
                errorMessage += "CNPJ inválido.\n";
            }
            if (this.tfDado.getText() == null || this.tfDado.getText().length() == 0) {
                errorMessage += "Inscrição Estadual inválida.\n";
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            //exibindo uma mensagem de erro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Corrija os campos inválidos!");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }

}
