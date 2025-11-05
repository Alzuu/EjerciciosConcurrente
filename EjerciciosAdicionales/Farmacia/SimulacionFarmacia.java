package EjerciciosAdicionales.Farmacia;

public class SimulacionFarmacia {
    public static void main(String[] args) {
        Farmacia farmacia = new Farmacia("FarmConc");
        Thread[] auxiliares = new Thread[5];
        Thread[] contables = new Thread[1];
        Thread encargado = new Thread(new Encargado(0, farmacia));
        encargado.start();
        for (int i = 0; i < auxiliares.length; i++) {
            auxiliares[i] = new Thread(new AuxiliarFarmacia(i, farmacia));
            auxiliares[i].start();
            if (i < contables.length) {
                contables[i] = new Thread(new AuxiliarContable(i, farmacia));
                contables[i].start();
            }

        }

    }
}
