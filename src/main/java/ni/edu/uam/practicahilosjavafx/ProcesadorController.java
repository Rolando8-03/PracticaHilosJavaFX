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

    // Estas variables se modifican desde el hilo de JavaFX.
    private Thread hiloProceso;
    private boolean deteniendo;
    private boolean cerrado;
    private int procesados = 0;

    @FXML
    private void iniciarProceso() {
        if (cerrado || hiloProceso != null || procesados >= 100) {
            return;
        }

        // Continúa desde el siguiente registro pendiente.
        final int primerRegistro = procesados + 1;

        deteniendo = false;
        lblEstado.setText("Procesando registros...");
        habilitarBotones(false);

        Runnable tarea = () -> {
            Thread actual = Thread.currentThread();
            boolean completado = false;

            try {
                for (int i = primerRegistro; i <= 100; i++) {
                    if (actual.isInterrupted()) {
                        throw new InterruptedException();
                    }

                    Thread.sleep(100);

                    final int avance = i;

                    Platform.runLater(() -> {
                        if (cerrado || hiloProceso != actual || deteniendo) {
                            return;
                        }

                        // Guarda el último registro mostrado.
                        procesados = avance;

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
                    if (cerrado || hiloProceso != actual) {
                        return;
                    }

                    lblEstado.setText(
                            resultado && !deteniendo
                                    ? "Proceso finalizado"
                                    : "Proceso detenido"
                    );

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
        if (hiloProceso == null || deteniendo) {
            return;
        }

        deteniendo = true;
        btnDetener.setDisable(true);
        lblEstado.setText("Solicitando detención...");

        // Conserva el avance para continuar con Iniciar.
        hiloProceso.interrupt();
    }

    @FXML
    private void reiniciarProceso() {
        if (cerrado || hiloProceso != null) {
            return;
        }

        procesados = 0;

        barraProgreso.setProgress(0);
        lblPorcentaje.setText("0%");
        lblRegistros.setText("0 de 100 registros");
        lblEstado.setText("Esperando inicio");

        habilitarBotones(true);
    }

    private void habilitarBotones(boolean disponible) {
        btnIniciar.setDisable(!disponible || procesados >= 100);
        btnDetener.setDisable(disponible);
        btnReiniciar.setDisable(!disponible);
        btnBloqueo.setDisable(!disponible);
    }

    // Demostración intencional del bloqueo del hilo de JavaFX.
    @FXML
    private void procesoBloqueante() {
        if (cerrado || hiloProceso != null) {
            return;
        }

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

        if (hiloProceso != null) {
            hiloProceso.interrupt();
        }
    }
}