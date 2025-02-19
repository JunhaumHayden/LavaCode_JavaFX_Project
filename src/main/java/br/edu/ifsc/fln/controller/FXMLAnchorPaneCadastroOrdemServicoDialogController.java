package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.ordenservicos.ServicoDAO;
import br.edu.ifsc.fln.model.dao.veiculos.VeiculoDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.ECategoria;
import br.edu.ifsc.fln.model.domain.ordemServicos.EStatus;
import br.edu.ifsc.fln.model.domain.ordemServicos.OrdemDeServico;
import br.edu.ifsc.fln.model.domain.ordemServicos.Servico;
import br.edu.ifsc.fln.model.domain.ordemServicos.ItemDeOrdemDeServico;
import br.edu.ifsc.fln.model.domain.veiculos.Veiculo;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author mpisching
 */
public class FXMLAnchorPaneCadastroOrdemServicoDialogController implements Initializable {

    @FXML
    private ComboBox<Veiculo> comboBoxVeiculos;
    @FXML
    private TextField tfCliente;
    @FXML
    private DatePicker datePickerData;
    @FXML
    private ComboBox<Servico> comboBoxServico;
    @FXML
    private TextField textFieldQuantidadeServico;
    @FXML
    private TableView<ItemDeOrdemDeServico> tableViewItensDeOrdemDeServico;
    @FXML
    private TableColumn<ItemDeOrdemDeServico, Servico> tableColumnServico;
    @FXML
    private TableColumn<ItemDeOrdemDeServico, Integer> tableColumnQuantidade;
    @FXML
    private TableColumn<ItemDeOrdemDeServico, Double> tableColumnValor;
    @FXML
    private TextField textFieldValor;
    @FXML
    private TextField textFieldDesconto;
    @FXML
    private ChoiceBox choiceBoxStatus;


    @FXML
    private Button buttonAdicionar;
    @FXML
    private Button buttonConfirmar;
    @FXML
    private Button buttonCancelar;
    @FXML
    private ContextMenu contextMenuTableView;
    @FXML
    private MenuItem contextMenuItemAtualizarQtd;
    @FXML
    private MenuItem contextMenuItemRemoverItem;




    private List<Veiculo> listaVeiculos;
    private List<Servico> listaServicos;
    private ObservableList<Veiculo> observableListVeiculos;
    private ObservableList<Servico> observableListServicos;
    private ObservableList<ItemDeOrdemDeServico> observableListItensDeOrdemDeServico;

    //atributos para manipulação de banco de dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO(connection);
    private final ServicoDAO servicoDAO = new ServicoDAO(connection);

