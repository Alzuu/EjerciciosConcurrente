package TPN6;

public class ejercicio3_Observatorio {
    public static void main(String[] args) {
        Observatorio observatorio = new Observatorio();
        Thread[] pMantenimiento = { new Thread(new PersonalMantenimiento(0, observatorio)),
                new Thread(new PersonalMantenimiento(1, observatorio)),
                new Thread(new PersonalMantenimiento(2, observatorio)) };
        Thread[] investigadores = { new Thread(new Investigador(0, observatorio)),
                new Thread(new Investigador(1, observatorio)) };
        Thread[] turistas = new Thread[150];

        for (int i = 0; i < turistas.length; i++) {
            turistas[i] = new Thread(new Turista(i, false, observatorio));
            turistas[i].start();
        }
        pMantenimiento[0].start();
        pMantenimiento[1].start();
        pMantenimiento[2].start();
        investigadores[0].start();
        investigadores[1].start();
    }

    public static class Observatorio {
        private final int capacidad = 50;
        private final int capHayEnSilla = 30;
        private int capActual;
        private boolean hayTuristas;
        private int cantTuristasConSilla;
        private boolean hayMantenimiento;
        private boolean hayInvestigadores;

        public Observatorio() {
            this.capActual = 0;
            this.hayTuristas = false;
            this.cantTuristasConSilla = 0;
            this.hayMantenimiento = false;
            this.hayInvestigadores = false;

        }

        public synchronized void entrarTurista(int id, boolean usaSilla) throws InterruptedException {
            while (capActual == capacidad || hayMantenimiento || hayInvestigadores
                    || (cantTuristasConSilla > 0 && capActual == capHayEnSilla)) {
                System.out.println("Turista " + id + ": ESPERA para entrar.");
                wait();
            }
            capActual++;
            if (capActual == 1) {
                System.out.println("Turista " + id + ": Es el PRIMERO en entrar.");
                // Si es el primer turista en entrar aviso.
                hayTuristas = true;
            }
            System.out.println("Turista " + id + ": ENTRÓ al observatorio.");
            if (usaSilla) {
                System.out.println("Turista " + id + ": usa SILLA DE RUEDAS.");
                cantTuristasConSilla++;
            }   
        }

        public synchronized void limpiar(int id) throws InterruptedException {
            while (capActual == capacidad || hayTuristas || hayInvestigadores) {
                System.out.println("Mantenimiento " + id + ": ESPERA para entrar.");
                wait();
            }
            capActual++;
            if (capActual == 1) {
                System.out.println("Mantenimiento " + id + ": Es el PRIMERO en entrar.");
                hayMantenimiento = true; // Si es el primero avisa.
            }
            System.out.println("Mantenimiento " + id + ": ENTRÓ al observatorio.");
        }

        public synchronized void investigar(int id) throws InterruptedException {
            while (hayTuristas || hayMantenimiento || hayInvestigadores) {
                // Como solo puede entrar 1 investigador debe cumplir estas condiciones.
                System.out.println("Investigador " + id + ": ESPERA para entrar.");
                wait();
            }
            System.out.println("Investigador " + id + ": ENTRÓ al observatorio.");
            hayInvestigadores = true;
            capActual = 1;
        }

        public synchronized void salirTurista(Turista turista) {
            int id = turista.getId();
            boolean usaSilla = turista.usaSilla();
            if (usaSilla) {
                cantTuristasConSilla--;
            }
            capActual--;
            if (capActual == 0) {
                // Si es el último turista en salir.
                System.out.println("Turista " + id + ": Es el ÚLTIMO en salir.");
                hayTuristas = false;
            }
            System.out.println("Turista " + id + ": SALE del observatorio.");
            notifyAll();
        }

        public synchronized void salirMantenimiento(int id) {
            capActual--;
            if (capActual == 0) {
                System.out.println("Mantenimiento " + id + ": Es el ÚLTIMO en salir.");
                hayMantenimiento = false;
            }
             System.out.println("Mantenimiento " + id + ": SALE del observatorio.");
            notifyAll();
        }

        public synchronized void salirInvestigador(int id) {
            capActual = 0;
            hayInvestigadores = false;
             System.out.println("Investigador " + id + ": SALE del observatorio.");
            notifyAll();
            
        }
    }

    public static class Turista implements Runnable {
        private int id;
        private boolean usaSilla;
        private Observatorio observatorio;

        public Turista(int id, boolean usaSilla, Observatorio observatorio) {
            this.id = id;
            this.usaSilla = usaSilla;
            this.observatorio = observatorio;
        }

        public int getId() {
            return this.id;
        }

        public boolean usaSilla() {
            return this.usaSilla;
        }

        @Override
        public void run() {
            try {
                observatorio.entrarTurista(id, usaSilla);
                Thread.sleep(2000);
                observatorio.salirTurista(this);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Turista " + id + ": interrumpido.");
            }
        }
    }

    public static class PersonalMantenimiento implements Runnable {
        private int id;
        private Observatorio observatorio;

        public PersonalMantenimiento(int id, Observatorio observatorio) {
            this.id = id;
            this.observatorio = observatorio;
        }

        @Override
        public void run() {
            try {
                observatorio.limpiar(id);
                Thread.sleep(5000);
                observatorio.salirMantenimiento(id);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Personal de Mantenimiento " + id + ": interrumpido.");
            }
        }
    }

    public static class Investigador implements Runnable {
        private int id;
        private Observatorio observatorio;

        public Investigador(int id, Observatorio observatorio) {
            this.id = id;
            this.observatorio = observatorio;
        }

        @Override
        public void run() {
            try {
                observatorio.investigar(id);
                Thread.sleep(5000);
                observatorio.salirInvestigador(id);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Investigador " + id + ": interrumpido.");
            }
        }
    }
}
