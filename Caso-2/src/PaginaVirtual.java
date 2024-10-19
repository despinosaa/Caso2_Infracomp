public class PaginaVirtual {
    private final int numero;
    private final boolean esEscritura;

    public PaginaVirtual(int numero, boolean esEscritura) {
        this.numero = numero;
        this.esEscritura = esEscritura;
    }

    public int getNumero() {
        return numero;
    }
    
    public boolean esEscritura() {
        return esEscritura;
    }
}
