package EjerciciosAdicionales.BobDylan;

public class Productor implements Runnable {
    private int id;
    private char tipo;
    private MesaDiscos mesa;
    private Disquera disquera;

    public Productor(int id, char tipo, MesaDiscos mesa, Disquera disquera) {
        this.id = id;
        this.tipo = tipo;
        this.mesa = mesa;
        this.disquera = disquera;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int cancion = mesa.sacarCancion(id, tipo);
                disquera.ponerCancionEnAlbum(id, cancion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("PRODUCTOR " + id + ": INTERRUMPIDO.");
        }

    }
}
