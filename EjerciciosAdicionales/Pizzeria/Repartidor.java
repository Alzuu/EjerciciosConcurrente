package EjerciciosAdicionales.Pizzeria;

import java.util.Random;

public class Repartidor implements Runnable {
    private final int turnosDescanso;
    private int turno = 0;
    private int id;
    private Pizzeria pizzeria;

    public Repartidor(int id, int turnosDescanso, Pizzeria pizzeria) {
        this.turnosDescanso = turnosDescanso;
        this.id = id;
        this.pizzeria = pizzeria;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                turno++;
                pizzeria.repartir();
                System.out.println("REPARTIDOR " + id + ": se VA A REPARTIR.");
                Thread.sleep((new Random().nextInt(3) + 1) * 1000);
                if (turno % turnosDescanso == 0) {
                    System.out.println("REPARTIDOR " + id + " tomando un DESCANSO.");
                    Thread.sleep(5000);
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("REPARTIDOR " + id + ": INTERRUMPIDO");
        }
    }
}
