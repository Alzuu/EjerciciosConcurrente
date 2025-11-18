package EjerciciosAdicionales.BobDylan;

import java.util.Random;

public class BobDylan implements Runnable {
    private String nombre;
    private MesaDiscos mesa;

    public BobDylan(MesaDiscos mesa) {
        this.nombre = "Bob Dylan";
        this.mesa = mesa;
    }

    public String getNombre() {
        return nombre;
    }

    private char sacarCancion() {
        char tipoCancion;
        int random = new Random().nextInt(4);
        switch (random) {
            case 0:
                tipoCancion = 'R';
                break;
            case 1:
                tipoCancion = 'F';
                break;

            case 2:
                tipoCancion = 'B';
                break;

            default:
                tipoCancion = 'C';
                break;
        }
        return tipoCancion;
    }

    @Override
    public void run() {
        try {
            while (true) {
                char tipoCancion = sacarCancion();
                int duracion = mesa.ponerCancion(tipoCancion);
                Thread.sleep(duracion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("BOB INTERRUMPIDO.");
        }
    }
}
