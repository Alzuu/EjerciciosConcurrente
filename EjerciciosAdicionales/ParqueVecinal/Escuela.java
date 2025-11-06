package EjerciciosAdicionales.ParqueVecinal;

import java.util.Random;

public class Escuela implements Runnable {
    private int id;
    private final int totalPersonas;
    private Parque parque;

    public Escuela(int id, int cantAlumnos, int cantMaestros, Parque parque) {
        this.id = id;
        this.totalPersonas = cantAlumnos + cantMaestros;
        this.parque = parque;
    }

    @Override
    public void run() {
        try {
            System.out.println("ESCUELA " + id + ": ESPERA al parque.");
            parque.escuelaEntra(totalPersonas);
            System.out.println("ESCUELA " + id + ": ENTRA al parque.");
            Thread.sleep((new Random().nextInt(5) + 1) * 1000);
            System.out.println("ESCUELA " + id + ": SALE del parque.");
            parque.escuelaSale(totalPersonas);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("ESCUELA " + id + ": INTERRUMPIDA.");
        }
    }
}
