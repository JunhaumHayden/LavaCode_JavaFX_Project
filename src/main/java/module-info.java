module Main {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires java.sql;
    opens br.edu.ifsc.fln to javafx.fxml;
    opens br.edu.ifsc.fln.controller to javafx.fxml;
    opens br.edu.ifsc.fln.model.domain to javafx.base;
    opens br.edu.ifsc.fln.model.domain.veiculos to javafx.base;
    opens br.edu.ifsc.fln.model.domain.clientes to javafx.base;
    opens br.edu.ifsc.fln.model.domain.ordemServicos to javafx.base;
    opens br.edu.ifsc.fln.model.dao.veiculos to javafx.base;
    exports br.edu.ifsc.fln;
    exports br.edu.ifsc.fln.service;
    opens br.edu.ifsc.fln.service to javafx.fxml;
}