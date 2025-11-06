package TPN6.Pasteleria;

import java.util.Random;

public class Horno implements Runnable {
    private int id;
    private char tipo; // Tipo A, B o C.
    private Mostrador mostrador;

    public Horno(int id, char tipo, Mostrador mostrador) {
        this.id = id;
        this.tipo = tipo;
        this.mostrador = mostrador;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                switch (tipo) {
                    case 'A':
                        mostrador.producirPastelA();
                        System.out.println("HORNO " + id + "(" + tipo + "): PRODUJO pastel A.");
                        Thread.sleep((new Random().nextInt(3) + 1) * 1000);
                        break;
                    case 'B':
                        mostrador.producirPastelB();
                        System.out.println("HORNO " + id + "(" + tipo + "): PRODUJO pastel B.");
                        Thread.sleep((new Random().nextInt(5) + 1) * 1000);
                        break;
                    case 'C':
                        mostrador.producirPastelC();
                        System.out.println("HORNO " + id + "(" + tipo + "): PRODUJO pastel C.");
                        Thread.sleep((new Random().nextInt(7) + 1) * 1000);
                        break;
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("HORNO " + id + "(" + tipo + "): INTERRUMPIDO.");
        }
    }
}
