package ni.edu.uam.practicahilosjavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class ProcesadorController {

    @FXML
    private Label lblEstado;

    @FXML
    private Label lblPorcentaje;

    @FXML
    private ProgressBar barraProgreso;

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnDetener;

    @FXML
    private Button btnReiniciar;

    private Thread hiloProceso;

    @FXML
    private void iniciarProceso() {

        if (hiloProceso != null &&
                hiloProceso.isAlive()) {
            return;
        }

        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Procesando registros...");

        btnIniciar.setDisable(true);
        btnDetener.setDisable(false);
        btnReiniciar.setDisable(true);

        Runnable tarea = () -> {

            try {
                for (int i = 1; i <= 100; i++) {

                    if (Thread.currentThread()
                            .isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(100);

                    final int avance = i;

                    Platform.runLater(() -> {
                        barraProgreso.setProgress(
                                avance / 100.0
                        );

                        lblPorcentaje.setText(
                                avance + "%"
                        );
                    });
                }

                Platform.runLater(() -> {
                    lblEstado.setText(
                            "Proceso finalizado"
                    );
                    habilitarBotones();
                });

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                Platform.runLater(() -> {
                    lblEstado.setText(
                            "Proceso detenido"
                    );
                    habilitarBotones();
                });
            }
        };

        hiloProceso = new Thread(tarea);

        hiloProceso.setName("Hilo-Procesamiento");
        hiloProceso.setDaemon(true);
        hiloProceso.start();
    }

    @FXML
    private void detenerProceso() {

        if (hiloProceso != null &&
                hiloProceso.isAlive()) {

            btnDetener.setDisable(true);

            lblEstado.setText(
                    "Solicitando detención..."
            );

            hiloProceso.interrupt();
        }
    }

    @FXML
    private void reiniciarProceso() {

        if (hiloProceso != null &&
                hiloProceso.isAlive()) {
            return;
        }

        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Esperando inicio");

        habilitarBotones();
    }

    private void habilitarBotones() {
        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        btnReiniciar.setDisable(false);
    }

    public void detenerAlCerrar() {

        if (hiloProceso != null &&
                hiloProceso.isAlive()) {

            hiloProceso.interrupt();
        }
    }
}