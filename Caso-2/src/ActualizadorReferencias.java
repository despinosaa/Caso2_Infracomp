
import java.util.List;

public class ActualizadorReferencias extends Thread {
    private final Memoria memoria;
    private final Swap swap;
    private final List<Integer> referencias;
    private int hits = 0;
    private int fallas = 0;
    private boolean continuar = true;

    public ActualizadorReferencias(Memoria memoria, Swap swap, List<Integer> referencias) {
        this.memoria = memoria;
        this.swap = swap;
        this.referencias = referencias;
    }

    @Override
    public void run() {
        for (int referencia : referencias) {
            if (!continuar){
                break;
            }
            synchronized (memoria) {
                if (memoria.contienePagina(referencia)) {
                    hits++;
                    memoria.actualizarPagina(referencia);
                } else {
                    fallas++;
                    if (!swap.contienePagina(referencia)) {
                    swap.almacenarPagina(referencia);
                }
                    memoria.cargarPagina(referencia);
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                continuar = false;
                break;
            }   
        }
    }

    public void detener() {
        continuar = false;
    }

    public int getHits() {
        return hits;
    }

    public int getFallas() {
        return fallas;
    }
}
