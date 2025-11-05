package TPN5;

import java.util.concurrent.Semaphore;

public class ejercicio7_Babuinos {
    public static void main(String[] args) {
        Cuerda cuerda = new Cuerda();
        Thread[] babuinos = new Thread[13];

        for (int i = 0; i < babuinos.length; i++) {
            char lado = (i % 2 == 0) ? 'A' : 'B';
            babuinos[i] = new Thread(new Babuino(i, lado, cuerda));
        }
        for (int i = 0; i < babuinos.length; i++) {
            babuinos[i].start();
        }
    }

    public static class Cuerda {
        private final int capCuerda = 5;
        private int esperandoA = 0;
        private int esperandoB = 0;
        private int enCuerda = 0;

        private char turno = 'X';

        private Semaphore mutex = new Semaphore(1);
        private Semaphore cruzarA = new Semaphore(0);
        private Semaphore cruzarB = new Semaphore(0);

        public void entrarA() throws InterruptedException {
            mutex.acquire();
            esperandoA++;
            if (turno == 'X') {
                turno = 'A';
                // Es el primero en cruzar, quien tiene que liberar los permisos.
                int cantPermisos = Math.min(capCuerda, esperandoA);
                /*
                 * Libera el minimo entre la capacidad de la cuerda y la cantidad esperando para
                 * evitar liberar de más.
                 */
                cruzarA.release(cantPermisos);
            }
            mutex.release();

            cruzarA.acquire();
            // Entra en la cuerda.
            mutex.acquire();
            esperandoA--;
            enCuerda++;
            mutex.release();
        }

        public void salirA() throws InterruptedException {
            mutex.acquire();
            enCuerda--;
            if (enCuerda == 0) {
                // Si es el último en salir decide que hacer.
                if (esperandoB > 0) {
                    /*
                     * Si ya completaron su turno o no hay mas en B esperando.
                     * Cambia el turno y libera los permisos para que pasen.
                     */
                    turno = 'B';
                    int cantPermisos = Math.min(capCuerda, esperandoB);
                    cruzarB.release(cantPermisos);
                } else if (esperandoA > 0) {
                    /*
                     * Si no hay esperando en B, pero si hay esperando en A.
                     * Libera más permisos.
                     */
                    turno = 'A';
                    int cantPermisos = Math.min(capCuerda, esperandoA);
                    cruzarA.release(cantPermisos);
                } else {
                    // Si no hay nadie esperando en ningún lado.
                    turno = 'X';
                }
            }
            mutex.release();
        }

        public void entrarB() throws InterruptedException {
            mutex.acquire();
            esperandoB++;
            if (turno == 'X') {
                turno = 'B';
                int cantPermisos = Math.min(capCuerda, esperandoB);
                cruzarB.release(cantPermisos);
            }
            mutex.release();

            cruzarB.acquire();
            // Entra en la cuerda.
            mutex.acquire();
            esperandoB--;
            enCuerda++;
            mutex.release();
        }

        public void salirB() throws InterruptedException {
            mutex.acquire();
            enCuerda--;
            if (enCuerda == 0) {
                if (esperandoA > 0) {
                    turno = 'A';
                    int cantPermisos = Math.min(capCuerda, esperandoA);
                    cruzarA.release(cantPermisos);
                } else if (esperandoB > 0) {
                    turno = 'B';
                    int cantPermisos = Math.min(capCuerda, esperandoB);
                    cruzarB.release(cantPermisos);
                } else {
                    turno = 'X';
                }
            }
            mutex.release();
        }
    }

    public static class Babuino implements Runnable {
        private int id;
        private char lado; // Lado 'A' o lado 'B'
        private Cuerda cuerda;

        public Babuino(int id, char lado, Cuerda cuerda) {
            this.id = id;
            this.lado = lado;
            this.cuerda = cuerda;
        }

        @Override
        public void run() {
            try {
                if (lado == 'A') {
                    // System.out.println("Babuino " + id + "(A): ESPERA la CUERDA.");
                    cuerda.entrarA();
                    System.out.println("Babuino " + id + "(A): CRUZA la CUERDA.");
                    Thread.sleep(3000);
                    System.out.println("Babuino " + id + "(A): SALIÓ de la CUERDA.");
                    cuerda.salirA();
                    
                } else {
                    // System.out.println("Babuino " + id + "(B): ESPERA la CUERDA.");
                    cuerda.entrarB();
                    System.out.println("Babuino " + id + "(B): CRUZA la CUERDA.");
                    Thread.sleep(3000);
                    System.out.println("Babuino " + id + "(B): SALIÓ de la CUERDA.");
                    cuerda.salirB();
                    
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Babuino " + id + ": INTERRUMPIDO.");
            }

        }
    }
}
