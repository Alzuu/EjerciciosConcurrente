package EjerciciosAdicionales.Pizzeria;

import java.util.Random;

public class Cocinero implements Runnable {
    private int id;
    private char tipo; // 'N' para Napolitana, 'V' para Vegana
    private Pizzeria pizzeria;

    public Cocinero(int id, char tipo, Pizzeria pizzeria) {
        this.id = id;
        this.tipo = tipo;
        this.pizzeria = pizzeria;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (tipo == 'N') {
                    pizzeria.cocinarNapolitana();
                    System.out.println("COCINERO " + id + ": COCINA una NAPOLITANA.");
                } else {
                    pizzeria.cocinarVegana();
                    System.out.println("COCINERO " + id + ": COCINA una VEGANA.");
                }
                Thread.sleep((new Random().nextInt(3) + 1) * 1000);
                System.out.println("COCINERO " + id + " AVISA al REPARTIDOR.");
                pizzeria.avisarAlRepartidor();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("COCINERO " + id + ": INTERRUMPIDO.");
        }
    }
}
