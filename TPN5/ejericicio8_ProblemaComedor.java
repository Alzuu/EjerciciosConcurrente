package practicos.TPN5;

import java.util.concurrent.Semaphore;

public class ejericicio8_ProblemaComedor {
    public static void main(String[] args) {

    }

    public static class Comedor {
        private final int capacidadComederos = 2;
        private int cantAnimalesLlenosTotal = 0;
        private int cantAnimalesLlenos = 0;
        private char turno = 'N';

        private Semaphore mutex = new Semaphore(1);

        // Para saber el total de animales que quieren comer.
        private Semaphore totalAnimales = new Semaphore(0);
        private Semaphore totalPerros = new Semaphore(0);
        private Semaphore totalGatos = new Semaphore(0);
        // Turnos para comer.
        private Semaphore permisoComerPerros = new Semaphore(0);
        private Semaphore permisoComerGatos = new Semaphore(0);

        private Semaphore terminoDeComer = new Semaphore(0);

        public void gestionComederos() throws InterruptedException {
            boolean forzarCambioTurno = false;

            while (true) {
                // Espera hasta que haya algún animal.
                totalAnimales.acquire();
                // Gestión de turnos.
                boolean turnoPerros = false;
                boolean turnoGatos = false;

                if (turno == 'G' && totalPerros.availablePermits() > 0) {
                    // Si hay que cambiar el turno a los perros y hay perros esperando.

                } else if (turno == 'P' && totalGatos.availablePermits() > 0) {
                    // Si hay que cambiar el turno a los gatos y hay gatos esperando.
                } else if(turno == 'N' && totalPerros.availablePermits()>0) {
                   // Si no hay nadie y hay perros esperando.
                } else if(turno == 'N' && totalGatos.availablePermits()>0){
                    // Si no hay nadie y hay gatos esperando.
                }
                /*
                 * Casos:
                 * que hayan perros esperando y sean los primeros
                 * que hayan gatos esperando y sean los primeros.
                 * que hayan perros
                 * que hayan gatos
                 */
            }
        }
    }

    public static class Gestor implements Runnable {

    }

    public static class Perro implements Runnable {

    }

    public static class Gato implements Runnable {

    }
}
