package EjerciciosAdicionales.Pizzeria;

public class SimulacionPizzeria {
    public static void main(String[] args) {
        int aux = 0;
        int turnosDescanso = 10;
        Pizzeria pizzeria = new Pizzeria(10);
        Thread[] empleados = new Thread[5];
        Thread generadorPedidos = new Thread(new GeneradorPedidos(pizzeria));
        generadorPedidos.start();
        for (int i = 0; i < empleados.length; i++) {
            if(i%2==0) aux++;
            if(i %2 == 0){
                // Cocineros
                char tipo = (aux %2 == 0)? 'N' : 'V';
                empleados[i] = new Thread(new Cocinero(i, tipo, pizzeria));
            } else {
                // Repartidores.
                empleados[i] = new Thread(new Repartidor(i, turnosDescanso, pizzeria));
            }
            empleados[i].start();
        }
    }
}
