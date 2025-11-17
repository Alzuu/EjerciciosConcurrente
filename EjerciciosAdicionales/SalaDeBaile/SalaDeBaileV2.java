package EjerciciosAdicionales.SalaDeBaile;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class SalaDeBaileV2 {
    public static void main(String[] args) {
        Sala sala = new Sala();
        Thread[] personas = new Thread[20];
        Thread patova = new Thread(new Patova(sala));
        patova.start();
        for (int i = 0; i < personas.length; i++) {
            personas[i] = new Thread(new Persona(i,sala));
            personas[i].start();
        }
    }

    public static class Sala {
        private final int capSalaBaile = 10;
        private int cantFila1 = 0;
        private int cantFila2 = 0;

        private Semaphore mutex = new Semaphore(1, true);
        private Semaphore enFila1 = new Semaphore(0, true);
        private Semaphore enFila2 = new Semaphore(0, true);
        private Semaphore hayLugarSala = new Semaphore(capSalaBaile);
        private Semaphore entrarABailar = new Semaphore(0, true);

        public void hacerFila(int id, int fila) throws InterruptedException {
            // Si son pareja entra uno en cada fila, si no es en la que tiene menos.
            if (fila == 0) {
                mutex.acquire();
                fila = (cantFila1 <= cantFila2) ? 1 : 2;
                mutex.release();
            }
            if (fila == 1) {
                hacerFilaEn1(id);
            } else {
                hacerFilaEn2(id);
            }

        }

        private void hacerFilaEn1(int id) throws InterruptedException {
            mutex.acquire();
            cantFila1++;
            mutex.release();
            enFila1.release();
            System.out.println("PERSONA " + id + ": EN FILA 1.");
            entrarABailar.acquire();
            mutex.acquire();
            cantFila1--;
            mutex.release();
        }

        private void hacerFilaEn2(int id) throws InterruptedException {
            mutex.acquire();
            cantFila2++;
            mutex.release();
            enFila2.release();
            System.out.println("PERSONA " + id + ": EN FILA 2.");
            entrarABailar.acquire();
            mutex.acquire();
            cantFila2--;
            mutex.release();
        }

        public void hacerEntrar() throws InterruptedException {
            // Se fija si hay espacio para 2 personas en la sala.
            hayLugarSala.acquire(2);
            // Agarra uno de cada fila.
            enFila1.acquire();
            enFila2.acquire();
            // Los hace pasar.
            entrarABailar.release(2);
        }

        public void salirPistaBaile() throws InterruptedException {
            hayLugarSala.release();
        }
    }

    public static class Patova implements Runnable{
        private Sala sala;

        public Patova (Sala sala){
            this.sala = sala;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    sala.hacerEntrar();
                }
            } catch (Exception e) {
                
            }
        }
    }

    public static class Persona implements Runnable {
        private int id;
        private Sala salaDeBaile;

        public Persona(int id, Sala sala){
            this.id = id;
            this.salaDeBaile = sala;
        }

        @Override
        public void run() {
            try {
                salaDeBaile.hacerFila(id, 0);
                System.out.println("PERSONA " + id + ": BAILANDO");
                Thread.sleep((new Random().nextInt(4) + 1) * 1000);
                salaDeBaile.salirPistaBaile();
                System.out.println("PERSONA " + id + ": SALIÓ");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("BAILARIN INTERRUMPIDO.");
            }

        }
    }

}
