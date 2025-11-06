package TPN6.Pasteleria;

import java.util.Random;

public class Empaquetador implements Runnable {
    int id;
    Mostrador mostrador;
    Mesa mesa;

    public Empaquetador(int id, Mostrador mostrador, Mesa mesa) {
        this.id = id;
        this.mostrador = mostrador;
        this.mesa = mesa;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Pastel pastel = mostrador.tomarPastel();
                char tipo = pastel.getTipo();
                System.out.println("EMPAQUETADOR " + id + ": TOMO un pastel (" + tipo + ")");
                Thread.sleep((new Random().nextInt(3) + 2) * 1000);
                System.out.println("EMPAQUETADOR " + id + ": PUSO un pastel (" + tipo + ") en CAJA.");
                mesa.ponerPastel(pastel);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("EMPAQUETADOR " + id + ": INTERRUMPIDO.");
        }
    }
}
