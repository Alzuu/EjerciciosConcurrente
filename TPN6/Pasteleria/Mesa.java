package TPN6.Pasteleria;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Mesa {
    private LinkedList<Caja> almacenCaja = new LinkedList<>();
    private Caja cajaActual = new Caja();
    private boolean retirarCaja = false;

    private Lock lock = new ReentrantLock();
    private Condition hayCaja = lock.newCondition();
    private Condition sacarCaja = lock.newCondition();

    public void ponerPastel(Pastel pastel) throws InterruptedException {
        lock.lock();
        try {
            while ((pastel.getPeso() + cajaActual.getPesoActual()) > cajaActual.getPesoMax()) {
                if ((cajaActual.getPesoActual()) >= cajaActual.getPesoMax()) {
                    System.out.println("ULTIMO PASTEL QUE ENTRA EN CAJA");
                    retirarCaja = true;
                    sacarCaja.signal();
                }
                hayCaja.await();
            }

            cajaActual.agregarPastel(pastel);

            if ((cajaActual.getPesoActual()) == cajaActual.getPesoMax()) {
                System.out.println("ULTIMO PASTEL QUE ENTRA EN CAJA");
                retirarCaja = true;
                sacarCaja.signal();
            }
        } finally {
            lock.unlock();
        }

    }

    public void reponerCaja() throws InterruptedException {
        lock.lock();
        try {
            while (!retirarCaja) {
                sacarCaja.await();
            }

            almacenCaja.add(cajaActual);
            cajaActual = new Caja();
            retirarCaja = false;
            hayCaja.signalAll();
        } finally {
            lock.unlock();
        }

    }
}
