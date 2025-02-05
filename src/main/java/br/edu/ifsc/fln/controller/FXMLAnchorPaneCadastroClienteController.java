/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.clientes.ClienteDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.clientes.Cliente;
import br.edu.ifsc.fln.model.domain.clientes.PessoaFisica;
import br.edu.ifsc.fln.model.domain.clientes.PessoaJuridica;
import br.edu.ifsc.fln.utils.AlertDialog;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mpisc
 */
public class FXMLAnchorPaneCadastroClienteController implements Initializable {

    @FXML
    private Button btAlterar;

    @FXML
    private Button btExcluir;

    @FXML
    private Button btInserir;

    @FXML
    private Label lbClienteEmail;

    @FXML
    private Label lbClienteId;

    @FXML
    private Label lbClienteNome;

    @FXML
    private Label lbClienteNumFiscal;

    @FXML
    private Label lbClientePontuacao;

    @FXML
    private Label lbClienteFone;

    @FXML
    private Label lbClienteTipo;

    @FXML
    private Label lbClienteDataCadastro;

    @FXML
    private Label lbClienteDado;

    @FXML
    private Label lbClienteEtiquetaDado;
    
    @FXML
    private TableColumn<Cliente, String> tableColumnClienteFone;

    @FXML
    private TableColumn<Cliente, String> tableColumnClienteNome;

    @FXML
    private TableView<Cliente> tableViewClientes;

    
    private List<Cliente> listaClientes;
    private ObservableList<Cliente> observableListClientes;
    
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ClienteDAO clienteDAO = new ClienteDAO(connection);
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            carregarTableViewCliente();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        tableViewClientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableViewClientes(newValue));
    }     
    
    public void carregarTableViewCliente() throws SQLException {
        tableColumnClienteNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tableColumnClienteFone.setCellValueFactory(new PropertyValueFactory<>("celular"));
        
        listaClientes = clienteDAO.listar();
        
        observableListClientes = FXCollections.observableArrayList(listaClientes);
        tableViewClientes.setItems(observableListClientes);
    }
    
    public void selecionarItemTableViewClientes(Cliente cliente) {
        if (cliente != null) {
            lbClienteId.setText(String.valueOf(cliente.getId())); 
            lbClienteNome.setText(cliente.getNome());
            lbClienteFone.setText(cliente.getCelular());
            lbClienteEmail.setText(cliente.getEmail());
            lbClienteDataCadastro.setText(cliente.getDataCadastro().toString());
            lbClientePontuacao.setText(String.valueOf(cliente.getPontuacao().getSaldo()));
            if (cliente instanceof PessoaFisica) {
                lbClienteTipo.setText("PessoaFisica");
                lbClienteNumFiscal.setText(((PessoaFisica)cliente).getCpf());
                lbClienteEtiquetaDado.setText("Data de Nascimento:");
                lbClienteDado.setText(((PessoaFisica)cliente).getDataNascimento().toString());
            } else {
                lbClienteTipo.setText("PessoaJuridica");
                lbClienteNumFiscal.setText(((PessoaJuridica)cliente).getCnpj());
                lbClientePontuacao.setText(((PessoaJuridica)cliente).getInscricaoEstadual());
                lbClienteEtiquetaDado.setText("Ins. Estadual:");
                lbClienteDado.setText(((PessoaJuridica)cliente).getInscricaoEstadual());
            }
        } else {
            lbClienteId.setText(""); 
            lbClienteNome.setText("");
            lbClienteFone.setText("");
            lbClienteEmail.setText("");
            lbClienteTipo.setText("");
            lbClienteNumFiscal.setText("");
            lbClientePontuacao.setText("");
        }
        
    }
    
    @FXML
    public void handleBtInserir() throws IOException, SQLException {
        Cliente cliente = getTipoCliente();
        if (cliente != null ) {
            boolean btConfirmarClicked = showFXMLAnchorPaneCadastroClienteDialog(cliente);
            if (btConfirmarClicked) {
                clienteDAO.inserir(cliente);
                carregarTableViewCliente();
            }
        }
    }
    
    private Cliente getTipoCliente() {
        List<String> opcoes = new ArrayList<>();
        opcoes.add("Pessoa Fisica");
        opcoes.add("Pessoa Juridica");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Pessoa Fisica", opcoes);
        dialog.setTitle("Dialogo de Opções");
        dialog.setHeaderText("Escolha o tipo de cliente");
        dialog.setContentText("Tipo de cliente: ");
        Optional<String> escolha = dialog.showAndWait();
        if (escolha.isPresent()) {
            if (escolha.get().equalsIgnoreCase("Pessoa Fisica"))
                return new PessoaFisica();
            else 
                return new PessoaJuridica();
        } else {
            return null;
        }
    }
    
    @FXML 
    public void handleBtAlterar() throws IOException, SQLException {
        Cliente cliente = tableViewClientes.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            boolean btConfirmarClicked = showFXMLAnchorPaneCadastroClienteDialog(cliente);
            if (btConfirmarClicked) {
                clienteDAO.alterar(cliente);
                carregarTableViewCliente();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde um Cliente na tabela ao lado");
            alert.show();
  }
    }
    
    @FXML
    public void handleBtExcluir() throws IOException, SQLException {
        Cliente cliente = tableViewClientes.getSelectionModel().getSelectedItem();
        if (cliente != null) {
            if (AlertDialog.confirmarExclusao("Tem certeza que deseja excluir o cliente " + cliente.getNome())) {
                clienteDAO.remover(cliente);
                carregarTableViewCliente();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Esta operação requer a seleção \nde uma Cliente na tabela ao lado");
            alert.show();
        }
    }

    private boolean showFXMLAnchorPaneCadastroClienteDialog(Cliente cliente) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroClienteController.class.getResource("/view/FXMLAnchorPaneCadastroClienteDialog.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        
        //criação de um estágio de diálogo (StageDialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de Cliente");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        //enviando o objeto cliente para o controller
        FXMLAnchorPaneCadastroClienteDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setCliente(cliente);
        
        //apresenta o diálogo e aguarda a confirmação do usuário
        dialogStage.showAndWait();
        
        return controller.isBtConfirmarClicked();
    }
    
}
