package ni.edu.uam.practicahilosjavafx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class MonitorController {
    @FXML private Label lblEstadoClientes;
    @FXML private Label lblPorcentajeClientes;
    @FXML private Label lblRegistrosClientes;
    @FXML private ProgressBar barraClientes;
    @FXML private Button btnIniciarClientes;
    @FXML private Button btnDetenerClientes;
    @FXML private Button btnReiniciarClientes;

    @FXML private Label lblEstadoProductos;
    @FXML private Label lblPorcentajeProductos;
    @FXML private Label lblRegistrosProductos;
    @FXML private ProgressBar barraProductos;
    @FXML private Button btnIniciarProductos;
    @FXML private Button btnDetenerProductos;
    @FXML private Button btnReiniciarProductos;
    @FXML private Button btnIniciarAmbos;
    @FXML private Button btnReiniciarAmbos;

    private Proceso clientes;
    private Proceso productos;
    private boolean cerrado;

    @FXML
    private void initialize() {
        clientes = new Proceso("Clientes", 100, 100, lblEstadoClientes,
                lblPorcentajeClientes, lblRegistrosClientes, barraClientes,
                btnIniciarClientes, btnDetenerClientes, btnReiniciarClientes);
        productos = new Proceso("Productos", 50, 140, lblEstadoProductos,
                lblPorcentajeProductos, lblRegistrosProductos, barraProductos,
                btnIniciarProductos, btnDetenerProductos, btnReiniciarProductos);
        clientes.reiniciar();
        productos.reiniciar();
    }

    @FXML private void iniciarClientes() { clientes.iniciar(); }
    @FXML private void detenerClientes() { clientes.detener(); }
    @FXML private void reiniciarClientes() { clientes.reiniciar(); }
    @FXML private void iniciarProductos() { productos.iniciar(); }
    @FXML private void detenerProductos() { productos.detener(); }
    @FXML private void reiniciarProductos() { productos.reiniciar(); }

    @FXML
    private void iniciarAmbos() {
        if (cerrado || clientes.hilo != null || productos.hilo != null) return;
        clientes.iniciar();
        productos.iniciar();
    }

    @FXML
    private void reiniciarAmbos() {
        if (clientes.hilo != null || productos.hilo != null) return;
        clientes.reiniciar();
        productos.reiniciar();
    }

    private void actualizarBotonesGenerales() {
        boolean ocupado = clientes.hilo != null || productos.hilo != null;
        btnIniciarAmbos.setDisable(ocupado);
        btnReiniciarAmbos.setDisable(ocupado);
    }

    public void detenerAlCerrar() {
        cerrado = true;
        if (clientes != null && clientes.hilo != null) clientes.hilo.interrupt();
        if (productos != null && productos.hilo != null) productos.hilo.interrupt();
    }

    // Cada instancia conserva su propio hilo y sus propios controles.
    // El código se comparte, pero los procesos son independientes.
    private class Proceso {
        private final String nombre;
        private final int total;
        private final long espera;
        private final Label estado;
        private final Label porcentaje;
        private final Label registros;
        private final ProgressBar barra;
        private final Button iniciar;
        private final Button detener;
        private final Button reiniciar;
        private Thread hilo;
        private boolean deteniendo;

        private Proceso(String nombre, int total, long espera, Label estado,
                        Label porcentaje, Label registros, ProgressBar barra,
                        Button iniciar, Button detener, Button reiniciar) {
            this.nombre = nombre;
            this.total = total;
            this.espera = espera;
            this.estado = estado;
            this.porcentaje = porcentaje;
            this.registros = registros;
            this.barra = barra;
            this.iniciar = iniciar;
            this.detener = detener;
            this.reiniciar = reiniciar;
        }

        private void iniciar() {
            if (cerrado || hilo != null) return;
            barra.setProgress(0);
            porcentaje.setText("0%");
            registros.setText("0 de " + total + " registros");
            estado.setText("Procesando " + nombre.toLowerCase() + "...");
            deteniendo = false;
            botones(false);

            Runnable tarea = () -> {
                Thread actual = Thread.currentThread();
                boolean completado = false;
                try {
                    for (int i = 1; i <= total; i++) {
                        if (actual.isInterrupted()) throw new InterruptedException();
                        Thread.sleep(espera);
                        final int avance = i;
                        Platform.runLater(() -> {
                            if (cerrado || hilo != actual || deteniendo) return;
                            barra.setProgress(avance / (double) total);
                            porcentaje.setText((avance * 100 / total) + "%");
                            registros.setText(avance + " de " + total + " registros");
                        });
                    }
                    completado = true;
                } catch (InterruptedException e) {
                    actual.interrupt();
                } finally {
                    final boolean resultado = completado;
                    Platform.runLater(() -> {
                        if (cerrado || hilo != actual) return;
                        estado.setText(resultado && !deteniendo
                                ? "Proceso finalizado" : "Proceso detenido");
                        hilo = null;
                        deteniendo = false;
                        botones(true);
                        actualizarBotonesGenerales();
                    });
                }
            };

            // Iniciar clientes y productos crea dos objetos Thread distintos.
            hilo = new Thread(tarea, "Hilo-" + nombre);
            hilo.setDaemon(true);
            hilo.start();
            actualizarBotonesGenerales();
        }

        private void detener() {
            if (hilo == null || deteniendo) return;
            deteniendo = true;
            detener.setDisable(true);
            estado.setText("Solicitando detención...");
            hilo.interrupt();
        }

        private void reiniciar() {
            if (cerrado || hilo != null) return;
            barra.setProgress(0);
            porcentaje.setText("0%");
            registros.setText("0 de " + total + " registros");
            estado.setText("Esperando inicio");
            botones(true);
        }

        private void botones(boolean disponible) {
            iniciar.setDisable(!disponible);
            detener.setDisable(disponible);
            reiniciar.setDisable(!disponible);
        }
    }
}
