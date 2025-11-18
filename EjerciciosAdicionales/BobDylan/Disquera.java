package EjerciciosAdicionales.BobDylan;

import java.util.concurrent.Semaphore;

public class Disquera {
    private Album album;

    private Semaphore mutexAlbum = new Semaphore(1);
    private Semaphore ponerAlbum = new Semaphore(1);
    private Semaphore hayAlbum = new Semaphore(0);

    public void ponerAlbum(Album nuevoAlbum) throws InterruptedException {
        // De manager.
        // Duerme hasta que le digan que ponga un album.
        ponerAlbum.acquire();
        mutexAlbum.acquire();
        this.album = nuevoAlbum;
        System.out.println("MANAGER PUSO un albúm: " + album.getNombre());
        mutexAlbum.release();
        hayAlbum.release();
    }

    public void ponerCancionEnAlbum(int id, int duracionCancion) throws InterruptedException {
        // De Productor.
        // Duerme hasta que haya un albúm.
        hayAlbum.acquire();
        mutexAlbum.acquire();
        if (duracionCancion + album.getDuracionActual() > album.getDuracionTotal()) {
            // Si sobrepasa, aviso que hay que sacar el album.
            mutexAlbum.release();
            System.out.println("NO ENTRA CANCION EN ALBUM.");
            ponerAlbum.release();
            hayAlbum.acquire();
        } else {
            mutexAlbum.release();
        }
        mutexAlbum.acquire();
        album.agregarCancion(duracionCancion);
        System.out.println("DURACION ALBUM: " + album.getDuracionActual());
        System.out.println("PRODUCTOR: " + id + ", AGREGÓ CANCIÓN al albúm.");
        mutexAlbum.release();
        hayAlbum.release();
    }
}
