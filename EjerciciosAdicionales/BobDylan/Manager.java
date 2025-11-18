package EjerciciosAdicionales.BobDylan;

public class Manager implements Runnable {
    private int nombreAlbum;
    private Disquera disquera;
    private Album album;

    public Manager(Disquera disquera) {
        this.nombreAlbum = 0;
        this.disquera = disquera;
    }

    private void crearAlbum() {
        this.album = new Album(nombreAlbum);
        nombreAlbum++;
    }

    @Override
    public void run() {
        try {
            while (true) {
                crearAlbum();
                disquera.ponerAlbum(album);
            }
        } catch (Exception e) {
           e.printStackTrace();
           System.out.println("MANAGER INTERRUMPIDO.");
        }

    }
}
