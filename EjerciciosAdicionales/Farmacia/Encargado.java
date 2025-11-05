package EjerciciosAdicionales.Farmacia;

import java.util.Random;

public class Encargado implements Runnable{
    private final int id;
    private int idFichaEnc;
    private final Farmacia farmacia;

    public Encargado(int id, Farmacia farmacia) {
        this.id = id;
        this.idFichaEnc = 0;
        this.farmacia = farmacia;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String ficha = idFichaEnc + " Ficha Cont";
                idFichaEnc++;
                farmacia.consumirEncargado(id, ficha);
                Random random = new Random();
                int tAleat = (random.nextInt(3) + 1) * 1000;
                Thread.sleep(tAleat);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("Encargado " + id + ": INTERRUMPIDO.");
        }

    }
}
