package practicos.TPN6;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ejercicio3_ObservatorioConLocks {
    public static void main(String[] args) {
        Observatorio observatorio = new Observatorio();
        Thread[] pMantenimiento = { new Thread(new PersonalMantenimiento(0, observatorio)),
                new Thread(new PersonalMantenimiento(1, observatorio)),
                new Thread(new PersonalMantenimiento(2, observatorio)) };
        Thread[] investigadores = { new Thread(new Investigador(0, observatorio)),
                new Thread(new Investigador(1, observatorio)) };
        Thread[] turistas = new Thread[51];
        pMantenimiento[0].start();
        pMantenimiento[1].start();
        pMantenimiento[2].start();
        investigadores[0].start();
        investigadores[1].start();
        for (int i = 0; i < turistas.length; i++) {
            turistas[i] = new Thread(new Turista(i, (i % 50 == 0), observatorio));
            turistas[i].start();
        }

    }

    public static class Observatorio {
        private final int capSala;
        private final int capReducidaSala;
        private int cantTuristas;
        private int cantConSilla;
        private int cantConSillaEsperando;
        private int cantMantenimiento;
        private int cantInvestigadores;

        private Lock accesoObs;
        private Condition turista;
        private Condition conSilla;
        private Condition mantenimiento;
        private Condition investigador;

        public Observatorio() {
            this.capSala = 50;
            this.capReducidaSala = 30;
            this.cantTuristas = 0;
            this.cantConSilla = 0;
            this.cantConSillaEsperando = 0;
            this.cantMantenimiento = 0;
            this.cantInvestigadores = 0;

            this.accesoObs = new ReentrantLock();
            this.turista = accesoObs.newCondition();
            this.conSilla = accesoObs.newCondition();
            this.mantenimiento = accesoObs.newCondition();
            this.investigador = accesoObs.newCondition();
        }

        public void entrarConSilla() throws InterruptedException {
            accesoObs.lock();
            try {
                cantConSillaEsperando++;
                while (cantTuristas >= capReducidaSala || cantMantenimiento > 0 || cantInvestigadores > 0) {
                    conSilla.await();
                }
                cantConSillaEsperando--;
                cantConSilla++;
                cantTuristas++;
            } finally {
                accesoObs.unlock();
            }
        }

        public void entrarTurista() throws InterruptedException {
            accesoObs.lock();
            try {
                int limite = (cantConSilla > 0) ? capReducidaSala : capSala;
                while (cantTuristas == limite || cantMantenimiento > 0
                        || cantInvestigadores > 0) {
                    /*
                     * Si la sala esta llena.
                     * Hay un Turista en silla de ruedas esperando.
                     * Hay Personal de mantenimiento en la sala.
                     * Hay un Investigador en la sala.
                     * Espera.
                     */
                    turista.await();
                }
                cantTuristas++;
            } finally {
                accesoObs.unlock();
            }
        }

        public void entrarMantenimiento() throws InterruptedException {
            accesoObs.lock();
            try {
                while (cantTuristas > 0 || cantInvestigadores > 0 || cantMantenimiento == capSala) {
                    mantenimiento.await();
                }
                cantMantenimiento++;
            } finally {
                accesoObs.unlock();
            }
        }

        public void entrarInvestigador() throws InterruptedException {
            accesoObs.lock();
            try {
                while (cantTuristas > 0 || cantMantenimiento > 0 || cantInvestigadores > 0) {
                    investigador.await();
                }
                cantInvestigadores++;
            } finally {
                accesoObs.unlock();
            }
        }

        private void liberarPermisos() {
            /*
             * Preguntar si es necesario tener tantas condiciones para cada clase o si
             * teniendo una vale.
             */
            if (cantConSillaEsperando > 0) {
                conSilla.signalAll();
            }
            turista.signalAll();
            mantenimiento.signalAll();
            investigador.signal();
        }

        public void salirConSilla() {
            accesoObs.lock();
            try {
                cantConSilla--;
                cantTuristas--;
                liberarPermisos();
            } finally {
                accesoObs.unlock();
            }
        }

        public void salirTurista() {
            accesoObs.lock();
            try {
                cantTuristas--;
                liberarPermisos();
            } finally {
                accesoObs.unlock();
            }
        }

        public void salirMantenimiento() {
            accesoObs.lock();
            try {
                cantMantenimiento--;
                liberarPermisos();
            } finally {
                accesoObs.unlock();
            }
        }

        public void salirInvestigador() {
            accesoObs.lock();
            try {
                cantInvestigadores--;
                liberarPermisos();
            } finally {
                accesoObs.unlock();
            }
        }

    }

    public static class Turista implements Runnable {
        private int id;
        private boolean tieneSilla;
        private Observatorio observatorio;

        public Turista(int id, boolean tieneSilla, Observatorio observatorio) {
            this.id = id;
            this.tieneSilla = tieneSilla;
            this.observatorio = observatorio;
        }

        @Override
        public void run() {
            try {
                if (tieneSilla) {
                    System.out.println("Turista SILLA " + id + ": ESPERA para entrar en la sala.");
                    observatorio.entrarConSilla();
                    System.out.println("Turista SILLA " + id + ": ENTRÓ en la sala.");
                    Thread.sleep(2500);
                    System.out.println("Turista SILLA " + id + ": SALIÓ de la sala.");
                    observatorio.salirConSilla();
                } else {
                    System.out.println("Turista " + id + ": ESPERA para entrar en la sala.");
                    observatorio.entrarTurista();
                    System.out.println("Turista " + id + ": ENTRÓ en la sala.");
                    Thread.sleep(2000);
                    System.out.println("Turista " + id + ": SALIÓ de la sala.");
                    observatorio.salirTurista();
                }

            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Turista " + id + ": interrumpido.");
            }
        }
    }

    public static class PersonalMantenimiento implements Runnable {
        int id;
        Observatorio observatorio;

        public PersonalMantenimiento(int id, Observatorio observatorio) {
            this.id = id;
            this.observatorio = observatorio;
        }

        @Override
        public void run() {
            try {
                System.out.println("Mantenimiento " + id + ": ESPERA para entrar en la sala.");
                observatorio.entrarMantenimiento();
                System.out.println("Mantenimiento " + id + ": ENTRÓ en la sala.");
                Thread.sleep(3000);
                System.out.println("Mantenimiento " + id + ": SALIÓ de la sala.");
                observatorio.salirMantenimiento();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Personal de Mantenimiento " + id + ": interrumpido.");
            }
        }
    }

    public static class Investigador implements Runnable {
        int id;
        Observatorio observatorio;

        public Investigador(int id, Observatorio observatorio) {
            this.id = id;
            this.observatorio = observatorio;
        }

        @Override
        public void run() {
            try {
                System.out.println("Investigador " + id + ": ESPERA para entrar en la sala.");
                observatorio.entrarInvestigador();
                System.out.println("Investigador " + id + ": ENTRÓ en la sala.");
                Thread.sleep(3000);
                System.out.println("Investigador " + id + ": SALIÓ de la sala.");
                observatorio.salirInvestigador();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Investigador " + id + ": interrumpido.");
            }
        }
    }
}
