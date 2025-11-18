package EjerciciosAdicionales.BobDylan;

public class Album {
    private int nombre;
    private final int duracionTotal;
    private int duracionActual;

    public Album(int nombre) {
        this.nombre = nombre;
        this.duracionActual = 0;
        this.duracionTotal = 10000;
    }

    public int getNombre() {
        return nombre;
    }

    public int getDuracionActual() {
        return duracionActual;
    }

    public int getDuracionTotal() {
        return duracionTotal;
    }

    public void agregarCancion(int duracionCancion) {
        duracionActual += duracionCancion;
    }
}
