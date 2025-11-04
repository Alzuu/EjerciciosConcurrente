package TPN6;

public class ejercicio4_PuenteEstrecho {
    public static void main(String[] args) {
        Puente puente = new Puente();
        Thread[] autos = new Thread[6];

        for (int i = 0; i < autos.length; i++) {
            char lado = (i % 2 == 0) ? 'N' : 'S';
            autos[i] = new Thread(new Auto(i, lado, puente));
            autos[i].start();
        }

    }

    public static class Puente {
        private int cantNorte;
        private int cantSur;
        private boolean turnoNorte;

        public Puente() {
            this.cantNorte = 0;
            this.cantSur = 0;
            this.turnoNorte = false; // Para evitar inanición.
        }

        public synchronized void entrarNorte() throws InterruptedException {
            while ((cantSur > 0 && !turnoNorte)) {
                wait();
            }
            cantNorte++;
        }

        public synchronized void entrarSur() throws InterruptedException {
            while (cantNorte > 0 && turnoNorte) {
                wait();
            }
            cantSur++;
        }

        public synchronized void salirNorte() {
            cantNorte--;
            turnoNorte = cantSur > 0;
            notifyAll();
        }

        public synchronized void salirSur() {
            cantSur--;
            turnoNorte = cantNorte > 0;
            notifyAll();
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
                    Thread.sleep(5000);
                    System.out.println("Auto " + id + ": SALIÓ desde el NORTE.");
                    puente.salirNorte();
                } else {
                    System.out.println("Auto " + id + ": ESPERA para entrar desde el SUR.");
                    puente.entrarSur();
                    System.out.println("Auto " + id + ": ENTRÓ desde el SUR.");
                    Thread.sleep(5000);
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
