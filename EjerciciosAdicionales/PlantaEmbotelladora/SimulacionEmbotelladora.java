package EjerciciosAdicionales.PlantaEmbotelladora;

public class SimulacionEmbotelladora {
    public static void main(String[] args) {
        PlantaEmbotelladora embotelladora = new PlantaEmbotelladora();
        Thread[] embotelladores = new Thread[4];
        Thread empaquetador = new Thread(new Empaquetador(0, embotelladora));
        Thread camion = new Thread(new Camion(0, embotelladora));

        for (int i = 0; i < embotelladores.length; i++) {
            char tipo = (i%2 == 0)? 'A':'V';
            embotelladores[i] = new Thread(new Embotellador(i, tipo, embotelladora));
            embotelladores[i].start();
        }
        empaquetador.start();
        camion.start();
    }
}
