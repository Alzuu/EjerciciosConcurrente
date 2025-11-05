package EjerciciosAdicionales.Pizzeria;

import java.util.Random;

public class GeneradorPedidos implements Runnable {
    private Pizzeria pizzeria;

    public GeneradorPedidos(Pizzeria pizzeria) {
        this.pizzeria = pizzeria;
    }

    private char generarTipoPizza() {
        int random = new Random().nextInt(2);
        char tipo = (random == 0) ? 'N' : 'V'; // N para Napolitana, V para Vegana
        return tipo;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                char tipo = generarTipoPizza();
                System.out.println("NUEVO PEDIDO: " + tipo);
                pizzeria.generarPedido(tipo);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("GENERADOR PEDIDOS INTERRUMPIDO.");
        }

    }
}
