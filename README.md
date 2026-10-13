# JavaFX_Hilos_Practica

Práctica de hilos de Programación de Aplicaciones de Escritorio.

**Estudiante:** Rolando Enrique Mayorga Mena

La aplicación simula el procesamiento de registros de NicaData Solutions S.A. Tiene una pestaña para la práctica de un hilo y otra para el reto final, donde se procesan clientes y productos al mismo tiempo.

## Abrir y ejecutar

Se necesita JDK 21 y conexión a Internet para que Maven descargue JavaFX la primera vez.

1. Descomprimir el proyecto.
2. En IntelliJ IDEA, seleccionar **File > Open** y abrir la carpeta `JavaFX_Hilos_Practica`.
3. Configurar **Project SDK** en Java 21 y cargar el proyecto Maven.
4. Abrir el panel **Maven**, entrar a **Plugins > javafx** y ejecutar **javafx:run**.

También se puede ejecutar desde una terminal, dentro de la carpeta del proyecto:

```bash
mvn clean javafx:run
```

Si IntelliJ muestra `release version 21 not supported`, revisar que el SDK del proyecto y el JRE del ejecutor de Maven estén configurados en Java 21.

## Práctica: un hilo

El botón **Iniciar** procesa 100 registros, con una pausa de 100 milisegundos entre cada uno. El proceso completo tarda aproximadamente 10 segundos.

**Detener** interrumpe el hilo y conserva el avance alcanzado. **Reiniciar** devuelve la barra y el porcentaje a cero; después se presiona Iniciar para volver a procesar. También se puede iniciar directamente después de detener o finalizar: comienza desde el primer registro.

El botón **Probar bloqueo (5 s)** corresponde al ejemplo del paso 6. Pausa toda la ventana de forma intencional para comparar ese comportamiento con el procesamiento en segundo plano.

## Reto final: dos hilos

- **Clientes:** 100 registros, con pausas de 100 ms. Duración aproximada: 10 segundos.
- **Productos:** 50 registros, con pausas de 140 ms. Duración aproximada: 7 segundos.

**Iniciar ambos** ejecuta los dos procesos. Cada uno tiene su propia barra, porcentaje, contador y botones. Detener clientes no detiene productos, y viceversa. Un proceso detenido puede reiniciarse o iniciarse de nuevo mientras el otro continúa trabajando.

Los botones generales quedan deshabilitados mientras alguno de los procesos está activo. Los controles individuales siguen el estado de su propio proceso.

## Cómo se implementaron los hilos

En `ProcesadorController.java`, un `Runnable` contiene el recorrido de los 100 registros. Se crea un `Thread` con esa tarea y se inicia con `start()`.

En `MonitorController.java`, la clase interna `Proceso` reúne los controles y el hilo de cada proceso. Se crean dos instancias, una para clientes y otra para productos. Al iniciarlas, cada una crea su propio objeto `Thread` a partir de un `Runnable`.

Las pausas con `Thread.sleep()` ocurren en los hilos de procesamiento. Los cambios en las barras y etiquetas se envían al hilo de JavaFX mediante `Platform.runLater()`.

Para detener se usa `interrupt()`. El ciclo consulta `isInterrupted()` y también atiende `InterruptedException` cuando se interrumpe una pausa. Los botones se habilitan nuevamente al terminar la tarea. Mientras existe una ejecución pendiente, el controlador rechaza otro inicio del mismo proceso.

Al cerrar la ventana se solicita la interrupción de todos los hilos. Las actualizaciones pendientes se descartan si la ventana ya se cerró o si se solicitó detener el proceso. Los hilos también se configuran como daemon.

## Pregunta de análisis

**¿Por qué no debemos ejecutar procesos prolongados directamente dentro de los eventos de JavaFX?**

Porque esos eventos se ejecutan en el JavaFX Application Thread, que también atiende los controles y dibuja la interfaz. Si ese hilo se queda esperando o haciendo un trabajo largo, la ventana deja de responder hasta que termine. Por eso el procesamiento se ejecuta en otro hilo y las actualizaciones visuales se realizan con `Platform.runLater()`.

En la demostración de bloqueo, incluso el mensaje “Procesando...” puede no llegar a dibujarse antes de la pausa: el mismo hilo que debería mostrarlo está ocupado ejecutando `sleep()`.

## Archivos principales

- `MainApp.java`: carga las vistas y detiene los procesos al cerrar.
- `ProcesadorController.java`: práctica de un hilo y ejemplo de bloqueo.
- `MonitorController.java`: reto de clientes y productos.
- `principal-view.fxml`: ventana con las dos pestañas.
- `procesador-view.fxml`: interfaz de la práctica base.
- `monitor-view.fxml`: interfaz del reto final.
- `estilos.css`: apariencia de los controles.
- `module-info.java`: módulos de JavaFX y acceso del cargador FXML.
- `pom.xml`: dependencias y configuración de Maven.

## Comprobaciones

1. Iniciar la práctica y comprobar que aumenta el porcentaje.
2. Mover la ventana durante el procesamiento.
3. Detener antes del 100% y comprobar que conserva el avance.
4. Reiniciar y comprobar que vuelve a 0%.
5. Iniciar de nuevo y esperar hasta el 100%.
6. En el reto, iniciar ambos procesos y detener solamente clientes.
7. Comprobar que productos continúa avanzando.
8. Reiniciar clientes e iniciarlo mientras productos sigue trabajando.
9. Esperar hasta que ambos lleguen al 100%.
10. Iniciar otra ejecución y cerrar la ventana durante el procesamiento.

## Capturas

Las imágenes de la carpeta `capturas` muestran la ejecución de la aplicación.

![Práctica en ejecución](capturas/01_practica_en_ejecucion.png)

![Práctica detenida](capturas/02_practica_detenida.png)

![Práctica reiniciada](capturas/03_practica_reiniciada.png)

![Práctica finalizada](capturas/04_practica_finalizada.png)

![Dos procesos simultáneos](capturas/05_reto_concurrente.png)

![Clientes detenido y productos activo](capturas/06_detencion_independiente.png)

![Reto finalizado](capturas/07_reto_finalizado.png)

## Entrega

Subir el contenido de esta carpeta a un repositorio llamado `JavaFX_Hilos_Practica`. El repositorio debe conservar el código fuente, los archivos FXML, este README y las capturas.
