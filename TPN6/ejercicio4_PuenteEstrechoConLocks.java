package practicos.TPN6;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ejercicio4_PuenteEstrechoConLocks {
    public static void main(String[] args) {
        Puente puente = new Puente();
        Thread[] autos = new Thread[15];

        for (int i = 0; i < autos.length; i++) {
            char lado = (i % 2 == 0) ? 'N' : 'S';
            autos[i] = new Thread(new Auto(i, lado, puente));
            autos[i].start();
        }
    }

    public static class Puente {
        private int esperandoN = 0;
        private int esperandoS = 0;
        private int cantCruzoN = 0; // Total que cruzaron.
        private int cantCruzoS = 0;
        private int enPuente = 0;
        private final int limiteDeCruces = 2;

        private char turno = '-';

        private Lock accesoPuente = new ReentrantLock(true);
        private Condition pasaNorte = accesoPuente.newCondition();
        private Condition pasaSur = accesoPuente.newCondition();

        public void entrarNorte() throws InterruptedException {
            accesoPuente.lock();
            try {
                esperandoN++;
                while (turno == 'S' || (turno == 'N' && cantCruzoN >= limiteDeCruces && esperandoS > 0)) {
                    /*
                     * Si es el turno del SUR.
                     * O era su turno excedieron el limite y hay esperando en el SUR.
                     * ESPERA.
                     */
                    pasaNorte.await();
                }
                if (turno == '-') {
                    turno = 'N';
                    cantCruzoN = 0;
                }
                esperandoN--;
                enPuente++;
                cantCruzoN++;
            } finally {
                accesoPuente.unlock();
            }
        }

        public void entrarSur() throws InterruptedException {
            accesoPuente.lock();
            try {
                esperandoS++;
                while (turno == 'N' || (turno == 'S' && cantCruzoS >= limiteDeCruces && esperandoN > 0)) {
                    /*
                     * Si es el turno del NORTE.
                     * O era su turno y excedieron el limite y hay esperando en el NORTE.
                     * ESPERA.
                     */
                    pasaSur.await();
                }
                if (turno == '-') {
                    turno = 'S';
                    cantCruzoS = 0;
                }
                esperandoS--;
                enPuente++;
                cantCruzoS++;
            } finally {
                accesoPuente.unlock();
            }
        }

        public void salirNorte() {
            accesoPuente.lock();
            try {
                enPuente--;
                if (enPuente == 0) { // Solo decido si no hay nadie en el puente.
                    if (esperandoS > 0 && (cantCruzoN >= limiteDeCruces || esperandoN == 0)) {
                        // Si se cumplio el limite o no hay en el NORTE y hay en el SUR esperando.
                        turno = 'S';
                        cantCruzoN = 0;
                        pasaSur.signalAll();
                    } else if (esperandoN > 0) {
                        // Si alguno quedo esperando por el limite.
                        turno = 'N';
                        pasaNorte.signalAll();
                    } else {
                        // No hay nadie de ningun lado.
                        turno = '-';
                    }
                }
            } finally {
                accesoPuente.unlock();
            }
        }

        public void salirSur() {
            accesoPuente.lock();
            try {
                enPuente--;
                if (enPuente == 0) {
                    if (esperandoN > 0 && (cantCruzoS >= limiteDeCruces || esperandoS == 0)) {
                        turno = 'N';
                        cantCruzoS = 0;
                        pasaNorte.signalAll();
                    } else if (esperandoS > 0) {
                        turno = 'S';
                        pasaSur.signalAll();
                    } else {
                        turno = '-';
                    }
                }
            } finally {
                accesoPuente.unlock();
            }
        }
    }

    public static class Auto implements Runnable {
        int id;
        char lado;
        Puente puente;

        public Auto(int id, char lado, Puente puente) {
            this.id = id;
            this.lado = lado;
            this.puente = puente;
        }

        @Override
        public void run() {
            try {
                if (lado == 'N') {
                    System.out.println("Auto " + id + ": ESPERA para entrar desde el NORTE.");
                    puente.entrarNorte();
                    System.out.println("Auto " + id + ": ENTRÓ desde el NORTE.");
                    Thread.sleep(3000);
                    System.out.println("Auto " + id + ": SALIÓ desde el NORTE.");
                    puente.salirNorte();
                } else {
                    System.out.println("Auto " + id + ": ESPERA para entrar desde el SUR.");
                    puente.entrarSur();
                    System.out.println("Auto " + id + ": ENTRÓ desde el SUR.");
                    Thread.sleep(3000);
                    System.out.println("Auto " + id + ": SALIÓ desde el SUR.");
                    puente.salirSur();
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Auto " + id + ": interrumpido.");
            }

        }
    }
}
