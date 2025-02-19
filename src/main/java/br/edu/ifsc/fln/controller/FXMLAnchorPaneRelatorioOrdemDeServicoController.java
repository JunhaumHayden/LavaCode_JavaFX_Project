/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.fln.controller;

import br.edu.ifsc.fln.exceptions.ExceptionLavacao;
import br.edu.ifsc.fln.model.dao.ordenservicos.OrdemDeServicoDAO;
import br.edu.ifsc.fln.model.database.Database;
import br.edu.ifsc.fln.model.database.DatabaseFactory;
import br.edu.ifsc.fln.model.domain.ordemServicos.EStatus;
import br.edu.ifsc.fln.model.domain.ordemServicos.OrdemDeServico;
import br.edu.ifsc.fln.utils.AlertDialog;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.JasperReport;
//import net.sf.jasperreports.engine.util.JRLoader;
//import net.sf.jasperreports.view.JasperViewer;

/**
 * FXML Controller class
 *
 * @author mpisching
 */
public class FXMLAnchorPaneRelatorioOrdemDeServicoController implements Initializable {

    @FXML
    private TableView<OrdemDeServico> tableView;
    @FXML
    private TableColumn<OrdemDeServico, Integer> tableColumnOrdemDeServicoNumero;
    @FXML
    private TableColumn<OrdemDeServico, String> tableColumnOrdemDeServicoPlaca;
    @FXML
    private TableColumn<OrdemDeServico, BigDecimal> tableColumnOrdemDeServicoValor;
    @FXML
    private TableColumn<OrdemDeServico, String> tableColumnOrdemDeServicoModelo;
    @FXML
    private TableColumn<OrdemDeServico, EStatus> tableColumnOrdemDeServicoStatus;
    @FXML
    private Button buttonImprimir;
    
    private List<OrdemDeServico> listaOrdemDeServicos;

    private ObservableList<OrdemDeServico> observableListOrdemDeServicos;

    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();

    private final OrdemDeServicoDAO osDAO = new OrdemDeServicoDAO(connection);

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        carregarTableView();
    }    
    
    private void carregarTableView() {
        try {
            listaOrdemDeServicos = osDAO.listar();
        } catch (SQLException ex) {
            Logger.getLogger(FXMLAnchorPaneRelatorioOrdemDeServicoController.class.getName()).log(Level.SEVERE, null, ex);
            AlertDialog.exceptionMessage(ex);
            return;
        }
        
        tableColumnOrdemDeServicoNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        tableColumnOrdemDeServicoPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        tableColumnOrdemDeServicoValor.setCellValueFactory(new PropertyValueFactory<>("total"));
        tableColumnOrdemDeServicoModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        tableColumnOrdemDeServicoStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        observableListOrdemDeServicos = FXCollections.observableArrayList(listaOrdemDeServicos);
        tableView.setItems(observableListOrdemDeServicos);
    }
    
    @FXML
    public void handleImprimir() throws JRException {
        URL url = getClass().getResource("/report/Simple_Report_test.jasper");
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(url);

        //null: caso não existam filtros
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, connection);

        //false: não deixa fechar a aplicação principal
        JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
        jasperViewer.setVisible(true);
    }
    
}
