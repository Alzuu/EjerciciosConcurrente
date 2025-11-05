package EjerciciosAdicionales.PlantaEmbotelladora;

import java.util.concurrent.Semaphore;

public class PlantaEmbotelladora {
    private final int capAlmacen = 10;
    private final int capCaja = 10;

    private Semaphore mutex = new Semaphore(1);
    // Para el embotellador.
    private Semaphore semVino = new Semaphore(capCaja);
    private int cantVinoEnCaja = 0;
    private Semaphore semAgua = new Semaphore(capCaja);
    private int cantAguaEnCaja = 0;
    // Para el empaquetador.
    private Semaphore sacarCaja = new Semaphore(0);
    private Semaphore semAlmacen = new Semaphore(capAlmacen);
    private int cantCajasAlmacen = 0;
    // Para el camión.
    private Semaphore sacarAlmacen = new Semaphore(0);

    public void producirAgua() throws InterruptedException {
        // Espera hasta que haya espacio en la caja de agua.
        semAgua.acquire();
        mutex.acquire();
        cantAguaEnCaja++;
        if (cantAguaEnCaja == capCaja) {
            System.out.println("caja de AGUA LLENA.");
            sacarCaja.release();
        }
        mutex.release();
    }

    public void producirVino() throws InterruptedException {
        // Espera hasta que haya espacio en la caja de vino.
        semVino.acquire();
        mutex.acquire();
        cantVinoEnCaja++;
        if (cantVinoEnCaja == capCaja) {
            System.out.println("caja de VINO LLENA.");
            sacarCaja.release();
        }
        mutex.release();
    }

    public void sacarCaja() throws InterruptedException {
        // Espera hasta que le avisen de sacar una caja.
        sacarCaja.acquire();
        // Espero a que el almacen tenga lugar.
        semAlmacen.acquire();
        mutex.acquire();
        // La agrego en el almacen.
        cantCajasAlmacen++;
        if (cantCajasAlmacen == capAlmacen) {
            // Si llene el almacen aviso al camion.
            System.out.println("ALMACEN LLENO");
            sacarAlmacen.release();
        }
        mutex.release();
    }

    public void reponerCaja() throws InterruptedException {
        mutex.acquire();
        // Reviso cual caja tengo que reponer.
        if (cantAguaEnCaja == capCaja) {
            // Saco la de agua y pongo otra (simulo).
            System.out.println("EMPAQUETADOR REPUSO una caja de AGUA.");
            cantAguaEnCaja = 0;
            semAgua.release(capCaja);
        } else {
            // Saco la de vino y pongo otra (simulo).
             System.out.println("EMPAQUETADOR REPUSO una caja de VINO.");
            cantVinoEnCaja = 0;
            semVino.release(capCaja);
        }
        mutex.release();
    }

    public void vaciarAlmacen() throws InterruptedException{
        // Espero a que me avisen que hay que vaciar el almacen.
        sacarAlmacen.acquire();
        mutex.acquire();
        cantCajasAlmacen = 0;
        mutex.release();
        semAlmacen.release(capAlmacen);
    }

}
