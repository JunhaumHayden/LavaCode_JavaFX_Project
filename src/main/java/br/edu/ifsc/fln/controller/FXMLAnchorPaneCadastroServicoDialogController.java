package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.domain.ECategoria;
import br.edu.ifsc.fln.model.domain.ordemServicos.Servico;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroServicoDialogController implements Initializable {

    @FXML
    private ChoiceBox<ECategoria> cbECategoria;
    @FXML
    private Button btCancelar;
    @FXML
    private Button btConfirmar;

    @FXML
    private TextField tfPontos;

    @FXML
    private TextField tfValor;

    @FXML
    private TextField tfServicoDescricao;

    private Stage dialogStage;
    private boolean btConfirmarClicked = false;
    private Servico servico;


    public Servico getServico() {
        return servico;
    }
    public void setServico(Servico servico) {
        this.servico = servico;
        tfServicoDescricao.setText(servico.getDescricao());
        tfPontos.setText(Integer.toString(servico.getPontos()));
        if (servico.getValor() != null) {
            tfValor.setText(servico.getValor().toString());
        } else {
            tfValor.setText("");
        }
        cbECategoria.getSelectionModel().select(servico.getCategoria());

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        carregarChoiceBoxCategoria();
        setFocusLostHandle();
    }

    @FXML
    void handleBtCancelar(){
        dialogStage.close();
    }

    @FXML
    private void handleBtConfirmar() {
        if (validarEntradaDeDados()) {
            servico.setDescricao(tfServicoDescricao.getText());
            servico.setPontos(Integer.parseInt(tfPontos.getText()));
            servico.setCategoria(cbECategoria.getSelectionModel().getSelectedItem());
            servico.setValor(new BigDecimal(tfValor.getText()));

            btConfirmarClicked = true;
            dialogStage.close();
        }
    }
    // Metodos auxiliares
    private void setFocusLostHandle() {
        tfServicoDescricao.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                if (tfServicoDescricao.getText() == null || tfServicoDescricao.getText().isEmpty()) {
                    tfServicoDescricao.requestFocus();
                }
            }
        });
    }
    public void carregarChoiceBoxCategoria() {
        cbECategoria.setItems( FXCollections.observableArrayList( ECategoria.values()));
    }

    //validar entrada de dados do cadastro
    private boolean validarEntradaDeDados() {
        String errorMessage = "";

        if (tfServicoDescricao.getText() == null || tfServicoDescricao.getText().isEmpty()) {
            errorMessage += "Descrição inválido!\n";
        }

        if (tfValor.getText() == null || tfValor.getText().isEmpty()) {
            errorMessage += "Valor inválido!\n";
        }

        if (cbECategoria.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Selecione uma categoria!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campo(s) inválido(s), por favor corrija...");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }

    public boolean isButtonConfirmarClicked() {
        return btConfirmarClicked;
    }
}
