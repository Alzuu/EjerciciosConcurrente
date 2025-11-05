package EjerciciosAdicionales.PlantaEmbotelladora;

import java.util.Random;

public class Camion implements Runnable {
    private int id;
    private PlantaEmbotelladora embotelladora;

    public Camion(int id, PlantaEmbotelladora embotelladora) {
        this.id = id;
        this.embotelladora = embotelladora;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("CAMION " + id + ": ESPERA para vaciar el almacen.");
                embotelladora.vaciarAlmacen();
                System.out.println("CAMION " + id + ": VACIÓ el almacen.");
                Random random = new Random();
                int tAleat = (random.nextInt(6) + 5) * 1000;
                Thread.sleep(tAleat);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("CAMION " + id + ": INTERRUMPIDO.");
        }

    }
}
