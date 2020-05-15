package eu.davidknotek.ytdownloader.gui;

import eu.davidknotek.ytdownloader.configuration.Konfigurace;
import eu.davidknotek.ytdownloader.enums.TypVidea;
import eu.davidknotek.ytdownloader.services.ServiceAnalyzer;
import eu.davidknotek.ytdownloader.services.ServiceStahovani;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {
    private final ObservableList<FormatVidea> onlyVideoList = FXCollections.observableArrayList();
    private final ObservableList<FormatVidea> onlyAudioList = FXCollections.observableArrayList();

    private Fronta fronta;

    private final ServiceAnalyzer serviceAnalyzer = new ServiceAnalyzer();
    private final ServiceStahovani serviceStahovani = new ServiceStahovani();

    private FormatVidea vybranyVideoFormat;
    private FormatVidea vybranyAudioFormat;

    private String urlVidea;

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
    private Label lblUkazatelPrubehu;

    @FXML
    private Label lblZbyvajiciCas;

    @FXML
    private ProgressBar pbUkazatelPrubehu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Konfigurace.nacistNastaveni();
        tfCestaUlozit.setText(Konfigurace.getCesta());

        ObservableList<VideoKeStazeni> seznamVideiKeStazeni = FXCollections.observableArrayList();
        fronta = new Fronta(seznamVideiKeStazeni);

        serviceStahovani.setLblUkazatelPrubehu(lblUkazatelPrubehu);
        serviceStahovani.setLblZbyvajiciCas(lblZbyvajiciCas);

        cbxVideo.setItems(onlyVideoList);
        cbxAudio.setItems(onlyAudioList);
        lvFronta.setItems(seznamVideiKeStazeni);

        unbindAnalyzer();
        unbindStahovani();
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
        urlVidea = edtUrl.getText();
        if (!urlVidea.trim().equals("")) {
            serviceAnalyzer.setUrl(urlVidea);
            serviceAnalyzer.restart();
            onlyVideoList.clear();
            onlyAudioList.clear();

            lblNazevVidea.textProperty().bind(serviceAnalyzer.titleProperty());
            lblZprava.textProperty().bind(serviceAnalyzer.messageProperty());
            pbUkazatelPrubehu.progressProperty().bind(serviceAnalyzer.progressProperty());
        }
    }

    /**
     * Stahneme YT video.
     * @param event udalost
     */
    @FXML
    void onStahnout(ActionEvent event) {
        if (fronta.getSeznamVideiKeStazeni().size() > 0) {
            serviceStahovani.setFronta(fronta);
            serviceStahovani.setCesta(tfCestaUlozit.getText().trim());
            serviceStahovani.restart();

            pbUkazatelPrubehu.progressProperty().bind(serviceStahovani.progressProperty());
            lblZprava.textProperty().bind(serviceStahovani.messageProperty());
        }
    }

    /**
     * Prerusime stahovani YT videa.
     * @param event udalost
     */
    @FXML
    void onPrerusitStahovani(ActionEvent event) {
        if (serviceStahovani.isRunning()) {
            serviceStahovani.cancel();
        }
    }

    /**
     * Vložíme vybrané video a vybrané audio do fronty ke stazeni.
     * @param event událost
     */
    @FXML
    void onVlozitDoFronty(ActionEvent event) {
        if (povolitVkladaniDoFronty) {
            VideoKeStazeni videoKeStazeni = new VideoKeStazeni();
            videoKeStazeni.setVideoName(lblNazevVidea.getText());
            videoKeStazeni.setVideoCode(vybranyVideoFormat.getFormatCode());
            videoKeStazeni.setResolution(vybranyVideoFormat.getResolution());
            videoKeStazeni.setUrl(urlVidea);
            videoKeStazeni.setExtensionVideo(vybranyVideoFormat.getExtension());
            videoKeStazeni.setExtensionAudio(vybranyAudioFormat.getExtension());
            videoKeStazeni.setFps(vybranyVideoFormat.getFps());
            videoKeStazeni.setDone("");
            String audioSoubor = stahovatAudioSoubor ? vybranyAudioFormat.getFormatCode() : null;
            videoKeStazeni.setAudioCode(audioSoubor);
            fronta.vlozitDoFronty(videoKeStazeni);
        }
    }

    @FXML
    void onVybratAdresar(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Vyberte adresář");

        File targetFile = new File(tfCestaUlozit.getText());
        if (targetFile.exists()) {
            directoryChooser.setInitialDirectory(targetFile);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = directoryChooser.showDialog(stage);

        if (file != null) {
            tfCestaUlozit.setText(file.getAbsolutePath());
        }
    }

    /**
     * Odstraní vybranou polozku z fronty ke stahnuti
     * @param event udalost
     */
    @FXML
    void onOdstranitZFronty(ActionEvent event) {
        if (lvFronta.getSelectionModel().getSelectedItem() != null) {
            fronta.odstranitZFronty(lvFronta.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Presune polozku fronty o jednu dolu.
     * @param event udalost
     */
    @FXML
    void onPresunoutDolu(ActionEvent event) {
        if (lvFronta.getSelectionModel().getSelectedItem() != null) {
            int index = fronta.presunoutDolu(lvFronta.getSelectionModel().getSelectedItem());
            lvFronta.getSelectionModel().select(index);
        }
    }

    /**
     * Presune polozku fronty o jednu nahoru
     * @param event udalost
     */
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
        konec();
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
    private void unbindAnalyzer() {
        serviceAnalyzer.setOnSucceeded(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatelPrubehu.progressProperty().unbind();
            pbUkazatelPrubehu.setProgress(0.0);
            rozstriditSeznamFormatu(serviceAnalyzer.getValue());
            povolitVkladaniDoFronty = !lblNazevVidea.getText().equals("");
        });

        serviceAnalyzer.setOnCancelled(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatelPrubehu.progressProperty().unbind();
            lblZprava.setText("Analýza byla přerušena.");
            pbUkazatelPrubehu.setProgress(0.0);
            povolitVkladaniDoFronty = false;
        });

        serviceAnalyzer.setOnFailed(workerStateEvent -> {
            lblNazevVidea.textProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatelPrubehu.progressProperty().unbind();
            lblZprava.setText("Vyskytl se problém při analýze URL.");
            pbUkazatelPrubehu.setProgress(0.0);
            povolitVkladaniDoFronty = false;
        });
    }

    /**
     * Zrušení provázanosti s druhým vláknem, který stahuje URL.
     * Reakce na skončení vlákna. Buď se to podařilo, přerušili jsme to, či nikoliv.
     */
    private void unbindStahovani() {
        serviceStahovani.setOnSucceeded(workerStateEvent -> {
            pbUkazatelPrubehu.progressProperty().unbind();
            lblZprava.textProperty().unbind();
            lblZprava.setText(serviceStahovani.getValue());
            lblUkazatelPrubehu.setText("");
            lblZbyvajiciCas.setText("");
            pbUkazatelPrubehu.setProgress(0.0);
        });

        serviceStahovani.setOnCancelled(workerStateEvent -> {
            pbUkazatelPrubehu.progressProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatelPrubehu.setProgress(0.0);
            lblUkazatelPrubehu.setText("");
            lblZbyvajiciCas.setText("");
            lblZprava.setText("Úloha byla přerušeno.");
        });

        serviceStahovani.setOnFailed(workStateEvent -> {
            pbUkazatelPrubehu.progressProperty().unbind();
            lblZprava.textProperty().unbind();
            pbUkazatelPrubehu.setProgress(0.0);
            lblUkazatelPrubehu.setText("");
            lblZbyvajiciCas.setText("");
            lblZprava.setText("Vyskytl se nějaký problém, úloha byla přerušena.");
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
                    setText(item.getDone() + " " +
                            item.getVideoName() + " " +
                            item.getResolution() + " " +
                            item.getFps());
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

    public void konec() {
        Stage stage = (Stage) lblNazevVidea.getScene().getWindow();
        Konfigurace.setCesta(tfCestaUlozit.getText());
        Konfigurace.ulozitNastaveni();
        stage.close();
    }
}
