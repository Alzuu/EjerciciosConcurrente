package EjerciciosAdicionales.ParqueVecinal;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Parque {
    // Capacidad.
    private final int capMax;
    private final int capReducida;
    // Esperando.
    private int cantVecinosEsp = 0;

    private int cantEscEnParque = 0;
    private int enParque = 0;

    Lock lock = new ReentrantLock();
    Condition entrarVecino = lock.newCondition();
    Condition entrarOtro = lock.newCondition();

    public Parque(int capMax, int capReducida) {
        this.capMax = capMax;
        this.capReducida = capReducida;
    }

    public void vecinoEntra() throws InterruptedException {
        lock.lock();
        cantVecinosEsp++;
        try {
            while (enParque >= capMax || (cantEscEnParque > 0 && enParque >= capReducida)) {
                entrarVecino.await();
            }
            cantVecinosEsp--;
            enParque++;
        } finally {
            lock.unlock();
        }
    }

    public void personaEntra() throws InterruptedException {
        lock.lock();
        try {
            while (enParque >= capMax || (cantEscEnParque > 0 && enParque >= capReducida)) {
                entrarOtro.await();
            }
            enParque++;
        } finally {
            lock.unlock();
        }
    }

    public void personaSale() {
        lock.lock();
        try {
            enParque--;
            if (cantVecinosEsp > 0) {
                entrarVecino.signalAll();
            } else {
                entrarOtro.signalAll();
            }

        } finally {
            lock.unlock();
        }
    }

    public void escuelaEntra(int cantPersonas) throws InterruptedException {
        lock.lock();
        try {
            // Si la escuela y el parque superan la capacidad cuando hay escuela en parque.
            while ((enParque + cantPersonas) >= capReducida) {
                entrarOtro.await();
            }
            cantEscEnParque++;
            enParque += cantPersonas;
        } finally {
            lock.unlock();
        }
    }

    public void escuelaSale(int cantPersonas) throws InterruptedException {
        lock.lock();
        try {
            enParque -= cantPersonas;
            cantEscEnParque--;
            if (cantVecinosEsp > 0) {
                entrarVecino.signalAll();
            } else {
                entrarOtro.signalAll();
            }

        } finally {
            lock.unlock();
        }
    }

}
