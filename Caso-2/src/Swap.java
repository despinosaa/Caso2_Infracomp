import java.util.ArrayList;
import java.util.List;

public class Swap {
    private List<Integer> paginas;

    public Swap() {
        this.paginas = new ArrayList<>();
    }

    public synchronized void almacenarPagina(int pagina) {
        if (!paginas.contains(pagina)) {
            paginas.add(pagina);
        }
    }

    public synchronized boolean contienePagina(int pagina) {
        return paginas.contains(pagina);
    }
}
