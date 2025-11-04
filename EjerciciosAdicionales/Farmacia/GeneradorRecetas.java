package EjerciciosAdicionales.Farmacia;

public class GeneradorRecetas implements Runnable {
    private int id;
    private Farmacia farmacia;

    public GeneradorRecetas(int id, Farmacia farmacia){
        this.id = id;
        this.farmacia = farmacia;
    }
    
    @Override
    public void run() {
        try {
            farmacia.generarReceta();
        Thread.sleep(3000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("GeneradorRecetas INTERRUMPIDO.");
        }
        
        
    }
}
