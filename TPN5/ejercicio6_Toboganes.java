package practicos.TPN5;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ejercicio6_Toboganes {
    public static void main(String[] args) {

    }

    public static class Mirador {
        private final int capacidadEscalera = 50;

        private Semaphore escalera = new Semaphore(capacidadEscalera); // Para proteger la COLA de la escalera.
        private Semaphore mutex = new Semaphore(1);
        private LinkedList<Visitante> colaEscalera = new LinkedList<>();
        private Semaphore hayVisitante = new Semaphore(0, true);

        int toboganVisitante;
        private Semaphore[] toboganLibre = { new Semaphore(1, true), new Semaphore(1, true) };

        private Semaphore permisoBajar = new Semaphore(0);

        public void comportamientoVisitante(Visitante visitante) throws InterruptedException {
            System.out.println("Visitante " + visitante.getId() + ": Espera por la escalera.");
            escalera.acquire(); // Esperar un lugar en la escalera.

            mutex.acquire(); // Entró en la escalera.
            colaEscalera.addLast(visitante);
            System.out.println("Visitante " + visitante.getId() + ": Está en la escalera.");
            System.out.println("Escalón N°: " + colaEscalera.indexOf(visitante));
            System.out.println("Cantidad Escalones Ocupados: " + (colaEscalera.size() - 1));
            mutex.release();
            hayVisitante.release(); // Avisa que está en la escalera.

            permisoBajar.acquire();
            mutex.acquire();
        }

        public void comportamientoEncargado() throws InterruptedException {
            while (true) {
                hayVisitante.acquire();

                int tobogan = darTobogan();
                mutex.acquire();
                toboganVisitante = tobogan;
                mutex.release();
                permisoBajar.release();

            }
        }

    }

    public static class Visitante implements Runnable {
        private int id;
        private Mirador mirador;

        public Visitante(int id, Mirador mirador) {
            this.id = id;
            this.mirador = mirador;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public void run() {
            try {
                mirador.comportamientoVisitante(this);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public static class Encargado implements Runnable {
        private Mirador mirador;

        public Encargado(Mirador mirador) {
            this.mirador = mirador;
        }

        @Override
        public void run() {
            try {
                mirador.comportamientoEncargado();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
