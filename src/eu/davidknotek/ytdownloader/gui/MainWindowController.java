package eu.davidknotek.ytdownloader.gui;

import eu.davidknotek.ytdownloader.typy.FormatVidea;
import eu.davidknotek.ytdownloader.enums.TypVidea;
import eu.davidknotek.ytdownloader.services.ServiceAnalyzer;
import eu.davidknotek.ytdownloader.typy.Fronta;
import eu.davidknotek.ytdownloader.typy.VideoKeStazeni;
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

    private final ObservableList<VideoKeStazeni> seznamVideiKeStazeni = FXCollections.observableArrayList();
    private final Fronta fronta = new Fronta(seznamVideiKeStazeni);

    private final ServiceAnalyzer serviceAnalyzer = new ServiceAnalyzer();

    private FormatVidea vybranyVideoFormat;
    private FormatVidea vybranyAudioFormat;

    private boolean povolitVkladaniDoFronty = false;

    @FXML
    private TextField edtUrl;

    @FXML
    private Label lblNazevVidea;

    @FXML
    private ComboBox<String> cbxVideo;

    @FXML
    private ComboBox<String> cbxAudio;

    @FXML
    private ListView<VideoKeStazeni> lvFronta;

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
        lvFronta.setItems(seznamVideiKeStazeni);

        unbind();
        vlastniListViewModel();
    }

    /**
     * Analyzuje se URL YT videa
     * @param event událost
     */
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

    /**
     * Vložíme vybrané video a vybrané audio do fronty.
     * @param event událost
     */
    @FXML
    void onVlozitDoFronty(ActionEvent event) {
        if (povolitVkladaniDoFronty) {
            VideoKeStazeni videoKeStazeni = new VideoKeStazeni();
            videoKeStazeni.setVideoName(lblNazevVidea.getText());
            videoKeStazeni.setVideoCode(vybranyVideoFormat.getFormatCode());
            videoKeStazeni.setAudioCode(vybranyAudioFormat.getFormatCode());
            videoKeStazeni.setResolution(vybranyVideoFormat.getResolution());
            String typ = vybranyAudioFormat.getExtension().equals("m4a") ? typ = "mp4" : vybranyAudioFormat.getExtension();
            videoKeStazeni.setExtension(typ);
            fronta.vlozitDoFronty(videoKeStazeni);
        }
    }

    @FXML
    void onPrerusitStahovani(ActionEvent event) {
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
    void onOdstranitZFronty(ActionEvent event) {

    }

    @FXML
    void onPresunoutDolu(ActionEvent event) {

    }

    @FXML
    void onPresunoutNahoru(ActionEvent event) {

    }

    /**
     * Reakce na výběr formátu z comboboxu. Vybírá se video.
     * @param event událost
     */
    @FXML
    void onVybratVideoFormat(ActionEvent event) {
        onlyAudioList.clear();
        int index = cbxVideo.getSelectionModel().getSelectedIndex();
        if (index > -1) {
            vybranyVideoFormat = seznamVideoFormatu.get(index);
            if (vybranyVideoFormat.getTypVidea() == TypVidea.VIDEO_ONLY) {
                cbxAudio.setDisable(false);
                zobrazitSeznamAudia();
            } else {
                cbxAudio.setDisable(true);
            }
        }
    }

    /**
     * Reakce na výběr formátu z comboboxu. Vybírá se audio.
     * @param event událost
     */
    @FXML
    void onVybratAudioFormat(ActionEvent event) {
        int index = cbxAudio.getSelectionModel().getSelectedIndex();
        if (index > - 1) {
            vybranyAudioFormat = seznamAudioFormatu.get(index);
        }
    }

    /**
     * Ukončení aplikace
     * @param event událost
     */
    @FXML
    void onKonec(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    ///////////////////////////////////////////////////////
    // Soukromé metody
    ///////////////////////////////////////////////////////

    /**
     * Zrušení provázanosti s druhým vláknem, který analyzuje URL.
     * Reakce na skončení vlákna. Buď se to podařilo, přerušili jsme to, či nikoliv.
     */
    private void unbind() {
        serviceAnalyzer.setOnSucceeded(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatel.progressProperty().unbind();
            pbUkazatel.setProgress(0.0);
            rozstriditSeznamFormatu(serviceAnalyzer.getValue());
            zobrazitSeznamVidea();
            povolitVkladaniDoFronty = true;
        });

        serviceAnalyzer.setOnCancelled(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatel.progressProperty().unbind();
            lblZprava.setText("Analýza byla přerušena.");
            pbUkazatel.setProgress(0.0);
            povolitVkladaniDoFronty = false;
        });

        serviceAnalyzer.setOnFailed(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatel.progressProperty().unbind();
            lblZprava.setText("Vyskytl se problém při analýze URL.");
            pbUkazatel.setProgress(0.0);
            povolitVkladaniDoFronty = false;
        });
    }

    /**
     * Po analýze URL nám vznikl seznam formátů, který je potřeba rozstřídit
     * na video a audio formáty.
     * @param allFormatList kompletní seznam formátů
     */
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

    /**
     * V comboboxu zobrazíme seznam video formátů.
     */
    private void zobrazitSeznamVidea() {
        // TODO predelat s vlastnim modelem
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

    /**
     * V comboboxu zobrazíme seznam audio formátů.
     */
    private void zobrazitSeznamAudia() {
        // TODO predelat s vlastnim modelem
        for (FormatVidea format : seznamAudioFormatu) {
            String fileSize = format.getFileSize().equals("") ? "*" : format.getFileSize();
            onlyAudioList.add(format.getAudioQuality() + " / " +
                    format.getExtension() + " / " +
                    fileSize);
        }
        cbxAudio.getSelectionModel().selectFirst();
    }

    /**
     * Vlastni ListView model. Pouzity u fronty.
     */
    private void vlastniListViewModel() {
        lvFronta.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(VideoKeStazeni item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getVideoName().equals("")) {
                    setText(null);
                } else {
                    setText(item.getVideoName() + " " +
                            item.getResolution() + " " +
                            item.getVideoCode() + " " +
                            item.getAudioCode());
                }
            }
        });
    }
}
