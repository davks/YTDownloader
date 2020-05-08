package eu.davidknotek.ytdownloader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextField edtUrl;

    @FXML
    private Button btnAnalyzovat;

    @FXML
    private Label lblNazev;

    @FXML
    private Label lblSmazat;

    private ServiceAnalyzer serviceAnalyzer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        serviceAnalyzer = new ServiceAnalyzer(lblNazev);
    }

    @FXML
    void onAnalyzovat(ActionEvent event) {
        String url = edtUrl.getText();
        lblNazev.setText("");
        if (!url.trim().equals("")) {
            serviceAnalyzer.setUrl(url);
            serviceAnalyzer.restart();
            lblNazev.textProperty().bind(serviceAnalyzer.messageProperty());
        }


    }
}
