package practicos.TPN5;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class ejercicio4_TrenTuristico {
    public static void main(String[] args) {
        Tren tren = new Tren();
        Thread[] pasajeros = new Thread[100];
        Thread conductor = new Thread(new ControlTren(tren));
        conductor.start();

        for (int i = 0; i < pasajeros.length; i++) {
            pasajeros[i] = new Thread(new Pasajero(i, tren));
            pasajeros[i].start();
        }

        try {
            conductor.join();
            for (int i = 0; i < pasajeros.length; i++) {
                pasajeros[i].join();
            }
        } catch (Exception e) {
            System.out.println(" . . . . . CODIGO INTERRUMPIDO . . . . . ");
        }
        System.out.println(" . . . . .  CODIGO FINALIZADO . . . . . ");
    }

    public static class Tren {
        private final int capacidadTren = 30;
        private final LinkedList<Pasajero> pasajerosArribaTren = new LinkedList<>();

        // Para los Pasajeros.
        private Semaphore mutex = new Semaphore(1);
        private Semaphore semTren = new Semaphore(capacidadTren);
        private Semaphore semTickets = new Semaphore(1);

        // Para el controlador del tren.
        private Semaphore semArrancar = new Semaphore(0);
        private Semaphore semBajar = new Semaphore(0);

        public void comportamientoPasajero(Pasajero pasajero) throws InterruptedException {
            int idPasajero = pasajero.getId();
            comprarTicket(idPasajero); // Compra el ticket.
            System.out.println("Pasajero " + idPasajero + ": Espera para subir al tren.");
            semTren.acquire(); // Espera al tren.
            subirATren(pasajero); // Se sube al tren.
            // Termina el recorrido.
            bajarDelTren(pasajero);// Se baja del tren.
        }

        private void comprarTicket(int id) throws InterruptedException {
            System.out.println("Pasajero " + id + ": Está haciendo fila para comprar un ticket.");
            semTickets.acquire();
            // Pasajero.agregarTicket.
            System.out.println("Pasajero " + id + ": Compró un ticket.");
            semTickets.release();
        }

        private void subirATren(Pasajero pasajero) throws InterruptedException {
            int id = pasajero.getId();
            mutex.acquire();
            System.out.println("Pasajero " + id + ": Subió al tren.");
            pasajerosArribaTren.add(pasajero);
            if (pasajerosArribaTren.size() == capacidadTren) {
                System.out.println("Pasajero " + id + ": Es el último, el tren va LLENO.");
                semArrancar.release();
                mutex.release();
            } else {
                System.out.println("Pasajeros en Tren: " + pasajerosArribaTren.size());
                mutex.release();
            }
        }

        private void bajarDelTren(Pasajero pasajero) throws InterruptedException {
            int id = pasajero.getId();
            semBajar.acquire();
            pasajerosArribaTren.remove(pasajero);
            System.out.println("Pasajero " + id + ": Bajó del tren.");
            if (pasajerosArribaTren.size() == 0) {
                System.out.println("Pasajero " + id + ": Fue el último en bajar.");
                // Hasta que no se baje el último no pueden subir los demás.
                semTren.release(capacidadTren);
            } else {
                System.out.println("Pasajeros restantes en Tren: " + pasajerosArribaTren.size());
                semBajar.release();
            }
        }

        private void comportamientoConductorTren(int tiempoDeRecorrido, int cantVueltas) throws InterruptedException {
            System.out.println("El conductor espera a arrancar el TREN...");
            semArrancar.acquire();// Espera a que se llene el tren.
            System.out.println(". . . . . RECORRIENDO . . . . . ");
            System.out.println("Número de Vuelta: " + cantVueltas);
            Thread.sleep(tiempoDeRecorrido);// Cuando se llena empieza a manejar.
            System.out.println("Terminó el recorrido del TREN.");
            semBajar.release();// Termina el recorrido y avisa que se pueden bajar.
        }
    }

    public static class Pasajero implements Runnable {
        int id;
        Tren tren;

        public Pasajero(int id, Tren tren) {
            this.id = id;
            this.tren = tren;
        }

        public int getId() {
            return this.id;
        }

        @Override
        public void run() {
            try {
                tren.comportamientoPasajero(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static class VendedorTickets implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub

        }
    }

    public static class ControlTren implements Runnable {
        private final int cantPaseosTren = 5;
        private final int tiempoDeRecorrido = 3000;
        Tren tren;

        public ControlTren(Tren tren) {
            this.tren = tren;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < cantPaseosTren; i++) {
                    tren.comportamientoConductorTren(tiempoDeRecorrido, i);
                }
            } catch (Exception e) {
                System.out.println("El conductor del Tren se cansó de conducir.");
            }

        }
    }
}
