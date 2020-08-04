package eu.davidknotek.ytdownloader.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class OProgramuController implements Initializable {

    @FXML
    private Label lblVerzeYTDL;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void onAktualizaceYTDL(ActionEvent event) {

    }

    @FXML
    void onKonec(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
