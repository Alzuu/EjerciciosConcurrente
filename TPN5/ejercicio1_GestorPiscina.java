package practicos.TPN5;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class ejercicio1_GestorPiscina {
    public static void main(String[] args) {
        Piscina piscina = new Piscina();
        int cantPersonas = 10;
        Thread[] personas = new Thread[cantPersonas];
        for (int i = 0; i < cantPersonas; i++) {
            personas[i] = new Thread(new Persona(i, piscina));
            personas[i].start();
        }
        try {
            for (int i = 0; i < cantPersonas; i++) {
                personas[i].join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class Piscina {
        private static final int capacidadPiscina = 5;
        private Semaphore semCapacidadPiscina = new Semaphore(capacidadPiscina);

        public void comportamientoPersona(int id) throws InterruptedException {
            Thread.sleep(3000);
            System.out.println("La Persona " + id + " está yendo a la piscina.");
            semCapacidadPiscina.acquire();
            Thread.sleep(2000);
            System.out.println("La Persona " + id + " entra en la piscina.");
            int tiempo = generadorTiempoAleatorio();
            Thread.sleep(tiempo);
            System.out.println("La Persona " + id + " estuvo en la piscina durante " + (tiempo/1000) + " seg");
            semCapacidadPiscina.release();
        }

        private int generadorTiempoAleatorio() {
            Random random = new Random();
            int aleatorio = (random.nextInt(9) + 1) * 1000;
            return aleatorio;
        }
    }

    public static class Persona implements Runnable {
        int id;
        Piscina piscina;

        public Persona(int id, Piscina piscina) {
            this.id = id;
            this.piscina = piscina;
        }

        @Override
        public void run() {
            try {
                piscina.comportamientoPersona(id);
            } catch (InterruptedException e) {
                System.out.println("La Persona " + id + "se cayó.");
            }

        }
    }
}
