package practicos.TPN6;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ejercicio1_SalaDeEstudioConLocks {
    public static void main(String[] args) {
        SalaDeEstudio sala = new SalaDeEstudio(5);
        Thread[] estudiantes = new Thread[15];

        for (int i = 0; i < estudiantes.length; i++) {
            estudiantes[i] = new Thread(new Estudiante(i, sala));
            estudiantes[i].start();
        }
    }

    public static class SalaDeEstudio {
        private final int cantMesas;
        private int cantMesasOcupadas;
        private Lock accesoSala;
        private Condition hayMesas;

        public SalaDeEstudio(int cantMesas) {
            this.cantMesas = cantMesas;
            this.cantMesasOcupadas = 0;
            accesoSala = new ReentrantLock();
            hayMesas = accesoSala.newCondition();
        }

        public void ocuparMesa(int id) throws InterruptedException {
            try {
                accesoSala.lock();
                while (cantMesasOcupadas >= cantMesas) {
                    // No puede entrar en la sala.
                    hayMesas.await();
                }
                // Entro en la sala.
                cantMesasOcupadas++;
            } finally {
                accesoSala.unlock();
            }
        }

        public void dejarMesa(int id) {
            accesoSala.lock();
            cantMesasOcupadas--;
            hayMesas.signalAll();
            accesoSala.unlock();
        }

    }

    public static class Estudiante implements Runnable {
        int id;
        SalaDeEstudio sala;

        public Estudiante(int id, SalaDeEstudio sala) {
            this.id = id;
            this.sala = sala;
        }

        @Override
        public void run() {
            try {
                System.out.println("Estudiante " + id + ": ESPERA una mesa.");
                sala.ocuparMesa(id);
                System.out.println("Estudiante " + id + ": TOMA una mesa.");
                Thread.sleep(5000);
                sala.dejarMesa(id);
                System.out.println("Estudiante " + id + ": DEJA una mesa.");
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Estudiante " + id + ": Interrumpido.");
            }

        }
    }
}