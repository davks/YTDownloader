package eu.davidknotek.ytdownloader;

import com.sun.media.jfxmedia.control.VideoFormat;
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

public class Controller implements Initializable {
    private final List<FormatVidea> videoFormatList = new ArrayList<>();
    private final List<FormatVidea> audioFormatList = new ArrayList<>();

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
        unbindFromWorker();
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
    void onChooseVideoFormat(ActionEvent event) {
        onlyAudioList.clear();
        int index = cbxVideo.getSelectionModel().getSelectedIndex();
        if (index > -1) {
            FormatVidea vybranyFormat = videoFormatList.get(index);
            if (vybranyFormat.getTyp() == FormatVidea.Typ.VIDEO_ONLY) {
                cbxAudio.setDisable(false);
                showAudioList();
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

    private void unbindFromWorker() {
        serviceAnalyzer.setOnSucceeded(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatel.progressProperty().unbind();
            pbUkazatel.setProgress(0.0);
            classifyFormatList(serviceAnalyzer.getValue());
            showVideoList();
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

    private void classifyFormatList(List<FormatVidea> allFormatList) {
        audioFormatList.clear();
        videoFormatList.clear();
        for (FormatVidea format : allFormatList) {
            if (format.getTyp() == FormatVidea.Typ.AUDIO_ONLY) {
                audioFormatList.add(format);
            } else {
                videoFormatList.add(format);
            }
        }
    }

    private void showVideoList() {
        for (FormatVidea format : videoFormatList) {
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

    private void showAudioList() {
        for (FormatVidea format : audioFormatList) {
            String fileSize = format.getFileSize().equals("") ? "*" : format.getFileSize();
            onlyAudioList.add(format.getAudioQuality() + " / " +
                    format.getExtension() + " / " +
                    fileSize);
        }
        cbxAudio.getSelectionModel().selectFirst();
    }
}
