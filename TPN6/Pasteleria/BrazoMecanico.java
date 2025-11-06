package TPN6.Pasteleria;

public class BrazoMecanico implements Runnable {
    private int id;
    private Mesa mesa;

    public BrazoMecanico(int id, Mesa mesa) {
        this.id = id;
        this.mesa = mesa;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                mesa.reponerCaja();
                System.out.println("BRAZO MEC. " + id + ": REPUSO una CAJA.");
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("BRAZO MEC. " + id + ": INTERRUMPIDO.");
        }
    }
}
