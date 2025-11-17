package EjerciciosAdicionales.SalaDeBaile;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class SalaDeBaile {
    public static void main(String[] args) {

    }

    public static class Sala {
        private final int capSala = 10;
        private int cantFila1 = 0;
        private int cantFila2 = 0;
        private int bailando = 0;

        private LinkedList<Integer> fila1 = new LinkedList<>();
        private LinkedList<Integer> fila2 = new LinkedList<>();

        private Semaphore mutex = new Semaphore(1);
        private Semaphore mutexSala = new Semaphore(1);
        private Semaphore entrarFila1 = new Semaphore(1, true);
        private Semaphore entrarFila2 = new Semaphore(1, true);

        public void hacerFila(int id, int fila) throws InterruptedException {
            mutex.acquire();
            // Va a entrar en la fila que tenga menos gente
            if (fila == 0) {
                fila = (cantFila1 <= cantFila2) ? 1 : 2;
            }
            if (fila == 1) {
                System.out.println("PERSONA " + id + ": ENTRÓ EN LA FILA 1.");
                cantFila1++;
                fila1.addLast(id);
                // Espera a que le digan que entre a bailar.
                entrarFila1.acquire();
            } else {
                System.out.println("PERSONA " + id + ": ENTRÓ EN LA FILA 2.");
                cantFila2++;
                fila2.addLast(id);
                // Espera a que le digan que entre a bailar.
                entrarFila2.acquire();
            }
            mutex.release();
        }

        public void hacerEntrar() throws InterruptedException {
            mutexSala.acquire();
            if (bailando < capSala) {
                if (cantFila1 > 0 && cantFila2 > 0) {
                    entrarFila1.release();
                    entrarFila2.release();
                }
            }
            mutexSala.release();
        }

        public void salir() throws InterruptedException {

        }

    }

    public static class Persona implements Runnable {
        private int id;
        private Sala salaDeBaile;

        @Override
        public void run() {
            // TODO Auto-generated method stub

        }
    }
}
