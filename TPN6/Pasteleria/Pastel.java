package TPN6.Pasteleria;

public class Pastel {
    private char tipo;
    private int peso;

    public Pastel(char tipo) {
        this.tipo = tipo;
        switch (tipo) {
            // Dependiendo el peso puede ser que el código se rompa o no 
            case 'A':
                this.peso = 1;
                break;
            case 'B':
                this.peso = 2;
                break;
            default:
                this.peso = 5;
                break;
        }
    }

    public char getTipo(){
        return this.tipo;
    }

    public int getPeso(){
        return this.peso;
    }
}
