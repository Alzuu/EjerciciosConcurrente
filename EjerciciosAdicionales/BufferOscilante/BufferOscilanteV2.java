package EjerciciosAdicionales.BufferOscilante;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class BufferOscilanteV2 {
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
        private int colaInsercion = 1;
        private int colaExtraccion = 2;

        private Semaphore mutex = new Semaphore(1);
        private Semaphore mutexc1 = new Semaphore(1);
        private Semaphore mutexc2 = new Semaphore(1);
        private Semaphore puedeExtraer = new Semaphore(0);

        private LinkedList<Integer> cola1 = new LinkedList<>();
        private LinkedList<Integer> cola2 = new LinkedList<>();

        public void extraer() throws InterruptedException {
            puedeExtraer.acquire();
            verificarColaExtraccion();
            mutex.acquire();
            if (colaExtraccion == 1) {
                mutex.release();
                mutexc1.acquire();
                cola1.removeFirst();
                System.out.println("EXTRACCIÓN EN COLA 1");
                mutexc1.release();
            } else {
                mutex.release();
                mutexc2.acquire();
                cola2.removeFirst();
                System.out.println("EXTRACCIÓN EN COLA 2");
                mutexc2.release();
            }
        }

        public void insertar(int algo) throws InterruptedException {
            mutex.acquire();
            if (colaInsercion == 1) {
                mutex.release();

                mutexc1.acquire();
                System.out.println("INSERCIÓN EN COLA 1");
                cola1.addLast(algo);
                mutexc1.release();
            } else {
                mutex.release();

                mutexc2.acquire();
                System.out.println("INSERCIÓN EN COLA 2");
                cola2.addLast(algo);
                mutexc2.release();
            }
            puedeExtraer.release();
        }

        private void verificarColaExtraccion() throws InterruptedException {
            mutex.acquire();
            if (colaExtraccion == 1) {
                mutex.release();
                // Verifico que tenga elementos, sino cambio.
                mutexc1.acquire();
                if (cola1.isEmpty()) {
                    mutex.acquire();
                    colaInsercion = 1;
                    colaExtraccion = 2;
                    mutex.release();
                }
                mutexc1.release();
            } else {
                mutex.release();

                mutexc2.acquire();
                if (cola2.isEmpty()) {
                    mutex.acquire();
                    colaInsercion = 2;
                    colaExtraccion = 1;
                    mutex.release();
                }
                mutexc2.release();
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
            try {
                while (true) {
                    buffer.insertar(new Random().nextInt(2));
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                System.out.println("INSERTOR INTERRUMPIDO.");
            }

        }
    }

    public static class Extractor implements Runnable {
        private Buffer buffer;

        public Extractor(Buffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    buffer.extraer();
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                System.out.println("EXTRACTOR INTERRUMPIDO.");
            }

        }
    }
}
