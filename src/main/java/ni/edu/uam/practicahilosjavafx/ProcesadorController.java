package ni.edu.uam.practicahilosjavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProcesadorController {
    @FXML private Label lblEstado;
    @FXML private Label lblPorcentaje;
    @FXML private Label lblRegistros;
    @FXML private ProgressBar barraProgreso;
    @FXML private Button btnIniciar;
    @FXML private Button btnDetener;
    @FXML private Button btnReiniciar;
    @FXML private Button btnBloqueo;

    // Estas variables se consultan y modifican solo en el hilo de JavaFX.
    private Thread hiloProceso;
    private boolean deteniendo;
    private boolean cerrado;

    @FXML
    private void iniciarProceso() {
        if (cerrado || hiloProceso != null) return;

        deteniendo = false;
        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblRegistros.setText("0 de 100 registros");
        lblEstado.setText("Procesando registros...");
        habilitarBotones(false);

        Runnable tarea = () -> {
            Thread actual = Thread.currentThread();
            boolean completado = false;
            try {
                for (int i = 1; i <= 100; i++) {
                    if (actual.isInterrupted()) throw new InterruptedException();
                    Thread.sleep(100);
                    final int avance = i;
                    Platform.runLater(() -> {
                        // Ignora actualizaciones pendientes después de pedir detener.
                        if (cerrado || hiloProceso != actual || deteniendo) return;
                        barraProgreso.setProgress(avance / 100.0);
                        lblPorcentaje.setText(avance + "%");
                        lblRegistros.setText(avance + " de 100 registros");
                    });
                }
                completado = true;
            } catch (InterruptedException e) {
                actual.interrupt();
            } finally {
                final boolean resultado = completado;
                Platform.runLater(() -> {
                    if (cerrado || hiloProceso != actual) return;
                    lblEstado.setText(resultado && !deteniendo
                            ? "Proceso finalizado" : "Proceso detenido");
                    hiloProceso = null;
                    deteniendo = false;
                    habilitarBotones(true);
                });
            }
        };

        hiloProceso = new Thread(tarea, "Hilo-Procesamiento");
        hiloProceso.setDaemon(true);
        hiloProceso.start();
    }

    @FXML
    private void detenerProceso() {
        if (hiloProceso == null || deteniendo) return;
        deteniendo = true;
        btnDetener.setDisable(true);
        lblEstado.setText("Solicitando detención...");
        hiloProceso.interrupt();
    }

    @FXML
    private void reiniciarProceso() {
        if (cerrado || hiloProceso != null) return;
        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblRegistros.setText("0 de 100 registros");
        lblEstado.setText("Esperando inicio");
        habilitarBotones(true);
    }

    private void habilitarBotones(boolean disponible) {
        btnIniciar.setDisable(!disponible);
        btnDetener.setDisable(disponible);
        btnReiniciar.setDisable(!disponible);
        btnBloqueo.setDisable(!disponible);
    }

    // Ejemplo del paso 6: esta pausa bloquea la ventana intencionalmente.
    @FXML
    private void procesoBloqueante() {
        if (cerrado || hiloProceso != null) return;
        lblEstado.setText("Procesando...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        lblEstado.setText("Demostración finalizada");
    }

    public void detenerAlCerrar() {
        cerrado = true;
        if (hiloProceso != null) hiloProceso.interrupt();
    }
}
