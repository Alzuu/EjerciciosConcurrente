package EjerciciosAdicionales.BufferOscilante;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class BufferOscilante {
    public static void main(String[] args) {
        Buffer buffer = new Buffer();
        Thread insertor1 = new Thread(new Insertor(buffer));
        Thread insertor2 = new Thread(new Insertor(buffer));
        Thread insertor3 = new Thread(new Insertor(buffer));
        Thread extractor = new Thread(new Extractor(buffer));

        insertor1.start();
        insertor2.start();
        insertor3.start();
        extractor.start();
    }

    public static class Buffer {
        private char colaInsercion = 'A';

        private LinkedList<Integer> colaA = new LinkedList<>();
        private LinkedList<Integer> colaB = new LinkedList<>();

        private Semaphore mutex = new Semaphore(1);
        private Semaphore puedeExtraer = new Semaphore(0);

        public int extraer() throws InterruptedException {
            int elementoExtraido;
            puedeExtraer.acquire();
            verificarColaExtraccion();
            mutex.acquire();
            if (colaInsercion == 'A') {
                // Debo extraer de B.
                elementoExtraido = colaB.removeFirst();
                System.out.println("EXTRACCIÓN EN COLA B");
            } else {
                // Debo extraer de A.
                elementoExtraido = colaA.removeFirst();
                System.out.println("EXTRACCIÓN EN COLA A");
            }
            mutex.release();
            return elementoExtraido;
        }

        public void insertar(int numero) throws InterruptedException {
            // Inserta en la cola que indica el char.
            mutex.acquire();
            if (colaInsercion == 'A') {
                colaA.addLast(numero);
                System.out.println("INSERCIÓN EN COLA A");
            } else {
                colaB.addLast(numero);
                System.out.println("INSERCIÓN EN COLA B");
            }
            mutex.release();
            // Verifica en que cola se debe insertar
            puedeExtraer.release();
        }

        private void verificarColaExtraccion() throws InterruptedException {
            mutex.acquire();
            if (colaInsercion == 'A') {
                // Si se inserta en la cola A y se extrae en la cola B.
                if (colaB.isEmpty()) {
                    // Si la cola B está vacía cambio para extraer en A.
                    colaInsercion = 'B';
                }
            } else {
                // Si se inserta en la cola B y se extrae en la cola A.
                if (colaA.isEmpty()) {
                    // Si la cola A está vacía cambio para extraer en B.
                    colaInsercion = 'A';
                }
            }
            mutex.release();
        }
    }

    public static class Extractor implements Runnable {
        private Buffer buffer;

        public Extractor(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    buffer.extraer();
                } catch (Exception e) {
                    System.out.println("EXTRACTOR INTERRUMPIDO.");
                }
            }
        }
    }

    public static class Insertor implements Runnable {
        private Buffer buffer;

        public Insertor(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    buffer.insertar(new Random().nextInt(2));
                    Thread.sleep(3000);
                } catch (Exception e) {
                    System.out.println("INSERTOR INTERRUMPIDO.");
                }
            }
        }
    }
}
