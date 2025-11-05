package EjerciciosAdicionales.PlantaEmbotelladora;

public class Embotellador implements Runnable {
    private int id;
    private char tipo; // 'A' para Agua, 'V' para Vino.
    private PlantaEmbotelladora embotelladora;

    public Embotellador(int id, char tipo, PlantaEmbotelladora embotelladora) {
        this.id = id;
        this.tipo = tipo;
        this.embotelladora = embotelladora;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (tipo == 'A') {
                    embotelladora.producirAgua();
                    System.out.println("EMBOTELLADOR " + id + ": PRODUJO AGUA.");
                    Thread.sleep(3000);
                } else {
                    embotelladora.producirVino();
                    System.out.println("EMBOTELLADOR " + id + ": PRODUJO VINO.");
                    Thread.sleep(5000);
                }
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("EMBOTELLADOR " + id + ": INTERRUMPIDO.");
        }
    }
}
