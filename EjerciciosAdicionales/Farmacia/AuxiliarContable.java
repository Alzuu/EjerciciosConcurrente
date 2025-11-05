package EjerciciosAdicionales.Farmacia;

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
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("Contable " + id + ": INTERRUMPIDO.");
        }

    }
}
