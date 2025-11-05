package EjerciciosAdicionales.SalonDeActividades;

public class Persona implements Runnable {
    private int id;
    private Salon salon;

    public Persona(int id, Salon salon){
        this.id = id;
        this.salon = salon;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }
}
