package TPN6.Pasteleria;

import java.util.LinkedList;

public class Caja {
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
        this.pesoActual += pastel.getPeso();
        pasteles.add(pastel);
    }
}
