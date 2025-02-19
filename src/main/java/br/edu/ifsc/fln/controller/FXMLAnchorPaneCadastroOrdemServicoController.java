/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.ordenservicos.ItemDeOrdemDeServicoDAO;
import br.edu.ifsc.fln.model.dao.ordenservicos.OrdemDeServicoDAO;
import br.edu.ifsc.fln.model.dao.ordenservicos.ServicoDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.ordemServicos.OrdemDeServico;
import br.edu.ifsc.fln.model.domain.ordemServicos.ItemDeOrdemDeServico;
import br.edu.ifsc.fln.service.OrdemDeServicoService;
import br.edu.ifsc.fln.utils.AlertDialog;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mpisching
 */
public class FXMLAnchorPaneCadastroOrdemServicoController implements Initializable {
    @FXML
    private TableView<OrdemDeServico> tableView;

    @FXML
    private TableColumn<OrdemDeServico, String> tableColumnOsNumero;

    @FXML
    private TableColumn<OrdemDeServico, String> tableColumnOsPlaca;

    @FXML
    private TableColumn<OrdemDeServico, OrdemDeServico> tableColumnOsCliente;

    @FXML
    private Label labelOsNumero;

    @FXML
    private Label labelOsAgenda;

    @FXML
    private Label labelOsStatus;

    @FXML
    private Label labelOsCliente;

    @FXML
    private Label labelOsVeiculo;

    @FXML
    private Label labelOsDesconto;

    @FXML
    private Label labelOsTotal;
    
    @FXML
    private Button buttonAlterar;

    @FXML
    private Button buttonInserir;

    @FXML
    private Button buttonRemover;


    private List<OrdemDeServico> listaOrdemDeServicos;
    private ObservableList<OrdemDeServico> observableListOrdemDeServicos;

    //acesso ao banco de dados
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final OrdemDeServicoDAO ordemDeServicoDAO = new OrdemDeServicoDAO(connection);
    private final ItemDeOrdemDeServicoDAO itemDeOrdemDeServicoDAO = new ItemDeOrdemDeServicoDAO(connection);
    private final ServicoDAO servicoDAO = new ServicoDAO(connection);
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            carregarTableView();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableView(newValue));
    }

    public void carregarTableView() throws SQLException {
        
        tableColumnOsNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        tableColumnOsPlaca.setCellValueFactory(new PropertyValueFactory<>("Placa"));
        tableColumnOsCliente.setCellValueFactory(new PropertyValueFactory<>("Cliente"));

        listaOrdemDeServicos = ordemDeServicoDAO.listar();

        observableListOrdemDeServicos = FXCollections.observableArrayList(listaOrdemDeServicos);
        tableView.setItems(observableListOrdemDeServicos);
    }

    public void selecionarItemTableView(OrdemDeServico os) {
        if (os != null) {
            labelOsNumero.setText(os.getNumero());
            labelOsAgenda.setText(String.valueOf(os.getAgenda().toString()));
            labelOsStatus.setText(os.getStatus().name());
            labelOsCliente.setText(os.getVeiculo().getCliente().getNome());
            labelOsVeiculo.setText(os.getVeiculo().getPlaca());
            labelOsDesconto.setText((String.format("%.2f", os.getDesconto())));
            labelOsTotal.setText(String.format("%.2f", os.getTotal()));
            
            
        } else {
            labelOsNumero.setText("");
            labelOsAgenda.setText("");
            labelOsStatus.setText("");
            labelOsCliente.setText("");
            labelOsVeiculo.setText("");
            labelOsDesconto.setText("");
            labelOsTotal.setText("");
        }
    }

    @FXML
    private void handleButtonInserir(ActionEvent event) throws IOException, SQLException, ExceptionLavacao {
        OrdemDeServico os = new OrdemDeServico();
        boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastroOrdemDeServicoDialog(os);
        if (buttonConfirmarClicked) {
            ordemDeServicoDAO.inserir(os);
            carregarTableView();
        }
    }

    @FXML
    private void handleButtonAlterar(ActionEvent event) throws IOException, SQLException {
        OrdemDeServico os = tableView.getSelectionModel().getSelectedItem();
        if (os != null) {
            boolean buttonConfirmarClicked = showFXMLAnchorPaneCadastroOrdemDeServicoDialog(os);
            if (buttonConfirmarClicked) {
                ordemDeServicoDAO.alterar(os);
                carregarTableView();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um os na Tabela.");
            alert.show();
        }        
    }

    @FXML
    private void handleButtonRemover(ActionEvent event) throws SQLException {
//        OrdemDeServico os = tableView.getSelectionModel().getSelectedItem();
//        if (os != null) {
//            if (AlertDialog.confirmarExclusao("Tem certeza que deseja excluir a os " + os.getId())) {
//                ordemDeServicoDAO.setConnection(connection);
//                ordemDeServicoDAO.remover(os);
//                carregarTableView();
//            }
//        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Uma Ordem de Serviço não pode ser removida!\n"
                    + "Proceda com o Cancelamento da Ordem de Serviço inserindo as devidas justificativas.");
            alert.show();
//        }
    }

    public boolean showFXMLAnchorPaneCadastroOrdemDeServicoDialog(OrdemDeServico os) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroOrdemServicoDialogController.class.getResource(
                "/view/FXMLAnchorPaneCadastroOrdemServicoDialog.fxml"));
        AnchorPane page = loader.load();

        //criando um estágio de diálogo  (Stage Dialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Ordem de Serviço");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        //Setando o os ao controller
        FXMLAnchorPaneCadastroOrdemServicoDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setOrdemDeServico(os);

        //Mostra o diálogo e espera até que o usuário o feche
        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();
    }



}
