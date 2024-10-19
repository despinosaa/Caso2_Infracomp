import java.util.HashMap;
import java.util.Map;

public class TablaDePaginas {
    private final Map<Integer, PaginaReal> tabla = new HashMap<>();

    public void asignarRelacion(PaginaVirtual paginaV, PaginaReal paginaR) {
        tabla.put(paginaV.getNumero(), paginaR);
    }

    public PaginaReal obtenerPaginaReal(int numero) {
        return tabla.get(numero);
    }

    public boolean contienePaginaVirtual(int numero) {
        return tabla.containsKey(numero);
    }
}
