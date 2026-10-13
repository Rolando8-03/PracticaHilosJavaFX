package ni.edu.uam.practicahilosjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("procesador-view.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Práctica - Hilos en JavaFX");
        stage.setScene(scene);
        stage.show();

        ProcesadorController controller =
                loader.getController();

        stage.setOnCloseRequest(event ->
                controller.detenerAlCerrar()
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}