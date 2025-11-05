package EjerciciosAdicionales.Farmacia;

public class SimulacionFarmacia {
    public static void main(String[] args) {
        Farmacia farmacia = new Farmacia("FarmConc");
        Thread[] auxiliares = new Thread[3];
        Thread[] contables = new Thread[3];
        Thread encargado = new Thread(new Encargado(0, farmacia));

        for (int i = 0; i < contables.length; i++) {
            auxiliares[i] = new Thread(new AuxiliarFarmacia(i, farmacia));
            contables[i] = new Thread(new AuxiliarContable(i, farmacia));

            auxiliares[i].start();
            contables[i].start();
        }
        encargado.start();
    }
}
