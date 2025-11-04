package practicos.TPN6;

import java.util.Random;
//import java.util.concurrent.ThreadLocalRandom;

public class ejercicio1_SalaDeEstudio {
    public static void main(String[] args) {
        SalaDeEstudio sala = new SalaDeEstudio(5);
        Thread[] estudiantes = new Thread[15];

        for (int i = 0; i < estudiantes.length; i++) {
            estudiantes[i] = new Thread(new Estudiante(i,sala));
            estudiantes[i].start();
        }
    }

    public static class SalaDeEstudio {
        private final int cantMesas;
        private int cantMesasOcupadas;

        public SalaDeEstudio(int cantMesas) {
            this.cantMesas = cantMesas;
            this.cantMesasOcupadas = 0;
        }

        public synchronized void tomarUnaMesa(int id) throws InterruptedException {
            while (this.cantMesasOcupadas == cantMesas) {
                // Si todas las mesas están ocupadas espera.
                System.out.println("Estudiante " + id + ": ESPERA, todas las mesas ocupadas.");
                wait();
            }
            // Si pudo tomar una mesa.
            System.out.println("Estudiante " + id + ": se SIENTA en una mesa.");
            cantMesasOcupadas++;
        }

        public synchronized void dejarMesa(int id) throws InterruptedException {
            // Si termina de estudiar.
            cantMesasOcupadas--;
            System.out.println("Estudiante " + id + ": se VA de la mesa.");
            notify();
        }
    }

    public static class Estudiante implements Runnable {
        private int id;
        private SalaDeEstudio sala;

        public Estudiante(int id, SalaDeEstudio sala) {
            this.id = id;
            this.sala = sala;
        }

        private int generarTiempoDeEstudio() {
            Random random = new Random();
            return (random.nextInt(3) + 1) * 1000;
            // return ThreadLocalRandom.current().nextInt(1, 4) * 1000;
        }

        @Override
        public void run() {
            try {
                sala.tomarUnaMesa(id);
                int tiempoDeEstudio = generarTiempoDeEstudio();
                System.out.println("Estudiante " + this.id + ": ESTUDIA durante: " + tiempoDeEstudio + " ms.");
                Thread.sleep(tiempoDeEstudio);
                sala.dejarMesa(id);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Estudiante " + id + ": interrumpido.");
            }

        }
    }
}
