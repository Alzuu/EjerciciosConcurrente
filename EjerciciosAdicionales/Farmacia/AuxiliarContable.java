package EjerciciosAdicionales.Farmacia;

import java.util.Random;

public class AuxiliarContable implements Runnable {
    private final int id;
    private int idFichaCont;
    private final Farmacia farmacia;

    public AuxiliarContable(int id, Farmacia farmacia) {
        this.id = id;
        this.idFichaCont = 0;
        this.farmacia = farmacia;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String ficha = idFichaCont + " Ficha Cont";
                idFichaCont++;
                farmacia.producirFichaGeneral(id, ficha);
                Random random = new Random();
                int tAleat = (random.nextInt(3) + 1) * 1000;
                Thread.sleep(tAleat);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("Contable " + id + ": INTERRUMPIDO.");
        }

    }
}
