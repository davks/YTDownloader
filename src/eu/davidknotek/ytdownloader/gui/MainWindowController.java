package eu.davidknotek.ytdownloader.gui;

import eu.davidknotek.ytdownloader.typy.FormatVidea;
import eu.davidknotek.ytdownloader.enums.TypVidea;
import eu.davidknotek.ytdownloader.services.ServiceAnalyzer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private final List<FormatVidea> seznamVideoFormatu = new ArrayList<>();
    private final List<FormatVidea> seznamAudioFormatu = new ArrayList<>();

    private final ObservableList<String> onlyVideoList = FXCollections.observableArrayList();
    private final ObservableList<String> onlyAudioList = FXCollections.observableArrayList();
    private final ServiceAnalyzer serviceAnalyzer = new ServiceAnalyzer();

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
        cbxVideo.setItems(onlyVideoList);
        cbxAudio.setItems(onlyAudioList);
        unbind();
    }

    @FXML
    void onAnalyzovat(ActionEvent event) {
        String url = edtUrl.getText();
        if (!url.trim().equals("")) {
            serviceAnalyzer.setUrl(url);
            serviceAnalyzer.restart();
            onlyVideoList.clear();
            onlyAudioList.clear();

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
    void onVybratVideoFormat(ActionEvent event) {
        onlyAudioList.clear();
        int index = cbxVideo.getSelectionModel().getSelectedIndex();
        if (index > -1) {
            FormatVidea vybranyFormat = seznamVideoFormatu.get(index);
            if (vybranyFormat.getTypVidea() == TypVidea.VIDEO_ONLY) {
                cbxAudio.setDisable(false);
                zobrazitSeznamAudia();
            } else {
                cbxAudio.setDisable(true);
            }
        }
    }

    @FXML
    void onKonec(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    ///////////////////////////////////////////////////////
    // Soukromé metody
    ///////////////////////////////////////////////////////

    private void unbind() {
        serviceAnalyzer.setOnSucceeded(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatel.progressProperty().unbind();
            pbUkazatel.setProgress(0.0);
            rozstriditSeznamFormatu(serviceAnalyzer.getValue());
            zobrazitSeznamVidea();
        });

        serviceAnalyzer.setOnCancelled(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatel.progressProperty().unbind();
            lblZprava.setText("Analýza byla přerušena.");
            pbUkazatel.setProgress(0.0);
        });

        serviceAnalyzer.setOnFailed(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatel.progressProperty().unbind();
            lblZprava.setText("Vyskytl se problém při analýze URL.");
            pbUkazatel.setProgress(0.0);
        });
    }

    private void rozstriditSeznamFormatu(List<FormatVidea> allFormatList) {
        seznamAudioFormatu.clear();
        seznamVideoFormatu.clear();
        for (FormatVidea format : allFormatList) {
            if (format.getTypVidea() == TypVidea.AUDIO_ONLY) {
                seznamAudioFormatu.add(format);
            } else {
                seznamVideoFormatu.add(format);
            }
        }
    }

    private void zobrazitSeznamVidea() {
        for (FormatVidea format : seznamVideoFormatu) {
            if (!format.getResolution().equals("")) {
                String fps = format.getFps().equals("") ? "*" : format.getFps();
                String fileSize = format.getFileSize().equals("") ? "*" : format.getFileSize();
                onlyVideoList.add(format.getResolution() + " / " +
                        fps + " / " +
                        format.getExtension() + " / " +
                        fileSize);
            }
        }
        cbxVideo.getSelectionModel().selectFirst();
    }

    private void zobrazitSeznamAudia() {
        for (FormatVidea format : seznamAudioFormatu) {
            String fileSize = format.getFileSize().equals("") ? "*" : format.getFileSize();
            onlyAudioList.add(format.getAudioQuality() + " / " +
                    format.getExtension() + " / " +
                    fileSize);
        }
        cbxAudio.getSelectionModel().selectFirst();
    }
}
