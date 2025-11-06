package TPN6.Pasteleria;

public class Pasteleria {
    public static void main(String[] args) {
        Mostrador mostrador = new Mostrador();
        Mesa mesa = new Mesa();
        Thread[] hornos = new Thread[3];
        hornos[0] = new Thread(new Horno(0, 'A', mostrador));
        hornos[1] = new Thread(new Horno(1, 'B', mostrador));
        hornos[2] = new Thread(new Horno(2, 'C', mostrador));
        hornos[0].start();
        hornos[1].start();
        hornos[2].start();

        Thread[] empaquetadores = new Thread[2];
        empaquetadores[0] = new Thread(new Empaquetador(0, mostrador, mesa));
        empaquetadores[1] = new Thread(new Empaquetador(1, mostrador, mesa));
        empaquetadores[0].start();
        empaquetadores[1].start();

        Thread brazoMec = new Thread(new BrazoMecanico(0, mesa));
        brazoMec.start();
    }
}
