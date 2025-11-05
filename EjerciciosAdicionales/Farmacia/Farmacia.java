package EjerciciosAdicionales.Farmacia;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Farmacia {
    private String nombre;

    private LinkedList<String> canastoFichasGeneral;
    private LinkedList<String> canastoFichasContables;
    private LinkedList<String> canastoFichasEncargado;

    private Lock lockGen;
    private Condition hayParaEncargado;
    private Condition hayParaContable;

    public Farmacia(String nombre) {
        this.nombre = nombre;

        this.canastoFichasGeneral = new LinkedList<>();
        this.canastoFichasContables = new LinkedList<>();
        this.canastoFichasEncargado = new LinkedList<>();

        this.lockGen = new ReentrantLock(); // Pueden acceder los 3.
        this.hayParaEncargado = lockGen.newCondition();
        this.hayParaContable = lockGen.newCondition();
    }

    public void producirFichaGeneral(int id, String ficha) throws InterruptedException {
        // Produce todo el tiempo y no es necesario que consuma nada.
        lockGen.lock();
        try {
            System.out.println("Aux-Farmacia " + id + ": PRODUCE ficha GENERAL.");
            canastoFichasGeneral.addLast(ficha);
            hayParaContable.signal();
            hayParaEncargado.signal();
        } finally {
            lockGen.unlock();
        }
    }

    public void consumirContable(int id, String ficha) throws InterruptedException {
        lockGen.lock();
        try {
            while (canastoFichasGeneral.isEmpty() && canastoFichasContables.isEmpty()) {
                hayParaContable.await();
            }
            if (!canastoFichasContables.isEmpty()) {
                // Tira la Ficha Contable
                System.out.println("Contable " + id + ": TIRA ficha CONTABLE.");
                canastoFichasContables.removeFirst();
            } else {
                // Produce la Ficha Encargado
                System.out.println("Contable " + id + ": PRODUCE ficha ENCARGADO.");
                producirFichaEncargado(ficha);
            }
        } finally {
            lockGen.unlock();
        }
    }

    public void consumirEncargado(int id, String ficha) throws InterruptedException {
        lockGen.lock();
        try {
            while (canastoFichasGeneral.isEmpty() && canastoFichasEncargado.isEmpty()) {
                hayParaEncargado.await();
            }
            if (!canastoFichasEncargado.isEmpty()) {
                // Tira la ficha encargado.
                System.out.println("Encargado " + id + ": TIRA ficha ENCARGADO.");
                canastoFichasEncargado.removeFirst();
            } else {
                // Produce una ficha contable.
                System.out.println("Encargado " + id + ": PRODUCE ficha CONTABLE.");
                producirFichaContable(ficha);
            }
        } finally {
            lockGen.unlock();
        }
    }

    private void producirFichaEncargado(String ficha) throws InterruptedException {
        lockGen.lock();
        try {
            canastoFichasGeneral.removeFirst();
            canastoFichasEncargado.addLast(ficha);
            hayParaEncargado.signal();
        } finally {
            lockGen.unlock();
        }
    }

    private void producirFichaContable(String ficha) throws InterruptedException {
        // Consume una ficha General y produce una ficha contable.
        lockGen.lock();
        try {
            canastoFichasGeneral.removeFirst();
            canastoFichasContables.addLast(ficha);
            hayParaContable.signal();
        } finally {
            lockGen.unlock();
        }
    }

}
