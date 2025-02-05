package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.model.dao.ordenservicos.ServicoDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.ordemServicos.Servico;
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
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLAnchorPaneCadastroServicoController implements Initializable {

    @FXML
    private TableView<Servico> tableView;
    @FXML
    private TableColumn<Servico, String> tableColumnServicoDescricao;
    @FXML
    private TableColumn<Servico, BigDecimal> tableColumnServicoValor;

    @FXML
    private Button btAlterar;
    @FXML
    private Button btInserir;
    @FXML
    private Button btRemover;

    @FXML
    private Label lbServicoId;
    @FXML
    private Label lbServicoDescricao;
    @FXML
    private Label lbServicoCategoria;
    @FXML
    private Label lbServicoValor;
    @FXML
    private Label lbServicoPonto;

    private List<Servico> listaServicos;
    private ObservableList<Servico> observableListServicos;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ServicoDAO servicoDAO = new ServicoDAO(connection);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarTableView();
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selecionarItemTableView(newValue));
    }

    @FXML
    public void handleBtAlterar() throws IOException, SQLException {
        Servico servico = tableView.getSelectionModel().getSelectedItem();
        if (servico != null) {
            boolean btConfirmarClicked = showFXMLAnchorPaneCadastroServicosDialog(servico);
            if (btConfirmarClicked) {
                servicoDAO.alterar(servico);
                System.out.println("Botao Alterar do Serviço confirmado");
                carregarTableView();
                System.out.println("table view do servico confirmado");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um serviço na Tabela.");
            alert.show();
        }
    }



    @FXML
    void handleBtInserir() throws IOException, SQLException {
        Servico servico = new Servico();
        boolean btConfirmarClicked = showFXMLAnchorPaneCadastroServicosDialog(servico);
        if (btConfirmarClicked) {
            servicoDAO.inserir(servico);
            System.out.println("Botao INSERIR do servico confirmado");
            carregarTableView();
            System.out.println("####table view do servico confirmado#####");
        }

    }

    @FXML
    void handleBtRemover() throws IOException, SQLException {
        Servico servico = tableView.getSelectionModel().getSelectedItem();
        if (servico != null) {
            servicoDAO.remover(servico);
            carregarTableView();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Por favor, escolha um produto na Tabela.");
            alert.show();
        }

    }







    //##########################################################
    //###################################################
    private void selecionarItemTableView(Servico servico) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (servico != null) {
            lbServicoId.setText(String.valueOf(servico.getId()));
            lbServicoDescricao.setText(servico.getDescricao());
            lbServicoCategoria.setText(servico.getCategoria().name());
            lbServicoValor.setText(df.format(servico.getValor().doubleValue()));
            lbServicoPonto.setText(Integer.toString(servico.getPontos()));
        } else {
            lbServicoId.setText("");
            lbServicoDescricao.setText("");
            lbServicoCategoria.setText("");
            lbServicoValor.setText("");
            lbServicoPonto.setText("");
        }
    }

    public void carregarTableView() {
        tableColumnServicoDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        tableColumnServicoValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        listaServicos = servicoDAO.listar();
        System.out.println("Atualizando table view confirmado");

        observableListServicos = FXCollections.observableArrayList(listaServicos);
        tableView.setItems(observableListServicos);
    }

    /**
     * // Metodo auxiliar para abrir janela de dialogo
     * @param servico
     * @return boolean
     */
    private boolean showFXMLAnchorPaneCadastroServicosDialog(Servico servico) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(FXMLAnchorPaneCadastroServicoDialogController.class.getResource(
                "/view/FXMLAnchorPaneCadastroServicoDialog.fxml"));
        AnchorPane page = (AnchorPane)loader.load();

        //criando um estágio de diálogo (Stage Dialog)
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Cadastro de servicos");
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        //Setando o servico ao controller
        FXMLAnchorPaneCadastroServicoDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setServico(servico);

        dialogStage.showAndWait();

        return controller.isButtonConfirmarClicked();
    }

}


