package TPN6.Pasteleria;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Mostrador {
    private LinkedList<Pastel> mostrador = new LinkedList<>();

    Lock lock = new ReentrantLock();
    Condition hayPastel = lock.newCondition();

    public void producirPastelA() {
        lock.lock();
        try {
            Pastel pastel = new Pastel('A');
            mostrador.addLast(pastel);
            hayPastel.signal();
        } finally {
            lock.unlock();
        }
    }

    public void producirPastelB() {
        lock.lock();
        try {
            Pastel pastel = new Pastel('B');
            mostrador.addLast(pastel);
            hayPastel.signal();
        } finally {
            lock.unlock();
        }

    }

    public void producirPastelC() {
        lock.lock();
        try {
            Pastel pastel = new Pastel('C');
            mostrador.addLast(pastel);
            hayPastel.signal();
        } finally {
            lock.unlock();
        }

    }

    public Pastel tomarPastel() throws InterruptedException {
        lock.lock();
        try {
            while (mostrador.isEmpty()) {
                hayPastel.await();
            }
            Pastel pastel = mostrador.removeFirst();
            return pastel;
        } finally {
            lock.unlock();
        }

    }
}
