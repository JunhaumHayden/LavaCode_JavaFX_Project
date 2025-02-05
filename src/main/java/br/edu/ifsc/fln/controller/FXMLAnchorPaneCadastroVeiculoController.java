package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.controller.FXMLAnchorPaneCadastroVeiculoDialogController;
import br.edu.ifsc.fln.model.dao.veiculos.VeiculoDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.veiculos.Marca;
import br.edu.ifsc.fln.model.domain.veiculos.Veiculo;
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

public class FXMLAnchorPaneCadastroVeiculoController implements Initializable {

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
    private Label lbModeloId;

    @FXML
    private Label lbModeloCategoria;

    @FXML
    private Label lbModeloDescricao;

    @FXML
    private Label lbVeiculoId;

    @FXML
    private Label lbMotorCategoria;

    @FXML
    private Label lbMotorId;

    @FXML
    private Label lbMotorPotencia;

    @FXML
    private Label lbVeiculoCliente;

    @FXML
    private Label lbVeiculoCor;

    @FXML
    private Label lbVeiculoPlaca;

    @FXML
    private TextArea taVeiculoObservacoes;

    @FXML
    private TableColumn<Marca, String> tableColumnMarcaNome;

    @FXML
    private TableColumn<Veiculo, String> tableColumnVeiculoPlaca;

    @FXML
    private TableView<Veiculo> tableView;

    private List<Veiculo> listaVeiculos;
    private ObservableList<Veiculo> observableListVeiculos;

    //acesso ao banco de dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO(connection);

    /**
     * Initializes the controller class.
     */
    //implementando Metodo da Interface Initializable
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        veiculoDAO.setConnection(connection); // implementado diretamente na instanciacao para testes

        carregarTableView();

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableView(newValue));

    }

    public void carregarTableView() {
        tableColumnVeiculoPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        tableColumnMarcaNome.setCellValueFactory(new PropertyValueFactory<>("nomeMarca")); // Necessario implementar um Método Getter em Veiculo.

        listaVeiculos = veiculoDAO.listar();

        observableListVeiculos = FXCollections.observableArrayList(listaVeiculos);
        tableView.setItems(observableListVeiculos);
    }

    public void selecionarItemTableView(Veiculo veiculo) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (veiculo != null) {
            lbVeiculoId.setText(Integer.toString(veiculo.getId()));
            lbVeiculoPlaca.setText(veiculo.getPlaca());
            lbVeiculoCor.setText(veiculo.getCor().getNome());
            lbVeiculoCliente.setText(veiculo.getCliente().getNome());
            taVeiculoObservacoes.setText(veiculo.getObservacoes());
            lbModeloId.setText(Integer.toString(veiculo.getModelo().getId()));
            lbModeloDescricao.setText(veiculo.getModelo().getDescricao());
            lbModeloCategoria.setText(veiculo.getModelo().getCategoria().name());
            lbMarcaId.setText(Integer.toString(veiculo.getModelo().getMarca().getId()));
            lbMarcaNome.setText(veiculo.getModelo().getMarca().getNome());
            lbMotorId.setText(Integer.toString(veiculo.getModelo().getMotor().getId()));
            lbMotorPotencia.setText(Integer.toString(veiculo.getModelo().getMotor().getPotencia()));
            lbMotorCategoria.setText(veiculo.getModelo().getMotor().getTipoCombustivel().name());

        } else {
            lbVeiculoId.setText("");
            lbVeiculoPlaca.setText("");
            lbVeiculoCor.setText("");
            lbVeiculoCliente.setText("");
            taVeiculoObservacoes.setText("");
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

    // Declaracao dos metodos
    @FXML
    public void handleBtAlterar() throws IOException {
        Veiculo veiculo = tableView.getSelectionModel().getSelectedItem();
        if (veiculo != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosVeiculosDialog(veiculo);
            if (buttonConfirmarClicked) {
                veiculoDAO.alterar(veiculo);
                System.out.println("BTALTERAR Confirmado");
                carregarTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um veiculo na Tabela.");
            alert.show();
        }

    }

    @FXML
    public void handleBtInserir() throws IOException {
        Veiculo veiculo = new Veiculo();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastrosVeiculosDialog(veiculo);
        if (buttonConfirmarClicked) {
            veiculoDAO.inserir(veiculo);
            carregarTableView();
        }
    }

    @FXML
    public void handleBtRemover() throws IOException {
        Veiculo veiculo = tableView.getSelectionModel().getSelectedItem();
        if (veiculo != null) {
            veiculoDAO.remover(veiculo);
            carregarTableView();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um veiculo na Tabela.");
            alert.show();
        }
    }

    // Metodo auxiliar para abrir janela de dialogo
    public boolean showFXMLAnchorPaneCadastrosVeiculosDialog(Veiculo veiculo) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroVeiculoDialogController.class.getResource(
                "/view/FXMLAnchorPaneCadastroVeiculoDialog.fxml"));
        AnchorPane page = (AnchorPane)loader.load();

        //criando um estágio de diálogo  (Stage Dialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de veiculos");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        //Setando o veiculo ao controller
        FXMLAnchorPaneCadastroVeiculoDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setVeiculo(veiculo);

        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();
    }
}
