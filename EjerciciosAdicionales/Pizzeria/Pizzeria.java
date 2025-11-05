package EjerciciosAdicionales.Pizzeria;

import java.util.concurrent.Semaphore;

public class Pizzeria {
    private final int capMaxPedidos;

    private Semaphore semListaPedidos; // Mostrador.
    private Semaphore pedidoNapolitana = new Semaphore(0);
    private Semaphore pedidoVegana = new Semaphore(0);
    private Semaphore aRepartir = new Semaphore(0);

    public Pizzeria(int capMaxPed) {
        this.capMaxPedidos = capMaxPed;
        this.semListaPedidos = new Semaphore(capMaxPedidos);
    }

    public void generarPedido(char tipo) throws InterruptedException {
        // Espera hasta que pueda poner su pedido en "el mostrador".
        semListaPedidos.acquire();
        // Avisa al Cocinero correspondiente.
        if (tipo == 'N') {
            pedidoNapolitana.release();
        } else {
            pedidoVegana.release();
        }
    }

    public void cocinarNapolitana() throws InterruptedException {
        // Espera hasta tener un pedido de Napolitana.
        pedidoNapolitana.acquire();
        // Cuando toma el pedido "saca el pedido del mostrador".
        semListaPedidos.release();
        // Luego comienza a cocinar.
    }

    public void cocinarVegana() throws InterruptedException {
        // Espera hasta tener un pedido de las pizzas Veganas.
        pedidoVegana.acquire();
        // Comienza a cocinar.
    }

    public void avisarAlRepartidor() {
        aRepartir.release();
    }

    public void repartir() throws InterruptedException {
        aRepartir.acquire();
        // Cuando toma el pedido "saca el pedido del mostrador".
        semListaPedidos.release();
    }
}
