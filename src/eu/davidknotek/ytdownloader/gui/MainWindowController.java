package eu.davidknotek.ytdownloader.gui;

import eu.davidknotek.ytdownloader.enums.TypVidea;
import eu.davidknotek.ytdownloader.services.ServiceAnalyzer;
import eu.davidknotek.ytdownloader.typy.FormatVidea;
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
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private final ObservableList<FormatVidea> onlyVideoList = FXCollections.observableArrayList();
    private final ObservableList<FormatVidea> onlyAudioList = FXCollections.observableArrayList();

    private final ObservableList<VideoKeStazeni> seznamVideiKeStazeni = FXCollections.observableArrayList();
    private final Fronta fronta = new Fronta(seznamVideiKeStazeni);

    private final ServiceAnalyzer serviceAnalyzer = new ServiceAnalyzer();

    private FormatVidea vybranyVideoFormat;
    private FormatVidea vybranyAudioFormat;

    private boolean povolitVkladaniDoFronty = false;
    private boolean stahovatAudioSoubor = false;

    @FXML
    private TextField edtUrl;

    @FXML
    private Label lblNazevVidea;

    @FXML
    private ComboBox<FormatVidea> cbxVideo;

    @FXML
    private ComboBox<FormatVidea> cbxAudio;

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
        videoFormatyCbxModel();
        audioFormatyCbxModel();
        frontaListViewModel();
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
            videoKeStazeni.setResolution(vybranyVideoFormat.getResolution());
            String typ = vybranyAudioFormat.getExtension().equals("m4a") ? typ = "mp4" : vybranyAudioFormat.getExtension();
            videoKeStazeni.setExtension(typ);
            String audioSoubor = stahovatAudioSoubor ? vybranyAudioFormat.getFormatCode() : null;
            videoKeStazeni.setAudioCode(audioSoubor);
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
        if (lvFronta.getSelectionModel().getSelectedItem() != null) {
            fronta.odstranitZFronty(lvFronta.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    void onPresunoutDolu(ActionEvent event) {
        if (lvFronta.getSelectionModel().getSelectedItem() != null) {
            int index = fronta.presunoutDolu(lvFronta.getSelectionModel().getSelectedItem());
            lvFronta.getSelectionModel().select(index);
        }
    }

    @FXML
    void onPresunoutNahoru(ActionEvent event) {
        if (lvFronta.getSelectionModel().getSelectedItem() != null) {
            int index = fronta.presunoutNahoru(lvFronta.getSelectionModel().getSelectedItem());
            lvFronta.getSelectionModel().select(index);
        }
    }

    /**
     * Reakce na výběr formátu z comboboxu. Vybírá se video.
     * Nektera videa jsou jen video only a pak je potreba stahnout i audio soubor.
     * @param event událost
     */
    @FXML
    void onVybratVideoFormat(ActionEvent event) {
        int index = cbxVideo.getSelectionModel().getSelectedIndex();
        if (index > -1) {
            vybranyVideoFormat = onlyVideoList.get(index);
            if (vybranyVideoFormat.getTypVidea() == TypVidea.VIDEO_ONLY) {
                cbxAudio.setDisable(false);
                stahovatAudioSoubor = true;
            } else {
                cbxAudio.setDisable(true);
                stahovatAudioSoubor = false;
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
            vybranyAudioFormat = onlyAudioList.get(index);
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


    /* ****************************************************
     *
     * Soukromé metody
     *
     ******************************************************/

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
    private void rozstriditSeznamFormatu(ObservableList<FormatVidea> allFormatList) {
        onlyAudioList.clear();
        onlyVideoList.clear();
        for (FormatVidea format : allFormatList) {
            if (format.getTypVidea() == TypVidea.AUDIO_ONLY) {
                onlyAudioList.add(format);
            } else {
                onlyVideoList.add(format);
            }
        }
        cbxVideo.getSelectionModel().selectFirst();
        cbxAudio.getSelectionModel().selectFirst();
    }

    /**
     * Vlastni ListView model. Pouzity u fronty.
     */
    private void frontaListViewModel() {
        lvFronta.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(VideoKeStazeni item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getVideoName().equals("")) {
                    setText(null);
                } else {
                    String audioCode = item.getAudioCode() == null ? "" : item.getAudioCode();
                    setText(item.getVideoName() + " " +
                            item.getResolution() + " " +
                            item.getVideoCode() + " " +
                            audioCode);
                }
            }
        });
    }

    /**
     * Vlastní ComboBox model. Pouzit u seznamu videi.
     * Callback je interfejs a prvni typovy parametr je navratovym typem metody call.
     * Druhy typovy parametr je parametrem metody call.
     * Je vytvoren objekt ListCell, který zobrazi polozky v comboboxu, reaguje na vybrani...
     * Ta ma pretizenou metodu updateItem, ktera naplni combobox
     */
    private void videoFormatyCbxModel() {
        Callback<ListView<FormatVidea>, ListCell<FormatVidea>> cellFactory = param -> new ListCell<>() {
            @Override
            protected void updateItem(FormatVidea item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getFormatCode().equals("")) {
                    setText(null);
                } else {
                    String fps = item.getFps().equals("") ? "*" : item.getFps();
                    String fileSize = item.getFileSize().equals("") ? "*" : item.getFileSize();
                    setText(item.getResolution() + " / " +
                            fps + " / " +
                            item.getExtension() + " / " +
                            fileSize);
                }
            }
        };
        cbxVideo.setButtonCell(cellFactory.call(null));
        cbxVideo.setCellFactory(cellFactory);
    }

    /**
     * Vlastní combobox model. Pouzit v seznamu audii.
     */
    private void audioFormatyCbxModel() {
        Callback<ListView<FormatVidea>, ListCell<FormatVidea>> cellFactory = param -> new ListCell<>() {
            @Override
            protected void updateItem(FormatVidea item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getFormatCode().equals("")) {
                    setText(null);
                } else {
                    String fileSize = item.getFileSize().equals("") ? "*" : item.getFileSize();
                    setText(item.getAudioQuality() + " / " +
                            item.getExtension() + " / " +
                            fileSize);
                }
            }
        };
        cbxAudio.setButtonCell(cellFactory.call(null));
        cbxAudio.setCellFactory(cellFactory);
    }

//    private void videoFormatyCbxModel2() {
//        Callback<ListView<FormatVidea>, ListCell<FormatVidea>> cellFactory = new Callback<>() {
//            @Override
//            public ListCell<FormatVidea> call(ListView<FormatVidea> param) {
//                return new ListCell<>() {
//                    @Override
//                    protected void updateItem(FormatVidea item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (item == null | empty) {
//                            setGraphic(null);
//                        } else {
//                            setText(item.getResolution());
//                        }
//                    }
//                };
//            }
//        };
//        cbxVideo.setButtonCell(cellFactory.call(null));
//        cbxVideo.setCellFactory(cellFactory);
//    }
}
