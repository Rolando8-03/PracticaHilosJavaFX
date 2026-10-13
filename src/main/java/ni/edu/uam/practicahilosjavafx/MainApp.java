package ni.edu.uam.practicahilosjavafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private ProcesadorController procesador;
    private MonitorController monitor;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("principal-view.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(MainApp.class.getResource("estilos.css").toExternalForm());

        // fx:include también registra el controlador de cada vista incluida.
        procesador = (ProcesadorController) loader.getNamespace().get("practicaController");
        monitor = (MonitorController) loader.getNamespace().get("retoController");

        stage.setTitle("NicaData Solutions | Hilos en JavaFX");
        stage.setScene(scene);
        stage.setMinWidth(820);
        stage.setMinHeight(760);
        stage.setOnCloseRequest(event -> detenerProcesos());
        stage.show();
    }

    private void detenerProcesos() {
        if (procesador != null) procesador.detenerAlCerrar();
        if (monitor != null) monitor.detenerAlCerrar();
    }

    @Override
    public void stop() {
        detenerProcesos();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
