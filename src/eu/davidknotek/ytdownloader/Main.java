package eu.davidknotek.ytdownloader;

import eu.davidknotek.ytdownloader.gui.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/main_window.fxml"));
        Parent root = (Parent) loader.load();
        MainWindowController controller = loader.getController();

        primaryStage.setTitle("YTDownloader");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
