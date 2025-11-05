package EjerciciosAdicionales.Farmacia;

public class AuxiliarFarmacia implements Runnable {
    private final int id;
    private int idFichaGen;
    private final Farmacia farmacia;

    public AuxiliarFarmacia(int id, Farmacia farmacia) {
        this.id = id;
        this.idFichaGen = 0;
        this.farmacia = farmacia;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String ficha = idFichaGen + " Ficha Gen";
                idFichaGen++;
                farmacia.consumirContable(id, ficha);
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("Auxiliar Farmacia " + id + ": INTERRUMPIDO.");
        }

    }
}
