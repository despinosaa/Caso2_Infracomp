import java.util.List;

public class ActualizadorReferencias extends Thread {
    private final List<int[]> referencias;
    private final RAM ram;
    private final TablaDePaginas tablaDePaginas;

    public ActualizadorReferencias(List<int[]> referencias, RAM ram, TablaDePaginas tablaDePaginas) {
        this.referencias = referencias;
        this.ram = ram;
        this.tablaDePaginas = tablaDePaginas;
    }

    @Override
    public void run() {
        synchronized (ram) {
            for (int[] referencia : referencias) {
                int numeroPagina = referencia[0];
                boolean esEscritura = referencia[1] == 1;
                if (tablaDePaginas.contienePagina(numeroPagina)) {
                    PaginaReal pagina = tablaDePaginas.obtenerPaginaReal(numeroPagina);
                    ram.incrementarHits();
                    pagina.setBitReferencia(1);
                    if (esEscritura) {
                        pagina.setBitModificacion(1);
                    }
                } else {
                    PaginaVirtual nuevaPaginaV = new PaginaVirtual(numeroPagina, esEscritura);
                    PaginaReal nuevaPaginaR = new PaginaReal(nuevaPaginaV);
                    tablaDePaginas.asignarPagina(nuevaPaginaV, nuevaPaginaR);
                    ram.agregarPagina(nuevaPaginaR);
                    ram.incrementarFallas();
                }
                try {
                    Thread.sleep(1); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
