package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.veiculos.MarcaDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.ECategoria;
import br.edu.ifsc.fln.model.domain.veiculos.ETipoCombustivel;
import br.edu.ifsc.fln.model.domain.veiculos.Marca;
import br.edu.ifsc.fln.model.domain.veiculos.Modelo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroModeloDialogController implements Initializable {


    @FXML
    private ChoiceBox<ECategoria> cbECategoria;
    @FXML
    private ChoiceBox<ETipoCombustivel> cbETipoCombustivel;
    @FXML
    private ComboBox<Marca> cbMarca;
    @FXML
    private Button btCancelar;
    @FXML
    private Button btConfirmar;

    @FXML
    private TextField tfModeloDescricao;
    @FXML
    private TextField tfPotencia;



    private List<Marca> listaMarcas;
    private ObservableList<Marca> observableListMarcas;
    //acesso ao banco de dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final MarcaDAO marcaDAO = new MarcaDAO();

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private Modelo modelo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        marcaDAO.setConnection(connection);
        carregarComboBoxMarcas();
        carregarChoiceBoxCategoria();
        carregarChoiceBoxETipoCombustivel();
        setFocusLostHandle();
    }
    // Getter and Setter

    /**
     *
     * @return the dialogStage
     */
    public Stage getDialogStage() {
        return dialogStage;
    }
    /**
     * @param dialogStage the dialogStage to set
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    /**
     * @return the buttonConfirmarClicked
     */
    public boolean isButtonConfirmarClicked() {
        return buttonConfirmarClicked;
    }

    /**
     * @param buttonConfirmarClicked the buttonConfirmarClicked to set
     */
    public void setButtonConfirmarClicked(boolean buttonConfirmarClicked) {
        this.buttonConfirmarClicked = buttonConfirmarClicked;
    }
    /**
     * @return the modelo
     */
    public Modelo getModelo() {
        return modelo;
    }
    /**
     * @param modelo the modelo to set
     */
    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
        tfModeloDescricao.setText(modelo.getDescricao());
        if (modelo.getMotor() != null) {
            tfPotencia.setText(Integer.toString(modelo.getMotor().getPotencia()));
        } else {
            tfPotencia.setText("");
        }
        cbMarca.getSelectionModel().select(modelo.getMarca());
        cbECategoria.getSelectionModel().select(modelo.getCategoria());
        cbETipoCombustivel.getSelectionModel().select(modelo.getMotor().getTipoCombustivel());
    }



    @FXML
    private void handleBtCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleBtConfirmar() {
        if (validarEntradaDeDados()) {
            modelo.setDescricao(tfModeloDescricao.getText());
            modelo.getMotor().setPotencia(Integer.parseInt(tfPotencia.getText()));
            modelo.setCategoria(cbECategoria.getSelectionModel().getSelectedItem());
            modelo.getMotor().setTipoCombustivel(cbETipoCombustivel.getSelectionModel().getSelectedItem());
            modelo.setMarca(cbMarca.getSelectionModel().getSelectedItem());

            buttonConfirmarClicked = true;
            dialogStage.close();
        }
    }

    // Metodos auxiliares
    private void setFocusLostHandle() {
        tfModeloDescricao.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                if (tfModeloDescricao.getText() == null || tfModeloDescricao.getText().isEmpty()) {
                    tfModeloDescricao.requestFocus();
                }
            }
        });
    }

    /**
     * Metodo que carrega uma lista de Marcas
     * exibe o valor retornado pelo método toString() da classe Marca por padrão. Se Marca não tiver o método toString() sobrescrito para retornar o atributo nome, o ComboBox exibirá algo genérico (como o nome da classe e o hash do objeto).
     */
    private void carregarComboBoxMarcas() {
        listaMarcas = marcaDAO.listar();
        observableListMarcas =
                FXCollections.observableArrayList(listaMarcas);
        cbMarca.setItems(observableListMarcas);
    }

    public void carregarChoiceBoxCategoria() {
        cbECategoria.setItems( FXCollections.observableArrayList( ECategoria.values()));
    }

    public void carregarChoiceBoxETipoCombustivel() {
        cbETipoCombustivel.setItems( FXCollections.observableArrayList( ETipoCombustivel.values()));
    }

    //validar entrada de dados do cadastro
    private boolean validarEntradaDeDados() {
        String errorMessage = "";

        if (tfModeloDescricao.getText() == null || tfModeloDescricao.getText().isEmpty()) {
            errorMessage += "Descrição inválido!\n";
        }

        if (tfPotencia.getText() == null || tfPotencia.getText().isEmpty()) {
            errorMessage += "Potência inválida!\n";
        }

        if (cbMarca.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Selecione uma marca!\n";
        }
        if (cbECategoria.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Selecione uma categoria!\n";
        }
        if (cbETipoCombustivel.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Selecione o tipo de combustível!\n";
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


}
