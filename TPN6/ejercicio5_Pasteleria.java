package TPN6;

import java.util.LinkedList;

public class ejercicio5_Pasteleria {
    public static void main(String[] args) {

    }

    public static class Pasteleria {
        private LinkedList<Pastel> mostrador;
        private LinkedList<Caja> listaCajas;
        private Caja cajaActual;
        private boolean retirarCaja;

        public Pasteleria() {
            this.mostrador = new LinkedList<>();
            this.listaCajas = new LinkedList<>();
            this.cajaActual = new Caja();
            this.retirarCaja = false;
        }

        public void producirPastelA() {
            Pastel pastelA = new Pastel('A');
            mostrador.add(pastelA);
            notifyAll();
        }

        public void producirPastelB() {
            Pastel pastelB = new Pastel('B');
            mostrador.add(pastelB);
            notifyAll();
        }

        public void producirPastelC() {
            Pastel pastelC = new Pastel('C');
            mostrador.add(pastelC);
            notifyAll();
        }

        public synchronized Pastel tomarPastel() throws InterruptedException {
            while (mostrador.isEmpty()) {
                wait();
            }
            Pastel tomado = mostrador.removeFirst();
            return tomado;
        }

        public synchronized void ponerPastel(Pastel pastel) throws InterruptedException {
            int pesoPastel = pastel.getPeso();
            int pesoMax = cajaActual.getPesoMax();
            int pesoActual = cajaActual.getPesoActual();
            while ((pesoPastel + pesoActual) > pesoMax || cajaActual == null) {
                // Si poner el pastel implica pasarme de peso espera.
                wait();
            }
            cajaActual.agregarPastel(pastel);
        }

        public synchronized void retirarCaja() throws InterruptedException {
            while (!retirarCaja) {
                wait();
            }
            listaCajas.add(cajaActual);
            cajaActual = null;
        }

        public synchronized void reponerCaja() throws InterruptedException {
            while (cajaActual!= null) {
                wait();
            }
            cajaActual = new Caja();
            notifyAll();
        }
    }

    public static class Pastel {
        private char tipo;
        private int peso;

        public Pastel(char tipo) {
            this.tipo = tipo;
            switch (tipo) {
                case 'A':
                    this.peso = 2;
                    break;
                case 'B':
                    this.peso = 5;
                    break;
                case 'C':
                    this.peso = 10;
                    break;
                default:
                    break;
            }
        }

        public char getTipo() {
            return this.tipo;
        }

        public int getPeso() {
            return this.peso;
        }

    }

    public static class Caja {
        private final int pesoMax;
        private int pesoActual;
        private LinkedList<Pastel> pasteles;

        public Caja() {
            this.pesoMax = 20;
            this.pesoActual = 0;
            this.pasteles = new LinkedList<>();
        }

        public int getPesoMax() {
            return this.pesoMax;
        }

        public int getPesoActual() {
            return this.pesoActual;
        }

        public void agregarPastel(Pastel pastel) {
            pasteles.add(pastel);
        }
    }

    public static class Horno implements Runnable {
        private char tipo;
        private Pasteleria pasteleria;

        public Horno(char tipo, Pasteleria pasteleria) {
            this.tipo = tipo;
            this.pasteleria = pasteleria;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    switch (tipo) {
                        case 'A':
                            pasteleria.producirPastelA();
                            Thread.sleep(2000);
                            break;
                        case 'B':
                            pasteleria.producirPastelB();
                            Thread.sleep(3500);
                            break;
                        case 'C':
                            pasteleria.producirPastelC();
                            Thread.sleep(5000);
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Horno " + tipo + ": interrumpido.");
            }

        }
    }

    public static class Empaquetador implements Runnable {
        int id;
        Pasteleria pasteleria;

        public Empaquetador(int id, Pasteleria pasteleria) {
            this.id = id;
            this.pasteleria = pasteleria;
        }

        @Override
        public void run() {
            try {
                Pastel pastel = pasteleria.tomarPastel();
                pasteleria.ponerPastel(pastel);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                System.out.println("Empaquetador " + id + ": interrumpido.");
            }
        }
    }

    public static class BrazoMecanico implements Runnable {

    }

}
