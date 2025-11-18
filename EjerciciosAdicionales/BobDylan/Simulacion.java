package EjerciciosAdicionales.BobDylan;

public class Simulacion {
    public static void main(String[] args) {
        MesaDiscos mesa = new MesaDiscos();
        Disquera disquera = new Disquera();
        Thread bob = new Thread(new BobDylan(mesa));
        bob.start();
        Thread manager = new Thread(new Manager(disquera));
        manager.start();
        Thread[] productores = new Thread[6];

        for (int i = 0; i < productores.length; i++) {
            char tipo;
            switch (i % 4) {
                case 0:
                    tipo = 'R';
                    break;
                case 1:
                    tipo = 'F';
                    break;
                case 2:
                    tipo = 'C';
                    break;
                case 3:
                    tipo = 'B';
                    break;

                default:
                    tipo = 'B';
                    break;
            }
            productores[i] = new Thread(new Productor(i, tipo, mesa, disquera));
            productores[i].start();
        }
    }
}
