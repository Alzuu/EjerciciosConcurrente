package TPN5;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ejercicio5_TorreDeControl {
    public static void main(String[] args) {
        Aeropuerto aeropuerto = new Aeropuerto();
        Thread torreDeControl = new Thread(new TorreDeControl(aeropuerto));
        Thread[] aviones = new Thread[20];
        torreDeControl.start();

        for (int i = 0; i < aviones.length; i++) {
            char estado = (i % 2 == 0) ? 'V' : 'E';
            Avion avion = new Avion(" ", i, estado, aeropuerto);
            aviones[i] = new Thread(avion);
            aviones[i].start();
            if (estado == 'E') {
                // Para coherencia con 'despegar' que hace remove()
                try {
                    aeropuerto.mutex.acquire();
                    aeropuerto.avionesEstacionados.add(avion);
                    aeropuerto.mutex.release();
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
    }

    public static class Aeropuerto {
        /*
         * cantAvionesAterrizados es una Variable que va de 0 a 10 para saber cuando
         * tiene prioridad de despegar un avion.
         */
        private int cantAvionesAterrizados = 0;

        private LinkedList<Avion> avionesEstacionados = new LinkedList<>();

        private Semaphore mutex = new Semaphore(1);

        // Cuenta todos los pedidos de maniobras.
        private Semaphore pedidos = new Semaphore(0);
        // Uso para saber cuantos aviones estan pidiendo aterrizar/despegar.
        private Semaphore avionesAterrizando = new Semaphore(0);
        private Semaphore avionesDespegando = new Semaphore(0);

        private Semaphore permisoAterrizar = new Semaphore(0);
        private Semaphore permisoDespegar = new Semaphore(0);
        private Semaphore finManiobra = new Semaphore(0);

        public void comportamientoAvion(Avion avion) throws InterruptedException {
            char estado = avion.getEstado();
            if (estado == 'V') {
                aterrizar(avion);
            } else {
                despegar(avion);
            }
        }

        private void aterrizar(Avion avion) throws InterruptedException {
            System.out.println("Avion " + avion.getId() + ": Pide permiso para ATERRIZAR.");
            // Avisa que quiere aterrizar.
            avionesAterrizando.release();
            pedidos.release();
            // Espera a que le den permiso.
            permisoAterrizar.acquire();

            System.out.println("Avion " + avion.getId() + ": Se le otorga permiso para ATERRIZAR.");
            System.out.println("Avion " + avion.getId() + ": ATERRIZANDO.");
            // Aterriza.
            Thread.sleep(avion.getTiempoAterrizaje());
            // Estaciona.
            mutex.acquire();
            System.out.println("Avion " + avion.getId() + ": ESTACIONANDO.");
            avionesEstacionados.add(avion);
            System.out.println("Cantidad de aviones Estacionados: " + avionesEstacionados.size());
            mutex.release();

            finManiobra.release(); // Avisa que terminó.
        }

        private void despegar(Avion avion) throws InterruptedException {
            System.out.println("Avion " + avion.getId() + ": Pide permiso para DESPEGAR.");
            // Avisa que quiere despegar.
            avionesDespegando.release();
            pedidos.release();
            // Espera a que le den permiso.
            permisoDespegar.acquire();

            System.out.println("Avion " + avion.getId() + ": Se le otorga permiso para DESPEGAR.");
            System.out.println("Avion " + avion.getId() + ": DESPEGANDO.");
            // Despega.
            Thread.sleep(avion.getTiempoDespegue());
            // Se va.
            mutex.acquire();
            System.out.println("Avion " + avion.getId() + ": VOLANDO.");
            avionesEstacionados.remove(avion);
            System.out.println("Cantidad de aviones Estacionados: " + avionesEstacionados.size());
            mutex.release();

            finManiobra.release(); // Avisa que terminó.
        }

        public void ControlTorre() throws InterruptedException {
            boolean forzarDespegue = false;
            while (true) {
                // Espera hasta que haya algún pedido.
                pedidos.acquire();
                boolean autorizarAterrizaje = false;
                boolean autorizarDespegue = false;

                if (forzarDespegue && avionesDespegando.availablePermits() > 0) {
                    // Caso 1: Política de prioridad 10 aterrizajes, 1 despegue.
                    avionesDespegando.acquire();
                    autorizarDespegue = true;
                } else {
                    if (avionesAterrizando.availablePermits() > 0) {
                        // Caso 2: Un avión quiere aterrizar.
                        avionesAterrizando.acquire();
                        autorizarAterrizaje = true;
                    } else if (avionesDespegando.availablePermits() > 0) {
                        // Caso 3: No hay aterrizajes pedidos pero si despegues.
                        avionesDespegando.acquire();
                        autorizarDespegue = true;
                    }
                }

                if (autorizarAterrizaje) {
                    permisoAterrizar.release();
                    finManiobra.acquire(); // Espera a que termine la acción.

                    mutex.acquire();
                    cantAvionesAterrizados++;
                    if (cantAvionesAterrizados == 10) {
                        forzarDespegue = true;
                        cantAvionesAterrizados = 0;
                    }
                    mutex.release();
                } else if (autorizarDespegue) {
                    permisoDespegar.release();
                    finManiobra.acquire();
                    mutex.acquire();
                    if (forzarDespegue) {
                        forzarDespegue = false;
                    }
                    mutex.release();
                }

            }
        }
    }

    public static class TorreDeControl implements Runnable {
        Aeropuerto aeropuerto;

        public TorreDeControl(Aeropuerto aeropuerto) {
            this.aeropuerto = aeropuerto;
        }

        @Override
        public void run() {
            try {
                aeropuerto.ControlTorre();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static class Avion implements Runnable {
        String tipo;
        int id;
        char estado; // V para VOLANDO, E para ESTACIONADO.
        Aeropuerto aeropuerto;

        public Avion(String tipo, int id, char estado, Aeropuerto aeropuerto) {
            this.tipo = tipo;
            this.id = id;
            this.estado = estado;
            this.aeropuerto = aeropuerto;
        }

        public int getId() {
            return this.id;
        }

        public char getEstado() {
            return this.estado;
        }

        public void cambiarEstado() {
            estado = (estado == 'V') ? 'E' : 'V';
        }

        public int getTiempoAterrizaje() {
            Random random = new Random();
            int tiempoAterrizaje = (random.nextInt(5) + 1) * 1000;
            return tiempoAterrizaje;
        }

        public int getTiempoDespegue() {
            Random random = new Random();
            int tiempoDespegue = (random.nextInt(5) + 1) * 1000;
            return tiempoDespegue;
        }

        @Override
        public void run() {
            try {
                aeropuerto.comportamientoAvion(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
