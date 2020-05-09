package eu.davidknotek.ytdownloader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private ServiceAnalyzer serviceAnalyzer;

    @FXML
    private TextField edtUrl;

    @FXML
    private Label lblNazevVidea;

    @FXML
    private ComboBox<String> cbxVideo;

    @FXML
    private ComboBox<String> cbxAudio;

    @FXML
    private TextField tfCestaUlozit;

    @FXML
    private Label lblZprava;

    @FXML
    private ProgressBar pbUkazatel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceAnalyzer = new ServiceAnalyzer(lblNazevVidea, lblZprava, pbUkazatel);
    }

    @FXML
    void onAnalyzovat(ActionEvent event) {
        String url = edtUrl.getText();
        if (!url.trim().equals("")) {
            serviceAnalyzer.setUrl(url);
            serviceAnalyzer.restart();
            lblNazevVidea.textProperty().bind(serviceAnalyzer.titleProperty());
            lblZprava.textProperty().bind(serviceAnalyzer.messageProperty());
            pbUkazatel.progressProperty().bind(serviceAnalyzer.progressProperty());
        }
    }

    @FXML
    void onDoFronty(ActionEvent event) {
        //TODO vlozeni do fronty
    }

    @FXML
    void onPrerusit(ActionEvent event) {
        //TODO prerusit stahovani
    }

    @FXML
    void onStahnout(ActionEvent event) {
        //TODO zacit stahovat z fronty
    }

    @FXML
    void onVybratAdresar(ActionEvent event) {
        // TODO vybrat adresar
    }

    @FXML
    void onKonec(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
