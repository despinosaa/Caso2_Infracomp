import java.util.HashMap;
import java.util.Map;

public class TablaDePaginas {
    private final Map<Integer, PaginaReal> tabla = new HashMap<>();

    public void asignarPagina(PaginaVirtual paginaVirtual, PaginaReal paginaReal) {
        tabla.put(paginaVirtual.getNumero(), paginaReal);
    }

    public PaginaReal obtenerPaginaReal(int numeroPagina) {
        return tabla.get(numeroPagina);
    }

    public boolean contienePagina(int numeroPagina) {
        return tabla.containsKey(numeroPagina);
    }
}
