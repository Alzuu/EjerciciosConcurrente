package TPN5;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ejercicio2_LosPollosHermanosII {
    public static void main(String[] args) {
        Confiteria confiteria = new Confiteria();
        Thread mozo = new Thread(new Mozo(1, confiteria));
        Thread cocinero = new Thread(new Cocinero(confiteria));
        Thread[] empleados = new Thread[10];
        mozo.start();
        cocinero.start();
        for (int i = 0; i < empleados.length; i++) {
            empleados[i] = new Thread(new Empleado(i, confiteria));
            empleados[i].start();
        }
    }

    public static class Confiteria {
        private final int cantAsientos = 2;

        private Semaphore mutex = new Semaphore(1);
        // Mutex existe para que un Mozo/Cocinero no entrege la orden a otro empleado.
        private Semaphore semAsientos = new Semaphore(cantAsientos);
        private Semaphore semMozo = new Semaphore(0);
        private Semaphore semCocinero = new Semaphore(0);
        private Semaphore semComida = new Semaphore(0);
        private Semaphore semBebida = new Semaphore(0);

        public void comportamientoEmpleado(int id) throws InterruptedException {
            semAsientos.acquire();// Busca si hay lugar.
            // Si hay se queda y le pide bebida (al mozo) o comida (al cocinero) o ambas.
            System.out.println("El Empleado " + id + " se sienta.");
            int opcion = generarOpcionPedido();
            switch (opcion) {
                case 1:
                    pedirSoloBebida(id, bebidaRandom());
                    break;
                case 2:
                    pedirSoloComida(id, comidaRandom());
                    break;
                case 3:
                    // Si ambos, primero atiende el mozo y después el cocinero.
                    pedirComidaYBebida(id, bebidaRandom(), comidaRandom());
                    break;
                default:
                    break;
            }
            System.out.println("El Empleado " + id + " ya terminó de comer.");
            semAsientos.release();
        }

        private int generarOpcionPedido(){
            Random random = new Random();
            int opcion = random.nextInt(3) + 1;
            return opcion;
        }

        private void pedirSoloBebida(int id, String bebida) throws InterruptedException {
            semMozo.release();
            System.out.println("El Empleado " + id + " pidió " + bebida + ".");
            mutex.acquire();
            semBebida.acquire();
            mutex.release();
            System.out.println("El Empleado " + id + " toma su " + bebida + ".");
        }

        private void pedirSoloComida(int id, String comida) throws InterruptedException {
            semCocinero.release();
            System.out.println("El Empleado " + id + " pidió " + comida + ".");
            mutex.acquire();
            semComida.acquire();
            mutex.release();
            System.out.println("El Empleado " + id + " come su " + comida + ".");
        }

        private void pedirComidaYBebida(int id, String bebida, String comida) throws InterruptedException {
            semMozo.release();
            System.out.println("El Empleado " + id + " pidió " + bebida + " y " + comida + ".");
            mutex.acquire();
            semCocinero.release();
            semBebida.acquire();
            semComida.acquire();
            mutex.release();
            System.out.println("El empleado "+ id + " come su comida.");
            
        }

        private String bebidaRandom() {
            Random random = new Random();
            int indice = random.nextInt(5);
            String bebida;
            switch (indice) {
                case 1:
                    bebida = "CAFÉ";
                    break;
                case 2:
                    bebida = "SODA";
                    break;
                case 3:
                    bebida = "CERVEZA";
                    break;
                case 4:
                    bebida = "MATE";
                    break;

                default:
                    bebida = "AGUA";
                    break;
            }
            return bebida;
        }

        private String comidaRandom() {
            Random random = new Random();
            int indice = random.nextInt(5);
            String comida;
            switch (indice) {
                case 1:
                    comida = "HAMBURGUESA";
                    break;
                case 2:
                    comida = "POLLO FRITO";
                    break;
                case 3:
                    comida = "ASADO";
                    break;
                case 4:
                    comida = "EMPANADAS";
                    break;

                default:
                    comida = "FIDEOS";
                    break;
            }
            return comida;
        }

        public void comportamientoMozo(int id) throws InterruptedException {
            semMozo.acquire();// Espera a un empleado.
            // Atiende si le pide bebida.
            System.out.println("El mozo " + id + " está sirviendo la bebida");
            int tiempo = generarTiempo("SERVIR");
            Thread.sleep(tiempo);
            semBebida.release();
        }

        public void comportamientoCocinero(String nombre) throws InterruptedException {
            semCocinero.acquire();// Espera un empleado.
            // Atiende si le pide comida.
            System.out.println("El cocinero " + nombre + " empieza a cocinar.");
            int tiempo = generarTiempo("COCINAR");
            Thread.sleep(tiempo);
            semComida.release();
        }

        private int generarTiempo(String tipo) {
            Random random = new Random();
            int tiempo;
            if (tipo.equals("COCINAR")) {
                tiempo = (random.nextInt(10) + 5) * 1000; // Entre 5 y 10 seg.
            } else {
                tiempo = (random.nextInt(5) + 1) * 1000; // Entre 1 y 5 seg.
            }
            return tiempo;
        }
    }

    public static class Empleado implements Runnable {
        int id;
        Confiteria confiteria;

        public Empleado(int id, Confiteria confiteria) {
            this.id = id;
            this.confiteria = confiteria;
        }

        @Override
        public void run() {
            try {
                confiteria.comportamientoEmpleado(id);
            } catch (Exception e) {
                System.out.println("Al empleado " + id + " no le gustó la comida y se fue.");
            }

        }
    }

    public static class Mozo implements Runnable {
        int id;
        int cantTurnos = 10;
        Confiteria confiteria;

        public Mozo(int id, Confiteria confiteria) {
            this.id = id;
            this.confiteria = confiteria;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < cantTurnos; i++) {
                    confiteria.comportamientoMozo(id);
                }
            } catch (Exception e) {
                System.out.println("El mozo" + id + " se cansó y se fue.");
            }

        }
    }

    public static class Cocinero implements Runnable {
        String nombre = "Ratatouille";
        int cantTurnos = 10;
        Confiteria confiteria;

        public Cocinero(Confiteria confiteria) {
            this.confiteria = confiteria;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < cantTurnos; i++) {
                    confiteria.comportamientoCocinero(nombre);
                }

            } catch (Exception e) {
                System.out.println("El cocinero " + nombre + " se cansó de cocinar y se fue.");
            }

        }
    }
}
