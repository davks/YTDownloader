package eu.davidknotek.ytdownloader;

import eu.davidknotek.ytdownloader.configuration.Konfigurace;
import eu.davidknotek.ytdownloader.gui.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private String VERSION = "1.0.4";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Konfigurace.nacistNastaveni();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/main_window.fxml"));
        Parent root = (Parent) loader.load();
        MainWindowController controller = loader.getController();

        // Reakce na stisk křížku a uzavření programu
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            controller.konec();
        });

        primaryStage.setTitle("YTDownloader " + VERSION);
        double windowWidth = Konfigurace.getWindowWidth().equals("") ? 800 : Double.parseDouble(Konfigurace.getWindowWidth());
        double windowHeight = Konfigurace.getWindowHeight().equals("") ? 400 : Double.parseDouble(Konfigurace.getWindowHeight());
        primaryStage.setScene(new Scene(root, windowWidth, windowHeight));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
