package EjerciciosAdicionales.BobDylan;

import java.util.concurrent.Semaphore;

public class MesaDiscos {
    private final int duracionR = 1000;
    private final int duracionF = 1500;
    private final int duracionB = 2000;
    private final int duracionC = 2500;

    private Semaphore hayR = new Semaphore(0);
    private Semaphore hayF = new Semaphore(0);
    private Semaphore hayB = new Semaphore(0);
    private Semaphore hayC = new Semaphore(0);

    public int ponerCancion(char tipoCancion) {
        // Pone una cancion en la pila (semaforo) que corresponda.
        int duracion = 0;
        switch (tipoCancion) {
            case 'R':
                duracion = duracionR;
                hayR.release();
                break;

            case 'F':
                duracion = duracionF;
                hayF.release();
                break;

            case 'B':
                duracion = duracionB;
                hayB.release();
                break;

            case 'C':
                duracion = duracionC;
                hayC.release();
                break;

            default:
                break;
        }
        System.out.println("BOB PUSO una de tipo: " + tipoCancion);
        return duracion;
    }

    public int sacarCancion(int id, char tipoProductor) throws InterruptedException {
        // Saca una cancion de la pila que le corresponda. (Productor).
        int duracion = 0;
        switch (tipoProductor) {
            case 'R':
                hayR.acquire();
                duracion = duracionR;
                break;

            case 'F':
                hayF.acquire();
                duracion = duracionF;
                break;

            case 'B':
                hayB.acquire();
                duracion = duracionB;
                break;

            case 'C':
                hayC.acquire();
                duracion = duracionC;
                break;

            default:
                break;
        }
        System.out.println("PRODUCTOR: " + id + " SACO una de tipo: " + tipoProductor);
        return duracion;
    }
}
