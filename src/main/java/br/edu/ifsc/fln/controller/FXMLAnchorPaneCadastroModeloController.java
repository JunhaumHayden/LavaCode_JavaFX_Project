package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.veiculos.ModeloDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.veiculos.Marca;
import br.edu.ifsc.fln.model.domain.veiculos.Modelo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Carlos Hayden
 */
public class FXMLAnchorPaneCadastroModeloController implements Initializable {

    @FXML
    private TableView<Modelo> tableView;

    @FXML
    private TableColumn<Marca, String> tableColumnMarcaNome;

    @FXML
    private TableColumn<Modelo, String> tableColumnModelodescricao;
    
    @FXML
    private Button btAlterar;

    @FXML
    private Button btInserir;

    @FXML
    private Button btRemover;

    @FXML
    private Label lbMarcaId;

    @FXML
    private Label lbMarcaNome;

    @FXML
    private Label lbModeloCategoria;

    @FXML
    private Label lbModeloDescricao;

    @FXML
    private Label lbModeloId;

    @FXML
    private Label lbMotorCategoria;

    @FXML
    private Label lbMotorId;

    @FXML
    private Label lbMotorPotencia;

    private List<Modelo> listaModelos;
    private ObservableList<Modelo> observableListModelos;

    //acesso ao banco de dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ModeloDAO modeloDAO = new ModeloDAO();

    /**
     * Initializes the controller class.
     */
    //implementando Metodo da Interface Initializable
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        modeloDAO.setConnection(connection);

        carregarTableView();

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableView(newValue));

    }

    public void carregarTableView() {
        tableColumnModelodescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        tableColumnMarcaNome.setCellValueFactory(new PropertyValueFactory<>("nomeMarca"));

        listaModelos = modeloDAO.listar();

        observableListModelos = FXCollections.observableArrayList(listaModelos);
        tableView.setItems(observableListModelos);
    }
    // metodo que atualiza o detalhamaneto de acordo com a selecao da tabela
    public void selecionarItemTableView(Modelo modelo) {
        if (modelo != null) {
            lbModeloId.setText(Integer.toString(modelo.getId()));
            lbModeloDescricao.setText(modelo.getDescricao());
            lbModeloCategoria.setText(modelo.getCategoria().name());
            lbMarcaId.setText(Integer.toString(modelo.getMarca().getId()));
            lbMarcaNome.setText(modelo.getMarca().getNome());
            lbMotorId.setText(Integer.toString(modelo.getMotor().getId()));
            lbMotorPotencia.setText(Integer.toString(modelo.getMotor().getPotencia()));
            lbMotorCategoria.setText(modelo.getMotor().getTipoCombustivel().name());

        } else {
            lbModeloId.setText("");
            lbModeloDescricao.setText("");
            lbModeloCategoria.setText("");
            lbMarcaId.setText("");
            lbMarcaNome.setText("");
            lbMotorId.setText("");
            lbMotorPotencia.setText("");
            lbMotorCategoria.setText("");
        }
    }
    
    @FXML
    public void handleBtAlterar() throws IOException {
        Modelo modelo = tableView.getSelectionModel().getSelectedItem();
        if (modelo != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosModelosDialog(modelo);
            if (buttonConfirmarClicked) {
                modeloDAO.alterar(modelo);
                carregarTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um modelo na Tabela.");
            alert.show();
        }
    }

    @FXML
    public void handleBtInserir() throws IOException {
        Modelo modelo = new Modelo();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosModelosDialog(modelo);
        if (buttonConfirmarClicked) {
            modeloDAO.inserir(modelo);
            carregarTableView();
        }
    }

    @FXML
    public void handleBtRemover() throws IOException {
        Modelo modelo = tableView.getSelectionModel().getSelectedItem();
        if (modelo != null) {
            modeloDAO.remover(modelo);
            carregarTableView();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um modelo na Tabela.");
            alert.show();
        }
    }

    // Metodo auxiliar para abrir janela de dialogo
    public boolean showFXMLAnchorPaneCadastrosModelosDialog(Modelo modelo) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroModeloDialogController.class.getResource(
                "/view/FXMLAnchorPaneCadastroModeloDialog.fxml"));
        AnchorPane page = (AnchorPane)loader.load();

        //criando um estágio de diálogo  (Stage Dialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de modelos");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        //Setando o modelo ao controller
        FXMLAnchorPaneCadastroModeloDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setModelo(modelo);

        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();
    }

}