    private Stage dialogStage;
    private boolean buttonConfirmarClicked = false;
    private OrdemDeServico os;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        servicoDAO.setConnection(connection);
        carregarComboBoxVeiculos();
//        carregarComboBoxServicos();
        carregarChoiceBoxStatus();
        setFocusLostHandle();
        tableColumnServico.setCellValueFactory(new PropertyValueFactory<>("servico")); //leitura da propriedade servico no PropertyValueFactory.
        tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade")); //leitura da propriedade quantidade no PropertyValueFactory.
        tableColumnValor.setCellValueFactory(new PropertyValueFactory<>("valor")); //leitura da propriedade valor no PropertyValueFactory.
    }




    private void carregarComboBoxVeiculos() {
        listaVeiculos = veiculoDAO.listar();
        observableListVeiculos = FXCollections.observableArrayList(listaVeiculos);
        comboBoxVeiculos.setItems(observableListVeiculos);
        comboBoxVeiculos.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) { //listener ao comboBoxVeiculos para carregar os serviços quando um veículo for selecionado
                carregarComboBoxServicos(newValue.getModelo().getCategoria());
                tfCliente.setText(newValue.getCliente().getNome());
            }
        });


    }private void carregarComboBoxServicos(ECategoria categoria) {
        listaServicos = servicoDAO.listarPorCategoria(categoria);
        observableListServicos = FXCollections.observableArrayList(listaServicos);
        comboBoxServico.setItems(observableListServicos);
        textFieldQuantidadeServico.setText("1");
        os.setDesconto(BigDecimal.ZERO);
    }


    public void carregarChoiceBoxStatus() {
        choiceBoxStatus.setItems( FXCollections.observableArrayList(EStatus.values()));
        choiceBoxStatus.getSelectionModel().select(EStatus.ABERTA);
    }

    

    private void setFocusLostHandle() {
        textFieldDesconto.focusedProperty().addListener((ov, oldV, newV) -> {
        if (!newV) { // focus lost
                if (textFieldDesconto.getText() != null && !textFieldDesconto.getText().isEmpty()) {
                    //System.out.println("teste focus lost");
                    os.setDesconto(BigDecimal.valueOf(Double.parseDouble(textFieldDesconto.getText())));
                    textFieldValor.setText(os.getTotal().toString());

                }
            }
        });
    }

    /**
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
     * @return the os
     */
    public OrdemDeServico getOrdemDeServico() {
        return os;
    }

    /**
     * @param os the os to set
     */
    public void setOrdemDeServico(OrdemDeServico os) {
        this.os = os;
        if (os.getId() != 0) {
            comboBoxVeiculos.getSelectionModel().select(this.os.getVeiculo());
            datePickerData.setValue(this.os.getAgenda()); // Lembra que Date esta deprecated
            observableListItensDeOrdemDeServico = FXCollections.observableArrayList(
                    this.os.getItens());
            tableViewItensDeOrdemDeServico.setItems(observableListItensDeOrdemDeServico);
            textFieldValor.setText(String.format("%.2f", this.os.getTotal()));
            textFieldDesconto.setText(String.format("%.2f", this.os.getDesconto()));
            choiceBoxStatus.getSelectionModel().select(this.os.getStatus());

        }
    }


    @FXML
    public void handleButtonAdicionar() throws ExceptionLavacao {
        Servico servico;
        ItemDeOrdemDeServico itemDeOrdemDeServico = new ItemDeOrdemDeServico();
        if (comboBoxServico.getSelectionModel().getSelectedItem() != null) {
            //o comboBox possui dados sintetizados de Servico para evitar carga desnecessária de informação
            servico = comboBoxServico.getSelectionModel().getSelectedItem();
            //a instrução a seguir busca detalhes do servico selecionado
            servico = servicoDAO.buscar(servico);
            itemDeOrdemDeServico.setServico(servico);
            itemDeOrdemDeServico.setValorServico(servico.getValor().multiply(BigDecimal.valueOf(Integer.parseInt(textFieldQuantidadeServico.getText()))));
            itemDeOrdemDeServico.setOrdemDeServico(os);
            os.addItemOS(itemDeOrdemDeServico);
            observableListItensDeOrdemDeServico = FXCollections.observableArrayList(os.getItens());
            tableViewItensDeOrdemDeServico.setItems(observableListItensDeOrdemDeServico);
            textFieldValor.setText(String.format("%.2f", os.getTotal()));
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao adicionar item");
            alert.setContentText("Selecione um serviço para adicionar a OS.");
            alert.show();
        }
    }

    @FXML
    private void handleButtonConfirmar() throws ExceptionLavacao {
        if (validarEntradaDeDados()) {
            os.setVeiculo(comboBoxVeiculos.getSelectionModel().getSelectedItem());
            os.setAgenda(datePickerData.getValue());
            os.setStatus((EStatus)choiceBoxStatus.getSelectionModel().getSelectedItem());
            os.setDesconto(BigDecimal.valueOf(Double.parseDouble(textFieldDesconto.getText())));
            buttonConfirmarClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleButtonCancelar() {
        dialogStage.close();
    }

    @FXML
    void handleTableViewMouseClicked(MouseEvent event) {
        ItemDeOrdemDeServico itemDeOrdemDeServico
                = tableViewItensDeOrdemDeServico.getSelectionModel().getSelectedItem();
        if (itemDeOrdemDeServico == null) {
            contextMenuItemAtualizarQtd.setDisable(true);
            contextMenuItemRemoverItem.setDisable(true);
        } else {
            contextMenuItemAtualizarQtd.setDisable(false);
            contextMenuItemRemoverItem.setDisable(false);
        }

    }

    @FXML
    private void handleContextMenuItemAtualizarQtd() {
        ItemDeOrdemDeServico itemDeOrdemDeServico
                = tableViewItensDeOrdemDeServico.getSelectionModel().getSelectedItem();
        int index = tableViewItensDeOrdemDeServico.getSelectionModel().getSelectedIndex();

        int qtdAtualizada = Integer.parseInt(inputDialog(1));
//        if (itemDeOrdemDeServico.getServico().getEstoque().getQuantidade() >= qtdAtualizada) {
//            itemDeOrdemDeServico.setQuantidade(qtdAtualizada);
//            //os.getItensDeOrdemDeServico().set(os.getItensDeOrdemDeServico().indexOf(itemDeOrdemDeServico),itemDeOrdemDeServico);
//            os.getItensDeOrdemDeServico().set(index, itemDeOrdemDeServico);
//            itemDeOrdemDeServico.setValor(itemDeOrdemDeServico.getServico().getPreco().multiply(BigDecimal.valueOf(itemDeOrdemDeServico.getQuantidade())));
//            tableViewItensDeOrdemDeServico.refresh();
//            textFieldValor.setText(String.format("%.2f", os.getTotal()));
//        } else {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText("Erro no estoque");
//            alert.setContentText("Não há quantidade suficiente de servicos para os.");
//            alert.show();
//        }
    }

    private String inputDialog(int value) {
        TextInputDialog dialog = new TextInputDialog(Integer.toString(value));
        dialog.setTitle("Entrada de dados.");
        dialog.setHeaderText("Atualização da quantidade de serviços.");
        dialog.setContentText("Quantidade: ");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        return result.get();
    }

    @FXML
    private void handleContextMenuItemRemoverItem() throws ExceptionLavacao {
        os.removeItemOS(tableViewItensDeOrdemDeServico.getSelectionModel().getSelectedItem());
        observableListItensDeOrdemDeServico = FXCollections.observableArrayList(os.getItens());
        tableViewItensDeOrdemDeServico.setItems(observableListItensDeOrdemDeServico);

        textFieldValor.setText(String.format("%.2f", os.getTotal()));
    }

    //validar entrada de dados do cadastro
    private boolean validarEntradaDeDados() {
        String errorMessage = "";

        if (comboBoxVeiculos.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Veiculo inválido!\n";
        }

        if (datePickerData.getValue() == null) {
            errorMessage += "Data inválida!\n";
        }

        if (observableListItensDeOrdemDeServico == null) {
            errorMessage += "Itens de os inválidos!\n";
        }
        
        DecimalFormat df = new DecimalFormat("0.00");
        try {
            textFieldDesconto.setText(df.parse(textFieldDesconto.getText()).toString());
        } catch (ParseException ex) {
            errorMessage += "O desconto está incorreto! Use \",\" como ponto decimal.\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no cadastro");
            alert.setHeaderText("Campos inválidos, por favor corrija...");
            alert.setContentText(errorMessage);
            alert.show();
            return false;
        }
    }
}
