package EjerciciosAdicionales.PlantaEmbotelladora;

public class Empaquetador implements Runnable {
    private int id;
    private PlantaEmbotelladora embotelladora;

    public Empaquetador(int id, PlantaEmbotelladora embotelladora){
        this.id = id;
        this.embotelladora = embotelladora;
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                embotelladora.sacarCaja();
                 Thread.sleep(4000);
                 embotelladora.reponerCaja();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("EMPAQUETADOR " + id + ": INTERRUMPIDO.");
        }
        
    }
}
