package practicos.TPN5;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class ejercicio7_Babuinos {
    public static void main(String[] args) {
        Cañon cañon = new Cañon();
        Thread[] babuinos = new Thread[20];

        for (int i = 0; i < babuinos.length; i++) {
            babuinos[i] = new Thread(new Babuino(i, cañon));
            babuinos[i].start();
        }
    }

    public static class Cañon {
        private final int capacidadMaxSoga = 5;

        private int enSoga = 0;
        private int esperandoLadoIzq = 0;
        private int esperandoLadoDer = 0;
        private char direccion = 'N';

        private Semaphore mutex = new Semaphore(1);
        private Semaphore capSoga = new Semaphore(capacidadMaxSoga);
        private Semaphore babuinosLadoIzq = new Semaphore(0);
        private Semaphore babuinosLadoDer = new Semaphore(0);

        public void comportamientoBabuino(Babuino babuino) throws InterruptedException {
            char lado = babuino.getLado();

            if (lado == 'I') {
                cruzarDeIzqADer(babuino);
            } else {
                cruzarDeDerAIzq(babuino);
            }
        }

        private void cruzarDeIzqADer(Babuino babuino) throws InterruptedException {
            int id = babuino.getId();
            System.out.println("Babuino " + id + ": Espera para cruzar de Izq a Der.");

            mutex.acquire();
            esperandoLadoIzq++;

            while (!puedeEntrar('I')) {
                // Si no puede entrar.
                mutex.release();
                babuinosLadoIzq.acquire(); // Espera su turno.
                mutex.acquire();
            }
            // Va a entrar.
            esperandoLadoIzq--;
            enSoga++;
            if (direccion == 'N') {
                // Si no había nadie en la cuerda cambio para avisar que están cruzando en I.
                direccion = 'I';
            }
            capSoga.acquire(); // Espera a que este disponible.

            // Si hay lugar y hay más esperando, despierto otro.
            if (enSoga < capacidadMaxSoga && esperandoLadoIzq > 0) {
                babuinosLadoIzq.release();
            }
            mutex.release();

            // Cruza.
            System.out.println("Babuino " + id + ": Empieza a cruzar.");
            Thread.sleep(3000);
            System.out.println("Babuino " + id + ": Terminó de cruzar de (I -> D).");

            // Sale de la soga.
            mutex.acquire();
            enSoga--;
            capSoga.release();
            if (enSoga == 0) {
                if (esperandoLadoDer > 0) {
                    direccion = 'D';
                    // Si hay esperando del otro lado paso.
                    babuinosLadoDer.release(capacidadMaxSoga);
                } else if (esperandoLadoIzq > 0) {
                    direccion = 'I';
                    // Si quedan más esperando cruzan TODOS juntos.
                    babuinosLadoIzq.release(capacidadMaxSoga);
                } else {
                    // Si no hay nadie de ningún lado termina.
                    direccion = 'N';
                }
            } else {
                // Todavía esta ocupado, y hay lugar paso otro del mismo lado.
                if (direccion == 'I' && enSoga < capacidadMaxSoga && esperandoLadoIzq > 0) {
                    babuinosLadoIzq.release();
                }
            }
            mutex.release();
            babuino.cambiarLado();
        }

        private void cruzarDeDerAIzq(Babuino babuino) throws InterruptedException {
           
        }

        private boolean puedeEntrar(char lado) {
            return enSoga < capacidadMaxSoga && (direccion == 'N' || direccion == lado);
        }

    }

    public static class Babuino implements Runnable {
        private int id;
        private char lado;
        private Cañon cañon;

        public Babuino(int id, Cañon cañon) {
            this.id = id;
            this.lado = (id % 2 == 0) ? 'I' : 'D';
            this.cañon = cañon;
        }

        public int getId() {
            return this.id;
        }

        public char getLado() {
            return this.lado;
        }

        public void cambiarLado() {
            this.lado = (lado == 'I') ? 'D' : 'I';
        }

        @Override
        public void run() {
            try {
                try {
                    cañon.comportamientoBabuino(this);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}
