package eu.davidknotek.ytdownloader;

import eu.davidknotek.ytdownloader.configuration.Konfigurace;
import eu.davidknotek.ytdownloader.gui.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Konfigurace.nacistNastaveni();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/eu/davidknotek/fxml/main_window.fxml"));
        Parent root = (Parent) loader.load();
        MainWindowController controller = loader.getController();

        // Reakce na stisk křížku a uzavření programu
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            controller.konec();
        });

        primaryStage.getIcons().add(new Image(getClass().getResource("/eu/davidknotek/icons/YTD-96.png").toString()));

        primaryStage.setTitle("YTDownloader " + getVersion());
        double windowWidth = Konfigurace.getWindowWidth().equals("") ? 800 : Double.parseDouble(Konfigurace.getWindowWidth());
        double windowHeight = Konfigurace.getWindowHeight().equals("") ? 400 : Double.parseDouble(Konfigurace.getWindowHeight());
        primaryStage.setScene(new Scene(root, windowWidth, windowHeight));
        primaryStage.show();
    }

    /**
     * Získáme verzi programu z pom.xml souboru přes projent.properties
     *
     * @return verzi programu
     */
    private String getVersion() {
        try {
            final Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
            return properties.getProperty("version");

        } catch (IOException exception) {
            exception.printStackTrace();
            return "";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
