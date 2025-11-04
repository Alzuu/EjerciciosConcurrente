package TPN6;

public class ejercicio2_Ferry {
    public static void main(String[] args) {
        Ferry ferry = new Ferry(5);
        Thread control = new Thread(new ControlFerry(ferry));

        Thread[] pasajeros = new Thread[20];

        for (int i = 0; i < pasajeros.length; i++) {
            if (i % 2 == 0) {
                pasajeros[i] = new Thread(new Pasajero(i, ferry));
            } else {
                pasajeros[i] = new Thread(new Auto(i, ferry));
            }
            pasajeros[i].start();
        }

        control.start();
    }

    public static class Ferry {
        private final int capMax;
        private int capActual;
        private int aBordo;
        private String fase; // EMBARCANDO, NAVEGANDO, DESEMBARCANDO.

        public Ferry(int capMax) {
            this.capMax = capMax;
            this.capActual = 0;
            this.fase = "EMBARCANDO";
            this.aBordo = 0;
        }

        public synchronized void abordar(int id, char tipo) throws InterruptedException {
            // Pongo mayor-igual porque podría haber sobrecapacidad.
            while (!fase.equals("EMBARCANDO") || capActual + peso(tipo) > capMax) {
                // Si no entra o no esta embarcando espera.
                wait();
            }
            if (tipo == 'A') {
                System.out.println("Auto " + id + ": SUBE al ferry. Cap. Actual: " + capActual);
                // Si es auto ocupa más espacio.
                capActual += 3;
            } else {
                System.out.println("Pasajero " + id + ": SUBE al ferry. Cap. Actual: " + capActual);
                capActual++;
            }
            aBordo++;
            notifyAll();
        }

        private int peso(char tipo) {
            return (tipo == 'A') ? 3 : 1;
        }

        public synchronized void bajar(int id, char tipo) throws InterruptedException {
            while (!fase.equals("DESEMBARCANDO")) {
                // Si no llegó a destino espera para bajar.
                wait();
            }
            if (tipo == 'A') {
                System.out.println("Auto " + id + ": BAJA del ferry. Cap. Actual: " + capActual);
                capActual -= 3;
            } else {
                System.out.println("Pasajero " + id + ": BAJA del ferry. Cap. Actual: " + capActual);
                capActual--;
            }
            aBordo--;

            notifyAll();
        }

        public synchronized void controlarEmbarque(int tMaxEspera) throws InterruptedException {
            System.out.println("Ferry: " + fase);
            this.fase = "EMBARCANDO";
            notifyAll();
            while (capActual < capMax) {
                /*
                 * Acá podría agregar que no hayan entidades esperando, porque si no el ferry no
                 * sale hasta que se haya llenado.
                 */
                wait();
            }
            this.fase = "NAVEGANDO";
            notifyAll();
        }

        public synchronized void controlarDesembarque() throws InterruptedException {
            System.out.println("Ferry: " + fase);
            this.fase = "DESEMBARCANDO";
            notifyAll();
            while (aBordo > 0) {
                wait();
            }
            this.fase = "NAVEGANDO";
            notifyAll();
        }
    }

    public static class ControlFerry implements Runnable {
        private Ferry ferry;

        public ControlFerry(Ferry ferry) {
            this.ferry = ferry;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ferry.controlarEmbarque(5000);
                    System.out.println(".......... NAVEGANDO ..........");
                    Thread.sleep(5000);
                    ferry.controlarDesembarque();
                    System.out.println(".......... VOLVIENDO ..........");
                    Thread.sleep(5000);
                }

            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Control del Ferry interrumpido.");
            }

        }
    }

    public static class Auto implements Runnable {
        private int id;
        private Ferry ferry;
        private final char tipo;

        public Auto(int id, Ferry ferry) {
            this.id = id;
            this.ferry = ferry;
            this.tipo = 'A';
        }

        @Override
        public void run() {
            try {
                System.out.println("Auto " + id + ": ESPERA al ferry.");
                ferry.abordar(id, tipo);
                ferry.bajar(id, tipo);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Auto " + id + ": interrumpido.");
            }

        }
    }

    public static class Pasajero implements Runnable {
        private int id;
        private Ferry ferry;
        private final char tipo;

        public Pasajero(int id, Ferry ferry) {
            this.id = id;
            this.ferry = ferry;
            this.tipo = 'P';
        }

        @Override
        public void run() {
            try {
                System.out.println("Pasajero " + id + ": ESPERA al ferry.");
                ferry.abordar(id, tipo);
                ferry.bajar(id, tipo);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Pasajero " + id + ": interrumpido.");
            }
        }
    }
}
