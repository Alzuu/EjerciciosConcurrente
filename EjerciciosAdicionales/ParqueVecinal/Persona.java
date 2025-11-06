package EjerciciosAdicionales.ParqueVecinal;

import java.util.Random;

public class Persona implements Runnable {
    private int id;
    private boolean esVecino;
    private Parque parque;

    public Persona(int id, boolean esVecino, Parque parque) {
        this.id = id;
        this.esVecino = esVecino;
        this.parque = parque;
    }

    @Override
    public void run() {
        try {
            if (esVecino) {
                System.out.println("VECINO " + id + ": ESPERA entrar al parque.");
                parque.vecinoEntra();
                System.out.println("VECINO " + id + ": ENTRA al parque.");
                Thread.sleep((new Random().nextInt(4) + 1) * 1000);
                System.out.println("VECINO " + id + ": SALE del parque.");
                parque.personaSale();
            } else {
                System.out.println("PERSONA " + id + ": ESPERA al parque.");
                parque.personaEntra();
                System.out.println("PERSONA " + id + ": ENTRA al parque.");
                Thread.sleep((new Random().nextInt(4) + 1) * 1000);
                System.out.println("PERSONA " + id + ": SALE del parque.");
                parque.personaSale();

            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("PERSONA " + id + ": INTERRUMPIDA.");
        }
    }
}
