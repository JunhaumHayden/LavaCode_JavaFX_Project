package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.clientes.ClienteDAO;
import br.edu.ifsc.fln.model.dao.veiculos.CorDAO;
import br.edu.ifsc.fln.model.dao.veiculos.MarcaDAO;
import br.edu.ifsc.fln.model.dao.veiculos.ModeloDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.clientes.Cliente;
import br.edu.ifsc.fln.model.domain.veiculos.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroVeiculoDialogController implements Initializable {


    @FXML
    private ChoiceBox<Cor> cbCor;

    @FXML
    private ChoiceBox<Cliente> cbCliente;

    @FXML
    private ComboBox<Modelo> cbModelo;

    @FXML
    private TextField tfVeiculoPlaca;

    @FXML
    private TextField tfVeiculoobservacao;

    @FXML
    private Button btCancelar;

    @FXML
    private Button btConfirmar;

    private List<Modelo> listaModelos;
    private ObservableList<Modelo> observableListModelos;
    private List<Cor> listaCores;
    private ObservableList<Cor> observableListCores;
    private List<Cliente> listaClientes;
    private ObservableList<Cliente> observableListClientes;

    //acesso ao banco de dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ModeloDAO modeloDAO = new ModeloDAO();
    private final CorDAO corDAO = new CorDAO();
    private final MarcaDAO marcaDAO = new MarcaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO(connection);


    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private Veiculo veiculo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        marcaDAO.setConnection(connection);
        corDAO.setConnection(connection);
        modeloDAO.setConnection(connection);

        try {
            carregarComboBoxModelos();
            carregarChoiceBoxCor();
            carregarChoiceBoxCliente();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
     * @return the veiculo
     */
    public Veiculo getVeiculo() {
        return veiculo;
    }
    /**
     * @param veiculo the veiculo to set
     */
    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
        tfVeiculoPlaca.setText(veiculo.getPlaca());
        tfVeiculoobservacao.setText(veiculo.getObservacoes());
        cbModelo.getSelectionModel().select(veiculo.getModelo());
        cbCor.getSelectionModel().select(veiculo.getCor());
        cbCliente.getSelectionModel().select(veiculo.getCliente());
    }

    @FXML
    private void handleBtCancelar() {
        dialogStage.close();
    }

    @FXML
    private void handleBtConfirmar() throws ExceptionLavacao {
        if (validarEntradaDeDados()) {
            veiculo.setPlaca(tfVeiculoPlaca.getText());
            veiculo.setObservacoes(tfVeiculoobservacao.getText());
            veiculo.setCor(cbCor.getSelectionModel().getSelectedItem());
            veiculo.setModelo(cbModelo.getSelectionModel().getSelectedItem());
            veiculo.setCliente(cbCliente.getSelectionModel().getSelectedItem());

            buttonConfirmarClicked = true;
            dialogStage.close();
        }
    }

    // Metodos auxiliares
    private void setFocusLostHandle() {
        tfVeiculoPlaca.focusedProperty().addListener((ov, oldV, newV) -> {
            if (!newV) { // focus lost
                if (tfVeiculoPlaca.getText() == null || tfVeiculoPlaca.getText().isEmpty()) {
                    tfVeiculoPlaca.requestFocus();
                }
            }
        });
    }

    /**
     * Metodo que carrega uma lista de Marcas
     * exibe o valor retornado pelo método toString() da classe Marca por padrão. Se Marca não tiver o método toString() sobrescrito para retornar o atributo nome, o ComboBox exibirá algo genérico (como o nome da classe e o hash do objeto).
     */
    private void carregarComboBoxModelos() {
        listaModelos = modeloDAO.listar();
        observableListModelos =
                FXCollections.observableArrayList(listaModelos);
        cbModelo.setItems(observableListModelos);
    }

    public void carregarChoiceBoxCor() {
        listaCores = corDAO.listar();
        observableListCores =
                FXCollections.observableArrayList(listaCores);
        cbCor.setItems(observableListCores);
    }

    public void carregarChoiceBoxCliente() throws SQLException {
        listaClientes = clienteDAO.listar();
        observableListClientes =
                FXCollections.observableArrayList(listaClientes);
        cbCliente.setItems(observableListClientes);
    }

    //validar entrada de dados do cadastro
    private boolean validarEntradaDeDados() {
        String errorMessage = "";

        if (tfVeiculoPlaca.getText() == null || tfVeiculoPlaca.getText().isEmpty()) {
            errorMessage += "Placa inválida!\n";
        }

        if (tfVeiculoobservacao.getText() == null || tfVeiculoobservacao.getText().isEmpty()) {
            errorMessage += "Preencha uma Observação!\n";
        }

        if (cbModelo.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Selecione um Modelo!\n";
        }
        if (cbCor.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Selecione uma Cor!\n";
        }
        if (cbCliente.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Selecione o um Cliente!\n";
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
