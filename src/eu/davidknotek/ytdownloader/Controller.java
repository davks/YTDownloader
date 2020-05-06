package eu.davidknotek.ytdownloader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable {
    @FXML
    private TextField edtUrl;

    @FXML
    private Button btnAnalyzovat;

    @FXML
    private Label lblNazev;

    @FXML
    private Label lblSmazat;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    void onAnalyzovat(ActionEvent event) {
        VideoList videoList = new VideoList(edtUrl.getText(), VideoList.SpustitPrikaz.ANALYZOVAT);
        videoList.setOnRunning((successesEvent) -> {
            lblNazev.setText("Začínám...");
            lblSmazat.setText("");
        });

        videoList.setOnSucceeded((successedEvent) -> {
            lblNazev.setText(videoList.getYtVideo().getName());
            lblSmazat.setText(videoList.getErrors().toString());
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(videoList);
        executorService.shutdown();
    }
}
