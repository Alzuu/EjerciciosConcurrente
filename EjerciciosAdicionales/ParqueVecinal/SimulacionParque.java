package EjerciciosAdicionales.ParqueVecinal;

public class SimulacionParque {
    public static void main(String[] args) {
        Parque parque = new Parque(30,20);
        Thread[] personas = new Thread[100];

        for (int i = 0; i < personas.length; i++) {
            boolean esVecino = (i<20)? true: false;
            personas[i] = new Thread(new Persona(i, esVecino, parque));
            personas[i].start();
        }
        Thread escuela1 = new Thread(new Escuela(0, 15, 3, parque));
        Thread escuela2 = new Thread(new Escuela(1, 12, 2, parque));
        escuela1.start();
        escuela2.start();
    }
}
